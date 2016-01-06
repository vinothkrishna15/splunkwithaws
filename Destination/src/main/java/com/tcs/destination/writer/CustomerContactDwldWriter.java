package com.tcs.destination.writer;

import static com.tcs.destination.utils.Constants.DOWNLOAD;
import static com.tcs.destination.utils.Constants.DOWNLOADCONSTANT;
import static com.tcs.destination.utils.Constants.FILE_DIR_SEPERATOR;
import static com.tcs.destination.utils.Constants.REQUEST;
import static com.tcs.destination.utils.Constants.XLSM;

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
import org.springframework.http.HttpStatus;

import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.enums.ContactType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.DataProcessingService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.FileManager;
import com.tcs.destination.utils.PropertyUtil;
/**
 * This CustomerContactDwldWriter class contains the functionality to writes Contact 
 * data into the workbook
 * 
 */
public class CustomerContactDwldWriter implements ItemWriter<ContactT>,
		StepExecutionListener {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomerContactDwldWriter.class);

	private int rowCount=1;
	private String template;
	private DataProcessingRequestRepository dataProcessingRequestRepository;
	private DataProcessingService dataProcessingService;
	private Sheet sheet;
	private Workbook workbook;
	private StepExecution stepExecution;
	private String fileServerPath;
	private String filePath;
	private FileInputStream fileInputStream;
	private Map<String,CustomerMasterT> mapOfContactCustomerLinkT;
	
	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
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

	public Workbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}

	public StepExecution getStepExecution() {
		return stepExecution;
	}

	public void setStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	public String getFileServerPath() {
		return fileServerPath;
	}

	public void setFileServerPath(String fileServerPath) {
		this.fileServerPath = fileServerPath;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public FileInputStream getFileInputStream() {
		return fileInputStream;
	}

	public void setFileInputStream(FileInputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		logger.debug("Inside after method of customer contact download writer");

		try {
			
			fileInputStream.close();
			FileOutputStream outputStream = new FileOutputStream(new File(
					filePath));
			workbook.write(outputStream); // write changes
			outputStream.close(); // close the stream
			
			ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
			 jobContext.remove("mapOfContactCustomerLinkT");
		} catch (IOException e) {
			logger.error("Error in after step process: {}", e);
		}

		return stepExecution.getExitStatus();
	
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		logger.debug("Inside before method of customer contact download writer");

		try {
			this.stepExecution = stepExecution;

			ExecutionContext jobContext = stepExecution.getJobExecution()
					.getExecutionContext();
			mapOfContactCustomerLinkT = (Map<String,CustomerMasterT>) jobContext.get("mapOfContactCustomerLinkT");
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext
					.get(REQUEST);
			String entity = dataProcessingService.getEntity(request
					.getRequestType());
			String environmentName=PropertyUtil.getProperty("environment.name");
			
			StringBuffer filePath = new StringBuffer(fileServerPath)
					.append(entity).append(FILE_DIR_SEPERATOR).append(DOWNLOAD)
					.append(FILE_DIR_SEPERATOR)
					.append(DateUtils.getCurrentDate())
					.append(FILE_DIR_SEPERATOR)
					.append(request.getUserT().getUserId())
					.append(FILE_DIR_SEPERATOR);
			StringBuffer fileName = new StringBuffer(environmentName)
			.append(entity)
			.append(DOWNLOADCONSTANT)
			.append(DateUtils.getCurrentDateForFile()).append(XLSM);
		
			FileManager.copyFile(filePath.toString(), template,
					fileName.toString());

			request.setFilePath(filePath.toString());
			request.setFileName(fileName.toString());
			request.setStatus(RequestStatus.INPROGRESS.getStatus());
			dataProcessingRequestRepository.save(request);

			jobContext.put(REQUEST, request);

		} catch (Exception e) {
			logger.error("Error in before step process: {}", e);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
	 */
	@Override
	public void write(List<? extends ContactT> items) throws Exception {
		logger.debug("Inside write method of customer contact download writer");

		if (rowCount == 1) {
			ExecutionContext jobContext = stepExecution.getJobExecution()
					.getExecutionContext();
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext
					.get(REQUEST);

			filePath = request.getFilePath() + request.getFileName();
			fileInputStream = new FileInputStream(new File(
					request.getFilePath() + request.getFileName()));
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

			sheet = workbook.getSheet(Constants.CUSTOMER_CONTACT_SHEET_NAME);
		}
		
		if(items!=null)
		{
			//mapOfContactCustomerLinkT = customerDownloadService.getContactCustomerLinkT();
			for (ContactT ct : items)
			{
				if ((ct.getContactCategory().equals(EntityType.CUSTOMER.toString()) && 
						(ct.getContactType().equals(ContactType.EXTERNAL.toString())))) { // For Customer Contact

					// Create row with rowCount
					Row row = sheet.createRow(rowCount);

					// Create new Cell and set cell value
					Cell cellCustomerId = row.createCell(1);
					cellCustomerId.setCellValue(ct.getContactId());
					
					// Create new Cell and set cell value saturday modified
					Cell cellCustomerName = row.createCell(2);
					if(mapOfContactCustomerLinkT.containsKey(ct.getContactId())){
					CustomerMasterT customerObj = mapOfContactCustomerLinkT.get(ct.getContactId());
					cellCustomerName.setCellValue(customerObj.getCustomerName());
					}
					else {
						throw new DestinationException(HttpStatus.NOT_FOUND, "customername NOT Found");
					}
				
                    Cell cellCustomerContactType = row.createCell(3);
					cellCustomerContactType.setCellValue(ct.getContactType());

					Cell cellCustomerContactName = row.createCell(5);
					cellCustomerContactName.setCellValue(ct.getContactName());

					Cell cellCustomerContactRole = row.createCell(6);
					cellCustomerContactRole.setCellValue(ct.getContactRole());

					Cell cellCustomerContactEmailId = row.createCell(7);
					if(ct.getContactEmailId()!=null) {
						cellCustomerContactEmailId.setCellValue(ct.getContactEmailId());
					}

					// Increment row counter for partner contact sheet
					rowCount++;

				}
			
			}
		}
		
	}

}
