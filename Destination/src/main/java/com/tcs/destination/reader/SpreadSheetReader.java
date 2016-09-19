package com.tcs.destination.reader;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ReadListener;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.tcs.destination.utils.ExcelUtils;

public class SpreadSheetReader implements ItemReader<String[]>, ReadListener {
	
	private static final Logger logger = LoggerFactory
			.getLogger(SpreadSheetReader.class);
	
	private int rowNo = 0;
	
	private Workbook workbook;
	
	private String filePath;
	
	private String sheetName;
	
	private int rowsToSkip = 0;
	
	private int rowLength = 0;

	@Override
	public String[] read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		
		
		
		String[] returnValue = null;
		
		try {
		
			if (rowNo == 0) {
				workbook = ExcelUtils.getWorkBook(new File(filePath));
				rowLength = workbook.getSheet(sheetName).getRow(0).getLastCellNum();
				rowNo = rowNo + rowsToSkip;
			}
			
			logger.debug("Inside read method - reading record:" + rowNo);
		
		
			Row row = workbook.getSheet(sheetName).getRow(rowNo);
			
			if (row != null) {
				returnValue = new String[rowLength + 1];
				returnValue[0] = Integer.toString(rowNo);
				for (int i = 0; i < row.getLastCellNum(); i++) {
					if (row.getCell(i) != null) {
						Cell cell = row.getCell(i);
						String value;
						switch (cell.getCellType()) {
						
						case Cell.CELL_TYPE_NUMERIC:
							if(!DateUtil.isCellDateFormatted(cell)) {
								value = cell.toString().trim();
							} else {
								Date date = cell.getDateCellValue();
								DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
								value = formatter.format(date);
							}
							break;
						default:
							value = cell.toString().trim();
							break;
						}
						returnValue[i + 1] = value;
					}
				}
				rowNo++;
			} else {
				logger.debug("Reached end of file, row count:{}", rowNo - 1);
			}
			
			
		} catch (Exception e) {
			logger.error("Error occured during read:{}", e);
		}
		
		
		
		return returnValue;
	}

	public int getRowNo() {
		return rowNo;
	}

	public void setRowNo(int rowNo) {
		this.rowNo = rowNo;
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public int getRowsToSkip() {
		return rowsToSkip;
	}

	public void setRowsToSkip(int rowsToSkip) {
		this.rowsToSkip = rowsToSkip;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ReadListener#onDataAvailable()
	 */
	@Override
	public void onDataAvailable() throws IOException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ReadListener#onAllDataRead()
	 */
	@Override
	public void onAllDataRead() throws IOException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ReadListener#onError(java.lang.Throwable)
	 */
	@Override
	public void onError(Throwable throwable) {
		logger.error("Error during read - {}", throwable.getMessage());
		
	}


}
