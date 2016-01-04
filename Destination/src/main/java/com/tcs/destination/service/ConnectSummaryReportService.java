package com.tcs.destination.service;

import java.util.List;

import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFBorderFormatting;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.ReportConstants;

@Component
public class ConnectSummaryReportService {

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	UserRepository userRepository;

	private static final Logger logger = LoggerFactory
			.getLogger(ConnectSummaryReportService.class);

	public void getSummaryReport(List<Object[]> subSpConnectCountList,
			List<Object[]> geographyConnectCountList,
			List<Object[]> iouConnectCountList, String month, String quarter,
			String year, SXSSFWorkbook workbook) throws Exception {
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook.createSheet("Summary Report");
		SXSSFRow row = null;
		int currentRow = 0;
		int colValue = 0;
		CellStyle cellStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.REPORTHEADER);
		CellStyle cellStyle1 = ExcelUtils.createRowStyle(workbook,
				ReportConstants.REPORTHEADER1);
		row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
		
		if (!month.isEmpty()) {
			currentRow = connectSummaryReport(subSpConnectCountList,
					geographyConnectCountList, iouConnectCountList, month,
					spreadSheet, row, currentRow, colValue, cellStyle,
					cellStyle1);
		} else if (!quarter.isEmpty()) {
			currentRow = connectSummaryReport(subSpConnectCountList,
					geographyConnectCountList, iouConnectCountList, quarter,
					spreadSheet, row, currentRow, colValue, cellStyle,
					cellStyle1);
		} else {
			if (year.isEmpty()) {
				year=DateUtils.getCurrentFinancialYear();
			}
			currentRow = connectSummaryReport(subSpConnectCountList,
					geographyConnectCountList, iouConnectCountList, year,
					spreadSheet, row, currentRow, colValue, cellStyle,
					cellStyle1);
		}
	}

	public int connectSummaryReport(List<Object[]> subSpConnectCountList,
			List<Object[]> geographyConnectCountList,
			List<Object[]> iouConnectCountList, String date,
			SXSSFSheet spreadSheet, SXSSFRow row, int currentRow, int colValue,
			CellStyle cellStyle, CellStyle cellStyle1) {
		SXSSFCell cell1 = (SXSSFCell) row.createCell(colValue);
		cell1.setCellValue(date);
		cell1.setCellStyle(cellStyle);
		spreadSheet.addMergedRegion(new CellRangeAddress(currentRow,
				currentRow, colValue, colValue + 1));
		currentRow++;
		currentRow = summaryReport(subSpConnectCountList, spreadSheet,
				currentRow, colValue, cellStyle1,
				ReportConstants.SERVICELINESPLIT);
		currentRow++;
		currentRow = summaryReport(geographyConnectCountList, spreadSheet,
				currentRow, colValue, cellStyle1, ReportConstants.GEOSPLIT);
		currentRow++;
		currentRow = summaryReport(iouConnectCountList, spreadSheet,
				currentRow, colValue, cellStyle1, ReportConstants.IOUSPLIT);
		currentRow++;
		return currentRow;
	}

	public int summaryReport(List<Object[]> connectCountList,
			SXSSFSheet spreadSheet, int currentRow, int colValue,
			CellStyle cellStyle, String subHeader) {
		SXSSFRow row;
		row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
		SXSSFCell cell2 = (SXSSFCell) row.createCell(colValue);
		cell2.setCellValue(subHeader);
		cell2.setCellStyle(cellStyle);
		spreadSheet.addMergedRegion(new CellRangeAddress(currentRow,
				currentRow, colValue, colValue + 1));
		currentRow++;
		CellStyle subHeaderStyle = spreadSheet.getWorkbook().createCellStyle();
		subHeaderStyle.setFillForegroundColor(HSSFColor.GREEN.index);
		subHeaderStyle.setFillPattern(XSSFCellStyle.FINE_DOTS);
		row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
		SXSSFCell subHeadercell = (SXSSFCell) row.createCell(colValue);
		subHeadercell.setCellStyle(subHeaderStyle);
		subHeadercell.setCellValue(ReportConstants.ROWLABEL);
		// row.createCell(colValue).setCellValue(ReportConstants.ROWLABEL);
		spreadSheet.autoSizeColumn(colValue);
		SXSSFCell subHeadercell1 = (SXSSFCell) row.createCell(colValue + 1);
		subHeadercell1.setCellStyle(subHeaderStyle);
		subHeadercell1.setCellValue(ReportConstants.COUNTOFCONNECTIDS);
		// row.createCell(colValue+1).setCellValue(ReportConstants.COUNTOFCONNECTIDS);
		spreadSheet.autoSizeColumn(colValue + 1);
		currentRow++;
		CellStyle dataStyle = spreadSheet.getWorkbook().createCellStyle();
		dataStyle.setFillForegroundColor(HSSFColor.BLUE.index);
		dataStyle.setFillPattern(XSSFCellStyle.LESS_DOTS);
		dataStyle.setBorderBottom(XSSFBorderFormatting.BORDER_THIN);
		dataStyle.setBorderTop(XSSFBorderFormatting.BORDER_THIN);
		dataStyle.setBorderLeft(XSSFBorderFormatting.BORDER_THIN);
		dataStyle.setBorderRight(XSSFBorderFormatting.BORDER_THIN);
		for (Object[] object : connectCountList) {
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
			SXSSFCell cell = (SXSSFCell) row.createCell(colValue);
			SXSSFCell cell1 = (SXSSFCell) row.createCell(colValue + 1);
			cell.setCellStyle(dataStyle);
			cell1.setCellStyle(dataStyle);
			if(object[1]!=null){
			cell.setCellValue(object[1].toString());
			cell1.setCellValue(object[0].toString());
//			}else{
//			cell1.setCellValue(Constants.SPACE);
//			cell.setCellValue(Constants.SPACE);
//			}
			spreadSheet.autoSizeColumn(colValue);
			spreadSheet.autoSizeColumn(colValue + 1);
			currentRow++;
		}
		}
		return currentRow;
	}

}
