package com.tcs.destination.writer;

import static com.tcs.destination.utils.Constants.REQUEST;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.TimeZoneMappingT;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.service.DataProcessingService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.StringUtils;

public class UserDwldPrivilegeWriter implements ItemWriter<UserAccessPrivilegesT>,
		StepExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(UserDwldPrivilegeWriter.class);

	private StepExecution stepExecution;
	
	private String template;
	
	private String fileServerPath;

	private DataProcessingRequestRepository dataProcessingRequestRepository;
	
	private DataProcessingService dataProcessingService;
	
	private Sheet sheet;
	
	private Workbook workbook;
	
	private int rowCount = 1;
	
	private String filePath; 
	
	private FileInputStream fileInputStream;

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		
         try {
        	 fileInputStream.close();
        	 FileOutputStream outputStream = new FileOutputStream(new File(filePath));
             workbook.write(outputStream); //write changes
			 outputStream.close();  //close the stream
			 //removing the UseridUser map
			 ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
	         jobContext.remove("userDetails");
		} catch (IOException e) {
			logger.error("Error in after step process: {}", e);
		}
         
         
		return stepExecution.getExitStatus();

	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		logger.debug("Inside before step:");
		try {
			    this.stepExecution = stepExecution;
			} catch (Exception e) {
				logger.error("Error in before step process: {}", e);
			}
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
	 */
	@Override
	public void write(List<? extends UserAccessPrivilegesT> items) throws Exception {

		logger.debug("Inside write method:");
		
		if (rowCount == 1) {
			ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext.get(REQUEST);
			
			filePath = request.getFilePath() + request.getFileName();
			fileInputStream = new FileInputStream(new File(filePath));
			String fileName  = request.getFileName();
			
		    String fileExtension = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
            if(fileExtension.equalsIgnoreCase("xls")){
                workbook = new HSSFWorkbook(fileInputStream);
            } else if(fileExtension.equalsIgnoreCase("xlsx")){
            	workbook = new XSSFWorkbook(fileInputStream);
            } else if(fileExtension.equalsIgnoreCase("xlsm")){
            	workbook = new XSSFWorkbook(fileInputStream);
            }
	            
			sheet = workbook.getSheet(Constants.USER_TEMPLATE_PRIVILEGE);
		}

		Map<String, List<UserAccessPrivilegesT>> userPrivilegeMap = new HashMap<String,List<UserAccessPrivilegesT>>();
		ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
		Map<String,UserT> userIdUserMap = (Map<String, UserT>) jobContext.get("userDetails");
		
		//clubbing the same users together in a map for the given chunk
		for(UserAccessPrivilegesT userPrivilege : items){
			String userId = userPrivilege.getUserId();
			if(!userPrivilegeMap.isEmpty()){
				List<UserAccessPrivilegesT> userPrivilegesList = userPrivilegeMap.get(userId);
				if(userPrivilegesList!=null){
					userPrivilegesList.add(userPrivilege);
				} else {
					List<UserAccessPrivilegesT> userAccessPrivilegesList = new ArrayList<UserAccessPrivilegesT>();
					userAccessPrivilegesList.add(userPrivilege);
					userPrivilegeMap.put(userId,userAccessPrivilegesList);
				}
				
			} else {
				List<UserAccessPrivilegesT> userAccessPrivilegesList = new ArrayList<UserAccessPrivilegesT>();
				userAccessPrivilegesList.add(userPrivilege);
				userPrivilegeMap.put(userId, userAccessPrivilegesList);
			}
		}
		
		
		if(items!=null) {
			for (Map.Entry<String, List<UserAccessPrivilegesT>> entry : userPrivilegeMap.entrySet())
			{
				//Row row = sheet.createRow(rowCount);
			    String userId = entry.getKey();
			    List<UserAccessPrivilegesT> userPrivileges = entry.getValue();
			    for(UserAccessPrivilegesT privilege : userPrivileges){
			    	Row row = sheet.createRow(rowCount);
			    	ExcelUtils.createCell(userId, row, 1);
			    	UserT user = userIdUserMap.get(userId);
			    	ExcelUtils.createCell(user.getUserName(), row, 2);
			    	ExcelUtils.createCell(user.getUserGroup(), row, 3);
			    	ExcelUtils.createCell(privilege.getPrivilegeType(), row, 4);
			    	ExcelUtils.createCell(privilege.getPrivilegeValue(),row, 5);
			    	
			    	List<UserAccessPrivilegesT> childs = privilege.getUserAccessPrivilegesTs();
			    	StringBuffer ChildValues = new StringBuffer("");
			    	String childtype = "";
			    	
			    	//clubbing the childs together (secondary filter)
			    	if(childs!=null && !childs.isEmpty()){
			    		for(UserAccessPrivilegesT eachChildPrivilege : childs){
			    			if(StringUtils.isEmpty(ChildValues.toString())){
			    				ChildValues.append(eachChildPrivilege.getPrivilegeValue());
			    			} else {
			    				ChildValues.append(","+eachChildPrivilege.getPrivilegeValue());
			    			}
			    			childtype = eachChildPrivilege.getPrivilegeType();
			    		}
			    		ExcelUtils.createCell(childtype, row, 6);
			    		ExcelUtils.createCell(ChildValues.toString(), 
			    				row, 7);
			    	} 
			    	
			    	rowCount++;	
			    }
			    
			    
			   // rowCount++;
			}
//			for (UserAccessPrivilegesT userPrivilege : items) {
//				// Create row with rowCount
//				Row row = sheet.createRow(rowCount);
//
////				String timeZoneCode = timeZone.getTimeZoneCode();
////				ExcelUtils.createCell(timeZoneCode,row,0);
////				 
////				String timeZoneOffset = timeZone.getTimeZoneOffset();
////				ExcelUtils.createCell(timeZoneOffset,row,1);
////				
////				String timeZoneDesc = timeZone.getDescription();
////				ExcelUtils.createCell(timeZoneDesc,row,2);
//
//				// Increment row counter
//				rowCount++;
//			}
		} 
	}

	public StepExecution getStepExecution() {
		return stepExecution;
	}

	public void setStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getFileServerPath() {
		return fileServerPath;
	}

	public void setFileServerPath(String fileServerPath) {
		this.fileServerPath = fileServerPath;
	}

	public DataProcessingRequestRepository getDataProcessingRequestRepository() {
		return dataProcessingRequestRepository;
	}

	public void setDataProcessingRequestRepository(
			DataProcessingRequestRepository dataProcessingRequestRepository) {
		this.dataProcessingRequestRepository = dataProcessingRequestRepository;
	}

	public DataProcessingService getDataProcessingService() {
		return dataProcessingService;
	}

	public void setDataProcessingService(DataProcessingService dataProcessingService) {
		this.dataProcessingService = dataProcessingService;
	}

	public Sheet getSheet() {
		return sheet;
	}

	public void setSheet(Sheet sheet) {
		this.sheet = sheet;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}

	public FileInputStream getFileInputStream() {
		return fileInputStream;
	}

	public void setFileInputStream(FileInputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}


}
