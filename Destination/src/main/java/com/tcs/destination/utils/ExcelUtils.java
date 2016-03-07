package com.tcs.destination.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Joiner;
import com.tcs.destination.bean.UserT;

public class ExcelUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(DestinationUtils.class);
	
	public static CellStyle createRowStyle(Workbook workbook, String headerType) {

		CellStyle cellStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		switch (headerType) {
		case ReportConstants.REPORTHEADER:
			font.setColor((short) 0x2bc);
			font.setBoldweight((short) 12);
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 12);
			cellStyle.setFont(font);
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyle.setFillPattern(XSSFCellStyle.FINE_DOTS);
//			cellStyle.setFillBackgroundColor((short) 24);
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
			cellStyle.setFillPattern(XSSFCellStyle.DIAMONDS);
//			cellStyle.setFillBackgroundColor((short) 24);
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
//			cellStyle.setFillBackgroundColor((short) 24);
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
	
	/**
	 * This Method is used to get start date and end date for the given financial year (FY'20XX-XX)
	 * 
	 * @param year
	 * @param isStartingYear
	 * @return
	 */
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
	
	public static void arrangeSheetOrder(SXSSFWorkbook workbook) {
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
	
	/**
	 * This method is used to create the new row or get the row if exist
	 * 
	 * @param spreadsheet
	 * @param rowNo
	 * @return
	 */
	public static SXSSFRow getRow(SXSSFSheet spreadsheet, int rowNo) {
		SXSSFRow row = null;
		if (spreadsheet.getRow(rowNo) == null) {
			row = (SXSSFRow) spreadsheet.createRow(rowNo);
		} else {
			row = (SXSSFRow) spreadsheet.getRow(rowNo);
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

	public static void writeUserFilterConditions(SXSSFSheet spreadsheet, UserT user, String conditions) {
		SXSSFRow row;
		row = (SXSSFRow) spreadsheet.createRow(15);
		row.createCell(4).setCellValue("User");
		row.createCell(5).setCellValue(user.getUserName());
		row = (SXSSFRow) spreadsheet.createRow(16);
		row.createCell(4).setCellValue("Group");
		row.createCell(5).setCellValue(user.getUserGroup());
		row = (SXSSFRow) spreadsheet.createRow(17);
		row.createCell(4).setCellValue("Condition(S)");
		row.createCell(5).setCellValue(conditions);
	}
	
	/**
	 * This Method is used to set the user name, group and report condition to the sheet
	 * 
	 * @param spreadsheet
	 * @param user
	 * @param conditions
	 * @param currentRowNo
	 */
	public static void writeUserFilterConditions(SXSSFSheet spreadsheet, UserT user, String conditions, int currentRowNo, int currentColNo) {
		SXSSFRow row;
		row = (SXSSFRow) spreadsheet.createRow(currentRowNo++);
		row.createCell(currentColNo).setCellValue("User");
		row.createCell(currentColNo+1).setCellValue(user.getUserName());
		row = (SXSSFRow) spreadsheet.createRow(currentRowNo++);
		row.createCell(currentColNo).setCellValue("Group");
		row.createCell(currentColNo+1).setCellValue(user.getUserGroup());
		row = (SXSSFRow) spreadsheet.createRow(currentRowNo++);
		row.createCell(currentColNo).setCellValue("Condition(S)");
		row.createCell(currentColNo+1).setCellValue(conditions);
	}

	/**
	 * This Method is used to set user access filter details to sheet
	 * @param spreadsheet
	 * @param userAccessField
	 * @param privilegeValueList
	 * @param user
	 * @param dataRow
	 * @param previlegeBased
	 * @param currentRowNo
	 * @param currentColNo
	 */
	public static void writeDetailsForSearchTypeUserAccessFilter(SXSSFSheet spreadsheet, String userAccessField,
			List<String> privilegeValueList, UserT user, CellStyle dataRow,	String previlegeBased, int currentRowNo, int currentColNo) {
		SXSSFRow row = null;
		writeUserFilterConditions(spreadsheet, user, previlegeBased, currentRowNo++,currentColNo);
		currentRowNo=currentRowNo+2;
		row = (SXSSFRow) spreadsheet.createRow(currentRowNo++);
		row.createCell(currentColNo).setCellValue(userAccessField);
		String completeList = getCompleteList(privilegeValueList);
		row.createCell(currentColNo+1).setCellValue(completeList);
	}
	
	/**
	 * This method is used to convert List of string elements into single string format separated by comma
	 * 
	 * @param itemList
	 * @return
	 */
	public static String getCompleteList(List<String> itemList) {
		if (itemList.size() == 0) {
			return "All";
		} else {
			return itemList.toString().replace("[", "").replace("]", "");
		}
	}

	/**
	 * This Method is used to set label name and corresponding value to the sheet
	 * 
	 * @param spreadsheet
	 * @param labelName
	 * @param labelValue
	 * @param rowNo
	 * @param colNo
	 */
	public static void writeDetailsForSearchType(SXSSFSheet spreadsheet,
			String labelName, String labelValue, int rowNo, int colNo) {
		SXSSFRow row = null;
		if(labelValue.equals("")){
			labelValue="All";
		}
		row = (SXSSFRow) spreadsheet.createRow(rowNo);
		row.createCell(colNo).setCellValue(labelName);
		row.createCell(colNo+1).setCellValue(labelValue);
		
	}
	
	
	
	public static void writeDetailsForSearchType(SXSSFSheet spreadsheet,
			String searchType, List<String> searchList, int rowValue, CellStyle dataRow) {
		SXSSFRow row = null;
		row = (SXSSFRow) spreadsheet.createRow(rowValue);
		row.createCell(4).setCellValue(searchType);
		String completeList = getCompleteList(searchList);
		row.createCell(5).setCellValue(completeList);
		
	}
	

	public static void writeDetailsForSearchTypeUserAccessFilter(SXSSFSheet spreadsheet, String userAccessField,
			List<String> privilegeValueList, UserT user, CellStyle dataRow,	String previlegeBased) {
		SXSSFRow row = null;
		writeUserFilterConditions(spreadsheet, user, previlegeBased);
		row = (SXSSFRow) spreadsheet.createRow(18);
		row.createCell(4).setCellValue(userAccessField);
		String completeList = getCompleteList(privilegeValueList);
		row.createCell(5).setCellValue(completeList);
	}

	public static String getPeriod(String month, String quarter, String year) {
		String period=null;
		if(month.length()>0 && quarter.length()==0 && year.length()==0){
			period = month;
		}else if(month.length()==0 && quarter.length()>0 && year.length()==0){
			period = quarter;
		}else if(month.length()==0 && quarter.length()==0 && year.length()>0){
			period = year;
		}else{
			period = DateUtils.getCurrentFinancialYear();
		}
		return period;
	}
	
	public static String getPeriod(String fromMonth, String toMonth) {
		String period=null;
		List<String> periodList=new ArrayList<String>();
		periodList.add(fromMonth);
		periodList.add(toMonth);
		period = periodList.toString().replace("[", "").replace("]", "");
		return period;
	}
	
	/*
	 * Returns the workbook object for given file(xls / xlsm / xlsx)
	 */
	public static Workbook getWorkBook(File file) throws IOException{
	    
	    Workbook workbook = null;
	    
	    FileInputStream fileInputStream = new FileInputStream(file);

	    String fileName  = file.getName();
	    String fileExtension = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
            if(fileExtension.equalsIgnoreCase("xls")){
                workbook = new HSSFWorkbook(fileInputStream);
            } else if(fileExtension.equalsIgnoreCase("xlsx")){
        	workbook = new XSSFWorkbook(fileInputStream);
            } else if(fileExtension.equalsIgnoreCase("xlsm")){
        	workbook = new XSSFWorkbook(fileInputStream);
            }
            
            return workbook;
	}
	
	
	/**
	 * This Method used to append the single quotes to List elements
	 * 
	 * @param formattedList
	 * @return
	 */
	public static String getStringListWithSingleQuotes(List<String> formattedList) {
		String appendedString = Joiner.on("\',\'").join(formattedList);
		if (!formattedList.isEmpty()) {
			appendedString = "\'" + appendedString + "\'";
		}
		return appendedString;
	}
	
	/**
	 * This Method used to add ("") to targetList if list contains "All" else adds the itemList to targetList 
	 * @param itemList
	 * @param targetList
	 */
	public static void addItemToList(List<String> itemList, List<String> targetList){
		if(itemList.contains("All") || itemList.isEmpty()){
			targetList.add("");
		} else {
			targetList.addAll(itemList);
		}
	}
	
	public static void createCell(String val, Row row, int colIndex) {
		 Cell cell = row.createCell(colIndex);
		 if(val!=null)
		 cell.setCellValue(val.trim());
	}

}