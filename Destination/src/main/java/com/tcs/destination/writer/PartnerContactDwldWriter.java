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

import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;

public class PartnerContactDwldWriter implements ItemWriter<ContactT>,
		StepExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(PartnerContactDwldWriter.class);

	private StepExecution stepExecution;
	
	private Sheet sheet;
	
	private Workbook workbook;
	
	private int rowCount = 1;
	
	private String filePath; 
	
	private FileInputStream fileInputStream;
	
	private Map<String,String> partnerIdPartnerMap;
	
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		
        try {
        	 fileInputStream.close();
        	 FileOutputStream outputStream = new FileOutputStream(new File(filePath));
             workbook.write(outputStream); //write changes
			 outputStream.close();  //close the stream
			 ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
			 jobContext.remove("partnerIdPartnerMap");
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
			    ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
			    partnerIdPartnerMap = (Map<String, String>) jobContext.get("partnerIdPartnerMap");
			} catch (Exception e) {
				logger.error("Error in before step process: {}", e);
			}

	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
	 */
	@Override
	public void write(List<? extends ContactT> items) throws Exception {

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
	            
			sheet = workbook.getSheet(Constants.CONNECT_TEMPLATE_PARTNER_CONTACT_SHEET_NAME);
		}

		if(items!=null) {
			for (ContactT contact : items) {
				// Create row with rowCount
				Row row = sheet.createRow(rowCount);

				String contactId = contact.getContactId();
				
				String partnerId = contact.getPartnerId();
				String partnerName = partnerIdPartnerMap.get(partnerId);
				ExcelUtils.createCell(partnerName, row, 0);
				
				String contactName = contact.getContactName();
				ExcelUtils.createCell(contactName, row, 1);
				
				String contactRole =contact.getContactRole();
				ExcelUtils.createCell(contactRole, row, 2);
				
				String emailId = contact.getContactEmailId();
				ExcelUtils.createCell(emailId, row, 3);
				
				// Create new Cell and set cell value
//				Cell cellBeaconCustomerName = row.createCell(0);
//				cellBeaconCustomerName.setCellValue(iou.getDisplayIou().trim());
//
//				Cell cellBeaconIou = row.createCell(1);
//				cellBeaconIou.setCellValue(iou.getIou().trim());

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

	public FileInputStream getFileInputStream() {
		return fileInputStream;
	}

	public void setFileInputStream(FileInputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}


}
