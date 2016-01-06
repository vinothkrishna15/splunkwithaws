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
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.service.DataProcessingService;
import com.tcs.destination.utils.Constants;

/**
 * This CustomerContactMasterRefDwldWriter class contains the functionality to write customer 
 * data into the workbook
 * 
 */
public class CustomerContactMasterRefDwldWriter implements ItemWriter<CustomerMasterT>,
		StepExecutionListener {
	private static final Logger logger = LoggerFactory.getLogger(CustomerContactMasterRefDwldWriter.class);

	private int rowCount=1;
	private String template;
	
	private String fileServerPath;
	private String filePath;
	private FileInputStream fileInputStream;
	private Workbook workbook;
	private Sheet sheet;
	private DataProcessingRequestRepository dataProcessingRequestRepository;
	private DataProcessingService dataProcessingService;
	private StepExecution stepExecution;
	
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		try {
			logger.debug("Inside after step of customerContact Master Ref writer");
			fileInputStream.close();
			FileOutputStream outputStream = new FileOutputStream(new File(
					filePath));
			workbook.write(outputStream); // write changes
			outputStream.close(); // close the stream

			ExecutionContext jobContext = stepExecution.getJobExecution()
					.getExecutionContext();
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext
					.get(REQUEST);

			request.setStatus(RequestStatus.PROCESSED.getStatus());
			dataProcessingRequestRepository.save(request);

			jobContext.remove(REQUEST);
		} catch (IOException e) {
			logger.error("Error in after step process: {}", e);
		}

		return stepExecution.getExitStatus();
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		logger.debug("Inside before step of customerContact Master Ref writer");

		try {
			this.stepExecution = stepExecution;
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
	public void write(List<? extends CustomerMasterT> items) throws Exception {
		logger.debug("Inside write method of customerContact Master Ref writer");
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
					.getSheet(Constants.CUSTOMER_MASTER_REF);
		}
		if (items != null) {
			for (CustomerMasterT cmt : items){
					// Create row with rowCount
					Row row = sheet.createRow(rowCount);

					// Create new Cell and set cell value
					Cell cellGrpClient = row.createCell(0);
					cellGrpClient.setCellValue(cmt.getGroupCustomerName().trim());

					Cell cellCustName = row.createCell(1);
					cellCustName.setCellValue(cmt.getCustomerName().trim());

					Cell cellIou = row.createCell(2);
					cellIou.setCellValue(cmt.getIouCustomerMappingT().getIou().trim());

					Cell cellGeo = row.createCell(3);
					cellGeo.setCellValue(cmt.getGeographyMappingT().getGeography()
							.trim());

					// Increment row counter
					rowCount++;
				
			}
			}
		}

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

	public Workbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}

	public Sheet getSheet() {
		return sheet;
	}

	public void setSheet(Sheet sheet) {
		this.sheet = sheet;
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

	public StepExecution getStepExecution() {
		return stepExecution;
	}

	public void setStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}
	

}
