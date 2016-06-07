package com.tcs.destination.writer;

import static com.tcs.destination.utils.Constants.REQUEST;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

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
import com.tcs.destination.bean.GeographyCountryMappingT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.TimeZoneMappingT;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.service.DataProcessingService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;

public class ConnectDwldSubspWriter implements ItemWriter<SubSpMappingT>,
		StepExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(ConnectDwldSubspWriter.class);

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
	public void write(List<? extends SubSpMappingT> items) throws Exception {

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
	            
			sheet = workbook.getSheet(Constants.CONNECT_TEMPLATE_SUBSP_SHEET_NAME);
		}

		if(items!=null) {
			for (SubSpMappingT subSpMappingT : items) {
				// Create row with rowCount
				Row row = sheet.createRow(rowCount);

				String actualsp = subSpMappingT.getActualSubSp();
				ExcelUtils.createCell(actualsp, row, 0);
				
				String subsp = subSpMappingT.getSubSp();
				ExcelUtils.createCell(subsp, row, 1);
				
				String displaySubsp = subSpMappingT.getDisplaySubSp();
				ExcelUtils.createCell(displaySubsp,row, 2);
				
				String code = "";
				if(subSpMappingT.getSpCode()!=null){
					code = subSpMappingT.getSpCode().toString();
				} 
				ExcelUtils.createCell(code,row, 3);
				
				boolean active = subSpMappingT.isActive();
				ExcelUtils.createCell(String.valueOf(active), row, 4);
				
				// Increment row counter
				rowCount++;
			}
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
