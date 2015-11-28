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
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.helper.OpportunityUploadHelper;
import com.tcs.destination.service.OpportunityService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.FileManager;

/**
 * This OpportunityCustomWriter class writes the opportunity data to database
 * @author bnpp
 *
 */
public class OpportunityCustomWriter implements ItemWriter<String[]>,
		StepExecutionListener, WriteListener {

	private static final Logger logger = LoggerFactory
			.getLogger(OpportunityCustomWriter.class);

	private DataProcessingRequestRepository dataProcessingRequestRepository;

	private OpportunityUploadHelper helper;

	private DataProcessingRequestT request;

	private List<UploadServiceErrorDetailsDTO> errorList = null;

	private UploadErrorReport uploadErrorReport;

	private OpportunityService opportunityService;

	@Override
	public void write(List<? extends String[]> items) throws Exception {
		logger.debug("Inside write:");

		List<OpportunityT> opportunityList = new ArrayList<OpportunityT>();
		for (String[] data : items) {
            OpportunityT opportunity = new OpportunityT();
			UploadServiceErrorDetailsDTO errorDTO = helper
					.validateOpportunityData(data, request.getUserT()
							.getUserId(), opportunity);
			if (errorDTO.getMessage() != null) {
				errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
						: errorList;
				errorList.add(errorDTO);
			} else if (errorDTO.getMessage() == null) {
				opportunityList.add(opportunity);
			}
			
		}

		if (CollectionUtils.isNotEmpty(opportunityList)) {
			opportunityService.save(opportunityList);
		}
	}

	public OpportunityUploadHelper getHelper() {
		return helper;
	}

	public void setHelper(OpportunityUploadHelper helper) {
		this.helper = helper;
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

	public OpportunityService getOpportunityService() {
		return opportunityService;
	}

	public void setOpportunityService(OpportunityService opportunityService) {
		this.opportunityService = opportunityService;
	}

	public void setRequest(DataProcessingRequestT request) {
		this.request = request;
	}

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
				String errorFileName = "opportunityUpload_error.xlsx";

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

	@Override
	public void onWritePossible() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(Throwable throwable) {
		logger.error("Error while writing the error report: {}", throwable);

	}

}
