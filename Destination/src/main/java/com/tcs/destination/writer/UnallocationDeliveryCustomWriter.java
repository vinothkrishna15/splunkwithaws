package com.tcs.destination.writer;

import static com.tcs.destination.utils.Constants.FILE_DIR_SEPERATOR;
import static com.tcs.destination.utils.Constants.FILE_PATH;
import static com.tcs.destination.utils.Constants.REQUEST;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

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

import com.google.common.collect.Lists;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.DeliveryCentreUtilizationT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.data.repository.DeliveryCentreUtilizationRepository;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.helper.UnallocationUploadHelper;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.FileManager;

public class UnallocationDeliveryCustomWriter implements ItemWriter<String[]>,
		StepExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(UnallocationDeliveryCustomWriter.class);

	private DataProcessingRequestRepository dataProcessingRequestRepository;

	private UnallocationUploadHelper helper;

	private DataProcessingRequestT request;

	private List<UploadServiceErrorDetailsDTO> errorList = null;

	private UploadErrorReport uploadErrorReport;

	private DeliveryCentreUtilizationRepository deliveryCentreUtilizationRepository;

	public DataProcessingRequestRepository getDataProcessingRequestRepository() {
		return dataProcessingRequestRepository;
	}

	public void setDataProcessingRequestRepository(
			DataProcessingRequestRepository dataProcessingRequestRepository) {
		this.dataProcessingRequestRepository = dataProcessingRequestRepository;
	}

	public DataProcessingRequestT getRequest() {
		return request;
	}

	public void setRequest(DataProcessingRequestT request) {
		this.request = request;
	}

	public List<UploadServiceErrorDetailsDTO> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<UploadServiceErrorDetailsDTO> errorList) {
		this.errorList = errorList;
	}

	public UploadErrorReport getUploadErrorReport() {
		return uploadErrorReport;
	}

	public void setUploadErrorReport(UploadErrorReport uploadErrorReport) {
		this.uploadErrorReport = uploadErrorReport;
	}

	/**
	 * @return the helper
	 */
	public UnallocationUploadHelper getHelper() {
		return helper;
	}

	/**
	 * @param helper
	 *            the helper to set
	 */
	public void setHelper(UnallocationUploadHelper helper) {
		this.helper = helper;
	}

	/**
	 * @return the deliveryCentreUtilizationRepository
	 */
	public DeliveryCentreUtilizationRepository getDeliveryCentreUtilizationRepository() {
		return deliveryCentreUtilizationRepository;
	}

	/**
	 * @param deliveryCentreUtilizationRepository
	 *            the deliveryCentreUtilizationRepository to set
	 */
	public void setDeliveryCentreUtilizationRepository(
			DeliveryCentreUtilizationRepository deliveryCentreUtilizationRepository) {
		this.deliveryCentreUtilizationRepository = deliveryCentreUtilizationRepository;
	}

	/**
	 * Before staring with write process the status of the execution will be
	 * updated.
	 */

	@Override
	public void beforeStep(StepExecution stepExecution) {
		try {
			DataProcessingRequestT request = (DataProcessingRequestT) stepExecution
					.getJobExecution().getExecutionContext().get(REQUEST);
			request.setStatus(RequestStatus.INPROGRESS.getStatus());
			dataProcessingRequestRepository.save(request);

		} catch (Exception e) {
			logger.error("Error in before step process: {}", e);
		}

	}

	/**
	 * After step will be called to close and terminate the opened file used for
	 * uploading also to update the status of the job execution.
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		try {
			ExecutionContext jobContext = stepExecution.getJobExecution()
					.getExecutionContext();
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext
					.get(REQUEST);
			if (request != null && errorList != null) {
				Workbook workbook = uploadErrorReport
						.writeErrorToWorkbook(errorList);
				String errorPath = request.getFilePath() + "ERROR"
						+ FILE_DIR_SEPERATOR;
				String errorFileName = "unallocationUpload_error.xlsx";
				File file = FileManager.createFile(errorPath, errorFileName);
				FileOutputStream outputStream = new FileOutputStream(file);
				workbook.write(outputStream);
				outputStream.flush();
				outputStream.close();
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
		return ExitStatus.COMPLETED;
	}

	/**
	 * Write method called to upload the values into Database
	 */
	@Override
	public void write(List<? extends String[]> items) throws Exception {
		List<DeliveryCentreUtilizationT> deliveryCentreUtilization = Lists
				.newArrayList();

		for (String[] data : items) {
			DeliveryCentreUtilizationT deliveryCentreUtilizationT = new DeliveryCentreUtilizationT();
			UploadServiceErrorDetailsDTO errorDTO = helper
					.validateUnallocationDeliveryCentreData(data, request
							.getUserT().getUserId(), deliveryCentreUtilizationT);
			if (StringUtils.isNotEmpty(errorDTO.getMessage())) {
				errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
						: errorList;
				errorList.add(errorDTO);
			} else if (errorDTO.getMessage() == null) {
				deliveryCentreUtilization.add(deliveryCentreUtilizationT);
			}
		}
		if (CollectionUtils.isNotEmpty(deliveryCentreUtilization)) {
			deliveryCentreUtilizationRepository.save(deliveryCentreUtilization);
		}

	}

}
