package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

import com.google.common.base.Joiner;
import com.tcs.destination.bean.BDMDealValueDTO;
import com.tcs.destination.bean.BDMPerfromanceGeoIouDashboardResponse;
import com.tcs.destination.bean.BDMSupervisorDashboardDTO;
import com.tcs.destination.bean.CurrencyValue;
import com.tcs.destination.bean.DashBoardBDMResponse;
import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunitySummaryValue;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.OpportunityWinLossFactorsT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
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

@Service
public class BDMDetailedReportService {

	private static final Logger logger = LoggerFactory.getLogger(BDMDetailedReportService.class);
	
	private static final String BDM_DETAILED_REPORT_PART1 = " (select distinct(OPP.opportunity_id) as opportunity from opportunity_t OPP "
			+ " join geography_country_mapping_t GCMT on GCMT.country=OPP.country join geography_mapping_t GMT on GMT.geography = GCMT.geography " 
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id "
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp join customer_master_t CMT on opp.customer_id = CMT.customer_id where ";
	
	private static final String BDM_DETAILED_REPORT_PART2 = " UNION (select distinct(OPP.opportunity_id) as opportunity from opportunity_t OPP " 
			+ " join geography_country_mapping_t GCMT on GCMT.country=OPP.country join geography_mapping_t GMT on GMT.geography = GCMT.geography " 
			+ " join bid_details_t bidt on opp.opportunity_id = bidt.opportunity_id join bid_office_group_owner_link_t bofg on bidt.bid_id = bofg.bid_id " 
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id "
			+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp join customer_master_t CMT on opp.customer_id = CMT.customer_id where ";
	
	private static final String BDM_DETAILED_REPORT_PART3 = " UNION (select distinct(OPP.opportunity_id) as opportunity from opportunity_t OPP " 
			+ " join opportunity_sales_support_link_t OSSLT on OSSLT.opportunity_id=OPP.opportunity_id "
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

		
	public InputStreamResource getBdmDetailedReport(String financialYear, String from, String to,
			List<String> geography,  List<String> country, List<String> currency, List<String> servicelines,
			List<Integer> salesStage, List<String> opportunityOwners,
			String userId, List<String> fields) throws Exception {
		SXSSFWorkbook workbook = new SXSSFWorkbook(50);
		bdmReportsService.getBDMReportTitlePage(workbook, financialYear, from, to, geography, country, currency, servicelines, salesStage, opportunityOwners, userId, "Detailed");
		getBdmDetailedReportExcel(financialYear, from, to, geography, country, currency, servicelines, salesStage, opportunityOwners, userId, workbook, fields);
		ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
		workbook.write(byteOutPutStream);
		byteOutPutStream.flush();
		byteOutPutStream.close();
		byte[] bytes = byteOutPutStream.toByteArray();
		InputStreamResource inputStreamResource= new InputStreamResource(new ByteArrayInputStream(bytes));
		return inputStreamResource;
	}

	/**
	 * 
	 * @param financialYear * @param from * @param to * @param geography * @param currency
	 * @param serviceLines * @param salesStage * @param opportunityOwners * @param userId
	 * @param workbook * @throws Exception
	 */
	public void getBdmDetailedReportExcel(String financialYear, String from, String to, List<String> geography, List<String> country, List<String> currency,
			List<String> serviceLines, List<Integer> salesStage, List<String> opportunityOwners, String userId,
			SXSSFWorkbook workbook, List<String> fields) throws Exception {
		List<String> userIds = new ArrayList<String>();
		UserT user = userService.findByUserId(userId);
		boolean isIncludingSupervisor = false;
		if (user != null) {
			Date fromDate=new Date();
			Date toDate = new Date();
			List<String> geoList = new ArrayList<String>();
			List<String> countryList = new ArrayList<String>();
			List<String> serviceLinesList = new ArrayList<String>();
			
			String userGroup = user.getUserGroupMappingT().getUserGroup();
			
			List<String> opportunityOwnerList = new ArrayList<String>();
			addItemToListGeo(geography,geoList);
			addItemToList(country, countryList);
			addItemToList(serviceLines,serviceLinesList);
			addItemToList(opportunityOwners, opportunityOwnerList);
			List<OpportunityT> opportunityList=null;
			
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
				toDate = DateUtils.getDateFromMonth(from, false);
			}
			
		    if (UserGroup.contains(userGroup)) {
		    List<String> users = getRequiredBDMs(userId, opportunityOwners);
		    if(users.isEmpty()){
		    	logger.error("No Subordinates");
		    	throw new DestinationException(HttpStatus.NOT_FOUND, "No Subordinates");
		    }
		    // Validate user group, BDM's & BDM supervisor's are not authorized for this service
			switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
			case BDM:
				logger.error("User is not authorized to access this service");
			    throw new DestinationException(HttpStatus.UNAUTHORIZED, " User is not authorised to access this service ");
			case BDM_SUPERVISOR:
				if (users.isEmpty()) {
					logger.error("NOT_FOUND, BDMs not present");
					throw new DestinationException(HttpStatus.NOT_FOUND, "BDMs not present");
				}
				opportunityList = getBDMSupervisorPerformanceExcelReport(users, fromDate, toDate, geoList, salesStage, serviceLinesList, countryList, currency);
				isIncludingSupervisor = false;
				break;
			case GEO_HEADS:
			case IOU_HEADS:
				opportunityList = getOpportunityListBasedOnUserAccessPrivileges(userId, fromDate, toDate, geoList, 
						serviceLinesList, countryList, currency, salesStage, users);
				isIncludingSupervisor = true;
				break;
			default :
				List<String> userIdList = userRepository.findAllUserIds();
				opportunityList = getOpportunityListBasedOnUserAccessPrivileges(userId, fromDate, toDate, geoList, 
						serviceLinesList, countryList, currency, salesStage, userIdList);
				isIncludingSupervisor = true;
				break;
				}
		    }
		    //set the opportunity list to excel
		    setBDMsOpportunitiesToExcel(opportunityList, currency, workbook, fields, isIncludingSupervisor);
		} else {
		    logger.error("NOT_FOUND: User not found: {}", userId);
		    throw new DestinationException(HttpStatus.NOT_FOUND, "User not found: " + userId);
		}
	}
		
		
		
		/**
		 * This Method retrieves the BDM Supervisor performance details
		 * @param users * @param financialYear * @param geoList * @param serviceLinesList
		 * @param salesStage 
		 * @param countryList * @param workbook
		 * @param fields 
		 * @throws Exception 
		 */
		private List<OpportunityT> getBDMSupervisorPerformanceExcelReport(List<String> users, Date fromDate, Date toDate, List<String> geoList, List<Integer> salesStage, List<String> serviceLinesList,
				List<String> countryList, List<String> currency) throws Exception {
			logger.debug("Inside getBDMSupervisorPerformanceExcelReport() method");
			List<OpportunityT> opportunityList = null; 
			opportunityList = opportunityRepository.getBDMSupervisorOpportunities(users, salesStage, geoList, serviceLinesList, countryList, fromDate, toDate);
			return opportunityList;
		}

		/**
		 * 
		 * @param opportunityList
		 * @param workbook
		 * @param fields 
		 * @param isIncludingSupervisor 
		 * @throws Exception 
		 */
		private void setBDMsOpportunitiesToExcel(List<OpportunityT> opportunityList, List<String> currency, 
				SXSSFWorkbook workbook, List<String> fields, boolean isIncludingSupervisor) throws Exception {
			SXSSFSheet spreadSheet = (SXSSFSheet) workbook.createSheet("Detailed Report");
			int currentRow = 0;
			CellStyle cellStyle = ExcelUtils.createRowStyle(workbook, ReportConstants.REPORTHEADER);
			SXSSFRow row = null;
			if(fields.isEmpty()){
				row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
				setBDMSupervisorMandatoryHeaderToExcel(row, currentRow, spreadSheet, cellStyle, currency, isIncludingSupervisor);
				currentRow++;
				for(OpportunityT opportunity:opportunityList){
				setBDMSupervisorMandatoryDetails(row, opportunity, currentRow, spreadSheet, opportunityList, currency, isIncludingSupervisor);
				}
			} else {
				setBDMSupervisorHeaderAlongWithOptionalFieldsToExcel(currentRow, spreadSheet, cellStyle, currency, fields, isIncludingSupervisor);
				currentRow++;
				setBDMSupervisorAlongWithOptionalFieldsDetails(currentRow, spreadSheet, opportunityList, currency, fields, isIncludingSupervisor);
			}
		}


		/**
		 * 
		 * @param row 
		 * @param currentRow
		 * @param spreadSheet
		 * @param cellStyle
		 * @param currency 
		 */
		private void setBDMSupervisorMandatoryHeaderToExcel(SXSSFRow row, int currentRow, SXSSFSheet spreadSheet, CellStyle cellStyle, List<String> currency, boolean isIncludingSupervisor) {
//			SXSSFRow row = null;
//			row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
			CellStyle currencyStyle = ExcelUtils.createRowStyle(
					(SXSSFWorkbook) spreadSheet.getWorkbook(), ReportConstants.REPORTHEADER1);
			List<String> headerList = new ArrayList<String>();
			headerList.add("BDM");
			if(isIncludingSupervisor){
			headerList.add("Supervisor");
			}
			headerList.add("Display Service Line");
			headerList.add("Opportunity Owner");headerList.add("Sales Support Owners");
			headerList.add("Display Geography");headerList.add("Group Customer Name");
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
		 * 
		 * @param row 
		 * @param opportunity 
		 * @param currentRow
		 * @param spreadSheet
		 * @param opportunityList
		 * @param isIncludingSupervisor 
		 * @param currency
		 * @throws Exception 
		 */
		private void setBDMSupervisorMandatoryDetails(SXSSFRow row, OpportunityT opportunity, int currentRow, SXSSFSheet spreadSheet, List<OpportunityT> opportunityList,
				List<String> currencyList, boolean isIncludingSupervisor) throws Exception {
				List<String> salesSupportOwnerList = new ArrayList<String>();
				List<String> supervisorList = new ArrayList<String>();
				int columnNo = 0;
				UserT oppOwner = userRepository.findByUserId(opportunity.getOpportunityOwner());
//				boolean isPrimaryOwner = isPrimaryOrSalesOwner(opportunity);
//				String primaryOrSalesOnwer = getPrimaryOrSalesOwner(opportunity);
//				UserT primaryOrSalesOwnerName = userRepository.findByUserId(primaryOrSalesOnwer);
//				System.out.println("Ownersssssssssssssssssss" + primaryOrSalesOwnerName.getUserName());
				
				for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity.getOpportunitySalesSupportLinkTs()) {
					UserT user = userRepository.findByUserId(opportunitySalesSupportLinkT.getSalesSupportOwner());
					salesSupportOwnerList.add(user.getUserName());
					supervisorList.add(user.getSupervisorUserName());
				}
				String salesOwner = salesSupportOwnerList.toString().replace("[", "").replace("]", "");
				String salesOwnerSupervisor = supervisorList.toString().replace("[", "").replace("]", "");
				
				//set BDM
				row.createCell(columnNo++).setCellValue(oppOwner.getUserName() + "," + salesOwner);
				
				//set supervisor
				if(isIncludingSupervisor){
				row.createCell(columnNo++).setCellValue(opportunity.getPrimaryOwnerUser().getSupervisorUserName() + "," + salesOwnerSupervisor);
				}
				//set display_sub_sp
				row.createCell(columnNo++).setCellValue(opportunity.getOpportunitySubSpLinkTs().get(0).getSubSpMappingT().getDisplaySubSp());
				//set opportunity owner name
				row.createCell(columnNo++).setCellValue(oppOwner.getUserName());
				//set sales support owners
				row.createCell(columnNo++).setCellValue(salesOwner);
				//set display geography
				row.createCell(columnNo++).setCellValue(opportunity.getGeographyCountryMappingT().getGeographyMappingT().getDisplayGeography());
				//set group customer name
				row.createCell(columnNo++).setCellValue(opportunity.getCustomerMasterT().getGroupCustomerName());
				//set customer name
				row.createCell(columnNo++).setCellValue(opportunity.getCustomerMasterT().getCustomerName());
				//set sales stage code
				row.createCell(columnNo++).setCellValue(opportunity.getSalesStageCode());
				//set expected date of outcome
				if(opportunity.getBidDetailsTs().get(0).getExpectedDateOfOutcome()!=null){
				row.createCell(columnNo++).setCellValue(opportunity.getBidDetailsTs().get(0).getExpectedDateOfOutcome().toString());
				} else {
					row.createCell(columnNo++).setCellValue(Constants.SPACE);
				}
				//set Digital deal value
				for(String currency : currencyList) {
					int i = 0;
					if (opportunity.getDigitalDealValue() != null && opportunity.getDealCurrency() != null) {
						row.createCell(columnNo + i).setCellValue(beaconConverterService.convert(opportunity.getDealCurrency(),currency, 
								opportunity.getDigitalDealValue().doubleValue()).doubleValue());
					} else {
						row.createCell(columnNo + i).setCellValue(0);
					}
//					row.getCell(columnNo + i).setCellStyle(cellStyle);
					i++;
				}
				
		}

		
		/**
		 * 
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
			int columnNo = 10;
			if(isIncludingSupervisor){
				columnNo=11;
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
		 * 
		 * @param currency * @param fields 
		 * @param isIncludingSupervisor 
		 * @throws Exception 
		 */
		private void setBDMSupervisorAlongWithOptionalFieldsDetails(int currentRow, SXSSFSheet spreadSheet,
				List<OpportunityT> opportunityList, List<String> currency, List<String> fields, boolean isIncludingSupervisor) throws Exception {
			SXSSFRow row = null;
//			CellStyle dataRowStyle = ExcelUtils.createRowStyle((SXSSFWorkbook) spreadSheet.getWorkbook(), ReportConstants.DATAROW);
			boolean projectDVFlag = fields.contains(ReportConstants.DIGITALDEALVALUEPROJECTCURRENCY);
			boolean opportunityNameFlag = fields.contains(ReportConstants.OPPNAME);
			boolean targetBidSubDtFlag = fields.contains(ReportConstants.TARGETBIDSUBMISSIONDATE);
			boolean winProbFlag = fields.contains(ReportConstants.WINPROBABILITY);
			boolean factorForWLFlag = fields.contains(ReportConstants.FACTORSFORWINLOSS);
			boolean descForWLFlag = fields.contains(ReportConstants.DESCRIPTIONFORWINLOSS);
			boolean dealMarkFlag = fields.contains(ReportConstants.DEALREMARKSNOTES);
			
			for(OpportunityT opportunity:opportunityList){
				row = (SXSSFRow) spreadSheet.createRow((short) ++currentRow);
				setBDMSupervisorMandatoryDetails(row, opportunity,currentRow, spreadSheet, opportunityList, currency, isIncludingSupervisor);

//				row = (SXSSFRow) spreadSheet.createRow((short) currentRow++);
				int currentCol=10;
				if(isIncludingSupervisor){
					currentCol=11;
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
//					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
					if(opportunity.getDealCurrency() != null){
						row.createCell(colValue).setCellValue(opportunity.getDealCurrency());
						} else {
							row.createCell(colValue).setCellValue("");
						}
//					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
					}
				
				//set opportunity name
				if (opportunityNameFlag) {
					row.createCell(colValue).setCellValue(opportunity.getOpportunityName());
//					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
				}
				
				//set description for win loss
				if (descForWLFlag) {
					if(opportunity.getDescriptionForWinLoss() != null)
					row.createCell(colValue).setCellValue(opportunity.getDescriptionForWinLoss());
					else
						row.createCell(colValue).setCellValue("");
//					row.getCell(colValue).setCellStyle(dataRowStyle);
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
//					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
				}
				
				// set deal remarks notes
				if (dealMarkFlag) {
					List<String> dealRemarksNotesList = new ArrayList<String>();
					for (NotesT notesT : opportunity.getNotesTs()) {
						dealRemarksNotesList.add(notesT.getNotesUpdated());
					}
					row.createCell(colValue).setCellValue(dealRemarksNotesList.toString().replace("[", "").replace("]", ""));;
//					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
					}
				
			for (int i = 0; i < colValue; i++) {
				if(opportunity.getBidDetailsTs().size() > 0)
				spreadSheet.addMergedRegion(new CellRangeAddress(currentRow - 1, currentRow + opportunity.getBidDetailsTs().size() - 2, i, i));
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
//						row.getCell(colValue).setCellStyle(dataRowStyle);
					}
					colValue++;
				} else {
					row.createCell(colValue).setCellValue(Constants.SPACE);
//					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
				}
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
//						row.getCell(colValue).setCellStyle(dataRowStyle);
					}
					colValue++;
				} else {
					row.createCell(colValue).setCellValue(Constants.SPACE);
//					row.getCell(colValue).setCellStyle(dataRowStyle);
					colValue++;
				}
			}
			}
		}
		
		/**
		 * 
		 * @param userId 
		 * @param salesStage 
		 * @param opportunityOwnerList 
		 * @param financialYear * @param geoList * @param serviceLinesList
		 * @param workbook * @throws Exception
		 * @return 
		 */
		private List<OpportunityT> getOpportunityListBasedOnUserAccessPrivileges(String userId, Date fromDate, Date toDate, List<String> geoList, List<String> serviceLinesList, List<String> countryList,
				List<String> currency, List<Integer> salesStage, List<String> opportunityOwnerList) throws Exception {
			logger.debug("Inside getOpportunityListBasedOnUserAccessPrivileges() method");
			List<OpportunityT> opportunityList = new ArrayList<OpportunityT>();
			// Form the native top revenue query string
			String queryString = getOpportunityListQueryString(userId, fromDate, toDate, geoList, countryList, serviceLinesList, salesStage, opportunityOwnerList);
			logger.info("Query string: {}", queryString);
			// Execute the native revenue query string
			Query bdmReportQuery = entityManager.createNativeQuery(queryString);
			List<String> opportunityIds = bdmReportQuery.getResultList();
			
			if(!opportunityIds.isEmpty()){
				opportunityList = opportunityRepository.findByOpportunityIdInOrderByCountryAsc(opportunityIds);
			}
			
			if (opportunityList == null || opportunityList.isEmpty()) {
				logger.error("NOT_FOUND: Report could not be downloaded, as no opportunity details are available for user selection and privilege combination");
				throw new DestinationException(HttpStatus.NOT_FOUND, "Report could not be downloaded, as no opportunity details are available for user selection and privilege combination");
			}
			return opportunityList;
		}

		

		/**
		 * 
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
			StringBuffer queryBuffer = new StringBuffer(BDM_DETAILED_REPORT_PART1);
			// Get user access privilege groups 
			HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX, null, null);
			String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
			
			queryBuffer.append(SALES_STAGE_CODE +salesStage.toString().replace("[", "").replace("]", "") + Constants.RIGHT_PARANTHESIS);
			queryBuffer.append(SALES_STAGE_AND_DEAL_CLOSURE_DATE + fromDate+ Constants.SINGLE_QUOTE + Constants.RIGHT_PARANTHESIS);
			queryBuffer.append(DEAL_CLOSURE_END_DATE + toDate+ Constants.SINGLE_QUOTE + ")))");
			
			if(!opportunityOwnerList.contains("") && opportunityOwnerList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + OPPORTUNITY_OWNER +getStringListWithSingleQuotes(opportunityOwnerList) + Constants.RIGHT_PARANTHESIS);
			}
			if(!geoList.contains("") && geoList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + GEO_COND_PREFIX +getStringListWithSingleQuotes(geoList) + Constants.RIGHT_PARANTHESIS);
			}
			if(!serviceLinesList.contains("") && serviceLinesList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + SUBSP_COND_PREFIX +getStringListWithSingleQuotes(serviceLinesList) + Constants.RIGHT_PARANTHESIS);
			}
			if(!countryList.contains("") && countryList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + COUNTRY_COND_PREFIX + getStringListWithSingleQuotes(countryList) + Constants.RIGHT_PARANTHESIS);
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
			queryBuffer.append(Constants.AND_CLAUSE + BID_OFFICE_OWNER +getStringListWithSingleQuotes(opportunityOwnerList) + Constants.RIGHT_PARANTHESIS);
			}
			if(!geoList.contains("") && geoList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + GEO_COND_PREFIX +getStringListWithSingleQuotes(geoList) + Constants.RIGHT_PARANTHESIS);
			}
			if(!serviceLinesList.contains("") && serviceLinesList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + SUBSP_COND_PREFIX +getStringListWithSingleQuotes(serviceLinesList) + Constants.RIGHT_PARANTHESIS);
			}
			if(!countryList.contains("") && countryList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + COUNTRY_COND_PREFIX + getStringListWithSingleQuotes(countryList) + Constants.RIGHT_PARANTHESIS);
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
			queryBuffer.append(Constants.AND_CLAUSE + SALES_SUPPORT_OWNER +getStringListWithSingleQuotes(opportunityOwnerList) + Constants.RIGHT_PARANTHESIS);
			}
			if(!geoList.contains("") && geoList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + GEO_COND_PREFIX +getStringListWithSingleQuotes(geoList) + Constants.RIGHT_PARANTHESIS);
			}
			if(!serviceLinesList.contains("") && serviceLinesList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + SUBSP_COND_PREFIX +getStringListWithSingleQuotes(serviceLinesList) + Constants.RIGHT_PARANTHESIS);
			}
			if(!countryList.contains("") && countryList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + COUNTRY_COND_PREFIX + getStringListWithSingleQuotes(countryList) + Constants.RIGHT_PARANTHESIS);
			}
			if (whereClause != null && !whereClause.isEmpty()) { 
					queryBuffer.append(Constants.AND_CLAUSE + whereClause);
				}
			queryBuffer.append(Constants.RIGHT_PARANTHESIS);
			return queryBuffer.toString();
		}

		
		/**
		 * This Method used to get all subordinates of supervisor 
		 * @param userId
		 * @param opportunityOwners
		 * @return
		 */
		private List<String> getRequiredBDMs(String userId, List<String> opportunityOwners) {
			List<String> bdms = new ArrayList<String>();
			bdms.add(userId);
			List<String> userIds = null;
			userIds = userRepository.getAllSubordinatesIdBySupervisorId(userId);
			userIds.add(userId);
			if (!opportunityOwners.isEmpty()) {
				for (String user : userIds) {
					if (opportunityOwners.contains(user)) {
						bdms.add(user);
					}
				}
			} else {
				bdms.addAll(userIds);
			}
			return bdms;
		}

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
					targetList.addAll(geographyRepository.findByDisplayGeography(itemList));
				}
			}
			
			public String getStringListWithSingleQuotes(List<String> formattedList) {
				String appendedString = Joiner.on("\',\'").join(formattedList);
				if (!formattedList.isEmpty()) {
					appendedString = "\'" + appendedString + "\'";
				}
				return appendedString;
			}
			
//			private boolean isPrimaryOrSalesOwner(OpportunityT opportunity) throws Exception {
//				Boolean isPrimaryOwner = false;
//				List<String> userIds = userRepository.findUserIdByUserGroup("BDM");
//					if(userIds.contains(opportunity.getPrimaryOwnerUser().getUserId())){
//						isPrimaryOwner = true;
//					} else {
//						isPrimaryOwner = false;
//					}
//				return isPrimaryOwner;
//				}
//			
//			private String getPrimaryOrSalesOwner(OpportunityT opportunity) throws Exception {
//				String owner = null;
//				List<String> userIds = userRepository.findUserIdByUserGroup("BDM");
//				for(String user:userIds){
//					if(user.equals(opportunity.getOpportunityOwner())){
//						owner = user;
//						break;
//					} else {
//						for(OpportunitySalesSupportLinkT opportunitySalesSupportLinkT:opportunity.getOpportunitySalesSupportLinkTs()){
//						if(user.equals(opportunitySalesSupportLinkT.getSalesSupportOwner())){
//							owner = user;
//							break;
//							}
//						}
//					}
//				}
//				System.out.println("userIds"+owner);
//				return owner;
//				}
}