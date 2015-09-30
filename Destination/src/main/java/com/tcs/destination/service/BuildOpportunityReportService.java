package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.BidOfficeGroupOwnerLinkT;
import com.tcs.destination.bean.ConnectOpportunityLinkIdT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.OpportunityCompetitorLinkT;
import com.tcs.destination.bean.OpportunityCustomerContactLinkT;
import com.tcs.destination.bean.OpportunityDealValue;
import com.tcs.destination.bean.OpportunityOfferingLinkT;
import com.tcs.destination.bean.OpportunityPartnerLinkT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunitySubSpLinkT;
import com.tcs.destination.bean.OpportunitySummaryValue;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.OpportunityTcsAccountContactLinkT;
import com.tcs.destination.bean.OpportunityWinLossFactorsT;
import com.tcs.destination.bean.ReportSummaryOpportunity;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.BidDetailsTRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.SalesStageMappingRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.FieldsMap;
import com.tcs.destination.utils.GetMaximumListCount;
import com.tcs.destination.utils.ReportConstants;

@Component
public class BuildOpportunityReportService {
	private static final Logger logger = LoggerFactory
			.getLogger(BuildOpportunityReportService.class);

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	GeographyRepository geographyMappingTRepository;

	@Autowired
	BeaconConverterService beaconConverterService;

	@Autowired
	SalesStageMappingRepository salesStageMappingRepository;

	@Autowired
	UserRepository userRepository;
	
	@Autowired 
	UserService userService;

	@Autowired
	BidDetailsTRepository bidDetailsTRepository;
	
	@Autowired
	UserAccessPrivilegesRepository userAccessPrivilegesRepository;
	
	@Autowired 
	ReportsService reportsService;
	
	@PersistenceContext
	private EntityManager entityManager;

	// Detailed Report For Opportunity
	public void getOpportunities(String month, String quarter, String year,
			List<String> geography, List<String> country, List<String> iou,
			List<String> serviceLines, List<Integer> salesStage,
			List<String> currency, String userId, List<String> fields,
			SXSSFWorkbook workbook) throws Exception {

		logger.debug("Inside ReportService getOpportunitiesWith Method");
		Date fromDate = new Date();
		Date toDate = new Date();
		List<String> geoList = new ArrayList<String>();
		List<String> iouList = new ArrayList<String>();
		List<String> countryList = new ArrayList<String>();
		List<String> serviceLinesList = new ArrayList<String>();
		List<String> userIds = new ArrayList<String>();
		List<OpportunityT> opportunities = new ArrayList<OpportunityT>();

			fromDate = DateUtils.getDate(month, quarter, year, true);
			toDate = DateUtils.getDate(month, quarter, year, false);
		
		UserT user = userRepository.findByUserId(userId);
		if(user == null){
			logger.error("User Id Not Found "+ userId );
			throw new DestinationException(HttpStatus.NOT_FOUND,"User Id Not Found");
		}
		String userGroup=user.getUserGroupMappingT().getUserGroup();
		addItemToListGeo(geography,geoList);
		addItemToList(iou,iouList);
		addItemToList(country,countryList);
		addItemToList(serviceLines,serviceLinesList);
		switch (userGroup) {
		case ReportConstants.BDM:
			userIds.add(userId);
			opportunities = opportunityRepository.findOpportunitiesByRoleWith(fromDate, toDate, salesStage, userIds, 
					geoList, countryList, iouList, serviceLinesList);
			break;
		case ReportConstants.BDMSUPERVISOR:
			List<String> subOrdinatesList = userRepository.getAllSubordinatesIdBySupervisorId(userId);
			userIds.addAll(subOrdinatesList);
			if(!userIds.contains(userId)){
				userIds.add(userId);
			}
			opportunities = opportunityRepository.findOpportunitiesByRoleWith(fromDate, toDate, salesStage, userIds, 
					geoList, countryList, iouList, serviceLinesList);
			break;
		default:
				if(geography.contains("All") && (iou.contains("All") && serviceLines.contains("All")) && country.contains("All")){
					String queryString = reportsService.getOpportunityDetailedQueryString(userId,fromDate,toDate,salesStage);
					Query opportunityDetailedReportQuery = entityManager.createNativeQuery(queryString);
					List<String> opportunityIds = opportunityDetailedReportQuery.getResultList();
					opportunities = opportunityRepository.findByOpportunityIds(opportunityIds);
				} else {
					opportunities = opportunityRepository.findOpportunitiesWith(fromDate, toDate, geoList, countryList, iouList, serviceLinesList, 
							salesStage);
				}
			break;
		}
		if (opportunities.size() > 0) {
			List<OpportunityT> opportunityList = beaconConverterService
					.convertOpportunityCurrency(opportunities, currency);
			getOpportunityReport(opportunityList, fields, currency, workbook);

		} else {
			logger.error("NOT_FOUND: Report could not be downloaded, as no opportunities are available for user selection and privilege combination");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Report could not be downloaded, as no opportunities are available for user selection and privilege combination");
		}
	}

	public void getOpportunityReport(List<OpportunityT> opportunityList,
			List<String> fields, List<String> currency, SXSSFWorkbook workbook)
			throws Exception {
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook.createSheet("Complete Data");
		spreadSheet.setDefaultColumnWidth(30);
		SXSSFRow headerRow = null;
		int currentRow = 0;
		int headerColumnValue = 0;
		headerRow = (SXSSFRow) spreadSheet.createRow((short) currentRow);
		if (fields.size() == 0 && fields.isEmpty()) {
			CreateHeaderOpportunityReportMandatoryFields(headerRow, spreadSheet,
					currency);
			currentRow = OpportunityReportWithMandatoryFields(opportunityList,
					spreadSheet, headerRow, currency);
			currentRow++;
		} else {
			SXSSFRow currencyRow = (SXSSFRow) spreadSheet.createRow(1);
			headerColumnValue = CreateHeaderOpportunityReportOptionalFields(opportunityList, headerRow,
					currencyRow, fields, workbook, spreadSheet, currentRow, currency);
			currentRow = OpportunityReportWithOptionalFields(opportunityList,
					headerRow, spreadSheet, currentRow, fields, headerRow, currency, headerColumnValue);
			currentRow++;
		}
	}

	public void CreateHeaderOpportunityReportMandatoryFields(SXSSFRow row,
			SXSSFSheet spreadSheet, List<String> currency) {
		CellStyle cellStyle = ExcelUtils.createRowStyle(
				(SXSSFWorkbook) spreadSheet.getWorkbook(), ReportConstants.REPORTHEADER);
		getMandatoryHeaderForOpportunityReport(row, spreadSheet, cellStyle);
		if (currency.size() > 1) {
			row.createCell(7).setCellValue(ReportConstants.DIGITALDEALVALUE);
			row.getCell(7).setCellStyle(cellStyle);
//			spreadSheet.autoSizeColumn(7);
			spreadSheet.addMergedRegion(new CellRangeAddress(0, 0, 7,
					7 + currency.size() - 1));
			CellStyle cellStyle1 = ExcelUtils.createRowStyle(
					(SXSSFWorkbook) spreadSheet.getWorkbook(), ReportConstants.REPORTHEADER1);
			SXSSFRow row1 = (SXSSFRow) spreadSheet.createRow(1);
			for (int i = 0; i < currency.size(); i++) {
				row1.createCell((7 + i)).setCellValue(currency.get(i));
				row1.getCell(7 + i).setCellStyle(cellStyle1);
			}
		} else {
			row.createCell(7).setCellValue(
					ReportConstants.DIGITALDEALVALUE + "(" + currency.get(0)
							+ ")");
			row.getCell(7).setCellStyle(cellStyle);
//			spreadSheet.autoSizeColumn(7);
		}
	}

	private void getMandatoryHeaderForOpportunityReport(SXSSFRow row,
			SXSSFSheet spreadSheet, CellStyle cellStyle) {
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
		row.createCell(6).setCellValue(ReportConstants.OPPORTUNITYNAME);
		row.getCell(6).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(6);
	}

	public int CreateHeaderOpportunityReportOptionalFields(
			List<OpportunityT> opportunityList, SXSSFRow row, SXSSFRow row1,
			List<String> fields, SXSSFWorkbook workbook, SXSSFSheet spreadSheet,
			int currentRow, List<String> currency) {

		CellStyle cellStyle = ExcelUtils.createRowStyle(
				(SXSSFWorkbook) spreadSheet.getWorkbook(), ReportConstants.REPORTHEADER);
		CellStyle cellStyle1 = ExcelUtils.createRowStyle(
				(SXSSFWorkbook) spreadSheet.getWorkbook(), ReportConstants.REPORTHEADER1);

		/**
		 * This method creates default headers
		 */
		getMandatoryHeaderForOpportunityReport(row, spreadSheet, cellStyle);
		if (currency.size() > 1) {
			row.createCell(7).setCellValue(ReportConstants.DIGITALDEALVALUE);
			row.getCell(7).setCellStyle(cellStyle);
			spreadSheet.autoSizeColumn(7);
			spreadSheet.addMergedRegion(new CellRangeAddress(0, 0, 7,
					7 + currency.size() - 1));
			for (int i = 0; i < currency.size(); i++) {
				row1.createCell((7 + i)).setCellValue(currency.get(i));
				row1.getCell(7 + i).setCellStyle(cellStyle1);
			}
		} else {
			row.createCell(7).setCellValue(
					ReportConstants.DIGITALDEALVALUE + "(" + currency.get(0)+")");
			spreadSheet.autoSizeColumn(7);
			row.getCell(7).setCellStyle(cellStyle);
		}
		int colValue = 8;
		if (currency.size() > 1) {
			colValue = 9;
		}
		createHeaderForOptionalFields(row, spreadSheet, cellStyle,fields, colValue);		
		
		return colValue;
	}

	private void createHeaderForOptionalFields(SXSSFRow row, SXSSFSheet spreadSheet, 
			CellStyle cellStyle, List<String> fields, int columnNo) {
		
		if(fields.contains("projectDealValue")){
			row.createCell(columnNo).setCellValue("Project Digital Deal Value");
			row.getCell(columnNo).setCellStyle(cellStyle);
			columnNo++;
		}
		for (String field : fields) {
			row.createCell(columnNo).setCellValue(FieldsMap.fieldsMap.get(field));
			row.getCell(columnNo).setCellStyle(cellStyle);
			columnNo++;
		}
	}

	public int OpportunityReportWithMandatoryFields(
			List<OpportunityT> opportunityList, SXSSFSheet spreadSheet,
			SXSSFRow row, List<String> currency) {
		int currentRow = 1;
		if (currency.size() > 1) {
			currentRow = 2;
		}
		for (OpportunityT opportunity : opportunityList) {
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
			getOpportunityReportMandatoryFields(spreadSheet, row, currency,
					opportunity);
			currentRow++;
		}
//		for(int startCol=0;startCol<=8;startCol++){
//			spreadSheet.autoSizeColumn(startCol);
//		}
		return currentRow;
	}

	public void getOpportunityReportMandatoryFields(SXSSFSheet spreadSheet,
			SXSSFRow row, List<String> currency, OpportunityT opportunity) {
		int i = 0;
		CellStyle cellStyle = ExcelUtils.createRowStyle(
				(SXSSFWorkbook) spreadSheet.getWorkbook(), ReportConstants.DATAROW);
		row.createCell(0).setCellValue(opportunity.getOpportunityId());
		row.getCell(0).setCellStyle(cellStyle);
		String geography = opportunity.getGeographyCountryMappingT().getGeography();
		GeographyMappingT geographyMappingT = geographyMappingTRepository.findByGeography(geography);
		row.createCell(1).setCellValue(geographyMappingT.getDisplayGeography());
		row.getCell(1).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(1);
		row.createCell(2);
		List<String> subSpList = new ArrayList<String>();
		for (OpportunitySubSpLinkT opportunitySubSpLinkT : opportunity
				.getOpportunitySubSpLinkTs()) {
			subSpList.add(opportunitySubSpLinkT.getSubSpMappingT().getDisplaySubSp());
		}
		row.getCell(2).setCellValue(subSpList.toString().replace("]", "").replace("[", ""));
		row.getCell(2).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(2);
		row.createCell(3).setCellValue(
				opportunity.getCustomerMasterT().getIouCustomerMappingT().getDisplayIou());
		row.getCell(3).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(3);
		row.createCell(4).setCellValue(
				opportunity.getCustomerMasterT().getGroupCustomerName());
		row.getCell(4).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(4);
		row.createCell(5).setCellValue(opportunity.getSalesStageCode());
		row.getCell(5).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(5);
		row.createCell(6).setCellValue(opportunity.getOpportunityName());
		row.getCell(6).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(6);
		for (OpportunityDealValue opportunityDealValue : opportunity.getOpportunityDealValues()) {
			BigDecimal dealValue = opportunityDealValue.getDigitalDealValue();
			if (dealValue != null) {
				row.createCell(7 + i).setCellValue(opportunityDealValue.getDigitalDealValue().doubleValue());
			} else {
				row.createCell(7 + i).setCellValue(0);
			}
			row.getCell(7 + i).setCellStyle(cellStyle);
//			spreadSheet.autoSizeColumn(7 + i);
			i++;
		}
	}

	public int OpportunityReportWithOptionalFields(
			List<OpportunityT> opportunityList, SXSSFRow headerRow,
			SXSSFSheet spreadSheet, int currentRow, List<String> fields,
			SXSSFRow row, List<String> currency, int headerColumnValue) throws DestinationException {
//		CellStyle headingStyle = ExcelUtils.createRowStyle((SXSSFWorkbook) spreadSheet.getWorkbook(), ReportConstants.REPORTHEADER);
		CellStyle dataRowStyle = ExcelUtils.createRowStyle((SXSSFWorkbook) spreadSheet.getWorkbook(), ReportConstants.DATAROW);
		Boolean initialMerge = true;
		Boolean headingColumn = true;
		boolean projectDVFlag = fields.contains(ReportConstants.DIGITALDEALVALUEPROJECTCURRENCY);
		boolean custNameFlag = fields.contains(ReportConstants.CUSTNAME);
		boolean countryFlag = fields.contains(ReportConstants.COUNTRY);
		boolean iouFlag = fields.contains(ReportConstants.IOU);
		boolean geographyFlag = fields.contains(ReportConstants.GEOGRAPHY);
		boolean subFlag = fields.contains(ReportConstants.SUBSP);
		boolean offeringFlag = fields.contains(ReportConstants.OFFERING);
		boolean tcsAccConFlag = fields.contains(ReportConstants.TCSACCOUNTCONTACT);
		boolean custConNameFlag = fields.contains(ReportConstants.CUSTOMERCONTACTNAME);
		boolean opportunityOwnerFlag = fields.contains(ReportConstants.OPPORTUNITYOWNER);
		boolean oppDescFlag = fields.contains(ReportConstants.OPPORTUNITYDESCRIPTION);
		boolean reqRecvDtFlag = fields.contains(ReportConstants.REQUESTRECEIVEDDATE);
		boolean newLogoFlag = fields.contains(ReportConstants.NEWLOGO);
		boolean competitorsFlag = fields.contains(ReportConstants.COMPETITORS);
		boolean partnershipInvFlag = fields.contains(ReportConstants.PARTNERSHIPSINVOLVED);
		boolean dealTypeFlag = fields.contains(ReportConstants.DEALTYPE);
		boolean salesSuppOwnerFlag = fields.contains(ReportConstants.SALESSUPPORTOWNER);
		boolean dealMarkFlag = fields.contains(ReportConstants.DEALREMARKSNOTES);
		boolean descForWLFlag = fields.contains(ReportConstants.DESCRIPTIONFORWINLOSS);
		boolean dealClDtFlag = fields.contains(ReportConstants.DEALCLOSUREDATE);
		boolean factorForWLFlag = fields.contains(ReportConstants.FACTORSFORWINLOSS);
		boolean oppLinkedIdFlag = fields.contains(ReportConstants.OPPORTUNITYLINKID);
		boolean bidIdFlag = fields.contains(ReportConstants.BIDID);
		boolean bidOffGrpOwnerFlag = fields.contains(ReportConstants.BIDOFFICEGROUPOWNER);
		boolean bidReqRcvDtFlag = fields.contains(ReportConstants.BIDREQUESTRECEIVEDDATE);
		boolean bidReqTyFlag = fields.contains(ReportConstants.BIDREQUESTTYPE);
		boolean actualBidSubDtFlag = fields.contains(ReportConstants.ACTUALBIDSUBMISSIONDATE);
		boolean targetBidSubDtFlag = fields.contains(ReportConstants.TARGETBIDSUBMISSIONDATE);
		boolean winProbFlag = fields.contains(ReportConstants.WINPROBABILITY);
		boolean coreAttUsedForWinFlag = fields.contains(ReportConstants.COREATTRIBUTESUSEDFORWINNING);
		boolean expDtOfOutcomeFlag = fields.contains(ReportConstants.EXPECTEDDATEOFOUTCOME);
		
		
		if (currency.size() > 1) {
			currentRow = currentRow + 2;
		} else {
			currentRow = currentRow + 1;
		}
		for (OpportunityT opportunity : opportunityList) {
			
			initialMerge = true;
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow++);
			getOpportunityReportMandatoryFields(spreadSheet, row, currency,
					opportunity);
			int colValue = 8;
			if (currency.size() > 1) {
				colValue = 9;
			}
			
			if (projectDVFlag) {
				if(opportunity.getDigitalDealValue() != null){
				row.createCell(colValue).setCellValue(opportunity.getDigitalDealValue());
				row.getCell(colValue).setCellStyle(dataRowStyle);
				} else {
					row.createCell(colValue).setCellValue(0);
					row.getCell(colValue).setCellStyle(dataRowStyle);
				}
				colValue++;
				if(opportunity.getDealCurrency() != null){
					row.createCell(colValue).setCellValue(opportunity.getDealCurrency());
					row.getCell(colValue).setCellStyle(dataRowStyle);
					} else {
						row.createCell(colValue).setCellValue("");
						row.getCell(colValue).setCellStyle(dataRowStyle);
					}
				
				colValue++;
				}
			
			if (custNameFlag) {
				if(opportunity.getCustomerMasterT().getCustomerName() != null)
				row.createCell(colValue).setCellValue(opportunity.getCustomerMasterT().getCustomerName());
				else
					row.createCell(colValue).setCellValue("");
				row.getCell(colValue).setCellStyle(dataRowStyle);
				
				colValue++;
				}
			
			if (countryFlag) {
				
				if(opportunity.getGeographyCountryMappingT().getCountry() != null)
				row.createCell(colValue).setCellValue(opportunity.getGeographyCountryMappingT().getCountry());
				else
					row.createCell(colValue).setCellValue("");
				row.getCell(colValue).setCellStyle(dataRowStyle);
				
				colValue++;
			}
			
			if(iouFlag) {
				if(opportunity.getCustomerMasterT().getIouCustomerMappingT().getIou() != null) {
					row.createCell(colValue).setCellValue(opportunity.getCustomerMasterT().getIouCustomerMappingT().getIou());
				}
				else {
					row.createCell(colValue).setCellValue(Constants.SPACE);
				}
				row.getCell(colValue).setCellStyle(dataRowStyle);
				colValue++;
			}

			if (geographyFlag) {
				row.createCell(colValue).setCellValue(opportunity.getCustomerMasterT().getGeographyMappingT().getGeography());
				row.getCell(colValue).setCellStyle(dataRowStyle);
				colValue++;
			}
			
			if (subFlag) {
				List<String> subSpList = new ArrayList<String>();
				for (OpportunitySubSpLinkT opportunitySubSpLinkT : opportunity
						.getOpportunitySubSpLinkTs()) {
					subSpList.add(opportunitySubSpLinkT.getSubSpMappingT().getSubSp());
				}
				row.createCell(colValue).setCellValue(subSpList.toString().replace("]", "").replace("[", ""));
				row.getCell(colValue).setCellStyle(dataRowStyle);
				
				colValue++;
			}
			
			if (offeringFlag) {
					List<String> offeringList = new ArrayList<String>();
					if (opportunity.getOpportunityOfferingLinkTs().size() > 0) {
						for (OpportunityOfferingLinkT opportunityOfferingLinkT : opportunity.getOpportunityOfferingLinkTs()) {
							offeringList.add(opportunityOfferingLinkT.getOfferingMappingT().getOffering());
						}
					}
					row.createCell(colValue).setCellValue(offeringList.toString().replace("[", "").replace("]", ""));
					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
					}
			if (tcsAccConFlag) {
					List<String> tcsAccountContactList = new ArrayList<String>();
					for (OpportunityTcsAccountContactLinkT opportunityTcsAccountContactLinkT : opportunity
							.getOpportunityTcsAccountContactLinkTs()) {
						tcsAccountContactList.add(opportunityTcsAccountContactLinkT.getContactT().getContactName());
					}
					row.createCell(colValue).setCellValue(tcsAccountContactList.toString().replace("[", "").replace("]", ""));
					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
					}
			if (custConNameFlag) {
					List<String> customerContactNameList = new ArrayList<String>();
					for (OpportunityCustomerContactLinkT opportunityCustomerContactLinkT : opportunity
							.getOpportunityCustomerContactLinkTs()) {
						customerContactNameList.add(opportunityCustomerContactLinkT.getContactT().getContactName());
					}
					row.createCell(colValue).setCellValue(customerContactNameList.toString().replace("[", "").replace("]", ""));;
					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
					}
			
//			if (opportunityOwnerFlag) {
//				UserT userT = userRepository.findByUserId(opportunity.getOpportunityOwner());
//				if(userT.getUserName() != null)
//				row.createCell(colValue).setCellValue(userT.getUserName());
//				else
//					row.createCell(colValue).setCellValue("");
//				row.getCell(colValue).setCellStyle(dataRowStyle);
//				colValue++;
//				}
			if (opportunityOwnerFlag) {
				if(opportunity.getOpportunityName() != null) {
				row.createCell(colValue).setCellValue(opportunity.getOpportunityName());
				}
				else {
					row.createCell(colValue).setCellValue(Constants.SPACE);
				}
				row.getCell(colValue).setCellStyle(dataRowStyle);
				colValue++;
				}
			
			if (oppDescFlag) {
					if(opportunity.getOpportunityDescription() != null)
					row.createCell(colValue).setCellValue(opportunity.getOpportunityDescription());
					else
						row.createCell(colValue).setCellValue("");
					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
					}
			if (reqRecvDtFlag) {
					if(opportunity.getOpportunityRequestReceiveDate() != null)
					row.createCell(colValue).setCellValue(opportunity.getOpportunityRequestReceiveDate().toString());
					else
						row.createCell(colValue).setCellValue("");
					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
					}
			if (newLogoFlag) {
					if(opportunity.getNewLogo() != null) 
						row.createCell(colValue).setCellValue(opportunity.getNewLogo());
					else
						row.createCell(colValue).setCellValue("");
					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
					}
			
			if (competitorsFlag) {
					row.createCell(colValue);
					row.getCell(colValue).setCellStyle(dataRowStyle);
					List<String> competitorName = new ArrayList<String>();
				for (OpportunityCompetitorLinkT opportunityCompetitorLinkT : opportunity
						.getOpportunityCompetitorLinkTs()) {
					competitorName.add(opportunityCompetitorLinkT.getCompetitorName());
				}
				row.getCell(colValue).setCellValue(competitorName.toString().replace("[", "").replace("]", ""));
				colValue++;
				}
			
			if (partnershipInvFlag) {
				List<String> partnershipsInvolvedList = new ArrayList<String>();
				for (OpportunityPartnerLinkT opportunityPartnerLinkT : opportunity
						.getOpportunityPartnerLinkTs()) {
					partnershipsInvolvedList.add(opportunityPartnerLinkT.getPartnerMasterT().getPartnerName());
				}
				row.createCell(colValue).setCellValue(partnershipsInvolvedList.toString().replace("[", "").replace("]", ""));
				row.getCell(colValue).setCellStyle(dataRowStyle);
				colValue++;
				}
			
			if (dealTypeFlag) {
					if(opportunity.getDealType() != null){
					row.createCell(colValue).setCellValue(
							opportunity.getDealType());
					} else {
						row.createCell(colValue).setCellValue("");
					}
					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
					}
			
			if (salesSuppOwnerFlag) {
				List<String> salesSupportOwnerList = new ArrayList<String>();
				for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity
						.getOpportunitySalesSupportLinkTs()) {
					UserT user = userRepository.findByUserId(opportunitySalesSupportLinkT.getSalesSupportOwner());
					salesSupportOwnerList.add(user.getUserName());
				}
				row.createCell(colValue).setCellValue(salesSupportOwnerList.toString().replace("[", "").replace("]", ""));;
				row.getCell(colValue).setCellStyle(dataRowStyle);
				colValue++;
				}
			
			if (dealMarkFlag) {
				List<String> dealRemarksNotesList = new ArrayList<String>();
				for (NotesT notesT : opportunity.getNotesTs()) {
					dealRemarksNotesList.add(notesT.getNotesUpdated());
				}
				row.createCell(colValue).setCellValue(dealRemarksNotesList.toString().replace("[", "").replace("]", ""));;
				row.getCell(colValue).setCellStyle(dataRowStyle);
				colValue++;
				}
			
			if (dealClDtFlag) {
				if(opportunity.getDealClosureDate() != null)
					row.createCell(colValue).setCellValue(opportunity.getDealClosureDate().toString());
				else
					row.createCell(colValue).setCellValue("");
				row.getCell(colValue).setCellStyle(dataRowStyle);
				colValue++;
			}
			
			if (factorForWLFlag) {
				List<String> factorsForWinLossList = new ArrayList<String>();
				for (OpportunityWinLossFactorsT opportunityWinLossFactorsT : opportunity
						.getOpportunityWinLossFactorsTs()) {
					factorsForWinLossList.add(opportunityWinLossFactorsT.getWinLossFactor());
				}
				row.createCell(colValue).setCellValue(factorsForWinLossList.toString().replace("[", "").replace("]", ""));
				row.getCell(colValue).setCellStyle(dataRowStyle);
				colValue++;
			}
			
			if (descForWLFlag) {
				if(opportunity.getDescriptionForWinLoss() != null)
				row.createCell(colValue).setCellValue(opportunity.getDescriptionForWinLoss());
				else
					row.createCell(colValue).setCellValue("");
				row.getCell(colValue).setCellStyle(dataRowStyle);
				colValue++;
			}
			
			if (initialMerge && fields.size() > 0) {
				for (int i = 0; i < colValue; i++) {
					if(opportunity.getBidDetailsTs().size() > 0)
					spreadSheet.addMergedRegion(new CellRangeAddress(currentRow - 1, currentRow + opportunity.getBidDetailsTs().size() - 2, i, i));
				}
			}
			
			if (bidIdFlag) {
				if (opportunity.getBidDetailsTs().size() > 0) {
					for (int bid = 0; bid < opportunity.getBidDetailsTs().size(); bid++) {
						row = ExcelUtils.getRow(spreadSheet, (currentRow + bid - 1));
						if(opportunity.getBidDetailsTs().get(bid).getBidId() != null) {
						row.createCell(colValue).setCellValue(opportunity.getBidDetailsTs().get(bid).getBidId());
						}
						else {
							row.createCell(colValue).setCellValue(Constants.SPACE);
						}
						row.getCell(colValue).setCellStyle(dataRowStyle);
					}
					colValue++;
				} else {
					row.createCell(colValue).setCellValue(Constants.SPACE);
					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
				}
			}
			
			if (oppLinkedIdFlag) {
				List<String> opportunityLinkIDList = new ArrayList<String>();
				for (ConnectOpportunityLinkIdT connectOpportunityLinkIdT : opportunity
						.getConnectOpportunityLinkIdTs()) {
					opportunityLinkIDList.add(connectOpportunityLinkIdT.getConnectT().getConnectName());
				}
				row.createCell(colValue).setCellValue(opportunityLinkIDList.toString().replace("[", "").replace("]", ""));;
				row.getCell(colValue).setCellStyle(dataRowStyle);
				colValue++;
			}

			if (bidReqTyFlag) {
				if (opportunity.getBidDetailsTs().size() > 0) {
					for (int bid = 0; bid < opportunity.getBidDetailsTs().size(); bid++) {
						row = ExcelUtils.getRow(spreadSheet, (currentRow + bid - 1));
						if (opportunity.getBidDetailsTs().get(bid).getBidRequestType() != null) {
							row.createCell(colValue).setCellValue(opportunity.getBidDetailsTs().get(bid).getBidRequestType());
						} else {
							row.createCell(colValue).setCellValue(Constants.SPACE);
						}
						row.getCell(colValue).setCellStyle(dataRowStyle);
					}
					colValue++;
				} else {
					row.createCell(colValue).setCellValue(Constants.SPACE);
					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
				}
			}
			if (bidReqRcvDtFlag) {
				if (opportunity.getBidDetailsTs().size() > 0) {
					for (int bid = 0; bid < opportunity.getBidDetailsTs().size(); bid++) {
						row = ExcelUtils.getRow(spreadSheet, (currentRow + bid - 1));
						if(opportunity.getBidDetailsTs().get(bid).getBidRequestReceiveDate() != null){
							row.createCell(colValue).setCellValue(opportunity.getBidDetailsTs().get(bid).getBidRequestReceiveDate().toString());
						}
						else {
							row.createCell(colValue).setCellValue(Constants.SPACE);
						}
						row.getCell(colValue).setCellStyle(dataRowStyle);
					}
					colValue++;
				} else {
					row.createCell(colValue).setCellValue(Constants.SPACE);
					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
				}
			}
			if (bidOffGrpOwnerFlag) {
				if (opportunity.getBidDetailsTs().size() > 0) {
					int bid = 0;
					List<String> bidOfficeGroupOwnerList = new ArrayList<String>();
					for (BidDetailsT bidDetailsT : opportunity.getBidDetailsTs()) {
						bidOfficeGroupOwnerList.clear();
						row = ExcelUtils.getRow(spreadSheet, (currentRow + bid - 1));
						for (BidOfficeGroupOwnerLinkT bidOfficeGroupOwnerLinkT : bidDetailsT.getBidOfficeGroupOwnerLinkTs()) {
							UserT user = userRepository.findByUserId(bidOfficeGroupOwnerLinkT.getBidOfficeGroupOwner());
							bidOfficeGroupOwnerList.add(user.getUserName());
						}
						row.createCell(colValue).setCellValue(bidOfficeGroupOwnerList.toString().replace("[", "").replace("]", ""));
						row.getCell(colValue).setCellStyle(dataRowStyle);
						bid++;
					}
					colValue++;
				} else {
					row.createCell(colValue).setCellValue("");
					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
				}
			}
			if (targetBidSubDtFlag) {
				if (opportunity.getBidDetailsTs().size() > 0) {
					for (int bid = 0; bid < opportunity.getBidDetailsTs().size(); bid++) {
						row = ExcelUtils.getRow(spreadSheet, (currentRow + bid - 1));
						if(opportunity.getBidDetailsTs().get(bid).getTargetBidSubmissionDate() != null) {
							row.createCell(colValue).setCellValue(opportunity.getBidDetailsTs().get(bid).getTargetBidSubmissionDate().toString());
						}
						else {
							row.createCell(colValue).setCellValue(Constants.SPACE);
						}
						row.getCell(colValue).setCellStyle(dataRowStyle);
					}
					colValue++;
				} else {
					row.createCell(colValue).setCellValue(Constants.SPACE);
					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
				}
			}
			if (actualBidSubDtFlag) {
				if (opportunity.getBidDetailsTs().size() > 0) {
					for (int bid = 0; bid < opportunity.getBidDetailsTs().size(); bid++) {
						row = ExcelUtils.getRow(spreadSheet, (currentRow + bid - 1));
						if(opportunity.getBidDetailsTs().get(bid).getActualBidSubmissionDate() != null) {
						row.createCell(colValue).setCellValue(opportunity.getBidDetailsTs().get(bid).getActualBidSubmissionDate().toString());
						}
						else {
							row.createCell(colValue).setCellValue(Constants.SPACE);
						}
						row.getCell(colValue).setCellStyle(dataRowStyle);
					}
					colValue++;
				} else {
					row.createCell(colValue).setCellValue(Constants.SPACE);
					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
				}
			}
			if (expDtOfOutcomeFlag) {
					if (opportunity.getBidDetailsTs().size() > 0) {
						for (int bid = 0; bid < opportunity.getBidDetailsTs().size(); bid++) {
							row = ExcelUtils.getRow(spreadSheet, (currentRow + bid - 1));
							if(opportunity.getBidDetailsTs().get(bid).getActualBidSubmissionDate() != null) {
								row.createCell(colValue).setCellValue(opportunity.getBidDetailsTs().get(bid).getActualBidSubmissionDate().toString());
							}
							else {
								row.createCell(colValue).setCellValue(Constants.SPACE);
							}
							row.getCell(colValue).setCellStyle(dataRowStyle);
						}
						colValue++;
					} else {
						row.createCell(colValue).setCellValue(Constants.SPACE);
						row.getCell(colValue).setCellStyle(dataRowStyle);
						colValue++;
					}
			}
			if (winProbFlag) {
					if (opportunity.getBidDetailsTs().size() > 0) {
						for (int bid = 0; bid < opportunity.getBidDetailsTs().size(); bid++) {
							row = ExcelUtils.getRow(spreadSheet, (currentRow + bid - 1));
							if(opportunity.getBidDetailsTs().get(bid).getWinProbability() != null) {
								row.createCell(colValue).setCellValue(opportunity.getBidDetailsTs().get(bid).getWinProbability());
							}
							else {
								row.createCell(colValue).setCellValue(Constants.SPACE);
							}
							row.getCell(colValue).setCellStyle(dataRowStyle);
						}
						colValue++;
					} else {
						row.createCell(colValue).setCellValue(Constants.SPACE);
						row.getCell(colValue).setCellStyle(dataRowStyle);
						colValue++;
					}
				}
				if (coreAttUsedForWinFlag) {
					if (opportunity.getBidDetailsTs().size() > 0) {
						for (int bid = 0; bid < opportunity.getBidDetailsTs().size(); bid++) {
							row = ExcelUtils.getRow(spreadSheet, (currentRow + bid - 1));
							if(opportunity.getBidDetailsTs().get(bid).getCoreAttributesUsedForWinning() != null) {
							row.createCell(colValue).setCellValue(opportunity.getBidDetailsTs().get(bid).getCoreAttributesUsedForWinning());
							}
							else {
								row.createCell(colValue).setCellValue(Constants.SPACE);
							}
							row.getCell(colValue).setCellStyle(dataRowStyle);
						}
						colValue++;
					} else {
						row.createCell(colValue).setCellValue(Constants.SPACE);
						row.getCell(colValue).setCellStyle(dataRowStyle);
						colValue++;
					}
				}
						
			if(opportunity.getBidDetailsTs().size() > 1)
			currentRow = currentRow + opportunity.getBidDetailsTs().size() -1;		
			headingColumn = false;
		}
		return currentRow;
	}

	private void createHeader(SXSSFRow headerRow, SXSSFSheet spreadSheet,
			int headerColumnValue, CellStyle headingStyle,
			Boolean isHeadingColumn, String field) {
		if(isHeadingColumn){
		headerRow.createCell(headerColumnValue).setCellValue(
				FieldsMap.fieldsMap.get(field));
		headerRow.getCell(headerColumnValue).setCellStyle(headingStyle);
		}
	}

	public InputStreamResource getBidDetailsReport(
			List<BidDetailsT> bidDetailsList, List<String> fields,
			List<String> currency) throws Exception {
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook.createSheet("cell types");
		SXSSFRow row = null;
		int currentRow = 0;
		CellStyle cellStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.REPORTHEADER);
		row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
		row.setRowStyle(cellStyle);
		CellStyle cellStyle1 = ExcelUtils.createRowStyle(workbook,
				ReportConstants.REPORTHEADER1);
		SXSSFRow row1 = (SXSSFRow) spreadSheet.createRow(1);
		row1.setRowStyle(cellStyle1);
		if (fields.size() == 0 && fields.isEmpty()) {
			CreateHeaderBidDetailsReportMandatoryFields(row, row1, spreadSheet,
					currency);
			currentRow = BidReportWithMandatoryFields(bidDetailsList,
					spreadSheet, currentRow, row, currency);
			currentRow++;
		} else {
			CreateHeaderBidDetailsReportOptionalFields(bidDetailsList, row,
					row1, fields, workbook, spreadSheet, currentRow, currency);
			currentRow = BidReportWithOptionalFields(bidDetailsList, workbook,
					spreadSheet, currentRow, fields, row, currency);
			currentRow++;
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

	public void CreateHeaderBidDetailsReportMandatoryFields(SXSSFRow row,
			SXSSFRow row1, SXSSFSheet spreadSheet, List<String> currency) {
		row.createCell(0).setCellValue(ReportConstants.OPPORTUNITYID);
//		spreadSheet.autoSizeColumn(0);
		row.createCell(1).setCellValue(ReportConstants.DISPLAYGEO);
//		spreadSheet.autoSizeColumn(1);
		row.createCell(2).setCellValue(ReportConstants.DISPLAYSERVICELINE);
//		spreadSheet.autoSizeColumn(2);
		row.createCell(3).setCellValue(ReportConstants.DISPLAYIOU);
//		spreadSheet.autoSizeColumn(3);
		row.createCell(4).setCellValue(ReportConstants.GROUPCUSTOMERNAME);
//		spreadSheet.autoSizeColumn(4);
		row.createCell(5).setCellValue(ReportConstants.SALESSTAGE);
//		spreadSheet.autoSizeColumn(5);
		row.createCell(6).setCellValue(ReportConstants.BIDREQUESTTYPE);
//		spreadSheet.autoSizeColumn(6);
		row.createCell(7).setCellValue(ReportConstants.BIDREQUESTRECEIVEDDATE);
//		spreadSheet.autoSizeColumn(7);
		row.createCell(8).setCellValue(ReportConstants.DIGITALDEALVALUE);
//		spreadSheet.autoSizeColumn(8);
		spreadSheet.addMergedRegion(new CellRangeAddress(0, 0, 8, 8 + currency
				.size() - 1));
		for (int i = 0; i < currency.size(); i++) {
			row1.createCell((8 + i)).setCellValue(currency.get(i));
		}
	}

	public void CreateHeaderBidDetailsReportOptionalFields(
			List<BidDetailsT> bidDetailsList, SXSSFRow row, SXSSFRow row1,
			List<String> fields, SXSSFWorkbook workbook, SXSSFSheet spreadSheet,
			int currentRow, List<String> currency) {
		/**
		 * This method creates default headers
		 */
		CreateHeaderBidDetailsReportMandatoryFields(row, row1, spreadSheet,
				currency);
		int colValue = 9;
		if (currency.size() > 1) {
			colValue = 10;
		}
		for (String field : fields) {
			Integer listCount = GetMaximumListCount.getMaxBidDetailsListCount(
					bidDetailsList, field);
			row.createCell(colValue).setCellValue(
					FieldsMap.fieldsMap.get(field));
//			spreadSheet.autoSizeColumn(colValue);
			spreadSheet.addMergedRegion(new CellRangeAddress(0, 0, colValue,
					colValue + listCount - 1));
			for (int i = 0; i < listCount; i++) {
				row1.createCell(colValue + i).setCellValue(
						FieldsMap.childMap.get(field));
//				spreadSheet.autoSizeColumn(colValue + i);
			}
			colValue = colValue + listCount;
		}
	}

	public int BidReportWithMandatoryFields(List<BidDetailsT> bidDetailsList,
			SXSSFSheet spreadSheet, int currentRow, SXSSFRow row,
			List<String> currency) {
		for (BidDetailsT bidDetail : bidDetailsList) {
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow + 2);
			getBidDetailsReportMandatoryFields(spreadSheet, row, currency,
					bidDetail);
			currentRow++;
		}
		return currentRow;
	}

	public void getBidDetailsReportMandatoryFields(SXSSFSheet spreadSheet,
			SXSSFRow row, List<String> currency, BidDetailsT bidDetail) {
		int i = 0;
		row.createCell(0).setCellValue(bidDetail.getOpportunityId());
		row.createCell(1).setCellValue(
				bidDetail.getOpportunityT().getCustomerMasterT()
						.getGeographyMappingT().getDisplayGeography());
		for (OpportunitySubSpLinkT opportunitySubSpLinkT : bidDetail
				.getOpportunityT().getOpportunitySubSpLinkTs()) {
			row.createCell(2).setCellValue(
					opportunitySubSpLinkT.getSubSpMappingT().getDisplaySubSp());
		}
		row.createCell(3).setCellValue(
				bidDetail.getOpportunityT().getCustomerMasterT()
						.getIouCustomerMappingT().getDisplayIou());
		row.createCell(4).setCellValue(
				bidDetail.getOpportunityT().getCustomerMasterT()
						.getGroupCustomerName());
		row.createCell(5).setCellValue(
				bidDetail.getOpportunityT().getSalesStageCode());
		row.createCell(6).setCellValue(bidDetail.getBidRequestType());
		if(bidDetail.getBidRequestReceiveDate() != null){
		row.createCell(7).setCellValue(
				bidDetail.getBidRequestReceiveDate().toString());
		}
		for (OpportunityDealValue opportunityDealValue : bidDetail
				.getOpportunityT().getOpportunityDealValues()) {
			BigDecimal dealValue = opportunityDealValue.getDigitalDealValue();
			if (dealValue != null) {
				row.createCell(8 + i).setCellValue(
						opportunityDealValue.getDigitalDealValue()
								.doubleValue());
				i++;
			}
		}
	}

	public int BidReportWithOptionalFields(List<BidDetailsT> bidDetailsList,
			SXSSFWorkbook workbook, SXSSFSheet spreadSheet, int currentRow,
			List<String> fields, SXSSFRow row, List<String> currency) {
		CellStyle cellStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.DATAROW);
		currentRow = currentRow + 2;
		for (BidDetailsT bidDetail : bidDetailsList) {
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow++);
			row.setRowStyle(cellStyle);
			getBidDetailsReportMandatoryFields(spreadSheet, row, currency,
					bidDetail);
			int colValue = 9;
			if (currency.size() > 1) {
				colValue = 10;
			}
			for (String field : fields) {
				switch (field) {
				case ReportConstants.IOU:
					row.createCell(colValue).setCellValue(
							bidDetail.getOpportunityT().getCustomerMasterT()
									.getIouCustomerMappingT().getIou());
//					spreadSheet.autoSizeColumn(colValue);
					colValue++;
					break;
				case ReportConstants.GEOGRAPHY:
					row.createCell(colValue).setCellValue(
							bidDetail.getOpportunityT()
									.getGeographyCountryMappingT()
									.getGeography());
//					spreadSheet.autoSizeColumn(colValue);
					colValue++;
					break;
				case ReportConstants.SUBSP:
					for (OpportunitySubSpLinkT opportunitySubSpLinkT : bidDetail
							.getOpportunityT().getOpportunitySubSpLinkTs()) {
						row.createCell(colValue).setCellValue(
								opportunitySubSpLinkT.getSubSpMappingT()
										.getSubSp());
//						spreadSheet.autoSizeColumn(colValue);
						colValue++;
					}
					break;
				case ReportConstants.COUNTRY:
					row.createCell(colValue)
							.setCellValue(
									bidDetail.getOpportunityT()
											.getGeographyCountryMappingT()
											.getCountry());
					colValue++;
					break;
				case ReportConstants.CRMID:
					row.createCell(colValue).setCellValue(
							bidDetail.getOpportunityT().getCrmId());
//					spreadSheet.autoSizeColumn(colValue);
					colValue++;
					break;
				case ReportConstants.NEWLOGO:
					row.createCell(colValue).setCellValue(
							bidDetail.getOpportunityT().getNewLogo());
					colValue++;
					break;
				case ReportConstants.OPPORTUNITYNAME:
					row.createCell(colValue).setCellValue(
							bidDetail.getOpportunityT().getOpportunityName());
					colValue++;
					break;
				case ReportConstants.TCSACCOUNTCONTACT:
					Integer maxTcsAccountContactlistCount = GetMaximumListCount
							.getMaxBidDetailsListCount(bidDetailsList,
									ReportConstants.TCSACCOUNTCONTACT);
					int i = 0;
					for (OpportunityTcsAccountContactLinkT opportunityTcsAccountContactLinkT : bidDetail
							.getOpportunityT()
							.getOpportunityTcsAccountContactLinkTs()) {
						row.createCell(colValue + i).setCellValue(
								opportunityTcsAccountContactLinkT.getContactT()
										.getContactName());
//						spreadSheet.autoSizeColumn(colValue + i);
						i++;
					}
					colValue = colValue + maxTcsAccountContactlistCount;
					break;
				case ReportConstants.COMPETITORS:
					Integer maxCompetitorsCount = GetMaximumListCount
							.getMaxBidDetailsListCount(bidDetailsList,
									ReportConstants.COMPETITORS);
					int c = 0;
					for (OpportunityCompetitorLinkT opportunityCompetitorLinkT : bidDetail
							.getOpportunityT().getOpportunityCompetitorLinkTs()) {
						row.createCell(colValue + c).setCellValue(
								opportunityCompetitorLinkT.getCompetitorName());
//						spreadSheet.autoSizeColumn(colValue + c);
						c++;
					}
					colValue = colValue + maxCompetitorsCount;
					break;
				case ReportConstants.COREATTRIBUTESUSEDFORWINNING:
					row.createCell(colValue).setCellValue(
							bidDetail.getCoreAttributesUsedForWinning());
					colValue++;
					break;
				case ReportConstants.BIDID:
					row.createCell(colValue).setCellValue(bidDetail.getBidId());
					colValue++;
					break;
				case ReportConstants.BIDOFFICEGROUPOWNER:
					Integer maxBidOfficeGroupOwnerListCount = GetMaximumListCount
							.getMaxBidDetailsListCount(bidDetailsList,
									ReportConstants.BIDOFFICEGROUPOWNER);
					int b = 0;
					for (BidOfficeGroupOwnerLinkT bidOfficeGroupOwnerLinkT : bidDetail
							.getBidOfficeGroupOwnerLinkTs()) {
						UserT userT = userRepository
								.findByUserId(bidOfficeGroupOwnerLinkT
										.getBidOfficeGroupOwner());
						row.createCell(colValue + b).setCellValue(
								userT.getUserName());
//						spreadSheet.autoSizeColumn(colValue + b);
						b++;
					}
					colValue = colValue + maxBidOfficeGroupOwnerListCount;
					break;
				case ReportConstants.TARGETBIDSUBMISSIONDATE:
					if(bidDetail.getTargetBidSubmissionDate() != null){
					row.createCell(colValue).setCellValue(
							bidDetail.getTargetBidSubmissionDate().toString());
					}
					colValue++;
					break;
				case ReportConstants.ACTUALBIDSUBMISSIONDATE:
					if(bidDetail.getActualBidSubmissionDate() != null){
					row.createCell(colValue).setCellValue(
							bidDetail.getActualBidSubmissionDate().toString());
					}
					colValue++;
					break;
				case ReportConstants.EXPECTEDDATEOFOUTCOME:
					if(bidDetail.getExpectedDateOfOutcome() != null){
					row.createCell(colValue).setCellValue(
							bidDetail.getExpectedDateOfOutcome().toString());
					}
					colValue++;
					break;
				default:
					break;
				}
				currentRow = currentRow + 0;
			}
		}
		return currentRow;
	}

	// ///// my codeing
	public Map<String, List<OpportunitySummaryValue>> addObjectToMap(
			Object[] opportunity, String quarterName,
			Map<String, List<OpportunitySummaryValue>> oppSummaryValueListMap) {

		List<OpportunitySummaryValue> oppSummaryValueList = new ArrayList<OpportunitySummaryValue>();
		OpportunitySummaryValue oppSummaryValue = new OpportunitySummaryValue();
		oppSummaryValue.setTitle(quarterName);
		oppSummaryValue.setCount((BigInteger) opportunity[1]);
		oppSummaryValue.setBidValue((BigDecimal) opportunity[2]);
		oppSummaryValueList.add(oppSummaryValue);
		if(opportunity[0] != null){
		if (oppSummaryValueListMap.containsKey(opportunity[0].toString())) {
			List<OpportunitySummaryValue> oppSummaryValueMap = oppSummaryValueListMap
					.get(opportunity[0].toString());
			for (OpportunitySummaryValue samp : oppSummaryValueMap) {
				oppSummaryValueList.add(samp);
			}
			oppSummaryValueListMap.put(opportunity[0].toString(),
					oppSummaryValueList);

		} else {
			oppSummaryValueListMap.put(opportunity[0].toString(),
					oppSummaryValueList);

		}
		}
		return oppSummaryValueListMap;
	}

	public List<ReportSummaryOpportunity> getServiceLineForPipelineAnticipating(
			List<String> currency, List<String> geography,
			List<String> country, List<String> iou, List<String> serviceLines,
			List<Integer> salesStageList, List<String> userIds, String userId, String userGroup)
			throws Exception {
		List<ReportSummaryOpportunity> reportSummaryOpportunityList = new ArrayList<ReportSummaryOpportunity>();
		List<Integer> salesStageAnticipating = new ArrayList<Integer>();
		List<Integer> salesStagePipeline = new ArrayList<Integer>();
		List<String> geoList = new ArrayList<String>();
		List<String> iouList = new ArrayList<String>();
		List<String> countryList = new ArrayList<String>();
		List<String> serviceLinesList = new ArrayList<String>();
		addItemToListGeo(geography,geoList);
		addItemToList(iou,iouList);
		addItemToList(country,countryList);
		addItemToList(serviceLines,serviceLinesList);
		for (int i = 0; i < salesStageList.size(); i++) {
			if (salesStageList.get(i) >= 0 && salesStageList.get(i) < 4) {
				salesStageAnticipating.add(salesStageList.get(i));
			} else if (salesStageList.get(i) > 3 && salesStageList.get(i) < 9) {
				salesStagePipeline.add(salesStageList.get(i));
			}
		}
		if (!salesStagePipeline.isEmpty()) {
			BigInteger totalCount = BigInteger.ZERO;
			BigDecimal totalBidValue = new BigDecimal(0);
			BigDecimal totalBidValueUsd = new BigDecimal(0);
			List<Object[]> opportunityList = new ArrayList<Object[]>();
			ReportSummaryOpportunity reportSummaryOpportunity = new ReportSummaryOpportunity();
			List<OpportunitySummaryValue> pipelineOpportunitySummaryValueList = new ArrayList<OpportunitySummaryValue>();
			switch (userGroup) {
			case ReportConstants.BDM:
				opportunityList = opportunityRepository.findPipelineSummaryServiceLineByRole(salesStagePipeline, userIds, geoList, countryList, iouList, serviceLinesList);
				break;
			case ReportConstants.BDMSUPERVISOR:
				opportunityList = opportunityRepository.findPipelineSummaryServiceLineByRole(salesStagePipeline, userIds, geoList, countryList, iouList, serviceLinesList);
				break;
			default:
					if(geography.contains("All") && (iou.contains("All") && serviceLines.contains("All")) && country.contains("All")){
						String queryString = reportsService.getPipelineAnticipatingOppServiceLineSummaryQueryString(userId,salesStagePipeline);
						Query opportunitySummaryReportQuery = entityManager.createNativeQuery(queryString);
						opportunityList = opportunitySummaryReportQuery.getResultList();
					} else {
						opportunityList = opportunityRepository.findPipelineSummaryServiceLine(geoList, countryList, iouList, serviceLinesList, 
								salesStagePipeline);
					}
				break;
			}
			if (opportunityList.size() > 0) {
				for (Object[] opportunity : opportunityList) {
					OpportunitySummaryValue opportunitySummaryValue = new OpportunitySummaryValue();
					if(opportunity[0]!=null){
					opportunitySummaryValue.setTitle(opportunity[0].toString());
					}
					opportunitySummaryValue
							.setCount((BigInteger) opportunity[1]);
					opportunitySummaryValue
							.setBidValue((BigDecimal) opportunity[2]);
					pipelineOpportunitySummaryValueList
							.add(opportunitySummaryValue);
					totalCount = totalCount.add((BigInteger) opportunity[1]);
					totalBidValue = totalBidValue
							.add((BigDecimal) opportunity[2]);
					totalBidValueUsd = totalBidValueUsd
							.add((BigDecimal) (((BigDecimal) opportunity[2])));
				}

				reportSummaryOpportunity.setTotalCount(totalCount);
				reportSummaryOpportunity
						.setTotalBidValueFirstCurrency(totalBidValue);
				reportSummaryOpportunity
						.setTotalBidValueSecondCurrency(totalBidValueUsd);
				reportSummaryOpportunity
						.setOpportunitySummaryValueList(pipelineOpportunitySummaryValueList);
				reportSummaryOpportunity
						.setSalesStageDescription(ReportConstants.PIPELINE);
				reportSummaryOpportunityList.add(reportSummaryOpportunity);
			}

		}
		if (!salesStageAnticipating.isEmpty()) {
			BigInteger totalCount = BigInteger.ZERO;
			BigDecimal totalBidValue = new BigDecimal(0);
			BigDecimal totalBidValueUsd = new BigDecimal(0);
			List<Object[]> opportunityList = new ArrayList<Object[]>();
			ReportSummaryOpportunity reportSummaryOpportunity = new ReportSummaryOpportunity();
			List<OpportunitySummaryValue> anticipatingOpportunitySummaryValueList = new ArrayList<OpportunitySummaryValue>();
			switch (userGroup) {
			case ReportConstants.BDM:
				opportunityList = opportunityRepository.findPipelineSummaryServiceLineByRole(salesStageAnticipating, userIds, geoList, countryList, iouList, serviceLinesList);
				break;
			case ReportConstants.BDMSUPERVISOR:
				opportunityList = opportunityRepository.findPipelineSummaryServiceLineByRole(salesStageAnticipating, userIds, geoList, countryList, iouList, serviceLinesList);
				break;
			default:
					if(geography.contains("All") && (iou.contains("All") && serviceLines.contains("All")) && country.contains("All")){
						String queryString = reportsService.getPipelineAnticipatingOppServiceLineSummaryQueryString(userId,salesStageAnticipating);
						Query opportunitySummaryReportQuery = entityManager.createNativeQuery(queryString);
						opportunityList = opportunitySummaryReportQuery.getResultList();
					} else {
						opportunityList = opportunityRepository.findPipelineSummaryServiceLine(geoList, countryList, iouList, serviceLinesList, 
								salesStageAnticipating);
					}
				break;
			}
//			opportunityList.addAll(opportunityRepository
//					.findPipelineSummaryServiceLine(geography, country, iou,
//							serviceLines, salesStageAnticipating, userIds));
			if (opportunityList.size() > 0) {
				for (Object[] opportunity : opportunityList) {
					OpportunitySummaryValue opportunitySummaryValue = new OpportunitySummaryValue();
					if(opportunity[0]!=null){
					opportunitySummaryValue.setTitle(opportunity[0].toString());
					}
					opportunitySummaryValue
							.setCount((BigInteger) opportunity[1]);
					opportunitySummaryValue
							.setBidValue((BigDecimal) opportunity[2]);
					anticipatingOpportunitySummaryValueList
							.add(opportunitySummaryValue);
					totalCount = totalCount.add((BigInteger) opportunity[1]);
					totalBidValue = totalBidValue
							.add((BigDecimal) opportunity[2]);
					totalBidValueUsd = totalBidValueUsd
							.add((BigDecimal) opportunity[2]);
				}

				reportSummaryOpportunity.setTotalCount(totalCount);
				reportSummaryOpportunity
						.setTotalBidValueFirstCurrency(totalBidValue);
				reportSummaryOpportunity
						.setTotalBidValueSecondCurrency(totalBidValueUsd);
				reportSummaryOpportunity
						.setOpportunitySummaryValueList(anticipatingOpportunitySummaryValueList);
				reportSummaryOpportunity
						.setSalesStageDescription("Anticipating");
				reportSummaryOpportunityList.add(reportSummaryOpportunity);
			}

		}
		return reportSummaryOpportunityList;
	}

	public List<ReportSummaryOpportunity> getPipelineAnticipatingOpportunities(
			List<Object[]> opportunityList, int salesStageCode,
			Boolean isDistinctIou) throws DestinationException {

		BigInteger totalCount = BigInteger.ZERO;
		BigDecimal totalBidValue = new BigDecimal(0);
		BigDecimal totalBidValueUsd = new BigDecimal(0);
		List<ReportSummaryOpportunity> reportSummaryOppList = new ArrayList<ReportSummaryOpportunity>();
		Map<String, List<OpportunitySummaryValue>> oppSummaryMap = new TreeMap<String, List<OpportunitySummaryValue>>();
		if (opportunityList.size() > 0) {
			for (Object[] opportunity : opportunityList) {
				List<OpportunitySummaryValue> opportunitySummaryValueList = new ArrayList<OpportunitySummaryValue>();
				OpportunitySummaryValue opportunitySummaryValue = new OpportunitySummaryValue();
				opportunitySummaryValue.setCount((BigInteger) opportunity[1]);
				if(opportunity[2] != null){
				opportunitySummaryValue.setTitle(opportunity[2].toString());
				}
				opportunitySummaryValue
						.setBidValue((BigDecimal) opportunity[3]);
				opportunitySummaryValueList.add(opportunitySummaryValue);
				// /
				totalCount = totalCount.add((BigInteger) opportunity[1]);
				totalBidValue = totalBidValue.add((BigDecimal) opportunity[3]);
				totalBidValueUsd = totalBidValueUsd
						.add((BigDecimal) opportunity[3]);
				if(opportunity[0] != null) {
				if (oppSummaryMap.containsKey(opportunity[0].toString())) {
					List<OpportunitySummaryValue> opportunitySummaryListMap = oppSummaryMap
							.get(opportunity[0].toString());
					for (OpportunitySummaryValue opportunitySummaryValueMap : opportunitySummaryValueList) {
						opportunitySummaryListMap
								.add(opportunitySummaryValueMap);
					}
					oppSummaryMap.put(opportunity[0].toString(),
							opportunitySummaryListMap);
				} else {
					oppSummaryMap.put(opportunity[0].toString(),
							opportunitySummaryValueList);

				}
				}
			}

		}
		for (Map.Entry<String, List<OpportunitySummaryValue>> entry : oppSummaryMap
				.entrySet()) {
			ReportSummaryOpportunity reportSummaryOpportunity = new ReportSummaryOpportunity();
			if (isDistinctIou) {
				reportSummaryOpportunity.setIou("distinctIou");
			}
			reportSummaryOpportunity.setSalesStageCode(salesStageCode);
			reportSummaryOpportunity
					.setSalesStageDescription((salesStageMappingRepository
							.findBySalesStageCode(salesStageCode))
							.getSalesStageDescription());
			reportSummaryOpportunity.setSalesStageDescription(entry.getKey());
			reportSummaryOpportunity.setOpportunitySummaryValueList(entry
					.getValue());
			reportSummaryOpportunity.setTotalCount(totalCount);
			reportSummaryOpportunity
					.setTotalBidValueFirstCurrency(totalBidValue);
			reportSummaryOpportunity
					.setTotalBidValueSecondCurrency(totalBidValueUsd);
			reportSummaryOppList.add(reportSummaryOpportunity);
		}
		return reportSummaryOppList;

	}

	public List<ReportSummaryOpportunity> getSummaryReportWinLoss(String month,
			String year, String quarter, int salesStageCode,
			List<Object[]> opportunityList, Map<String, Date> fromDateMap,
			Map<String, List<Object[]>> objectListForSubCategory,
			String required) throws DestinationException {
		List<ReportSummaryOpportunity> reportSummaryOppList = new ArrayList<ReportSummaryOpportunity>();
		if (!month.isEmpty()) {
			List<Object[]> opportunityListMonth = objectListForSubCategory
					.get(month);
			if (opportunityListMonth.size() > 0) {
				for (Object[] opportunity : opportunityListMonth) {
					List<OpportunitySummaryValue> summaryValueList = new ArrayList<OpportunitySummaryValue>();
					ReportSummaryOpportunity reportSummaryOpportunity = new ReportSummaryOpportunity();
					OpportunitySummaryValue opportunitySummaryValue = new OpportunitySummaryValue();
					if(opportunity[0] != null){
					if (required.equals("geography"))
						reportSummaryOpportunity.setGeography(opportunity[0]
								.toString());
					else if (required.equals("serviceLine"))
						reportSummaryOpportunity.setSubSp(opportunity[0]
								.toString());
					else
						reportSummaryOpportunity.setIou(opportunity[0]
								.toString());
					}
					reportSummaryOpportunity.setSalesStageCode(salesStageCode);
					reportSummaryOpportunity
							.setSalesStageDescription((salesStageMappingRepository
									.findBySalesStageCode(salesStageCode))
									.getSalesStageDescription());
					opportunitySummaryValue.setTitle(month);
					opportunitySummaryValue
							.setCount((BigInteger) opportunity[1]);
					opportunitySummaryValue
							.setBidValue((BigDecimal) opportunity[2]);
					summaryValueList.add(opportunitySummaryValue);
					reportSummaryOpportunity
							.setOpportunitySummaryValueList(summaryValueList);
					reportSummaryOppList.add(reportSummaryOpportunity);
				}
			}

		} else if (!year.isEmpty() || !quarter.isEmpty()) {
			Map<String, List<OpportunitySummaryValue>> oppSummaryMap = new TreeMap<String, List<OpportunitySummaryValue>>();
			String subCategoryName = null;
			for (String subCategory : fromDateMap.keySet()) {
				opportunityList = objectListForSubCategory.get(subCategory);
				if (opportunityList.size() > 0) {

					for (Object[] opportunity : opportunityList) {
						if (!year.isEmpty()) {
							subCategoryName = subCategory.substring(0, 2)
									+ " - " + year.substring(3, year.length());
						} else {
							subCategoryName = subCategory;
						}
						oppSummaryMap = addObjectToMap(opportunity,
								subCategoryName, oppSummaryMap);
					}
				}
			}
			for (Map.Entry<String, List<OpportunitySummaryValue>> entry : oppSummaryMap
					.entrySet()) {
				BigInteger totalCount = BigInteger.ZERO;
				BigDecimal totalBidValue = new BigDecimal(0);
				BigDecimal totalBidValueUsd = new BigDecimal(0);
				ReportSummaryOpportunity reportSummaryOpp = new ReportSummaryOpportunity();
				if (required.equals("geography"))
					reportSummaryOpp.setGeography(entry.getKey());
				else if (required.equals("serviceLine"))
					reportSummaryOpp.setSubSp(entry.getKey());
				else
					reportSummaryOpp.setIou(entry.getKey());
				reportSummaryOpp
						.setSalesStageDescription((salesStageMappingRepository
								.findBySalesStageCode(salesStageCode))
								.getSalesStageDescription());
				reportSummaryOpp.setSalesStageCode(salesStageCode);
				reportSummaryOpp.setOpportunitySummaryValueList(entry
						.getValue());
				for (OpportunitySummaryValue oppSummaryValue : entry.getValue()) {
					totalCount = totalCount.add(oppSummaryValue.getCount());
					totalBidValue = totalBidValue.add(oppSummaryValue
							.getBidValue());
					totalBidValueUsd = totalBidValueUsd
							.add((BigDecimal) oppSummaryValue.getBidValue());
				}
				reportSummaryOpp.setTotalCount(totalCount);
				reportSummaryOpp.setTotalBidValueFirstCurrency(totalBidValue);
				reportSummaryOpp
						.setTotalBidValueSecondCurrency(totalBidValueUsd);
				reportSummaryOppList.add(reportSummaryOpp);
			}
		}
		return reportSummaryOppList;

	}

	public void buildExcelReport(
			Map<String, List<ReportSummaryOpportunity>> reportSummaryOpportunityListMap,
			String month, String year, String quarter, List<String> currency,
			List<String> geography, List<String> iou, SXSSFWorkbook workbook)
			throws Exception, DestinationException {

		logger.debug("Inside Report Service buildExcelReport method");
		Map<String, String> map = new LinkedHashMap<String, String>();
		CellStyle headingStyle = ExcelUtils.createRowStyle(workbook,
				"headingStyle");
		SXSSFSheet spreadsheet = null;
		SXSSFRow row;
		SXSSFCell cell;
		List<ReportSummaryOpportunity> serviceLineOpp = new ArrayList<ReportSummaryOpportunity>();
		List<ReportSummaryOpportunity> geoOpp = new ArrayList<ReportSummaryOpportunity>();
		List<ReportSummaryOpportunity> iouOpp = new ArrayList<ReportSummaryOpportunity>();
		if (reportSummaryOpportunityListMap
				.containsKey("pipelineAnticipatingGeography")) {
			getPipelineAnticipatingDetails(workbook,
					reportSummaryOpportunityListMap
							.get("pipelineAnticipatingGeography"), currency);
			reportSummaryOpportunityListMap
					.remove("pipelineAnticipatingGeography");
		}
		if (reportSummaryOpportunityListMap
				.containsKey("pipelineAnticipatingServiceLine")) {
			getPipelineAnticipatingServiceLines(workbook,
					reportSummaryOpportunityListMap
							.get("pipelineAnticipatingServiceLine"), currency);
			reportSummaryOpportunityListMap
					.remove("pipelineAnticipatingServiceLine");
		}
		
		if (reportSummaryOpportunityListMap
				.containsKey("pipelineAnticipatingIou")) {
			getPipelineAnticipatingDetails(workbook,
					reportSummaryOpportunityListMap
							.get("pipelineAnticipatingIou"), currency);
			reportSummaryOpportunityListMap.remove("pipelineAnticipatingIou");
		}

		for (Map.Entry<String, List<ReportSummaryOpportunity>> entry : reportSummaryOpportunityListMap
				.entrySet()) {
			serviceLineOpp.clear();
			geoOpp.clear();
			iouOpp.clear();
			for (ReportSummaryOpportunity repSummaryOpp : entry.getValue()) {
				if (!map.containsKey(repSummaryOpp.getSalesStageDescription())) {
					if (repSummaryOpp.getSalesStageCode() == 9) {
						spreadsheet = (SXSSFSheet) workbook
								.createSheet(ReportConstants.WINS);
						map.put(repSummaryOpp.getSalesStageDescription(),
								ReportConstants.WINS);
					} else {
						spreadsheet = (SXSSFSheet) workbook
								.createSheet(ReportConstants.LOSSES);
						map.put(repSummaryOpp.getSalesStageDescription(),
								ReportConstants.LOSSES);
					}
					row = (SXSSFRow) spreadsheet.createRow((short) 1);
					if (!quarter.isEmpty() && currency.size() > 1) {
						spreadsheet.addMergedRegion(new CellRangeAddress(1, 1,
								0, 12));
					} else if (!month.isEmpty() && currency.size() == 1) {
						spreadsheet.addMergedRegion(new CellRangeAddress(1, 1,
								0, 6));
					} else if ((!year.isEmpty() || entry.getKey()
							.contains("FY")) && currency.size() > 1) {
						spreadsheet.addMergedRegion(new CellRangeAddress(1, 1,
								0, 15));
					} else if ((!year.isEmpty() || entry.getKey()
							.contains("FY")) && currency.size() == 1) {
						spreadsheet.addMergedRegion(new CellRangeAddress(1, 1,
								0, 10));
					} else {
						spreadsheet.addMergedRegion(new CellRangeAddress(1, 1,
								0, 8));
					}
					cell = (SXSSFCell) row.createCell(0);
					cell.setCellStyle(headingStyle);
					cell.setCellValue(map.get(repSummaryOpp
							.getSalesStageDescription())
							+ "( "
							+ repSummaryOpp.getSalesStageDescription() + " )");
					row = (SXSSFRow) spreadsheet.createRow((short) 3);
					if (!quarter.isEmpty() && currency.size() > 1) {
						spreadsheet.addMergedRegion(new CellRangeAddress(
								3, 3, 0, 12));
					} else if (!quarter.isEmpty() && currency.size() == 1) {
						spreadsheet.addMergedRegion(new CellRangeAddress(
								3, 3, 0, 8));
					} else if (!year.isEmpty() && currency.size() > 1) {
						spreadsheet.addMergedRegion(new CellRangeAddress(
								3, 3, 0, 15));
					} else {
						spreadsheet.addMergedRegion(new CellRangeAddress(
								3, 3, 0, 10));
					}
					cell = (SXSSFCell) row.createCell(0);
					cell.setCellStyle(headingStyle);
					if (!quarter.isEmpty())
						cell.setCellValue(quarter);
					else
						cell.setCellValue(year);
				}
				if (!(repSummaryOpp.getSubSp() == null)) {
					serviceLineOpp.add(repSummaryOpp);
				} else if (!(repSummaryOpp.getGeography() == null)) {
					geoOpp.add(repSummaryOpp);
				} else {
					iouOpp.add(repSummaryOpp);
				}
			}
			Map<String,Integer> zeroOffesetRowMap = new TreeMap<String,Integer>();
			zeroOffesetRowMap.put("Wins", 0);
			zeroOffesetRowMap.put("Losses", 0);
			if (entry.getKey().equals("month")) {
				if(serviceLineOpp.size() > 0){
				serviceTypeDetailsMonthWise(workbook, serviceLineOpp, month,
						ReportConstants.OFFSETCOLUMNVALUE, currency);
				serviceTypeDetailsMonthWise(workbook, geoOpp, month,
						ReportConstants.OFFSETCOLUMNGEO, currency);
				serviceTypeDetailsMonthWise(workbook, iouOpp, month,
						ReportConstants.OFFSETCOLUMNIOU, currency);
				} else {
					serviceTypeDetailsMonthWise(workbook, geoOpp, month,
							ReportConstants.OFFSETCOLUMNVALUE, currency);
					serviceTypeDetailsMonthWise(workbook, iouOpp, month,
							ReportConstants.OFFSETCOLUMNGEO, currency);
				}
			} else if (!quarter.isEmpty()) {
				getServiceTypeDetails(workbook, serviceLineOpp,currency, quarter, year);
				getServiceTypeDetails(workbook, geoOpp, currency, quarter,year);
				getServiceTypeDetails(workbook, iouOpp, currency, quarter, year);
			} else {
				getServiceTypeDetails(workbook, serviceLineOpp,currency, quarter, entry.getKey());
				getServiceTypeDetails(workbook, geoOpp, currency, quarter,entry.getKey());
				getServiceTypeDetails(workbook, iouOpp, currency, quarter,entry.getKey());
			}
		}

	}

	public void getPipelineAnticipatingServiceLines(SXSSFWorkbook workbook,
			List<ReportSummaryOpportunity> reportSummaryOpportunityList,
			List<String> currency) throws IOException, DestinationException {
		logger.debug("Inside Report Service getPipelineAnticipatingServiceLines method");

		SXSSFRow row;
		SXSSFCell cell;
		int rowValue = 0;
		SXSSFSheet spreadsheet = null;
		CellStyle subHeadingStyle2 = ExcelUtils.createRowStyle(workbook,
				ReportConstants.SUBHEADINGSTYLE2);
		CellStyle subHeadingStyle3 = ExcelUtils.createRowStyle(workbook,
				ReportConstants.SUBHEADINGSTYLE3);
		CellStyle rowDataStyle = ExcelUtils.createRowStyle(workbook, "dataRow");
		if (reportSummaryOpportunityList.size() > 0) {
			for (ReportSummaryOpportunity repSummaryOpp : reportSummaryOpportunityList) {
				if (repSummaryOpp.getSalesStageDescription().equals(
						ReportConstants.PIPELINE)) {
					spreadsheet = (SXSSFSheet) workbook.getSheet(ReportConstants.PIPELINE);
				} else if (repSummaryOpp.getSalesStageDescription().equals(
						"Anticipating")) {
					spreadsheet = (SXSSFSheet) workbook.getSheet(ReportConstants.PROSPECTS);
				}
				rowValue = spreadsheet.getLastRowNum() + 2;
				row = (SXSSFRow) spreadsheet.createRow((short) rowValue++);
				if (!(currency.size() > 1)) {
					spreadsheet.addMergedRegion(new CellRangeAddress(
							rowValue - 1, rowValue - 1, 0, 2));
				} else {
					spreadsheet.addMergedRegion(new CellRangeAddress(
							rowValue - 1, rowValue - 1, 0, 3));
				}
				cell = (SXSSFCell) row.createCell(0);
				cell.setCellValue("SERVICE LINE WISE");
//				spreadsheet.autoSizeColumn(0);
				cell.setCellStyle(subHeadingStyle3);
				row = (SXSSFRow) spreadsheet.createRow((short) rowValue++);
				cell = (SXSSFCell) row.createCell(0);
//				spreadsheet.autoSizeColumn(0);
				cell.setCellValue("Service Line");
				cell.setCellStyle(subHeadingStyle2);
				cell = (SXSSFCell) row.createCell(1);
//				spreadsheet.autoSizeColumn(1);
				cell.setCellValue("# of bids");
				cell.setCellStyle(subHeadingStyle2);
				cell = (SXSSFCell) row.createCell(2);
//				spreadsheet.autoSizeColumn(2);
				cell.setCellValue("bid value(" + currency.get(0) + ")");
				cell.setCellStyle(subHeadingStyle2);
				if ((currency.size() > 1)) {
					cell = (SXSSFCell) row.createCell(3);
//					spreadsheet.autoSizeColumn(3);
					cell.setCellValue("bid value(" + currency.get(1) + ")");
					cell.setCellStyle(subHeadingStyle2);
				}
				for (OpportunitySummaryValue oppSummaryValue : repSummaryOpp
						.getOpportunitySummaryValueList()) {
					row = (SXSSFRow) spreadsheet.createRow((short) rowValue++);
					row.createCell(0).setCellValue(oppSummaryValue.getTitle());
					row.getCell(0).setCellStyle(rowDataStyle);
//					spreadsheet.autoSizeColumn(0);
					row.createCell(1).setCellValue(
							oppSummaryValue.getCount().intValue());
//					spreadsheet.autoSizeColumn(1);
					row.getCell(1).setCellStyle(rowDataStyle);
					row.createCell(2).setCellValue(
							(beaconConverterService.convert("INR", currency
									.get(0), oppSummaryValue.getBidValue()
									.doubleValue())).doubleValue());
//					spreadsheet.autoSizeColumn(2);
					row.getCell(2).setCellStyle(rowDataStyle);
					if ((currency.size() > 1)) {
						row.createCell(3).setCellValue(
								(beaconConverterService.convert("INR", currency
										.get(1), oppSummaryValue.getBidValue()
										.doubleValue())).doubleValue());
//						spreadsheet.autoSizeColumn(3);
						row.getCell(3).setCellStyle(rowDataStyle);
					}
				}
				row = (SXSSFRow) spreadsheet.createRow((short) rowValue);
				row.createCell(0).setCellValue("Total");
//				spreadsheet.autoSizeColumn(0);
				spreadsheet.getRow(rowValue).getCell(0)
						.setCellStyle(subHeadingStyle2);
				row.createCell(1).setCellValue(
						repSummaryOpp.getTotalCount().intValue());
//				spreadsheet.autoSizeColumn(1);
				spreadsheet.getRow(rowValue).getCell(1)
						.setCellStyle(subHeadingStyle2);
				row.createCell(2).setCellValue(
						(beaconConverterService.convert("INR", currency.get(0),
								repSummaryOpp.getTotalBidValueFirstCurrency()
										.doubleValue())).doubleValue());
//				spreadsheet.autoSizeColumn(2);
				spreadsheet.getRow(rowValue).getCell(2)
						.setCellStyle(subHeadingStyle2);
				if ((currency.size() > 1)) {
					row.createCell(3).setCellValue(
							(beaconConverterService.convert("INR", currency
									.get(1), repSummaryOpp
									.getTotalBidValueFirstCurrency()
									.doubleValue())).doubleValue());
//					spreadsheet.autoSizeColumn(3);
					spreadsheet.getRow(rowValue).getCell(3)
							.setCellStyle(subHeadingStyle2);
				}

			}
		}
	}

	private Map<String, List<String>> getSearchItemList(
			List<ReportSummaryOpportunity> reportSummaryOpportunityList) {
		logger.debug("Inside Report Service getSearchItemList Method");
		Map<String, List<String>> geoListMap = new TreeMap<String, List<String>>();
		List<String> pipelineGeoList = new ArrayList<String>();
		List<String> anticipatingeGeoList = new ArrayList<String>();
		if (reportSummaryOpportunityList.size() > 0) {
			for (ReportSummaryOpportunity repSummaryOpp : reportSummaryOpportunityList) {
				if (!repSummaryOpp.getSalesStageDescription().equals(
						ReportConstants.PIPELINE)
						&& !repSummaryOpp.getSalesStageDescription().equals(
								"Anticipating")) {
					if (repSummaryOpp.getSalesStageCode() > 3
							&& repSummaryOpp.getSalesStageCode() < 9) {
						for (OpportunitySummaryValue oppSummaryValue : repSummaryOpp
								.getOpportunitySummaryValueList()) {
							if (!pipelineGeoList.contains(oppSummaryValue
									.getTitle())) {
								pipelineGeoList.add(oppSummaryValue.getTitle());
								geoListMap.put(ReportConstants.PIPELINE,
										pipelineGeoList);
							}
						}
					} else if (repSummaryOpp.getSalesStageCode() < 4) {
						for (OpportunitySummaryValue oppSummaryValue : repSummaryOpp
								.getOpportunitySummaryValueList()) {
							if (!anticipatingeGeoList.contains(oppSummaryValue
									.getTitle())) {
								anticipatingeGeoList.add(oppSummaryValue
										.getTitle());
								geoListMap.put("anticipating",
										anticipatingeGeoList);
							}
						}
					}
				}
			}
		}
		return geoListMap;
	}

	public void serviceTypeDetailsMonthWise(SXSSFWorkbook workbook,
			List<ReportSummaryOpportunity> repSummaryOpportunityList,
			String month, int offsetValue, List<String> currency)
			throws DestinationException {
		logger.debug("Inside Report Service serviceTypeDetailsMonthWise method");

		int rowValue = 3;

		SXSSFRow row = null;
		SXSSFCell cell = null;
		Boolean isHeading = false;
		SXSSFSheet spreadsheet = null;
		String previousSpreadSheetName = null;
		Map<String, String> sheetMap = new LinkedHashMap<String, String>();
		CellStyle rowDataStyle = ExcelUtils.createRowStyle(workbook, "dataRow");
		CellStyle headingRow = ExcelUtils
				.createRowStyle(workbook, "headingRow");
		CellStyle headingStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.HEADINGSTYLE);
		CellStyle subHeadingStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.SUBHEADINGSTYLE);
		CellStyle subHeadingStyle2 = ExcelUtils.createRowStyle(workbook,
				ReportConstants.SUBHEADINGSTYLE2);

		if ((currency.size() > 1) && offsetValue > 0 && offsetValue == 4) {
			offsetValue = 5;
		} else if ((currency.size() > 1) && offsetValue > 0 && offsetValue == 8) {
			offsetValue = 10;
		}
		if (repSummaryOpportunityList.size() > 0) {
			for (ReportSummaryOpportunity repSummaryOpp : repSummaryOpportunityList) {
				if (!sheetMap.containsKey(repSummaryOpp
						.getSalesStageDescription())) {
					isHeading = true;
					if (repSummaryOpp.getSalesStageCode() == 9) {
						previousSpreadSheetName = ReportConstants.LOSSES;
						spreadsheet = (SXSSFSheet) workbook.getSheet(ReportConstants.WINS);
						sheetMap.put(repSummaryOpp.getSalesStageDescription(),
								ReportConstants.WINS);
					} else {
						previousSpreadSheetName = ReportConstants.WINS;
						spreadsheet = (SXSSFSheet) workbook.getSheet(ReportConstants.LOSSES);
						sheetMap.put(repSummaryOpp.getSalesStageDescription(),
								ReportConstants.LOSSES);
					}

					if ((workbook.getSheet(previousSpreadSheetName) != null)
							&& workbook.getSheet(previousSpreadSheetName)
									.getLastRowNum() > 5) {
						if (workbook.getSheet(previousSpreadSheetName).getRow(
								rowValue) == null)
							row = (SXSSFRow) workbook.getSheet(previousSpreadSheetName)
									.createRow((short) rowValue);
						else
							row = (SXSSFRow) workbook.getSheet(previousSpreadSheetName)
									.getRow(rowValue);

						row.createCell(0 + offsetValue).setCellValue("Total");
						row.getCell(0 + offsetValue).setCellStyle(
								subHeadingStyle2);
						row.createCell(1 + offsetValue).setCellFormula(
								"SUM(" + ((char) (66 + offsetValue)) + "7:"
										+ ((char) (66 + offsetValue))
										+ (rowValue) + ")");
						row.getCell(1 + offsetValue).setCellStyle(
								subHeadingStyle2);
						row.createCell(2 + offsetValue).setCellFormula(
								"SUM(" + ((char) (67 + offsetValue)) + "7:"
										+ ((char) (67 + offsetValue))
										+ (rowValue) + ")");
						row.getCell(2 + offsetValue).setCellStyle(
								subHeadingStyle2);
						if ((currency.size() > 1)) {
							row.getCell(3 + offsetValue).setCellFormula(
									"SUM(" + ((char) (68 + offsetValue)) + "7:"
											+ ((char) (68 + offsetValue))
											+ (rowValue) + ")");
							row.getCell(3 + offsetValue).setCellStyle(
									subHeadingStyle2);
						}
					}
					rowValue = 3;
				}
				if (isHeading) {
					if (spreadsheet.getRow(2) == null)
						row = (SXSSFRow) spreadsheet.createRow((short) 2);
					else
						row = (SXSSFRow) spreadsheet.getRow((short) 2);
					if (currency.size() == 1) {
						spreadsheet.addMergedRegion(new CellRangeAddress(2, 2,
								0, 6));
					} else {
						spreadsheet.addMergedRegion(new CellRangeAddress(2, 2,
								0, 8));
					}
					cell = (SXSSFCell) row.createCell(0);
					cell.setCellStyle(headingStyle);
					cell.setCellValue(month);
					if (spreadsheet.getRow(rowValue) == null)
						row = (SXSSFRow) spreadsheet.createRow((short) rowValue++);
					else
						row = (SXSSFRow) spreadsheet.getRow((short) rowValue++);
					if ((currency.size() > 1)) {
						spreadsheet.addMergedRegion(new CellRangeAddress(
								rowValue - 1, rowValue - 1, 0 + offsetValue,
								3 + offsetValue));
					} else {
						spreadsheet.addMergedRegion(new CellRangeAddress(
								rowValue - 1, rowValue - 1, 0 + offsetValue,
								2 + offsetValue));
					}
					cell = (SXSSFCell) row.createCell(0 + offsetValue);
					if (!(repSummaryOpp.getSubSp() == null))
						cell.setCellValue("Service Line Wise");
					else if (!(repSummaryOpp.getGeography() == null)) {
						cell.setCellValue("Geography Wise");
					} else {
						cell.setCellValue("Iou	 Wise");
					}
					cell.setCellStyle(subHeadingStyle);
					if (spreadsheet.getRow(rowValue) == null)
						row = (SXSSFRow) spreadsheet.createRow((short) rowValue++);
					else
						row = (SXSSFRow) spreadsheet.getRow((short) rowValue++);

					if ((currency.size() > 1)) {
						spreadsheet.addMergedRegion(new CellRangeAddress(
								rowValue - 1, rowValue - 1, 1 + offsetValue,
								3 + offsetValue));
					} else {
						spreadsheet.addMergedRegion(new CellRangeAddress(
								rowValue - 1, rowValue - 1, 1 + offsetValue,
								2 + offsetValue));
					}
					cell = (SXSSFCell) row.createCell(1 + offsetValue);
					cell.setCellValue(month.substring(0, 3));
					cell.setCellStyle(headingRow);
					// cell = (SXSSFCell) row.createCell(5);
					if (spreadsheet.getRow(rowValue) == null)
						row = (SXSSFRow) spreadsheet.createRow((short) rowValue++);
					else
						row = (SXSSFRow) spreadsheet.getRow((short) rowValue++);
					row.createCell(0 + offsetValue)
							.setCellValue("Service Line");
//					spreadsheet.autoSizeColumn(0 + offsetValue);
					row.getCell(0 + offsetValue).setCellStyle(subHeadingStyle2);
					if(repSummaryOpp.getSalesStageCode() == 9){
					row.createCell(1 + offsetValue).setCellValue("# of wins");
					} else {
						row.createCell(1 + offsetValue).setCellValue("# of Losses");
					}
//					spreadsheet.autoSizeColumn(1 + offsetValue);
					row.getCell(1 + offsetValue).setCellStyle(subHeadingStyle2);
					row.createCell(2 + offsetValue).setCellValue(
							"total value(" + currency.get(0) + ")");
//					spreadsheet.autoSizeColumn(2 + offsetValue);
					row.getCell(2 + offsetValue).setCellStyle(subHeadingStyle2);
					isHeading = false;
					if ((currency.size() > 1)) {
						row.createCell(3 + offsetValue).setCellValue(
								"total value(" + currency.get(1) + ")");
//						spreadsheet.autoSizeColumn(3 + offsetValue);
						row.getCell(3 + offsetValue).setCellStyle(
								subHeadingStyle2);
					}
				}
				for (OpportunitySummaryValue oppSummaryValue : repSummaryOpp
						.getOpportunitySummaryValueList()) {
					if(oppSummaryValue.getCount() == BigInteger.ZERO){
						continue;
					}
					if (spreadsheet.getRow(rowValue) == null)
						row = (SXSSFRow) spreadsheet.createRow((short) rowValue++);
					else
						row = (SXSSFRow) spreadsheet.getRow((short) rowValue++);
					if (!(repSummaryOpp.getSubSp() == null)) {
						row.createCell(0 + offsetValue).setCellValue(
								repSummaryOpp.getSubSp());
					} else if (!(repSummaryOpp.getGeography() == null)) {
						row.createCell(0 + offsetValue).setCellValue(
								repSummaryOpp.getGeography());
					} else {
						row.createCell(0 + offsetValue).setCellValue(
								repSummaryOpp.getIou());
					}
					row.getCell(0 + offsetValue).setCellStyle(rowDataStyle);
//					spreadsheet.autoSizeColumn(0 + offsetValue);
					row.createCell(1 + offsetValue).setCellValue(
							oppSummaryValue.getCount().intValue());
//					spreadsheet.autoSizeColumn(1 + offsetValue);
					row.getCell(1 + offsetValue).setCellStyle(rowDataStyle);
					row.createCell(2 + offsetValue).setCellValue(
							beaconConverterService
									.convert(
											"INR",
											currency.get(0),
											oppSummaryValue.getBidValue()
													.doubleValue())
									.doubleValue());
//					spreadsheet.autoSizeColumn(2 + offsetValue);
					row.getCell(2 + offsetValue).setCellStyle(rowDataStyle);
					if ((currency.size() > 1)) {
						row.createCell(3 + offsetValue).setCellValue(
								beaconConverterService.convert(
										"INR",
										currency.get(1),
										oppSummaryValue.getBidValue()
												.doubleValue()).doubleValue());
//						spreadsheet.autoSizeColumn(3 + offsetValue);
						row.getCell(3 + offsetValue).setCellStyle(rowDataStyle);
					}
				}
			}

			if (spreadsheet.getRow(rowValue) == null)
				row = (SXSSFRow) spreadsheet.createRow((short) rowValue++);
			else
				row = (SXSSFRow) spreadsheet.getRow((short) rowValue++);

			row.createCell(0 + offsetValue).setCellValue("Total");
//			spreadsheet.autoSizeColumn(0 + offsetValue);
			row.getCell(0 + offsetValue).setCellStyle(subHeadingStyle2);
			row.createCell(1 + offsetValue).setCellFormula(
					"SUM(" + ((char) (66 + offsetValue)) + "7:"
							+ ((char) (66 + offsetValue)) + (rowValue - 1)
							+ ")");
//			spreadsheet.autoSizeColumn(1 + offsetValue);
			row.getCell(1 + offsetValue).setCellStyle(subHeadingStyle2);
			row.createCell(2 + offsetValue).setCellFormula(
					"SUM(" + ((char) (67 + offsetValue)) + "7:"
							+ ((char) (67 + offsetValue)) + (rowValue - 1)
							+ ")");
//			spreadsheet.autoSizeColumn(2 + offsetValue);
			row.getCell(2 + offsetValue).setCellStyle(subHeadingStyle2);
			if ((currency.size() > 1)) {
				row.createCell(3 + offsetValue).setCellFormula(
						"SUM(" + ((char) (68 + offsetValue)) + "7:"
								+ ((char) (68 + offsetValue)) + (rowValue - 1)
								+ ")");
				row.getCell(3 + offsetValue).setCellStyle(subHeadingStyle2);
//				spreadsheet.autoSizeColumn(3 + offsetValue);
			}
		}
	}

	public void getServiceTypeDetails(SXSSFWorkbook workbook,
			List<ReportSummaryOpportunity> repSummaryOpportunityList,
			List<String> currency, String quarter, String year) throws DestinationException {
		logger.debug("Inside Report Service getServiceTypeDetails method");

		CellStyle subHeadingStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.SUBHEADINGSTYLE);
		CellStyle subHeadingStyle2 = ExcelUtils.createRowStyle(workbook,
				ReportConstants.SUBHEADINGSTYLE2);
		CellStyle rowDataStyle = ExcelUtils.createRowStyle(workbook, ReportConstants.DATAROW);
		CellStyle headingRow = ExcelUtils
				.createRowStyle(workbook, ReportConstants.HEADINGROW);
		SXSSFSheet spreadsheet = null;
		int rowValue = 0;
		SXSSFRow row = null;
		SXSSFCell cell = null;
		int bothCurrencyColumn = 0;
		int[] currencyOffsetValue = new int[10];
		int totalHeadingRow = 0;
		int lastRow = 0;
		int lastColumn = 0;
		int lastCell = 0;
		int columnSize = 5;
		int columnStyleSize = 13;
		int count = 0;
		String previousSpreadSheet = null;
		String quarterToMonths[] = new String[4];
		Boolean isHeading = true;
		Map<String, String> map = new HashMap<String, String>();
		if (repSummaryOpportunityList.size() > 0) {
			for (ReportSummaryOpportunity repSummaryOpp : repSummaryOpportunityList) {
				if (!map.containsKey(repSummaryOpp.getSalesStageDescription())) {
					if (repSummaryOpp.getSalesStageCode() == 9) {
						previousSpreadSheet = ReportConstants.LOSSES;
						spreadsheet = (SXSSFSheet) workbook.getSheet(ReportConstants.WINS);
					} else {
						previousSpreadSheet = ReportConstants.WINS;
						spreadsheet = (SXSSFSheet) workbook.getSheet(ReportConstants.LOSSES);
					}
					if (totalHeadingRow != 0) {
							lastRow = workbook.getSheet(previousSpreadSheet)
									.getLastRowNum() + 1;
							lastColumn = workbook
									.getSheet(previousSpreadSheet)
									.getRow(workbook.getSheet(
											previousSpreadSheet)
											.getLastRowNum()).getLastCellNum();
							row = (SXSSFRow) workbook.getSheet(previousSpreadSheet)
									.createRow((short) lastRow);
						row.createCell(0)
								.setCellValue("Total");
						workbook.getSheet(previousSpreadSheet).autoSizeColumn(0);
						row.getCell(0).setCellStyle(
								subHeadingStyle2);
						for (int col = 1 ; col < lastColumn; col++) {
							if (col < 26) {
								row.createCell(col).setCellFormula(
										"SUM(" + ((char) (65 + col))
												+ (totalHeadingRow) + ":"
												+ ((char) (65 + col))
												+ (lastRow) + ")");
								workbook.getSheet(previousSpreadSheet)
										.autoSizeColumn(col);
								row.getCell(col).setCellStyle(subHeadingStyle2);
							} else {
								row.createCell(col).setCellFormula(
										"SUM(" + "A"
												+ ((char) (65 + (col % 26)))
												+ (totalHeadingRow) + ":" + "A"
												+ ((char) (65 + (col % 26)))
												+ (lastRow) + ")");
								workbook.getSheet(previousSpreadSheet)
										.autoSizeColumn(col);
								row.getCell(col).setCellStyle(subHeadingStyle2);
							}
						}
					}
					map.put(repSummaryOpp.getSalesStageDescription(), "value");
					isHeading = true;

				}
				if (isHeading) {
					rowValue = spreadsheet.getLastRowNum() + 2;
					row = (SXSSFRow) spreadsheet.createRow((short) rowValue++);
					if (!year.isEmpty() && currency.size() > 1) {
							lastCell = 15;
					} else if (year.isEmpty() && currency.size() > 1) {
							lastCell = 12;
					} else if (!year.isEmpty()) {
							lastCell = 10;
					} else {
							lastCell = 8;
					}
					spreadsheet.addMergedRegion(new CellRangeAddress(
							rowValue - 1, rowValue - 1, 0,
							lastCell));
					if (row.getCell(0) == null) {
						cell = (SXSSFCell) row.createCell(0);
					} else {
						cell = (SXSSFCell) row.getCell(0);
					}
//					spreadsheet.autoSizeColumn(0);
					cell.setCellStyle(subHeadingStyle);
					if (!(repSummaryOpp.getSubSp() == null))
						cell.setCellValue("SERVICE LINES WISE");
					else if (!(repSummaryOpp.getGeography() == null))
						cell.setCellValue("GEOGRAPHY WISE");
					else
						cell.setCellValue("IOU WISE");
						row = (SXSSFRow) spreadsheet.createRow((short) rowValue++);
					if (!(currency.size() > 1)) {
						spreadsheet.addMergedRegion(new CellRangeAddress(
								rowValue - 1, rowValue - 1, 1, 2));
						spreadsheet.addMergedRegion(new CellRangeAddress(
								rowValue - 1, rowValue - 1, 3, 4));
						spreadsheet.addMergedRegion(new CellRangeAddress(
								rowValue - 1, rowValue - 1, 5, 6));
						spreadsheet.addMergedRegion(new CellRangeAddress(
								rowValue - 1, rowValue - 1, 7, 8));
						if (!year.isEmpty()) {
							spreadsheet.addMergedRegion(new CellRangeAddress(
									rowValue - 1, rowValue - 1, 9, 10));
						}
					} else {
						spreadsheet.addMergedRegion(new CellRangeAddress(
								rowValue - 1, rowValue - 1, 1, 3));
						spreadsheet.addMergedRegion(new CellRangeAddress(
								rowValue - 1, rowValue - 1, 4, 6));
						spreadsheet.addMergedRegion(new CellRangeAddress(
								rowValue - 1, rowValue - 1, 7, 9));
						spreadsheet.addMergedRegion(new CellRangeAddress(
								rowValue - 1, rowValue - 1, 10, 12));
						if (!year.isEmpty()) {
							spreadsheet.addMergedRegion(new CellRangeAddress(
									rowValue - 1, rowValue - 1, 13, 15));
						}
					}
					if ((currency.size() > 1)) {
						currencyOffsetValue[0] = 1;
						currencyOffsetValue[1] = 4;
						currencyOffsetValue[2] = 7;
						currencyOffsetValue[3] = 10;
						currencyOffsetValue[4] = 13;
					} else {
						currencyOffsetValue[0] = 1;
						currencyOffsetValue[1] = 3;
						currencyOffsetValue[2] = 5;
						currencyOffsetValue[3] = 7;
						currencyOffsetValue[4] = 9;
					}
					if (year.isEmpty()) {
						switch (quarter.substring(0, 2)) {
						case "Q1":
							quarterToMonths[0] = "APR";
							quarterToMonths[1] = "MAY";
							quarterToMonths[2] = "JUN";

							break;
						case "Q2":
							quarterToMonths[0] = "JUL";
							quarterToMonths[1] = "AUG";
							quarterToMonths[2] = "SEP";
							break;
						case "Q3":
							quarterToMonths[0] = "OCT";
							quarterToMonths[1] = "NOV";
							quarterToMonths[2] = "DEC";
							break;
						case "Q4":
							quarterToMonths[0] = "JAN";
							quarterToMonths[1] = "FEB";
							quarterToMonths[2] = "MAR";
							break;
						}
						quarterToMonths[3] = "Total";
						for (int columnValue = 0; columnValue < 4; columnValue++) {
							cell = (SXSSFCell) row
									.createCell(currencyOffsetValue[columnValue]);
							spreadsheet
									.autoSizeColumn(currencyOffsetValue[columnValue]);
							cell.setCellValue(quarterToMonths[columnValue]);
							cell.setCellStyle(headingRow);
						}
					} else {

						for (int i = 0; i < 5; i++) {
							cell = (SXSSFCell) row
									.createCell(currencyOffsetValue[i]);
//							spreadsheet.autoSizeColumn(currencyOffsetValue[i]);
							cell.setCellValue(ReportConstants.QUARTERS[i]);
							cell.setCellStyle(headingRow);
						}
					}
						row = (SXSSFRow) spreadsheet.createRow((short) rowValue++);
					if ((currency.size() > 1)) {
						count = 0;
						columnSize = 5;
						columnStyleSize = 13;
						if (!year.isEmpty()) {
							columnSize = 6;
							columnStyleSize = 16;
						}
						row.createCell(0).setCellValue(
								"Service Line");
//						spreadsheet.autoSizeColumn(0);
						for (int i = 1; i < columnSize; i++) {
							if(repSummaryOpp.getSalesStageCode() == 9){
							row.createCell((++count)).setCellValue("# of wins");
							} else {
								row.createCell((++count)).setCellValue("# of Losses");
							}
//							spreadsheet.autoSizeColumn(count);
							row.createCell(++count)
									.setCellValue(
											"Total Value (" + currency.get(0)
													+ ")");
//							spreadsheet.autoSizeColumn(count);
							row.createCell(++count)
									.setCellValue(
											"Total Value (" + currency.get(1)
													+ ")");
						}
						for (int i = 0; i < columnStyleSize; i++) {
							row.getCell(i).setCellStyle(
									subHeadingStyle2);
						}
					} else {
						count = 0;
						columnSize = 5;
						columnStyleSize = 9;
						if (!year.isEmpty()) {
							columnSize = 6;
							columnStyleSize = 11;
						}
						row.createCell(0).setCellValue(
								"Service Line");
//						spreadsheet.autoSizeColumn(0);
						for (int i = 1; i < columnSize; i++) {
							if(repSummaryOpp.getSalesStageCode() == 9){
							row.createCell(++count)
									.setCellValue("# of wins");
							} else {
								row.createCell(++count)
								.setCellValue("# of Losses");
							}
//							spreadsheet.autoSizeColumn(count);
							row.createCell(++count)
									.setCellValue(
											"Total Value (" + currency.get(0)
													+ ")");
//							spreadsheet.autoSizeColumn(count);
						}
						for (int i = 0; i < columnStyleSize; i++) {
							row.getCell(i).setCellStyle(
									subHeadingStyle2);
						}
					}
						totalHeadingRow = rowValue - 1;
					isHeading = false;
				}

				row = (SXSSFRow) spreadsheet.createRow((short) rowValue++);
				List<OpportunitySummaryValue> oppSummaryValList = new ArrayList<OpportunitySummaryValue>();
				List<String> subCategoryList = null;
				Boolean isPresent = null;
				if (quarter.isEmpty()) {
					subCategoryList = DateUtils.getQuarters(year);
				} else {
					subCategoryList = DateUtils.getMonths(quarter);
				}

				for (int i = 0; i < subCategoryList.size(); i++) {
					isPresent = false;
					for (int j = 0; j < repSummaryOpp
							.getOpportunitySummaryValueList().size(); j++) {
						if (subCategoryList.get(i).contains(
								repSummaryOpp.getOpportunitySummaryValueList()
										.get(j).getTitle().subSequence(0, 2))) {
							isPresent = true;
							oppSummaryValList.add(repSummaryOpp
									.getOpportunitySummaryValueList().get(j));
						}

					}
					if (!isPresent) {
						OpportunitySummaryValue opportunitySummaryValue = new OpportunitySummaryValue();
						opportunitySummaryValue
								.setTitle(subCategoryList.get(i));
						opportunitySummaryValue.setCount(BigInteger.ZERO);
						opportunitySummaryValue.setBidValue(BigDecimal.ZERO);
						oppSummaryValList.add(opportunitySummaryValue);

					}
				}
				for (OpportunitySummaryValue oppSummaryValue : oppSummaryValList) {
					if (!((repSummaryOpp.getIou()) == null)) {
						row.createCell(0).setCellValue(
								repSummaryOpp.getIou());
					} else if (!((repSummaryOpp.getGeography()) == null)) {
						row.createCell(0).setCellValue(
								repSummaryOpp.getGeography());
					} else {
						row.createCell(0).setCellValue(
								repSummaryOpp.getSubSp());
					}
					spreadsheet.autoSizeColumn(0);
					row.getCell(0).setCellStyle(rowDataStyle);
					if (ReportConstants.FIRSTQUARTER.contains(oppSummaryValue.getTitle()
							.substring(0, 3))) {
						row.createCell(1).setCellValue(
								oppSummaryValue.getCount().intValue());
						spreadsheet.autoSizeColumn(1);
						row.getCell(1).setCellStyle(
								rowDataStyle);
						row.createCell(2).setCellValue(
								beaconConverterService.convert(
										"INR",
										currency.get(0),
										oppSummaryValue.getBidValue()
												.doubleValue()).doubleValue());
						spreadsheet.autoSizeColumn(2);
						row.getCell(2).setCellStyle(
								rowDataStyle);
						if ((currency.size() > 1)) {
							row.createCell(3).setCellValue(
									beaconConverterService.convert(
											"INR",
											currency.get(1),
											oppSummaryValue.getBidValue()
													.doubleValue())
											.doubleValue());
							spreadsheet.autoSizeColumn(3);
							row.getCell(3).setCellStyle(
									rowDataStyle);
						}

					} else if (ReportConstants.SECONDQUARTER.contains(oppSummaryValue
							.getTitle().substring(0, 3))) {
						bothCurrencyColumn = 0;
						if ((currency.size() > 1)) {
							bothCurrencyColumn = 1;
							row.createCell(6).setCellValue(
									beaconConverterService.convert(
											"INR",
											currency.get(1),
											oppSummaryValue.getBidValue()
													.doubleValue())
											.doubleValue());
							spreadsheet.autoSizeColumn(6);
							row.getCell(6).setCellStyle(
									rowDataStyle);
						}
						row.createCell(3 + bothCurrencyColumn)
								.setCellValue(
										oppSummaryValue.getCount().intValue());
						spreadsheet.autoSizeColumn(3 + bothCurrencyColumn);
						row.getCell(3 + bothCurrencyColumn)
								.setCellStyle(rowDataStyle);
						row.createCell(4 + bothCurrencyColumn)
								.setCellValue(
										beaconConverterService.convert(
												"INR",
												currency.get(0),
												oppSummaryValue.getBidValue()
														.doubleValue())
												.doubleValue());
						spreadsheet.autoSizeColumn(4 + bothCurrencyColumn);
						row.getCell(4 + bothCurrencyColumn)
								.setCellStyle(rowDataStyle);
					} else if (ReportConstants.THIRDQUARTER.contains(oppSummaryValue.getTitle()
							.substring(0, 3))) {
						bothCurrencyColumn = 0;
						if ((currency.size() > 1)) {
							bothCurrencyColumn = 2;
							row.createCell(9).setCellValue(
									beaconConverterService.convert(
											"INR",
											currency.get(1),
											oppSummaryValue.getBidValue()
													.doubleValue())
											.doubleValue());
							spreadsheet.autoSizeColumn(9);
							row.getCell(9).setCellStyle(
									rowDataStyle);
						}
						row.createCell(5 + bothCurrencyColumn)
								.setCellValue(
										oppSummaryValue.getCount().intValue());
						spreadsheet.autoSizeColumn(5 + bothCurrencyColumn);
						row.getCell(5 + bothCurrencyColumn)
								.setCellStyle(rowDataStyle);
						row.createCell(6 + bothCurrencyColumn)
								.setCellValue(
										beaconConverterService.convert(
												"INR",
												currency.get(0),
												oppSummaryValue.getBidValue()
														.doubleValue())
												.doubleValue());
						spreadsheet.autoSizeColumn(6 + bothCurrencyColumn);
						row.getCell(6 + bothCurrencyColumn)
								.setCellStyle(rowDataStyle);
					} else if (!year.isEmpty()
							&& (oppSummaryValue.getTitle().contains("Q4"))) {
						bothCurrencyColumn = 0;
						if ((currency.size() > 1)) {
							bothCurrencyColumn = 3;
							row.createCell(12).setCellValue(
									beaconConverterService.convert(
											"INR",
											currency.get(1),
											oppSummaryValue.getBidValue()
													.doubleValue())
											.doubleValue());
							spreadsheet.autoSizeColumn(12);
							row.getCell(12).setCellStyle(
									rowDataStyle);
						}
						row.createCell(7 + bothCurrencyColumn)
								.setCellValue(
										oppSummaryValue.getCount().intValue());
						spreadsheet.autoSizeColumn(7 + bothCurrencyColumn);
						row.getCell(7 + bothCurrencyColumn)
								.setCellStyle(rowDataStyle);
						row.createCell(8 + bothCurrencyColumn)
								.setCellValue(
										beaconConverterService.convert(
												"INR",
												currency.get(0),
												oppSummaryValue.getBidValue()
														.doubleValue())
												.doubleValue());
						spreadsheet.autoSizeColumn(8 + bothCurrencyColumn);
						row.getCell(8 + bothCurrencyColumn)
								.setCellStyle(rowDataStyle);
					}
				}
				if (!year.isEmpty() && (currency.size() > 1)) {
					row.createCell(13).setCellValue(
							repSummaryOpp.getTotalCount().intValue());
					spreadsheet.autoSizeColumn(13);
					row.getCell(13).setCellStyle(rowDataStyle);
					row.createCell(14).setCellValue(
							beaconConverterService.convert(
									"INR",
									currency.get(0),
									repSummaryOpp
											.getTotalBidValueFirstCurrency()
											.doubleValue()).doubleValue());
					spreadsheet.autoSizeColumn(14);
					row.getCell(14).setCellStyle(rowDataStyle);
					row.createCell(15).setCellValue(
							beaconConverterService.convert(
									"INR",
									currency.get(1),
									repSummaryOpp
											.getTotalBidValueSecondCurrency()
											.doubleValue()).doubleValue());
					spreadsheet.autoSizeColumn(15);
					row.getCell(15).setCellStyle(rowDataStyle);
				} else if (year.isEmpty() && (currency.size() > 1)) {
					row.createCell(10).setCellValue(
							repSummaryOpp.getTotalCount().intValue());
					spreadsheet.autoSizeColumn(10);
					row.getCell(10).setCellStyle(rowDataStyle);
					row.createCell(11).setCellValue(
							beaconConverterService.convert(
									"INR",
									currency.get(0),
									repSummaryOpp
											.getTotalBidValueFirstCurrency()
											.doubleValue()).doubleValue());
					spreadsheet.autoSizeColumn(11);
					row.getCell(11).setCellStyle(rowDataStyle);
					row.createCell(12).setCellValue(
							beaconConverterService.convert(
									"INR",
									currency.get(1),
									repSummaryOpp
											.getTotalBidValueSecondCurrency()
											.doubleValue()).doubleValue());
					spreadsheet.autoSizeColumn(12);
					row.getCell(12).setCellStyle(rowDataStyle);
				} else if (!year.isEmpty()) {
					row.createCell(9).setCellValue(
							repSummaryOpp.getTotalCount().intValue());
					spreadsheet.autoSizeColumn(9);
					row.getCell(9).setCellStyle(rowDataStyle);
					row.createCell(10).setCellValue(
							beaconConverterService.convert(
									"INR",
									currency.get(0),
									repSummaryOpp
											.getTotalBidValueFirstCurrency()
											.doubleValue()).doubleValue());
					spreadsheet.autoSizeColumn(10);
					row.getCell(10).setCellStyle(rowDataStyle);
				} else {
					row.createCell(7).setCellValue(
							repSummaryOpp.getTotalCount().intValue());
					spreadsheet.autoSizeColumn(7);
					row.getCell(7).setCellStyle(rowDataStyle);
					row.createCell(8).setCellValue(
							beaconConverterService.convert(
									"INR",
									currency.get(0),
									repSummaryOpp
											.getTotalBidValueFirstCurrency()
											.doubleValue()).doubleValue());
					spreadsheet.autoSizeColumn(8);
					row.getCell(8).setCellStyle(rowDataStyle);
				}
			}

				lastRow = spreadsheet.getLastRowNum();
				lastColumn = spreadsheet.getRow(spreadsheet.getLastRowNum())
						.getLastCellNum();
				row = (SXSSFRow) spreadsheet.createRow((short) rowValue++);
			row.createCell(0).setCellValue("Total");
			spreadsheet.autoSizeColumn(0);
			row.getCell(0).setCellStyle(subHeadingStyle2);
			for (int col = 1; col < lastColumn; col++) {
				if (col < 26) {
					row.createCell(col)
							.setCellFormula(
									"SUM(" + ((char) (65 + col))
											+ (totalHeadingRow + 1) + ":"
											+ ((char) (65 + col))
											+ (lastRow + 1) + ")");
					spreadsheet.autoSizeColumn(col);
					row.getCell(col).setCellStyle(subHeadingStyle2);
				} else {
					row.createCell(col).setCellFormula(
							"SUM(" + "A" + ((char) (65 + (col % 26)))
									+ (totalHeadingRow + 1) + ":" + "A"
									+ ((char) (65 + (col % 26)))
									+ (lastRow + 1) + ")");
					spreadsheet.autoSizeColumn(col);
					row.getCell(col).setCellStyle(subHeadingStyle2);
				}
			}
		}
		
//		return yearHeadingMap;
	}

	/**
	 * This method will add the geography or iou details in excel.
	 * 
	 * @param reportSummaryOpportunityList
	 *            It contains the List of reportSummaryOpportunity.
	 * @param currency
	 *            It contains the list of currency.
	 * @return this method will return geography or iou details in excel.
	 */
	public void getPipelineAnticipatingDetails(SXSSFWorkbook workbook,
			List<ReportSummaryOpportunity> reportSummaryOpportunityList,
			List<String> currency) throws DestinationException {
		logger.debug("Inside Report Service getPipelineAnticipatingDetails method");
		
		CellStyle rowDateStyle = ExcelUtils.createRowStyle(workbook, "dataRow");
		CellStyle headingStyle = ExcelUtils.createRowStyle(workbook,
				"headingStyle");
		CellStyle subHeadingStyle2 = ExcelUtils.createRowStyle(workbook,
				"subHeadingStyle2");
		CellStyle subHeadingStyle3 = ExcelUtils.createRowStyle(workbook,
				"subHeadingStyle3");
		SXSSFSheet spreadsheet = null;
		SXSSFRow row = null;
		SXSSFCell cell = null;
		int rowValuePipeline = 0;
		int rowValueAnticipating = 0;
		int headingColPipeline = 1;
		int headingColAnticipating = 1;
		int headingRowPipeline = 0;
		int lastColumnPipeline = 0;
		int lastColumnAnticipating = 0;
		int headingRowAnticipating = 0;
		int startTotalValuePipeline = 0;
		int startTotalValueAnticipating = 0;
		int columnValuePipeline = 1;
		int columnValueAnticipating = 1;
		try{
		Map<String, List<String>> iouListMap = new TreeMap<String, List<String>>();
		Map<String, List<Integer>> geographyMapPipeline = new LinkedHashMap<String, List<Integer>>();
		Map<String, List<Integer>> geographyMapAnticipating = new LinkedHashMap<String, List<Integer>>();
		if (reportSummaryOpportunityList.size() > 0) {
			iouListMap = getSearchItemList(reportSummaryOpportunityList);
			for (ReportSummaryOpportunity repSummaryOpp : reportSummaryOpportunityList) {
				if (repSummaryOpp.getSalesStageCode() < 4) {
					if (workbook.getSheet(ReportConstants.PROSPECTS) == null) {
						spreadsheet = (SXSSFSheet) workbook
								.createSheet(ReportConstants.PROSPECTS);
						row = (SXSSFRow) spreadsheet.createRow((short) 1);
					}
					spreadsheet = (SXSSFSheet) workbook.getSheet(ReportConstants.PROSPECTS);
					rowValueAnticipating = spreadsheet.getLastRowNum() + 1;
					if (geographyMapAnticipating.isEmpty()) {
						rowValueAnticipating = spreadsheet.getLastRowNum() + 2;
						headingRowAnticipating = rowValueAnticipating;
						row = (SXSSFRow) spreadsheet
								.createRow((short) rowValueAnticipating++);
						cell = (SXSSFCell) row.createCell(0);
						cell.setCellValue("Row Labels");
						spreadsheet.autoSizeColumn(0);
						cell.setCellStyle(subHeadingStyle3);
						row = (SXSSFRow) spreadsheet
								.createRow((short) rowValueAnticipating++);
						startTotalValueAnticipating = rowValueAnticipating;

					}
					row = (SXSSFRow) spreadsheet.createRow((short) rowValueAnticipating); // Sales
																				// Stage
																				// description
																				// contents
																				// Row
					cell = (SXSSFCell) row.createCell(0);
					cell.setCellValue(repSummaryOpp.getSalesStageDescription());
					spreadsheet.autoSizeColumn(0);
					cell.setCellStyle(rowDateStyle);
					List<OpportunitySummaryValue> oppSumVal = addZeroToEmptyOpportunitySummaryValue(
							repSummaryOpp.getOpportunitySummaryValueList(),
							iouListMap.get("anticipating"));
					OpportunitySummaryValue opportunitySummaryValue = new OpportunitySummaryValue();
					opportunitySummaryValue.setCount(repSummaryOpp
							.getTotalCount());
					opportunitySummaryValue.setBidValue(repSummaryOpp
							.getTotalBidValueFirstCurrency());
					opportunitySummaryValue.setTitle("Grand Total");
					oppSumVal.add(opportunitySummaryValue);
					for (OpportunitySummaryValue oppSummaryValue : oppSumVal) {
						if (geographyMapAnticipating
								.containsKey(oppSummaryValue.getTitle())) {
							List<Integer> geoColumns = geographyMapAnticipating
									.get(oppSummaryValue.getTitle());
							cell = (SXSSFCell) row.createCell(geoColumns.get(0));
							cell.setCellValue(oppSummaryValue.getCount()
									.intValue());
							spreadsheet.autoSizeColumn(geoColumns.get(0));
							cell.setCellStyle(rowDateStyle);
							cell = (SXSSFCell) row.createCell(geoColumns.get(1));
							cell.setCellStyle(rowDateStyle);
							spreadsheet.autoSizeColumn(geoColumns.get(1));
							cell.setCellValue((beaconConverterService.convert(
									"INR", currency.get(0), oppSummaryValue
											.getBidValue().doubleValue()))
									.doubleValue());
							if ((currency.size() > 1)) {
								cell = (SXSSFCell) row.createCell(geoColumns
										.get(2));
								cell.setCellStyle(rowDateStyle);
								spreadsheet.autoSizeColumn(geoColumns.get(2));
								cell.setCellValue((beaconConverterService
										.convert("INR", currency.get(1),
												oppSummaryValue.getBidValue()
														.doubleValue()))
										.doubleValue());
							}
						} else {
							if (!(currency.size() > 1)) {
								spreadsheet
										.addMergedRegion(new CellRangeAddress(
												headingRowAnticipating,
												headingRowAnticipating,
												columnValueAnticipating,
												columnValueAnticipating + 1)); // including
																				// Row
																				// label
																				// in
																				// the
																				// heading
							} else {
								spreadsheet
										.addMergedRegion(new CellRangeAddress(
												headingRowAnticipating,
												headingRowAnticipating,
												columnValueAnticipating,
												columnValueAnticipating + 2));
							}
							spreadsheet.getRow(headingRowAnticipating)
									.createCell(headingColAnticipating)
									.setCellValue(oppSummaryValue.getTitle());
							spreadsheet.getRow(headingRowAnticipating)
									.getCell(headingColAnticipating)
									.setCellStyle(subHeadingStyle3);
							spreadsheet.autoSizeColumn(headingColAnticipating);
							List<Integer> geoColumns = new ArrayList<Integer>();
							spreadsheet.getRow(headingRowAnticipating + 1)
									.createCell(columnValueAnticipating)
									.setCellValue("# of bids");
							spreadsheet.autoSizeColumn(columnValueAnticipating);
							spreadsheet.getRow(headingRowAnticipating + 1)
									.getCell(columnValueAnticipating)
									.setCellStyle(subHeadingStyle2);
							geoColumns.add(columnValueAnticipating++);
							spreadsheet
									.getRow(headingRowAnticipating + 1)
									.createCell(columnValueAnticipating)
									.setCellValue(
											"bid value(" + currency.get(0)
													+ ")");
							spreadsheet.autoSizeColumn(columnValueAnticipating);
							spreadsheet.getRow(headingRowAnticipating + 1)
									.getCell(columnValueAnticipating)
									.setCellStyle(subHeadingStyle2);
							geoColumns.add(columnValueAnticipating++);
							cell = (SXSSFCell) row.createCell(geoColumns.get(0));
							cell.setCellValue(oppSummaryValue.getCount()
									.intValue());
							spreadsheet.autoSizeColumn(geoColumns.get(0));
							cell.setCellStyle(rowDateStyle);
							cell = (SXSSFCell) row.createCell(geoColumns.get(1));
							cell.setCellStyle(rowDateStyle);
							spreadsheet.autoSizeColumn(geoColumns.get(1));
							cell.setCellValue((beaconConverterService.convert(
									"INR", currency.get(0), oppSummaryValue
											.getBidValue().doubleValue()))
									.doubleValue());
							if ((currency.size() > 1)) {
								spreadsheet
										.getRow(headingRowAnticipating + 1)
										.createCell(columnValueAnticipating)
										.setCellValue(
												"bid value(" + currency.get(1)
														+ ")");
								spreadsheet.getRow(headingRowAnticipating + 1)
										.getCell(columnValueAnticipating)
										.setCellStyle(subHeadingStyle2);
								geoColumns.add(columnValueAnticipating++);
								cell = (SXSSFCell) row.createCell(geoColumns
										.get(2));
								spreadsheet.autoSizeColumn(geoColumns.get(2));
								cell.setCellStyle(rowDateStyle);
								cell.setCellValue((beaconConverterService
										.convert("INR", currency.get(1),
												oppSummaryValue.getBidValue()
														.doubleValue()))
										.doubleValue());
								headingColAnticipating++;
								lastColumnAnticipating = lastColumnAnticipating + 3;
							} else {
								lastColumnAnticipating = lastColumnAnticipating + 2;
							}

							geographyMapAnticipating.put(
									oppSummaryValue.getTitle(), geoColumns);
							headingColAnticipating = headingColAnticipating + 2;
						}
					}
				} else {
					if (workbook.getSheet(ReportConstants.PIPELINE) == null) {
						spreadsheet = (SXSSFSheet) workbook
								.createSheet(ReportConstants.PIPELINE);
						row = (SXSSFRow) spreadsheet.createRow((short) 1);
					}
					spreadsheet = (SXSSFSheet) workbook.getSheet(ReportConstants.PIPELINE);
					rowValuePipeline = spreadsheet.getLastRowNum() + 1;
					if (geographyMapPipeline.isEmpty()) {
						rowValuePipeline = spreadsheet.getLastRowNum() + 2;
						headingRowPipeline = rowValuePipeline;
						row = (SXSSFRow) spreadsheet.createRow((short) rowValuePipeline++); // Heading
																					// Row
																					// for
																					// iou
						cell = (SXSSFCell) row.createCell(0);
						cell.setCellValue("Row Labels");
						cell.setCellStyle(subHeadingStyle3);
						row = (SXSSFRow) spreadsheet.createRow((short) rowValuePipeline++); // For
																					// Bid
																					// value
																					// and
																					// no
																					// of
																					// bids
																					// heading
						startTotalValuePipeline = rowValuePipeline;
					}
					row = (SXSSFRow) spreadsheet.createRow((short) rowValuePipeline); // Sales
																			// Stage
																			// description
																			// contents
																			// Row
					cell = (SXSSFCell) row.createCell(0);
					cell.setCellValue(repSummaryOpp.getSalesStageDescription());
					spreadsheet.autoSizeColumn(0);
					cell.setCellStyle(rowDateStyle);
					List<OpportunitySummaryValue> oppSumVal = addZeroToEmptyOpportunitySummaryValue(
							repSummaryOpp.getOpportunitySummaryValueList(),
							iouListMap.get(ReportConstants.PIPELINE));
					OpportunitySummaryValue opportunitySummaryValue = new OpportunitySummaryValue();
					opportunitySummaryValue.setCount(repSummaryOpp
							.getTotalCount());
					opportunitySummaryValue.setBidValue(repSummaryOpp
							.getTotalBidValueFirstCurrency());
					opportunitySummaryValue.setTitle("Grand Total");
					oppSumVal.add(opportunitySummaryValue);
					for (OpportunitySummaryValue oppSummaryValue : oppSumVal) {
						if (geographyMapPipeline.containsKey(oppSummaryValue
								.getTitle())) {
							List<Integer> geoColumns = geographyMapPipeline
									.get(oppSummaryValue.getTitle());
							cell = (SXSSFCell) row.createCell(geoColumns.get(0));
							cell.setCellStyle(rowDateStyle);
							spreadsheet.autoSizeColumn(geoColumns.get(0));
							cell.setCellValue(oppSummaryValue.getCount()
									.intValue());
							cell = (SXSSFCell) row.createCell(geoColumns.get(1));
							cell.setCellStyle(rowDateStyle);
							cell.setCellValue((beaconConverterService.convert(
									"INR", currency.get(0), oppSummaryValue
											.getBidValue().doubleValue()))
									.doubleValue());
							spreadsheet.autoSizeColumn(geoColumns.get(1));
							if ((currency.size() > 1)) {
								cell = (SXSSFCell) row.createCell(geoColumns
										.get(2));
								cell.setCellStyle(rowDateStyle);
								cell.setCellValue((beaconConverterService
										.convert("INR", currency.get(1),
												oppSummaryValue.getBidValue()
														.doubleValue()))
										.doubleValue());
								spreadsheet.autoSizeColumn(geoColumns.get(2));
							}

						} else {
							if (!(currency.size() > 1)) {
								spreadsheet
										.addMergedRegion(new CellRangeAddress(
												headingRowPipeline,
												headingRowPipeline,
												columnValuePipeline,
												columnValuePipeline + 1));
							} else {
								spreadsheet
										.addMergedRegion(new CellRangeAddress(
												headingRowPipeline,
												headingRowPipeline,
												columnValuePipeline,
												columnValuePipeline + 2));
							}
							spreadsheet.getRow(headingRowPipeline)
									.createCell(headingColPipeline)
									.setCellValue(oppSummaryValue.getTitle());
							spreadsheet.autoSizeColumn(headingColPipeline);
							spreadsheet.getRow(headingRowPipeline)
									.getCell(headingColPipeline)
									.setCellStyle(subHeadingStyle3);
							;
							List<Integer> geoColumns = new ArrayList<Integer>();
							spreadsheet.getRow(headingRowPipeline + 1)
									.createCell(columnValuePipeline)
									.setCellValue("# of bids");
							spreadsheet.autoSizeColumn(columnValuePipeline);
							spreadsheet.getRow(headingRowPipeline + 1)
									.getCell(columnValuePipeline)
									.setCellStyle(subHeadingStyle2);
							geoColumns.add(columnValuePipeline++);
							spreadsheet
									.getRow(headingRowPipeline + 1)
									.createCell(columnValuePipeline)
									.setCellValue(
											"bid value(" + currency.get(0)
													+ ")");
							spreadsheet.autoSizeColumn(columnValuePipeline);
							spreadsheet.getRow(headingRowPipeline + 1)
									.getCell(columnValuePipeline)
									.setCellStyle(subHeadingStyle2);
							geoColumns.add(columnValuePipeline++);
							cell = (SXSSFCell) row.createCell(geoColumns.get(0));
							cell.setCellValue(oppSummaryValue.getCount()
									.intValue());
							spreadsheet.autoSizeColumn(geoColumns.get(0));
							cell.setCellStyle(rowDateStyle);
							cell = (SXSSFCell) row.createCell(geoColumns.get(1));
							cell.setCellValue((beaconConverterService.convert(
									"INR", currency.get(0), oppSummaryValue
											.getBidValue().doubleValue()))
									.doubleValue());
							spreadsheet.autoSizeColumn(geoColumns.get(1));
							cell.setCellStyle(rowDateStyle);
							if ((currency.size() > 1)) {
								spreadsheet
										.getRow(headingRowPipeline + 1)
										.createCell(columnValuePipeline)
										.setCellValue(
												"bid value(" + currency.get(1)
														+ ")");
								spreadsheet.getRow(headingRowPipeline + 1)
										.getCell(columnValuePipeline)
										.setCellStyle(subHeadingStyle2);
								geoColumns.add(columnValuePipeline++);
								cell = (SXSSFCell) row.createCell(geoColumns
										.get(2));
								spreadsheet.autoSizeColumn(geoColumns.get(2));
								cell.setCellStyle(rowDateStyle);
								cell.setCellValue((beaconConverterService
										.convert("INR", currency.get(1),
												oppSummaryValue.getBidValue()
														.doubleValue()))
										.doubleValue());
								headingColPipeline++;
								lastColumnPipeline = lastColumnPipeline + 3;
							} else {
								lastColumnPipeline = lastColumnPipeline + 2;
							}
							geographyMapPipeline.put(
									oppSummaryValue.getTitle(), geoColumns);
							headingColPipeline = headingColPipeline + 2;
						}
					}
				}
			}
			spreadsheet = (SXSSFSheet) workbook.getSheet(ReportConstants.PIPELINE);
			if (spreadsheet != null) {
				row = (SXSSFRow) spreadsheet.getRow(1);
				if (currency.size() > 1) {
					spreadsheet.addMergedRegion(new CellRangeAddress(1, 1, 0,
							(headingColPipeline - 1)));
				} else {
					spreadsheet.addMergedRegion(new CellRangeAddress(1, 1, 0,
							(headingColPipeline - 1)));
				}
				cell = (SXSSFCell) row.createCell(0);
				spreadsheet.autoSizeColumn(0);
				cell.setCellValue("Summary - Pipeline(04-08)");
				cell.setCellStyle(headingStyle);
				row = (SXSSFRow) spreadsheet
						.createRow((short) spreadsheet.getLastRowNum() + 1); // Heading
																				// Row
																				// for
																				// iou
				cell = (SXSSFCell) row.createCell(0);
				cell.setCellValue("Total");
				spreadsheet.autoSizeColumn(0);
				cell.setCellStyle(subHeadingStyle2);
				for (int column = 1; column <= lastColumnPipeline; column++) {
					cell = (SXSSFCell) row.createCell(column);
					cell.setCellStyle(subHeadingStyle2);
					setCellFormulaWithColumnFormed(spreadsheet, cell,
							startTotalValuePipeline, column);
					spreadsheet.autoSizeColumn(column);
				}
			}
			spreadsheet = (SXSSFSheet) workbook.getSheet(ReportConstants.PROSPECTS);
			if (spreadsheet != null) {
				row = (SXSSFRow) spreadsheet.createRow((short) 1);
				if (currency.size() > 1) {
					spreadsheet.addMergedRegion(new CellRangeAddress(1, 1, 0,
							(headingColAnticipating - 1)));
				} else {
					spreadsheet.addMergedRegion(new CellRangeAddress(1, 1, 0,
							(headingColAnticipating - 1)));
				}
				cell = (SXSSFCell) row.createCell(0);
				cell.setCellValue("Summary - Opportunities(00-03)");
				spreadsheet.autoSizeColumn(0);
				cell.setCellStyle(headingStyle);
				row = (SXSSFRow) spreadsheet
						.createRow((short) spreadsheet.getLastRowNum() + 1); // Heading
																				// Row
																				// for
																				// iou
				cell = (SXSSFCell) row.createCell(0);
				cell.setCellValue("Total");
				spreadsheet.autoSizeColumn(0);
				cell.setCellStyle(subHeadingStyle2);
				for (int column = 1; column <= lastColumnAnticipating; column++) {
					cell = (SXSSFCell) row.createCell(column);
					cell.setCellStyle(subHeadingStyle2);
					setCellFormulaWithColumnFormed(spreadsheet, cell,
							startTotalValueAnticipating, column);
					spreadsheet.autoSizeColumn(column);
				}
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		}

	public void setCellFormulaWithColumnFormed(SXSSFSheet spreadsheet,
			SXSSFCell cell, int startTotalValuePipeline, int column) {
		String formula = "";
		if(column < 26){
			formula = "SUM(" + ((char) (65 + column))
					+ startTotalValuePipeline + ":"
					+ ((char) (65 + column))
					+ spreadsheet.getLastRowNum() + ")";
		cell.setCellFormula(formula);
		
		}
		else {
			char[] colName = new char[2];
			colName[0] = (char)(65 + (column / 26) - 1);
			colName[1] = (char)(65 + (column % 26));
			
	        String columnName = new String(colName);
	        formula = "SUM(" + columnName
					+ startTotalValuePipeline + ":"
					+ columnName
					+ spreadsheet.getLastRowNum() + ")";
			cell.setCellFormula(formula);
			
		}
		}

	/**
	 * Add zero to List which doesn't contain the search item
	 * 
	 * @param opportunitySummaryValueList
	 *            It contains the List of opportunitySummaryValue.
	 * @param itemList
	 *            the list containing the search item.
	 * @return returns the list with added zero.
	 */
	public List<OpportunitySummaryValue> addZeroToEmptyOpportunitySummaryValue(
			List<OpportunitySummaryValue> opportunitySummaryValueList,
			List<String> itemList) {
		logger.debug("Inside Report Service addZeroToEmptyOpportunitySummaryValue Method");

		if (opportunitySummaryValueList.size() > 0) {
			if (itemList.size() > 0 && !itemList.get(0).equals("")) {

				for (int i = 0; i < itemList.size(); i++) {
					Boolean isPresent = false;
					for (int j = 0; j < opportunitySummaryValueList.size(); j++) {
						if (opportunitySummaryValueList.get(j).getTitle()
								.equals(itemList.get(i))) {
							isPresent = true;
						}
					}
					if (!isPresent) {
						OpportunitySummaryValue oppSummaryVal = new OpportunitySummaryValue();
						oppSummaryVal.setTitle(itemList.get(i));
						oppSummaryVal.setCount(BigInteger.ZERO);
						oppSummaryVal.setBidValue(BigDecimal.ZERO);
						opportunitySummaryValueList.add(oppSummaryVal);
					}

				}
			}
		}
		return opportunitySummaryValueList;
	}

	public List<ReportSummaryOpportunity> getWinLossOpportunities(String month,
			String year, String quarter, List<String> geography,
			List<String> country, List<String> iou, List<String> serviceLines,
			List<Integer> salesStageList, List<String> userIds, String userId,
			Boolean isDistinctIou, String userGroup) throws DestinationException, Exception {
		logger.debug("Inside Report Service getWinLossOpportunities Method");
		List<String> geoList = new ArrayList<String>();
		List<String> iouList = new ArrayList<String>();
		List<String> countryList = new ArrayList<String>();
		List<String> serviceLinesList = new ArrayList<String>();
		addItemToListGeo(geography,geoList);
	    isDistinctIou = true;
		addItemToList(iou,iouList);
		addItemToList(country,countryList);
		addItemToList(serviceLines,serviceLinesList);
		Map<String, List<Object[]>> objectListForSubCategory = new LinkedHashMap<String, List<Object[]>>();
		Map<String, Date> toDateMap = null;
		Map<String, Date> fromDateMap = null;
		Date fromDate = new Date();
		Date toDate = new Date();
		List<Object[]> opportunityList = new ArrayList<Object[]>();
		List<ReportSummaryOpportunity> reportSummaryOpportunities = new ArrayList<ReportSummaryOpportunity>();
		fromDateMap = DateUtils.getSubDatesList(month, year, quarter, true);
		toDateMap = DateUtils.getSubDatesList(month, year, quarter, false);
		for (int salesStageCode : salesStageList) {
				objectListForSubCategory.clear();
				for (String subCategory : fromDateMap.keySet()) {
					List<Object[]> serviceLineOpportunityList = new ArrayList<Object[]>();
					fromDate = fromDateMap.get(subCategory);
					toDate = toDateMap.get(subCategory);
					switch (userGroup) {
					case ReportConstants.BDM:
						serviceLineOpportunityList = opportunityRepository.findOpportunitiesWithServiceLineByRole(fromDate, toDate, salesStageCode, userIds,  geoList, countryList, iouList, serviceLinesList);
						break;
					case ReportConstants.BDMSUPERVISOR:
						serviceLineOpportunityList = opportunityRepository.findOpportunitiesWithServiceLineByRole(fromDate, toDate, salesStageCode, userIds,  geoList, countryList, iouList, serviceLinesList);
						break;
					default:
							if(geography.contains("All") && (iou.contains("All") && serviceLines.contains("All")) && country.contains("All")){
								String queryString = reportsService.getOpportunityServiceLineSummaryQueryString(userId,fromDate,toDate,salesStageCode);
								Query opportunitySummaryReportQuery = entityManager.createNativeQuery(queryString);
								serviceLineOpportunityList = opportunitySummaryReportQuery.getResultList();
								
							} else {
								serviceLineOpportunityList = opportunityRepository.findOpportunitiesWithServiceLine(fromDate, toDate, geoList, countryList, iouList, serviceLinesList, 
										salesStageCode);
							}
						break;
					}
					objectListForSubCategory.put(subCategory,
							serviceLineOpportunityList);
				}
				reportSummaryOpportunities.addAll(getSummaryReportWinLoss(
						month, year, quarter, salesStageCode, opportunityList,
						fromDateMap, objectListForSubCategory, "serviceLine"));
				// for Geography
				objectListForSubCategory.clear();
				for (String subCategory : fromDateMap.keySet()) {
					List<Object[]> geographyOpportunityList = new ArrayList<Object[]>();
					fromDate = fromDateMap.get(subCategory);
					toDate = toDateMap.get(subCategory);
					switch (userGroup) {
					case ReportConstants.BDM:
						geographyOpportunityList = opportunityRepository.findOpportunitiesWithGeographyByRole(fromDate, toDate, salesStageCode, userIds, geoList, countryList, iouList, serviceLinesList);
						break;
					case ReportConstants.BDMSUPERVISOR:
						geographyOpportunityList = opportunityRepository.findOpportunitiesWithGeographyByRole(fromDate, toDate, salesStageCode, userIds, geoList, countryList, iouList, serviceLinesList);
						break;
					default:
							if(geography.contains("All") && (iou.contains("All") && serviceLines.contains("All")) && country.contains("All")){
								String queryString = reportsService.getOpportunityGeoSummaryQueryString(userId,fromDate,toDate,salesStageCode);
								Query opportunitySummaryReportQuery = entityManager.createNativeQuery(queryString);
								geographyOpportunityList = opportunitySummaryReportQuery.getResultList();
								
							} else {
								geographyOpportunityList = opportunityRepository.findOpportunitiesWithGeography(fromDate, toDate, geoList, countryList, iouList, serviceLinesList, 
										salesStageCode);
							}
						break;
					}
					objectListForSubCategory.put(subCategory,
							geographyOpportunityList);
				}
				reportSummaryOpportunities.addAll(getSummaryReportWinLoss(
						month, year, quarter, salesStageCode, opportunityList,
						fromDateMap, objectListForSubCategory, "geography"));
				if (isDistinctIou) {

					objectListForSubCategory.clear();
					for (String subCategory : fromDateMap.keySet()) {
						List<Object[]> iouOpportunityList = new ArrayList<Object[]>();
						fromDate = fromDateMap.get(subCategory);
						toDate = toDateMap.get(subCategory);
						switch (userGroup) {
						case ReportConstants.BDM:
							iouOpportunityList = opportunityRepository.findOpportunitiesWithIouByRole(fromDate, toDate, salesStageCode, userIds, geoList, countryList, iouList, serviceLinesList);
							break;
						case ReportConstants.BDMSUPERVISOR:
							iouOpportunityList = opportunityRepository.findOpportunitiesWithIouByRole(fromDate, toDate, salesStageCode, userIds, geoList, countryList, iouList, serviceLinesList);
							break;
						default:
								if(geography.contains("All") && (iou.contains("All") && serviceLines.contains("All")) && country.contains("All")){
									String queryString = reportsService.getOpportunityIouSummaryQueryString(userId,fromDate,toDate,salesStageCode);
									Query opportunitySummaryReportQuery = entityManager.createNativeQuery(queryString);
									iouOpportunityList = opportunitySummaryReportQuery.getResultList();
									
								} else {
									iouOpportunityList = opportunityRepository.findOpportunitiesWithIou(fromDate, toDate, geoList, countryList, iouList, serviceLinesList, 
											salesStageCode);
								}
							break;
						}
						objectListForSubCategory.put(subCategory,
								iouOpportunityList);
					}
					reportSummaryOpportunities.addAll(getSummaryReportWinLoss(
							month, year, quarter, salesStageCode,
							opportunityList, fromDateMap,
							objectListForSubCategory, "iou"));
				}
			}
		return reportSummaryOpportunities;
	}

	public void getTitleSheet(SXSSFWorkbook workbook, List<String> geography,
			List<String> iou, List<String> serviceLines,
			List<Integer> salesStage, String userId, String tillDate, List<String> country, String reportType, String month, String quarter, String year, List<String> currency) {

		List<String> privilegeValueList = new ArrayList<String>();
		CellStyle headinStyle = ExcelUtils.createRowStyle(workbook, ReportConstants.REPORTHEADINGSTYLE);
		CellStyle subHeadingStyle = ExcelUtils.createRowStyle(workbook, ReportConstants.ROWS);
		CellStyle dataRow = ExcelUtils.createRowStyle(workbook, ReportConstants.DATAROW);
		String completeList = null;
		SXSSFRow row = null;
		SXSSFSheet spreadsheet = (SXSSFSheet) workbook.createSheet(ReportConstants.TITLE);
		
		row = (SXSSFRow) spreadsheet.createRow(4);
		spreadsheet.addMergedRegion(new CellRangeAddress(4, 4, 4, 7));
		row.createCell(4).setCellValue("Opportunity report as on " + tillDate);
		row.getCell(4).setCellStyle(headinStyle);
		row = (SXSSFRow) spreadsheet.createRow(6);
		row.createCell(4).setCellValue("User Selection Filter's");
		row.getCell(4).setCellStyle(subHeadingStyle);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, ReportConstants.GEO, geography, 7, dataRow);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, "Country", country, 8, dataRow);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, Constants.IOU, iou, 9, dataRow);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, "Service Line", serviceLines, 10, dataRow);
		row = (SXSSFRow) spreadsheet.createRow(11);
		row.createCell(4).setCellValue("Period");
		String period=ExcelUtils.getPeriod(month, quarter, year);
		row.createCell(5).setCellValue(period);
		row = (SXSSFRow) spreadsheet.createRow(12);
		if (salesStage.size() == 0) {
			completeList = "All";
		} else {
			completeList = ExcelUtils.getSalesStageCode(salesStage);
		}
		row.createCell(4).setCellValue("Sales stage");
		row.createCell(5).setCellValue(completeList);
		spreadsheet.autoSizeColumn(5);
		////
		String userAccessField = null;
		List<UserAccessPrivilegesT> userPrivilegesList = 
				userAccessPrivilegesRepository.findByUserIdAndParentPrivilegeIdIsNullAndIsactive(userId, Constants.Y);
		UserT user = userRepository.findByUserId(userId);
		String userGroup=user.getUserGroupMappingT().getUserGroup();
		row = (SXSSFRow) spreadsheet.createRow(14);
		row.createCell(4).setCellValue("User Access Filter's");
		row.getCell(4).setCellStyle(subHeadingStyle);
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
			ExcelUtils.writeDetailsForSearchTypeUserAccessFilter(spreadsheet, userAccessField, privilegeValueList, user, dataRow, ReportConstants.OPPBASEDONPRIVILAGE);
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
			ExcelUtils.writeDetailsForSearchTypeUserAccessFilter(spreadsheet, userAccessField, privilegeValueList, user, dataRow, ReportConstants.OPPBASEDONPRIVILAGE);
			break;
		case ReportConstants.BDM:
			ExcelUtils.writeUserFilterConditions(spreadsheet, user, ReportConstants.OPPWHEREBDMPRIMARYORSALESOWNER);
			break;
		case ReportConstants.BDMSUPERVISOR:
			ExcelUtils.writeUserFilterConditions(spreadsheet, user, ReportConstants.OPPWHEREBDMSUPERVISORPRIMARYORSALESOWNER);
			break;
		default :
			ExcelUtils.writeUserFilterConditions(spreadsheet, user, ReportConstants.FULLACCESS);
		}
		
		////s
		row = (SXSSFRow) spreadsheet.createRow(21);
//		spreadsheet.addMergedRegion(new CellRangeAddress(21, 21, 4, 5));
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


	
	// BDM performance
	
	public void getBdmPerformanceExcel(
			Map<String, List<OpportunitySummaryValue>> oppSummaryValueMap, List<String> currency, SXSSFWorkbook workbook) {
		
		int rowNo = 0;
		SXSSFSheet spreadsheet = (SXSSFSheet) workbook.createSheet("BdmSummary");
		if(oppSummaryValueMap.get(ReportConstants.WINS).size() > 0){
			Map<String,Integer> columnValueMap = new TreeMap<String,Integer>();
			columnValueMap = createHeaderForWinOrLoss(spreadsheet, currency, ReportConstants.WINS, columnValueMap);
			writeValuesForWinsOrLoss(spreadsheet, oppSummaryValueMap.get(ReportConstants.WINS), columnValueMap);
		}
		if(oppSummaryValueMap.get(ReportConstants.LOSSES).size()>0) {
			Map<String,Integer> columnValueMap = new TreeMap<String,Integer>();
			columnValueMap = createHeaderForWinOrLoss(spreadsheet,currency,ReportConstants.LOSSES,columnValueMap);
			writeValuesForWinsOrLoss(spreadsheet, oppSummaryValueMap.get(ReportConstants.LOSSES), columnValueMap);
		}
//			createHeaderFor
//	}
	}
	
	private Map<String, Integer> createHeaderForWinOrLoss(SXSSFSheet spreadsheet,
 List<String> currency, String requiredType,
			Map<String, Integer> columnValueMap) {
		int rowNo = 1;
		int columnNo = 0;
		SXSSFRow row = null;
		CellStyle headingStyle = ExcelUtils.createRowStyle((SXSSFWorkbook) spreadsheet.getWorkbook(), ReportConstants.REPORTHEADINGSTYLE);
		CellStyle dataStyle = ExcelUtils.createRowStyle((SXSSFWorkbook) spreadsheet.getWorkbook(), ReportConstants.DATAROW);
		row = ExcelUtils.getRow(spreadsheet, rowNo++);
		if (columnValueMap.get(ReportConstants.STARTINGCOLUMN) == null) {
			columnNo = spreadsheet.getRow(rowNo - 1).getLastCellNum() + 2;
		} else {
			columnNo = columnValueMap.get(ReportConstants.ENDINGCOLUMN) + 2;
		}
		columnValueMap.put(ReportConstants.STARTINGCOLUMN, columnNo);
		spreadsheet.addMergedRegion(new CellRangeAddress(1, 1, columnNo,(columnNo + 1 + currency.size())));
		createCellWithStyle(spreadsheet, requiredType, columnNo, row,headingStyle);
		row = ExcelUtils.getRow(spreadsheet, rowNo++);
		columnNo = spreadsheet.getRow(rowNo - 1).getLastCellNum() + 2;
		createCellWithStyle(spreadsheet, "BDM", columnNo++, row, dataStyle);
		createCellWithStyle(spreadsheet, "No Of " + requiredType, columnNo++, row, dataStyle);
		if (currency.size() > 1) {
			spreadsheet.addMergedRegion(new CellRangeAddress(rowNo - 1, rowNo - 1, columnNo, columnNo + 1));
			if (requiredType.contains("Win"))
				createCellWithStyle(spreadsheet, "Win deal Value", columnNo, row, dataStyle);
			else
				createCellWithStyle(spreadsheet, "Loss deal value", columnNo, row, dataStyle);
			row = ExcelUtils.getRow(spreadsheet, rowNo++);
			createCellWithStyle(spreadsheet, "", columnNo-1, row, dataStyle); // Empty columns in currency Row
			createCellWithStyle(spreadsheet, "", columnNo-2, row, dataStyle); // Empty columns in currency Row
			createCellWithStyle(spreadsheet, currency.get(0), columnNo, row, dataStyle);
			createCellWithStyle(spreadsheet, currency.get(1), columnNo + 1, row, dataStyle);
			columnValueMap.put(ReportConstants.ENDINGROW, rowNo);
		} else {
			columnValueMap.put(ReportConstants.ENDINGROW, rowNo);
			if (requiredType.contains("Win"))
				createCellWithStyle(spreadsheet,"Win deal Value(" + currency.get(0) + ")", columnNo, row, dataStyle);
			else
				createCellWithStyle(spreadsheet,"Loss deal value(" + currency.get(0) + ")", columnNo, row, dataStyle);
		}
		columnValueMap.put(ReportConstants.ENDINGCOLUMN, columnNo + 1);
		return columnValueMap;
	}

	private void createCellWithStyle(SXSSFSheet spreadsheet,
			String requiredType, int columnNo, SXSSFRow row,
			CellStyle headingStyle) {
		row.createCell(columnNo).setCellValue(requiredType);
		row.getCell(columnNo).setCellStyle(headingStyle);
		spreadsheet.autoSizeColumn(columnNo);
	} 
	
	private void writeValuesForWinsOrLoss(SXSSFSheet spreadsheet, List<OpportunitySummaryValue> oppSummaryValueList, Map<String, Integer> columnValueMap){
		
		SXSSFRow row;
		int rowValue = columnValueMap.get(ReportConstants.ENDINGROW);
		CellStyle dataStyle = ExcelUtils.createRowStyle((SXSSFWorkbook) spreadsheet.getWorkbook(), ReportConstants.DATAROW);		
		for (OpportunitySummaryValue oppSummaryValue : oppSummaryValueList) {
			row = ExcelUtils.getRow(spreadsheet, rowValue++);
			createCellWithStyle(spreadsheet, oppSummaryValue.getTitle(), columnValueMap.get(ReportConstants.STARTINGCOLUMN), row, dataStyle);
			createCellWithStyle(spreadsheet, ""+oppSummaryValue.getCount(), (columnValueMap.get(ReportConstants.STARTINGCOLUMN) + 1), row, dataStyle);
			createCellWithStyle(spreadsheet, ""+oppSummaryValue.getBidValues().get(0).getValue(), (columnValueMap.get(ReportConstants.STARTINGCOLUMN) + 2), row, dataStyle);
			if(oppSummaryValue.getBidValues().size()>1){
				createCellWithStyle(spreadsheet, ""+oppSummaryValue.getBidValues().get(1).getValue(), (columnValueMap.get(ReportConstants.STARTINGCOLUMN) + 3), row, dataStyle);
				
			}
			
		}

		
	}
	
	
//	private void createTitle(SXSSFSheet spreadsheet, List<String> currency) {
//
//		spreadsheet.addMergedRegion(new CellRangeAddress(1, 1, 0,(1+currency.size())));
//	}

	public void addItemToList(List<String> itemList, List<String> targetList){
		if(itemList.contains("All") || itemList.isEmpty()){
			targetList.add("");
		} else {
			targetList.addAll(itemList);
		}
	}
	
	public void addItemToListGeo(List<String> itemList, List<String> targetList){
		if(itemList.contains("All") || itemList.isEmpty()){
			targetList.add("");
		} else {
			targetList.addAll(geographyMappingTRepository.findByDisplayGeography(itemList));
		}
	}
}
