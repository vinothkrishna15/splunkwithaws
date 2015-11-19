package com.tcs.destination.writer;

import static com.tcs.destination.utils.Constants.REQUEST;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import com.tcs.destination.bean.RevenueCustomerMappingT;
import com.tcs.destination.helper.CommonHelper;
import com.tcs.destination.utils.Constants;

public class RevenueDwldWriter implements ItemWriter<RevenueCustomerMappingT>,
		StepExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(RevenueDwldWriter.class);

	private StepExecution stepExecution;
	
	private Sheet sheet;
	
	private Workbook workbook;
	
	private int rowCount = 1;
	
	private String fileName; 
	
	private FileInputStream fileInputStream;
	
	private Map<String, CustomerMasterT> mapOfCustomerMasterT = null;
	
	private CommonHelper commonHelper;

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		
        try {
        	 fileInputStream.close();
        	 FileOutputStream outputStream = new FileOutputStream(new File(fileName));
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
				mapOfCustomerMasterT = commonHelper.getCustomerMappingT();
				
			} catch (Exception e) {
				logger.error("Error in before step process: {}", e);
			}

	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
	 */
	@Override
	public void write(List<? extends RevenueCustomerMappingT> items) throws Exception {

		logger.debug("Inside write method:");
		
		if (rowCount == 1) {
			ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext.get(REQUEST);
			
			FileInputStream fileInputStream = new FileInputStream(new File(request.getFilePath() + request.getFileName()));
			String fileName  = request.getFileName();
			
		    String fileExtension = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
            if(fileExtension.equalsIgnoreCase("xls")){
                workbook = new HSSFWorkbook(fileInputStream);
            } else if(fileExtension.equalsIgnoreCase("xlsx")){
            	workbook = new XSSFWorkbook(fileInputStream);
            } else if(fileExtension.equalsIgnoreCase("xlsm")){
            	workbook = new XSSFWorkbook(fileInputStream);
            }
	            
			sheet = workbook.getSheet(Constants.FINANCE_MAPPING_SHEET_NAME);
		}

		if(items!=null) {
			for (RevenueCustomerMappingT finance : items) {
				// Create row with rowCount
				Row row = sheet.createRow(rowCount);
				
				CustomerMasterT customerObj = mapOfCustomerMasterT.get(finance.getCustomerName());
				
				Cell cellGroupCustomerName = row.createCell(1);
				cellGroupCustomerName.setCellValue(customerObj.getGroupCustomerName().trim());

				Cell cellCustomerName = row.createCell(2);
				cellCustomerName.setCellValue(customerObj.getCustomerName().trim());

				Cell cellIou = row.createCell(3);
				cellIou.setCellValue(customerObj.getIou().trim());
				
				Cell cellGeo = row.createCell(4);
				cellGeo.setCellValue(customerObj.getGeography().trim());
				
				// Create new Cell and set cell value
				Cell cellFinanceCustomerName = row.createCell(5);
				cellFinanceCustomerName.setCellValue(finance.getFinanceCustomerName().trim());

				Cell cellFinanceIou = row.createCell(6);
				cellFinanceIou.setCellValue(finance.getFinanceIou().trim());

				Cell cellFinanceGeo = row.createCell(7);
				cellFinanceGeo.setCellValue(finance.getCustomerGeography().trim());

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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public FileInputStream getFileInputStream() {
		return fileInputStream;
	}

	public void setFileInputStream(FileInputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}

	public Map<String, CustomerMasterT> getMapOfCustomerMasterT() {
		return mapOfCustomerMasterT;
	}

	public void setMapOfCustomerMasterT(
			Map<String, CustomerMasterT> mapOfCustomerMasterT) {
		this.mapOfCustomerMasterT = mapOfCustomerMasterT;
	}

	public CommonHelper getCommonHelper() {
		return commonHelper;
	}

	public void setCommonHelper(CommonHelper commonHelper) {
		this.commonHelper = commonHelper;
	}


}
