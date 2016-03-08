package com.tcs.destination.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ConnectSummaryResponse;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.ReportConstants;

/**
 * This service deals with connect summary reports related requests
 */
@Component
public class ConnectSummaryReportService {

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	UserRepository userRepository;

	private static final Logger logger = LoggerFactory.getLogger(ConnectSummaryReportService.class);

	/**
	 * This Method is used to set the connect summary details to sheet
	 * 
	 * @param subSpCustomerConnectCountList
	 * @param subSpPartnerConnectCountList
	 * @param geographyCustomerConnectCountList
	 * @param geographyPartnerConnectCountList
	 * @param iouConnectCountList
	 * @param month
	 * @param quarter
	 * @param year
	 * @param workbook
	 * @param connectCategory 
	 * @throws Exception
	 */
	public void getConnectSummaryExcelReport(List<Object[]> subSpCustomerConnectCountList,List<Object[]> subSpPartnerConnectCountList,
			List<Object[]> geographyCustomerConnectCountList,  List<Object[]> geographyPartnerConnectCountList,
			List<Object[]> iouConnectCountList, String month, String quarter,
			String year, SXSSFWorkbook workbook, String connectCategory) throws Exception {
		
		logger.debug("Begin: Inside getSummaryReport() of ConnectSummaryReportService");
		
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook.createSheet("Summary Report");
		
		SXSSFRow row = null;
		
		int currentRow = 0;
		
		int colValue = 0;
		
		CellStyle cellStyle = ExcelUtils.createRowStyle(workbook, ReportConstants.HEADINGSTYLE);
		
		CellStyle cellStyle1 = ExcelUtils.createRowStyle(workbook, ReportConstants.SUBHEADINGSTYLE2);
		
		row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
		
		if (!month.isEmpty()) {
			currentRow = connectSummaryReport(subSpCustomerConnectCountList,subSpPartnerConnectCountList,
					geographyCustomerConnectCountList, geographyPartnerConnectCountList,iouConnectCountList, month,
					spreadSheet, row, currentRow, colValue, cellStyle, cellStyle1,connectCategory);
		
		} else if (!quarter.isEmpty()) {
			
			currentRow = connectSummaryReport(subSpCustomerConnectCountList,subSpPartnerConnectCountList,
					geographyCustomerConnectCountList, geographyPartnerConnectCountList,iouConnectCountList, quarter,
					spreadSheet, row, currentRow, colValue, cellStyle, cellStyle1,connectCategory);
		} else {
			
			if (year.isEmpty()) {
				year=DateUtils.getCurrentFinancialYear();
			}
			
			currentRow = connectSummaryReport(subSpCustomerConnectCountList,subSpPartnerConnectCountList,
					geographyCustomerConnectCountList, geographyPartnerConnectCountList, iouConnectCountList, year,
					spreadSheet, row, currentRow, colValue, cellStyle, cellStyle1,connectCategory);
		}
		logger.debug("End: Inside getSummaryReport() of ConnectSummaryReportService");
	}

	/**
	 * This method is used to set customer or partner or both connects summary details to excel sheet
	 * 
	 * @param subSpCustomerConnectCountList
	 * @param subSpPartnerConnectCountList
	 * @param geographyCustomerConnectCountList
	 * @param geographyPartnerConnectCountList
	 * @param iouConnectCountList
	 * @param date
	 * @param spreadSheet
	 * @param row
	 * @param currentRow
	 * @param colValue
	 * @param cellStyle
	 * @param cellStyle1
	 * @param connectCategory
	 * @return
	 */
	public int connectSummaryReport(List<Object[]> subSpCustomerConnectCountList, List<Object[]> subSpPartnerConnectCountList,
			List<Object[]> geographyCustomerConnectCountList, List<Object[]> geographyPartnerConnectCountList, 
			List<Object[]> iouConnectCountList, String date, SXSSFSheet spreadSheet, SXSSFRow row, int currentRow, 
			int colValue, CellStyle cellStyle, CellStyle cellStyle1, String connectCategory) {
		
		logger.debug("Begin: Inside connectSummaryReport() of ConnectSummaryReportService");
		
		SXSSFCell cell1 = (SXSSFCell) row.createCell(colValue);
		cell1.setCellValue(date);
		cell1.setCellStyle(cellStyle);
		
		spreadSheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, colValue, colValue + 1));
		currentRow++;
		
		currentRow = connectSummaryReport(subSpCustomerConnectCountList, subSpPartnerConnectCountList, spreadSheet, currentRow, colValue, ReportConstants.SERVICELINESPLIT,cellStyle,cellStyle1,connectCategory);
		currentRow++;
		
		currentRow = connectSummaryReport(geographyCustomerConnectCountList, geographyPartnerConnectCountList, spreadSheet, currentRow, colValue, ReportConstants.GEOSPLIT,cellStyle,cellStyle1,connectCategory);
		currentRow++;
		
		if(!connectCategory.equals("PARTNER")){
			currentRow = connectSummaryReportByIou(iouConnectCountList, spreadSheet, currentRow, colValue, ReportConstants.IOUSPLIT,cellStyle,cellStyle1,connectCategory);
			currentRow++;
		}
		
		logger.debug("End:: Inside connectSummaryReport() of ConnectSummaryReportService");
		return currentRow;
	}

	/**
	 * This method is used to set the customer or partner or both connects summary details to the corresponding rows and columns
	 *  
	 * @param connectCustomerCountList
	 * @param connectPartnerCountList
	 * @param spreadSheet
	 * @param currentRow
	 * @param colValue
	 * @param subHeader
	 * @param headerStyle
	 * @param subHeaderStyle
	 * @param connectCategory
	 * @return
	 */
	public int connectSummaryReport(List<Object[]> connectCustomerCountList, List<Object[]> connectPartnerCountList, 
			SXSSFSheet spreadSheet, int currentRow, int colValue, String subHeader, CellStyle headerStyle, CellStyle subHeaderStyle, String connectCategory) {

		logger.debug("Begin:: Inside summaryReport() of ConnectSummaryReportService");
		SXSSFRow row;
		int rowNo=0;
		row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
		row.createCell(colValue).setCellValue(subHeader);
		row.getCell(colValue).setCellStyle(headerStyle);
		spreadSheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, colValue, colValue + 1));
		currentRow++;
		
		row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
		
		if(connectCategory.equals("All")){
			List<ConnectSummaryResponse> connectSummaryResponsesList = new ArrayList<ConnectSummaryResponse>();
			
			getCustomerAndPartnerConnectSummary(connectCustomerCountList, connectPartnerCountList,connectSummaryResponsesList);
			
			row.createCell(colValue).setCellValue(ReportConstants.ROWLABEL);
			row.getCell(colValue).setCellStyle(subHeaderStyle);
			row.createCell(colValue+1).setCellValue(ReportConstants.COUNTOFCUSTOMERCONNECTS);
			row.getCell(colValue+1).setCellStyle(subHeaderStyle);
			row.createCell(colValue+2).setCellValue(ReportConstants.COUNTOFPARTNERCONNECTS);
			row.getCell(colValue+2).setCellStyle(subHeaderStyle);
			rowNo = currentRow+1;
			for (ConnectSummaryResponse connectSummaryResponse : connectSummaryResponsesList) {
				row = (SXSSFRow) spreadSheet.createRow((short) rowNo);
					row.createCell(colValue).setCellValue(connectSummaryResponse.getRowLabel());
					row.createCell(colValue+1).setCellValue(connectSummaryResponse.getCustomerConnectCount().intValue());
					row.createCell(colValue+2).setCellValue(connectSummaryResponse.getPartnerConnectCount().intValue());
					rowNo++;
			}
			
		} else if(connectCategory.equals("CUSTOMER")){
			row.createCell(colValue).setCellValue(ReportConstants.ROWLABEL);
			row.getCell(colValue).setCellStyle(subHeaderStyle);
			row.createCell(colValue+1).setCellValue(ReportConstants.COUNTOFCUSTOMERCONNECTS);
			row.getCell(colValue+1).setCellStyle(subHeaderStyle);
			rowNo = currentRow+1;
			for (Object[] object : connectCustomerCountList) {
				row = (SXSSFRow) spreadSheet.createRow((short) rowNo);
				if(object[1]!=null){
					row.createCell(colValue).setCellValue(object[1].toString());
					row.createCell(colValue+1).setCellValue(((BigInteger) object[0]).intValue());
					rowNo++;
				}
			}
			
		} else if(connectPartnerCountList!=null && connectCategory.equals("PARTNER")){
				row = ExcelUtils.getRow(spreadSheet, currentRow);
				row.createCell(colValue).setCellValue(ReportConstants.ROWLABEL);
				row.getCell(colValue).setCellStyle(subHeaderStyle);
				row.createCell(colValue+1).setCellValue(ReportConstants.COUNTOFPARTNERCONNECTS);
				row.getCell(colValue+1).setCellStyle(subHeaderStyle);
				rowNo = currentRow+1;
			
				for (Object[] object : connectPartnerCountList) {
					row = ExcelUtils.getRow(spreadSheet, rowNo);
					if(object[1]!=null){
						row.createCell(colValue).setCellValue(object[1].toString());
						row.createCell(colValue+1).setCellValue(((BigInteger) object[0]).intValue());
						rowNo++;
					}
				}
			}
		
		logger.debug("End:: Inside summaryReport() of ConnectSummaryReportService");
		return rowNo;
	}

	/**
	 * This method is used to set the customer or partner or both connects summary details to the corresponding rows and columns
	 *  
	 * @param connectCustomerCountList
	 * @param connectPartnerCountList
	 * @param spreadSheet
	 * @param currentRow
	 * @param colValue
	 * @param subHeader
	 * @param headerStyle
	 * @param subHeaderStyle
	 * @param connectCategory
	 * @return
	 */
	public int connectSummaryReportByIou(List<Object[]> connectCustomerCountList, SXSSFSheet spreadSheet, int currentRow, int colValue, String subHeader, CellStyle headerStyle, CellStyle subHeaderStyle, String connectCategory) {

		logger.debug("Begin:: Inside summaryReport() of ConnectSummaryReportService");
		SXSSFRow row;
		int rowNo=0;
		row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
		row.createCell(colValue).setCellValue(subHeader);
		row.getCell(colValue).setCellStyle(headerStyle);
		spreadSheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, colValue, colValue + 1));
		currentRow++;
		
		row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
		
			row.createCell(colValue).setCellValue(ReportConstants.ROWLABEL);
			row.getCell(colValue).setCellStyle(subHeaderStyle);
			row.createCell(colValue+1).setCellValue(ReportConstants.COUNTOFCUSTOMERCONNECTS);
			row.getCell(colValue+1).setCellStyle(subHeaderStyle);
			rowNo = currentRow+1;
			for (Object[] object : connectCustomerCountList) {
				row = (SXSSFRow) spreadSheet.createRow((short) rowNo);
				if(object[1]!=null){
					row.createCell(colValue).setCellValue(object[1].toString());
					row.createCell(colValue+1).setCellValue(((BigInteger) object[0]).intValue());
					rowNo++;
				}
			}
		logger.debug("End:: Inside summaryReport() of ConnectSummaryReportService");
		return rowNo;
	}
	
	
	/**
	 * This Method is used to combine customer connects summary and partner connects summary into List
	 * @param connectCustomerCountList
	 * @param connectPartnerCountList
	 * @param connectSummaryResponseList
	 */
	private void getCustomerAndPartnerConnectSummary(List<Object[]> connectCustomerCountList,
			List<Object[]> connectPartnerCountList, List<ConnectSummaryResponse> connectSummaryResponseList) {
			List<String> custRowLabel = new ArrayList<String>();
			BigInteger custPartZeroCount = BigInteger.ZERO;
		 	for (Object[] object : connectCustomerCountList) {
		 		ConnectSummaryResponse connectSummaryResponse = new ConnectSummaryResponse();
				connectSummaryResponse.setRowLabel(object[1].toString());
				custRowLabel.add(object[1].toString());
				connectSummaryResponse.setCustomerConnectCount((BigInteger) object[0]);
				connectSummaryResponse.setPartnerConnectCount(custPartZeroCount);
				connectSummaryResponseList.add(connectSummaryResponse);
				}
		 	
		 	List<ConnectSummaryResponse> partnerConnectSummaryList = new ArrayList<ConnectSummaryResponse>();
		 	for(Object[] partnerObject : connectPartnerCountList){
		 		for(ConnectSummaryResponse connectSummary:connectSummaryResponseList) {
		 			if(connectSummary.getRowLabel().equals((String) partnerObject[1])) {
		 				connectSummary.setPartnerConnectCount((BigInteger) partnerObject[0]);
		 				
		 			}
		 		}
		 		
		 	for(ConnectSummaryResponse connectSummary:connectSummaryResponseList) {
		 		if(!connectSummary.getRowLabel().equals((String) partnerObject[1]) && !custRowLabel.contains((String) partnerObject[1])) {
		 			ConnectSummaryResponse partnerConnectSummaryResponse = new ConnectSummaryResponse();
		 			partnerConnectSummaryResponse.setRowLabel(partnerObject[1].toString());
		 			partnerConnectSummaryResponse.setCustomerConnectCount(custPartZeroCount);
		 			partnerConnectSummaryResponse.setPartnerConnectCount((BigInteger) partnerObject[0]);
		 			partnerConnectSummaryList.add(partnerConnectSummaryResponse);
		 			}
		 		}
		 	}
		 	connectSummaryResponseList.addAll(partnerConnectSummaryList);
		}
}
