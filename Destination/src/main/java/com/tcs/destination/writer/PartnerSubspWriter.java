package com.tcs.destination.writer;

import static com.tcs.destination.utils.Constants.FILE_DIR_SEPERATOR;
import static com.tcs.destination.utils.Constants.REQUEST;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.WriteListener;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.PartnerSubSpMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.data.repository.PartnerSubSpMappingTRepository;
import com.tcs.destination.enums.Operation;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.helper.PartnerSubSpUploadHelper;
import com.tcs.destination.service.PartnerService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.FileManager;

public class PartnerSubspWriter implements ItemWriter<String[]>,
		StepExecutionListener, WriteListener {

	private static final Logger logger = LoggerFactory
			.getLogger(PartnerSubspWriter.class);

	private DataProcessingRequestRepository dataProcessingRequestRepository;

	private PartnerSubSpUploadHelper helper;

	private DataProcessingRequestT request;

	private StepExecution stepExecution;

	private List<UploadServiceErrorDetailsDTO> errorList = null;

	private UploadErrorReport uploadErrorReport;

	private PartnerService partnerService;

	private PartnerSubSpMappingTRepository partnerSubSpMappingTRepository;

	boolean deleteFlag = false;

	@Override
	public void write(List<? extends String[]> items) throws Exception {

		logger.debug("Inside write of partner subsp mapping:");
		List<PartnerSubSpMappingT> insertList = new ArrayList<PartnerSubSpMappingT>();
		List<PartnerSubSpMappingT> updateList = new ArrayList<PartnerSubSpMappingT>();
		List<PartnerSubSpMappingT> deleteList = new ArrayList<PartnerSubSpMappingT>();
		deleteFlag = false;
		String operation = null;

		for (String[] data : items) {
			operation = (String) data[1];

			if (operation != null) {
				if (operation.equalsIgnoreCase(Operation.ADD.name())) {

					logger.debug("***PARTNER SUBSP ADD***");
					PartnerSubSpMappingT partnerSubSpMappingT = new PartnerSubSpMappingT();
					UploadServiceErrorDetailsDTO errorDTO = helper
							.validatePartnerSubSpData(data, request.getUserT()
									.getUserId(), partnerSubSpMappingT);
					if (StringUtils.isNotEmpty(errorDTO.getMessage())) {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
								: errorList;
						errorList.add(errorDTO);
					} else {
						insertList.add(partnerSubSpMappingT);
					}

				} 
				else if (operation.equalsIgnoreCase(Operation.DELETE.name())) {
					logger.debug("***PARTNER SUBSP DELETE***");
					String partnerSubspMappingId = data[2];
					UploadServiceErrorDetailsDTO errorDTO = new UploadServiceErrorDetailsDTO();
					PartnerSubSpMappingT partnerSubspT = new PartnerSubSpMappingT();
					partnerSubspT = partnerSubSpMappingTRepository
							.findByPartnerSubspMappingId(partnerSubspMappingId);
					if (partnerSubspT != null) {
						errorDTO = helper.validatePartnerSubspId(data,
								partnerSubspT, deleteFlag);

						if (StringUtils.isNotEmpty(errorDTO.getMessage() )) {
							errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
									: errorList;
							errorList.add(errorDTO);
						} else {
							deleteList.add(partnerSubspT);
						}
					} else {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
								: errorList;
						errorDTO.setRowNumber(Integer.parseInt(data[0]) + 1);
						errorDTO.setMessage("Partner Subsp Id is invalid");
						errorList.add(errorDTO);

					}

				}
			}
		}
		// to save partner subsp details to db
		if (CollectionUtils.isNotEmpty(insertList)) {
			partnerService.savePartnerSubsp(insertList);
		}
		// to delete partner subsp details from db
		else if (CollectionUtils.isNotEmpty(deleteList)) {
			partnerService.deletePartnerSubSp(deleteList);
		}
	}

	public PartnerSubSpUploadHelper getHelper() {
		return helper;
	}

	public void setHelper(PartnerSubSpUploadHelper helper) {
		this.helper = helper;
	}

	public PartnerSubSpMappingTRepository getPartnerSubSpMappingTRepository() {
		return partnerSubSpMappingTRepository;
	}

	public void setPartnerSubSpMappingTRepository(
			PartnerSubSpMappingTRepository partnerSubSpMappingTRepository) {
		this.partnerSubSpMappingTRepository = partnerSubSpMappingTRepository;
	}

	public DataProcessingRequestT getRequest() {
		return request;
	}

	public DataProcessingRequestRepository getDataProcessingRequestRepository() {
		return dataProcessingRequestRepository;
	}

	public void setDataProcessingRequestRepository(
			DataProcessingRequestRepository dataProcessingRequestRepository) {
		this.dataProcessingRequestRepository = dataProcessingRequestRepository;
	}

	public UploadErrorReport getUploadErrorReport() {
		return uploadErrorReport;
	}

	public void setUploadErrorReport(UploadErrorReport uploadErrorReport) {
		this.uploadErrorReport = uploadErrorReport;
	}

	public PartnerService getPartnerService() {
		return partnerService;
	}

	public void setPartnerService(PartnerService partnerService) {
		this.partnerService = partnerService;
	}

	public void setRequest(DataProcessingRequestT request) {
		this.request = request;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		try {
			ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
			jobContext.put("deleteFlag", deleteFlag);
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext
					.get(REQUEST);
			if (errorList != null) {
				String errorPath = request.getFilePath() + "ERROR"
						+ FILE_DIR_SEPERATOR;
				String errorFileName = "partnerUpload_error.xlsx";
				File file = new File(errorPath + errorFileName);
				if (!file.exists()) {
					Workbook workbook = uploadErrorReport.writeErrorToWorkbook(
							errorList,
							Constants.PARTNER_SUBSP_TEMPLATE_SHEET_NAME);
					File file1 = FileManager.createFile(errorPath,
							errorFileName);
					logger.info("created file : " + file1.getAbsolutePath());
					FileOutputStream outputStream = new FileOutputStream(file1);
					workbook.write(outputStream);
					outputStream.flush();
					outputStream.close();
				} else {
					Workbook workbook = ExcelUtils.getWorkBook(file);
					uploadErrorReport.writeErrorToWorkbook(errorList, workbook,
							Constants.PARTNER_SUBSP_TEMPLATE_SHEET_NAME);
					FileOutputStream outputStream = new FileOutputStream(file);
					workbook.write(outputStream); // write changes
					outputStream.flush();
					outputStream.close();
				}
				request.setErrorFileName(errorFileName);
				request.setErrorFilePath(errorPath);
			}
			request.setStatus(RequestStatus.INPROGRESS.getStatus());
			dataProcessingRequestRepository.save(request);
		} catch (Exception e) {
			logger.error("Error while writing the error report: {}", e);
		}
		return stepExecution.getExitStatus();
	}

	@Override
	public void onWritePossible() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(Throwable throwable) {
		logger.error("Error while writing the error report: {}", throwable);

	}

}
