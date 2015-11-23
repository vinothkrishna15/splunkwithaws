package com.tcs.destination.writer;

import static com.tcs.destination.utils.Constants.REQUEST;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
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
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.service.DataProcessingService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;

public class UserDwldGoalRefPreprocessWriter implements ItemWriter<Object[]>,
		StepExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(UserDwldGoalRefPreprocessWriter.class);

	private StepExecution stepExecution;
	
//	private String template;
//	
//	private String fileServerPath;
//
//	private DataProcessingRequestRepository dataProcessingRequestRepository;
//	
//	private DataProcessingService dataProcessingService;
//	
//	private Sheet sheet;
	
	//private Workbook workbook;
	
	//private int rowCount = 1;
	
	//private String filePath; 
	
	//private FileInputStream fileInputStream;

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		
//         try {
//        	 fileInputStream.close();
//        	 FileOutputStream outputStream = new FileOutputStream(new File(filePath));
//             workbook.write(outputStream); //write changes
//			 outputStream.close();  //close the stream
//		} catch (IOException e) {
//			logger.error("Error in after step process: {}", e);
//		}
		
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
	public void write(List<? extends Object[]> items) throws Exception {

		logger.debug("Inside write method:");
		
		Map<String,List<String>> goalIdGroupMap = new HashMap<String,List<String>>();
		ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
		if(items!=null){
			for (Object[] goalGroupRecord : items) {
				String goalId = (String)goalGroupRecord[0];
				String group = (String)goalGroupRecord[1];
				if(goalIdGroupMap.isEmpty()){
					
					List<String> groupList = new ArrayList<String>();
					groupList.add(group);
					goalIdGroupMap.put(goalId, groupList);
				} else {
					List<String> chkGroupList = goalIdGroupMap.get(goalId);
					if(chkGroupList==null){
						List<String> groupList = new ArrayList<String>();
						groupList.add(group);
						goalIdGroupMap.put(goalId, groupList);
					} else {
						chkGroupList.add(group);
					}
				}
			}
			jobContext.put("goalIdGroupMap", goalIdGroupMap);
		}
		
		

//		if(items!=null) {
//			for (Object[] userGoals : items) {
//				// Create row with rowCount
//				Row row = sheet.createRow(rowCount);
//
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
//
//				// Increment row counter
//				rowCount++;
//			}
//		} 
	}

	public StepExecution getStepExecution() {
		return stepExecution;
	}

	public void setStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

//	public String getTemplate() {
//		return template;
//	}
//
//	public void setTemplate(String template) {
//		this.template = template;
//	}
//
//	public String getFileServerPath() {
//		return fileServerPath;
//	}
//
//	public void setFileServerPath(String fileServerPath) {
//		this.fileServerPath = fileServerPath;
//	}
//
//	public DataProcessingRequestRepository getDataProcessingRequestRepository() {
//		return dataProcessingRequestRepository;
//	}
//
//	public void setDataProcessingRequestRepository(
//			DataProcessingRequestRepository dataProcessingRequestRepository) {
//		this.dataProcessingRequestRepository = dataProcessingRequestRepository;
//	}
//
//	public DataProcessingService getDataProcessingService() {
//		return dataProcessingService;
//	}
//
//	public void setDataProcessingService(DataProcessingService dataProcessingService) {
//		this.dataProcessingService = dataProcessingService;
//	}
//
//	public Sheet getSheet() {
//		return sheet;
//	}
//
//	public void setSheet(Sheet sheet) {
//		this.sheet = sheet;
//	}
//
//	public int getRowCount() {
//		return rowCount;
//	}
//
//	public void setRowCount(int rowCount) {
//		this.rowCount = rowCount;
//	}
//
//	public Workbook getWorkbook() {
//		return workbook;
//	}
//
//	public void setWorkbook(Workbook workbook) {
//		this.workbook = workbook;
//	}
//
//	public FileInputStream getFileInputStream() {
//		return fileInputStream;
//	}
//
//	public void setFileInputStream(FileInputStream fileInputStream) {
//		this.fileInputStream = fileInputStream;
//	}


}
