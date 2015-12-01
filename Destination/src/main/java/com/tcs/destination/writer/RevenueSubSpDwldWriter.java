/**
 * 
 * RevenueSubSpDwldWriter.java 
 *
 * @author TCS
 * @Version 1.0 - 2015
 * 
 * @Copyright 2015 Tata Consultancy 
 */
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

import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.utils.Constants;

/**
 * This RevenueSubSpDwldWriter class contains the functionality to populate the data sheet for Sub sp
 * 
 */
public class RevenueSubSpDwldWriter implements ItemWriter<SubSpMappingT>,
StepExecutionListener {
	
	private static final Logger logger = LoggerFactory
			.getLogger(RevenueSubSpDwldWriter.class);

	private StepExecution stepExecution;

	private Sheet sheet;

	private Workbook workbook;

	private int rowCount = 1;

	private String filePath;

	private FileInputStream fileInputStream;

	private DataProcessingRequestRepository dataProcessingRequestRepository;

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.StepExecutionListener#beforeStep(org.springframework.batch.core.StepExecution)
	 */
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
	 * @see org.springframework.batch.core.StepExecutionListener#afterStep(org.springframework.batch.core.StepExecution)
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		try {
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

	/* (non-Javadoc)
	 * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
	 */
	@Override
	public void write(List<? extends SubSpMappingT> items) throws Exception {
		logger.info("Inside write method:");
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
					.getSheet(Constants.SUB_SP_MAP_REF);
		}

		if (items != null) {
			for (SubSpMappingT subSp : items) {
				// Create row with rowCount
				Row row = sheet.createRow(rowCount);

				// Create new Cell and set cell value
				
				Cell cellActualSubSp = row.createCell(0);
				cellActualSubSp.setCellValue(subSp.getActualSubSp().trim());
				
				Cell cellSubSp = row.createCell(1);
				cellSubSp.setCellValue(subSp.getSubSp().trim());

				Cell cellDisplaySubSp = row.createCell(2);
				cellDisplaySubSp.setCellValue(subSp.getDisplaySubSp().trim());

				Cell cellSpCode = row.createCell(3);
				String spCode = String.valueOf(subSp.getSpCode());
				cellSpCode.setCellValue(spCode);

				Cell cellActive = row.createCell(4);
				cellActive.setCellValue(subSp.getActive().trim());

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

	public DataProcessingRequestRepository getDataProcessingRequestRepository() {
		return dataProcessingRequestRepository;
	}

	public void setDataProcessingRequestRepository(
			DataProcessingRequestRepository dataProcessingRequestRepository) {
		this.dataProcessingRequestRepository = dataProcessingRequestRepository;
	}
  
}
