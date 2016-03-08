package com.tcs.destination.writer;

import static com.tcs.destination.utils.Constants.REQUEST;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
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
import com.tcs.destination.bean.GoalGroupMappingT;
import com.tcs.destination.bean.GoalMappingT;
import com.tcs.destination.bean.TimeZoneMappingT;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.service.DataProcessingService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.StringUtils;

public class UserDwldGoalRefWriter implements ItemWriter<GoalMappingT>,
		StepExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(UserDwldGoalRefWriter.class);

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
	public void write(List<? extends GoalMappingT> items) throws Exception {

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
	            
			sheet = workbook.getSheet(Constants.USER_TEMPLATE_USERGOALREF);
		}
		ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
		Map<String,List<String>> goalIdGroupMap = (Map<String, List<String>>) jobContext.get("goalIdGroupMap");
		if(items!=null) {
			for (GoalMappingT goal : items) {
				// Create row with rowCount
				Row row = sheet.createRow(rowCount);

				String goalId = goal.getGoalId();
				ExcelUtils.createCell(goalId.trim(), row, 0);
				
				String goalName = goal.getGoalName();
				ExcelUtils.createCell(goalName.trim(), row, 1);
				
				String displayUnit = goal.getDisplayUnit();
				ExcelUtils.createCell(displayUnit.trim(), row, 2);
				
				String finYear = goal.getFinancialyear();
				ExcelUtils.createCell(finYear.trim(), row, 3);
				
				String defTarget = goal.getDefaultTarget().toString();
				ExcelUtils.createCell(defTarget.trim(), row, 4);
				
				//String userGroup = getAppendedGroups(goal.getGoalGroupMappingTs());
				//ExcelUtils.createCell(userGroup.trim(), row, 5);
				
				String isActive = "Y";
				ExcelUtils.createCell(isActive.trim(), row, 6);
				
				List<String> groups = goalIdGroupMap.get(goalId);
				String groupsStr = getAppendedStrGroups(groups);
				ExcelUtils.createCell(groupsStr.trim(), row, 5);
				
//				String userId = (String)userGoals[0];
//				ExcelUtils.createCell(userId.trim(), row, 1);
//				
//				String userName = (String)userGoals[1];
//				ExcelUtils.createCell(userName.trim(), row, 2);
//				
//				String userGroup = (String)userGoals[2];
//				ExcelUtils.createCell(userGroup.trim(), row, 3);
//				
//				String goalName = (String)userGoals[3];
//				ExcelUtils.createCell(goalName.trim(), row, 4);
//				
//				String financialYear = (String)userGoals[4];
//				ExcelUtils.createCell(financialYear.trim(), row, 5);
//				
//				BigDecimal target = (BigDecimal)userGoals[5];
//				ExcelUtils.createCell(target.toString(), row, 6);

				// Increment row counter
				rowCount++;
			}
		} 
	}

	private String getAppendedStrGroups(List<String> groups) {
		StringBuffer groupBuffer = new StringBuffer("");
		for(String group : groups){
			if(StringUtils.isEmpty(groupBuffer.toString())){
				groupBuffer.append(group.trim());
			} else {
				groupBuffer.append("," + group.trim());
			}
		}
		return groupBuffer.toString();
	}
	
	private String getAppendedGroups(List<GoalGroupMappingT> groups) {
		StringBuffer groupBuffer = new StringBuffer("");
		for(GoalGroupMappingT group : groups){
			groupBuffer.append("," + group.getUserGroup());
		}
		return groupBuffer.toString();
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
