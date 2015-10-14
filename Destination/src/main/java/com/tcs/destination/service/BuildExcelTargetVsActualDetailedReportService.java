package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CurrencyValue;
import com.tcs.destination.bean.TargetVsActualDetailed;
import com.tcs.destination.bean.TargetVsActualYearToDate;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.ReportConstants;

@Component
public class BuildExcelTargetVsActualDetailedReportService {
	
	private static final Logger logger = LoggerFactory.getLogger(ReportsService.class);
	
	@Autowired
	UserAccessPrivilegesRepository userAccessPrivilegesRepository;
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	BuildExcelTargetVsActualSummaryReportService buildExcelTargetVsActualSummaryReportService;

	public void getTargetVsActualExcel(List<TargetVsActualDetailed> targetVsActualDetailedList,List<String> fields,List<String> currencyList, String fromMonth, SXSSFWorkbook workbook)
			throws Exception {
		logger.debug("Inside getTargetVsActualExcel() method");
		SXSSFSheet spreadSheet =  (SXSSFSheet) workbook.createSheet("targetVsActual");
		SXSSFRow row = null;
		int currentRow = 0;
		String currentFinancialYear = DateUtils.getCurrentFinancialYear();
		row =  (SXSSFRow) spreadSheet.createRow((short) currentRow);
		SXSSFRow row1 =  (SXSSFRow) spreadSheet.createRow(1);
		createHeaderTargetVsActualReport(
				targetVsActualDetailedList, row, row1, spreadSheet,
				currencyList, currentFinancialYear, fields);
		currentRow = createTargetVsActualReportExcelFormat(
				targetVsActualDetailedList, workbook, spreadSheet, currentRow,
				row, currencyList, fields);
		currentRow++;
	}
	
	/**
	 * This method is used to create Header for the TargetVsActual Report
	 * @param targetVsActualDetailedList  This parameter contains the list of targetVsActual objects
	 * @param row This parameter is used to create the first row in spread sheet
	 * @param row1 This parameter is used to create the second row in spread sheet
	 * @param spreadSheet This parameter is used to create the spread sheet in excel workbook
	 * @param currencyList This parameter contains the list of Currencies 
	 * @param currentFinancialYear
	 * @param fields
	 */
	public void createHeaderTargetVsActualReport(
			List<TargetVsActualDetailed> targetVsActualDetailedList,
			SXSSFRow row, SXSSFRow row1, SXSSFSheet spreadSheet,
			List<String> currencyList, String currentFinancialYear, List<String> fields) {
		int columnValue = 0;
		int revColStartNo = 19;
		CellStyle headerStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(), ReportConstants.REPORTHEADER);
		row.createCell(0).setCellValue(ReportConstants.CUSTOMERNAME);
		row.getCell(0).setCellStyle(headerStyle);
		
		row.createCell(1).setCellValue(ReportConstants.GROUPCUSTOMERNAME);
		row.getCell(1).setCellStyle(headerStyle);
		
		if (fields.contains(ReportConstants.GEOGRAPHY)) {
			row.createCell(2 + columnValue).setCellValue(ReportConstants.GEO);
			row.getCell(2+columnValue).setCellStyle(headerStyle);
			spreadSheet.autoSizeColumn(2 + columnValue);
			columnValue++;
		}
		row.createCell(2 + columnValue).setCellValue(ReportConstants.DISPLAYGEO);
		row.getCell(2+columnValue).setCellStyle(headerStyle);
		spreadSheet.autoSizeColumn(2 + columnValue);
		if (fields.contains(ReportConstants.IOU)) {
			row.createCell(3 + columnValue).setCellValue(ReportConstants.Iou);
			row.getCell(3+columnValue).setCellStyle(headerStyle);
			spreadSheet.autoSizeColumn(3 + columnValue);
			columnValue++;
		}
		row.createCell(3 + columnValue).setCellValue(ReportConstants.DISPLAYIOU);
		row.getCell(3 + columnValue).setCellStyle(headerStyle);
		spreadSheet.autoSizeColumn(3 + columnValue);
		List<String> headerList = new ArrayList<String>();
		headerList.add(ReportConstants.Q1  + currentFinancialYear + ReportConstants.BEACONTARGET);
		headerList.add(ReportConstants.Q2  + currentFinancialYear + ReportConstants.BEACONTARGET);
		headerList.add(ReportConstants.Q3  + currentFinancialYear + ReportConstants.BEACONTARGET);
		headerList.add(ReportConstants.Q4  + currentFinancialYear + ReportConstants.BEACONTARGET);
		if (fields.contains(ReportConstants.YTDBEACONTARGET)) {
			headerList.add(4, ReportConstants.YTDFY + currentFinancialYear + ReportConstants.BEACONTARGET);
		}
		headerList.add(ReportConstants.Q1  + currentFinancialYear + ReportConstants.ACTUAL);
		headerList.add(ReportConstants.Q2  + currentFinancialYear + ReportConstants.ACTUAL);
		headerList.add(ReportConstants.Q3  + currentFinancialYear + ReportConstants.ACTUAL);
		headerList.add(ReportConstants.Q4  + currentFinancialYear + ReportConstants.ACTUAL);
		if (fields.contains(ReportConstants.YTDACTUAL)) {
			headerList.add(9, ReportConstants.YTDFY + currentFinancialYear + ReportConstants.ACTUAL);
		}
		Boolean isTrue = getProjectedValue(targetVsActualDetailedList);
		if (isTrue) {
			headerList.add(ReportConstants.Q1  + currentFinancialYear + ReportConstants.PROJECTED);
			headerList.add(ReportConstants.Q2  + currentFinancialYear + ReportConstants.PROJECTED);
			headerList.add(ReportConstants.Q3  + currentFinancialYear + ReportConstants.PROJECTED);
			headerList.add(ReportConstants.Q4  + currentFinancialYear + ReportConstants.PROJECTED);
			if (fields.contains(ReportConstants.YTDPROJECTED)) {
				headerList.add(14, ReportConstants.YTDFY + currentFinancialYear + ReportConstants.PROJECTED);
			}
		}
		if (isTrue != true) {
			revColStartNo = 15;
		}
		headerList.add(ReportConstants.Q1  + currentFinancialYear + ReportConstants.REVENUE);
		headerList.add(ReportConstants.Q2  + currentFinancialYear + ReportConstants.REVENUE);
		headerList.add(ReportConstants.Q3  + currentFinancialYear + ReportConstants.REVENUE);
		headerList.add(ReportConstants.Q4  + currentFinancialYear + ReportConstants.REVENUE);
		if (fields.contains(ReportConstants.YTDREVENUE)) {
			headerList.add(revColStartNo, ReportConstants.YTDFY + currentFinancialYear + ReportConstants.REVENUE);
		}
		int columnNo = 4 + columnValue;
		for (int i = 0; i < headerList.size(); i++) {
			row.createCell(columnNo).setCellValue(headerList.get(i));
			row.getCell(columnNo).setCellStyle(headerStyle);
			spreadSheet.autoSizeColumn(columnNo);
			spreadSheet.addMergedRegion(new CellRangeAddress(0, 0, columnNo, columnNo
					+ currencyList.size() - 1));
			for (int j = 0; j < currencyList.size(); j++) {
				row1.createCell(columnNo + j).setCellValue(currencyList.get(j));
				row1.getCell(columnNo + j).setCellStyle(headerStyle);
				// spreadSheet.autoSizeColumn(col+j);
			}
			columnNo = columnNo + currencyList.size();
		}
		row.createCell(columnNo)
				.setCellValue(ReportConstants.Q1  + currentFinancialYear + ReportConstants.TARGETACHIEVEDPERCENT);
		row.getCell(columnNo).setCellStyle(headerStyle);
		spreadSheet.autoSizeColumn(columnNo);
		columnNo++;
		row.createCell(columnNo)
				.setCellValue(ReportConstants.Q2  + currentFinancialYear + ReportConstants.TARGETACHIEVEDPERCENT);
		row.getCell(columnNo).setCellStyle(headerStyle);
		spreadSheet.autoSizeColumn(columnNo);
		columnNo++;
		row.createCell(columnNo)
				.setCellValue(ReportConstants.Q3  + currentFinancialYear + ReportConstants.TARGETACHIEVEDPERCENT);
		row.getCell(columnNo).setCellStyle(headerStyle);
		spreadSheet.autoSizeColumn(columnNo);
		columnNo++;
		row.createCell(columnNo)
				.setCellValue(ReportConstants.Q4  + currentFinancialYear + ReportConstants.TARGETACHIEVEDPERCENT);
		row.getCell(columnNo).setCellStyle(headerStyle);
		spreadSheet.autoSizeColumn(columnNo);
		columnNo++;
		if (fields.contains(ReportConstants.YTDTARGETACHIEVED)) {
			row.createCell(columnNo).setCellValue(
					ReportConstants.YTDFY  + currentFinancialYear + ReportConstants.TARGETACHIEVEDPERCENT);
			row.getCell(columnNo).setCellStyle(headerStyle);
			spreadSheet.autoSizeColumn(columnNo);
			columnNo++;
		}
		row.createCell(columnNo).setCellValue(ReportConstants.PERCENTACHIEVEDBRACKET);
		row.getCell(columnNo).setCellStyle(headerStyle);
		spreadSheet.autoSizeColumn(columnNo);
	}

	public Boolean getProjectedValue(List<TargetVsActualDetailed> targetVsActualDetailedList){
		Boolean isProjectedIsEmpty=false;
		BigDecimal projectedValue= new BigDecimal(0); 
		for (TargetVsActualDetailed targetVsActual : targetVsActualDetailedList) {
			for (TargetVsActualYearToDate targetVsActualYearToDate : targetVsActual
					.getYearToDate()) {
				if (targetVsActualYearToDate.getProjected() != null
						&& targetVsActualYearToDate.getProjected()
								.doubleValue() != projectedValue.doubleValue())
					isProjectedIsEmpty = true;
				break;
			}
		}
		return isProjectedIsEmpty;
	}
	
	public int createTargetVsActualReportExcelFormat(
			List<TargetVsActualDetailed> targetVsActualDetailedList,
			SXSSFWorkbook workbook, SXSSFSheet spreadSheet, int currentRow,
		SXSSFRow row, List<String> currencyList, List<String> fields) {
		Boolean isTrue = getProjectedValue(targetVsActualDetailedList);
		for (TargetVsActualDetailed targetVsActual : targetVsActualDetailedList) {
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow + 2);
			getTargetVsActualReportWithOrWithOutFields(spreadSheet, row,
					currencyList, targetVsActualDetailedList, targetVsActual,
					fields,isTrue);
			currentRow++;
		}
		return currentRow;
	}

	public void getTargetVsActualReportWithOrWithOutFields(SXSSFSheet spreadSheet,
			SXSSFRow row, List<String> currencyList,
			List<TargetVsActualDetailed> targetVsActualDetailedList,
			TargetVsActualDetailed targetVsActual, List<String> fields, Boolean isProjectedValuesIsNotEmpty) {
		int columnNo = 0;
		int offset = 0;
		int projectedOffset = 0;
		int targetAchievedOffset=0;
		BigDecimal actualProjectedRevenue=new BigDecimal(0);
		BigDecimal targetRevenue=new BigDecimal(0);
		String percentAchievedBracket=null;
		BigDecimal percentAchieved=new BigDecimal(0);
		double percentAchieve=0;
		boolean geographyFlag=fields.contains(ReportConstants.GEOGRAPHY);
		boolean iouFlag=fields.contains(ReportConstants.IOU);
		boolean yTdBeaconTargetFlag=fields.contains(ReportConstants.YTDBEACONTARGET);
		boolean yTdActualFlag=fields.contains(ReportConstants.YTDACTUAL);
		boolean yTdProjectedFlag=fields.contains(ReportConstants.YTDPROJECTED);
		boolean yTdTargetAchievedFlag=fields.contains(ReportConstants.YTDTARGETACHIEVED);
		boolean yTdRevenueFlag=fields.contains(ReportConstants.YTDREVENUE);
		
		CellStyle rowStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(), ReportConstants.DATAROW);
		
		//setting customer name
		row.createCell(columnNo).setCellValue(targetVsActual.getCustomerMasterT().getCustomerName());
		row.getCell(columnNo).setCellStyle(rowStyle);
		spreadSheet.autoSizeColumn(columnNo);
		columnNo++;
		//setting group customer name
		row.createCell(columnNo).setCellValue(targetVsActual.getCustomerMasterT().getGroupCustomerName());
		row.getCell(columnNo).setCellStyle(rowStyle);
		spreadSheet.autoSizeColumn(columnNo);
		columnNo++;
		
		//if geographyFlag is true this will write geography value in excel
		if (geographyFlag) {
			row.createCell(columnNo).setCellValue(targetVsActual.getCustomerMasterT().getGeographyMappingT().getGeography());
			row.getCell(columnNo).setCellStyle(rowStyle);
			spreadSheet.autoSizeColumn(columnNo);
			offset++;
			columnNo = columnNo + offset;
		}
		
		row.createCell(columnNo).setCellValue(targetVsActual.getCustomerMasterT().getGeographyMappingT().getDisplayGeography());
		row.getCell(columnNo).setCellStyle(rowStyle);
		spreadSheet.autoSizeColumn(columnNo);
		columnNo++;
		
		//if iouFlag is true this will write iou value in excel
		if (iouFlag) {
			row.createCell(columnNo).setCellValue(targetVsActual.getCustomerMasterT().getIouCustomerMappingT().getIou());
			row.getCell(columnNo).setCellStyle(rowStyle);
			spreadSheet.autoSizeColumn(columnNo);
			offset++;
			columnNo++;
		}
		row.createCell(columnNo).setCellValue(targetVsActual.getCustomerMasterT().getIouCustomerMappingT().getDisplayIou());
		row.getCell(columnNo).setCellStyle(rowStyle);
		for (TargetVsActualYearToDate targetVsActualYearToDate : targetVsActual.getYearToDate()) {
			columnNo = 4;
			for (CurrencyValue targetCurrency : targetVsActualYearToDate.getTargetValues()) {
				if (!targetVsActualYearToDate.getTargetValues().isEmpty()) {
					targetRevenue=targetCurrency.getValue();
				}
			}
			for (CurrencyValue revenueCurrency : targetVsActualYearToDate.getRevenueValues()) {
			if (!targetVsActualYearToDate.getRevenueValues().isEmpty()) {
					actualProjectedRevenue=revenueCurrency.getValue();
				}
			}
			
			//Write the Target Values Into Excel for The Respective Quarter
			setZerosToExcell(targetVsActualYearToDate, columnNo, offset, row,
					currencyList,rowStyle);
			for (int i = 0; i < targetVsActualYearToDate.getQuarterList().size(); i++) {
				String quarter = targetVsActualYearToDate.getQuarterList().get(i).getQuarter();
				switch (quarter.substring(0, 2)) {
				case ReportConstants.Q1:
					columnNo = 4 + offset;
					columnNo = writeTargetValuesIntoExcel(spreadSheet, row, columnNo, targetVsActualYearToDate, i);
					break;
				case ReportConstants.Q2:
					if (currencyList.size() > 1) {
						columnNo = 6 + offset;
					} else {
						columnNo = 5 + offset;
					}
					columnNo = writeTargetValuesIntoExcel(spreadSheet, row,	columnNo, targetVsActualYearToDate, i);
					break;
				case ReportConstants.Q3:
					if (currencyList.size() > 1) {
						columnNo = 8 + offset;
					} else {
						columnNo = 6 + offset;
					}
					columnNo = writeTargetValuesIntoExcel(spreadSheet, row, columnNo, targetVsActualYearToDate, i);
					break;
				case ReportConstants.Q4:
					if (currencyList.size() > 1) {
						columnNo = 10 + offset;
					} else {
						columnNo = 7 + offset;
					}
					columnNo = writeTargetValuesIntoExcel(spreadSheet, row, columnNo, targetVsActualYearToDate, i);
					break;
				}
			}
			
			//If fields contains beaconTarget , Write the targetValues for the financial year into Excel
			if (yTdBeaconTargetFlag) {
				if (currencyList.size() > 1) {
					columnNo = 12 + offset;
				} else {
					columnNo = 8 + offset;
				}
				int targetOffset = 0;
				for (CurrencyValue currency : targetVsActualYearToDate.getTargetValues()) {
					if (currency.getValue() != null) {
						row.createCell(columnNo + targetOffset).setCellValue(currency.getValue().doubleValue());
						row.getCell(columnNo + targetOffset).setCellStyle(rowStyle);
						targetOffset++;
						offset++;
					}
				}
			}
			
			//Write the ActualRevenue Values Into Excel for The Respective Quarter
			if (currencyList.size() > 1) {
				columnNo = 12;
			} else {
				columnNo = 8;
			}
			setZerosToExcell(targetVsActualYearToDate, columnNo, offset, row, currencyList,rowStyle);
			for (int i = 0; i < targetVsActualYearToDate.getQuarterList().size(); i++) {
				String quarter = targetVsActualYearToDate.getQuarterList()
						.get(i).getQuarter();
				switch (quarter.substring(0, 2)) {
				case ReportConstants.Q1:
					if (currencyList.size() > 1) {
						columnNo = 12 + offset;
					} else {
						columnNo = 8 + offset;
					}
					columnNo = writeActualValuesIntoExcel(spreadSheet, row, columnNo, targetVsActualYearToDate, i);
					break;
				case ReportConstants.Q2:
					if (currencyList.size() > 1) {
						columnNo = 14 + offset;
					} else {
						columnNo = 9 + offset;
					}
					columnNo = writeActualValuesIntoExcel(spreadSheet, row,
							columnNo, targetVsActualYearToDate, i);
					break;
				case ReportConstants.Q3:
					if (currencyList.size() > 1) {
						columnNo = 16 + offset;
					} else {
						columnNo = 10 + offset;
					}
					columnNo = writeActualValuesIntoExcel(spreadSheet, row,
							columnNo, targetVsActualYearToDate, i);
					break;
				case ReportConstants.Q4:
					if (currencyList.size() > 1) {
						columnNo = 18 + offset;
					} else {
						columnNo = 11 + offset;
					}
					columnNo = writeActualValuesIntoExcel(spreadSheet, row,
							columnNo, targetVsActualYearToDate, i);
					break;
				}
			}

			//If fields contains Actual , Write the actualRevenue for the financial year into Excel
			if (yTdActualFlag) {
				if (currencyList.size() > 1) {
					columnNo = 20 + offset;
				} else {
					columnNo = 12 + offset;
				}
				int actualOffset = 0;
				for (CurrencyValue currency : targetVsActualYearToDate.getActualValues()) {
					if (currency.getValue() != null) {
						row.createCell(columnNo + actualOffset).setCellValue(currency.getValue().doubleValue());
						row.getCell(columnNo + actualOffset).setCellStyle(rowStyle);
						actualOffset++;
						offset++;
					}
				}
			}

			//Write the Projected Values Into Excel for The Respective Quarter
			if (isProjectedValuesIsNotEmpty) {
				if (currencyList.size() > 1) {
					columnNo = 20;
				} else {
					columnNo = 12;
				}
				setZerosToExcell(targetVsActualYearToDate, columnNo, offset,
						row, currencyList,rowStyle);
				for (int i = 0; i < targetVsActualYearToDate.getQuarterList().size(); i++) {
					String quarter = targetVsActualYearToDate.getQuarterList().get(i).getQuarter();
					switch (quarter.substring(0, 2)) {
					case ReportConstants.Q1:
						if (currencyList.size() > 1) {
							columnNo = 20 + offset;
						} else {
							columnNo = 12 + offset;
						}
						columnNo = writeProjectedValuesIntoExcel(spreadSheet, row, columnNo, targetVsActualYearToDate, i);
						break;
					case ReportConstants.Q2:
						if (currencyList.size() > 1) {
							columnNo = 22 + offset;
						} else {
							columnNo = 13 + offset;
						}
						columnNo = writeProjectedValuesIntoExcel(spreadSheet, row, columnNo, targetVsActualYearToDate, i);
						break;
					case ReportConstants.Q3:
						if (currencyList.size() > 1) {
							columnNo = 24 + offset;
						} else {
							columnNo = 14 + offset;
						}
						columnNo = writeProjectedValuesIntoExcel(spreadSheet, row, columnNo, targetVsActualYearToDate, i);
						break;
					case ReportConstants.Q4:
						if (currencyList.size() > 1) {
							columnNo = 26 + offset;
						} else {
							columnNo = 15 + offset;
						}
						columnNo = writeProjectedValuesIntoExcel(spreadSheet, row, columnNo, targetVsActualYearToDate, i);
						break;
					}
				}
					if (currencyList.size() > 1) {
						projectedOffset = projectedOffset + 8;
					} else {
						projectedOffset = projectedOffset + 4;
					}

			//If fields contains Projected, Write the ProjectedRevenue for the financial year into Excel
//			if (isProjectedValuesIsNotEmpty) {
				if (yTdProjectedFlag) {
					if (currencyList.size() > 1) {
						columnNo = 20 + offset + projectedOffset;
					} else {
						columnNo = 12 + offset + projectedOffset;
					}
					int projectOffset = 0;
					for (CurrencyValue currency : targetVsActualYearToDate.getProjectedValues()) {
						if (currency.getValue() != null) {
							row.createCell(columnNo + projectOffset).setCellValue(currency.getValue().doubleValue());
							row.getCell(columnNo + projectOffset).setCellStyle(rowStyle);
							projectOffset++;
							offset++;
						}
					}
				}
			}
		
			//Write the Revenue Values Into Excel for The Respective Quarter
			if (currencyList.size() > 1) {
				columnNo = 20 + projectedOffset;
			} else {
				columnNo = 12 + projectedOffset;
			}
			setZerosToExcell(targetVsActualYearToDate, columnNo, offset, row,
					currencyList,rowStyle);
			for (int i = 0; i < targetVsActualYearToDate.getQuarterList()
					.size(); i++) {
				String quarter = targetVsActualYearToDate.getQuarterList()
						.get(i).getQuarter();
				switch (quarter.substring(0, 2)) {
				case ReportConstants.Q1:
					if (currencyList.size() > 1) {
						columnNo = 20 + offset + projectedOffset;
					} else {
						columnNo = 12 + offset + projectedOffset;
					}
					columnNo = writeRevenueValuesIntoExcel(spreadSheet, row,
							columnNo, targetVsActualYearToDate, i);
					break;
				case ReportConstants.Q2:
					if (currencyList.size() > 1) {
						columnNo = 22 + offset + projectedOffset;
					} else {
						columnNo = 13 + offset + projectedOffset;
					}
					columnNo = writeRevenueValuesIntoExcel(spreadSheet, row,
							columnNo, targetVsActualYearToDate, i);
					break;
				case ReportConstants.Q3:
					if (currencyList.size() > 1) {
						columnNo = 24 + offset + projectedOffset;
					} else {
						columnNo = 14 + offset + projectedOffset;
					}
					columnNo = writeRevenueValuesIntoExcel(spreadSheet, row,
							columnNo, targetVsActualYearToDate, i);
					break;
				case ReportConstants.Q4:
					if (currencyList.size() > 1) {
						columnNo = 26 + offset + projectedOffset;
					} else {
						columnNo = 15 + offset + projectedOffset;
					}
					columnNo = writeRevenueValuesIntoExcel(spreadSheet, row,
							columnNo, targetVsActualYearToDate, i);
					break;
				}
			}
				 
			//If fields contains Revenue, Write the Revenue for the financial year into Excel
			if (yTdRevenueFlag) {
				if (currencyList.size() > 1) {
					columnNo = 28 + offset + projectedOffset;
				} else {
					columnNo = 16 + offset + projectedOffset;
				}
				int revenueOffset = 0;
				for (CurrencyValue currency : targetVsActualYearToDate.getProjectedValues()) {
					if (currency.getValue() != null) {
						row.createCell(columnNo + revenueOffset).setCellValue(currency.getValue().doubleValue());
						row.getCell(columnNo + revenueOffset).setCellStyle(rowStyle);
						revenueOffset++;
						offset++;
					}
				}
			}
			
			//Write the Target Achieved Values Into Excel for The Respective Quarter
			if (currencyList.size() > 1) {
				columnNo = 28 + projectedOffset;
			} else {
				columnNo = 16 + projectedOffset;
			}
			setZerosTargetAchievedToExcell(targetVsActualYearToDate, columnNo,
					offset, row, currencyList,rowStyle);
			for (int i = 0; i < targetVsActualYearToDate.getQuarterList()
					.size(); i++) {
				String quarter = targetVsActualYearToDate.getQuarterList()
						.get(i).getQuarter();
				switch (quarter.substring(0, 2)) {
				case ReportConstants.Q1:
					if (currencyList.size() > 1) {
						columnNo = 28 + offset + projectedOffset;
					} else {
						columnNo = 16 + offset + projectedOffset;
					}
					columnNo = writeTargetAchievedIntoExcel(spreadSheet, row,
							columnNo, targetVsActualYearToDate, i);
					break;
				case ReportConstants.Q2:
					if (currencyList.size() > 1) {
						columnNo = 29 + offset + projectedOffset;
					} else {
						columnNo = 17 + offset + projectedOffset;
					}
					columnNo = writeTargetAchievedIntoExcel(spreadSheet, row,
							columnNo, targetVsActualYearToDate, i);
					break;
				case ReportConstants.Q3:
					if (currencyList.size() > 1) {
						columnNo = 30 + offset + projectedOffset;
					} else {
						columnNo = 18 + offset + projectedOffset;
					}
					columnNo = writeTargetAchievedIntoExcel(spreadSheet, row,
							columnNo, targetVsActualYearToDate, i);
					break;
				case ReportConstants.Q4:
					if (currencyList.size() > 1) {
						columnNo = 31 + offset + projectedOffset;
					} else {
						columnNo = 19 + offset + projectedOffset;
					}
					columnNo = writeTargetAchievedIntoExcel(spreadSheet, row,
							columnNo, targetVsActualYearToDate, i);
					break;
				}
			}
			
			//If fields contains TargetAchieved , Write the targetAchieved for the financial year into Excel
		 	if (yTdTargetAchievedFlag) {
				if (currencyList.size() > 1) {
					columnNo = 32 + offset + projectedOffset;
				} else {
					columnNo = 20 + offset + projectedOffset;
				}
				if (targetVsActualYearToDate.getTargetAchieved() != null) {
					row.createCell(columnNo).setCellValue(targetVsActualYearToDate.getTargetAchieved().doubleValue());
					targetAchievedOffset++;
				}
				row.getCell(columnNo).setCellStyle(rowStyle);
			}
			
			//write percentage Achieved Bracket into Excel
			if (targetRevenue.doubleValue()!=0) {
				percentAchieved = actualProjectedRevenue.divide(targetRevenue, 4,	RoundingMode.HALF_DOWN);
				percentAchieve = percentAchieved.doubleValue() * 100;
			}
			percentAchievedBracket = buildExcelTargetVsActualSummaryReportService.setPercentageAchievedBracket(percentAchievedBracket, percentAchieve);
			if (currencyList.size() > 1) {
				columnNo = 32 + offset + projectedOffset + targetAchievedOffset;
			} else {
				columnNo = 20 + offset + projectedOffset + targetAchievedOffset;
			}
			row.createCell(columnNo).setCellValue(percentAchievedBracket);
			row.getCell(columnNo).setCellStyle(rowStyle);
		}
	}

	private void setZerosTargetAchievedToExcell(
			TargetVsActualYearToDate targetVsActualYearToDate, int columnNo,
			int offset, SXSSFRow row, List<String> currencyList, CellStyle rowStyle) {
		int currentColumn = 0;
		for (int i = 0; i < targetVsActualYearToDate.getQuarterList().size(); i++) {
			String quarter = targetVsActualYearToDate.getQuarterList().get(i)
					.getQuarter();
			if (quarter.contains(ReportConstants.Q1)) {
				currentColumn = columnNo + offset;
				row.createCell(currentColumn).setCellValue(0);
				row.getCell(currentColumn).setCellStyle(rowStyle);
				currentColumn++;
			} else {
				currentColumn = columnNo + offset;
				row.createCell(currentColumn).setCellValue(0);
				row.getCell(currentColumn).setCellStyle(rowStyle);
				currentColumn++;
			}
			if (quarter.contains(ReportConstants.Q2)) {
				currentColumn = columnNo + 1 + offset;
				row.createCell(currentColumn).setCellValue(0);
				row.getCell(currentColumn).setCellStyle(rowStyle);
				currentColumn++;
			} else {
				currentColumn = columnNo + 1 + offset;
				row.createCell(currentColumn).setCellValue(0);
				row.getCell(currentColumn).setCellStyle(rowStyle);
				currentColumn++;
			}
			if (quarter.contains(ReportConstants.Q3)) {
				currentColumn = columnNo + 2 + offset;
				row.createCell(currentColumn).setCellValue(0);
				row.getCell(currentColumn).setCellStyle(rowStyle);
				currentColumn++;
			} else {
				currentColumn = columnNo + 2 + offset;
				row.createCell(currentColumn).setCellValue(0);
				row.getCell(currentColumn).setCellStyle(rowStyle);
				currentColumn++;
			}
			if (quarter.contains(ReportConstants.Q4)) {
				currentColumn = columnNo + 3 + offset;
				row.createCell(currentColumn).setCellValue(0);
				row.getCell(currentColumn).setCellStyle(rowStyle);
				currentColumn++;
			} else {
				currentColumn = columnNo + 3 + offset;
				row.createCell(currentColumn).setCellValue(0);
				row.getCell(currentColumn).setCellStyle(rowStyle);
				currentColumn++;
			}
		}
	}

	private void setZerosToExcell(
			TargetVsActualYearToDate targetVsActualYearToDate, int columnNo,
			int offset, SXSSFRow row, List<String> currencyList, CellStyle rowStyle) {
		int currentColumn = 0;
		for (int i = 0; i < targetVsActualYearToDate.getQuarterList().size(); i++) {
			String quarter = targetVsActualYearToDate.getQuarterList().get(i)
					.getQuarter();
			if (currencyList.size() > 1) {
				for (int j = 0; j < currencyList.size(); j++) {
					if (quarter.contains(ReportConstants.Q1)) {
						currentColumn = columnNo + offset + j;
						row.createCell(currentColumn).setCellValue(0);
						row.getCell(currentColumn).setCellStyle(rowStyle);
						currentColumn++;
					} else {
						currentColumn = columnNo + offset + j;
						row.createCell(currentColumn).setCellValue(0);
						row.getCell(currentColumn).setCellStyle(rowStyle);
						currentColumn++;
					}
					if (quarter.contains(ReportConstants.Q2)) {
						currentColumn = columnNo + 2 + offset + j;
						row.createCell(currentColumn).setCellValue(0);
						row.getCell(currentColumn).setCellStyle(rowStyle);
						currentColumn++;
					} else {
						currentColumn = columnNo + 2 + offset + j;
						row.createCell(currentColumn).setCellValue(0);
						row.getCell(currentColumn).setCellStyle(rowStyle);
						currentColumn++;
					}
					if (quarter.contains(ReportConstants.Q3)) {
						currentColumn = columnNo + 4 + offset + j;
						row.createCell(currentColumn).setCellValue(0);
						row.getCell(currentColumn).setCellStyle(rowStyle);
						currentColumn++;
					} else {
						currentColumn = columnNo + 4 + offset + j;
						row.createCell(currentColumn).setCellValue(0);
						row.getCell(currentColumn).setCellStyle(rowStyle);
						currentColumn++;
					}
					if (quarter.contains(ReportConstants.Q4)) {
						currentColumn = columnNo + 6 + offset + j;
						row.createCell(currentColumn).setCellValue(0);
						row.getCell(currentColumn).setCellStyle(rowStyle);
						currentColumn++;
					} else {
						currentColumn = columnNo + 6 + offset + j;
						row.createCell(currentColumn).setCellValue(0);
						row.getCell(currentColumn).setCellStyle(rowStyle);
						currentColumn++;
					}
				}
			} else {
				if (quarter.contains(ReportConstants.Q1)) {
					currentColumn = columnNo + offset;
					row.createCell(currentColumn).setCellValue(0);
					row.getCell(currentColumn).setCellStyle(rowStyle);
					currentColumn++;
				} else {
					currentColumn = columnNo + offset;
					row.createCell(currentColumn).setCellValue(0);
					row.getCell(currentColumn).setCellStyle(rowStyle);
					currentColumn++;
				}
				if (quarter.contains(ReportConstants.Q2)) {
					currentColumn = columnNo + 1 + offset;
					row.createCell(currentColumn).setCellValue(0);
					row.getCell(currentColumn).setCellStyle(rowStyle);
					currentColumn++;
				} else {
					currentColumn = columnNo + 1 + offset;
					row.createCell(currentColumn).setCellValue(0);
					row.getCell(currentColumn).setCellStyle(rowStyle);
					currentColumn++;
				}
				if (quarter.contains(ReportConstants.Q3)) {
					currentColumn = columnNo + 2 + offset;
					row.createCell(currentColumn).setCellValue(0);
					row.getCell(currentColumn).setCellStyle(rowStyle);
					currentColumn++;
				} else {
					currentColumn = columnNo + 2 + offset;
					row.createCell(currentColumn).setCellValue(0);
					row.getCell(currentColumn).setCellStyle(rowStyle);
					currentColumn++;
				}
				if (quarter.contains(ReportConstants.Q4)) {
					currentColumn = columnNo + 3 + offset;
					row.createCell(currentColumn).setCellValue(0);
					row.getCell(currentColumn).setCellStyle(rowStyle);
					currentColumn++;
				} else {
					currentColumn = columnNo + 3 + offset;
					row.createCell(currentColumn).setCellValue(0);
					row.getCell(currentColumn).setCellStyle(rowStyle);
					currentColumn++;
				}
			}
		}
	}

	private int writeTargetValuesIntoExcel(SXSSFSheet spreadSheet, SXSSFRow row,
			int columnNo, TargetVsActualYearToDate targetVsActualYearToDate,
			int i) {
		CellStyle rowStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(), ReportConstants.DATAROW);
		for (CurrencyValue currencyValues : targetVsActualYearToDate
				.getQuarterList().get(i).getTargetValues()) {
			if (currencyValues.getValue() != null
					&& currencyValues.getValue().doubleValue() != 0) {
				row.createCell(columnNo).setCellValue(
						currencyValues.getValue().doubleValue());
				row.getCell(columnNo).setCellStyle(rowStyle);
				columnNo++;
			} else {
				row.createCell(columnNo).setCellValue(0);
				row.getCell(columnNo).setCellStyle(rowStyle);
				columnNo++;
			}
		}
		return columnNo;
	}

	private int writeActualValuesIntoExcel(SXSSFSheet spreadSheet, SXSSFRow row,
			int columnNo, TargetVsActualYearToDate targetVsActualYearToDate,
			int i) {
		CellStyle rowStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(), ReportConstants.DATAROW);
		for (CurrencyValue currencyValues : targetVsActualYearToDate
				.getQuarterList().get(i).getActualValues()) {
			if (currencyValues.getValue() != null
					&& currencyValues.getValue().doubleValue() != 0) {
				row.createCell(columnNo).setCellValue(
						currencyValues.getValue().doubleValue());
				row.getCell(columnNo).setCellStyle(rowStyle);
				columnNo++;
			} else {
				row.createCell(columnNo).setCellValue(0);
				row.getCell(columnNo).setCellStyle(rowStyle);
				columnNo++;
			}
		}
		return columnNo;
	}

	private int writeProjectedValuesIntoExcel(SXSSFSheet spreadSheet,
			SXSSFRow row, int columnNo,
			TargetVsActualYearToDate targetVsActualYearToDate, int i) {
		CellStyle rowStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(), ReportConstants.DATAROW);
		for (CurrencyValue currencyValues : targetVsActualYearToDate
				.getQuarterList().get(i).getProjectedValues()) {
			if (currencyValues.getValue() != null
					&& currencyValues.getValue().doubleValue() != 0.00) {
				row.createCell(columnNo).setCellValue(
						currencyValues.getValue().doubleValue());
				row.getCell(columnNo).setCellStyle(rowStyle);
				columnNo++;
			} else {
				row.createCell(columnNo).setCellValue(0);
				row.getCell(columnNo).setCellStyle(rowStyle);
				columnNo++;
			}
		}
		return columnNo;
	}

	private int writeRevenueValuesIntoExcel(SXSSFSheet spreadSheet, SXSSFRow row,
			int columnNo, TargetVsActualYearToDate targetVsActualYearToDate,
			int i) {
		CellStyle rowStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(), ReportConstants.DATAROW);
		for (CurrencyValue currencyValues : targetVsActualYearToDate
				.getQuarterList().get(i).getRevenueValues()) {
			if (currencyValues.getValue() != null
					&& currencyValues.getValue().doubleValue() != 0) {
				row.createCell(columnNo).setCellValue(
						currencyValues.getValue().doubleValue());
				row.getCell(columnNo).setCellStyle(rowStyle);
				columnNo++;
			} else {
				row.createCell(columnNo).setCellValue(0);
				row.getCell(columnNo).setCellStyle(rowStyle);
				columnNo++;
			}
		}
		return columnNo;
	}

	private int writeTargetAchievedIntoExcel(SXSSFSheet spreadSheet,
			SXSSFRow row, int columnNo,
			TargetVsActualYearToDate targetVsActualYearToDate, int i) {
		CellStyle rowStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(), ReportConstants.DATAROW);
		if (targetVsActualYearToDate.getQuarterList().get(i)
				.getTargetAchieved() != null
				&& targetVsActualYearToDate.getQuarterList().get(i)
						.getTargetAchieved().doubleValue() != 0) {
			row.createCell(columnNo).setCellValue(
					targetVsActualYearToDate.getQuarterList().get(i)
							.getTargetAchieved().doubleValue());
			row.getCell(columnNo).setCellStyle(rowStyle);
			columnNo++;
		} else {
			row.createCell(columnNo).setCellValue(0);
			row.getCell(columnNo).setCellStyle(rowStyle);
			columnNo++;
		}
		return columnNo;
	}
	
	public void getTargetVsActualTitlePage(SXSSFWorkbook workbook, List<String> geography, List<String> iou, String userId,
			String tillDate, List<String> currency, String fromMonth, String toMonth, String reportType) {
		
		SXSSFSheet spreadsheet =  (SXSSFSheet) workbook.createSheet(ReportConstants.TITLE);
		List<String> privilegeValueList = new ArrayList<String>();
		CellStyle headinStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.REPORTHEADER);
		CellStyle subHeadingStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.DATAROW);
		CellStyle dataRow = ExcelUtils.createRowStyle(workbook,
				ReportConstants.DATAROW);
		SXSSFRow row = null;
		
		row = (SXSSFRow) spreadsheet.createRow(8);
		spreadsheet.addMergedRegion(new CellRangeAddress(8, 8, 4, 10));
		row.createCell(4).setCellValue("Beacon Target Vs Actual report as on " + tillDate);
		spreadsheet.autoSizeColumn(4);
		row.getCell(4).setCellStyle(headinStyle);
		row =  (SXSSFRow) spreadsheet.createRow(9);
		row.createCell(4).setCellValue("User Selection Filter's");
		row.getCell(4).setCellStyle(subHeadingStyle);
		spreadsheet.autoSizeColumn(4);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, ReportConstants.GEO, geography, 10, dataRow);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, ReportConstants.IOU, iou, 11, dataRow);
		row = (SXSSFRow) spreadsheet.createRow(12);
		row.createCell(4).setCellValue("Period");
		String period=ExcelUtils.getPeriod(fromMonth, toMonth);
		row.createCell(5).setCellValue(period);
		
		//
		String userAccessField = null;
		List<UserAccessPrivilegesT> userPrivilegesList = 
				userAccessPrivilegesRepository.findByUserIdAndParentPrivilegeIdIsNullAndIsactive(userId, Constants.Y);
		UserT user = userRepository.findByUserId(userId);
		String userGroup=user.getUserGroupMappingT().getUserGroup();
		row =  (SXSSFRow) spreadsheet.createRow(14);
		row.createCell(4).setCellValue("User Access Filter's");
		row.getCell(4).setCellStyle(subHeadingStyle);
		spreadsheet.autoSizeColumn(4);
		switch (userGroup) {
		case ReportConstants.GEOHEAD:
			userAccessField = Constants.GEOGRAPHY;
			for(UserAccessPrivilegesT accessPrivilegesT:userPrivilegesList){
				String previlageType=accessPrivilegesT.getPrivilegeType();
				String privilageValue=accessPrivilegesT.getPrivilegeValue();
				if(previlageType.equals(Constants.GEOGRAPHY)){
					privilegeValueList.add(privilageValue);
				}
			}
			ExcelUtils.writeDetailsForSearchTypeUserAccessFilter(spreadsheet, userAccessField, privilegeValueList, user, dataRow, ReportConstants.TARVSACTBASEDONPRIVILAGE);
			break;
		case ReportConstants.IOUHEAD:
			userAccessField = Constants.IOU;
			for(UserAccessPrivilegesT accessPrivilegesT:userPrivilegesList){
				String previlageType=accessPrivilegesT.getPrivilegeType();
				String privilageValue=accessPrivilegesT.getPrivilegeValue();
				if(previlageType.equals(Constants.IOU)){
					privilegeValueList.add(privilageValue);
				}
			}
			ExcelUtils.writeDetailsForSearchTypeUserAccessFilter(spreadsheet, userAccessField, privilegeValueList, user, dataRow, ReportConstants.TARVSACTBASEDONPRIVILAGE);
			break;
		default :
			ExcelUtils.writeUserFilterConditions(spreadsheet, user, ReportConstants.FULLACCESS);
		}
		
		row = (SXSSFRow) spreadsheet.createRow(21);
//		spreadsheet.addMergedRegion(new CellRangeAddress(21, 21, 4, 7));
		row.createCell(4).setCellValue("Display Preferences");
		row.getCell(4).setCellStyle(subHeadingStyle);
		row = (SXSSFRow) spreadsheet.createRow(22);
		row.createCell(4).setCellValue("Currency");
		row.createCell(5).setCellValue(currency.toString().replace("[", "").replace("]", ""));
		row = (SXSSFRow) spreadsheet.createRow(23);
		row.createCell(4).setCellValue("Report Type");
		row.createCell(5).setCellValue(reportType);
		
		spreadsheet.addMergedRegion(new CellRangeAddress(25, 25, 4, 7));
		row = (SXSSFRow) spreadsheet.createRow(25);
		row.createCell(4).setCellValue(ReportConstants.REPORTNOTE);
	}
	
}
