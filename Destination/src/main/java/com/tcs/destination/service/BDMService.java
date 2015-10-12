package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;
import com.tcs.destination.bean.BDMDashBoardResponse;
import com.tcs.destination.bean.BDMPerfromanceGeoIouDashboardResponse;
import com.tcs.destination.bean.BDMSupervisorDashboardDTO;
import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.DashBoardBDMResponse;
import com.tcs.destination.bean.GeoIouDashboardDTO;
import com.tcs.destination.bean.LeadershipOverallWinsDTO;
import com.tcs.destination.bean.LeadershipWinsDTO;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.PipelineDTO;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.BdmTargetTRepository;
import com.tcs.destination.data.repository.BidDetailsTRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.UserAccessPrivilegeQueryBuilder;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;

@Service
public class BDMService {


	private static final Logger logger = LoggerFactory
			.getLogger(BDMService.class);

	private static final int ONE_DAY_IN_MILLIS = 86400000;
	
	private static final String OPP_WINS_QUERY_PART1 = "select sum(deal_value) as wins from (";
	
	private static final String OPP_WINS_QUERY_PART2 = "select distinct opp.opportunity_id, ";
	
	private static final String PIPELINE_FUNNEL_QUERY_PART = "select distinct sales_stage_code , ";
	
	private static final String DEAL_VALUE_AND_JOINS = "sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / " 
			+ " (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('USD'))) as deal_value from opportunity_t OPP JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ " JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id " 
			+ " JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou where ";
	
	private static final String SUP_SP_ABOVE_THREE_QUERY_PART = "select count(countOfSubSp) from (select ARDT.finance_customer_name, count(distinct(ARDT.sub_sp)) as countOfSubSp " 
			+ " from actual_revenues_data_t ARDT JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = ARDT.finance_customer_name " 
			+ " and RCMT.customer_geography=ARDT.finance_geography and RCMT.finance_iou =ARDT.finance_iou) " 
			+ " JOIN iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou " 
			+ " JOIN sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp where ";
	
	private static final String MONTHS = " upper(ARDT.month) in (";
	
	private static final String GROUP_BY_FINANCE_CUSTOMER = " group by ARDT.finance_customer_name order by ARDT.finance_customer_name "; 
	
	private static final String COUNT_GREATER_THAN_THREE = " ) as subSpPenetration where countOfSubSp>3";

	private static final String AS_SUP_SP_PENETRATION = " ) as subSpPenetration";
	
	private static final String OPP_WINS_GROUP_BY_PART1 = " group by opp.opportunity_id";
	private static final String OPP_WINS_GROUP_BY_PART2 = ") as geoHeadsOrIouSpocsWinsOrLosses";
	
	private static final String GROUP_BY_SALES_STAGE = " group by sales_stage_code";
	
	private static final String SALES_STAGE_CODE = " sales_stage_code=";
	private static final String DIGITAL_FLAG = " digital_flag='Y'";
	
	private static final String RCMT_GEO_COND_PREFIX = "RCMT.customer_geography in (";
	
	private static final String GEO_COND_PREFIX = "GMT.geography in (";
	private static final String SUBSP_COND_PREFIX = "SSMT.display_sub_sp in (";
	private static final String IOU_COND_PREFIX = "ICMT.display_iou in (";
	private static final String DEAL_CLOSURE_DATE_BETWEEN = " and deal_closure_date between '";
	
	private static final String  CUSTOMER_NAME = "RCMT.customer_name in (";
	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	BeaconConverterService beaconConverterService;

	@Autowired
	BidDetailsTRepository bidDetailsTRepository;
	
	@Autowired
	BdmTargetTRepository bdmTargetRepository;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ConnectRepository connectRepository;
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserAccessPrivilegeQueryBuilder userAccessPrivilegeQueryBuilder;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	/**
	 * This Method used to find BDM Dashboard Details
	 * @param userId
	 * @param financialYear
	 * @param isDashboardByYear
	 * @return
	 * @throws Exception
	 */
	public DashBoardBDMResponse getOpportunityWinsByBDM(String userId,
			String financialYear, boolean isDashboardByYear) throws Exception {
		logger.debug("Inside getOpportunityWinsByBDM()");
		DashBoardBDMResponse opportunityWinValueDTO = null;
		UserT user = userService.findByUserId(userId);
		if (user != null) {
			String userGroup = user.getUserGroupMappingT().getUserGroup();
			if (userGroup.equals("BDM")) {
				if (isDashboardByYear) {
					opportunityWinValueDTO = getBDMDashBoardByYear(userId, financialYear);
				} else {
					opportunityWinValueDTO = getBDMDashBoardByQuarter(userId, financialYear);
				}
			} else {
				logger.error("NOT_FOUND: User is not BDM: {}", userId);
				throw new DestinationException(HttpStatus.NOT_FOUND, "User is not BDM: " + userId);
			}
		} else {
			logger.error("NOT_FOUND: User not found: {}", userId);
			throw new DestinationException(HttpStatus.NOT_FOUND, "User not found: " + userId);
		}
		return opportunityWinValueDTO;
	}
	
	/**
	 * This Method used to get BDM Supervisors Dashboard Details
	 * @param userId
	 * @param financialYear
	 * @param isDashboardByYear
	 * @param isAlongWithSupervisor 
	 * @return
	 * @throws Exception
	 */
	public BDMSupervisorDashboardDTO getBDMSupervisorByUserId(String userId,
			String financialYear, boolean isDashboardByYear, boolean isAlongWithSupervisor) throws Exception {
		logger.debug("Inside getBDMSupervisorByUserId()");
		List<String> userIds = null;
		BDMSupervisorDashboardDTO bdmSupervisorDashboardDetails = null;
		UserT user = userService.findByUserId(userId);
		if (user != null) {
			String userGroup = user.getUserGroupMappingT().getUserGroup();
			if (userGroup.equals("BDM Supervisor")) {
				userIds = new ArrayList<String>();
				bdmSupervisorDashboardDetails = new BDMSupervisorDashboardDTO();
				
				userIds = userRepository.getAllSubordinatesIdBySupervisorId(userId);
				if(isAlongWithSupervisor){
					userIds.add(userId);
				}
				if (userIds.isEmpty()) {
					logger.error("NOT_FOUND, Subordinates not present");
					throw new DestinationException(HttpStatus.NOT_FOUND, "Subordinates not present");
				}
				bdmSupervisorDashboardDetails = getBDMSupervisorDashboardByUser(userIds, financialYear, isDashboardByYear);
			} else {
				logger.error("NOT_FOUND: User is not BDM Supervisor: {}", userId);
				throw new DestinationException(HttpStatus.NOT_FOUND, "User is not BDM Supervisor: " + userId);
			}
		} else {
			logger.error("NOT_FOUND: User not found: {}", userId);
			throw new DestinationException(HttpStatus.NOT_FOUND, "User not found: " + userId);
		}
		return bdmSupervisorDashboardDetails;
	}
	
	/**
	 * This method is used to get Goe Heads Or Iou Heads Performance Details
	 * @param userId
	 * @param financialYear
	 * @param isDashboardByYear
	 * @return
	 * @throws Exception
	 */
	public BDMPerfromanceGeoIouDashboardResponse getGeoIouPerformanceDashboard(String userId, String financialYear, 
			boolean isDashboardByYear) throws Exception {
		
		BDMPerfromanceGeoIouDashboardResponse bdmPerfromanceGeoIouDashboardResponse=new BDMPerfromanceGeoIouDashboardResponse();
		
		UserT user = userService.findByUserId(userId);
		if (user != null) {
		    
			String userGroup = user.getUserGroupMappingT().getUserGroup();
		    
		    if (UserGroup.contains(userGroup)) {
		    // Validate user group, BDM's & BDM supervisor's are not authorized for this service
			switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
			case BDM:
			case BDM_SUPERVISOR:
				logger.error("User is not authorized to access this service");
			    throw new DestinationException(HttpStatus.UNAUTHORIZED,  "User is not authorised to access this service");
			case GEO_HEADS:
			case IOU_HEADS:
				if(financialYear.equals("")){
					financialYear=DateUtils.getCurrentFinancialYear();
					}
				setTargetValuesForGeoHeadsOrIouSpocsDashboard(userId, financialYear, bdmPerfromanceGeoIouDashboardResponse, isDashboardByYear);
				
				if(isDashboardByYear){
				
					bdmPerfromanceGeoIouDashboardResponse = getGeoIouPerformanceDashboardBasedOnUserPrivileges(userId, financialYear, bdmPerfromanceGeoIouDashboardResponse);

				} else {
				
					bdmPerfromanceGeoIouDashboardResponse = getGeoIouPerformanceDashboardByQuartersBasedOnUserPrivileges(userId, financialYear, bdmPerfromanceGeoIouDashboardResponse);
				}
				break;
			default :
				 logger.error("NOT_FOUND: User is not Geo Head Or Iou Head: {}", userId);
				  throw new DestinationException(HttpStatus.NOT_FOUND, "User is not Geo Head Or Iou Head: " + userId);
			}
	    }
		} else {
		    logger.error("NOT_FOUND: User not found: {}", userId);
		    throw new DestinationException(HttpStatus.NOT_FOUND, "User not found: " + userId);
		}
		return bdmPerfromanceGeoIouDashboardResponse;
	}
	
	
	/**
	 * This method used to get BDM Supervisors Dashboard by user and financial year or quarters
	 * @param userIds
	 * @param financialYear
	 * @param isDashboardByYear
	 * @return
	 * @throws Exception
	 */
	public List<DashBoardBDMResponse> getBDMSupervisorDashBoardByUser(List<String> userIds,	String financialYear, boolean isDashboardByYear) throws Exception {
		List<DashBoardBDMResponse> dashBoardBDMResponseList = new ArrayList<DashBoardBDMResponse>();
		DashBoardBDMResponse dashBoardBDMResponse = new DashBoardBDMResponse();
		for(String userId:userIds){
			if(isDashboardByYear){
				dashBoardBDMResponse = getBDMDashBoardByYear(userId, financialYear);
		    	} else {
		    	dashBoardBDMResponse = getBDMDashBoardByQuarter(userId, financialYear);
		    	}
			dashBoardBDMResponseList.add(dashBoardBDMResponse);
		}
		return dashBoardBDMResponseList;
	}

	
	public BDMSupervisorDashboardDTO getBDMSupervisorDashboardByUser(List<String> userIds, String financialYear, boolean isDashboardByYear) throws Exception {
		BDMSupervisorDashboardDTO bdmSupervisorDashboardDTO = new BDMSupervisorDashboardDTO();
		List<DashBoardBDMResponse> dashBoardBDMResponseList = new ArrayList<DashBoardBDMResponse>();
		DashBoardBDMResponse dashBoardBDMResponse = new DashBoardBDMResponse();
		BigDecimal totalOppOwnerWinValue=new BigDecimal(0);
		int totalProposalsSupportedValue = 0;
		int totalConnectsSupportedValue = 0;
		for(String userId:userIds){
			if(isDashboardByYear){
				dashBoardBDMResponse = getBDMDashBoardByYear(userId, financialYear);
		    	} else {
		    	dashBoardBDMResponse = getBDMDashBoardByQuarter(userId, financialYear);
		    	}
			for(BDMDashBoardResponse bdmDashBoardResponse:dashBoardBDMResponse.getBdmDashboard()){
				totalOppOwnerWinValue=totalOppOwnerWinValue.add(bdmDashBoardResponse.getTotalOppWinsAchieved());
				totalProposalsSupportedValue=totalProposalsSupportedValue+bdmDashBoardResponse.getTotalProposalSupportAchieved();
				totalConnectsSupportedValue= totalConnectsSupportedValue+bdmDashBoardResponse.getTotalConnects();
			}
			
			dashBoardBDMResponseList.add(dashBoardBDMResponse);
		}
		bdmSupervisorDashboardDTO.setTotalOpportunityWinsAchieved(totalOppOwnerWinValue);
		bdmSupervisorDashboardDTO.setTotalProposalSupportedAchieved(totalProposalsSupportedValue);
		bdmSupervisorDashboardDTO.setTotalConnectSupportedAchieved(totalConnectsSupportedValue);
		bdmSupervisorDashboardDTO.setBdmSupervisorDashboard(dashBoardBDMResponseList);
		return bdmSupervisorDashboardDTO;
	}
	
	/**
	 * This method used to get BDM Dashboard details by financial
	 * @param userId
	 * @param financialYear
	 * @param bdmSupervisorDashboardDTO 
	 * @param totalConnectsSupportedValue2 
	 * @param totalProposalsSupportedValue2 
	 * @param totalOppOwnerWinValue2 
	 * @return
	 * @throws Exception
	 */
	private DashBoardBDMResponse getBDMDashBoardByYear(String userId, String financialYear) throws Exception {
		boolean isCurrentFinancialYear=false;
		DashBoardBDMResponse dashBoardBDMResponse = new DashBoardBDMResponse();
		List<BDMDashBoardResponse> bdmOppWinValueDTO = new ArrayList<BDMDashBoardResponse>();
		if (financialYear.equals("")) {
			logger.debug("Financial Year is Empty");
			financialYear = DateUtils.getCurrentFinancialYear();
		}
		if(financialYear.equals(DateUtils.getCurrentFinancialYear())){
			isCurrentFinancialYear=true;
		}
		Date fDate = DateUtils.getDateFromFinancialYear(financialYear, true);
		Date tDate = DateUtils.getDateFromFinancialYear(financialYear, false);
		dashBoardBDMResponse.setUserId(userId);

		setBDMOrSupervisorDashboardTarget(userId, financialYear, dashBoardBDMResponse);
		
		bdmOppWinValueDTO = getBDMPerformanceByUser(bdmOppWinValueDTO, userId, fDate, tDate, financialYear, isCurrentFinancialYear);
		dashBoardBDMResponse.setBdmDashboard(bdmOppWinValueDTO);
		return dashBoardBDMResponse;
	}

	/**
	 * This method used to get BDM Dashboard details by quarter
	 * @param userId
	 * @param financialYear
	 * @param bdmSupervisorDashboardDTO 
	 * @param totalConnectsSupportedValue2 
	 * @param totalProposalsSupportedValue2 
	 * @param totalOppOwnerWinValue2 
	 * @return
	 * @throws Exception
	 */
	private DashBoardBDMResponse getBDMDashBoardByQuarter(String userId, String financialYear) throws Exception {
		DashBoardBDMResponse dashBoardBDMResponse = new DashBoardBDMResponse();
		List<BDMDashBoardResponse> bdmOppWinValueDTO = new ArrayList<BDMDashBoardResponse>();
		if (financialYear.equals("")) {
			logger.debug("Financial Year is Empty");
			financialYear = DateUtils.getCurrentFinancialYear();
		}
		dashBoardBDMResponse.setUserId(userId);
		//
		setBDMOrSupervisorDashboardTarget(userId, financialYear, dashBoardBDMResponse);
		
		List<String> quarters = DateUtils.getQuarters(financialYear);
		for(String quarter:quarters){
			boolean isCurrentQuarter=false;
			if(quarter.equals(DateUtils.getCurrentQuarter())){
				isCurrentQuarter=true;
			}
			Date fDate = DateUtils.getDateFromQuarter(quarter, true);
			Date tDate = DateUtils.getDateFromQuarter(quarter, false);
			bdmOppWinValueDTO = getBDMPerformanceByUser(bdmOppWinValueDTO, userId, fDate, tDate, quarter, isCurrentQuarter);
			dashBoardBDMResponse.setBdmDashboard(bdmOppWinValueDTO);
			}
		return dashBoardBDMResponse;
	}

	/**
	 * This method used to get BDMDashBoardResponse by user
	 * @param bdmOppWinValueList
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param timeLine
	 * @param isFinYearOrQuarter
	 * @param bdmSupervisorDashboardDTO 
	 * @param totalConnectsSupportedValue 
	 * @param totalProposalsSupportedValue 
	 * @param totalOppOwnerWinValue 
	 * @return
	 */
	private List<BDMDashBoardResponse> getBDMPerformanceByUser(List<BDMDashBoardResponse> bdmOppWinValueList, 
			String userId, Date fromDate, Date toDate, String timeLine, boolean isFinYearOrQuarter) {
		BDMDashBoardResponse bdmDashboardResponse = new BDMDashBoardResponse();
		int primaryOwnerConnects = 0;
		int secondaryOwnerConnects = 0;
		BigDecimal oppOwnerWinValue=new BigDecimal(0);
		BigDecimal salesSupOwnerWinValue=new BigDecimal(0);
		int primaryProposalSupported=0;
		int salesProposalSupported=0;
		Object[][] proposalSupported = null;
		Timestamp fromDateTs = new Timestamp(fromDate.getTime());
		Timestamp toDateTs = new Timestamp(toDate.getTime()	+ ONE_DAY_IN_MILLIS - 1);
		Object[][] opportunityWin = opportunityRepository.findOpportunityWinValueByOpportunityOwnerOrSalesSupportOwner(userId, fromDate, toDate);
		if(isFinYearOrQuarter){ 
			proposalSupported = opportunityRepository.findProposalSupportedByOpportunityOwnerOrSalesSupportOwnerByCurrentQuarterOrYear(userId, fromDate, toDate);
		} else {
			proposalSupported = opportunityRepository.findProposalSupportedByOpportunityOwnerOrSalesSupportOwnerByPreviousQuarterOrYear(userId, fromDate, toDate);
		}
		Object[][] connectsCount = connectRepository.findConnectsByPrimaryOwnerOrSecondaryOwner(userId, fromDateTs, toDateTs);
		if(opportunityWin!=null && opportunityWin.length>0){
			oppOwnerWinValue = ((BigDecimal) opportunityWin[0][1]);
			salesSupOwnerWinValue = ((BigDecimal) opportunityWin[0][2]);
		}
		if(proposalSupported!=null && proposalSupported.length>0){
			if(proposalSupported[0][0]!=null){
				primaryProposalSupported = (((BigDecimal) proposalSupported[0][0]).intValue());
			}
			if(proposalSupported[0][1]!=null){
				salesProposalSupported = (((BigDecimal) proposalSupported[0][1]).intValue());
				}
			}
		if(connectsCount!=null && connectsCount.length>0){
			primaryOwnerConnects = (((BigDecimal) connectsCount[0][1]).intValue());
			secondaryOwnerConnects = (((BigDecimal) connectsCount[0][2]).intValue());
		}
		
		bdmDashboardResponse.setTimeLine(timeLine);
		
		bdmDashboardResponse.setPrimaryOrBidOppWinsAchieved(oppOwnerWinValue);
		bdmDashboardResponse.setSalesOwnerOppWinsAchieved(salesSupOwnerWinValue);
		
		bdmDashboardResponse.setPrimaryProposalSupportAchieved(primaryProposalSupported);
		bdmDashboardResponse.setSalesProposalSupportAchieved(salesProposalSupported);
		
		bdmDashboardResponse.setConnectPrimary(primaryOwnerConnects);
		bdmDashboardResponse.setConnectSecondary(secondaryOwnerConnects);
		
		bdmOppWinValueList.add(bdmDashboardResponse);
		
		return bdmOppWinValueList;
	}

	
	private BDMPerfromanceGeoIouDashboardResponse getGeoIouPerformanceDashboardBasedOnUserPrivileges(String userId, 
		String financialYear, BDMPerfromanceGeoIouDashboardResponse bdmPerfromanceGeoIouDashboardResponse) throws Exception {
		
		List<GeoIouDashboardDTO> geoIouDashboardList = new ArrayList<GeoIouDashboardDTO>();
		
		int reImaginationDealsCount = 0;
		int countOfOpportunitiesAboveTwentyMillion = 0;
    	int countOfOpportunitiesAboveTenMillion = 0;
//		BDMPerfromanceGeoIouDashboardResponse bdmPerfromanceGeoIouDashboardResponse=new BDMPerfromanceGeoIouDashboardResponse();
		
		GeoIouDashboardDTO geoIouDashboardDTO =null;
		
		List<String> formattedMonths=DateUtils.getMonthsFromYear(financialYear); 
		
		Date fromDate=DateUtils.getDateFromFinancialYear(financialYear, true);
		Date toDate=DateUtils.getDateFromFinancialYear(financialYear, false);
		
		// This Method constructs the Query and executes and return sum of wins based on access privileges and date range
		BigDecimal wins=constructQueryForGeoIouPerformanceDashboardWinsPrivileges(userId,fromDate,toDate);
		
		geoIouDashboardDTO = new GeoIouDashboardDTO();
		//populate bean
		geoIouDashboardDTO.setTimeLine(financialYear);
		geoIouDashboardDTO.setWinsAchieved(wins);
		
		//This method constructs and executes the query and returns the opportunityWins List based on access privileges and date range
		List<Object[]> oppWinsList=constructQueryForOpportunityWinsBasedOnPrivileges(userId, fromDate, toDate);
		
    	if(oppWinsList!=null && !oppWinsList.isEmpty()){
    	
    		// Loop through the win list and get the opportunities greater than 1M and 2M
    	   for (Object[] oppWins : oppWinsList) {
    		   double dealValue=0.0;
    		  
    		   if(oppWins[1]!=null){
    		   dealValue = ((BigDecimal) oppWins[1]).doubleValue();
    		   }
    		   
//    		   double convertedDigitalDealValue = (beaconConverterService.convert(Constants.USD, "INR",dealValue)).doubleValue();
    		   if(dealValue > Constants.TWENTY_MILLION){
    			   countOfOpportunitiesAboveTwentyMillion++;
    		   }
    		  
    		   if(dealValue > Constants.TEN_MILLION){
    			   countOfOpportunitiesAboveTenMillion++;
    		   }
    	   }
    	}
		//populate bean
		geoIouDashboardDTO.setDealsAboveTwentyMillionAchieved(countOfOpportunitiesAboveTwentyMillion);
		geoIouDashboardDTO.setDealsAboveTenMillionAchieved(countOfOpportunitiesAboveTenMillion);
		
		//This method constructs and executes the query and returns the Reimagination Deals based on access privileges and date range
		List<Object[]> reImaginationDeals=constructQueryForReimaginationDealsBasedOnPrivileges(userId, fromDate, toDate);
		
		if(reImaginationDeals!=null && !reImaginationDeals.isEmpty()){
			reImaginationDealsCount = reImaginationDeals.size();
		}
		geoIouDashboardDTO.setDigitalReimaginationDealsAchieved(reImaginationDealsCount);
		
		// This Method constructs the Query and executes and return sum of wins based on access privileges and date range
		BigDecimal loss=constructQueryForGeoIouPerformanceDashboardLossesPrivileges(userId,fromDate,toDate);
		double winsRatio=0;
		if(!loss.equals(new BigDecimal(0))){
		winsRatio=wins.divide(wins.add(loss),2,BigDecimal.ROUND_CEILING).doubleValue();
		}
		geoIouDashboardDTO.setOverAllWinRatioAchieved(winsRatio*100);
		
		//Accounts With Sub Sp Penetration 
		BigInteger subSpAboveThree = constructQueryForGeoIouPerformanceDashboardSubSpPenetrationPrivileges(userId, formattedMonths, true);
		BigInteger revCustomers = constructQueryForGeoIouPerformanceDashboardSubSpPenetrationPrivileges(userId, formattedMonths, false);
		BigDecimal subSpGreaterThanThree = new BigDecimal(subSpAboveThree); 
		BigDecimal revenueCustomers = new BigDecimal(revCustomers); 
		double supSpRatio=0;
		if(!revenueCustomers.equals(new BigDecimal(0))){
//		double supSpRatio = (subSpAboveThree.intValue()/revCustomers.intValue())*100;
		supSpRatio = (subSpGreaterThanThree.divide(revenueCustomers, 2, RoundingMode.CEILING)).doubleValue();
		}
		geoIouDashboardDTO.setAccountsWithSpPenetrationAboveThreeAchieved(supSpRatio*100);
		
		geoIouDashboardList.add(geoIouDashboardDTO);
		bdmPerfromanceGeoIouDashboardResponse.setGeoOrIouHeadAchieved(geoIouDashboardList);
    	return bdmPerfromanceGeoIouDashboardResponse;
    }
	
	
	private BDMPerfromanceGeoIouDashboardResponse getGeoIouPerformanceDashboardByQuartersBasedOnUserPrivileges(
			String userId, String financialYear, BDMPerfromanceGeoIouDashboardResponse bdmPerfromanceGeoIouDashboardResponse) throws Exception {
		List<GeoIouDashboardDTO> geoIouDashboardList = new ArrayList<GeoIouDashboardDTO>();
		GeoIouDashboardDTO geoIouDashboardDTO =null;
		List<String> quartersList =DateUtils.getQuarters(financialYear);
		
		for(String quarter:quartersList){
			List<String> formattedMonths=DateUtils.getMonthsFromQuarter(quarter); 
			
			Date fromDate=DateUtils.getDateFromQuarter(quarter, true);
			Date toDate=DateUtils.getDateFromQuarter(quarter, false);
		// This Method constructs the Query and executes and return sum of wins based on access privileges and date range
		BigDecimal wins=constructQueryForGeoIouPerformanceDashboardWinsPrivileges(userId,fromDate,toDate);
		geoIouDashboardDTO = new GeoIouDashboardDTO();
		geoIouDashboardDTO.setTimeLine(quarter);
		geoIouDashboardDTO.setWinsAchieved(wins);
		
		//This method constructs and executes the query and returns the opportunityWins List based on access privileges and date range
		List<Object[]> oppWinsList=constructQueryForOpportunityWinsBasedOnPrivileges(userId, fromDate, toDate);
		 int countOfOpportunitiesAboveTwentyMillion = 0;
    	 int countOfOpportunitiesAboveTenMillion = 0;
		if(oppWinsList!=null){
    	    // Loop through the win list and get the opportunities greater than 1M and 2M
    	   for (Object[] oppWins : oppWinsList) {
    		   double dealValue=0.0;
    		   if(oppWins[1]!=null){
    		   dealValue = ((BigDecimal) oppWins[1]).doubleValue();
    		   }
    		   if(dealValue>Constants.TWENTY_MILLION){
    			   countOfOpportunitiesAboveTwentyMillion++;
    		   }
    		   if(dealValue>Constants.TEN_MILLION){
    			   countOfOpportunitiesAboveTenMillion++;
    		   }
    	   }
    	   	
    	}
		//populate bean
		geoIouDashboardDTO.setDealsAboveTwentyMillionAchieved(countOfOpportunitiesAboveTwentyMillion);
		geoIouDashboardDTO.setDealsAboveTenMillionAchieved(countOfOpportunitiesAboveTenMillion);
		
		//This method constructs and executes the query and returns the Reimagination Deals based on access privileges and date range
		List<Object[]> reImaginationDeals=constructQueryForReimaginationDealsBasedOnPrivileges(userId, fromDate, toDate);
		int reImaginationDealsCount = 0;		
		if(reImaginationDeals!=null && !reImaginationDeals.isEmpty()){
			reImaginationDealsCount = reImaginationDeals.size();
		}
		//Populate bean
		geoIouDashboardDTO.setDigitalReimaginationDealsAchieved(reImaginationDealsCount);
		
		// This Method constructs the Query and executes and return sum of wins based on access privileges and date range
		BigDecimal loss=constructQueryForGeoIouPerformanceDashboardLossesPrivileges(userId,fromDate,toDate);
		double winsRatio=0;
		if(!loss.equals(new BigDecimal(0))){
		winsRatio=wins.divide(wins.add(loss),2,BigDecimal.ROUND_CEILING).doubleValue();
		}
		geoIouDashboardDTO.setOverAllWinRatioAchieved(winsRatio*100);
		
		//Accounts With Sub Sp Penetration 
		BigInteger subSpAboveThree = constructQueryForGeoIouPerformanceDashboardSubSpPenetrationPrivileges(userId, formattedMonths, true);
		BigInteger revCustomers = constructQueryForGeoIouPerformanceDashboardSubSpPenetrationPrivileges(userId, formattedMonths, false);
		BigDecimal subSpGreaterThanThree = new BigDecimal(subSpAboveThree); 
		BigDecimal revenueCustomers = new BigDecimal(revCustomers); 
		double supSpRatio = 0;
		if(!revenueCustomers.equals(new BigDecimal(0))){
//		double supSpRatio = (subSpAboveThree.intValue()/revCustomers.intValue())*100;
		supSpRatio = (subSpGreaterThanThree.divide(revenueCustomers, 2, RoundingMode.CEILING)).doubleValue();
		}
		//double supSpRatio = (subSpAboveThree.divide(revCustomers)).doubleValue();
		geoIouDashboardDTO.setAccountsWithSpPenetrationAboveThreeAchieved(supSpRatio*100);
		geoIouDashboardList.add(geoIouDashboardDTO);
		}
		bdmPerfromanceGeoIouDashboardResponse.setGeoOrIouHeadAchieved(geoIouDashboardList);
    	return bdmPerfromanceGeoIouDashboardResponse;
	}


	private BigDecimal constructQueryForGeoIouPerformanceDashboardWinsPrivileges(
			String userId, Date fromDate, Date toDate) throws Exception {
		BigDecimal winsSum=new BigDecimal(0);
		logger.debug("Inside constructQueryForGeoIouPerformanceDashboardWinsPrivileges() method");
		String queryString = getPerformanceDashboardWinsQueryString(userId, fromDate, toDate, true, true, false, false);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query performanceDashboardWinsQuery = entityManager.createNativeQuery(queryString);
		List<BigDecimal> resultList = performanceDashboardWinsQuery.getResultList();
		if(resultList.get(0)!=null){
			winsSum=resultList.get(0);
		}
		return winsSum;
	}
	
	private List<Object[]> constructQueryForOpportunityWinsBasedOnPrivileges(String userId, Date fromDate, Date toDate) throws Exception {
		List<Object[]> oppWinsList = null;
		logger.debug("Inside constructQueryForOpportunityWinsBasedOnPrivileges() method");
		String queryString = getPerformanceDashboardWinsQueryString(userId, fromDate, toDate, false, true ,false, false);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query performanceDashboardWinsQuery = entityManager.createNativeQuery(queryString);
		oppWinsList = performanceDashboardWinsQuery.getResultList();
		return oppWinsList;
	}
	
	private List<Object[]> constructQueryForReimaginationDealsBasedOnPrivileges(String userId, Date fromDate, Date toDate) throws Exception {
		List<Object[]> oppWinsList = null;
		logger.debug("Inside constructQueryForOpportunityWinsBasedOnPrivileges() method");
		String queryString = getPerformanceDashboardWinsQueryString(userId, fromDate, toDate, false, false ,true,false);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query performanceDashboardWinsQuery = entityManager.createNativeQuery(queryString);
		oppWinsList = performanceDashboardWinsQuery.getResultList();
		return oppWinsList;
	}
	
	private BigDecimal constructQueryForGeoIouPerformanceDashboardLossesPrivileges(
			String userId, Date fromDate, Date toDate) throws Exception {
		BigDecimal lossSum=new BigDecimal(0);
		logger.debug("Inside constructQueryForGeoIouPerformanceDashboardWinsPrivileges() method");
		String queryString = getPerformanceDashboardWinsQueryString(userId, fromDate, toDate, true, true, false, true);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query performanceDashboardWinsQuery = entityManager.createNativeQuery(queryString);
		List<BigDecimal> resultList = performanceDashboardWinsQuery.getResultList();
		if(resultList.get(0)!=null){
			lossSum=resultList.get(0);
		}
		return lossSum;
	}
	
	private List<Object[]> constructQueryForGeoIouPerformanceDashboardPipelineFunnelPrivileges(
			String userId, Date fromDate, Date toDate, int salesStageCode) throws Exception {
		BigDecimal lossSum=new BigDecimal(0);
		logger.debug("Inside constructQueryForGeoIouPerformanceDashboardWinsPrivileges() method");
		String queryString = getPerformanceDashboardPipelinFunnelQueryString(userId, fromDate, toDate, salesStageCode);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query performanceDashboardWinsQuery = entityManager.createNativeQuery(queryString);
		List<Object[]> resultList = performanceDashboardWinsQuery.getResultList();
		return resultList;
	}
	
	private BigInteger constructQueryForGeoIouPerformanceDashboardSubSpPenetrationPrivileges(
			String userId, List<String> formattedMonths, boolean isSpPenetrationAboveThree) throws Exception {
		BigDecimal lossSum=new BigDecimal(0);
		logger.debug("Inside constructQueryForGeoIouPerformanceDashboardWinsPrivileges() method");
		String queryString = getPerformanceDashboardSubSpPenetrationQueryString(userId, formattedMonths, isSpPenetrationAboveThree);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query performanceDashboardWinsQuery = entityManager.createNativeQuery(queryString);
		List<BigInteger> resultList = performanceDashboardWinsQuery.getResultList();
		return resultList.get(0);
	}
	
	private String getPerformanceDashboardWinsQueryString(String userId, Date fromDate, Date toDate, boolean isWinsSum, 
			boolean isSalesStageCode, boolean isDigitalFlag, boolean isLosses) throws Exception {
		logger.debug("Inside getPerformanceDashboardWinsQueryString() method");
		
		int count=9;
		if (isLosses) {
			count = 10;
		}
		
		StringBuffer queryBuffer = new StringBuffer();
		if(isWinsSum){
		queryBuffer.append(OPP_WINS_QUERY_PART1);
		}
		queryBuffer.append(OPP_WINS_QUERY_PART2);
		queryBuffer.append(DEAL_VALUE_AND_JOINS);
		
		if(isSalesStageCode){
		queryBuffer.append(SALES_STAGE_CODE+count);
		}
		if(isDigitalFlag){
		queryBuffer.append(DIGITAL_FLAG);
		}
		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX, IOU_COND_PREFIX, null);
		// Get WHERE clause string
		queryBuffer.append(DEAL_CLOSURE_DATE_BETWEEN +fromDate+Constants.SINGLE_QUOTE+" " +Constants.AND_CLAUSE+" "+Constants.SINGLE_QUOTE+toDate+Constants.SINGLE_QUOTE);
		
		String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(OPP_WINS_GROUP_BY_PART1);
		if(isWinsSum){
		queryBuffer.append(OPP_WINS_GROUP_BY_PART2);
		}
		return queryBuffer.toString();
	}
	
	
	private String getPerformanceDashboardPipelinFunnelQueryString(String userId, Date fromDate, Date toDate, int salesStageCode) throws Exception {
		logger.debug("Inside getPerformanceDashboardPipelinFunnelQueryString() method");
		
		StringBuffer queryBuffer = new StringBuffer(PIPELINE_FUNNEL_QUERY_PART);
		queryBuffer.append(DEAL_VALUE_AND_JOINS);
		queryBuffer.append(SALES_STAGE_CODE+salesStageCode);

		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX, IOU_COND_PREFIX, null);
		// Get WHERE clause string
		queryBuffer.append(DEAL_CLOSURE_DATE_BETWEEN +fromDate+Constants.SINGLE_QUOTE+" " +Constants.AND_CLAUSE+" "+Constants.SINGLE_QUOTE+toDate+Constants.SINGLE_QUOTE);
		
		String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
		
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(GROUP_BY_SALES_STAGE);
		
		return queryBuffer.toString();
	}
	
	
	private String getPerformanceDashboardSubSpPenetrationQueryString(String userId, List<String> formattedMonths, boolean isSubSpAboveThree) throws Exception {
		logger.debug("Inside getPerformanceDashboardPipelinFunnelQueryString() method");
		
		StringBuffer queryBuffer = new StringBuffer(SUP_SP_ABOVE_THREE_QUERY_PART);
		String formattedMonthsList = getStringListWithSingleQuotes(formattedMonths);
		queryBuffer.append(MONTHS+formattedMonthsList +Constants.RIGHT_PARANTHESIS);
		
		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder.getQueryPrefixMap(RCMT_GEO_COND_PREFIX, SUBSP_COND_PREFIX, IOU_COND_PREFIX, CUSTOMER_NAME);
		// Get WHERE clause string
		
		String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
		
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(GROUP_BY_FINANCE_CUSTOMER);
		if(isSubSpAboveThree){
		queryBuffer.append(COUNT_GREATER_THAN_THREE);
		}
		if(!isSubSpAboveThree){
		queryBuffer.append(AS_SUP_SP_PENETRATION);
			}
		
		return queryBuffer.toString();
	}
	
	public void setTargetValuesForGeoHeadsOrIouSpocsDashboard(String userId, String financialYear,
			BDMPerfromanceGeoIouDashboardResponse bdmPerfromanceGeoIouDashboardResponse, boolean isIndividualSalesStage) throws Exception {
		
		BigDecimal oppWinsTarget = bdmTargetRepository.findBDMOrSupervisorTargetGoalsByUserIdAndYear(userId, "G4", financialYear);
		
		if(oppWinsTarget!=null){
			bdmPerfromanceGeoIouDashboardResponse.setWinsTarget(oppWinsTarget);
		} else {
			BigDecimal oppRefWins = bdmTargetRepository.findBDMOrSupervisorRefTargetGoalsByYear("G4", financialYear);
			bdmPerfromanceGeoIouDashboardResponse.setWinsTarget(oppRefWins);
		}
		
		BigDecimal pipelineTarget = bdmTargetRepository.findBDMOrSupervisorTargetGoalsByUserIdAndYear(userId, "G5", financialYear);
		
		if(pipelineTarget!=null){
			bdmPerfromanceGeoIouDashboardResponse.setPipelineFunnelTarget(pipelineTarget);
		} else {
			BigDecimal pipelineRefTarget = bdmTargetRepository.findBDMOrSupervisorRefTargetGoalsByYear("G5", financialYear);
			bdmPerfromanceGeoIouDashboardResponse.setPipelineFunnelTarget(pipelineRefTarget);
		}
		
		BigDecimal dealsAboveTwentyMillionTarget = bdmTargetRepository.findBDMOrSupervisorTargetGoalsByUserIdAndYear(userId, "G6", financialYear);
		
		if(dealsAboveTwentyMillionTarget!=null){
			bdmPerfromanceGeoIouDashboardResponse.setDealsAboveTwentyMillionTarget(dealsAboveTwentyMillionTarget.intValue());
		} else {
			BigDecimal dealsAboveTwentyMillionRefTarget = bdmTargetRepository.findBDMOrSupervisorRefTargetGoalsByYear("G6", financialYear);
			bdmPerfromanceGeoIouDashboardResponse.setDealsAboveTwentyMillionTarget(dealsAboveTwentyMillionRefTarget.intValue());
		}
		
		BigDecimal dealsAboveTenMillionTarget = bdmTargetRepository.findBDMOrSupervisorTargetGoalsByUserIdAndYear(userId, "G7", financialYear);
		
		if(dealsAboveTenMillionTarget!=null){
			bdmPerfromanceGeoIouDashboardResponse.setDealsAboveTenMillionTarget(dealsAboveTenMillionTarget.intValue());
		} else {
			BigDecimal dealsAboveTenMillionRefTarget = bdmTargetRepository.findBDMOrSupervisorRefTargetGoalsByYear("G7", financialYear);
			bdmPerfromanceGeoIouDashboardResponse.setDealsAboveTenMillionTarget(dealsAboveTenMillionRefTarget.intValue());
		}
		
		BigDecimal digitalReimaginationDealsTarget = bdmTargetRepository.findBDMOrSupervisorTargetGoalsByUserIdAndYear(userId, "G8", financialYear);
		
		if(digitalReimaginationDealsTarget!=null){
			bdmPerfromanceGeoIouDashboardResponse.setDigitalReimaginationDealsTarget(digitalReimaginationDealsTarget.intValue());
		} else {
			BigDecimal digitalReimaginationDealsRefTarget = bdmTargetRepository.findBDMOrSupervisorRefTargetGoalsByYear("G8", financialYear);
			bdmPerfromanceGeoIouDashboardResponse.setDigitalReimaginationDealsTarget(digitalReimaginationDealsRefTarget.intValue());
		}
		
		BigDecimal overAllWinsRatioTarget = bdmTargetRepository.findBDMOrSupervisorTargetGoalsByUserIdAndYear(userId, "G9", financialYear);
		
		if(overAllWinsRatioTarget!=null){
			bdmPerfromanceGeoIouDashboardResponse.setOverAllWinRatioTarget(overAllWinsRatioTarget.doubleValue());
		} else {
			BigDecimal overAllWinsRatioRefTarget = bdmTargetRepository.findBDMOrSupervisorRefTargetGoalsByYear("G9", financialYear);
			bdmPerfromanceGeoIouDashboardResponse.setOverAllWinRatioTarget(overAllWinsRatioRefTarget.doubleValue());
		}
		
		BigDecimal subSpPenetrationAboveThreeTarget = bdmTargetRepository.findBDMOrSupervisorTargetGoalsByUserIdAndYear(userId, "G10", financialYear);
		
		if(subSpPenetrationAboveThreeTarget!=null){
			bdmPerfromanceGeoIouDashboardResponse.setAccountsWithSpPenetrationAboveThreeTarget(subSpPenetrationAboveThreeTarget.doubleValue());
		} else {
			BigDecimal subSpPenetrationAboveThreeRefTarget = bdmTargetRepository.findBDMOrSupervisorRefTargetGoalsByYear("G10", financialYear);
			bdmPerfromanceGeoIouDashboardResponse.setAccountsWithSpPenetrationAboveThreeTarget(subSpPenetrationAboveThreeRefTarget.doubleValue());
		}
		
		Date fromDate = DateUtils.getDateFromFinancialYear(financialYear, true);
		Date toDate = DateUtils.getDateFromFinancialYear(financialYear, false);
		List<Integer> salesStageCodes = new ArrayList<Integer>();
		salesStageCodes.add(4); 	salesStageCodes.add(5);
		salesStageCodes.add(6);		salesStageCodes.add(7); salesStageCodes.add(8);
		List<PipelineDTO> pipelineDTOList = new ArrayList<PipelineDTO>();
		BigDecimal pipelineFunnelSum = new BigDecimal(0);
		PipelineDTO pipelineFunnelDTO = new PipelineDTO();
		for(Integer salesStage:salesStageCodes){
		List<Object[]> pipelineFunnelDealValues = constructQueryForGeoIouPerformanceDashboardPipelineFunnelPrivileges(userId, fromDate, toDate, salesStage);
		
		String salesCode = null;
		
		BigDecimal pipelineAchieved = new BigDecimal(0);
			
			PipelineDTO pipelineDTO = new PipelineDTO();
			
				if(!pipelineFunnelDealValues.isEmpty()){
					salesCode = pipelineFunnelDealValues.get(0)[0].toString();
					pipelineAchieved = (BigDecimal) pipelineFunnelDealValues.get(0)[1];
					pipelineFunnelSum = pipelineFunnelSum.add(pipelineAchieved);
				} else {
					salesCode = salesStage+"";
				}
				if(!isIndividualSalesStage){
				pipelineDTO.setSalesStageCode(salesCode);
				pipelineDTO.setAchieved(pipelineAchieved);
				pipelineDTOList.add(pipelineDTO);
				}
		}
		if(isIndividualSalesStage){
		pipelineFunnelDTO.setSalesStageCode("4,5,6,7,8");
		pipelineFunnelDTO.setAchieved(pipelineFunnelSum);
		pipelineDTOList.add(pipelineFunnelDTO);
		}
		bdmPerfromanceGeoIouDashboardResponse.setPipelineFunnelAchieved(pipelineDTOList);
	}
	
	public void setBDMOrSupervisorDashboardTarget(String userId,
			String financialYear, DashBoardBDMResponse dashBoardBDMResponse) {
		BigDecimal oppWinsTarget=bdmTargetRepository.findBDMOrSupervisorTargetGoalsByUserIdAndYear(userId, "G1", financialYear);
		if(oppWinsTarget!=null){
			dashBoardBDMResponse.setWinsTarget(oppWinsTarget);
		} else {
			BigDecimal oppRefWins =bdmTargetRepository.findBDMOrSupervisorRefTargetGoalsByYear("G1", financialYear);
			dashBoardBDMResponse.setWinsTarget(oppRefWins);
		}
		
		BigDecimal proposalsTarget=bdmTargetRepository.findBDMOrSupervisorTargetGoalsByUserIdAndYear(userId, "G2", financialYear);
		if(proposalsTarget!=null){
			dashBoardBDMResponse.setProposalSupportedTarget(proposalsTarget.intValue());
		} else {
			BigDecimal proposalsRefTarget =bdmTargetRepository.findBDMOrSupervisorRefTargetGoalsByYear("G2", financialYear);
			dashBoardBDMResponse.setProposalSupportedTarget(proposalsRefTarget.intValue());
		}
		
		BigDecimal connectsTarget=bdmTargetRepository.findBDMOrSupervisorTargetGoalsByUserIdAndYear(userId, "G3", financialYear);
		if(connectsTarget!=null){
			dashBoardBDMResponse.setConnectSupportedTarget(connectsTarget.intValue());
		} else {
			BigDecimal connectsRefTarget =bdmTargetRepository.findBDMOrSupervisorRefTargetGoalsByYear("G3", financialYear);
			dashBoardBDMResponse.setConnectSupportedTarget(connectsRefTarget.intValue());
		}
	}
	
	public String getStringListWithSingleQuotes(List<String> formattedList) {
		String appendedString = Joiner.on("\',\'").join(formattedList);
		if (!formattedList.isEmpty()) {
			appendedString = "\'" + appendedString + "\'";
		}
		return appendedString;
	}
}
