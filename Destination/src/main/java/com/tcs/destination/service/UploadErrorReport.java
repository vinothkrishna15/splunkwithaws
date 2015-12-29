package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.ReportConstants;

@Component
public class UploadErrorReport {

	/**
	 * This Method converts the errorDetailsDTOs into Input Stream
	 * @param errorDetailsDTOs
	 * @return input stream
	 * @throws Exception
	 */
	public InputStreamResource getErrorSheet(List<UploadServiceErrorDetailsDTO> errorDetailsDTOs) throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook();
		writeErrorReportIntoExcel(workbook, errorDetailsDTOs);
		ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
		workbook.write(byteOutPutStream);
		byteOutPutStream.flush();
		byteOutPutStream.close();
		byte[] bytes = byteOutPutStream.toByteArray();
		InputStreamResource inputStream = new InputStreamResource(new ByteArrayInputStream(bytes));
		return inputStream;
	}
	
	/**
	 * This Method converts the errorDetailsDTOs into Input Stream
	 * @param errorDetailsDTOs
	 * @return input stream
	 * @throws Exception
	 */
	public XSSFWorkbook writeErrorToWorkbook(List<UploadServiceErrorDetailsDTO> errorDetailsDTOs) throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook();
		writeErrorReportIntoExcel(workbook, errorDetailsDTOs);
		return workbook;
	}
	
	public void writeErrorToWorkbook(List<UploadServiceErrorDetailsDTO> errorDetailsDTOs, Workbook workbook,String sheetName) throws Exception {
		writeErrorReportIntoExcel((XSSFWorkbook)workbook, errorDetailsDTOs,sheetName);
		//return workbook;
	}
	
	public XSSFWorkbook writeErrorToWorkbook(List<UploadServiceErrorDetailsDTO> errorDetailsDTOs,String sheetName) throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook();
		writeErrorReportIntoExcel(workbook, errorDetailsDTOs,sheetName);
		return workbook;
	}

	/**
	 * This method write the Row number and Corresponding Error into the excel sheet 
	 * @param workbook
	 * @param errorDetailsDTOs
	 */
	private void writeErrorReportIntoExcel(XSSFWorkbook workbook,
			List<UploadServiceErrorDetailsDTO> errorDetailsDTOs) {
		XSSFSheet spreadSheet = workbook.createSheet(Constants.UPLOAD_ERRORS);
		CellStyle headerSyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(),ReportConstants.REPORTHEADER);
		CellStyle rowStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(),ReportConstants.DATAROW);
		int currentRow = 0;
		XSSFRow row = null;
		row = spreadSheet.createRow((short) currentRow);
		row.createCell(0).setCellValue(Constants.SHEETNAME);
		row.getCell(0).setCellStyle(headerSyle);
		spreadSheet.autoSizeColumn(0);
		row.createCell(1).setCellValue(Constants.ROWNUMBER);
		row.getCell(1).setCellStyle(headerSyle);
		spreadSheet.autoSizeColumn(1);
		row.createCell(2).setCellValue(Constants.ERROR_MESSAGE);
		row.getCell(2).setCellStyle(headerSyle);
		spreadSheet.autoSizeColumn(2);
		currentRow++;
		if (errorDetailsDTOs != null) {
			for (UploadServiceErrorDetailsDTO upErorDto : errorDetailsDTOs) {
				row = spreadSheet.createRow((short) currentRow);
				row.createCell(0).setCellValue(upErorDto.getSheetName());
				row.getCell(0).setCellStyle(rowStyle);
				spreadSheet.autoSizeColumn(0);
				row.createCell(1).setCellValue(upErorDto.getRowNumber());
				row.getCell(1).setCellStyle(rowStyle);
				spreadSheet.autoSizeColumn(1);
				row.createCell(2).setCellValue(upErorDto.getMessage());
				row.getCell(2).setCellStyle(rowStyle);
				spreadSheet.autoSizeColumn(2);				
				currentRow++;
			}
		}		
	}
	
	private void writeErrorReportIntoExcel(XSSFWorkbook workbook,
			List<UploadServiceErrorDetailsDTO> errorDetailsDTOs,String sheetName) {
		XSSFSheet spreadSheet = workbook.createSheet(sheetName);
		CellStyle headerSyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(),ReportConstants.REPORTHEADER);
		CellStyle rowStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(),ReportConstants.DATAROW);
		int currentRow = 0;
		XSSFRow row = null;
		row = spreadSheet.createRow((short) currentRow);
		row.createCell(0).setCellValue(Constants.SHEETNAME);
		row.getCell(0).setCellStyle(headerSyle);
		spreadSheet.autoSizeColumn(0);
		row.createCell(1).setCellValue(Constants.ROWNUMBER);
		row.getCell(1).setCellStyle(headerSyle);
		spreadSheet.autoSizeColumn(1);
		row.createCell(2).setCellValue(Constants.ERROR_MESSAGE);
		row.getCell(2).setCellStyle(headerSyle);
		spreadSheet.autoSizeColumn(2);
		currentRow++;
		if (errorDetailsDTOs != null) {
			for (UploadServiceErrorDetailsDTO upErorDto : errorDetailsDTOs) {
				row = spreadSheet.createRow((short) currentRow);
				row.createCell(0).setCellValue(upErorDto.getSheetName());
				row.getCell(0).setCellStyle(rowStyle);
				spreadSheet.autoSizeColumn(0);
				row.createCell(1).setCellValue(upErorDto.getRowNumber());
				row.getCell(1).setCellStyle(rowStyle);
				spreadSheet.autoSizeColumn(1);
				row.createCell(2).setCellValue(upErorDto.getMessage());
				row.getCell(2).setCellStyle(rowStyle);
				spreadSheet.autoSizeColumn(2);				
				currentRow++;
			}
		}		
	}
	
}
