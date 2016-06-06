package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.BidDetailsTRepository;
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
	
	@Autowired
	BidDetailsTRepository bidDetailsTRepository;

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
			boolean projectDVFlag = fields.contains(ReportConstants.PROJECTDEALVALUE);
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
			CellStyle cellStyleDateTimeFormat = spreadSheet.getWorkbook().createCellStyle(); 
			CellStyle cellStyleDateFormat = spreadSheet.getWorkbook().createCellStyle(); 
			CreationHelper createHelper = spreadSheet.getWorkbook().getCreationHelper();
			cellStyleDateTimeFormat.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy hh:mm")); 
			cellStyleDateFormat.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy"));
			
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
				
			BidDetailsT bidDetailsT=bidDetailsTRepository.findLatestBidByOpportunityId(opportunity.getOpportunityId());
			
			// set win probability
			if (winProbFlag) {
				if (bidDetailsT!=null) {
					if(bidDetailsT.getWinProbability() != null) {
						row.createCell(colValue).setCellValue(bidDetailsT.getWinProbability());
					}
				}
				colValue++;
			}
			
			//set target bid submission date
			if (targetBidSubDtFlag) {
				if (bidDetailsT!=null) {
					if(bidDetailsT.getTargetBidSubmissionDate() != null) {
						row.createCell(colValue).setCellValue(bidDetailsT.getTargetBidSubmissionDate());
						row.getCell(colValue).setCellStyle(cellStyleDateFormat); 
					}
				}
				colValue++;
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
					row.createCell(colValue).setCellValue(opportunity.getDealClosureDate());
					row.getCell(colValue).setCellStyle(cellStyleDateFormat);
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
				row.createCell(colValue).setCellValue(createdDate);
				row.getCell(colValue).setCellStyle(cellStyleDateTimeFormat);
				colValue++;
			}
			//set modified by 
			if (modifiedByFlag) {
				row.createCell(colValue).setCellValue(opportunity.getModifiedBy());
				colValue++;
			}
			
			//set modified date 
			if (modifiedDateFlag) {
				Timestamp modifiedDateTimeStamp = opportunity.getModifiedDatetime();
				Date modifiedDate = DateUtils.toDate(modifiedDateTimeStamp);
				row.createCell(colValue).setCellValue(modifiedDate);
				row.getCell(colValue).setCellStyle(cellStyleDateTimeFormat);
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
				row.createCell(columnNo).setCellValue(ReportConstants.DEALVALUEINR);
				row.getCell(columnNo++).setCellStyle(cellStyle);
				row.createCell(columnNo).setCellValue(ReportConstants.DEALVALUEUSD);
				row.getCell(columnNo++).setCellStyle(cellStyle);
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
			
			List<String> orderedFields = Arrays.asList("projectDealValue","winProbability", "targetBidSubmissionDate", "opportunityName", "factorsForWinLoss", 
					"dealClosureComments", "dealRemarksNotes", "subSp", "dealClosureDate", "createdBy",	"createdDate","modifiedBy","modifiedDate");
			
			for (String field : orderedFields) {
				if(fields.contains(field)){
				row.createCell(columnNo).setCellValue(FieldsMap.bdmReportFieldMap.get(field));
				row.getCell(columnNo).setCellStyle(cellStyle);
				columnNo++;
				}
			}
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
 }