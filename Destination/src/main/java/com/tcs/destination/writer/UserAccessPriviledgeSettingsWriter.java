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
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UserAccessPrivilegeDTO;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.Operation;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.helper.UserUploadHelper;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.service.UserService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.FileManager;

public class UserAccessPriviledgeSettingsWriter implements ItemWriter<String[]>, StepExecutionListener, WriteListener {
	
	private static final Logger logger = LoggerFactory
			.getLogger(UserAccessPriviledgeSettingsWriter.class);
	
	private DataProcessingRequestRepository dataProcessingRequestRepository;
	
	private UserUploadHelper helper;
	
	private DataProcessingRequestT request; 
	
	private StepExecution stepExecution;
	
	private List<UploadServiceErrorDetailsDTO> errorList = null;
	
	private UploadErrorReport uploadErrorReport;
	
	private UserService userService;
	
    private UserRepository userRepository;
    
    List<UserT> usersList= new ArrayList<UserT>();
	
    @Override
    public void write(List<? extends String[]> items) throws Exception {
    	logger.debug("Inside write:");

        String operation = null; 

        List<UserAccessPrivilegeDTO> insertList = new ArrayList<UserAccessPrivilegeDTO>();
    	for (String[] data: items) {
    		
    		operation = (String) data[1];
    		if(operation!=null)
    		{
    			if (operation.equalsIgnoreCase(Operation.ADD.name())) {

    				logger.debug("***USER_ACCESS_PRIVILEDGE ADD***");
    				UserAccessPrivilegeDTO userAccessPrivilegesDTO =  new UserAccessPrivilegeDTO();
    				UploadServiceErrorDetailsDTO errorDTO = helper.validateUserAccessPrivilegesData(data, request.getUserT().getUserId() ,userAccessPrivilegesDTO);
    				errorDTO.setSheetName(Constants.USER_TEMPLATE_PRIVILEGE);
    				if (errorDTO.getMessage() != null) {
    					errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
    					errorList.add(errorDTO);
    				} else if (errorDTO.getMessage() == null) {
    					insertList.add(userAccessPrivilegesDTO);
    				}

    			}
    			
    		}
    	}
    	
    	

			if (CollectionUtils.isNotEmpty(insertList)) {
				userService.saveAccessPriviledgeSettings(insertList,usersList);
			} 

		
    }


    public UserUploadHelper getHelper() {
    	return helper;
    }


    public void setHelper(UserUploadHelper helper) {
    	this.helper = helper;
    }

    public UserRepository getUserRepository() {
    	return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
    	this.userRepository = userRepository;
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


    public UserService getUserService() {
    	return userService;
    }


    public void setUserService(UserService userService) {
    	this.userService = userService;
    }


    public void setRequest(DataProcessingRequestT request) {
    	this.request = request;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {

    	try{
    		this.stepExecution = stepExecution;
    		ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
    		usersList=(List<UserT>)jobContext.get("UsersList");
    	}
    	catch(Exception e)
    	{
    		System.out.println("Exception" + e);
    	}

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

    	try {
    		ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
    		DataProcessingRequestT request = (DataProcessingRequestT) jobContext.get(REQUEST);
    		if ( errorList != null) {
    			String errorPath = request.getFilePath() + "ERROR" +FILE_DIR_SEPERATOR;
    			String errorFileName = "userUpload_error.xlsx";
    			File file = new File(errorPath + errorFileName);
    			if(!file.exists()){
    				Workbook workbook = uploadErrorReport.writeErrorToWorkbook(errorList, Constants.USER_TEMPLATE_PRIVILEGE);
    				File file1 = FileManager.createFile(errorPath, errorFileName);
    				logger.info("created file : " + file1.getAbsolutePath());
    				FileOutputStream outputStream = new FileOutputStream(file1);
    				workbook.write(outputStream);
    				outputStream.flush();
    				outputStream.close();
    			} else {
    				Workbook workbook = ExcelUtils.getWorkBook(file);
    				uploadErrorReport.writeErrorToWorkbook(errorList,workbook,Constants.USER_TEMPLATE_PRIVILEGE);
    				FileOutputStream outputStream = new FileOutputStream(file);
    				workbook.write(outputStream); //write changes
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
    public void onWritePossible() throws IOException 
    {

    }


    @Override
    public void onError(Throwable throwable) {
    	logger.error("Error while writing the error report: {}", throwable);

    }


}
