package com.tcs.destination.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFBorderFormatting;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion.Static;
import com.tcs.destination.bean.UserT;

public class ExcelUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(DestinationUtils.class);
	
	public static CellStyle createRowStyle(Workbook workbook,
			String headerType) {

		CellStyle cellStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		switch (headerType) {
		case ReportConstants.REPORTHEADER:
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
		case ReportConstants.REPORTHEADER1:
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
		case ReportConstants.REPORTHEADINGSTYLE:
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
			cellStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.getIndex());
			break;
		case ReportConstants.DATAROW:
			font.setColor((short) 0x2bc);
			font.setFontName("Arial");
			font.setBoldweight((short) 12);
			font.setFontHeightInPoints((short) 10);
			cellStyle.setFont(font);
			cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
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
			font.setColor(IndexedColors.BLACK.getIndex());
			font.setBoldweight((short) 11);
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
			
		case ReportConstants.SUBHEADINGSTYLE:
			font.setBold(true);
			font.setColor(IndexedColors.RED.getIndex());
			font.setBoldweight((short) 11);
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 11);
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			break;
		case ReportConstants.SUBHEADINGSTYLE2:
			font.setBold(true);
			font.setColor(IndexedColors.BLACK.getIndex());
			font.setBoldweight((short) 11);
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 11);
			cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
			cellStyle.setFillPattern(XSSFCellStyle.LEAST_DOTS);
			cellStyle.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.getIndex());
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			break;
		case ReportConstants.SUBHEADINGSTYLE3:
			font.setBold(true);
			font.setColor(IndexedColors.BLACK.getIndex());
			font.setBoldweight((short) 11);
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 11);
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyle.setFillPattern(XSSFCellStyle.LEAST_DOTS);
			cellStyle.setFillBackgroundColor(IndexedColors.LAVENDER.getIndex());
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			break;

		case ReportConstants.ROWS:
			font.setColor(IndexedColors.BLACK.getIndex());
			font.setBoldweight((short) 11);
			font.setBold(true);
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 11);
			cellStyle.setFont(font);
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			break;
		
		case ReportConstants.HEADER:
			font.setColor((short) 0x2bc);
			font.setBoldweight((short) 12);
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 12);
			cellStyle.setFont(font);
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyle.setFillPattern(XSSFCellStyle.LESS_DOTS);
			cellStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.getIndex());
			cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			break;
		case ReportConstants.HEADER1:
			font.setColor(IndexedColors.BLACK.getIndex());
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 11);
			cellStyle.setFont(font);
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyle.setFillPattern(XSSFCellStyle.LEAST_DOTS);
			cellStyle.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.getIndex());
			cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			
			break;
		case ReportConstants.HEADINGSTYLE:
			font.setBold(true);
			font.setColor(IndexedColors.BLACK.getIndex());
			font.setBoldweight((short) 12);
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 12);
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyle.setFillPattern(XSSFCellStyle.LEAST_DOTS);
			cellStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.getIndex());
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			break;

		}
		return cellStyle;

	}
	
	public static int getStartingAndEndingYear(String[] year , Boolean isStartingYear) {
		int alteredYear = 0;
		if (isStartingYear) {
			if (Integer.parseInt(year[1]) < 4) {
				alteredYear = Integer.parseInt(year[0]) - 1;
			} else {
				alteredYear = Integer.parseInt(year[0]);
			}
		} else {
			if (Integer.parseInt(year[1]) < 4) {
				alteredYear = Integer.parseInt(year[0]);
			} else {
				alteredYear = Integer.parseInt(year[0]) + 1;
			}
		}
		return alteredYear;
	}
	
	public static void arrangeSheetOrder(XSSFWorkbook workbook) {
		int i = 0;
		if (workbook.getSheet(ReportConstants.TITLE) != null) {
			workbook.setSheetOrder(ReportConstants.TITLE, i);
			i++;
		}
		if (workbook.getSheet(ReportConstants.WINS) != null) {
			workbook.setSheetOrder(ReportConstants.WINS, i);
			i++;
		}
		if (workbook.getSheet(ReportConstants.PIPELINE) != null) {
			workbook.setSheetOrder(ReportConstants.PIPELINE, i);
			i++;
		}
		if (workbook.getSheet(ReportConstants.PROSPECTS) != null) {
			workbook.setSheetOrder(ReportConstants.PROSPECTS, i);
			i++;
		}
		if (workbook.getSheet(ReportConstants.LOSSES) != null) {
			workbook.setSheetOrder(ReportConstants.LOSSES, i);
			i++;
		}
		if (workbook.getSheet(ReportConstants.COMPLETEDATA) != null) {
			workbook.setSheetOrder(ReportConstants.COMPLETEDATA, i);
			i++;
		}

	}
	
	public static XSSFRow getRow(XSSFSheet spreadsheet, int rowNo) {
		XSSFRow row = null;
		if (spreadsheet.getRow(rowNo) == null) {
			row = spreadsheet.createRow(rowNo);
		} else {
			row = spreadsheet.getRow(rowNo);
		}
		return row;
	}
	
	/*
	 * Returns the workbook object for given multipart file(xls / xlsm / xlsx)
	 */
	public static Workbook getWorkBook(MultipartFile file) throws IOException{
		String fileName = file.getOriginalFilename();
		logger.info("Received File : " + fileName);
		String fileExtension = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
		logger.info("Received File's Extension : " + fileName);
		InputStream fileInputStream = file.getInputStream();
        if(fileExtension.equalsIgnoreCase("xls")){
        	return new HSSFWorkbook(fileInputStream);
        } else if(fileExtension.equalsIgnoreCase("xlsx")){
        	return new XSSFWorkbook(fileInputStream);
        } else if(fileExtension.equalsIgnoreCase("xlsm")){
        	return new XSSFWorkbook(fileInputStream);
        } else {
        	return null;
        }
	}
	
	/*
	 * returns if the workbook contains any errors returned by macro check
	 */
	public static boolean isValidWorkbook(Workbook workbook,String sheetName,int rowNum, int colNum) throws Exception{
		int index = workbook.getSheetIndex(sheetName);
		if(index==-1)
			//returning false if the validate sheet does not exist
			return false;
		else{
			Sheet worksheet = workbook.getSheetAt(index);
			Row row = worksheet.getRow(rowNum);
			if(row!=null){
			Cell c = row.getCell(colNum);
			if (c == null || c.getCellType() == Cell.CELL_TYPE_BLANK) {
				return true;
			} else {
				return false;
			}
			}
			else{
			    return true;
			}
		}
		    
	}

	public static void writeUserFilterConditions(XSSFSheet spreadsheet, UserT user, String conditions) {
		XSSFRow row;
		row = spreadsheet.createRow(13);
		row.createCell(4).setCellValue("User");
		row.createCell(5).setCellValue(user.getUserName());
		row = spreadsheet.createRow(14);
		row.createCell(4).setCellValue("Condition(S)");
		row.createCell(5).setCellValue(conditions);
		spreadsheet.autoSizeColumn(4);
		spreadsheet.autoSizeColumn(5);
	}

	public static void writeUserFilterConditions(SXSSFSheet spreadsheet, UserT user, String conditions) {
		SXSSFRow row;
		row = (SXSSFRow) spreadsheet.createRow(13);
		row.createCell(4).setCellValue("User");
		row.createCell(5).setCellValue(user.getUserName());
		row = (SXSSFRow) spreadsheet.createRow(14);
		row.createCell(4).setCellValue("Condition(S)");
		row.createCell(5).setCellValue(conditions);
		spreadsheet.autoSizeColumn(4);
		spreadsheet.autoSizeColumn(5);
	}

	public static void writeDetailsForSearchTypeUserAccessFilter(
			XSSFSheet spreadsheet, String userAccessField,
			List<String> privilegeValueList, UserT user, CellStyle dataRow,
			String previlegeBased) {
		XSSFRow row = null;
		writeUserFilterConditions(spreadsheet, user, previlegeBased);
		row = spreadsheet.createRow(15);
		row.createCell(4).setCellValue(userAccessField);
		String completeList = getCompleteList(privilegeValueList);
		row.createCell(5).setCellValue(completeList);
	}
	
	public static String getCompleteList(List<String> itemList) {
		if (itemList.size() == 0) {
			return "All";
		} else {
			return itemList.toString().replace("[", "").replace("]", "");
		}
	}

	public static void writeDetailsForSearchType(XSSFSheet spreadsheet,
			String searchType, List<String> searchList, int rowValue, CellStyle dataRow) {
		XSSFRow row = null;
		row = spreadsheet.createRow(rowValue);
		row.createCell(4).setCellValue(searchType);
		spreadsheet.autoSizeColumn(4);
		String completeList = getCompleteList(searchList);
		row.createCell(5).setCellValue(completeList);
		spreadsheet.autoSizeColumn(5);
		
	}
	
	public static void writeDetailsForSearchType(SXSSFSheet spreadsheet,
			String searchType, List<String> searchList, int rowValue, CellStyle dataRow) {
		SXSSFRow row = null;
		row = (SXSSFRow) spreadsheet.createRow(rowValue);
		row.createCell(4).setCellValue(searchType);
		spreadsheet.autoSizeColumn(4);
		String completeList = getCompleteList(searchList);
		row.createCell(5).setCellValue(completeList);
		spreadsheet.autoSizeColumn(5);
		
	}

	public static void writeDetailsForSearchTypeUserAccessFilter(
			SXSSFSheet spreadsheet, String userAccessField,
			List<String> privilegeValueList, UserT user, CellStyle dataRow,
			String previlegeBased) {
		SXSSFRow row = null;
		writeUserFilterConditions(spreadsheet, user, previlegeBased);
		row = (SXSSFRow) spreadsheet.createRow(15);
		row.createCell(4).setCellValue(userAccessField);
		String completeList = getCompleteList(privilegeValueList);
		row.createCell(5).setCellValue(completeList);
	}
	}