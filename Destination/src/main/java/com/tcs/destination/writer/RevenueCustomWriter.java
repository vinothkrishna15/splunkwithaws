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

import com.tcs.destination.bean.ActualRevenuesDataT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.enums.Operation;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.helper.RevenueUploadHelper;
import com.tcs.destination.service.RevenueService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.FileManager;
import com.tcs.destination.utils.StringUtils;

public class RevenueCustomWriter implements ItemWriter<String[]>, StepExecutionListener, WriteListener {
	
	public RevenueUploadHelper getHelper() {
		return helper;
	}




	public void setHelper(RevenueUploadHelper helper) {
		this.helper = helper;
	}




	public StepExecution getStepExecution() {
		return stepExecution;
	}




	public void setStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}




	public RevenueService getRevenueService() {
		return revenueService;
	}




	public List<UploadServiceErrorDetailsDTO> getErrorList() {
		return errorList;
	}




	public void setErrorList(List<UploadServiceErrorDetailsDTO> errorList) {
		this.errorList = errorList;
	}




	public void setRevenueService(RevenueService revenueService) {
		this.revenueService = revenueService;
	}


	private static final Logger logger = LoggerFactory
			.getLogger(RevenueCustomWriter.class);
	
	private DataProcessingRequestRepository dataProcessingRequestRepository;
	
	private RevenueUploadHelper helper;
	
	private DataProcessingRequestT request; 
	
	private StepExecution stepExecution;
	
	
	private RevenueService revenueService;
	
	private List<UploadServiceErrorDetailsDTO> errorList = null;
	
	private UploadErrorReport uploadErrorReport;
	
	
	
	
	@Override
	public void write(List<? extends String[]> items) throws Exception {
		logger.debug("Inside write:");
		
		List<ActualRevenuesDataT> addList = new ArrayList<ActualRevenuesDataT>();
		List<ActualRevenuesDataT> updateList = new ArrayList<ActualRevenuesDataT>();
		List<ActualRevenuesDataT> deleteList = new ArrayList<ActualRevenuesDataT>();
		String operation = null; 
		for (String[] data: items) {

			operation = (String) data[1];
			
			if ((!StringUtils.isEmpty(operation))) {
			if (operation.equalsIgnoreCase(Operation.ADD.name())) {
				logger.info("***ADDING****");
				ActualRevenuesDataT revenueT =  new ActualRevenuesDataT();;
				UploadServiceErrorDetailsDTO errorDTO = helper.validateRevenueAdd(data, request.getUserT().getUserId() ,revenueT);
				if (errorDTO.getMessage() != null) {
					logger.info("validation error in operation "+operation+"with error " +errorDTO.getMessage());
					errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
					errorList.add(errorDTO);
				} else if (errorDTO.getMessage() == null) {
					addList.add(revenueT);
				}
				
			} else if (operation.equalsIgnoreCase(Operation.UPDATE.name())){
				logger.info("****UPDATE****");
				ActualRevenuesDataT revenueT =  new ActualRevenuesDataT();;
				UploadServiceErrorDetailsDTO errorDTO = helper.validateRevenueUpdate(data, request.getUserT().getUserId() ,revenueT);
				if (errorDTO.getMessage() != null) {
					logger.info("validation error in operation "+operation+"with error " +errorDTO.getMessage());
					errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
					errorList.add(errorDTO);
				} else if (errorDTO.getMessage() == null) {
					updateList.add(revenueT);
				}
				
			} else if (operation.equalsIgnoreCase(Operation.DELETE.name())){
				logger.info("****DELETE****");
				ActualRevenuesDataT revenueT =  new ActualRevenuesDataT();;
				UploadServiceErrorDetailsDTO errorDTO = helper.validateRevenueDelete(data, request.getUserT().getUserId() ,revenueT);
				if (errorDTO.getMessage() != null) {
					logger.info("validation error in operation "+operation+"with error " +errorDTO.getMessage());
					errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
					errorList.add(errorDTO);
				} else if (errorDTO.getMessage() == null) {
					deleteList.add(revenueT);
				}
				
			}
			
			
		}}
		
		// for saving the rows which are valid
				if (CollectionUtils.isNotEmpty(addList)) {
					logger.info("list size " +updateList.size());
					revenueService.save(addList);
				}
				// for deleting the rows which are valid
				if (CollectionUtils.isNotEmpty(deleteList)) {
					logger.info("list size " +updateList.size());
					revenueService.delete(deleteList);
				}
				// for updating the rows which are valid
						if (CollectionUtils.isNotEmpty(updateList)) {
							logger.info("list size " +updateList.size());
							revenueService.save(updateList);
						}
		
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
			
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext.get(REQUEST);
			
			if (!CollectionUtils.isEmpty(errorList)) {
				Workbook workbook = uploadErrorReport.writeErrorToWorkbook(errorList);
				logger.info("request file path "+request.getFilePath());
				String errorPath = request.getFilePath() + "ERROR" +FILE_DIR_SEPERATOR;
				String errorFileName = "revenueUpload_error.xlsx";
				
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
