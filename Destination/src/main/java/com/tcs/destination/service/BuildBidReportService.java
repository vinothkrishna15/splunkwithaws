package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.OpportunityDealValue;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.BidDetailsTRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.OpportunityCompetitorLinkTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.OpportunitySubSpLinkTRepository;
import com.tcs.destination.data.repository.SalesStageMappingRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.PrivilegeType;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.FieldsMap;
import com.tcs.destination.utils.ReportConstants;

@Component
public class BuildBidReportService {
	private static final Logger logger = LoggerFactory
			.getLogger(BuildBidReportService.class);

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	BeaconConverterService beaconConverterService;

	@Autowired
	SalesStageMappingRepository salesStageMappingRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	BidDetailsTRepository bidDetailsTRepository;
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	OpportunitySubSpLinkTRepository opportunitySubSpLinkTRepository;
	
	@Autowired
	OpportunityCompetitorLinkTRepository opportunityCompetitorLinkTRepository;
	
	
	@Autowired
	UserAccessPrivilegesRepository userAccessPrivilegesRepository;

	/**
	 * This method is used to get spreadSheet for the bid detailed report
	 * 
	 * @param bidDetailsList
	 * @param fields
	 * @param currency
	 * @param workbook
	 * @return
	 * @throws Exception
	 */
	public InputStreamResource getBidDetailsReport(
			List<BidDetailsT> bidDetailsList, List<String> fields,
			List<String> currency, SXSSFWorkbook workbook) throws Exception {
		logger.debug("Inside getBidDetailsReport() method");
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook.createSheet("Bid Report");
		
		CellStyle cellStyle = ExcelUtils.createRowStyle(workbook, ReportConstants.REPORTHEADER);
		SXSSFRow row = (SXSSFRow) spreadSheet.createRow(0);
		if (fields.size() == 0 && fields.isEmpty()) {
			getMandatoryBidReportHeader(row, spreadSheet,currency,cellStyle);
			getBidReportWithMandatoryFields(bidDetailsList, spreadSheet, currency);
		} else {
			SXSSFRow row1 = (SXSSFRow) spreadSheet.createRow(1);
			createHeaderBidDetailsReportOptionalFields(bidDetailsList, row,
					row1, fields, spreadSheet, currency);
			getBidReportWithOptionalFields(bidDetailsList, spreadSheet, fields,
					currency);
		}
		ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
		workbook.write(byteOutPutStream);
		byteOutPutStream.flush();
		byteOutPutStream.close();
		byte[] bytes = byteOutPutStream.toByteArray();
		InputStreamResource inputStreamResource = new InputStreamResource(
				new ByteArrayInputStream(bytes));
		return inputStreamResource;
	}

//<<<<<<< HEAD
//	/**
//	 * This method is used to set bid detailed report mandatory fields along with deal value in both INR and USD to spreadSheet
//	 * 
//	 * @param row
//	 * @param spreadSheet
//	 * @param currency
//	 */
//	public void createHeaderBidDetailsReportMandatoryFields(SXSSFRow row,
//			SXSSFSheet spreadSheet, List<String> currency) {
//		logger.debug("Inside createHeaderBidDetailsReportMandatoryFields() method");
//		CellStyle cellStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(),	ReportConstants.REPORTHEADER);
//		getMandatoryBidReportHeader(row, spreadSheet);
//=======

	/**
	 * This method is used to set bid detailed report mandatory fields to spreadSheet
	 * 
	 * @param row
	 * @param spreadSheet
	 * @param currency 
	 * @param cellStyle 
	 */
	public void getMandatoryBidReportHeader(SXSSFRow row, SXSSFSheet spreadSheet, List<String> currency, CellStyle cellStyle) {
		logger.debug("Inside getMandatoryBidReportHeader() method");
		int colNo=0;
		row.createCell(colNo).setCellValue(ReportConstants.OPPORTUNITYID);
		row.getCell(colNo++).setCellStyle(cellStyle);
		row.createCell(colNo).setCellValue(ReportConstants.DISPLAYGEO);
		row.getCell(colNo++).setCellStyle(cellStyle);
		row.createCell(colNo).setCellValue(ReportConstants.DISPLAYSERVICELINE);
		row.getCell(colNo++).setCellStyle(cellStyle);
		row.createCell(colNo).setCellValue(ReportConstants.DISPLAYIOU);
		row.getCell(colNo++).setCellStyle(cellStyle);
		row.createCell(colNo).setCellValue(ReportConstants.GROUPCUSTOMERNAME);
		row.getCell(colNo++).setCellStyle(cellStyle);
		row.createCell(colNo).setCellValue(ReportConstants.SALESSTAGE);
		row.getCell(colNo++).setCellStyle(cellStyle);
		row.createCell(colNo).setCellValue(ReportConstants.BIDREQUESTTYPE);
		row.getCell(colNo++).setCellStyle(cellStyle);
		row.createCell(colNo).setCellValue(ReportConstants.BIDREQUESTRECEIVEDDATE);
		row.getCell(colNo++).setCellStyle(cellStyle);
		if (currency.size() > 1) {
			row.createCell(colNo).setCellValue(ReportConstants.DEALVALUEINR);
			row.getCell(colNo++).setCellStyle(cellStyle);
			row.createCell(colNo).setCellValue(ReportConstants.DEALVALUEUSD);
			row.getCell(colNo++).setCellStyle(cellStyle);
		} else {
			row.createCell(colNo).setCellValue(ReportConstants.DIGITALDEALVALUE + "(" + currency.get(0)	+ ")");
			row.getCell(colNo++).setCellStyle(cellStyle);
		}
	}

	/**
	 * This method is used to create header for bid detailed report for both mandatory and optional fields
	 * 
	 * @param bidDetailsList
	 * @param row
	 * @param row1
	 * @param fields
	 * @param spreadSheet
	 * @param currency
	 */
	public void createHeaderBidDetailsReportOptionalFields(
			List<BidDetailsT> bidDetailsList, SXSSFRow row, SXSSFRow row1,
			List<String> fields, SXSSFSheet spreadSheet, List<String> currency) {
		logger.debug("Inside createHeaderBidDetailsReportOptionalFields() method");
		CellStyle cellStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(),	ReportConstants.REPORTHEADER);
		// This method creates default headers for Bid Report
		getMandatoryBidReportHeader(row, spreadSheet,currency,cellStyle);
		int colValue = 9;
		if (currency.size() > 1) {
			colValue = 10;
		}
		List<String> orderedFields = Arrays.asList("iou","geography","subSp","country","crmId", "newLogo",
				"opportunityName","tcsAccountContact","competitors","coreAttributesUsedForWinning",	"bidId",
				"bidOfficeGroupOwner","targetBidSubmissionDate","actualBidSubmissionDate","expectedDateOfOutcome");
		
		for (String field : orderedFields) {
			if(fields.contains(field)){
			row.createCell(colValue).setCellValue(FieldsMap.fieldsMap.get(field));
			row.getCell(colValue).setCellStyle(cellStyle);
			colValue++;
			}
		}
	}

	/**
	 * This method is used to set  bid detailed report for mandatory fields to spreadSheet
	 * 
	 * @param bidDetailsList
	 * @param spreadSheet
	 * @param currency
	 * @return
	 */
	public int getBidReportWithMandatoryFields(List<BidDetailsT> bidDetailsList,
			SXSSFSheet spreadSheet, List<String> currency) {
		logger.debug("Inside getBidReportWithMandatoryFields() method");
		int currentRow = 1;
		if (currency.size() > 1) {
			currentRow = 2;
		}
		for (BidDetailsT bidDetail : bidDetailsList) {
			SXSSFRow row = (SXSSFRow) spreadSheet.createRow(currentRow);
			getBidDetailsReportMandatoryFields(spreadSheet, row, currency,
					bidDetail);
			currentRow++;
		}
		return currentRow;
	}

	/**
	 * This method is used to set bid detailed report for mandatory fields to spreadSheet
	 * 
	 * @param spreadSheet
	 * @param row
	 * @param currency
	 * @param bidDetail
	 */
	public void getBidDetailsReportMandatoryFields(SXSSFSheet spreadSheet,
			SXSSFRow row, List<String> currency, BidDetailsT bidDetail) {
		logger.debug("Inside getBidDetailsReportMandatoryFields() method");
		int i = 0;
//		CellStyle cellStyle = ExcelUtils.createRowStyle(
//				spreadSheet.getWorkbook(), ReportConstants.DATAROW);
		row.createCell(0).setCellValue(bidDetail.getOpportunityId());
		row.createCell(1).setCellValue(
				bidDetail.getOpportunityT().getCustomerMasterT()
						.getGeographyMappingT().getDisplayGeography());
		
		List<String> displaySubSpList = new ArrayList<String>();
		String oppPrimarySubSp = opportunitySubSpLinkTRepository.findPrimaryDisplaySubSpByOpportunityId(bidDetail.getOpportunityId());
		displaySubSpList.add(oppPrimarySubSp+ReportConstants.P);
		displaySubSpList.addAll(opportunitySubSpLinkTRepository.findSecondaryDisplaySubSpByOpportunityId(bidDetail.getOpportunityId()));
		if(!displaySubSpList.isEmpty()){
			row.createCell(2).setCellValue(ExcelUtils.removeSquareBracesAndAppendListElementsAsString(displaySubSpList));
		}
		
		row.createCell(3).setCellValue(
				bidDetail.getOpportunityT().getCustomerMasterT()
						.getIouCustomerMappingT().getDisplayIou());
		row.createCell(4).setCellValue(
				bidDetail.getOpportunityT().getCustomerMasterT()
						.getGroupCustomerName());
		//set sales stage code
		row.createCell(5).setCellValue(bidDetail.getOpportunityT().getSalesStageMappingT().getSalesStageDescription());
		row.createCell(6).setCellValue(bidDetail.getBidRequestType());
		row.createCell(7).setCellValue(
				bidDetail.getBidRequestReceiveDate().toString());
		for (OpportunityDealValue opportunityDealValue : bidDetail.getOpportunityT().getOpportunityDealValues()) {
			BigDecimal dealValue = opportunityDealValue.getDigitalDealValue();
			if (dealValue != null) {
				row.createCell(8 + i).setCellValue(opportunityDealValue.getDigitalDealValue().doubleValue());
				i++;
			}else{
				row.createCell(8 + i).setCellValue(0.00);
				i++;
			}
		}
	}

	/**
	 * This method is used to set bid detailed report both mandatory and optional fields to spreadSheet
	 * 
	 * @param bidDetailsList
	 * @param spreadSheet
	 * @param fields
	 * @param currency
	 * @return
	 */
	public int getBidReportWithOptionalFields(List<BidDetailsT> bidDetailsList,
			SXSSFSheet spreadSheet, List<String> fields, List<String> currency) {
		logger.debug("Inside getBidReportWithOptionalFields() method");
//		CellStyle cellStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(), ReportConstants.DATAROW);
		int currentRow = 1;
		SXSSFRow row = null;
		CellStyle cellStyleDateFormat = spreadSheet.getWorkbook().createCellStyle(); 
		CreationHelper createHelper = spreadSheet.getWorkbook().getCreationHelper();
		cellStyleDateFormat.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy"));
		boolean iouFlag = fields.contains(ReportConstants.IOU);
		boolean geographyFlag = fields.contains(ReportConstants.GEOGRAPHY);
		boolean subFlag = fields.contains(ReportConstants.SUBSP);
		boolean countryFlag = fields.contains(ReportConstants.COUNTRY);
		boolean crmIdFlag = fields.contains(ReportConstants.CRM);
		boolean newLogoFlag = fields.contains(ReportConstants.NEWLOGO);
		boolean opportunityNameFlag = fields.contains(ReportConstants.OPPNAME);
		boolean tcsAccConFlag = fields.contains(ReportConstants.TCSACCOUNTCONTACT);
		boolean competitorsFlag = fields.contains(ReportConstants.COMPETITORS);
		boolean coreAttUsedForWinFlag = fields.contains(ReportConstants.COREATTRIBUTESUSEDFORWINNING);
		boolean bidIdFlag = fields.contains(ReportConstants.BIDID);
		boolean bidOffGrpOwnerFlag = fields.contains(ReportConstants.BIDOFFICEGROUPOWNER);
		boolean targetBidSubDtFlag = fields.contains(ReportConstants.TARGETBIDSUBMISSIONDATE);
		boolean actualBidSubDtFlag = fields.contains(ReportConstants.ACTUALBIDSUBMISSIONDATE);
		boolean expDtOfOutcomeFlag = fields.contains(ReportConstants.EXPECTEDDATEOFOUTCOME);
		
		for (BidDetailsT bidDetail : bidDetailsList) {
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow++);
			//to set mandatory fields to spreadSheet
			getBidDetailsReportMandatoryFields(spreadSheet, row, currency, bidDetail);
			int colValue = 9;
			if (currency.size() > 1) {
				colValue = 10;
				}
			if(iouFlag){
				row.createCell(colValue).setCellValue(bidDetail.getOpportunityT().getCustomerMasterT().getIouCustomerMappingT().getIou());
				colValue++;
			}
			
			if(geographyFlag){
				row.createCell(colValue).setCellValue(bidDetail.getOpportunityT().getGeographyCountryMappingT().getGeography());
				colValue++;
			}
			
			if(subFlag){
				List<String> oppSubSpList = new ArrayList<String>();
				String oppPrimarySubSp = opportunitySubSpLinkTRepository.findPrimarySubSpByOpportunityId(bidDetail.getOpportunityId());
				oppSubSpList.add(oppPrimarySubSp+ReportConstants.P);
				oppSubSpList.addAll(opportunitySubSpLinkTRepository.findSecondarySubSpByOpportunityId(bidDetail.getOpportunityId()));
				if(!oppSubSpList.isEmpty()){
					row.createCell(colValue).setCellValue(ExcelUtils.removeSquareBracesAndAppendListElementsAsString(oppSubSpList));
				}
				colValue++;
			}
			
			if(countryFlag){
				row.createCell(colValue).setCellValue(bidDetail.getOpportunityT().getGeographyCountryMappingT().getCountry());
				colValue++;
			}
			
			if(crmIdFlag) {
				if(bidDetail.getOpportunityT().getCrmId()!=null){
					row.createCell(colValue).setCellValue(bidDetail.getOpportunityT().getCrmId());
				}
				colValue++;
			}

			if(newLogoFlag){
				if(bidDetail.getOpportunityT().getNewLogo()!=null){
					row.createCell(colValue).setCellValue(bidDetail.getOpportunityT().getNewLogo());
				}
				colValue++;
			}
			
			if(opportunityNameFlag){
				row.createCell(colValue).setCellValue(bidDetail.getOpportunityT().getOpportunityName());
				colValue++;
			}
			
			
			if(tcsAccConFlag){
				List<String> tcsContactNames= contactRepository.findTcsAccountContactNamesByOpportinityId(bidDetail.getOpportunityId());
				row.createCell(colValue).setCellValue(ExcelUtils.removeSquareBracesAndAppendListElementsAsString(tcsContactNames));
				colValue++;
			}
			
			if(competitorsFlag){
				List<String> competitorList=opportunityCompetitorLinkTRepository.findCompetitorNamesByOpportunityId(bidDetail.getOpportunityId());
				row.createCell(colValue).setCellValue(ExcelUtils.removeSquareBracesAndAppendListElementsAsString(competitorList));
				colValue++;
			}
			
			if(coreAttUsedForWinFlag){
				if(bidDetail.getCoreAttributesUsedForWinning()!=null){
					row.createCell(colValue).setCellValue(bidDetail.getCoreAttributesUsedForWinning());
				}
				colValue++;
			}
			
			if(bidIdFlag){
				row.createCell(colValue).setCellValue(bidDetail.getBidId());
				colValue++;
			}
			
			if(bidOffGrpOwnerFlag){
				if (bidDetail.getBidOfficeGroupOwnerLinkTs().size() > 0) {
					List<String> bodofficeGroupOwner=userRepository.findBidOfficeGroupOwnersNameByBidId(bidDetail.getBidId());
					row.createCell(colValue).setCellValue(ExcelUtils.removeSquareBracesAndAppendListElementsAsString(bodofficeGroupOwner));
				}
				colValue++;
			}
				
			if(targetBidSubDtFlag){
				if(bidDetail.getTargetBidSubmissionDate()!=null){
					row.createCell(colValue).setCellValue(bidDetail.getTargetBidSubmissionDate());
					row.getCell(colValue).setCellStyle(cellStyleDateFormat);
				}
				colValue++;
			}
			
			if(actualBidSubDtFlag){
				if(bidDetail.getActualBidSubmissionDate()!=null){
					row.createCell(colValue).setCellValue(bidDetail.getActualBidSubmissionDate().toString());
				}
				colValue++;
			}
				
			if(expDtOfOutcomeFlag){
				if(bidDetail.getExpectedDateOfOutcome()!=null){
					row.createCell(colValue).setCellValue(bidDetail.getExpectedDateOfOutcome());
					row.getCell(colValue).setCellStyle(cellStyleDateFormat);
				}
				colValue++;
			}
			
			currentRow = currentRow + 0;
		}
		return currentRow;
	}

	/**
	 * This method is used to set bid detailed report title page to spreadSheet
	 * 
	 * @param workbook
	 * @param geography
	 * @param iou
	 * @param serviceLines
	 * @param userId
	 * @param tillDate
	 * @param country
	 * @param currency
	 * @param fromMonth
	 * @param toMonth
	 * @param reportType
	 * @param year
	 */
	public void getBidReportTitlePage(SXSSFWorkbook workbook, List<String> geography, List<String> iou,
			List<String> serviceLines, String userId, String tillDate, List<String> country, List<String> currency, String fromMonth, String toMonth, String reportType, String year) {
		logger.debug("Inside getBidReportTitlePage() method");
		SXSSFSheet spreadsheet = (SXSSFSheet) workbook.createSheet(ReportConstants.TITLE);
		List<String> privilegeValueList = new ArrayList<String>();
		CellStyle headinStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.REPORTHEADER);
		CellStyle subHeadingStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.DATAROW);
		CellStyle dataRow = ExcelUtils.createRowStyle(workbook,
				ReportConstants.DATAROW);
		SXSSFRow row = null;
		String period = year;
		row = (SXSSFRow) spreadsheet.createRow(4);
		spreadsheet.addMergedRegion(new CellRangeAddress(4, 4, 4, 10));
		row.createCell(4).setCellValue("Bid report as on " + tillDate);
		spreadsheet.autoSizeColumn(4);
		row.getCell(4).setCellStyle(headinStyle);
		row = (SXSSFRow) spreadsheet.createRow(6);
		row.createCell(4).setCellValue(ReportConstants.USERSELECTIONFILTER);
		row.getCell(4).setCellStyle(subHeadingStyle);
		spreadsheet.autoSizeColumn(4);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, ReportConstants.GEO, geography, 7, dataRow);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, ReportConstants.Country, country, 8, dataRow);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, Constants.IOU, iou, 9, dataRow);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, ReportConstants.SERVICELINES, serviceLines, 10, dataRow);
		row = (SXSSFRow) spreadsheet.createRow(11);
		row.createCell(4).setCellValue(ReportConstants.PERIOD);
		if(year.length()==0){
			period=ExcelUtils.getPeriod(fromMonth, toMonth);
		}
		row.createCell(5).setCellValue(period);
		
		String userAccessField = null;
		List<UserAccessPrivilegesT> userPrivilegesList = 
				userAccessPrivilegesRepository.findByUserIdAndParentPrivilegeIdIsNullAndIsactive(userId, Constants.Y);
		UserT user = userRepository.findByUserId(userId);
		String userGroup=user.getUserGroupMappingT().getUserGroup();
		row = (SXSSFRow) spreadsheet.createRow(14);
		row.createCell(4).setCellValue(ReportConstants.USERACCESSFILTER);
		row.getCell(4).setCellStyle(subHeadingStyle);
		spreadsheet.autoSizeColumn(4);
		switch (userGroup) {
		case ReportConstants.GEOHEAD:
			userAccessField = ReportConstants.GEO;
			for(UserAccessPrivilegesT accessPrivilegesT:userPrivilegesList){
				String previlageType=accessPrivilegesT.getPrivilegeType();
				String privilageValue=accessPrivilegesT.getPrivilegeValue();
				if(previlageType.equals(PrivilegeType.GEOGRAPHY.name())){
					privilegeValueList.add(privilageValue);
				}
			}
			ExcelUtils.writeDetailsForSearchTypeUserAccessFilter(spreadsheet, userAccessField, privilegeValueList, user, dataRow, ReportConstants.BIDBASEDONPRIVILAGE);
			break;
		case ReportConstants.IOUHEAD:
			userAccessField = ReportConstants.Iou;
			for(UserAccessPrivilegesT accessPrivilegesT:userPrivilegesList){
				String previlageType=accessPrivilegesT.getPrivilegeType();
				String privilageValue=accessPrivilegesT.getPrivilegeValue();
				if(previlageType.equals(PrivilegeType.IOU.name())){
					privilegeValueList.add(privilageValue);
				}
			}
			ExcelUtils.writeDetailsForSearchTypeUserAccessFilter(spreadsheet, userAccessField, privilegeValueList, user, dataRow, ReportConstants.BIDBASEDONPRIVILAGE);
			break;
		default :
			ExcelUtils.writeUserFilterConditions(spreadsheet, user, ReportConstants.FULLACCESS);
		}
		row = (SXSSFRow) spreadsheet.createRow(21);
		row.createCell(4).setCellValue(ReportConstants.DISPLAYPREFERENCE);
		row.getCell(4).setCellStyle(subHeadingStyle);
		row = (SXSSFRow) spreadsheet.createRow(22);
		row.createCell(4).setCellValue(ReportConstants.CURRENCY);
		row.createCell(5).setCellValue(ExcelUtils.removeSquareBracesAndAppendListElementsAsString(currency));
		row = (SXSSFRow) spreadsheet.createRow(23);
		row.createCell(4).setCellValue(ReportConstants.REPORTTYPE);
		row.createCell(5).setCellValue(reportType);
		spreadsheet.addMergedRegion(new CellRangeAddress(25, 25, 4, 7));
		row = (SXSSFRow) spreadsheet.createRow(25);
		row.createCell(4).setCellValue(ReportConstants.REPORTNOTE);
	}
}
