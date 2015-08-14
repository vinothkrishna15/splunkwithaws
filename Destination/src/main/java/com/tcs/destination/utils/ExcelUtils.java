package com.tcs.destination.utils;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFBorderFormatting;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

	public static CellStyle createRowStyle(XSSFWorkbook workbook,
			String headerType) {

		CellStyle cellStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		switch (headerType) {
		case ReportConstants.HEADER:
			font.setColor((short) 0x2bc);
			font.setBoldweight((short) 12);
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 12);
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderTop(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderLeft(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderRight(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyle.setFillPattern(XSSFCellStyle.LEAST_DOTS);
			cellStyle.setFillBackgroundColor((short) 24);
			break;
		case ReportConstants.SUBHEADER:
			font.setColor(IndexedColors.BLACK.getIndex());
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 11);
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderTop(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderLeft(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderRight(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyle.setFillPattern(XSSFCellStyle.LEAST_DOTS);
			cellStyle.setFillBackgroundColor((short) 24);
			break;
		case ReportConstants.HEADER1:
			font.setColor((short) 1);
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 11);
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderTop(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderLeft(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderRight(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyle.setFillPattern(XSSFCellStyle.DIAMONDS);
			cellStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.getIndex());
			break;
		case ReportConstants.HEADINGSTYLE:
			font.setColor(IndexedColors.BLACK.getIndex());
			font.setFontName("Arial");
			font.setBoldweight((short) 12);
			font.setFontHeightInPoints((short) 12);
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderTop(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderLeft(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderRight(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyle.setFillPattern(XSSFCellStyle.LEAST_DOTS);
			cellStyle.setFillBackgroundColor(IndexedColors.TAN.getIndex());
			break;
		case ReportConstants.SUBHEADINGSTYLE:
			font.setColor(IndexedColors.RED.getIndex());
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 11);
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyle.setFont(font);
			break;
		case ReportConstants.SUBHEADINGSTYLE2:
			font.setColor(IndexedColors.BLACK.getIndex());
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 11);
			cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
			cellStyle.setFillPattern(XSSFCellStyle.FINE_DOTS);
			cellStyle.setFillBackgroundColor(IndexedColors.LAVENDER.getIndex());
			cellStyle.setFont(font);
			break;
		case ReportConstants.SUBHEADINGSTYLE3:
			font.setColor(IndexedColors.BLACK.getIndex());
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 11);
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyle.setFillPattern(XSSFCellStyle.FINE_DOTS);
			cellStyle.setFillBackgroundColor(IndexedColors.BLUE_GREY.getIndex());
			cellStyle.setFont(font);
			break;
		case ReportConstants.DATAROW:
			font.setColor((short) 0x2bc);
			font.setFontName("Arial");
			font.setBoldweight((short) 12);
			font.setFontHeightInPoints((short) 10);
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderTop(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderLeft(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderRight(XSSFBorderFormatting.BORDER_THIN);
			break;
		case "Border":
			font.setColor((short) 0x2bc);
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 10);
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderTop(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderLeft(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderRight(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
			break;
		case ReportConstants.HEADINGROW:
			font.setColor((short) 0x2bc);
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 11);
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderTop(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderLeft(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderRight(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			break;
		case "DATA":
			font.setColor((short) 0x2bc);
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 10);
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderTop(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderLeft(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderRight(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			break;
		case ReportConstants.BOTTOMROW:
			font.setColor((short) 0x2bc);
			font.setBoldweight((short) 10);
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 11);
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderTop(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderLeft(XSSFBorderFormatting.BORDER_THIN);
			cellStyle.setBorderRight(XSSFBorderFormatting.BORDER_THIN);
//			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyle.setFillPattern(XSSFCellStyle.LEAST_DOTS);
			cellStyle.setFillBackgroundColor((short) 24);
			break;
		}
		return cellStyle;

	}
}