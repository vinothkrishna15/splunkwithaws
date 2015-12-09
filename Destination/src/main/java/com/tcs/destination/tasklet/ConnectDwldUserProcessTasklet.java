package com.tcs.destination.tasklet;

import static com.tcs.destination.utils.Constants.REQUEST;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;



@Component("connectDwldUserProcessTasklet")
public class ConnectDwldUserProcessTasklet implements Tasklet{
	
	private static final Logger logger = LoggerFactory
			.getLogger(ConnectDwldUserProcessTasklet.class);
	
	private Map<String,UserT> userIdUserMap;
	
	private int rowCount = 1;
	
    private Sheet sheet;
	
	private Workbook workbook;
	
	private String filePath; 
	
	private FileInputStream fileInputStream;

	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		
		ExecutionContext jobContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
		userIdUserMap = (Map<String, UserT>) jobContext.get("userIdUserMap");
		if (rowCount == 1) {
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
	            
			sheet = workbook.getSheet(Constants.CONNECT_TEMPLATE_USER_SHEET_NAME);
		}
		
		for (Map.Entry<String, UserT> entry : userIdUserMap.entrySet())
		{
			 Row row = sheet.createRow(rowCount);
		     
			 String userId = entry.getKey();
		     ExcelUtils.createCell(userId, row, 0);
			 
		     UserT user = entry.getValue();
		     String userName = user.getUserName();
		     ExcelUtils.createCell(userName, row, 1);
		     
		     rowCount++;
		}
		
		try {
       	 fileInputStream.close();
       	 FileOutputStream outputStream = new FileOutputStream(new File(filePath));
            workbook.write(outputStream); //write changes
			 outputStream.close();  //close the stream
			 jobContext.remove("userIdUserMap");
		} catch (IOException e) {
			logger.error("Error in after step process: {}", e);
		}
		
		return RepeatStatus.FINISHED;
	}

}
