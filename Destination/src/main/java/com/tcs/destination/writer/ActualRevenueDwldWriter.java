/**
 * 
 * ActualRevenueDwldWriter.java 
 *
 * @author TCS
 * @Version 1.0 - 2015
 * 
 * @Copyright 2015 Tata Consultancy 
 */
package com.tcs.destination.writer;

import static com.tcs.destination.utils.Constants.DOWNLOAD;
import static com.tcs.destination.utils.Constants.FILE_DIR_SEPERATOR;
import static com.tcs.destination.utils.Constants.REQUEST;
import static com.tcs.destination.utils.Constants.DOWNLOADCONSTANT;
import static com.tcs.destination.utils.Constants.XLSM;

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

import com.tcs.destination.bean.ActualRevenuesDataT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.service.DataProcessingService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.FileManager;
import com.tcs.destination.utils.PropertyUtil;

/**
 * This ActualRevenueDwldWriter class contains the functionality to populate
 * datesheet for actual revenue
 * 
 */
public class ActualRevenueDwldWriter implements
		ItemWriter<ActualRevenuesDataT>, StepExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(ActualRevenueDwldWriter.class);

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
	public void beforeStep(StepExecution stepExecution) {

		logger.debug("Inside before step:");

		try {
			this.stepExecution = stepExecution;

			ExecutionContext jobContext = stepExecution.getJobExecution()
					.getExecutionContext();
			String environmentName=PropertyUtil.getProperty("environment.name");
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext
					.get(REQUEST);
			String entity = dataProcessingService.getEntity(request
					.getRequestType());
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
	public void write(List<? extends ActualRevenuesDataT> items)
			throws Exception {

		logger.debug("Inside write method:");

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

			sheet = workbook.getSheet(Constants.ACTUAL_REVENUE_DATA);
		}

		if (items != null) {
			for (ActualRevenuesDataT actualRevenue : items) {
				// Create row with rowCount
				Row row = sheet.createRow(rowCount);

				// Create new Cell and set cell value

				Cell cellGLMonth = row.createCell(3);
				cellGLMonth.setCellValue(actualRevenue.getMonth().trim());

				Cell cellQuarter = row.createCell(4);
				String quarter1 = actualRevenue.getQuarter().substring(0, 2);
				String quarter2 = actualRevenue.getQuarter().substring(10, 12);
				cellQuarter.setCellValue(quarter1 + quarter2);

				Cell cellFinancialYear = row.createCell(5);
				String financialYr1 = actualRevenue.getFinancialYear()
						.substring(0, 2);
				String financialYr2 = actualRevenue.getFinancialYear()
						.substring(8, 10);
				cellFinancialYear.setCellValue(financialYr1 + financialYr2);

				Cell cellRevenue = row.createCell(6);
				cellRevenue.setCellValue(actualRevenue.getRevenue().toString());

				Cell cellClientCountryName = row.createCell(7);
				cellClientCountryName.setCellValue(actualRevenue
						.getClientCountry().toString());

				Cell cellFinanceGeography = row.createCell(8);
				//cellFinanceGeography.setCellValue(actualRevenue.getFinanceGeography().toString());
				cellFinanceGeography.setCellValue(actualRevenue
						.getRevenueCustomerMappingT().getCustomerGeography().toString());
						
				Cell cellSubsp = row.createCell(9);
				cellSubsp.setCellValue(actualRevenue.getSubSp().toString());

				Cell cellFinanceCustomerName = row.createCell(10);
			   //cellFinanceCustomerName.setCellValue(actualRevenue.getFinanceCustomerName().toString());
				cellFinanceCustomerName.setCellValue(actualRevenue
						.getRevenueCustomerMappingT().getFinanceCustomerName().toString());
						

				Cell cellFinanceIou = row.createCell(11);
				cellFinanceIou.setCellValue(actualRevenue
						.getRevenueCustomerMappingT().getFinanceIou().toString());

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

	public void setDataProcessingService(
			DataProcessingService dataProcessingService) {
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
