package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

import com.tcs.destination.bean.BDMDealValueDTO;
import com.tcs.destination.bean.BDMPerfromanceGeoIouDashboardResponse;
import com.tcs.destination.bean.BDMSupervisorDashboardDTO;
import com.tcs.destination.bean.DashBoardBDMResponse;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ActualRevenuesDataTRepository;
import com.tcs.destination.data.repository.BeaconDataTRepository;
import com.tcs.destination.data.repository.BidDetailsTRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.ProjectedRevenuesDataTRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.UserAccessPrivilegeQueryBuilder;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.ReportConstants;

/*
 * This service handles the BDM Reports related functionalities
 */
@Service
public class BDMReportsService {

	private static final Logger logger = LoggerFactory.getLogger(BDMReportsService.class);
	
	
	@Autowired
	ActualRevenuesDataTRepository actualRevenuesDataTRepository;

	@Autowired
	BeaconDataTRepository beaconDataTRepository;

	@Autowired
	BidDetailsTRepository bidDetailsTRepository;

	@Autowired
	BeaconConverterService beaconConverterService;

	@Autowired
	BuildBidReportService buildBidReportService;

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	ConnectDetailedReportService connectDetailedReportService;

	@Autowired
	ConnectSummaryReportService connectSummaryReportService;

	@Autowired
	ProjectedRevenuesDataTRepository projectedRevenuesDataTRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BuildExcelTargetVsActualDetailedReportService buildExcelTargetVsActualDetailedReportService;

	@Autowired
	BuildExcelTargetVsActualSummaryReportService buildExcelTargetVsActualSummaryReportService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserAccessPrivilegesRepository userAccessPrivilegesRepository;

	@Autowired
	UserAccessPrivilegeQueryBuilder userAccessPrivilegeQueryBuilder;

	@Autowired
	UserService userService;

	@Autowired
	GeographyRepository geographyRepository;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	BuildOpportunityReportService buildOpportunityReportService;
	
	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	BDMService bdmService;
	
	@Autowired
	BDMDetailedReportService bdmDetailedReportService;
    
	/**
	 * This method gives the BDM Report in Excel format
	 * @param financialYear
	 * @param from
	 * @param to
	 * @param geography
	 * @param country
	 * @param currency
	 * @param serviceline
	 * @param iou
	 * @param salesStage
	 * @param opportunityOwners
	 * @param userId
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	public InputStreamResource getBdmsReport(String financialYear, String from, String to, List<String> geography, List<String> country, List<String> currency,
			List<String> serviceline, List<String> iou, List<Integer> salesStage, List<String> opportunityOwners, String userId, List<String> fields) throws Exception {
		logger.info("Inside getBdmsReport method");
		SXSSFWorkbook workbook = new SXSSFWorkbook(50);
		getBDMReportTitlePage(workbook, financialYear, from, to, geography, country, currency, serviceline, salesStage, opportunityOwners, userId, "both", iou);
		getBdmSummaryReportExcel(financialYear, from, to, geography, currency, serviceline, salesStage, opportunityOwners, userId, workbook, country, iou);
		bdmDetailedReportService.getBdmDetailedReportExcel(financialYear, from, to, geography, country, currency, serviceline, salesStage, opportunityOwners, userId, workbook, fields, iou);
		ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
		workbook.write(byteOutPutStream);
		byteOutPutStream.flush();
		byteOutPutStream.close();
		byte[] bytes = byteOutPutStream.toByteArray();
		InputStreamResource inputStreamResource= new InputStreamResource(new ByteArrayInputStream(bytes));
		return inputStreamResource;
	}
	
	/**
	 * This method generates the title page sheet for BDM Report
	 * @param workbook
	 * @param financialYear
	 * @param from
	 * @param to
	 * @param geography
	 * @param country
	 * @param currency
	 * @param serviceline
	 * @param salesStage
	 * @param opportunityOwners
	 * @param userId
	 * @param reportType
	 * @param iou
	 */
	public void getBDMReportTitlePage(SXSSFWorkbook workbook, String financialYear, String from, String to,
			List<String> geography, List<String> country, List<String> currency, List<String> serviceline,
			List<Integer> salesStage, List<String> opportunityOwners, String userId, String reportType, List<String> iou) {
		logger.info("Inside getBDMReportTitlePage method");
		List<String> privilegeValueList = new ArrayList<String>();
		CellStyle headinStyle = ExcelUtils.createRowStyle(workbook, ReportConstants.REPORTHEADINGSTYLE);
		CellStyle subHeadingStyle = ExcelUtils.createRowStyle(workbook, ReportConstants.ROWS);
		CellStyle dataRow = ExcelUtils.createRowStyle(workbook, ReportConstants.DATAROW);
		String completeList = null;
		SXSSFRow row = null;
		SXSSFSheet spreadsheet = (SXSSFSheet) workbook.createSheet(ReportConstants.TITLE);
		
		row = (SXSSFRow) spreadsheet.createRow(4);
		spreadsheet.addMergedRegion(new CellRangeAddress(4, 4, 4, 7));
		row.createCell(4).setCellValue("BDM Performance report as on " + DateUtils.getCurrentDate());
		row.getCell(4).setCellStyle(headinStyle);
		row = (SXSSFRow) spreadsheet.createRow(6);
		row.createCell(4).setCellValue("User Selection Filter's");
		row.getCell(4).setCellStyle(subHeadingStyle);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, ReportConstants.GEO, geography, 7, dataRow);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, "Country", country, 8, dataRow);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, ReportConstants.Iou, iou, 9, dataRow);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, "Service Line", serviceline, 10, dataRow);
		row = (SXSSFRow) spreadsheet.createRow(11);
		row.createCell(4).setCellValue("Period");
		
		String period = null;
		if(!financialYear.isEmpty()){
			period = financialYear;
		} else if(!from.isEmpty() && !to.isEmpty()){
			period = ExcelUtils.getPeriod(from, to);
		} else {
			period = DateUtils.getCurrentFinancialYear();
		}
		row.createCell(5).setCellValue(period);
		row = (SXSSFRow) spreadsheet.createRow(12);
		
		if (salesStage.size()>13) {
			completeList = "All";
		} else {
			completeList = buildOpportunityReportService.findBySalesStageCodeDescription(salesStage);
		}
		row.createCell(4).setCellValue("Sales stage");
		row.createCell(5).setCellValue(completeList);

		String userAccessField = null;
		List<UserAccessPrivilegesT> userPrivilegesList = 
				userAccessPrivilegesRepository.findByUserIdAndParentPrivilegeIdIsNullAndIsactive(userId, Constants.Y);
		UserT user = userRepository.findByUserId(userId);
		String userGroup=user.getUserGroupMappingT().getUserGroup();
		row = (SXSSFRow) spreadsheet.createRow(14);
		row.createCell(4).setCellValue("User Access Filter's");
		row.getCell(4).setCellStyle(subHeadingStyle);
		switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
		case GEO_HEADS:
			userAccessField = Constants.GEOGRAPHY;
			for(UserAccessPrivilegesT accessPrivilegesT:userPrivilegesList){
				String previlageType=accessPrivilegesT.getPrivilegeType();
				String privilageValue=accessPrivilegesT.getPrivilegeValue();
				if(previlageType.equals(Constants.GEOGRAPHY)){
					privilegeValueList.add(privilageValue);
				}
			}
			ExcelUtils.writeDetailsForSearchTypeUserAccessFilter(spreadsheet, userAccessField, privilegeValueList, user, dataRow, "NA");
			break;
		case IOU_HEADS:
			userAccessField = Constants.IOU;
			for(UserAccessPrivilegesT accessPrivilegesT:userPrivilegesList){
				String previlageType=accessPrivilegesT.getPrivilegeType();
				String privilageValue=accessPrivilegesT.getPrivilegeValue();
				if(previlageType.equals(Constants.IOU)){
					privilegeValueList.add(privilageValue);
				}
			}
			ExcelUtils.writeDetailsForSearchTypeUserAccessFilter(spreadsheet, userAccessField, privilegeValueList, user, dataRow, "NA");
			break;
		case BDM_SUPERVISOR:
		case PRACTICE_HEAD:
			ExcelUtils.writeUserFilterConditions(spreadsheet, user, "NA");
			break;
		default :
			ExcelUtils.writeUserFilterConditions(spreadsheet, user, ReportConstants.FULLACCESS);
		}
		
		row = (SXSSFRow) spreadsheet.createRow(22);
		row.createCell(4).setCellValue("Display Preferences");
		row.getCell(4).setCellStyle(subHeadingStyle);
		row = (SXSSFRow) spreadsheet.createRow(23);
		row.createCell(4).setCellValue("Currency");
		row.createCell(5).setCellValue(currency.toString().replace("[", "").replace("]", ""));
		row = (SXSSFRow) spreadsheet.createRow(24);
		row.createCell(4).setCellValue("Report Type");
		row.createCell(5).setCellValue(reportType);
		
		spreadsheet.addMergedRegion(new CellRangeAddress(26, 26, 4, 7));
		row = (SXSSFRow) spreadsheet.createRow(26);
		row.createCell(4).setCellValue(ReportConstants.REPORTNOTE);
		
	}


		/**
		 * This method used to get bdms summary details based on user selection and privileges
		 * @param financialYear  * @param from * @param to * @param geography * @param country * @param currency
		 * @param serviceLines2 
		 * @param serviceLines * @param salesStage * @param opportunityOwnerIds * @param supervisorId
		 * @param fields * @return * @throws Exception
		 */
		public InputStreamResource getBdmSummaryReport(String financialYear, String from, String to,
				List<String> geography, List<String> country, List<String> currency, List<String> servicelines, List<String> iou, List<Integer> salesStage, List<String> opportunityOwners,
				String userId, List<String> fields) throws Exception {
			SXSSFWorkbook workbook = new SXSSFWorkbook(50);
			getBDMReportTitlePage(workbook, financialYear, from, to, geography, country, currency, servicelines, salesStage, opportunityOwners, userId, "Summary", iou);
			getBdmSummaryReportExcel(financialYear, from, to, geography, currency, servicelines, salesStage, opportunityOwners, userId, workbook, country, iou);
			ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
			workbook.write(byteOutPutStream);
			byteOutPutStream.flush();
			byteOutPutStream.close();
			byte[] bytes = byteOutPutStream.toByteArray();
			InputStreamResource inputStreamResource= new InputStreamResource(new ByteArrayInputStream(bytes));
			return inputStreamResource;
		}
		
		/**
		 * This Method retrieves the BDM Performance Summary details
		 * @param financialYear  * @param from  * @param to  * @param geography * @param country
		 * @param currency * @param serviceLines * @param salesStage * @param opportunityOwners
		 * @param userId * @param workbook * @param fields
		 * @param country 
		 * @param iou 
		 * @throws Exception 
		 */
		private void getBdmSummaryReportExcel(String financialYear, String from, String to, List<String> geography, 
				List<String> currency, List<String> serviceLines, List<Integer> salesStage, 
				List<String> opportunityOwners, String userId, SXSSFWorkbook workbook, List<String> country, List<String> iou) throws Exception {
			List<String> userIds = new ArrayList<String>();
			UserT user = userService.findByUserId(userId);
			if (user != null) {
			    
				String userGroup = user.getUserGroupMappingT().getUserGroup();
				List<String> geoList = new ArrayList<String>();
				List<String> countryList = new ArrayList<String>();
				List<String> serviceLinesList = new ArrayList<String>();
				List<String> iouList = new ArrayList<String>();
				addItemToListGeo(geography,geoList);
				ExcelUtils.addItemToList(country,countryList);
				ExcelUtils.addItemToList(serviceLines,serviceLinesList);
				ExcelUtils.addItemToList(iou, iouList);
				
			    if (UserGroup.contains(userGroup)) {
			    	
			    	userIds = bdmDetailedReportService.getRequiredBDMs(userId, opportunityOwners);
			    	List<String> userGroupsGeoIouHeads = Arrays.asList("GEO Heads","IOU Heads");
			    // Validate user group, BDM's & BDM supervisor's are not authorized for this service
				switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
				case BDM:
				case PRACTICE_OWNER:
					logger.error("User is not authorized to access this service");
				    throw new DestinationException(HttpStatus.UNAUTHORIZED, " User is not authorised to access this service ");
				case BDM_SUPERVISOR:
				case PRACTICE_HEAD:
					 if(userIds.isEmpty()){
				    	logger.error("Given BDM is not his Subordinate");
				    	throw new DestinationException(HttpStatus.NOT_FOUND, "Given BDM is not his Subordinate");
					 }
					getOpportunitySummaryDetails(userIds, financialYear, geoList, serviceLinesList, workbook, countryList, iouList);
					getBDMSupervisorPerformanceExcelReport(userIds, financialYear, workbook);
					break;
				case GEO_HEADS:
				case IOU_HEADS:
				case PMO:
					 if(userIds.isEmpty()){
				    	logger.error("Given BDM is not his Subordinate");
				    	throw new DestinationException(HttpStatus.NOT_FOUND, "Given BDM is not his Subordinate");
					    }
					 	getOpportunitySummaryDetails(userIds, financialYear, geoList, serviceLinesList, workbook, countryList, iouList);
						getBDMSupervisorPerformanceExcelReport(userIds, financialYear, workbook);
						List<String> geoHeadOrIouSpocsUserIds = userRepository.findUserIdByuserGroup(userGroupsGeoIouHeads);
						getGeoHeadOrIouHeadPerformanceExcelReportForSI(geoHeadOrIouSpocsUserIds, userId, financialYear, workbook);
					break;
				default :
					List<String> userGroupBDMAndBDMSupervisor = Arrays.asList("BDM", "BDM Supervisor","Practice Head");
					List<String> bdmUser = Arrays.asList("BDM","Practice Owner");
					
					List<String> bdmsList = new ArrayList<String>();
					List<String> geoIouUserList = new ArrayList<String>();
					List<String> bdmSupervisorList = new ArrayList<String>();
					List<String> geoHeadOrIouSpocsUserList = userRepository.findUserIdByuserGroup(userGroupsGeoIouHeads);
					if(opportunityOwners.isEmpty()){
						 bdmsList = userRepository.findUserIdByuserGroup(bdmUser);
						 bdmSupervisorList = userRepository.findUserIdByuserGroup(userGroupBDMAndBDMSupervisor);
						 geoIouUserList = geoHeadOrIouSpocsUserList;
					} else {
						 bdmsList.addAll(opportunityOwners);
						 bdmSupervisorList = getSubOrdinatesList(opportunityOwners);
						 geoIouUserList = getRequiredGeoOrIouHeadsList(opportunityOwners, geoHeadOrIouSpocsUserList);
					}
					List<String> bdmList = getSubOrdinatesList(bdmsList);
					getOpportunitySummaryDetails(bdmList, financialYear, geoList, serviceLinesList, workbook, countryList, iouList);
					getBDMSupervisorPerformanceExcelReport(bdmSupervisorList, financialYear, workbook);
					getGeoHeadOrIouHeadPerformanceExcelReportForSI(geoIouUserList, userId, financialYear, workbook);
					break;
				}
		    }
			} else {
			    logger.error("NOT_FOUND: User not found: {}", userId);
			    throw new DestinationException(HttpStatus.NOT_FOUND, "User not found: " + userId);
			}
		}
		
		/** This method gives the required Geo Heads or IOU Lists based on the opportunity owners 
		 * and geoIou user list
		 * @param opportunityOwners
		 * @param geoIouUserList 
		 * @return
		 */
		private List<String> getRequiredGeoOrIouHeadsList(List<String> opportunityOwners, List<String> geoIouUserList) {
			List<String> geoHeadOrIouSpocUserIds = new ArrayList<String>();
			for(String geoIouUser:opportunityOwners){
				if(geoIouUserList.contains(geoIouUser)){
					geoHeadOrIouSpocUserIds.add(geoIouUser);
				}
			}
			return geoHeadOrIouSpocUserIds;
		}


		/** This method retrieves the subordinate list of BDM Supervisor, GEO Heads and IOU Heads
		 * @param bdmsList
		 * @param userGroup 
		 * @return
		 * @throws Exception 
		 */
		private List<String> getSubOrdinatesList(List<String> bdmsList) throws Exception {
			List<String> subOrdinatesList = new ArrayList<String>();
			for(String bdm:bdmsList){
				UserT user = userService.findByUserId(bdm);
				String userGroup = user.getUserGroupMappingT().getUserGroup();
			  if (UserGroup.contains(userGroup)) {
			    	
				switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
				case BDM:
				case PRACTICE_OWNER:
					subOrdinatesList.add(bdm);
					break;
				case BDM_SUPERVISOR:
				case PRACTICE_HEAD:
				case GEO_HEADS:
				case IOU_HEADS:
					List<String> userIds = null;
					userIds = userRepository.getAllSubordinatesIdBySupervisorId(bdm);
					subOrdinatesList.addAll(userIds);
					subOrdinatesList.add(bdm);
					break;
				default :
					subOrdinatesList.add(bdm);
					break;
				}
		    }
			}
			return subOrdinatesList;
		}

        /**
         * This method generates the Geo head or IOU head performance report for SI
         * @param geoIouUserList
         * @param userId
         * @param financialYear
         * @param workbook
         * @throws Exception
         */
		private void getGeoHeadOrIouHeadPerformanceExcelReportForSI(List<String> geoIouUserList, String userId,
				String financialYear, SXSSFWorkbook workbook) throws Exception {
			List<BDMPerfromanceGeoIouDashboardResponse> bdmPerfromanceGeoIouList = 
					new ArrayList<BDMPerfromanceGeoIouDashboardResponse>();
			BDMPerfromanceGeoIouDashboardResponse bdmPerfromanceGeoIouDashboardResponse = new BDMPerfromanceGeoIouDashboardResponse();
			for(String user:geoIouUserList) {
				bdmPerfromanceGeoIouDashboardResponse = getGeoOrIouHeadPerformanceByUserAccessPrivileges(user, financialYear);
				bdmPerfromanceGeoIouList.add(bdmPerfromanceGeoIouDashboardResponse);
			}
			setGeoOrIouHeadsPerformanceToExcelForSI(bdmPerfromanceGeoIouList, workbook, userId);
		}
			
        /**
         * This method inserts the Geo or IOU heads performance values into the excel
         * @param bdmPerfromanceGeoIouList
         * @param workbook
         * @param userId
         */
		private void setGeoOrIouHeadsPerformanceToExcelForSI(List<BDMPerfromanceGeoIouDashboardResponse> bdmPerfromanceGeoIouList,
				SXSSFWorkbook workbook, String userId) {
			SXSSFSheet spreadSheet = (SXSSFSheet) workbook.createSheet("Geo Or Iou Heads Summary");
			CellStyle cellStyle = ExcelUtils.createRowStyle(workbook, ReportConstants.REPORTHEADER);
			int currentRow = 0;
			boolean isGeoOrIouHead = false;
			SXSSFRow row = null;
			setGeoOrIouHeadsHeaderToExcel(currentRow, spreadSheet, cellStyle);
			currentRow++;
			for(BDMPerfromanceGeoIouDashboardResponse bdmPerfromanceGeoIouDashboardResponse:bdmPerfromanceGeoIouList){
				setGeoOrIouHeadsDetailsToExcel(currentRow, spreadSheet, bdmPerfromanceGeoIouDashboardResponse, userId, isGeoOrIouHead);
				currentRow = currentRow + 8;
			}
			row = (SXSSFRow) spreadSheet.createRow((short) ++currentRow);
			row.createCell(0).setCellValue("Note: Target & achieved displayed is for full year 2015-16 (no filter, user selection conditions are applied)");
		}
        
		/**
		 * This method is used to retrieve the opportunity summary details
		 * @param userIds
		 * @param financialYear
		 * @param geoList
		 * @param serviceLinesList
		 * @param workbook
		 * @param countryList
		 * @param iouList
		 * @throws Exception
		 */
		private void getOpportunitySummaryDetails(List<String> userIds, String financialYear, List<String> geoList,
				List<String> serviceLinesList, SXSSFWorkbook workbook, List<String> countryList, List<String> iouList) throws Exception {
			List<BDMDealValueDTO> bdmDealValueDTOList = new ArrayList<BDMDealValueDTO>();
			BDMDealValueDTO bdmDealValueDTO = null;
			for(String userId:userIds){
				bdmDealValueDTO = getOpportunityCountAndDigitalDealValueByUser(userId, financialYear, geoList,serviceLinesList, countryList, iouList);
			if(bdmDealValueDTO != null){
				bdmDealValueDTOList.add(bdmDealValueDTO);
				}
			}
			setOpportunitySummaryToExcel(bdmDealValueDTOList,workbook);
		}

		/**
		 * This method inserts the opportunity summary values into the excel
		 * @param bdmDealValueDTOList
		 * @param workbook
		 */
		private void setOpportunitySummaryToExcel(List<BDMDealValueDTO> bdmDealValueDTOList,
				SXSSFWorkbook workbook) {
			SXSSFSheet spreadSheet = (SXSSFSheet) workbook.createSheet("Opportunity Summary");
			int currentRow = 0;
			CellStyle cellStyle = ExcelUtils.createRowStyle(workbook, ReportConstants.REPORTHEADER);
			
			setOpportunitySummaryHeaderToExcel(currentRow, spreadSheet, cellStyle);
			currentRow = currentRow+2;
			setOpportunitySummaryToExcel(currentRow, spreadSheet, bdmDealValueDTOList);
		}

		/**
		 * This method inserts the opportunity summary values into the excel
		 * @param currentRow
		 * @param spreadSheet
		 * @param bdmDealValueDTOList
		 */
		private void setOpportunitySummaryToExcel(int currentRow, SXSSFSheet spreadSheet,
				List<BDMDealValueDTO> bdmDealValueDTOList) {
			logger.info("Inside setOpportunitySummaryToExcel method");
			SXSSFRow row = null;
			for(BDMDealValueDTO bdmDealValueDTO:bdmDealValueDTOList){
				row = (SXSSFRow) spreadSheet.createRow((short) ++currentRow);
				row.createCell(0).setCellValue(bdmDealValueDTO.getUserName());
				row.createCell(1).setCellValue(bdmDealValueDTO.getUserGroup());
				row.createCell(2).setCellValue(bdmDealValueDTO.getWinsOpportunityCount());
				row.createCell(3).setCellValue(bdmDealValueDTO.getWinsDigitalDealValue().doubleValue());
				row.createCell(4).setCellValue(bdmDealValueDTO.getLossOpportunityCount());
				row.createCell(5).setCellValue(bdmDealValueDTO.getLossDigitalDealValue().doubleValue());
				row.createCell(6).setCellValue(bdmDealValueDTO.getOtherLossOpportunityCount());
				row.createCell(7).setCellValue(bdmDealValueDTO.getOtherLossDigitalDealValue().doubleValue());
				row.createCell(8).setCellValue(bdmDealValueDTO.getPipelineOpportunityCount());
				row.createCell(9).setCellValue(bdmDealValueDTO.getPipelineDigitalDealValue().doubleValue());
				row.createCell(10).setCellValue(bdmDealValueDTO.getProspectsOpportunityCount());
				row.createCell(11).setCellValue(bdmDealValueDTO.getProspectsDigitalDealValue().doubleValue());
			}
		}
        
		/**
		 * This method inserts the header values for Opportunity summary into an excel sheet
		 * @param currentRow
		 * @param spreadSheet
		 * @param cellStyle
		 */
		private void setOpportunitySummaryHeaderToExcel(int currentRow, SXSSFSheet spreadSheet, CellStyle cellStyle) {
			SXSSFRow row = null;
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
			row.createCell(2).setCellValue("WINS (Stage 09)");
			row.getCell(2).setCellStyle(cellStyle);
			spreadSheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 2, 3));
			row.createCell(4).setCellValue("LOSSES (Stage 10)");
			row.getCell(4).setCellStyle(cellStyle);
			spreadSheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 4, 5));
			row.createCell(6).setCellValue("OTHER LOSS");
			row.getCell(6).setCellStyle(cellStyle);
			spreadSheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 6, 7));
			row.createCell(8).setCellValue("PIPELINE");
			row.getCell(8).setCellStyle(cellStyle);
			spreadSheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 8, 9));
			row.createCell(10).setCellValue("PROSPECTS");
			row.getCell(10).setCellStyle(cellStyle);
			spreadSheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 10, 11));
			
			row = (SXSSFRow) spreadSheet.createRow((short) ++currentRow);
			row.createCell(2).setCellValue("09-Closed & Won");
			row.getCell(2).setCellStyle(cellStyle);
			spreadSheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 2, 3));
			row.createCell(4).setCellValue("10-Closed & Lost");
			row.getCell(4).setCellStyle(cellStyle);
			spreadSheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 4, 5));
			row.createCell(6).setCellValue("Stage 11-13");
			row.getCell(6).setCellStyle(cellStyle);
			spreadSheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 6, 7));
			row.createCell(8).setCellValue("Stage 04-08");
			row.getCell(8).setCellStyle(cellStyle);
			spreadSheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 8, 9));
			row.createCell(10).setCellValue("Stage 00-03");
			row.getCell(10).setCellStyle(cellStyle);
			spreadSheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 10, 11));
			
			row = (SXSSFRow) spreadSheet.createRow((short) ++currentRow);
			row.createCell(0).setCellValue("BDM");
			row.getCell(0).setCellStyle(cellStyle);
			row.createCell(1).setCellValue("USER GROUP");
			row.getCell(1).setCellStyle(cellStyle);
			row.createCell(2).setCellValue("No Of Wins");
			row.getCell(2).setCellStyle(cellStyle);
			row.createCell(3).setCellValue("Deal Value(USD)");
			row.getCell(3).setCellStyle(cellStyle);
			row.createCell(4).setCellValue("No Of Losses");
			row.getCell(4).setCellStyle(cellStyle);
			row.createCell(5).setCellValue("Deal Value(USD)");
			row.getCell(5).setCellStyle(cellStyle);
			row.createCell(6).setCellValue("No Of Losses");
			row.getCell(6).setCellStyle(cellStyle);
			row.createCell(7).setCellValue("Deal Value(USD)");
			row.getCell(7).setCellStyle(cellStyle);
			row.createCell(8).setCellValue("No Of Pipelines");
			row.getCell(8).setCellStyle(cellStyle);
			row.createCell(9).setCellValue("Deal Value(USD)");
			row.getCell(9).setCellStyle(cellStyle);
			row.createCell(10).setCellValue("No Of Prospects");
			row.getCell(10).setCellStyle(cellStyle);
			row.createCell(11).setCellValue("Deal Value(USD)");
			row.getCell(11).setCellStyle(cellStyle);
		}

		/**
		 * This method retrieves the opportunity Count and Digital deal value based on user id.
		 * @param userId
		 * @param financialYear
		 * @param geoList
		 * @param serviceLinesList
		 * @param countryList
		 * @param iouList
		 * @return BDMDealValueDTO
		 * @throws Exception
		 */
		private BDMDealValueDTO getOpportunityCountAndDigitalDealValueByUser(String userId, String financialYear, List<String> geoList,
				List<String> serviceLinesList, List<String> countryList, List<String> iouList) throws Exception {
			BDMDealValueDTO bdmDealValueDTO = new BDMDealValueDTO();
			Date fromDate = null;
			Date toDate = null;
			BigDecimal dealValue = new BigDecimal(0);
			UserT userT = userRepository.findByUserId(userId);
			if(financialYear.equals("")){
				fromDate = DateUtils.getDateFromFinancialYear(DateUtils.getCurrentFinancialYear(), true);
				toDate = DateUtils.getDateFromFinancialYear(DateUtils.getCurrentFinancialYear(), false);
			}
			List<Integer> winsSalesStage = new ArrayList<Integer>();
			winsSalesStage.add(9);
			Object[][] oppCountDealValueWins = opportunityRepository.getOpportunityCountAndDealValueByUser(userId, winsSalesStage, geoList, countryList, serviceLinesList, fromDate, toDate, iouList);
			
			List<Integer> lossSalesStage = new ArrayList<Integer>();
			lossSalesStage.add(10);
			Object[][] oppCountDealValueLosses = opportunityRepository.getOpportunityCountAndDealValueByUser(userId, lossSalesStage, geoList, countryList, serviceLinesList, fromDate, toDate, iouList);
			
			List<Integer> otherLossSalesStage = new ArrayList<Integer>();
			otherLossSalesStage.add(11);otherLossSalesStage.add(12);otherLossSalesStage.add(13);
			Object[][] oppCountDealValueOtherLoss = opportunityRepository.getOpportunityCountAndDealValueByUser(userId, otherLossSalesStage, geoList, countryList, serviceLinesList, fromDate, toDate, iouList);
			
			List<Integer> pipelineSalesStage = new ArrayList<Integer>();
			pipelineSalesStage.add(4);pipelineSalesStage.add(5);pipelineSalesStage.add(6);pipelineSalesStage.add(7);pipelineSalesStage.add(8);
			Object[][] oppCountDealValuePipeline = opportunityRepository.getOpportunityCountAndDealValueByUser(userId, pipelineSalesStage, geoList, countryList, serviceLinesList, fromDate, toDate, iouList);
			
			List<Integer> prospectsSalesStage = new ArrayList<Integer>();
			prospectsSalesStage.add(0);prospectsSalesStage.add(1);prospectsSalesStage.add(2);prospectsSalesStage.add(3);
			Object[][] oppCountDealValueProspects = opportunityRepository.getOpportunityCountAndDealValueByUser(userId, prospectsSalesStage, geoList, countryList, serviceLinesList, fromDate, toDate, iouList);
			
			//setting bean
			bdmDealValueDTO.setUserName(userT.getUserName());
			
			bdmDealValueDTO.setUserGroup(userT.getUserGroup());
			
			bdmDealValueDTO.setWinsOpportunityCount(((BigInteger) oppCountDealValueWins[0][0]).intValue());
			if(oppCountDealValueWins[0][1]!=null){
			bdmDealValueDTO.setWinsDigitalDealValue(((BigDecimal) oppCountDealValueWins[0][1]));
			} else {
				bdmDealValueDTO.setWinsDigitalDealValue(dealValue);
			}
			
			bdmDealValueDTO.setLossOpportunityCount(((BigInteger) oppCountDealValueLosses[0][0]).intValue());
			if(oppCountDealValueLosses[0][1]!=null){
			bdmDealValueDTO.setLossDigitalDealValue(((BigDecimal) oppCountDealValueLosses[0][1]));
			} else {
				bdmDealValueDTO.setLossDigitalDealValue(dealValue);
			}
			
			bdmDealValueDTO.setOtherLossOpportunityCount(((BigInteger) oppCountDealValueOtherLoss[0][0]).intValue());
			if(oppCountDealValueOtherLoss[0][1]!=null){
			bdmDealValueDTO.setOtherLossDigitalDealValue(((BigDecimal) oppCountDealValueOtherLoss[0][1]));
			} else {
				bdmDealValueDTO.setOtherLossDigitalDealValue(dealValue);
			}
			
			bdmDealValueDTO.setPipelineOpportunityCount(((BigInteger) oppCountDealValuePipeline[0][0]).intValue());
			if(oppCountDealValuePipeline[0][1]!=null){
			bdmDealValueDTO.setPipelineDigitalDealValue(((BigDecimal) oppCountDealValuePipeline[0][1]));
			} else {
				bdmDealValueDTO.setPipelineDigitalDealValue(dealValue);
			}
			
			bdmDealValueDTO.setProspectsOpportunityCount(((BigInteger) oppCountDealValueProspects[0][0]).intValue());
			if(oppCountDealValueProspects[0][1]!=null){
			bdmDealValueDTO.setProspectsDigitalDealValue(((BigDecimal) oppCountDealValueProspects[0][1]));
			} else {
				bdmDealValueDTO.setProspectsDigitalDealValue(dealValue);
			}
			
			return bdmDealValueDTO;
		}
		

		/**
		 * This method inserts the Geo or IOU heads details into an Excel sheet
		 * @param currentRow
		 * @param spreadSheet
		 * @param bdmPerfromanceGeoIouDashboardResponse
		 * @param userId 
		 * @param isGeoOrIouHead 
		 */
		private void setGeoOrIouHeadsDetailsToExcel(int currentRow, SXSSFSheet spreadSheet,
				BDMPerfromanceGeoIouDashboardResponse bdmPerfromanceGeoIouDashboardResponse, String userId, boolean isGeoOrIouHead) {
			SXSSFRow row = null;
			double winsGap = 0.0;
			double pipelineGap = 0.0;
			int dealsAboveTwentyMillionGap = 0;
			int dealsAboveTenMillionGap = 0;
			int digitalReImaginationDealsGap = 0;
			double winRatioGap = 0.0;
			double serviceLineGap = 0.0;
			bdmPerfromanceGeoIouDashboardResponse.getWinsTarget();
				
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
			row.createCell(0).setCellValue(bdmPerfromanceGeoIouDashboardResponse.getUserName());
			row.createCell(1).setCellValue("WINS (USD)");
			double winsTarget = bdmPerfromanceGeoIouDashboardResponse.getWinsTarget().doubleValue();
			double winsAchieved =  bdmPerfromanceGeoIouDashboardResponse.getGeoOrIouHeadAchieved().get(0).getWinsAchieved().doubleValue();
			if(winsTarget!= 0.0){
			winsGap = (winsAchieved - winsTarget);
			}
			row.createCell(2).setCellValue(winsTarget);
			row.createCell(3).setCellValue(winsAchieved);
			if(winsTarget!=0.0){
			row.createCell(4).setCellValue(winsGap);
			}
			currentRow++;
			
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
			row.createCell(1).setCellValue("Pipeline funnel (5x Sales target)");
			double pipelineTarget = bdmPerfromanceGeoIouDashboardResponse.getPipelineFunnelTarget().doubleValue();
			double pipelineAchieved =  bdmPerfromanceGeoIouDashboardResponse.getPipelineFunnelAchieved().get(0).getAchieved().doubleValue();
			if(pipelineTarget!= 0.0){
			pipelineGap = (pipelineAchieved - pipelineTarget);
			}
			row.createCell(2).setCellValue(pipelineTarget);
			row.createCell(3).setCellValue(pipelineAchieved);
			if(pipelineTarget!=0.0){
			row.createCell(4).setCellValue(pipelineGap);
			}
			currentRow++;
				
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
			row.createCell(1).setCellValue("Deals >20M");
			int dealsAboveTwentyMillionTarget = bdmPerfromanceGeoIouDashboardResponse.getDealsAboveTwentyMillionTarget();
			int dealsAboveTwentyMillionAchieved =  bdmPerfromanceGeoIouDashboardResponse.getGeoOrIouHeadAchieved().get(0).getDealsAboveTwentyMillionAchieved();
			if(dealsAboveTwentyMillionTarget!=0){
			dealsAboveTwentyMillionGap = (dealsAboveTwentyMillionAchieved - dealsAboveTwentyMillionTarget);
			}
			row.createCell(2).setCellValue(dealsAboveTwentyMillionTarget);
			row.createCell(3).setCellValue(dealsAboveTwentyMillionAchieved);
			if(dealsAboveTwentyMillionTarget!=0){
			row.createCell(4).setCellValue(dealsAboveTwentyMillionGap);
			}
			currentRow++;
			
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
			row.createCell(1).setCellValue("Deals >10M");
			int dealsAboveTenMillionTarget = bdmPerfromanceGeoIouDashboardResponse.getDealsAboveTenMillionTarget();
			int dealsAboveTenMillionAchieved =  bdmPerfromanceGeoIouDashboardResponse.getGeoOrIouHeadAchieved().get(0).getDealsAboveTenMillionAchieved();
			if(dealsAboveTenMillionTarget!=0){
			dealsAboveTenMillionGap = (dealsAboveTenMillionAchieved - dealsAboveTenMillionTarget);
			}
			row.createCell(2).setCellValue(dealsAboveTenMillionTarget);
			row.createCell(3).setCellValue(dealsAboveTenMillionAchieved);
			if(dealsAboveTenMillionTarget!=0){
			row.createCell(4).setCellValue(dealsAboveTenMillionGap);
			}
			currentRow++;
			
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
			row.createCell(1).setCellValue("Digital Re-imagination deals");
			int digitalReImaginationDealsTarget = bdmPerfromanceGeoIouDashboardResponse.getDealsAboveTenMillionTarget();
			int digitalReImaginationDealsAchieved =  bdmPerfromanceGeoIouDashboardResponse.getGeoOrIouHeadAchieved().get(0).getDealsAboveTenMillionAchieved();
			if(digitalReImaginationDealsTarget!=0){
			digitalReImaginationDealsGap = (digitalReImaginationDealsAchieved - digitalReImaginationDealsTarget);
			}
			row.createCell(2).setCellValue(digitalReImaginationDealsTarget);
			row.createCell(3).setCellValue(digitalReImaginationDealsAchieved);
			if(digitalReImaginationDealsTarget!=0){
			row.createCell(4).setCellValue(digitalReImaginationDealsGap);
			}
			currentRow++;
			
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
			row.createCell(1).setCellValue("Overall Win ratio");
			double winRatioTarget = bdmPerfromanceGeoIouDashboardResponse.getOverAllWinRatioTarget();
			double winRatioAchieved =  bdmPerfromanceGeoIouDashboardResponse.getGeoOrIouHeadAchieved().get(0).getOverAllWinRatioAchieved();
			if(winRatioTarget!=0.0){
			winRatioGap = (winRatioAchieved - winRatioTarget);
			}
			row.createCell(2).setCellValue(winRatioTarget+"%");
			row.createCell(3).setCellValue(winRatioAchieved+"%");
			if(winRatioTarget!=0.0){
			row.createCell(4).setCellValue(winRatioGap+"%");
			}
			currentRow++;
			
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
			row.createCell(1).setCellValue("Accounts with service line penetration > 3");
			double serviceLineTarget = bdmPerfromanceGeoIouDashboardResponse.getAccountsWithSpPenetrationAboveThreeTarget();
			double serviceLineAchieved =  bdmPerfromanceGeoIouDashboardResponse.getGeoOrIouHeadAchieved().get(0).getAccountsWithSpPenetrationAboveThreeAchieved();
			if(serviceLineTarget!=0.0){
			serviceLineGap = (serviceLineAchieved - serviceLineTarget);
			}
			row.createCell(2).setCellValue(serviceLineTarget);
			row.createCell(3).setCellValue(serviceLineAchieved);
			if(serviceLineTarget!=0.0){
			row.createCell(4).setCellValue(serviceLineGap);
			}
			currentRow++;
			if(isGeoOrIouHead){
			row = (SXSSFRow) spreadSheet.createRow((short) ++currentRow);
			row.createCell(0).setCellValue(" Note: Target & achieved displayed is for full year 2015-16 (no filter, user selection conditions are applied)");
			}
		}

		/**
		 * This method sets the Header values for Geo or IOU Heads into an excel sheet
		 * @param currentRow
		 * @param spreadSheet
		 * @param cellStyle
		 */
		private void setGeoOrIouHeadsHeaderToExcel(int currentRow,
				SXSSFSheet spreadSheet, CellStyle cellStyle) {
			SXSSFRow row = null;
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
			row.createCell(0).setCellValue("BDM");
			row.getCell(0).setCellStyle(cellStyle);
			row.createCell(1).setCellValue("Targets Defined");
			row.getCell(1).setCellStyle(cellStyle);
			row.createCell(2).setCellValue("Sales Goals");
			row.getCell(2).setCellStyle(cellStyle);
			row.createCell(3).setCellValue("Achieved");
			row.getCell(3).setCellStyle(cellStyle);
			row.createCell(4).setCellValue("Gap/Surplus");
			row.getCell(4).setCellStyle(cellStyle);
		}

		/**
		 * This method is used to get the Geo or IOU head performance details based on user access privileges
		 * @param userId
		 * @param year
		 * @param geoList
		 * @param countryList
		 * @param serviceLinesList
		 * @return
		 * @throws Exception
		 */
		private BDMPerfromanceGeoIouDashboardResponse getGeoOrIouHeadPerformanceByUserAccessPrivileges(String userId, String year) throws Exception {
			String financialYear = null;
			BDMPerfromanceGeoIouDashboardResponse bdmPerfromanceGeoIouDashboardResponse = new BDMPerfromanceGeoIouDashboardResponse();
			if(year.equals("")){
				financialYear = DateUtils.getCurrentFinancialYear();
			}
			bdmService.setTargetValuesForGeoHeadsOrIouSpocsDashboard(userId, financialYear, bdmPerfromanceGeoIouDashboardResponse, true);
			bdmPerfromanceGeoIouDashboardResponse = bdmService.getGeoIouPerformanceDashboardBasedOnUserPrivileges(userId, financialYear, bdmPerfromanceGeoIouDashboardResponse);
			
			return bdmPerfromanceGeoIouDashboardResponse;
		}

		
		/**
		 * This Method retrieves the BDM Supervisor performance details
		 * @param userIds 
		 * @param year  * @param from * @param to * @param geography * @param country  * @param serviceLines
		 * @param serviceLinesList * @param salesStage * @param opportunityOwners * @param workbook
		 * @throws Exception 
		 */
		private void getBDMSupervisorPerformanceExcelReport(List<String> userIds, String year, SXSSFWorkbook workbook) throws Exception {
			logger.debug("Inside getBDMSupervisorPerformanceExcelReport() method");
			BDMSupervisorDashboardDTO bdmSupervisorDashboardDetails = null;
			boolean isDashboardByYear = true;
			bdmSupervisorDashboardDetails = bdmService.getBDMSupervisorDashboardByUser(userIds, year, isDashboardByYear);
			setBDMSupervisorPerformanceToExcel(bdmSupervisorDashboardDetails, workbook);	
		}


		/**
		 * This method inserts the BDM Supervisor Performance details into an excel sheet
		 * @param bdmSupervisorDashboardDetails
		 * @param workbook
		 */
		private void setBDMSupervisorPerformanceToExcel(BDMSupervisorDashboardDTO bdmSupervisorDashboardDetails,
				SXSSFWorkbook workbook) {
			SXSSFSheet spreadSheet = (SXSSFSheet) workbook.createSheet("Team Summary");
			int currentRow = 0;
			CellStyle cellStyle = ExcelUtils.createRowStyle(workbook, ReportConstants.REPORTHEADER);
			setBDMSupervisorHeaderToExcel(currentRow, spreadSheet, cellStyle);
			currentRow++;
			currentRow++;
			setBDMSupervisorDetails(currentRow, spreadSheet, bdmSupervisorDashboardDetails);
		}

		/**
		 * This method is used to set the BDM Supervisor details into an excel sheet
		 * @param currentRow
		 * @param spreadSheet
		 * @param bdmSupervisorDashboardDetails
		 */
		private void setBDMSupervisorDetails(int currentRow, SXSSFSheet spreadSheet,
				BDMSupervisorDashboardDTO bdmSupervisorDashboardDetails) {
			SXSSFRow row = null;
			for(DashBoardBDMResponse dashBoardBDMResponse:bdmSupervisorDashboardDetails.getBdmSupervisorDashboard()){
				dashBoardBDMResponse.getBdmDashboard().get(0).getSalesOwnerOppWinsAchieved();
				dashBoardBDMResponse.getBdmDashboard().get(0).getPrimaryProposalSupportAchieved();
				dashBoardBDMResponse.getBdmDashboard().get(0).getSalesProposalSupportAchieved();
				dashBoardBDMResponse.getBdmDashboard().get(0).getConnectPrimary();
				dashBoardBDMResponse.getBdmDashboard().get(0).getConnectSecondary();
				String userName = dashBoardBDMResponse.getUserT().getUserName();
				row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
				row.createCell(0).setCellValue(userName);
				row.createCell(1).setCellValue(dashBoardBDMResponse.getUserT().getUserGroup());
				row.createCell(2).setCellValue("Opportunity Win value (USD)");
				double oppWinsTarget = dashBoardBDMResponse.getWinsTarget().doubleValue();
				double oppWinsGap = 0;
				double primaryOwnerOppWins = 0;
				double salesOwnerOppWins = 0;
				
				row.createCell(3).setCellValue(oppWinsTarget);
				if(dashBoardBDMResponse.getBdmDashboard().get(0).getPrimaryOrBidOppWinsAchieved()!=null){
					primaryOwnerOppWins = dashBoardBDMResponse.getBdmDashboard().get(0).getPrimaryOrBidOppWinsAchieved().doubleValue();
				}
				row.createCell(4).setCellValue(primaryOwnerOppWins);
				if(dashBoardBDMResponse.getBdmDashboard().get(0).getSalesOwnerOppWinsAchieved()!=null){
					salesOwnerOppWins = dashBoardBDMResponse.getBdmDashboard().get(0).getSalesOwnerOppWinsAchieved().doubleValue();
				}
				row.createCell(5).setCellValue(salesOwnerOppWins);
				if(oppWinsTarget!=0.0){
				BigDecimal totalOppWinsAchieved = new BigDecimal(0);
				if(dashBoardBDMResponse.getBdmDashboard().get(0).getTotalOppWinsAchieved()!=null){
				totalOppWinsAchieved = dashBoardBDMResponse.getBdmDashboard().get(0).getTotalOppWinsAchieved();
				}
				oppWinsGap = (totalOppWinsAchieved.doubleValue() - oppWinsTarget);
				row.createCell(6).setCellValue(oppWinsGap);
				}
				currentRow++;
				
				row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
				row.createCell(2).setCellValue("Proposals Supported");
				int proposalSupportedTarget = dashBoardBDMResponse.getProposalSupportedTarget();
				int proposalSupportedGap = 0;
				
				row.createCell(3).setCellValue(proposalSupportedTarget);
				row.createCell(4).setCellValue(dashBoardBDMResponse.getBdmDashboard().get(0).getPrimaryProposalSupportAchieved());
				row.createCell(5).setCellValue(dashBoardBDMResponse.getBdmDashboard().get(0).getSalesProposalSupportAchieved());
				if(proposalSupportedTarget!=0){
					proposalSupportedGap = (dashBoardBDMResponse.getBdmDashboard().get(0).getTotalProposalSupportAchieved() - proposalSupportedTarget);
					row.createCell(6).setCellValue(proposalSupportedGap);
				}
				currentRow++;
				
				row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
				row.createCell(2).setCellValue("Connects Supported");
				int connectSupportedTarget = dashBoardBDMResponse.getConnectSupportedTarget();
				int connectSupportedGap = 0;
				
				row.createCell(3).setCellValue(connectSupportedTarget);
				row.createCell(4).setCellValue(dashBoardBDMResponse.getBdmDashboard().get(0).getConnectPrimary());
				row.createCell(5).setCellValue(dashBoardBDMResponse.getBdmDashboard().get(0).getConnectSecondary());
				if(connectSupportedTarget!=0){
					connectSupportedGap = (dashBoardBDMResponse.getBdmDashboard().get(0).getTotalConnects() - connectSupportedTarget);
					row.createCell(6).setCellValue(connectSupportedGap);
					}
				currentRow++;
			}
			row = (SXSSFRow) spreadSheet.createRow((short) ++currentRow);
			row.createCell(0).setCellValue(" Note: Target & achieved displayed is for full year" +DateUtils.getCurrentFinancialYear()+" (no filter, user selection conditions are applied)");
		}

		/**
		 * This method is used to set the header values for BDM Supervisor performance into the excel sheet
		 * @param currentRow
		 * @param spreadSheet
		 * @param cellStyle
		 */
		private void setBDMSupervisorHeaderToExcel(int currentRow, SXSSFSheet spreadSheet, CellStyle cellStyle) {
			SXSSFRow row = null;
			int firstRowColNo=0;
			int secRowColNo=4;
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
			row.createCell(firstRowColNo).setCellValue("BDM");
			row.getCell(firstRowColNo).setCellStyle(cellStyle);
			firstRowColNo++;
			row.createCell(firstRowColNo).setCellValue("User Group");
			row.getCell(firstRowColNo).setCellStyle(cellStyle);
			firstRowColNo++;
			row.createCell(firstRowColNo).setCellValue("Targets Defined");
			row.getCell(firstRowColNo).setCellStyle(cellStyle);
			firstRowColNo++;
			row.createCell(firstRowColNo).setCellValue("Sales Goals");
			row.getCell(firstRowColNo).setCellStyle(cellStyle);
			firstRowColNo++;
			row.createCell(firstRowColNo).setCellValue("Achieved");
			row.getCell(firstRowColNo).setCellStyle(cellStyle);
			spreadSheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, firstRowColNo, firstRowColNo+1));
			firstRowColNo++;
			row.createCell(firstRowColNo).setCellValue("Gap/Surplus");
			row.getCell(firstRowColNo).setCellStyle(cellStyle);
			
			row = (SXSSFRow) spreadSheet.createRow((short) ++currentRow);
			row.createCell(secRowColNo).setCellValue("Primary/Bid");
			row.getCell(secRowColNo).setCellStyle(cellStyle);
			secRowColNo++;
			row.createCell(secRowColNo).setCellValue("Secondary");
			row.getCell(secRowColNo).setCellStyle(cellStyle);
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