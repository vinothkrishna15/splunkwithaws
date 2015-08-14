package com.tcs.destination.service;

import java.util.List;

import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFBorderFormatting;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.data.repository.UserRepository;
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
			String year, XSSFWorkbook workbook) throws Exception {
		XSSFSheet spreadSheet = workbook.createSheet("Summary Report");
		XSSFRow row = null;
		int currentRow = 0;
		int colValue = 0;
		CellStyle cellStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.HEADER);
		CellStyle cellStyle1 = ExcelUtils.createRowStyle(workbook,
				ReportConstants.HEADER1);
		row = spreadSheet.createRow((short) currentRow);
		if (!month.isEmpty()) {
			currentRow = connectSummaryReport(subSpConnectCountList,
					geographyConnectCountList, iouConnectCountList, month,
					spreadSheet, row, currentRow, colValue, cellStyle,
					cellStyle1);
		}
		if (!quarter.isEmpty()) {
			currentRow = connectSummaryReport(subSpConnectCountList,
					geographyConnectCountList, iouConnectCountList, quarter,
					spreadSheet, row, currentRow, colValue, cellStyle,
					cellStyle1);
		}
		if (!year.isEmpty()) {
			currentRow = connectSummaryReport(subSpConnectCountList,
					geographyConnectCountList, iouConnectCountList, year,
					spreadSheet, row, currentRow, colValue, cellStyle,
					cellStyle1);
		}
	}

	@SuppressWarnings("deprecation")
	public int connectSummaryReport(List<Object[]> subSpConnectCountList,
			List<Object[]> geographyConnectCountList,
			List<Object[]> iouConnectCountList, String date,
			XSSFSheet spreadSheet, XSSFRow row, int currentRow, int colValue,
			CellStyle cellStyle, CellStyle cellStyle1) {
		XSSFCell cell1 = row.createCell(colValue);
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

	@SuppressWarnings("deprecation")
	public int summaryReport(List<Object[]> connectCountList,
			XSSFSheet spreadSheet, int currentRow, int colValue,
			CellStyle cellStyle, String subHeader) {
		XSSFRow row;
		row = spreadSheet.createRow((short) currentRow);
		XSSFCell cell2 = row.createCell(colValue);
		cell2.setCellValue(subHeader);
		cell2.setCellStyle(cellStyle);
		spreadSheet.addMergedRegion(new CellRangeAddress(currentRow,
				currentRow, colValue, colValue + 1));
		currentRow++;
		CellStyle subHeaderStyle = spreadSheet.getWorkbook().createCellStyle();
		subHeaderStyle.setFillForegroundColor(HSSFColor.GREEN.index);
		subHeaderStyle.setFillPattern(XSSFCellStyle.FINE_DOTS);
		row = spreadSheet.createRow((short) currentRow);
		XSSFCell subHeadercell = row.createCell(colValue);
		subHeadercell.setCellStyle(subHeaderStyle);
		subHeadercell.setCellValue(ReportConstants.ROWLABEL);
		// row.createCell(colValue).setCellValue(ReportConstants.ROWLABEL);
		spreadSheet.autoSizeColumn(colValue);
		XSSFCell subHeadercell1 = row.createCell(colValue + 1);
		subHeadercell1.setCellStyle(subHeaderStyle);
		subHeadercell1.setCellValue(ReportConstants.COUNTOFCONNECTIDS);
		// row.createCell(colValue+1).setCellValue(ReportConstants.COUNTOFCONNECTIDS);
		spreadSheet.autoSizeColumn(colValue + 1);
		currentRow++;
		for (Object[] object : connectCountList) {
			CellStyle dataStyle = spreadSheet.getWorkbook().createCellStyle();
			dataStyle.setFillForegroundColor(HSSFColor.BLUE.index);
			dataStyle.setFillPattern(XSSFCellStyle.LESS_DOTS);
			dataStyle.setBorderBottom(XSSFBorderFormatting.BORDER_THIN);
			dataStyle.setBorderTop(XSSFBorderFormatting.BORDER_THIN);
			dataStyle.setBorderLeft(XSSFBorderFormatting.BORDER_THIN);
			dataStyle.setBorderRight(XSSFBorderFormatting.BORDER_THIN);
			row = spreadSheet.createRow((short) currentRow);
			XSSFCell cell = row.createCell(colValue);
			cell.setCellStyle(dataStyle);
			cell.setCellValue(object[1].toString());
			// row.createCell(colValue).setCellValue(object[1].toString());
			spreadSheet.autoSizeColumn(colValue);
			XSSFCell cell1 = row.createCell(colValue + 1);
			cell1.setCellStyle(dataStyle);
			// row.createCell(colValue+1).setCellValue(object[0].toString());
			cell1.setCellValue(object[0].toString());
			spreadSheet.autoSizeColumn(colValue + 1);
			currentRow++;
		}
		return currentRow;
	}

}
