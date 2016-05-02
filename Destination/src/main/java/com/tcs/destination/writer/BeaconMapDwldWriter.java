/**
 * 
 * BeaconMapDwldWriter.java 
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

import com.tcs.destination.bean.BeaconCustomerMappingT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.utils.Constants;

/**
 * This BeaconMapDwldWriter class contains the functionality to populate 
 * the data sheet for beacon mapping
 *
 */
public class BeaconMapDwldWriter implements ItemWriter<BeaconCustomerMappingT>,
		StepExecutionListener {
	private static final Logger logger = LoggerFactory
			.getLogger(BeaconMapDwldWriter.class);

	private StepExecution stepExecution;
	
	private Sheet sheet;
	
	private Workbook workbook;
	
	private int rowCount = 1;
	
	private String filePath; 
	
	private FileInputStream fileInputStream;

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

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.StepExecutionListener#afterStep(org.springframework.batch.core.StepExecution)
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		logger.debug("Inside after step:");
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

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.StepExecutionListener#beforeStep(org.springframework.batch.core.StepExecution)
	 */
	@Override
	public void beforeStep(StepExecution stepExecution) {
		logger.debug("Inside after step:");
		try {
			this.stepExecution = stepExecution;
		} catch (Exception e) {
			logger.error("Error in before step process: {}", e);
		}
   }

	@Override
	public void write(List<? extends BeaconCustomerMappingT> items) throws Exception {
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
					.getSheet(Constants.BEACON_MAPPING_TEMPLATE_BEACON_SHEET_NAME);
		}
		if(items!=null)
		{
			logger.info("Populating Beacon Mapping Data Sheet");
			for(BeaconCustomerMappingT beaconMappingData: items)
			{


				Row row = sheet.createRow(rowCount);

				// Get Cell and set cell value

				// Master Customer Name
				Cell cellCustomerName = row.createCell(0);
				cellCustomerName.setCellValue(beaconMappingData.getCustomerMasterT().getCustomerName());
				

				// Beacon Customer Name
				Cell cellBeaconCustomerName = row.createCell(1);
				cellBeaconCustomerName.setCellValue(beaconMappingData.getBeaconCustomerName());

				// Beacon IOU
				Cell cellBeaconIOU = row.createCell(2);
				cellBeaconIOU.setCellValue(beaconMappingData.getBeaconIou());

				// Beacon Geography
				Cell cellGeography = row.createCell(3);
				cellGeography.setCellValue(beaconMappingData.getCustomerGeography());

				// Increment row counter
				rowCount++;
			
			}
				
		}
		
	}

}
