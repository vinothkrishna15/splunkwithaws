package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunitySubSpLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.OpportunityWinLossFactorsT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.NotesTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.OpportunitySubSpLinkTRepository;
import com.tcs.destination.data.repository.OpportunityWinLossFactorsTRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.UserAccessPrivilegeQueryBuilder;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.FieldsMap;
import com.tcs.destination.utils.ReportConstants;

/*
 * This service handles the BDM Detailed report functionalities
 */
@Service
public class BDMDetailedReportService {

	private static final Logger logger = LoggerFactory.getLogger(BDMDetailedReportService.class);
	
    private static final DateFormat actualFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat desiredFormat = new SimpleDateFormat("MM/dd/yy");
	
	
	private static final String BDM_DETAILED_REPORT_PREFIX = "select user_id, opportunity from ( ";
	
	private static final String BDM_DETAILED_REPORT_PART1 = " (select distinct user_id, (OPP.opportunity_id) as opportunity from opportunity_t OPP "
			+ " join user_t USR on USR.user_id=OPP.opportunity_owner "
			+ " join geography_country_mapping_t GCMT on GCMT.country=OPP.country join geography_mapping_t GMT on GMT.geography = GCMT.geography " 
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id "
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp join customer_master_t CMT on opp.customer_id = CMT.customer_id where ";
	
	private static final String BDM_DETAILED_REPORT_PART2 = " UNION (select distinct user_id, (OPP.opportunity_id) as opportunity from opportunity_t OPP " 
			+ " join geography_country_mapping_t GCMT on GCMT.country=OPP.country join geography_mapping_t GMT on GMT.geography = GCMT.geography " 
			+ " join bid_details_t bidt on opp.opportunity_id = bidt.opportunity_id join bid_office_group_owner_link_t bofg on bidt.bid_id = bofg.bid_id " 
			+ " join user_t USR on USR.user_id=bofg.bid_office_group_owner  "
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id "
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp join customer_master_t CMT on opp.customer_id = CMT.customer_id where ";
	
	private static final String BDM_DETAILED_REPORT_PART3 = " UNION (select distinct sales_support_owner, (OPP.opportunity_id) as opportunity from opportunity_t OPP " 
			+ " join opportunity_sales_support_link_t OSSLT on OSSLT.opportunity_id=OPP.opportunity_id "
			+ " join user_t USR on USR.user_id=OSSLT.sales_support_owner "
			+ " join geography_country_mapping_t GCMT on GCMT.country=OPP.country join geography_mapping_t GMT on GMT.geography = GCMT.geography "
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id "
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp join customer_master_t CMT on opp.customer_id = CMT.customer_id where ";
	
	private static final String SALES_STAGE_CODE = " sales_stage_code in (";
	
	private static final String OPPORTUNITY_OWNER = " OPP.opportunity_owner in (";
	
	private static final String BID_OFFICE_OWNER = " bofg.bid_office_group_owner in (";
	
	private static final String SALES_SUPPORT_OWNER = " OSSLT.sales_support_owner in (";
	
	private static final String SALES_STAGE_AND_DEAL_CLOSURE_DATE = " AND ((OPP.sales_stage_code between 0 and 8) "
			+ " OR (OPP.deal_closure_date between ('";
	
	private static final String DEAL_CLOSURE_END_DATE = " AND ('";
	
	private static final String GEO_COND_PREFIX = "GMT.geography in (";
	private static final String SUBSP_COND_PREFIX = "SSMT.display_sub_sp in (";
	private static final String COUNTRY_COND_PREFIX = "OPP.country in (";
	
	private static final String BDM_DETAILED_REPORT_SUFFIX =") as bdmUserAndOppId order by user_id ";
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	OpportunityRepository opportunityRepository;
	
	@Autowired
	GeographyRepository geographyRepository;
	
	@Autowired
	BeaconConverterService beaconConverterService;
	
	@Autowired
	UserAccessPrivilegesRepository userAccessPrivilegesRepository;
	
	@Autowired
	UserAccessPrivilegeQueryBuilder userAccessPrivilegeQueryBuilder;
	
	@Autowired
	BDMReportsService bdmReportsService;
	
	@Autowired
	OpportunityWinLossFactorsTRepository opportunityWinLossFactorsTRepository;
	
	@Autowired
	OpportunitySubSpLinkTRepository opportunitySubSpLinkTRepository;
	
	@Autowired
	NotesTRepository notesTRepository;

	/**
	 * This Method used to BDM Performance detailed report in excel format
	 * @param financialYear
	 * @param from
	 * @param to
	 * @param geography
	 * @param country
	 * @param currency
	 * @param servicelines
	 * @param salesStage
	 * @param opportunityOwners
	 * @param userId
	 * @param fields
	 * @param iou 
	 * @return
	 * @throws Exception
	 */
	public InputStreamResource getBdmDetailedReport(String financialYear, String from, String to,
			List<String> geography,  List<String> country, List<String> currency, List<String> servicelines, List<String> iou,
			List<Integer> salesStage, List<String> opportunityOwners,
			String userId, List<String> fields) throws Exception {
		SXSSFWorkbook workbook = new SXSSFWorkbook(50);
		bdmReportsService.getBDMReportTitlePage(workbook, financialYear, from, to, geography, country, currency, servicelines, salesStage, opportunityOwners, userId, "Detailed", iou);
		getBdmDetailedReportExcel(financialYear, from, to, geography, country, currency, servicelines, salesStage, opportunityOwners, userId, workbook, fields, iou);
		ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
		workbook.write(byteOutPutStream);
		byteOutPutStream.flush();
		byteOutPutStream.close();
		byte[] bytes = byteOutPutStream.toByteArray();
		InputStreamResource inputStreamResource= new InputStreamResource(new ByteArrayInputStream(bytes));
		return inputStreamResource;
	}

	/**
	 * This Method used to set the bdm performance details based on user access privileges
	 * @param financialYear
	 * @param from
	 * @param to
	 * @param geography
	 * @param country
	 * @param currency
	 * @param serviceLines
	 * @param salesStage
	 * @param opportunityOwners
	 * @param userId
	 * @param workbook
	 * @param fields
	 * @param iou 
	 * @throws Exception
	 */
	public void getBdmDetailedReportExcel(String financialYear, String from, String to, List<String> geography, List<String> country, List<String> currency,
			List<String> serviceLines, List<Integer> salesStage, List<String> opportunityOwners, String userId,
			SXSSFWorkbook workbook, List<String> fields, List<String> iou) throws Exception {
		UserT user = userService.findByUserId(userId);
		boolean isIncludingSupervisor = false;
		if (user != null) {
			Date fromDate=new Date();
			Date toDate = new Date();
			List<String> geoList = new ArrayList<String>();
			List<String> countryList = new ArrayList<String>();
			List<String> iouList = new ArrayList<String>();
			List<String> serviceLinesList = new ArrayList<String>();
			
			String userGroup = user.getUserGroupMappingT().getUserGroup();
			
			List<String> opportunityOwnerList = new ArrayList<String>();
			addItemToListGeo(geography,geoList);
			ExcelUtils.addItemToList(country, countryList);
			ExcelUtils.addItemToList(iou, iouList);
			ExcelUtils.addItemToList(serviceLines,serviceLinesList);
			ExcelUtils.addItemToList(opportunityOwners, opportunityOwnerList);
			
			if(from.equals("") && to.equals("")){
				if(financialYear.equals("")){
					fromDate = DateUtils.getDateFromFinancialYear(DateUtils.getCurrentFinancialYear(), true);
					toDate = DateUtils.getDateFromFinancialYear(DateUtils.getCurrentFinancialYear(), false);
				} else {
					fromDate = DateUtils.getDateFromFinancialYear(financialYear, true);
					toDate = DateUtils.getDateFromFinancialYear(financialYear, false);
				}
			} else{
				fromDate = DateUtils.getDateFromMonth(from, true);
				toDate = DateUtils.getDateFromMonth(to, false);
			}
			
		    if (UserGroup.contains(userGroup)) {
		    List<String> users = getRequiredBDMs(userId, opportunityOwners);
		   
		    // Validate user group, BDM's & BDM supervisor's are not authorized for this service
			switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
			case BDM:
			case PRACTICE_OWNER:
			case REPORTING_TEAM:
				logger.error("User is not authorized to access this service");
			    throw new DestinationException(HttpStatus.UNAUTHORIZED, " User is not authorised to access this service ");
			case BDM_SUPERVISOR:
			case PRACTICE_HEAD:
				 if(users.isEmpty()){
				    	logger.error("Given BDM is not his Subordinate");
				    	throw new DestinationException(HttpStatus.NOT_FOUND, "Given BDM is not his Subordinate");
				    }
				isIncludingSupervisor = false;
				getBDMSupervisorPerformanceReport(users, fromDate, toDate, geoList, salesStage, serviceLinesList, iouList,
						countryList, currency, workbook, fields, isIncludingSupervisor);
				break;
			case GEO_HEADS:
			case IOU_HEADS:
			case PMO:
				if(users.isEmpty()){
			    	logger.error("Given BDM is not his Subordinate");
			    	throw new DestinationException(HttpStatus.NOT_FOUND, "Given BDM is not his Subordinate");
				    }
				isIncludingSupervisor = true;
//				setBDMOpportunityDetailsBasedOnUserAccessPrivileges(userId, fromDate, toDate, geoList, 
//						serviceLinesList, iouList, countryList, currency, salesStage, users, fields, isIncludingSupervisor, workbook);
				getBDMSupervisorPerformanceReport(users, fromDate, toDate, geoList, salesStage, serviceLinesList, iouList, countryList, currency, workbook, fields,isIncludingSupervisor);
				break;
			default :
				List<String> userIdList = new ArrayList<String>();
				if(opportunityOwners.isEmpty()){
					userIdList = userRepository.findAllUserIds();
				} else {
					userIdList.addAll(opportunityOwners);
				}
				isIncludingSupervisor = true;
//				setBDMOpportunityDetailsBasedOnUserAccessPrivileges(userId, fromDate, toDate, geoList, 
//						serviceLinesList, iouList, countryList, currency, salesStage, userIdList, 
//						fields, isIncludingSupervisor, workbook);
				getBDMSupervisorPerformanceReport(userIdList, fromDate, toDate, geoList, salesStage, serviceLinesList, iouList, countryList, currency, workbook, fields,isIncludingSupervisor);
				break;
				}
		    }
		} else {
		    logger.error("NOT_FOUND: User not found: {}", userId);
		    throw new DestinationException(HttpStatus.NOT_FOUND, "User not found: " + userId);
		}
	}
		
		/**
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param geoList
	 * @param serviceLinesList
	 * @param iouList
	 * @param countryList
	 * @param currency
	 * @param salesStage
	 * @param userIdList
	 * @param fields
	 * @param isIncludingSupervisor
	 * @param workbook
		 * @throws Exception 
	 */
//	private void setBDMOpportunityDetailsBasedOnUserAccessPrivileges(String userId, Date fromDate, Date toDate, List<String> geoList,
//			List<String> serviceLinesList, List<String> iouList, List<String> countryList, List<String> currency,
//			List<Integer> salesStage, List<String> userIdList, List<String> fields, boolean isIncludingSupervisor,
//			SXSSFWorkbook workbook) throws Exception {
//		getBDMSupervisorPerformanceReport(userIdList, fromDate, toDate, geoList, salesStage, serviceLinesList, iouList, countryList, currency, workbook, fields,isIncludingSupervisor);
//	}

		/**
		 * This Method retrieves the BDM Supervisor performance details
		 * 
		 * @param users * @param financialYear * @param geoList * @param serviceLinesList
		 * @param salesStage 
		 * @param countryList * @param workbook
		 * @param iouList 
		 * @param workbook 
		 * @param isIncludingSupervisor 
		 * @param fields 
		 * @param fields 
		 * @throws Exception 
		 */
		private void getBDMSupervisorPerformanceReport(List<String> users, Date fromDate, Date toDate, List<String> geoList, List<Integer> salesStage, List<String> serviceLinesList,
				List<String> iouList, List<String> countryList, List<String> currency, SXSSFWorkbook workbook, List<String> fields, boolean isIncludingSupervisor) throws Exception {
			logger.debug("Inside getBDMSupervisorPerformanceExcelReport() method");
			List<OpportunityT> opportunityList = opportunityRepository.getBDMSupervisorOpportunities(users, salesStage, geoList, serviceLinesList, countryList, fromDate, toDate);
			
			if(opportunityList.isEmpty()){
				logger.error("Report could not be downloaded, as no details are available for user selection and privilege combination");
				throw new DestinationException(HttpStatus.NOT_FOUND, "Report could not be downloaded, as no details are available for user selection and privilege combination");
			}
			setBDMsOpportunitiesToExcel(opportunityList, currency, workbook, fields, isIncludingSupervisor);
		}
		

		/**
		 * @param opportunityList
		 * @param currency
		 * @param workbook
		 * @param fields
		 * @param isIncludingSupervisor
		 * @throws Exception 
		 */
		private void setBDMsOpportunitiesToExcel(List<OpportunityT> opportunityList, List<String> currency,
				SXSSFWorkbook workbook, List<String> fields, boolean isIncludingSupervisor) throws Exception {
			logger.info("Inside setBDMsOpportunitiesToExcel method");
			SXSSFSheet spreadSheet = (SXSSFSheet) workbook.createSheet("Detailed Report");
			int currentRow = 0;
			CellStyle cellStyle = ExcelUtils.createRowStyle(workbook, ReportConstants.REPORTHEADER);
			SXSSFRow row = null;
			if(fields.isEmpty()){
				row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
				setBDMSupervisorMandatoryHeaderToExcel(row, currentRow, spreadSheet, cellStyle, currency, isIncludingSupervisor);
				for(OpportunityT opportunity:opportunityList){
					currentRow++;
					row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
					setBDMReportMandatoryDetails(row, spreadSheet, currency, isIncludingSupervisor, opportunity);
				}
			} else {
				setBDMSupervisorHeaderAlongWithOptionalFieldsToExcel(currentRow, spreadSheet, cellStyle, currency, fields, isIncludingSupervisor);
				if(currency.size()>1){
					currentRow++;
				}
				setBDMReportAlongWithOptionalFieldsDetail(currentRow, spreadSheet, opportunityList, currency, fields, isIncludingSupervisor);
			}
		}
			
		/**
		 * @param currentRow
		 * @param spreadSheet
		 * @param object
		 * @param currency
		 * @param fields
		 * @param isIncludingSupervisor
		 * @throws Exception 
		 */
		private void setBDMReportAlongWithOptionalFieldsDetail(int currentRow, SXSSFSheet spreadSheet, List<OpportunityT> opportunityList, List<String> currency,
				List<String> fields, boolean isIncludingSupervisor) throws Exception {
			logger.info("Inside setBDMReportAlongWithOptionalFieldsDetail method");
			SXSSFRow row = null;
			boolean projectDVFlag = fields.contains(ReportConstants.DIGITALDEALVALUEPROJECTCURRENCY);
			boolean opportunityNameFlag = fields.contains(ReportConstants.OPPNAME);
			boolean targetBidSubDtFlag = fields.contains(ReportConstants.TARGETBIDSUBMISSIONDATE);
			boolean winProbFlag = fields.contains(ReportConstants.WINPROBABILITY);
			boolean factorForWLFlag = fields.contains(ReportConstants.FACTORSFORWINLOSS);
			boolean descForWLFlag = fields.contains(ReportConstants.DEALCLOSURECOMMENTS);
			boolean dealMarkFlag = fields.contains(ReportConstants.DEALREMARKSNOTES);
			
			boolean subSpFlag = fields.contains(ReportConstants.SUBSP);
			boolean dealClosureDateFlag = fields.contains(ReportConstants.DEALCLOSUREDATE);
			boolean createdByFlag = fields.contains(ReportConstants.CREATEDBY);
			boolean createdDateFlag = fields.contains(ReportConstants.CREATEDDATE);
			boolean modifiedByFlag = fields.contains(ReportConstants.MODIFIEDBY);
			boolean modifiedDateFlag = fields.contains(ReportConstants.MODIFIEDDATE);
			
			for(OpportunityT opportunity:opportunityList){
				row = (SXSSFRow) spreadSheet.createRow((short) ++currentRow);
				setBDMReportMandatoryDetails(row, spreadSheet, currency, isIncludingSupervisor, opportunity);
			
				int currentCol=12;
				if(isIncludingSupervisor){
					currentCol=13;
				}
				int colValue = currentCol;
				if (currency.size() > 1) {
					colValue = currentCol+1;
				}
				
				//set project deal value and deal currency
				if (projectDVFlag) {
					if(opportunity.getDigitalDealValue() != null){
						row.createCell(colValue).setCellValue(opportunity.getDigitalDealValue());
					} else {
						row.createCell(colValue).setCellValue(0);
					}
					colValue++;
					if(opportunity.getDealCurrency() != null){
						row.createCell(colValue).setCellValue(opportunity.getDealCurrency());
						}
					colValue++;
					}
				
				
			for (int i = 0; i < colValue; i++) {
				if(opportunity.getBidDetailsTs().size() > 0)
				spreadSheet.addMergedRegion(new CellRangeAddress(currentRow - 1, currentRow + opportunity.getBidDetailsTs().size() - 2, i, i));
			}
			
			// set win probability
			if (winProbFlag) {
				if (opportunity.getBidDetailsTs().size() > 0) {
					for (int bid = 0; bid < opportunity.getBidDetailsTs().size(); bid++) {
						row = ExcelUtils.getRow(spreadSheet, (currentRow + bid));
						if(opportunity.getBidDetailsTs().get(bid).getWinProbability() != null) {
							row.createCell(colValue).setCellValue(opportunity.getBidDetailsTs().get(bid).getWinProbability());
						}
					}
					colValue++;
				} else {
					colValue++;
				}
			}
			
			//set target bid submission date
			if (targetBidSubDtFlag) {
				if (opportunity.getBidDetailsTs().size() > 0) {
					for (int bid = 0; bid < opportunity.getBidDetailsTs().size(); bid++) {
						row = ExcelUtils.getRow(spreadSheet, (currentRow + bid));
						if(opportunity.getBidDetailsTs().get(bid).getTargetBidSubmissionDate() != null) {
							row.createCell(colValue).setCellValue(opportunity.getBidDetailsTs().get(bid).getTargetBidSubmissionDate().toString());
						}
					}
					colValue++;
				} else {
					colValue++;
				}
			}
			
			//set opportunity name
			if (opportunityNameFlag) {
				row.createCell(colValue).setCellValue(opportunity.getOpportunityName());
				colValue++;
			}

			//set factors for win loss
			if (factorForWLFlag) {
				List<String> oppFactorsForWinLossList=opportunityWinLossFactorsTRepository.findWinLossFactorByOpportunityId(opportunity.getOpportunityId());
				row.createCell(colValue).setCellValue(oppFactorsForWinLossList.toString().replace("[", "").replace("]", ""));
				colValue++;
			}
			
			//set description for win loss
			if (descForWLFlag) {
				if(opportunity.getDealClosureComments() != null) {
					row.createCell(colValue).setCellValue(opportunity.getDealClosureComments());
				}
				colValue++;
			}
			
			// set deal remarks notes
			if (dealMarkFlag) {
				List<String> oppDealRemarksNotesList=notesTRepository.findDealRemarksNotesByOpportunityId(opportunity.getOpportunityId());
				row.createCell(colValue).setCellValue(oppDealRemarksNotesList.toString().replace("[", "").replace("]", ""));;
				colValue++;
				}
			
			//Setting SubSp
			if (subSpFlag) {
				List<String> oppSubSpList = opportunitySubSpLinkTRepository.findSubSpByOpportunityId(opportunity.getOpportunityId());
				if(!oppSubSpList.isEmpty()){
					row.createCell(colValue).setCellValue(oppSubSpList.toString().replace("]", "").replace("[", ""));
				}
				colValue++;
			}

			//set deal closure date
			if (dealClosureDateFlag) {
				if(opportunity.getDealClosureDate() != null) {
					row.createCell(colValue).setCellValue(opportunity.getDealClosureDate().toString());
				}
				colValue++;
			}
			
			//set created by 
			if (createdByFlag) {
				row.createCell(colValue).setCellValue(opportunity.getCreatedByUser().getUserName());
				colValue++;
			}
			//set created date
			if (createdDateFlag) {
				Timestamp createdDateTimeStamp = opportunity.getCreatedDatetime();
				Date createdDate = DateUtils.toDate(createdDateTimeStamp);
				
				String dateOfCreation = DateUtils.convertDateToString(createdDate);
				row.createCell(colValue).setCellValue(getFormattedDate(opportunity.getCreatedDatetime().toString(), actualFormat, desiredFormat));
				colValue++;
			}
			//set modified by 
			if (modifiedByFlag) {
				row.createCell(colValue).setCellValue(getFormattedDate(opportunity.getModifiedDatetime().toString(), actualFormat, desiredFormat));
				colValue++;
			}
			
			//set modified date 
			if (modifiedDateFlag) {
				Timestamp modifiedDateTimeStamp = opportunity.getModifiedDatetime();
				Date modifiedDate = DateUtils.toDate(modifiedDateTimeStamp);
				String dateOfModification = DateUtils.convertDateToString(modifiedDate);
				row.createCell(colValue).setCellValue(dateOfModification);
				colValue++;
			}
			}
		}
			

		/**
		 * @param row
		 * @param object
		 * @param spreadSheet
		 * @param currency
		 * @param isIncludingSupervisor
		 * @param opportunity
		 */
		private void setBDMReportMandatoryDetails(SXSSFRow row, SXSSFSheet spreadSheet, List<String> currencyList,
				boolean isIncludingSupervisor, OpportunityT opportunity) {
				List<String> salesSupportOwnerList = new ArrayList<String>();
				List<String> supervisorList = new ArrayList<String>();
				int columnNo = 0;
				UserT oppOwner = userRepository.findByUserId(opportunity.getOpportunityOwner());
				
				for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity.getOpportunitySalesSupportLinkTs()) {
					UserT user = userRepository.findByUserId(opportunitySalesSupportLinkT.getSalesSupportOwner());
					salesSupportOwnerList.add(user.getUserName());
					supervisorList.add(user.getSupervisorUserName());
				}
				String salesOwner = salesSupportOwnerList.toString().replace("[", "").replace("]", "");
				String salesOwnerSupervisor = supervisorList.toString().replace("[", "").replace("]", "");
				
				//set BDM
				row.createCell(columnNo++).setCellValue(oppOwner.getUserName()+", "+salesOwner);
				
				//set supervisor
				if(isIncludingSupervisor){
					row.createCell(columnNo++).setCellValue(oppOwner.getSupervisorUserName()+", "+salesOwnerSupervisor);
				}
				
				//set opportunity owner name
				row.createCell(columnNo++).setCellValue(oppOwner.getUserName());
				//set sales support owners
				row.createCell(columnNo++).setCellValue(salesOwner);
				//set display_sub_sp
				List<String> oppDisplaySubSpList = opportunitySubSpLinkTRepository.findSubSpByOpportunityId(opportunity.getOpportunityId());
				if(!oppDisplaySubSpList.isEmpty()){
					row.createCell(columnNo++).setCellValue(oppDisplaySubSpList.toString().replace("]", "").replace("[", ""));
				}
				//set display IOU
				row.createCell(columnNo++).setCellValue(opportunity.getCustomerMasterT().getIouCustomerMappingT().getDisplayIou());
				//set display geography
				row.createCell(columnNo++).setCellValue(opportunity.getGeographyCountryMappingT().getGeographyMappingT().getDisplayGeography());
				//set country
				row.createCell(columnNo++).setCellValue(opportunity.getCountry());
				//set group customer name
				row.createCell(columnNo++).setCellValue(opportunity.getCustomerMasterT().getGroupCustomerName());
				//set customer name
				row.createCell(columnNo++).setCellValue(opportunity.getCustomerMasterT().getCustomerName());
				//set sales stage code
				row.createCell(columnNo++).setCellValue(opportunity.getSalesStageMappingT().getSalesStageDescription());
				
				//set expected date of outcome
				if(!opportunity.getBidDetailsTs().isEmpty()){
					if(opportunity.getBidDetailsTs().get(0).getExpectedDateOfOutcome()!=null){
						row.createCell(columnNo++).setCellValue(opportunity.getBidDetailsTs().get(0).getExpectedDateOfOutcome().toString());
					}
				} else {
					row.createCell(columnNo++).setCellValue(Constants.SPACE);
				}
				//set Digital deal value
				int i = 0;
				for(String currency : currencyList) {
					if (opportunity.getDigitalDealValue() != null && opportunity.getDealCurrency() != null) {
						row.createCell(columnNo + i).setCellValue(beaconConverterService.convert(opportunity.getDealCurrency(),currency, 
								opportunity.getDigitalDealValue().doubleValue()).doubleValue());
					} else {
						row.createCell(columnNo + i).setCellValue(0);
					}
					i++;
				}
			}
			

		/**
		 * This Method used to set bdms opportunity details to excel
		 * 
		 * @param userIdAndOppList
		 * @param currency
		 * @param workbook
		 * @param fields
		 * @param isIncludingSupervisor
		 */
		private void setBDMsOpportunitiesAndNameToExcel(List<Object[]> userIdAndOppList, List<String> currency,
				SXSSFWorkbook workbook, List<String> fields, boolean isIncludingSupervisor) {
			logger.info("Inside setBDMsOpportunitiesAndNameToExcel method");
			SXSSFSheet spreadSheet = (SXSSFSheet) workbook.createSheet("Detailed Report");
			int currentRow = 0;
			CellStyle cellStyle = ExcelUtils.createRowStyle(workbook, ReportConstants.REPORTHEADER);
			SXSSFRow row = null;
			if(fields.isEmpty()){
				row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
				setBDMSupervisorMandatoryHeaderToExcel(row, currentRow, spreadSheet, cellStyle, currency, isIncludingSupervisor);
				for(Object[] userIdAndOpp:userIdAndOppList){
					String opportunityId = 	(String) userIdAndOpp[1];
					OpportunityT opportunity = opportunityRepository.findByOpportunityId(opportunityId);
					currentRow++;
					row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
					setBDMSupervisorMandatoryDetails(row, userIdAndOpp, spreadSheet, currency, isIncludingSupervisor, opportunity);
				}
			} else {
				setBDMSupervisorHeaderAlongWithOptionalFieldsToExcel(currentRow, spreadSheet, cellStyle, currency, fields, isIncludingSupervisor);
				if(currency.size()>1){
					currentRow++;
				}
				setBDMSupervisorAlongWithOptionalFieldsDetail(currentRow, spreadSheet, userIdAndOppList, currency, fields, isIncludingSupervisor);
			}
		}

		/**
		 * This Method is used to set bdm supervisor details along with optional fields to excel
		 * @param currentRow
		 * @param spreadSheet
		 * @param userIdAndOppList
		 * @param currency
		 * @param fields
		 * @param isIncludingSupervisor
		 */
		private void setBDMSupervisorAlongWithOptionalFieldsDetail(int currentRow, SXSSFSheet spreadSheet,
				List<Object[]> userIdAndOppList, List<String> currency, List<String> fields, boolean isIncludingSupervisor) {
			logger.info("Insie setBDMSupervisorAlongWithOptionalFieldsDetail method");
			SXSSFRow row = null;
			boolean projectDVFlag = fields.contains(ReportConstants.DIGITALDEALVALUEPROJECTCURRENCY);
			boolean opportunityNameFlag = fields.contains(ReportConstants.OPPNAME);
			boolean targetBidSubDtFlag = fields.contains(ReportConstants.TARGETBIDSUBMISSIONDATE);
			boolean winProbFlag = fields.contains(ReportConstants.WINPROBABILITY);
			boolean factorForWLFlag = fields.contains(ReportConstants.FACTORSFORWINLOSS);
			boolean descForWLFlag = fields.contains(ReportConstants.DEALCLOSURECOMMENTS);
			boolean dealMarkFlag = fields.contains(ReportConstants.DEALREMARKSNOTES);
			
			for(Object[] userIdAndOpp:userIdAndOppList){
				OpportunityT opportunity = opportunityRepository.findByOpportunityId((String) userIdAndOpp[1]);
				row = (SXSSFRow) spreadSheet.createRow((short) ++currentRow);
				setBDMSupervisorMandatoryDetails(row, userIdAndOpp, spreadSheet, currency, isIncludingSupervisor, opportunity);
				int currentCol=11;
				if(isIncludingSupervisor){
					currentCol=12;
				}
				int colValue = currentCol;
				if (currency.size() > 1) {
					colValue = currentCol+1;
				}
				
				//set project deal value and deal currency
				if (projectDVFlag) {
					if(opportunity.getDigitalDealValue() != null){
						row.createCell(colValue).setCellValue(opportunity.getDigitalDealValue());
					} else {
						row.createCell(colValue).setCellValue(0);
					}
					colValue++;
					if(opportunity.getDealCurrency() != null){
						row.createCell(colValue).setCellValue(opportunity.getDealCurrency());
						} else {
							row.createCell(colValue).setCellValue("");
						}
					colValue++;
					}
				
				
			for (int i = 0; i < colValue; i++) {
				if(opportunity.getBidDetailsTs().size() > 0)
				spreadSheet.addMergedRegion(new CellRangeAddress(currentRow - 1, currentRow + opportunity.getBidDetailsTs().size() - 2, i, i));
			}
			
			// set win probability
			if (winProbFlag) {
				if (opportunity.getBidDetailsTs().size() > 0) {
					for (int bid = 0; bid < opportunity.getBidDetailsTs().size(); bid++) {
						row = ExcelUtils.getRow(spreadSheet, (currentRow + bid));
						if(opportunity.getBidDetailsTs().get(bid).getWinProbability() != null) {
							row.createCell(colValue).setCellValue(opportunity.getBidDetailsTs().get(bid).getWinProbability());
						} else {
							row.createCell(colValue).setCellValue(Constants.SPACE);
						}
					}
					colValue++;
				} else {
					row.createCell(colValue).setCellValue(Constants.SPACE);
					colValue++;
				}
			}
			
			//set target bid submission date
			if (targetBidSubDtFlag) {
				if (opportunity.getBidDetailsTs().size() > 0) {
					for (int bid = 0; bid < opportunity.getBidDetailsTs().size(); bid++) {
						row = ExcelUtils.getRow(spreadSheet, (currentRow + bid));
						if(opportunity.getBidDetailsTs().get(bid).getTargetBidSubmissionDate() != null) {
							row.createCell(colValue).setCellValue(opportunity.getBidDetailsTs().get(bid).getTargetBidSubmissionDate().toString());
						} else {
								row.createCell(colValue).setCellValue(Constants.SPACE);
						}
					}
					colValue++;
				} else {
					row.createCell(colValue).setCellValue(Constants.SPACE);
					colValue++;
				}
			}
			
			//set opportunity name
			if (opportunityNameFlag) {
				row.createCell(colValue).setCellValue(opportunity.getOpportunityName());
				colValue++;
			}

			//set factors for win loss
			if (factorForWLFlag) {
				List<String> factorsForWinLossList = new ArrayList<String>();
				for (OpportunityWinLossFactorsT opportunityWinLossFactorsT : opportunity
						.getOpportunityWinLossFactorsTs()) {
					factorsForWinLossList.add(opportunityWinLossFactorsT.getWinLossFactor());
				}
				row.createCell(colValue).setCellValue(factorsForWinLossList.toString().replace("[", "").replace("]", ""));
				colValue++;
			}
			
			//set description for win loss
			if (descForWLFlag) {
				if(opportunity.getDealClosureComments() != null)
				row.createCell(colValue).setCellValue(opportunity.getDealClosureComments());
				else
					row.createCell(colValue).setCellValue("");
				colValue++;
			}
			
			// set deal remarks notes
			if (dealMarkFlag) {
				List<String> dealRemarksNotesList = new ArrayList<String>();
				for (NotesT notesT : opportunity.getNotesTs()) {
					dealRemarksNotesList.add(notesT.getNotesUpdated());
				}
				row.createCell(colValue).setCellValue(dealRemarksNotesList.toString().replace("[", "").replace("]", ""));;
				colValue++;
				}
			
			}
		}

		/**
		 * This Method used to set bdm supervisor details with mandatory fields to excel
		 * @param row
		 * @param userIdAndOpp
		 * @param currentRow
		 * @param spreadSheet
		 * @param currencyList
		 * @param isIncludingSupervisor
		 * @param opportunity2 
		 */
		private void setBDMSupervisorMandatoryDetails(SXSSFRow row, Object[] userIdAndOpp, SXSSFSheet spreadSheet,
				List<String> currencyList, boolean isIncludingSupervisor, OpportunityT opportunity) {
			logger.info("Inside setBDMSupervisorMandatoryDetails method");
			List<String> salesSupportOwnerList = new ArrayList<String>();
			List<String> supervisorList = new ArrayList<String>();
			int columnNo = 0;
			UserT bdmUser = userRepository.findByUserId((String) userIdAndOpp[0]);
			UserT oppOwner = userRepository.findByUserId(opportunity.getOpportunityOwner());
			
			for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity.getOpportunitySalesSupportLinkTs()) {
				UserT user = userRepository.findByUserId(opportunitySalesSupportLinkT.getSalesSupportOwner());
				salesSupportOwnerList.add(user.getUserName());
				supervisorList.add(user.getSupervisorUserName());
			}
			String salesOwner = salesSupportOwnerList.toString().replace("[", "").replace("]", "");
			
			//set BDM
			row.createCell(columnNo++).setCellValue(bdmUser.getUserName());
			
			//set supervisor
			if(isIncludingSupervisor){
			row.createCell(columnNo++).setCellValue(bdmUser.getSupervisorUserName());
			}
			//set display_sub_sp
			
			List<String> displaySubSpList = new ArrayList<String>();
			for (OpportunitySubSpLinkT displaySubSp : opportunity.getOpportunitySubSpLinkTs()) {
				displaySubSpList.add(displaySubSp.getSubSpMappingT().getDisplaySubSp());
			}
			row.createCell(columnNo++).setCellValue(displaySubSpList.toString().replace("[", "").replace("]", ""));
			//set opportunity owner name
			row.createCell(columnNo++).setCellValue(oppOwner.getUserName());
			//set sales support owners
			row.createCell(columnNo++).setCellValue(salesOwner);
			//set display geography
			row.createCell(columnNo++).setCellValue(opportunity.getGeographyCountryMappingT().getGeographyMappingT().getDisplayGeography());
			//set country
			row.createCell(columnNo++).setCellValue(opportunity.getCountry());
			//set group customer name
			row.createCell(columnNo++).setCellValue(opportunity.getCustomerMasterT().getGroupCustomerName());
			//set customer name
			row.createCell(columnNo++).setCellValue(opportunity.getCustomerMasterT().getCustomerName());
			//set sales stage code
			row.createCell(columnNo++).setCellValue(opportunity.getSalesStageMappingT().getSalesStageDescription());
			
			//set expected date of outcome
			if(!opportunity.getBidDetailsTs().isEmpty()){
				if(opportunity.getBidDetailsTs().get(0).getExpectedDateOfOutcome()!=null){
					row.createCell(columnNo++).setCellValue(opportunity.getBidDetailsTs().get(0).getExpectedDateOfOutcome().toString());
				}  else {
					row.createCell(columnNo++).setCellValue(Constants.SPACE);
				}
			} else {
				row.createCell(columnNo++).setCellValue(Constants.SPACE);
			}
			//set Digital deal value
			int i = 0;
			for(String currency : currencyList) {
				if (opportunity.getDigitalDealValue() != null && opportunity.getDealCurrency() != null) {
					row.createCell(columnNo + i).setCellValue(beaconConverterService.convert(opportunity.getDealCurrency(),currency, 
							opportunity.getDigitalDealValue().doubleValue()).doubleValue());
				} else {
					row.createCell(columnNo + i).setCellValue(0);
				}
				i++;
			}
		}


		/**
		 * This Method used to set bdm supervisor mandatory fields to excel 
		 * @param row 
		 * @param currentRow
		 * @param spreadSheet
		 * @param cellStyle
		 * @param currency 
		 */
		private void setBDMSupervisorMandatoryHeaderToExcel(SXSSFRow row, int currentRow, SXSSFSheet spreadSheet, 
				CellStyle cellStyle, List<String> currency, boolean isIncludingSupervisor) {
			logger.info("Inside setBDMSupervisorMandatoryHeaderToExcel method");
			CellStyle currencyStyle = ExcelUtils.createRowStyle(
					(SXSSFWorkbook) spreadSheet.getWorkbook(), ReportConstants.REPORTHEADER1);
			List<String> headerList = new ArrayList<String>();
			headerList.add("BDM");
			if(isIncludingSupervisor){
				headerList.add("Supervisor");
			}
			headerList.add("Opportunity Owner");headerList.add("Sales Support Owners");headerList.add("Display Service Line");
			headerList.add("Display Geography");headerList.add("Display IOU");headerList.add("Country");headerList.add("Group Customer Name");
			headerList.add("Customer Name");headerList.add("Sales Stage");headerList.add("Expected Date Of Outcome");
			int columnNo = 0;
			for(String header:headerList) {
				row.createCell(columnNo).setCellValue(header);
				row.getCell(columnNo).setCellStyle(cellStyle);
				columnNo++;
			}
			if (currency.size() > 1) {
				row.createCell(columnNo).setCellValue(ReportConstants.DIGITALDEALVALUE);
				row.getCell(columnNo).setCellStyle(cellStyle);
				spreadSheet.addMergedRegion(new CellRangeAddress(0, 0, columnNo, columnNo + currency.size() - 1));
				
				SXSSFRow row1 = (SXSSFRow) spreadSheet.createRow(1);
				for (int i = 0; i < currency.size(); i++) {
					row1.createCell((columnNo + i)).setCellValue(currency.get(i));
					row1.getCell(columnNo + i).setCellStyle(currencyStyle);
				}
			} else {
				row.createCell(columnNo).setCellValue(ReportConstants.DIGITALDEALVALUE + "(" + currency.get(0) + ")");
				row.getCell(columnNo).setCellStyle(cellStyle);
			}
		}


		
		/**
		 * This Method used to set bdm supervisor header(both mandatory and optional fields) details to excel
		 * @param currentRow
		 * @param spreadSheet
		 * @param cellStyle
		 * @param currency
		 * @param fields 
		 * @param isIncludingSupervisor 
		 */
		private void setBDMSupervisorHeaderAlongWithOptionalFieldsToExcel(int currentRow, SXSSFSheet spreadSheet, 
				CellStyle cellStyle, List<String> currency, List<String> fields, boolean isIncludingSupervisor) {
			SXSSFRow row = null;
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
			setBDMSupervisorMandatoryHeaderToExcel(row, currentRow, spreadSheet, cellStyle, currency, isIncludingSupervisor);
			int columnNo = 13;
			if(isIncludingSupervisor){
				columnNo=14;
			}
			if(fields.contains("projectDealValue")){
				row.createCell(columnNo).setCellValue("Project Digital Deal Value");
				row.getCell(columnNo).setCellStyle(cellStyle);
				columnNo++;
			}
			for (String field : fields) {
				if(fields.contains(field)){
				row.createCell(columnNo).setCellValue(FieldsMap.bdmReportFieldMap.get(field));
				row.getCell(columnNo).setCellStyle(cellStyle);
				columnNo++;
				}
			}
		}

		/**
		 * This Method used to get opportunity ids based on user access privileges
		 * @param userId
		 * @param fromDate
		 * @param toDate
		 * @param geoList
		 * @param serviceLinesList
		 * @param countryList
		 * @param currency
		 * @param salesStage
		 * @param opportunityOwnerList
		 * @param fields
		 * @param isIncludingSupervisor
		 * @param workbook
		 * @throws Exception
		 */
		private void getBDMOpportunityIdsBasedOnUserAccessPrivileges(String userId, Date fromDate, Date toDate, List<String> geoList, List<String> serviceLinesList,  List<String> iouList, List<String> countryList,
				List<String> currency, List<Integer> salesStage, List<String> opportunityOwnerList, List<String> fields, boolean isIncludingSupervisor, SXSSFWorkbook workbook) throws Exception {
			logger.debug("Inside getOpportunityListBasedOnUserAccessPrivileges() method");
			// Form the native top revenue query string
			String queryString = getOpportunityListQueryString(userId, fromDate, toDate, geoList, countryList, serviceLinesList, salesStage, opportunityOwnerList);
			logger.debug("Query string: {}", queryString);
			// Execute the native revenue query string
			Query bdmReportQuery = entityManager.createNativeQuery(queryString);
			List<Object[]> bdmUserAndOppId = bdmReportQuery.getResultList();
			
			if (bdmUserAndOppId == null || bdmUserAndOppId.isEmpty()) {
				logger.error("NOT_FOUND: Report could not be downloaded, as no details are available for user selection and privilege combination");
				throw new DestinationException(HttpStatus.NOT_FOUND, "Report could not be downloaded, as no details are available for user selection and privilege combination");
			}
			setBDMsOpportunitiesAndNameToExcel(bdmUserAndOppId, currency, workbook, fields, isIncludingSupervisor);
		}


		/**
		 * This Method used construct query based on user access privileges, this query retrieves the list of opportunity ids and bdm details
		 * @param users 
		 * @param fromDate * @param toDate * @param geoList
		 * @param countryList * @param serviceLinesList * @return
		 * @param salesStage 
		 * @param opportunityOwnerList 
		 * @throws Exception
		 */
		private String getOpportunityListQueryString(String userId, Date fromDate, Date toDate, List<String> geoList,
				List<String> countryList, List<String> serviceLinesList, List<Integer> salesStage, List<String> opportunityOwnerList) throws Exception {
			logger.debug("Inside getBDMDetailedQueryString() method");
			
			StringBuffer queryBuffer = new StringBuffer(BDM_DETAILED_REPORT_PREFIX);
			queryBuffer.append(BDM_DETAILED_REPORT_PART1);
			// Get user access privilege groups 
			HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX, null, null);
			String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
			
			queryBuffer.append(SALES_STAGE_CODE +salesStage.toString().replace("[", "").replace("]", "") + Constants.RIGHT_PARANTHESIS);
			queryBuffer.append(SALES_STAGE_AND_DEAL_CLOSURE_DATE + fromDate+ Constants.SINGLE_QUOTE + Constants.RIGHT_PARANTHESIS);
			queryBuffer.append(DEAL_CLOSURE_END_DATE + toDate+ Constants.SINGLE_QUOTE + ")))");
			
			if(!opportunityOwnerList.contains("") && opportunityOwnerList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + OPPORTUNITY_OWNER +ExcelUtils.getStringListWithSingleQuotes(opportunityOwnerList) + Constants.RIGHT_PARANTHESIS);
			}
			if(!geoList.contains("") && geoList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + GEO_COND_PREFIX +ExcelUtils.getStringListWithSingleQuotes(geoList) + Constants.RIGHT_PARANTHESIS);
			}
			if(!serviceLinesList.contains("") && serviceLinesList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + SUBSP_COND_PREFIX +ExcelUtils.getStringListWithSingleQuotes(serviceLinesList) + Constants.RIGHT_PARANTHESIS);
			}
			if(!countryList.contains("") && countryList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + COUNTRY_COND_PREFIX +ExcelUtils.getStringListWithSingleQuotes(countryList) + Constants.RIGHT_PARANTHESIS);
			}
			if (whereClause != null && !whereClause.isEmpty()) { 
					queryBuffer.append(Constants.AND_CLAUSE + whereClause);
				}
			queryBuffer.append(Constants.RIGHT_PARANTHESIS);
			//query part 2
			queryBuffer.append(BDM_DETAILED_REPORT_PART2);
			queryBuffer.append(SALES_STAGE_CODE +salesStage.toString().replace("[", "").replace("]", "") + Constants.RIGHT_PARANTHESIS);
			queryBuffer.append(SALES_STAGE_AND_DEAL_CLOSURE_DATE + fromDate+ Constants.SINGLE_QUOTE + Constants.RIGHT_PARANTHESIS);
			queryBuffer.append(DEAL_CLOSURE_END_DATE + toDate+ Constants.SINGLE_QUOTE + ")))");
			
			
			if(!opportunityOwnerList.contains("") && opportunityOwnerList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + BID_OFFICE_OWNER +ExcelUtils.getStringListWithSingleQuotes(opportunityOwnerList) + Constants.RIGHT_PARANTHESIS);
			}
			if(!geoList.contains("") && geoList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + GEO_COND_PREFIX +ExcelUtils.getStringListWithSingleQuotes(geoList) + Constants.RIGHT_PARANTHESIS);
			}
			if(!serviceLinesList.contains("") && serviceLinesList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + SUBSP_COND_PREFIX +ExcelUtils.getStringListWithSingleQuotes(serviceLinesList) + Constants.RIGHT_PARANTHESIS);
			}
			if(!countryList.contains("") && countryList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + COUNTRY_COND_PREFIX + ExcelUtils.getStringListWithSingleQuotes(countryList) + Constants.RIGHT_PARANTHESIS);
			}
			if (whereClause != null && !whereClause.isEmpty()) { 
					queryBuffer.append(Constants.AND_CLAUSE + whereClause);
				}
			queryBuffer.append(Constants.RIGHT_PARANTHESIS);
			
			//query part 3
			queryBuffer.append(BDM_DETAILED_REPORT_PART3);
			queryBuffer.append(SALES_STAGE_CODE +salesStage.toString().replace("[", "").replace("]", "") + Constants.RIGHT_PARANTHESIS);
			queryBuffer.append(SALES_STAGE_AND_DEAL_CLOSURE_DATE + fromDate+ Constants.SINGLE_QUOTE + Constants.RIGHT_PARANTHESIS);
			queryBuffer.append(DEAL_CLOSURE_END_DATE + toDate+ Constants.SINGLE_QUOTE + ")))");
			
			if(!opportunityOwnerList.contains("") && opportunityOwnerList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + SALES_SUPPORT_OWNER +ExcelUtils.getStringListWithSingleQuotes(opportunityOwnerList) + Constants.RIGHT_PARANTHESIS);
			}
			if(!geoList.contains("") && geoList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + GEO_COND_PREFIX +ExcelUtils.getStringListWithSingleQuotes(geoList) + Constants.RIGHT_PARANTHESIS);
			}
			if(!serviceLinesList.contains("") && serviceLinesList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + SUBSP_COND_PREFIX +ExcelUtils.getStringListWithSingleQuotes(serviceLinesList) + Constants.RIGHT_PARANTHESIS);
			}
			if(!countryList.contains("") && countryList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + COUNTRY_COND_PREFIX + ExcelUtils.getStringListWithSingleQuotes(countryList) + Constants.RIGHT_PARANTHESIS);
			}
			if (whereClause != null && !whereClause.isEmpty()) { 
					queryBuffer.append(Constants.AND_CLAUSE + whereClause);
				}
			queryBuffer.append(Constants.RIGHT_PARANTHESIS);
			
			queryBuffer.append(BDM_DETAILED_REPORT_SUFFIX);
			return queryBuffer.toString();
		}

		
		/**
		 * This Method used to get all subordinates of supervisor 
		 * @param userId
		 * @param opportunityOwners
		 * @return
		 */
		public List<String> getRequiredBDMs(String userId, List<String> opportunityOwners) {
			List<String> bdms = new ArrayList<String>();
			List<String> userIds = null;
			userIds = userRepository.getAllSubordinatesIdBySupervisorId(userId);
			if (!opportunityOwners.isEmpty()) {
				for (String user : userIds) {
					if (opportunityOwners.contains(user)) {
						bdms.add(user);
						bdms.add(userId);
					}
				}
			} else {
				bdms.addAll(userIds);
				bdms.add(userId);
			}
			return bdms;
		}

		/**
		 * This Method used to add ("") to targetList if itemList contains "All" else adds geographies for the given display geography
		 * @param itemList
		 * @param targetList
		 */
		public void addItemToListGeo(List<String> itemList, List<String> targetList){
			if(itemList.contains("All") || itemList.isEmpty()){
				targetList.add("");
			} else {
				targetList.addAll(geographyRepository.findByDisplayGeography(itemList));
			}
		}
		
		
		/**
	     * Method to convert date in a format to another format
	     * 
	     * @param Stringdate
	     * @param actualFormat
	     * @param destFormat
	     * @return String
	     * @throws Exception
	     */
	    public static String getFormattedDate(String Stringdate, DateFormat actualFormat, DateFormat destFormat) throws Exception{

		return destFormat.format(actualFormat.parse(Stringdate));
	    }
}