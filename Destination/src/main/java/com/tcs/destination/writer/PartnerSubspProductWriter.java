package com.tcs.destination.writer;

import static com.tcs.destination.utils.Constants.FILE_DIR_SEPERATOR;
import static com.tcs.destination.utils.Constants.FILE_PATH;
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
import com.tcs.destination.bean.PartnerSubspProductMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.data.repository.PartnerSubSpProductMappingTRepository;
import com.tcs.destination.enums.Operation;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.helper.PartnerSubSpProductUploadHelper;
import com.tcs.destination.service.PartnerService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.FileManager;

public class PartnerSubspProductWriter implements ItemWriter<String[]>,
		StepExecutionListener, WriteListener {

	private static final Logger logger = LoggerFactory
			.getLogger(PartnerSubspProductWriter.class);

	private DataProcessingRequestRepository dataProcessingRequestRepository;

	private PartnerSubSpProductUploadHelper helper;

	private DataProcessingRequestT request;

	private StepExecution stepExecution;

	private List<UploadServiceErrorDetailsDTO> errorList = null;

	private UploadErrorReport uploadErrorReport;

	private PartnerService partnerService;

	private PartnerSubSpProductMappingTRepository partnerSubSpProductMappingTRepository;
	
	boolean deleteFlag;

	@Override
	public void write(List<? extends String[]> items) throws Exception {

		logger.debug("Inside write:");
		List<PartnerSubspProductMappingT> insertList = new ArrayList<PartnerSubspProductMappingT>();
		List<PartnerSubspProductMappingT> updateList = new ArrayList<PartnerSubspProductMappingT>();
		List<PartnerSubspProductMappingT> deleteList = new ArrayList<PartnerSubspProductMappingT>();

		String operation = null;

		for (String[] data : items) {
			operation = (String) data[1];
			if (operation != null) {
				if (operation.equalsIgnoreCase(Operation.ADD.name())) {

					logger.debug("***PARTNER SUBSP PRODUCT ADD***");
					PartnerSubspProductMappingT partnerSubspProductMappingT = new PartnerSubspProductMappingT();
					UploadServiceErrorDetailsDTO errorDTO = helper
							.validatePartnerSubspProductData(data, request
									.getUserT().getUserId(),
									partnerSubspProductMappingT);
					if (StringUtils.isNotEmpty(errorDTO.getMessage())) {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
								: errorList;
						errorList.add(errorDTO);
					} else {
						insertList.add(partnerSubspProductMappingT);
					}

				}
				else if (operation.equalsIgnoreCase(Operation.DELETE.name())) {
					logger.debug("***PARTNER SUBSP PRODUCT DELETE***");
					PartnerSubspProductMappingT partner = new PartnerSubspProductMappingT();
					String partnerSubspProductMappingId = data[2];
					partner = partnerSubSpProductMappingTRepository
							.findByPartnerSubspProductMappingId(partnerSubspProductMappingId);
					if((partner != null) &&(deleteFlag==false)) 
					{
						UploadServiceErrorDetailsDTO errorDTO = helper
								.validatePartnerSubSpProductId(data, partner);

						if (StringUtils.isNotEmpty(errorDTO.getMessage())) {
							errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
									: errorList;
							errorList.add(errorDTO);
						} else {
							deleteList.add(partner);
						}
					}
				}
				
                   // to save partner subsp product details to db
					if (CollectionUtils.isNotEmpty(insertList)) {
						partnerService.savePartnerSubSpProduct(insertList);
					} // to delete partner subsp product details from db 
					else if (CollectionUtils.isNotEmpty(deleteList)){
						partnerService.deletePartnerSubSpProduct(deleteList);
					}
				
			}
		}
	}

	public PartnerSubSpProductUploadHelper getHelper() {
		return helper;
	}

	public void setHelper(PartnerSubSpProductUploadHelper helper) {
		this.helper = helper;
	}

	public PartnerSubSpProductMappingTRepository getPartnerRepository() {
		return partnerSubSpProductMappingTRepository;
	}

	public void setPartnerSubSpProductMappingTRepository(
			PartnerSubSpProductMappingTRepository partnerSubSpProductMappingTRepository) {
		this.partnerSubSpProductMappingTRepository = partnerSubSpProductMappingTRepository;
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

    	try{
    		this.stepExecution = stepExecution;
    		ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
    		deleteFlag=(boolean)jobContext.get("deleteFlag");
    	}
    	catch(Exception e)
    	{
    		System.out.println("Exception"+e);
    	}

    }

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		try {
			ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext.get(REQUEST);
			if ( errorList != null) {
				String errorPath = request.getFilePath() + "ERROR" +FILE_DIR_SEPERATOR;
				String errorFileName ="partnerUpload_error.xlsx";
				File file = new File(errorPath+errorFileName);
				if(!file.exists()){
					Workbook workbook = uploadErrorReport.writeErrorToWorkbook(errorList, Constants.PARTNER_SUBSP_PRODUCT_TEMPLATE_SHEET_NAME);
					File file1 = FileManager.createFile(errorPath, errorFileName);
					logger.info("created file : " + file1.getAbsolutePath());
					FileOutputStream outputStream = new FileOutputStream(file1);
					workbook.write(outputStream);
					outputStream.flush();
					outputStream.close();
				} else {
					Workbook workbook = ExcelUtils.getWorkBook(file);
					uploadErrorReport.writeErrorToWorkbook(errorList,workbook,Constants.PARTNER_SUBSP_PRODUCT_TEMPLATE_SHEET_NAME);
					FileOutputStream outputStream = new FileOutputStream(file);
					workbook.write(outputStream); //write changes
					outputStream.flush();
					outputStream.close();
				}
				request.setErrorFileName(errorFileName);	
				request.setErrorFilePath(errorPath);
			}
			request.setStatus(RequestStatus.PROCESSED.getStatus());
			dataProcessingRequestRepository.save(request);
			jobContext.remove(REQUEST);
			jobContext.remove(FILE_PATH);
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
