package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CustomerRevenueValues;
import com.tcs.destination.bean.GroupCustomerGeoIouResponse;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.data.repository.ActualRevenuesDataTRepository;
import com.tcs.destination.data.repository.BeaconDataTRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.enums.PrivilegeType;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.ReportConstants;

@Component
public class BuildExcelTargetVsActualSummaryReportService {
	
	@Autowired
	BeaconDataTRepository beaconDataTRepository;
	
	@Autowired
	ActualRevenuesDataTRepository actualRevenuesDataTRepository;
	
	@Autowired
	BeaconConverterService beaconConverterService;
	
	@Autowired
	UserAccessPrivilegesRepository userAccessPrivilegesRepository;
	
	@Autowired
	GeographyRepository revenueCustomerRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(ReportsService.class);

	ExcelUtils excelUtils = new ExcelUtils();
	
	public void getTargetVsActualSummaryExcel(
		String fromMonth, BigDecimal totalTargetINR, BigDecimal totalActualINR, BigDecimal top30CustomersRevenueINR,
		List<CustomerRevenueValues> actualRevenuesMap, 	Map<String, BigDecimal> targetRevenuesMap,
		List<String> currencyList, Map<String, BigDecimal> targetOverAllRevenuesMap,
		Map<String, BigDecimal> actualOverAllRevenuesMap, String userId , List<String> formattedMonths, SXSSFWorkbook workbook,
		List<Object[]> overAllRevenuesByGeoMap, List<GroupCustomerGeoIouResponse> geoIouGroupCustName)
		throws Exception {
		logger.debug("Inside getTargetVsActualSummaryExcel() method");
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook.createSheet(ReportConstants.SUMMARY);
		int currentRow=0;
		totalTargetINR = totalTargetINR.setScale(2, BigDecimal.ROUND_HALF_UP);
		totalActualINR = totalActualINR.setScale(2, BigDecimal.ROUND_HALF_UP);
		top30CustomersRevenueINR = top30CustomersRevenueINR.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		//section One
		targetVsActualSummaryReportSectionOne(spreadSheet, totalTargetINR,	totalActualINR, top30CustomersRevenueINR, currencyList);
	
		//Section Two Part 1
		currentRow = getTargetVsActualSummaryReportSectionTwo(targetOverAllRevenuesMap,	actualOverAllRevenuesMap, currencyList, spreadSheet);
		currentRow=currentRow+4;
		//Section Two Part 2
		List<UserAccessPrivilegesT> userPrivilegesList = userAccessPrivilegesRepository.findByUserIdAndIsactive(userId, Constants.Y);
		int firstColumn = 1;
		int secColumn=2;
		for(UserAccessPrivilegesT accessPrivilegesT:userPrivilegesList){
			String privilageType=accessPrivilegesT.getPrivilegeType();
			String privilageValue=accessPrivilegesT.getPrivilegeValue();
			if(privilageType.equals(PrivilegeType.GEOGRAPHY.name())){
				Map<String, BigDecimal> actualProjectedOverAllRevenuesByGeoMap=new TreeMap<String,BigDecimal>();
				for(Object[] overAll:overAllRevenuesByGeoMap){
					if(privilageValue.equals(overAll[2].toString())){
						String customerName = overAll[0].toString();
						BigDecimal revenue = (BigDecimal) (overAll[1]);
						if(actualProjectedOverAllRevenuesByGeoMap!=null){
							actualProjectedOverAllRevenuesByGeoMap.remove(customerName);
						}
						actualProjectedOverAllRevenuesByGeoMap.put(customerName, revenue);
					}
				}
				currentRow = getTargetVsActualSummaryReportSectionTwoByGeo(targetOverAllRevenuesMap,overAllRevenuesByGeoMap,
						actualProjectedOverAllRevenuesByGeoMap, currencyList, spreadSheet,privilageValue,firstColumn,secColumn, 
						currentRow);
				firstColumn=firstColumn+3;
				secColumn=secColumn+3;
			}
		}
		//Section Three
		currentRow++;
		currentRow=getTargetVsActualSummaryReportHeaderSectionThree(spreadSheet, currencyList,currentRow, fromMonth);
		getTargetVsActualSummaryReportSectionThree(actualRevenuesMap, targetRevenuesMap, currencyList, spreadSheet,
				currentRow,geoIouGroupCustName);
		}
	
	private int getTargetVsActualSummaryReportHeaderSectionThree(SXSSFSheet spreadSheet, List<String> currencyList, 
			int currentRow, String fromMonth) {
		int columnValue = 1;
		CellStyle headerStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(),ReportConstants.REPORTHEADER);
		CellStyle subHeaderStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(),ReportConstants.SUBHEADER);
		SXSSFRow section3HeaderRow = (SXSSFRow) spreadSheet.createRow((short) currentRow);
		CellStyle section3HeaderStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(), ReportConstants.REPORTHEADINGSTYLE);
		section3HeaderRow.createCell(columnValue).setCellValue(ReportConstants.TOP30REVENUE);
		spreadSheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 1,10));
		section3HeaderRow.getCell(columnValue).setCellStyle(section3HeaderStyle);
		currentRow=currentRow+2;
		
		SXSSFRow headerRow = (SXSSFRow) spreadSheet.createRow((short) currentRow);
		currentRow++;
		
		SXSSFRow subHeaderRow = (SXSSFRow) spreadSheet.createRow((short) currentRow);
	    	
			headerRow.createCell(columnValue).setCellValue(ReportConstants.CUSTOMERNAME);
			subHeaderRow.createCell(columnValue).setCellStyle(subHeaderStyle);
	    	headerRow.getCell(columnValue).setCellStyle(headerStyle);
			spreadSheet.autoSizeColumn(columnValue);
			columnValue++;
			headerRow.createCell(columnValue).setCellValue(ReportConstants.GROUPCUSTOMERNAME);
			subHeaderRow.createCell(columnValue).setCellStyle(subHeaderStyle);
			headerRow.getCell(columnValue).setCellStyle(headerStyle);
			spreadSheet.autoSizeColumn(columnValue);
			columnValue++;
			headerRow.createCell(columnValue).setCellValue(ReportConstants.DISPLAYGEO);
			subHeaderRow.createCell(columnValue).setCellStyle(subHeaderStyle);
			headerRow.getCell(columnValue).setCellStyle(headerStyle);
			spreadSheet.autoSizeColumn(columnValue);
			columnValue++;
			headerRow.createCell(columnValue).setCellValue(ReportConstants.DISPLAYIOU);
			subHeaderRow.createCell(columnValue).setCellStyle(subHeaderStyle);
			headerRow.getCell(columnValue).setCellStyle(headerStyle);
			spreadSheet.autoSizeColumn(columnValue);
			columnValue++;
			String currentFinYear=DateUtils.getCurrentFinancialYear();
			String sumOfTargetRevenue = ReportConstants.SUMOFTARGETREVENUES+currentFinYear;
			columnValue = createHeaderTargetActualRevenueWithCurriencies(spreadSheet, currencyList, columnValue, currentRow,
					headerStyle,subHeaderStyle, headerRow, subHeaderRow, sumOfTargetRevenue);
			String sumOfAchievedRevenue = ReportConstants.SUMOFACHIEVEDREVENUE+currentFinYear;
			columnValue = createHeaderTargetActualRevenueWithCurriencies(spreadSheet, currencyList, columnValue, currentRow,
					headerStyle,subHeaderStyle, headerRow, subHeaderRow, sumOfAchievedRevenue);
			headerRow.createCell(columnValue).setCellValue(ReportConstants.PERCENTACHIEVED);
			subHeaderRow.createCell(columnValue).setCellStyle(subHeaderStyle);
			headerRow.getCell(columnValue).setCellStyle(headerStyle);
			spreadSheet.autoSizeColumn(columnValue);
			columnValue++;
			headerRow.createCell(columnValue).setCellValue(ReportConstants.PERCENTACHIEVEDBRACKET);
			subHeaderRow.createCell(columnValue).setCellStyle(subHeaderStyle);
			headerRow.getCell(columnValue).setCellStyle(headerStyle);
			spreadSheet.autoSizeColumn(columnValue);
			return currentRow++;
	}

	public int createHeaderTargetActualRevenueWithCurriencies(
			SXSSFSheet spreadSheet, List<String> currencyList, int columnValue,
			int currentRow, CellStyle headerStyle, CellStyle subHeaderStyle, SXSSFRow headerRow,
			SXSSFRow subHeaderRow, String sumOfTargetRevenue) {
		headerRow.createCell(columnValue).setCellValue(sumOfTargetRevenue);
		spreadSheet.autoSizeColumn(columnValue);
		headerRow.getCell(columnValue).setCellStyle(headerStyle);
		spreadSheet.addMergedRegion(new CellRangeAddress(currentRow-1, currentRow-1, columnValue, columnValue+ currencyList.size() - 1));
		if(currencyList.size()>1){
			subHeaderRow.createCell(columnValue).setCellValue(ReportConstants.INR);
			subHeaderRow.getCell(columnValue).setCellStyle(subHeaderStyle);
			subHeaderRow.createCell(columnValue+1).setCellValue(ReportConstants.USD);
			subHeaderRow.getCell(columnValue+1).setCellStyle(subHeaderStyle);
		}else {
			subHeaderRow.createCell(columnValue).setCellValue(currencyList.get(0));
			subHeaderRow.getCell(columnValue).setCellStyle(subHeaderStyle);
		}
		columnValue=columnValue+currencyList.size();
		return columnValue;
	}

	/**
	 * This method is used to create Header for the TargetVsActual summary Report section one
	 * @param spreadSheet This parameter is used to create the spread sheet in excel workbook
	 * @param totalActualProjectedINR 
	 * @param totalTargetINR 
	 * @param top30CustomersRevenueINR 
	 * @param currencyList 
	 * @throws Exception 
	 */
	public void targetVsActualSummaryReportSectionOne(SXSSFSheet spreadSheet, BigDecimal totalTargetINR, 
			BigDecimal totalActualProjectedINR, BigDecimal top30CustomersRevenueINR, List<String> currencyList) throws Exception {
		int currentRow=2;
		int columnNo=1;
		BigDecimal totalTargetUSD=beaconConverterService.convert(ReportConstants.INR, ReportConstants.USD, totalTargetINR);
		BigDecimal totalActualProjectedUSD=beaconConverterService.convert(ReportConstants.INR, ReportConstants.USD, totalActualProjectedINR);
		BigDecimal top30CustomersRevenueUSD=beaconConverterService.convert(ReportConstants.INR, ReportConstants.USD, top30CustomersRevenueINR);
		CellStyle borderStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(), "Border");
		CellStyle headerStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(), ReportConstants.REPORTHEADINGSTYLE);
		SXSSFRow headingRow = (SXSSFRow) spreadSheet.createRow((short) currentRow);
		headingRow.createCell(columnNo).setCellValue(ReportConstants.OVERALLSUMMARY);
		spreadSheet.addMergedRegion(new CellRangeAddress(2, 2, 1,12));
		headingRow.getCell(columnNo).setCellStyle(headerStyle);
		currentRow=currentRow+2;
		SXSSFRow row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
		row.createCell(columnNo).setCellValue(ReportConstants.TOTALTARGET);
		row.getCell(columnNo).setCellStyle(borderStyle);
		columnNo++;
		if(totalTargetINR!=null){
			if(currencyList.size()>1){
		row.createCell(columnNo).setCellValue(totalTargetINR.doubleValue());
		row.getCell(columnNo).setCellStyle(borderStyle);
		columnNo++;
		row.createCell(columnNo).setCellValue(ReportConstants.INR);
		row.getCell(columnNo).setCellStyle(borderStyle);
		columnNo++;
		row.createCell(columnNo).setCellValue(totalTargetUSD.doubleValue());
		row.getCell(columnNo).setCellStyle(borderStyle);
		columnNo++;
		row.createCell(columnNo).setCellValue(ReportConstants.USD);
		row.getCell(columnNo).setCellStyle(borderStyle);
		columnNo++;
			}else{
				if(currencyList.contains(ReportConstants.INR)){
					row.createCell(columnNo).setCellValue(totalTargetINR.doubleValue());
					row.getCell(columnNo).setCellStyle(borderStyle);
					columnNo++;
					row.createCell(columnNo).setCellValue(ReportConstants.INR);
					row.getCell(columnNo).setCellStyle(borderStyle);
					columnNo++;
				}else{
					row.createCell(columnNo).setCellValue(totalTargetUSD.doubleValue());
					row.getCell(columnNo).setCellStyle(borderStyle);
					columnNo++;
					row.createCell(columnNo).setCellValue(ReportConstants.USD);
					row.getCell(columnNo).setCellStyle(borderStyle);
					columnNo++;
				}
			}
		}
		currentRow++;
		SXSSFRow row1 = (SXSSFRow) spreadSheet.createRow((short) currentRow);
		row1.createCell(1).setCellValue(ReportConstants.TOTALACHIEVED);
		row1.getCell(1).setCellStyle(borderStyle);
		if(currencyList.size()>1){
		row1.createCell(2).setCellValue(totalActualProjectedINR.doubleValue());
		row1.getCell(2).setCellStyle(borderStyle);
		row1.createCell(3).setCellValue(ReportConstants.INR);
		row1.getCell(3).setCellStyle(borderStyle);
		row1.createCell(4).setCellValue(totalActualProjectedUSD.doubleValue());
		row1.getCell(4).setCellStyle(borderStyle);
		row1.createCell(5).setCellValue(ReportConstants.USD);
		row1.getCell(5).setCellStyle(borderStyle);
		}else{
			if(currencyList.contains(ReportConstants.INR)){
				row1.createCell(2).setCellValue(totalActualProjectedINR.doubleValue());
				row1.getCell(2).setCellStyle(borderStyle);
				row1.createCell(3).setCellValue(ReportConstants.INR);
				row1.getCell(3).setCellStyle(borderStyle);
			}else{
				row1.createCell(2).setCellValue(totalActualProjectedUSD.doubleValue());
				row1.getCell(2).setCellStyle(borderStyle);
				row1.createCell(3).setCellValue(ReportConstants.USD);
				row1.getCell(3).setCellStyle(borderStyle);
			}
		}
		currentRow++;
		SXSSFRow row2 = (SXSSFRow) spreadSheet.createRow((short) currentRow);
		row2.createCell(1).setCellValue(ReportConstants.PERCENTACHIEVED);
		row2.getCell(1).setCellStyle(borderStyle);
		BigDecimal targetAchieved=totalActualProjectedINR.divide(totalTargetINR, 4, RoundingMode.HALF_DOWN);  
		if(targetAchieved!=null)
		row2.createCell(2).setCellValue(targetAchieved.doubleValue()*100);
		row2.getCell(2).setCellStyle(borderStyle);
		currentRow++;
		row.createCell(7).setCellValue(ReportConstants.TOP30REVENUE);
		row.getCell(7).setCellStyle(borderStyle);
		if(top30CustomersRevenueINR!=null){
		if(currencyList.size()>1){
		row.createCell(8).setCellValue(top30CustomersRevenueINR.doubleValue());
		row.getCell(8).setCellStyle(borderStyle);
		row.createCell(9).setCellValue(ReportConstants.INR);
		row.getCell(9).setCellStyle(borderStyle);
		row.createCell(10).setCellValue(top30CustomersRevenueUSD.doubleValue());
		row.getCell(10).setCellStyle(borderStyle);
		row.createCell(11).setCellValue(ReportConstants.USD);
		row.getCell(11).setCellStyle(borderStyle);
			}else{
				if(currencyList.contains(ReportConstants.INR)){
					row.createCell(8).setCellValue(top30CustomersRevenueINR.doubleValue());
					row.getCell(8).setCellStyle(borderStyle);
					row.createCell(9).setCellValue(ReportConstants.INR);
					row.getCell(9).setCellStyle(borderStyle);
				}else{
					row.createCell(8).setCellValue(top30CustomersRevenueUSD.doubleValue());
					row.getCell(8).setCellStyle(borderStyle);
					row.createCell(9).setCellValue(ReportConstants.USD);
					row.getCell(9).setCellStyle(borderStyle);
				}
			}
		}
		row1.createCell(7).setCellValue(ReportConstants.PERCENTAGAINSTTOTAL);
		row1.getCell(7).setCellStyle(borderStyle);
		BigDecimal percentAgainstTotal=totalActualProjectedINR.divide(top30CustomersRevenueINR,4,RoundingMode.HALF_DOWN);
		if(percentAgainstTotal!=null)
		row1.createCell(8).setCellValue(percentAgainstTotal.doubleValue()*100);
		row1.getCell(8).setCellStyle(borderStyle);
	}

	private int getTargetVsActualSummaryReportSectionTwo(Map<String, BigDecimal> targetOverAllRevenuesMap,
			Map<String, BigDecimal> actualOverAllRevenuesMap,	
			List<String> currencyList, SXSSFSheet spreadSheet) {
		SXSSFRow row = null;
		BigDecimal targetRevenueINR = new BigDecimal(0);
		BigDecimal actualRevenueINR = new BigDecimal(0);
		int currentRow = 10;
		int count1=0; int count2=0; int count3=0;
		int count4=0; int count5=0; int count6=0;
		int count7=0;
		BigDecimal percentAchieved=new BigDecimal(0);
		double percentAchieve=0;
		if(!actualOverAllRevenuesMap.isEmpty()){
			for (String customerName : actualOverAllRevenuesMap.keySet()) {
				targetRevenueINR = targetOverAllRevenuesMap.get(customerName);
				actualRevenueINR = actualOverAllRevenuesMap.get(customerName);
				if (targetRevenueINR == null) {
					targetRevenueINR = new BigDecimal(0);
				}
				if (actualRevenueINR == null) {
					actualRevenueINR = new BigDecimal(0);
				}
				if (targetRevenueINR.doubleValue()!=0) {
					percentAchieved = actualRevenueINR.divide(targetRevenueINR, 4,
							RoundingMode.HALF_DOWN);
					percentAchieve = percentAchieved.doubleValue() * 100;
				}
				if (percentAchieve < 1) {
					count1++;
				}else if (percentAchieve >= 1 && percentAchieve <= 10) {
					count2++;
				}else if (percentAchieve > 10 && percentAchieve <= 25) {
					count3++;
				}else if (percentAchieve > 25 && percentAchieve <= 50) {
					count4++;
				}else if (percentAchieve > 50 && percentAchieve <= 75) {
					count5++;
				}else if (percentAchieve > 75 && percentAchieve <= 101) {
					count6++;
				}else if (percentAchieve > 100) {
					count7++;
				}
			}
			currentRow = addSectionTwoToExcel(row, currentRow, targetRevenueINR,actualRevenueINR,
						percentAchieved, currencyList,spreadSheet,count1,count2,count3,count4,count5,count6,count7);
		}
		return currentRow;		
	}
	
	
	private int getTargetVsActualSummaryReportSectionTwoByGeo(
			Map<String, BigDecimal> targetOverAllRevenuesMap,
			List<Object[]> overAllRevenuesByGeoMap, Map<String, BigDecimal> actualProjectedOverAllRevenuesByGeoMap,
			List<String> currencyList, SXSSFSheet spreadSheet, String geography, int firstColumn, int secColumn, int currentRow) {
		BigDecimal targetRevenueINR = new BigDecimal(0);
		BigDecimal actualRevenueINR = new BigDecimal(0);
		int count1=0; int count2=0; int count3=0;
		int count4=0; int count5=0; int count6=0;
		int count7=0;
		BigDecimal percentAchieved=new BigDecimal(0);
		double percentAchieve=0;
		if(!actualProjectedOverAllRevenuesByGeoMap.isEmpty()){
			for (String customerName : actualProjectedOverAllRevenuesByGeoMap.keySet()) {
				targetRevenueINR = targetOverAllRevenuesMap.get(customerName);
				actualRevenueINR = actualProjectedOverAllRevenuesByGeoMap.get(customerName);
				if (targetRevenueINR == null) {
					targetRevenueINR = new BigDecimal(0);
				}
				if (actualRevenueINR == null) {
					actualRevenueINR = new BigDecimal(0);
				}
				if (targetRevenueINR.doubleValue()!=0) {
					percentAchieved = actualRevenueINR.divide(targetRevenueINR, 4, RoundingMode.HALF_DOWN);
					percentAchieve = percentAchieved.doubleValue() * 100;
				}
				if (percentAchieve < 1) {
					count1++;
				} else if (percentAchieve >= 1 && percentAchieve <= 10) {
					count2++;
				} else if (percentAchieve > 10 && percentAchieve <= 25) {
					count3++;
				}else if (percentAchieve > 25 && percentAchieve <= 50) {
					count4++;
				}else if (percentAchieve > 50 && percentAchieve <= 75) {
					count5++;
				}else if (percentAchieve > 75 && percentAchieve <= 101) {
					count6++;
				}else if (percentAchieve > 100) {
					count7++;
				}
				}
		currentRow = addSectionTwoToExcelByGeo(currentRow, targetRevenueINR,actualRevenueINR,
						percentAchieved, currencyList,spreadSheet,
						count1,count2,count3,count4,count5,count6,count7,geography,firstColumn,secColumn);
		}
		return currentRow;		
	}

	public static SXSSFRow createOrGetRow(SXSSFSheet spreadsheet, int rowNo) {
		SXSSFRow row = null;
		if (spreadsheet.getRow(rowNo) == null) {
			row = (SXSSFRow) spreadsheet.createRow(rowNo);
		} else {
			row = (SXSSFRow) spreadsheet.getRow(rowNo);
		}
		return row;
	}
	
	private int addSectionTwoToExcelByGeo(int currentRow,
			BigDecimal targetRevenueINR, BigDecimal actualRevenueINR,
			BigDecimal percentAchieved, List<String> currencyList, SXSSFSheet spreadSheet, int count1,
			int count2, int count3, int count4, int count5, int count6,
			int count7, String geography,int firstColumn,int secColumn) {
		currentRow=20;
		CellStyle headerStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(),ReportConstants.BOTTOMROW);
		CellStyle rowStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(),ReportConstants.DATAROW);
		CellStyle subHeaderStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(), ReportConstants.REPORTHEADINGSTYLE);
		SXSSFRow headingRow = createOrGetRow(spreadSheet, currentRow);
		headingRow.createCell(firstColumn).setCellValue(geography);
		spreadSheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, firstColumn,secColumn));
		headingRow.getCell(firstColumn).setCellStyle(subHeaderStyle);
		currentRow=currentRow+2;
		SXSSFRow row0 = createOrGetRow(spreadSheet, currentRow++);
		row0.createCell(firstColumn).setCellValue(ReportConstants.PERCENTACHIEVEDBRACKET);
		row0.getCell(firstColumn).setCellStyle(headerStyle);
		row0.createCell(secColumn).setCellValue(ReportConstants.COUNT);
		row0.getCell(secColumn).setCellStyle(headerStyle);
		spreadSheet.autoSizeColumn(firstColumn);
		SXSSFRow row1 = createOrGetRow(spreadSheet, currentRow++);
		row1.createCell(firstColumn).setCellValue(ReportConstants.NOPENETRATION);
		row1.getCell(firstColumn).setCellStyle(rowStyle);
		row1.createCell(secColumn).setCellValue(count1);
		row1.getCell(secColumn).setCellStyle(rowStyle);
		SXSSFRow row2 = createOrGetRow(spreadSheet, currentRow++);
		row2.createCell(firstColumn).setCellValue(ReportConstants.ONETOTENPERCENT);
		row2.getCell(firstColumn).setCellStyle(rowStyle);
		row2.createCell(secColumn).setCellValue(count2);
		row2.getCell(secColumn).setCellStyle(rowStyle);
		SXSSFRow row3 = createOrGetRow(spreadSheet, currentRow++);
		row3.createCell(firstColumn).setCellValue(ReportConstants.ELEVENTOTWENTYFIVEPERCENT);
		row3.getCell(firstColumn).setCellStyle(rowStyle);
		row3.createCell(secColumn).setCellValue(count3);
		row3.getCell(secColumn).setCellStyle(rowStyle);
		SXSSFRow row4 = createOrGetRow(spreadSheet, currentRow++);
		row4.createCell(firstColumn).setCellValue(ReportConstants.TWENTYSIXTOFIFTYPERCENT);
		row4.getCell(firstColumn).setCellStyle(rowStyle);
		row4.createCell(secColumn).setCellValue(count4);
		row4.getCell(secColumn).setCellStyle(rowStyle);
		SXSSFRow row5 = createOrGetRow(spreadSheet, currentRow++);
		row5.createCell(firstColumn).setCellValue(ReportConstants.FIFTYONETOSEVENTYFIVEPERCENT);
		row5.getCell(firstColumn).setCellStyle(rowStyle);
		row5.createCell(secColumn).setCellValue(count5);
		row5.getCell(secColumn).setCellStyle(rowStyle);
		SXSSFRow row6 = createOrGetRow(spreadSheet, currentRow++);
		row6.createCell(firstColumn).setCellValue(ReportConstants.SEVENTYSIXTOHUNDREDPERCENT);
		row6.getCell(firstColumn).setCellStyle(rowStyle);
		row6.createCell(secColumn).setCellValue(count6);
		row6.getCell(secColumn).setCellStyle(rowStyle);
		SXSSFRow row7 = createOrGetRow(spreadSheet, currentRow++);
		row7.createCell(firstColumn).setCellValue(ReportConstants.HUNDREDPERCENT);
		row7.getCell(firstColumn).setCellStyle(rowStyle);
		row7.createCell(secColumn).setCellValue(count7);
		row7.getCell(secColumn).setCellStyle(rowStyle);
		SXSSFRow row8 = createOrGetRow(spreadSheet, currentRow++);
		row8.createCell(firstColumn).setCellValue(ReportConstants.TOTALRESULT);
		row8.getCell(firstColumn).setCellStyle(headerStyle);
		row8.createCell(secColumn).setCellValue(count1+count2+count3+count4+count5+count6+count7);
		row8.getCell(secColumn).setCellStyle(headerStyle);
		return currentRow;
	}

	private int addSectionTwoToExcel(SXSSFRow row, int currentRow, BigDecimal targetRevenueINR,
			BigDecimal actualRevenueINR, BigDecimal percentAchieved, List<String> currencyList, SXSSFSheet spreadSheet, int count1, int count2, int count3, int count4, int count5, int count6, int count7) {
		int firstColumn = 1;
		int secColumn=2;
		currentRow=10;
		CellStyle headerStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(),ReportConstants.BOTTOMROW);
		CellStyle rowStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(),ReportConstants.DATAROW);
		CellStyle subHeaderStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(), ReportConstants.REPORTHEADINGSTYLE);
		SXSSFRow headingRow = (SXSSFRow) spreadSheet.createRow((short) 8);
		headingRow.createCell(1).setCellValue(ReportConstants.OVERALL);
		spreadSheet.addMergedRegion(new CellRangeAddress(8, 8, 1,2));
		headingRow.getCell(1).setCellStyle(subHeaderStyle);
		SXSSFRow row0=(SXSSFRow) spreadSheet.createRow((short) currentRow++);
		row0.createCell(firstColumn).setCellValue(ReportConstants.PERCENTACHIEVEDBRACKET);
		row0.getCell(firstColumn).setCellStyle(headerStyle);
		row0.createCell(secColumn).setCellValue(ReportConstants.COUNT);
		row0.getCell(secColumn).setCellStyle(headerStyle);
		spreadSheet.autoSizeColumn(firstColumn);
		SXSSFRow row1 = (SXSSFRow) spreadSheet.createRow((short) currentRow++);
		row1.createCell(firstColumn).setCellValue(ReportConstants.NOPENETRATION);
		row1.getCell(firstColumn).setCellStyle(rowStyle);
		row1.createCell(secColumn).setCellValue(count1);
		row1.getCell(secColumn).setCellStyle(rowStyle);
		SXSSFRow row2 = (SXSSFRow) spreadSheet.createRow((short) currentRow++);
		row2.createCell(firstColumn).setCellValue(ReportConstants.ONETOTENPERCENT);
		row2.getCell(firstColumn).setCellStyle(rowStyle);
		row2.createCell(secColumn).setCellValue(count2);
		row2.getCell(secColumn).setCellStyle(rowStyle);
		SXSSFRow row3 = (SXSSFRow) spreadSheet.createRow((short) currentRow++);
		row3.createCell(firstColumn).setCellValue(ReportConstants.ELEVENTOTWENTYFIVEPERCENT);
		row3.getCell(firstColumn).setCellStyle(rowStyle);
		row3.createCell(secColumn).setCellValue(count3);
		row3.getCell(secColumn).setCellStyle(rowStyle);
		SXSSFRow row4 = (SXSSFRow) spreadSheet.createRow((short) currentRow++);
		row4.createCell(firstColumn).setCellValue(ReportConstants.TWENTYSIXTOFIFTYPERCENT);
		row4.getCell(firstColumn).setCellStyle(rowStyle);
		row4.createCell(secColumn).setCellValue(count4);
		row4.getCell(secColumn).setCellStyle(rowStyle);
		SXSSFRow row5 = (SXSSFRow) spreadSheet.createRow((short) currentRow++);
		row5.createCell(firstColumn).setCellValue(ReportConstants.FIFTYONETOSEVENTYFIVEPERCENT);
		row5.getCell(firstColumn).setCellStyle(rowStyle);
		row5.createCell(secColumn).setCellValue(count5);
		row5.getCell(secColumn).setCellStyle(rowStyle);
		SXSSFRow row6 = (SXSSFRow) spreadSheet.createRow((short) currentRow++);
		row6.createCell(firstColumn).setCellValue(ReportConstants.SEVENTYSIXTOHUNDREDPERCENT);
		row6.getCell(firstColumn).setCellStyle(rowStyle);
		row6.createCell(secColumn).setCellValue(count6);
		row6.getCell(secColumn).setCellStyle(rowStyle);
		SXSSFRow row7 = (SXSSFRow) spreadSheet.createRow((short) currentRow++);
		row7.createCell(firstColumn).setCellValue(ReportConstants.HUNDREDPERCENT);
		row7.getCell(firstColumn).setCellStyle(rowStyle);
		row7.createCell(secColumn).setCellValue(count7);
		row7.getCell(secColumn).setCellStyle(rowStyle);
		SXSSFRow row8 = (SXSSFRow) spreadSheet.createRow((short) currentRow++);
		row8.createCell(firstColumn).setCellValue(ReportConstants.TOTALRESULT);
		row8.getCell(firstColumn).setCellStyle(headerStyle);
		row8.createCell(secColumn).setCellValue(count1+count2+count3+count4+count5+count6+count7);
		row8.getCell(secColumn).setCellStyle(headerStyle);
		return currentRow;
	}

	public void getTargetVsActualSummaryReportSectionThree(
			List<CustomerRevenueValues> actualRevenuesList,
			Map<String, BigDecimal> targetRevenuesMap,
			List<String> currencyList, SXSSFSheet spreadSheet, int currentRow, List<GroupCustomerGeoIouResponse> geoIouGroupCustNameList) throws Exception {
//		int currentRow = 24;
		currentRow++;
		String percentAchievedBracket=null;
		BigDecimal totalTargetRevenueINR=new BigDecimal(0);
		BigDecimal totalActualRevenueINR=new BigDecimal(0);
		BigDecimal totalTargetRevenueUSD=new BigDecimal(0);
		BigDecimal totalActualRevenueUSD=new BigDecimal(0);
		String geography=null;
		String iou=null;
		String groupCustName=null;
		String customerName=null;
		double totalPercentageAchieved=0;
		SXSSFRow row=null;
		CellStyle bottomStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(), ReportConstants.BOTTOMROW);
		
		if(!actualRevenuesList.isEmpty()){
			for(CustomerRevenueValues revenueGeoValues:actualRevenuesList){
				BigDecimal percentAchieved=new BigDecimal(0);
				double percentAchieve=0;
				row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
				customerName=revenueGeoValues.getCustomerName();
				BigDecimal targetRevenueINR = targetRevenuesMap.get(customerName);
				BigDecimal actualRevenueINR =revenueGeoValues.getValue();
				
				for(GroupCustomerGeoIouResponse groupCustomerGeoIouResponse:geoIouGroupCustNameList){
					if(customerName.equals(groupCustomerGeoIouResponse.getCustomerName())){
						geography=groupCustomerGeoIouResponse.getDisplayGeography();
						iou=groupCustomerGeoIouResponse.getDisplayIou();
						groupCustName=groupCustomerGeoIouResponse.getGroupCustomerName();
					}
				}
				
				if (targetRevenueINR == null) {
					targetRevenueINR = new BigDecimal(0);
				}
				if (actualRevenueINR == null) {
					actualRevenueINR = new BigDecimal(0);
				}
				
				targetRevenueINR = targetRevenueINR.setScale(2, BigDecimal.ROUND_HALF_UP);
				actualRevenueINR = actualRevenueINR.setScale(2, BigDecimal.ROUND_HALF_UP);
				totalTargetRevenueINR=totalTargetRevenueINR.add(targetRevenueINR);
				totalActualRevenueINR=totalActualRevenueINR.add(actualRevenueINR);
				BigDecimal targetRevenueUSD=beaconConverterService.convert(ReportConstants.INR, ReportConstants.USD,  targetRevenueINR.doubleValue());
				BigDecimal actualRevenueUSD=beaconConverterService.convert(ReportConstants.INR, ReportConstants.USD,  actualRevenueINR.doubleValue());
				
				totalTargetRevenueUSD=beaconConverterService.convert(ReportConstants.INR, ReportConstants.USD,  totalTargetRevenueINR.doubleValue());
				totalActualRevenueUSD=beaconConverterService.convert(ReportConstants.INR, ReportConstants.USD,  totalActualRevenueINR.doubleValue());
				if (targetRevenueINR.doubleValue()!=0.00) {
					percentAchieved = actualRevenueINR.divide(targetRevenueINR, 4,
							RoundingMode.HALF_DOWN);
					percentAchieve = percentAchieved.doubleValue() * 100;
				}
				percentAchievedBracket = setPercentageAchievedBracket(percentAchievedBracket, percentAchieve);
				currentRow = addSectionThreeToExcel(row, currentRow, customerName, targetRevenueINR,targetRevenueUSD,
						actualRevenueINR,actualRevenueUSD, percentAchieved, percentAchievedBracket,
						 currencyList,spreadSheet, geography, iou, groupCustName);
			}
			int lastCol=row.getLastCellNum();
			for(int startCol=0;startCol<=lastCol;startCol++){
				spreadSheet.autoSizeColumn(startCol);
			}
			SXSSFRow totalRow = (SXSSFRow) spreadSheet.createRow((short) currentRow);
			int columnNo=1;
			totalRow.createCell(columnNo).setCellValue(ReportConstants.GRANDTOTAL);
			totalRow.getCell(columnNo).setCellStyle(bottomStyle);
			for(int i=2;i<5;i++){
			totalRow.createCell(i).setCellStyle(bottomStyle);
			}
			columnNo=columnNo+4;
			if(currencyList.size()>1){
			totalRow.createCell(columnNo).setCellValue(totalTargetRevenueINR.doubleValue()); 
			totalRow.getCell(columnNo).setCellStyle(bottomStyle);
			columnNo++;
			totalRow.createCell(columnNo).setCellValue(totalTargetRevenueUSD.doubleValue());
			totalRow.getCell(columnNo).setCellStyle(bottomStyle);
			columnNo++;
			totalRow.createCell(columnNo).setCellValue(totalActualRevenueINR.doubleValue());
			totalRow.getCell(columnNo).setCellStyle(bottomStyle);
			columnNo++;
			totalRow.createCell(columnNo).setCellValue(totalActualRevenueUSD.doubleValue());
			totalRow.getCell(columnNo).setCellStyle(bottomStyle);
			columnNo++;
			}else { 
				if(currencyList.contains(ReportConstants.INR)){
			totalRow.createCell(columnNo).setCellValue(totalTargetRevenueINR.doubleValue());
			totalRow.getCell(columnNo).setCellStyle(bottomStyle);
			columnNo++;
			totalRow.createCell(columnNo).setCellValue(totalActualRevenueINR.doubleValue());
			totalRow.getCell(columnNo).setCellStyle(bottomStyle);
			columnNo++;
			}else{
				totalRow.createCell(columnNo).setCellValue(totalTargetRevenueUSD.doubleValue());
				totalRow.getCell(columnNo).setCellStyle(bottomStyle);
//				spreadSheet.autoSizeColumn(columnNo);
				columnNo++;
				totalRow.createCell(columnNo).setCellValue(totalActualRevenueUSD.doubleValue());
				totalRow.getCell(columnNo).setCellStyle(bottomStyle);
//				spreadSheet.autoSizeColumn(columnNo);
				columnNo++;
			}
			}
			totalPercentageAchieved=totalActualRevenueINR.divide(totalTargetRevenueINR, 4,RoundingMode.HALF_DOWN).doubleValue()*100;
			totalRow.createCell(columnNo).setCellValue(totalPercentageAchieved);
			totalRow.getCell(columnNo).setCellStyle(bottomStyle);
			columnNo++;
			totalRow.createCell(columnNo).setCellStyle(bottomStyle);
			
		}
	}

	public String setPercentageAchievedBracket(String percentAchievedBracket,
			double percentAchieve) {
		if (percentAchieve < 1) {
			percentAchievedBracket = ReportConstants.NOPENETRATION;
		}
		if (percentAchieve >= 1 && percentAchieve <= 10) {
			percentAchievedBracket = ReportConstants.ONETOTENPERCENT;
		}
		if (percentAchieve > 10 && percentAchieve <= 25) {
			percentAchievedBracket = ReportConstants.ELEVENTOTWENTYFIVEPERCENT;
		}
		if (percentAchieve > 25 && percentAchieve <= 50) {
			percentAchievedBracket = ReportConstants.TWENTYSIXTOFIFTYPERCENT;
		}
		if (percentAchieve > 50 && percentAchieve <= 75) {
			percentAchievedBracket = ReportConstants.FIFTYONETOSEVENTYFIVEPERCENT;
		}
		if (percentAchieve > 75 && percentAchieve <= 101) {
			percentAchievedBracket = ReportConstants.SEVENTYSIXTOHUNDREDPERCENT;
		}
		if (percentAchieve > 100) {
			percentAchievedBracket = ReportConstants.HUNDREDPERCENT;
		}
		return percentAchievedBracket;
	}

	public int addSectionThreeToExcel(SXSSFRow row, int currentRow, String customerName,
			BigDecimal targetRevenueINR, BigDecimal targetRevenueUSD,
			BigDecimal actualRevenueINR, BigDecimal actualRevenueUSD,
			BigDecimal percentAchieved, String percentAchievedBracket,
			List<String> currencyList, SXSSFSheet spreadSheet, String geography, String iou, String groupCustName)
			throws Exception {
		CellStyle rowStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(), ReportConstants.DATAROW);
		int columnValue = 1;
		row.createCell(columnValue).setCellValue(customerName);
		row.getCell(columnValue).setCellStyle(rowStyle);
//		spreadSheet.autoSizeColumn(columnValue);
		columnValue++;
		if(groupCustName!=null){
		row.createCell(columnValue).setCellValue(groupCustName);
		}else {
			row.createCell(columnValue).setCellValue(Constants.SPACE);
		}
		row.getCell(columnValue).setCellStyle(rowStyle);
//		spreadSheet.autoSizeColumn(columnValue);
		columnValue++;
		row.createCell(columnValue).setCellValue(geography);
		row.getCell(columnValue).setCellStyle(rowStyle);
//		spreadSheet.autoSizeColumn(columnValue);
		columnValue++;
		row.createCell(columnValue).setCellValue(iou);
		row.getCell(columnValue).setCellStyle(rowStyle);
//		spreadSheet.autoSizeColumn(columnValue);
		columnValue++;
		if (currencyList.size() > 1) {
			row.createCell(columnValue).setCellValue(
					targetRevenueINR.doubleValue());
			row.getCell(columnValue).setCellStyle(rowStyle);
			columnValue++;
			row.createCell(columnValue).setCellValue(
					targetRevenueUSD.doubleValue());
			row.getCell(columnValue).setCellStyle(rowStyle);
//			spreadSheet.autoSizeColumn(columnValue);
			columnValue++;
			row.createCell(columnValue).setCellValue(
					actualRevenueINR.doubleValue());
			row.getCell(columnValue).setCellStyle(rowStyle);
//			spreadSheet.autoSizeColumn(columnValue);
			columnValue++;
			row.createCell(columnValue).setCellValue(
					actualRevenueUSD.doubleValue());
			row.getCell(columnValue).setCellStyle(rowStyle);
//			spreadSheet.autoSizeColumn(columnValue);
			columnValue++;
		} else {
			if (currencyList.contains(ReportConstants.INR)) {
				row.createCell(columnValue).setCellValue(
						targetRevenueINR.doubleValue());
				row.getCell(columnValue).setCellStyle(rowStyle);
				columnValue++;
				row.createCell(columnValue).setCellValue(
						actualRevenueINR.doubleValue());
				row.getCell(columnValue).setCellStyle(rowStyle);
//				spreadSheet.autoSizeColumn(columnValue);
				columnValue++;
			} else {
				row.createCell(columnValue).setCellValue(
						targetRevenueUSD.doubleValue());
				row.getCell(columnValue).setCellStyle(rowStyle);
				columnValue++;
				row.createCell(columnValue).setCellValue(
						actualRevenueUSD.doubleValue());
				row.getCell(columnValue).setCellStyle(rowStyle);
//				spreadSheet.autoSizeColumn(columnValue);
				columnValue++;
			}
		}
		row.createCell(columnValue).setCellValue(
				percentAchieved.doubleValue() * 100);
		row.getCell(columnValue).setCellStyle(rowStyle);
//		spreadSheet.autoSizeColumn(columnValue);
		columnValue++;
		row.createCell(columnValue).setCellValue(percentAchievedBracket);
		row.getCell(columnValue).setCellStyle(rowStyle);
//		spreadSheet.autoSizeColumn(columnValue);
		currentRow++;
		return currentRow;
	}
}
