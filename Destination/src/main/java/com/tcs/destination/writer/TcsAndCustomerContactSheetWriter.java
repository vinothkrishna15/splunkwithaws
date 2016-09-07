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
import org.springframework.beans.factory.annotation.Autowired;

import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.data.repository.ContactCustomerLinkTRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.utils.Constants;

public class TcsAndCustomerContactSheetWriter implements ItemWriter<ContactT>,
StepExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(TcsAndCustomerContactSheetWriter.class);

	private StepExecution stepExecution;

	private Sheet sheet;

	private Workbook workbook;

	private int rowCount = 1;
	
	private String filePath;

	private FileInputStream fileInputStream;
	
	@Autowired
	ContactCustomerLinkTRepository contactCustomerLinkTRepository;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		try {
			this.stepExecution = stepExecution;
		} catch (Exception e) {
			logger.error("Error in before step process: {}", e);
		}
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {

		try {
			fileInputStream.close();
			FileOutputStream outputStream = new FileOutputStream(new File(
					filePath));
			workbook.write(outputStream); // write changes
			outputStream.close(); // close the stream
		} catch (IOException e) {
			logger.error("Error in after step process: {}", e);
		}
		return stepExecution.getExitStatus();
	}

	@Override
	public void write(List<? extends ContactT> items) throws Exception {
		logger.debug("Inside write method:");

		if (rowCount == 1) {
			ExecutionContext jobContext = stepExecution.getJobExecution()
					.getExecutionContext();
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext
					.get(REQUEST);
			filePath = request.getFilePath() + request.getFileName();
			fileInputStream = new FileInputStream(new File(filePath));
			String fileName = request.getFileName();
			String fileExtension = fileName.substring(
					fileName.lastIndexOf(".") + 1, fileName.length());

			if (fileExtension.equalsIgnoreCase("xls")) {
				workbook = new HSSFWorkbook(fileInputStream);
			} else if (fileExtension.equalsIgnoreCase("xlsx")) {
				workbook = new XSSFWorkbook(fileInputStream);
			} else if (fileExtension.equalsIgnoreCase("xlsm")) {
				workbook = new XSSFWorkbook(fileInputStream);
			}

			sheet = workbook
					.getSheet(Constants.OPPORTUNITY_TEMPLATE_TCS_AND_CUSTOMER_SHEET_NAME);
		}

		if (items != null) {
			for (ContactT contact : items) {
				
				String customerName = "";

				// Create new Cell and set cell value
				if (contact.getContactCategory().equalsIgnoreCase(EntityType.CUSTOMER.toString())) {

					// Create row with rowCount
					Row row = sheet.createRow(rowCount);
					
					List<ContactCustomerLinkT> contactCustomerLinkTs = contactCustomerLinkTRepository.findByContactId(contact.getContactId());
					if (contactCustomerLinkTs.size() > 0) {
						if (contactCustomerLinkTs.get(0).getCustomerMasterT() != null) {
							customerName = contactCustomerLinkTs.get(0).getCustomerMasterT().getCustomerName();
						}
					}
					Cell cellCustomerName = row.createCell(0);
					cellCustomerName.setCellValue(customerName.trim());

					Cell cellContactType = row.createCell(1);
					cellContactType.setCellValue(contact.getContactType().trim());

					Cell employeeNumber = row.createCell(2);
					employeeNumber.setCellValue(contact.getEmployeeNumber().trim());

					Cell cellContactName = row.createCell(3);
					cellContactName.setCellValue(contact.getContactName());

					Cell cellContactRole = row.createCell(4);
					cellContactRole.setCellValue(contact.getContactRole());

					Cell cellContactEmail = row.createCell(5);
					cellContactEmail.setCellValue(contact.getContactEmailId());

					Cell cellContactTelephone = row.createCell(6);
					cellContactTelephone.setCellValue(contact.getContactTelephone());
					
					// Increment row counter
					rowCount++;
				}
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

	public Workbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public FileInputStream getFileInputStream() {
		return fileInputStream;
	}

	public void setFileInputStream(FileInputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}
}
