package com.tcs.destination.writer;

import static com.tcs.destination.utils.Constants.REQUEST;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import com.tcs.destination.utils.Constants;
import com.tcs.destination.bean.BidRequestTypeMappingT;
import com.tcs.destination.bean.DealTypeMappingT;
import com.tcs.destination.bean.SalesStageMappingT;

public class BidAndDealCompositeWriter implements ItemWriter<List<Object>>,
		StepExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(BidAndDealCompositeWriter.class);

	private StepExecution stepExecution;

	private Sheet sheet;

	private Workbook workbook;

	private int rowCount = 1;

	private String filePath;

	private FileInputStream fileInputStream;

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
	public void write(List<? extends List<Object>> items) throws Exception {
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
					.getSheet(Constants.OPPORTUNITY_TEMPLATE_BID_REQUEST_DEAL_TYPE_SHEET_NAME);
		}
		// List<BidRequestTypeMappingT> bids = new
		// ArrayList<BidRequestTypeMappingT>();
		// List<DealTypeMappingT> deals = new ArrayList<DealTypeMappingT>();
		for (List<Object> item : items) {

			BidRequestTypeMappingT bidRequest = (BidRequestTypeMappingT) item
					.get(0);
			DealTypeMappingT dealTypeMappingT = (DealTypeMappingT) item
					.get(1);
			SalesStageMappingT salesStageMappingT = (SalesStageMappingT) item.get(2);
			if (bidRequest != null || dealTypeMappingT != null || salesStageMappingT!=null) {
				Row row = sheet.createRow(rowCount);
				if (bidRequest != null) {
					Cell cellBid = row.createCell(0);
					cellBid.setCellValue(bidRequest.getBidRequestType()
							.trim());
				}
				if (dealTypeMappingT != null) {
					Cell cellDeal = row.createCell(2);
					cellDeal.setCellValue(dealTypeMappingT.getDealType()
							.trim());
				}
				if(salesStageMappingT!=null) {
					Cell cellSalesStageCode = row.createCell(4);
					cellSalesStageCode.setCellValue(salesStageMappingT.getSalesStageCode().toString());
					
					Cell cellSalesStageDescription = row.createCell(5);
					cellSalesStageDescription.setCellValue(salesStageMappingT.getSalesStageDescription());
				}
				rowCount++;
			}

			// bids.add(bidRequest);
			// deals.add(dealTypeMappingT);
		}
	}

	// int sizeOfBids = 0;
	// int sizeOfDeals = 0;
	// if(bids!=null) {
	// sizeOfBids = bids.size();
	// }
	// if(deals!=null) {
	// sizeOfDeals = deals.size();
	// }
	//
	// while((rowCount<=sizeOfBids)||(rowCount<=sizeOfDeals)){

	// Row row = sheet.createRow(rowCount);
	//
	// if(rowCount<=sizeOfBids){
	// Cell cellBid = row.createCell(0);
	// cellBid.setCellValue(bids.get(rowCount-1).getBidRequestType().trim());
	// }
	// if(rowCount<=sizeOfDeals){
	// Cell cellDeal = row.createCell(2);
	// cellDeal.setCellValue(deals.get(rowCount-1).getDealType().trim());
	// }
	//
	// rowCount++;

	// }

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
