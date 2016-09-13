package com.tcs.destination.writer;

import static com.tcs.destination.utils.Constants.FILE_DIR_SEPERATOR;
import static com.tcs.destination.utils.Constants.REQUEST;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.WriteListener;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.parboiled.common.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.Operation;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.helper.UserUploadHelper;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.service.UserService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.FileManager;

public class UserCustomWriter implements ItemWriter<String[]>, StepExecutionListener, WriteListener {

	private static final Logger logger = LoggerFactory
			.getLogger(UserCustomWriter.class);

	private DataProcessingRequestRepository dataProcessingRequestRepository;

	private UserUploadHelper helper;

	private DataProcessingRequestT request; 

	private StepExecution stepExecution;

	private List<UploadServiceErrorDetailsDTO> errorList = null;

	private UploadErrorReport uploadErrorReport;

	private UserService userService;

	private UserRepository userRepository;

	List<UserT> insertList;
	List<UserT> updateList;
	List<UserT> deleteList;

	@Override
	public void write(List<? extends String[]> items) throws Exception {
		logger.debug("Inside write:");
		insertList = new ArrayList<UserT>();
		updateList = new ArrayList<UserT>();
		deleteList = new ArrayList<UserT>();

		String operation = null; 
		

		for (String[] data: items) {
			operation = (String) data[1];
			if(operation!=null)
			{
				if (operation.equalsIgnoreCase(Operation.ADD.name())) {

					logger.debug("***USER ADD***");
					UserT user =  new UserT();
					UploadServiceErrorDetailsDTO errorDTO = helper.validateUserData(data, request.getUserT().getUserId() ,user);
					errorDTO.setSheetName(Constants.USER_TEMPLATE_USER_MASTER);
					
					if (errorDTO.getMessage() != null) {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
						errorList.add(errorDTO);
					} else if (errorDTO.getMessage() == null) {
						if(!isDuplicateIdOrName(user,insertList)){
							insertList.add(user);
						} else {
							int rowNum = Integer.parseInt(data[0]) + 1;
							errorDTO.setRowNumber(rowNum);
							String errMsg = errorDTO.getMessage();
							
							if(!StringUtils.isEmpty(errMsg)){
								errorDTO.setMessage(errMsg + " Duplicate User Id / name in the sheet ");	
							} else {
								errorDTO.setMessage(" Duplicate User Id / name in the sheet ");	
							}
							errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
							errorList.add(errorDTO);
						}
					}

				}
				else if (operation.equalsIgnoreCase(Operation.UPDATE.name()))
				{
					logger.debug("***USER UPDATE***");
					UploadServiceErrorDetailsDTO errorDTO = new UploadServiceErrorDetailsDTO();
					errorDTO.setSheetName(Constants.USER_TEMPLATE_USER_MASTER);
					if(data[2]!=null){
					String userId =data[2].toString();
					userId = userId.indexOf(".") < 0 ? userId : userId.replaceAll("0*$", "").replaceAll("\\.$", "");
				    
					if (!userId.isEmpty()) {
						try{
							
							UserT user= userRepository.findOne(userId);
						    if (user != null) {
							errorDTO = helper.validateUserDataUpdate(data, request.getUserT().getUserId() ,user);
							errorDTO.setSheetName(Constants.USER_TEMPLATE_USER_MASTER);
							if (errorDTO.getMessage() != null) {
								errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
								errorList.add(errorDTO);
							} 
							else if (errorDTO.getMessage() == null) {
								updateList.add(user);
							}
						} else {
							errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
							errorDTO.setRowNumber(Integer.parseInt(data[0]) + 1);
							errorDTO.setMessage("User Id is invalid");
							errorList.add(errorDTO);
						}
					}catch(InvocationTargetException e){System.out.println("Exception Cause:"+e.getCause());}
						}
					else {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
						errorDTO.setRowNumber(Integer.parseInt(data[0]) + 1);
						errorDTO.setMessage("User Id is mandatory");
						errorList.add(errorDTO);
					}
					}else {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
						errorDTO.setRowNumber(Integer.parseInt(data[0]) + 1);
						errorDTO.setMessage("User Id is mandatory");
						errorList.add(errorDTO);
					}
					}
				else if (operation.equalsIgnoreCase(Operation.DELETE.name()))
				{
					logger.debug("***USER DELETE***");
					UserT user =  new UserT();
					UploadServiceErrorDetailsDTO errorDTO =  helper.validateUserId(data, user);
					errorDTO.setSheetName(Constants.USER_TEMPLATE_USER_MASTER);
					 if (errorDTO.getMessage() != null) {
							errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
							errorList.add(errorDTO);
						} else if (errorDTO.getMessage() == null) {
							deleteList.add(user);
					}
					
				}
				
			}
		}
		
		if ((CollectionUtils.isNotEmpty(insertList)) || (CollectionUtils.isNotEmpty(updateList)) || (CollectionUtils.isNotEmpty(deleteList))) {

			if (CollectionUtils.isNotEmpty(insertList)) {
				userService.save(insertList);
			} 
			else if (CollectionUtils.isNotEmpty(updateList)){ 
				userService.updateUser(updateList);
			}
			else if (CollectionUtils.isNotEmpty(deleteList)){ 
				userService.deleteUser(deleteList);
			}

		}
	}


	private boolean isDuplicateIdOrName(UserT user, List<UserT> insertList) {
		boolean isDuplicate = false;
		for(UserT userFromList : insertList){
			if(
					(user.getUserId().equals(userFromList.getUserId()))		
					||(user.getUserName().equals(userFromList.getUserName()))
			 ) {
				isDuplicate = true;
				break;
			}
		}
		return isDuplicate;
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
		this.stepExecution = stepExecution;
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {

		try {

			ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();

			DataProcessingRequestT request = (DataProcessingRequestT) jobContext.get(REQUEST);

			if ( errorList != null) {
				Workbook workbook = uploadErrorReport.writeErrorToWorkbook(errorList, Constants.USER_TEMPLATE_USER_MASTER);

				String errorPath = request.getFilePath() + "ERROR" + FILE_DIR_SEPERATOR;
				String errorFileName = "userUpload_error.xlsx";

				File file = FileManager.createFile(errorPath, errorFileName);
				FileOutputStream outputStream = new FileOutputStream(file);
				workbook.write(outputStream);
				outputStream.flush();
				outputStream.close();

				request.setErrorFileName(errorFileName);	
				request.setErrorFilePath(errorPath);

			}
			request.setStatus(RequestStatus.INPROGRESS.getStatus());
			dataProcessingRequestRepository.save(request);
			jobContext.put("UsersList",insertList);



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
