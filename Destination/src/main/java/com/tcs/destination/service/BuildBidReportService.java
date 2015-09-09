package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.BidOfficeGroupOwnerLinkT;
import com.tcs.destination.bean.ConnectTcsAccountContactLinkT;
import com.tcs.destination.bean.OpportunityCompetitorLinkT;
import com.tcs.destination.bean.OpportunityDealValue;
import com.tcs.destination.bean.OpportunitySubSpLinkT;
import com.tcs.destination.bean.OpportunityTcsAccountContactLinkT;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.BidDetailsTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.SalesStageMappingRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.PrivilegeType;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.FieldsMap;
import com.tcs.destination.utils.GetMaximumListCount;
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
	UserAccessPrivilegesRepository userAccessPrivilegesRepository;

	public InputStreamResource getBidDetailsReport(
			List<BidDetailsT> bidDetailsList, List<String> fields,
			List<String> currency, SXSSFWorkbook workbook) throws Exception {
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook.createSheet("Bid Report");
		
		spreadSheet.setDefaultColumnWidth(30);
		
//		CellStyle cellStyle = ExcelUtils.createRowStyle(workbook, ReportConstants.REPORTHEADER);
		SXSSFRow row = (SXSSFRow) spreadSheet.createRow(0);
		if (fields.size() == 0 && fields.isEmpty()) {
			createHeaderBidDetailsReportMandatoryFields(row, spreadSheet,
					currency);
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

	public void createHeaderBidDetailsReportMandatoryFields(SXSSFRow row,
			SXSSFSheet spreadSheet, List<String> currency) {
		CellStyle cellStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(),	ReportConstants.REPORTHEADER);
		getMandatoryBidReportHeader(row, spreadSheet);
		if (currency.size() > 1) {
			row.createCell(8).setCellValue(ReportConstants.DIGITALDEALVALUE);
			row.getCell(8).setCellStyle(cellStyle);
//			spreadSheet.autoSizeColumn(8);
			spreadSheet.addMergedRegion(new CellRangeAddress(0, 0, 8,
					8 + currency.size() - 1));
			SXSSFRow row1 = (SXSSFRow) spreadSheet.createRow(1);
			for (int i = 0; i < currency.size(); i++) {
				row1.createCell((8 + i)).setCellValue(currency.get(i));
				row1.getCell(8+i).setCellStyle(cellStyle);
			}
		} else {
			row.createCell(8).setCellValue(
					ReportConstants.DIGITALDEALVALUE + " In " + currency.get(0));
			row.getCell(8).setCellStyle(cellStyle);
//			spreadSheet.autoSizeColumn(8);
		}
	}

	public void getMandatoryBidReportHeader(SXSSFRow row, SXSSFSheet spreadSheet) {
		CellStyle cellStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(),	ReportConstants.REPORTHEADER);
		row.createCell(0).setCellValue(ReportConstants.OPPORTUNITYID);
		row.getCell(0).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(0);
		row.createCell(1).setCellValue(ReportConstants.DISPLAYGEO);
		row.getCell(1).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(1);
		row.createCell(2).setCellValue(ReportConstants.DISPLAYSERVICELINE);
		row.getCell(2).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(2);
		row.createCell(3).setCellValue(ReportConstants.DISPLAYIOU);
		row.getCell(3).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(3);
		row.createCell(4).setCellValue(ReportConstants.GROUPCUSTOMERNAME);
		row.getCell(4).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(4);
		row.createCell(5).setCellValue(ReportConstants.SALESSTAGE);
		row.getCell(5).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(5);
		row.createCell(6).setCellValue(ReportConstants.BIDREQUESTTYPE);
		row.getCell(6).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(6);
		row.createCell(7).setCellValue(ReportConstants.BIDREQUESTRECEIVEDDATE);
		row.getCell(7).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(7);
	}

	public void createHeaderBidDetailsReportOptionalFields(
			List<BidDetailsT> bidDetailsList, SXSSFRow row, SXSSFRow row1,
			List<String> fields, SXSSFSheet spreadSheet, List<String> currency) {
		
		// This method creates default headers for Bid Report
		getMandatoryBidReportHeader(row, spreadSheet);
		CellStyle cellStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(),	ReportConstants.REPORTHEADER);
		if (currency.size() > 1) {
			row.createCell(8).setCellValue(ReportConstants.DIGITALDEALVALUE);
			row.getCell(8).setCellStyle(cellStyle);
//			spreadSheet.autoSizeColumn(8);
			spreadSheet.addMergedRegion(new CellRangeAddress(0, 0, 8, 8 + currency.size() - 1));
			for (int i = 0; i < currency.size(); i++) {
				row1.createCell((8 + i)).setCellValue(currency.get(i));
				row1.getCell(8 + i).setCellStyle(cellStyle);
			}
		} else {
			row.createCell(8).setCellValue(
					ReportConstants.DIGITALDEALVALUE + " In " + currency.get(0));
			row.getCell(8).setCellStyle(cellStyle);
//			spreadSheet.autoSizeColumn(8);
		}
		int colValue = 9;
		if (currency.size() > 1) {
			colValue = 10;
		}
		for (String field : fields) {
			row.createCell(colValue).setCellValue(FieldsMap.fieldsMap.get(field));
			row.getCell(colValue).setCellStyle(cellStyle);
//			spreadSheet.autoSizeColumn(colValue);
			colValue++;
		}
	}

	public int getBidReportWithMandatoryFields(List<BidDetailsT> bidDetailsList,
			SXSSFSheet spreadSheet, List<String> currency) {
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
//		for(int startCol=0;startCol<=8;startCol++){
//			spreadSheet.autoSizeColumn(startCol);
//		}
		return currentRow;
	}

	public void getBidDetailsReportMandatoryFields(SXSSFSheet spreadSheet,
			SXSSFRow row, List<String> currency, BidDetailsT bidDetail) {
		int i = 0;
		CellStyle cellStyle = ExcelUtils.createRowStyle(
				spreadSheet.getWorkbook(), ReportConstants.DATAROW);
		row.createCell(0).setCellValue(bidDetail.getOpportunityId());
		row.getCell(0).setCellStyle(cellStyle);
		row.createCell(1).setCellValue(
				bidDetail.getOpportunityT().getCustomerMasterT()
						.getGeographyMappingT().getDisplayGeography());
		row.getCell(1).setCellStyle(cellStyle);
		for (OpportunitySubSpLinkT opportunitySubSpLinkT : bidDetail
				.getOpportunityT().getOpportunitySubSpLinkTs()) {
			row.createCell(2).setCellValue(
					opportunitySubSpLinkT.getSubSpMappingT().getDisplaySubSp());
			row.getCell(2).setCellStyle(cellStyle);
		}
		row.createCell(3).setCellValue(
				bidDetail.getOpportunityT().getCustomerMasterT()
						.getIouCustomerMappingT().getDisplayIou());
		row.getCell(3).setCellStyle(cellStyle);
		row.createCell(4).setCellValue(
				bidDetail.getOpportunityT().getCustomerMasterT()
						.getGroupCustomerName());
		row.getCell(4).setCellStyle(cellStyle);
		row.createCell(5).setCellValue(
				bidDetail.getOpportunityT().getSalesStageCode());
		row.getCell(5).setCellStyle(cellStyle);
		row.createCell(6).setCellValue(bidDetail.getBidRequestType());
		row.getCell(6).setCellStyle(cellStyle);
		row.createCell(7).setCellValue(
				bidDetail.getBidRequestReceiveDate().toString());
		row.getCell(7).setCellStyle(cellStyle);
		for (OpportunityDealValue opportunityDealValue : bidDetail.getOpportunityT().getOpportunityDealValues()) {
			BigDecimal dealValue = opportunityDealValue.getDigitalDealValue();
			if (dealValue != null) {
				row.createCell(8 + i).setCellValue(opportunityDealValue.getDigitalDealValue().doubleValue());
				row.getCell(8+i).setCellStyle(cellStyle);
				i++;
			}else{
				row.createCell(8 + i).setCellValue(0.00);
				row.getCell(8+i).setCellStyle(cellStyle);
				i++;
			}
		}
	}

	public int getBidReportWithOptionalFields(List<BidDetailsT> bidDetailsList,
			SXSSFSheet spreadSheet, List<String> fields, List<String> currency) {
		CellStyle cellStyle = ExcelUtils.createRowStyle(
				spreadSheet.getWorkbook(), ReportConstants.DATAROW);
		int currentRow = 1;
		SXSSFRow row = null;
		if (currency.size() > 1) {
			currentRow = 2;
		}
		for (BidDetailsT bidDetail : bidDetailsList) {
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow++);
			getBidDetailsReportMandatoryFields(spreadSheet, row, currency, bidDetail);
			int colValue = 9;
			if (currency.size() > 1) {
				colValue = 10;
			}
			for (String field : fields) {
				switch (field) {
				case ReportConstants.IOU:
					row.createCell(colValue).setCellValue(bidDetail.getOpportunityT().getCustomerMasterT().getIouCustomerMappingT().getIou());
					row.getCell(colValue).setCellStyle(cellStyle);
//					spreadSheet.autoSizeColumn(colValue);
					colValue++;
					break;
				case ReportConstants.GEOGRAPHY:
					row.createCell(colValue).setCellValue(bidDetail.getOpportunityT().getGeographyCountryMappingT().getGeography());
					row.getCell(colValue).setCellStyle(cellStyle);
//					spreadSheet.autoSizeColumn(colValue);
					colValue++;
					break;
				case ReportConstants.SUBSP:
					List<String> subSpList=new ArrayList<String>();
					for (OpportunitySubSpLinkT opportunitySubSpLinkT : bidDetail.getOpportunityT().getOpportunitySubSpLinkTs()) {
					subSpList.add(opportunitySubSpLinkT.getSubSpMappingT().getSubSp());
					}
					row.createCell(colValue).setCellValue(subSpList.toString().replace("[", "").replace("]", ""));
					row.getCell(colValue).setCellStyle(cellStyle);
//					spreadSheet.autoSizeColumn(colValue);
					colValue++;
					break;
				case ReportConstants.COUNTRY:
					row.createCell(colValue).setCellValue(bidDetail.getOpportunityT().getGeographyCountryMappingT().getCountry());
					row.getCell(colValue).setCellStyle(cellStyle);
					colValue++;
					break;
				case ReportConstants.CRM:
					if(bidDetail.getOpportunityT().getCrmId()!=null){
					row.createCell(colValue).setCellValue(bidDetail.getOpportunityT().getCrmId());
					}else{
						row.createCell(colValue).setCellValue(Constants.SPACE);
					}
					row.getCell(colValue).setCellStyle(cellStyle);
//					spreadSheet.autoSizeColumn(colValue);
					colValue++;
					break;
				case ReportConstants.NEWLOGO:
					if(bidDetail.getOpportunityT().getNewLogo()!=null){
					row.createCell(colValue).setCellValue(bidDetail.getOpportunityT().getNewLogo());
					} else {
						row.createCell(colValue).setCellValue(Constants.SPACE);
					}
					row.getCell(colValue).setCellStyle(cellStyle);
					colValue++;
					break;
				case ReportConstants.OPPNAME:
					row.createCell(colValue).setCellValue(bidDetail.getOpportunityT().getOpportunityName());
					row.getCell(colValue).setCellStyle(cellStyle);
					colValue++;
					break;
				case ReportConstants.TCSACCOUNTCONTACT:
					List<String> tcsContactNames=new ArrayList<String>();
					for (OpportunityTcsAccountContactLinkT opportunityTcsAccountContactLinkT : bidDetail.getOpportunityT().getOpportunityTcsAccountContactLinkTs()) {
					tcsContactNames.add(opportunityTcsAccountContactLinkT.getContactT().getContactName());
					}
					row.createCell(colValue).setCellValue(tcsContactNames.toString().replace("[", "").replace("]", ""));
					row.getCell(colValue).setCellStyle(cellStyle);
//					spreadSheet.autoSizeColumn(colValue);
					colValue++;
					break;
				case ReportConstants.COMPETITORS:
					List<String> competitorList=new ArrayList<String>();
					for (OpportunityCompetitorLinkT opportunityCompetitorLinkT : bidDetail.getOpportunityT().getOpportunityCompetitorLinkTs()) {
						competitorList.add(opportunityCompetitorLinkT.getCompetitorName());
					}
					row.createCell(colValue).setCellValue(competitorList.toString().replace("[", "").replace("]", ""));
					row.getCell(colValue).setCellStyle(cellStyle);
//					spreadSheet.autoSizeColumn(colValue);
					colValue++;
					break;
				case ReportConstants.COREATTRIBUTESUSEDFORWINNING:
					if(bidDetail.getCoreAttributesUsedForWinning()!=null){
					row.createCell(colValue).setCellValue(bidDetail.getCoreAttributesUsedForWinning());
					} else {
						row.createCell(colValue).setCellValue(Constants.SPACE);
					}
					row.getCell(colValue).setCellStyle(cellStyle);
					colValue++;
					break;
				case ReportConstants.BIDID:
					row.createCell(colValue).setCellValue(bidDetail.getBidId());
					row.getCell(colValue).setCellStyle(cellStyle);
					colValue++;
					break;
				case ReportConstants.BIDOFFICEGROUPOWNER:
					if (bidDetail.getBidOfficeGroupOwnerLinkTs().size() > 0) {
					List<String> bodofficeGroupOwner=new ArrayList<String>();
					for (BidOfficeGroupOwnerLinkT bidOfficeGroupOwnerLinkT : bidDetail.getBidOfficeGroupOwnerLinkTs()) {
					UserT userT = userRepository.findByUserId(bidOfficeGroupOwnerLinkT.getBidOfficeGroupOwner());
					bodofficeGroupOwner.add(userT.getUserName());
					}
					row.createCell(colValue).setCellValue(bodofficeGroupOwner.toString().replace("[", "").replace("]", ""));
					row.getCell(colValue).setCellStyle(cellStyle);
//					spreadSheet.autoSizeColumn(colValue);
					}else{
						row.createCell(colValue).setCellValue(Constants.SPACE);
						row.getCell(colValue).setCellStyle(cellStyle);
					}
					colValue++;
					break;
				case ReportConstants.TARGETBIDSUBMISSIONDATE:
					if(bidDetail.getTargetBidSubmissionDate()!=null){
					row.createCell(colValue).setCellValue(bidDetail.getTargetBidSubmissionDate().toString());
					} else {
						row.createCell(colValue).setCellValue(Constants.SPACE);
					}
					row.getCell(colValue).setCellStyle(cellStyle);
					colValue++;
					break;
				case ReportConstants.ACTUALBIDSUBMISSIONDATE:
					if(bidDetail.getActualBidSubmissionDate()!=null){
					row.createCell(colValue).setCellValue(bidDetail.getActualBidSubmissionDate().toString());
					}else{
					row.createCell(colValue).setCellValue(Constants.SPACE);
					}
					row.getCell(colValue).setCellStyle(cellStyle);
					colValue++;
					break;
				case ReportConstants.EXPECTEDDATEOFOUTCOME:
					if(bidDetail.getExpectedDateOfOutcome()!=null){
					row.createCell(colValue).setCellValue(bidDetail.getExpectedDateOfOutcome().toString());
					} else {
						row.createCell(colValue).setCellValue(Constants.SPACE);
					}
					row.getCell(colValue).setCellStyle(cellStyle);
					colValue++;
					break;
				}
				currentRow = currentRow + 0;
			}
		}
//		int lastCol = row.getLastCellNum();
//		for(int startCol=8;startCol<=lastCol;startCol++){
//			if(row.getCell(startCol)!=null)
//			spreadSheet.autoSizeColumn(startCol);
//		}
		return currentRow;
	}

	public void getBidReportTitlePage(SXSSFWorkbook workbook, List<String> geography, List<String> iou,
			List<String> serviceLines, String userId, String tillDate) {
		SXSSFSheet spreadsheet = (SXSSFSheet) workbook.createSheet("Title");
		List<String> privilegeValueList = new ArrayList<String>();
		CellStyle headinStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.REPORTHEADER);
		CellStyle subHeadingStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.DATAROW);
		CellStyle dataRow = ExcelUtils.createRowStyle(workbook,
				ReportConstants.DATAROW);
		SXSSFRow row = null;
		
		////
		String userAccessField = null;
		List<UserAccessPrivilegesT> userPrivilegesList = 
				userAccessPrivilegesRepository.findByUserIdAndParentPrivilegeIdIsNullAndIsactive(userId, Constants.Y);
		UserT user = userRepository.findByUserId(userId);
		String userGroup=user.getUserGroupMappingT().getUserGroup();
		row = (SXSSFRow) spreadsheet.createRow(12);
		row.createCell(4).setCellValue("User Access Filter's");
		row.getCell(4).setCellStyle(subHeadingStyle);
		spreadsheet.autoSizeColumn(4);
		switch (userGroup) {
		case ReportConstants.GEOHEAD:
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
		////
		row = (SXSSFRow) spreadsheet.createRow(4);
		spreadsheet.addMergedRegion(new CellRangeAddress(4, 4, 4, 10));
		row.createCell(4).setCellValue("Bid report as on " + tillDate);
		spreadsheet.autoSizeColumn(4);
		row.getCell(4).setCellStyle(headinStyle);
		row = (SXSSFRow) spreadsheet.createRow(6);
		row.createCell(4).setCellValue("User Selection Filter's");
		row.getCell(4).setCellStyle(subHeadingStyle);
		spreadsheet.autoSizeColumn(4);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, ReportConstants.GEO, geography, 7,
				dataRow);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, "IOU", iou, 8, dataRow);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, "Service Line", serviceLines, 9,
				dataRow);
		row = (SXSSFRow) spreadsheet.createRow(10);
		row.setRowStyle(null);
	}
	
//	private void writeDetailsForSearchType(SXSSFSheet spreadsheet,
//			String searchType, List<String> searchList, int rowValue,
//			CellStyle dataRowStyle) {
//		SXSSFRow row = null;
//		row = (SXSSFRow) spreadsheet.createRow(rowValue);
//		row.createCell(4).setCellValue(searchType);
//		spreadsheet.autoSizeColumn(4);
//		String completeList = getCompleteList(searchList);
//		row.createCell(5).setCellValue(completeList);
//		spreadsheet.autoSizeColumn(5);
//	}

//	private String getCompleteList(List<String> itemList) {
//		if (itemList.size() == 0) {
//			return "All";
//		} else {
//			return itemList.toString().replace("[", "").replace("]", "");
//		}
//	}
}
