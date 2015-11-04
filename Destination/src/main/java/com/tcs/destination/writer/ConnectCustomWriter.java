/**
 * 
 * ConnectCustomWriter.java 
 *
 * @author TCS
 * @Version 1.0 - 2015
 * 
 * @Copyright 2015 Tata Consultancy 
 */
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

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.enums.Operation;
import com.tcs.destination.helper.ConnectUploadHelper;
import com.tcs.destination.service.ConnectService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.FileManager;
import com.tcs.destination.utils.StringUtils;
/**
 * This ConnectCustomWriter class provide the functionality for writing connect details to db, and having listener functionality for steps
 * 
 */
public class ConnectCustomWriter implements ItemWriter<String[]>, StepExecutionListener, WriteListener {
	
	private static final Logger logger = LoggerFactory
			.getLogger(ConnectCustomWriter.class);
	
	private DataProcessingRequestRepository dataProcessingRequestRepository;
	
	private ConnectUploadHelper helper;
	
	private DataProcessingRequestT request; 
	
	private List<UploadServiceErrorDetailsDTO> errorList = null;
	
	private UploadErrorReport uploadErrorReport;
	
	private ConnectService connectService;
	
	private ConnectRepository connectRepository;
	
	@Override
	public void write(List<? extends String[]> items) throws Exception {
		logger.debug("Inside write:");
		
		List<ConnectT> insertList = new ArrayList<ConnectT>();
		List<ConnectT> updateList = new ArrayList<ConnectT>();
		List<ConnectT> deleteList = new ArrayList<ConnectT>();
		String operation = null; 
		for (String[] data: items) {

			operation = (String) data[1];
			if (operation.equalsIgnoreCase(Operation.ADD.name())) {
				
				ConnectT connect =  new ConnectT();
				UploadServiceErrorDetailsDTO errorDTO = helper.validateConnectData(data, request.getUserT().getUserId() ,connect);
				if (errorDTO.getMessage() != null) {
					errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
					errorList.add(errorDTO);
				} else if (errorDTO.getMessage() == null) {
					insertList.add(connect);
				}
				
			} else if (operation.equalsIgnoreCase(Operation.UPDATE.name())){
				
				String connectId = data[2];
				UploadServiceErrorDetailsDTO errorDTO = new UploadServiceErrorDetailsDTO();
				
				if (!StringUtils.isEmpty(connectId)) {
					ConnectT connect = connectRepository.findByConnectId(connectId);
					if (connect != null) {
						errorDTO = helper.validateConnectDataUpdate(data, request.getUserT().getUserId() ,connect);
						if (errorDTO.getMessage() != null) {
							errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
							errorList.add(errorDTO);
						} else if (errorDTO.getMessage() == null) {
							updateList.add(connect);
						}
					} else {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
						errorDTO.setRowNumber(Integer.parseInt(data[0]) + 1);
						errorDTO.setMessage("Connect Id is invalid; ");
						errorList.add(errorDTO);
					}
				} else {
					errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
					errorDTO.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorDTO.setMessage("Connect Id is mandatory; ");
					errorList.add(errorDTO);
				}
				
			} else if (operation.equalsIgnoreCase(Operation.DELETE.name())){
				 ConnectT connect =  new ConnectT();
				 connect = connectRepository.findByConnectId(data[2]);
				 UploadServiceErrorDetailsDTO errorDTO = helper.validateConnectId(data, connect);
				 
				 if (errorDTO.getMessage() != null) {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
						errorList.add(errorDTO);
					} else if (errorDTO.getMessage() == null) {
						deleteList.add(connect);
				}
			
			}
		}
		
		if (CollectionUtils.isNotEmpty(insertList)) {
			connectService.save(insertList);
		} 
		if (CollectionUtils.isNotEmpty(updateList)){ 
			connectService.updateConnect(updateList);
		} 
		if (CollectionUtils.isNotEmpty(deleteList)){ 
			connectService.deleteConnect(deleteList);
		}
		
	}


	public ConnectUploadHelper getHelper() {
		return helper;
	}


	public void setHelper(ConnectUploadHelper helper) {
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

	
	public ConnectService getConnectService() {
		return connectService;
	}


	public void setConnectService(ConnectService connectService) {
		this.connectService = connectService;
	}


	public void setRequest(DataProcessingRequestT request) {
		this.request = request;
	}

	public ConnectRepository getConnectRepository() {
		return connectRepository;
	}


	public void setConnectRepository(ConnectRepository connectRepository) {
		this.connectRepository = connectRepository;
	}


	@Override
	public void beforeStep(StepExecution stepExecution) {
		
		try {
			
			DataProcessingRequestT request = (DataProcessingRequestT) stepExecution.getJobExecution().getExecutionContext().get(REQUEST);
			request.setStatus(RequestStatus.INPROGRESS.getStatus());
			dataProcessingRequestRepository.save(request);
			
		} catch (Exception e) {
			logger.error("Error in before step process: {}", e);
		}
			
			
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		
		try {
			
			ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
			
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext.get(REQUEST);
			
			if (request != null && errorList != null) {
				Workbook workbook = uploadErrorReport.writeErrorToWorkbook(errorList);
				
				String errorPath = request.getFilePath() + "ERROR" +FILE_DIR_SEPERATOR;
				String errorFileName = "connectUpload_error.xlsx";
				
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
