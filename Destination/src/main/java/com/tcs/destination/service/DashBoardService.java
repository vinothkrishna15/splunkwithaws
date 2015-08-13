package com.tcs.destination.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.LeadershipAllOpportunityDTO;
import com.tcs.destination.bean.LeadershipConnectsDTO;
import com.tcs.destination.bean.LeadershipOpportunitiesDTO;
import com.tcs.destination.bean.LeadershipOpportunityBySalesStageCodeDTO;
import com.tcs.destination.bean.LeadershipOverallWinsDTO;
import com.tcs.destination.bean.LeadershipWinsDTO;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.PerformaceChartBean;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.BdmTargetTRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.UserAccessPrivilegeQueryBuilder;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;

import static com.tcs.destination.utils.LeadershipQueryConstants.*;

@Service
public class DashBoardService {

	private static final Logger logger = LoggerFactory
			.getLogger(DashBoardService.class);

	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	BdmTargetTRepository bdmTargetRepository;

	@Autowired
	BeaconConverterService beaconService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserService userService;

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	UserAccessPrivilegeQueryBuilder userAccessPrivilegeQueryBuilder;
	
	@Autowired
	BeaconConverterService beaconConverterService;
	    
	public PerformaceChartBean getChartValues(String userId,
			String financialYear) throws Exception {

		boolean hasValues = false;

		PerformaceChartBean performanceBean = new PerformaceChartBean();

		financialYear = financialYear.equals("") ? DateUtils
				.getCurrentFinancialYear() : financialYear;
		List<BigDecimal> targetList = bdmTargetRepository
				.findSumOfTargetByBdmTargetIdAndYear(userId, financialYear);
		if (targetList != null && !targetList.isEmpty()) {
			performanceBean.setTarget(targetList.get(0));
			if (targetList.get(0) != null)
				hasValues = true;
		}
		String year = financialYear.substring(3, 7);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(year));
		cal.set(Calendar.MONTH, Calendar.APRIL);
		cal.set(Calendar.DATE, 1);
		Date fromDate = new Date(cal.getTimeInMillis());
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
		Date toDate = new Date(cal.getTimeInMillis());

		List<Object[]> pipelineList = opportunityRepository
				.findDealValueForPipeline(userId,
						new Timestamp(toDate.getTime()));

		BigDecimal pipelineSum = new BigDecimal(0);

		for (Object[] pipeline : pipelineList) {
			if (pipeline[1] != null && pipeline[0] != null) {
				pipelineSum = pipelineSum.add(beaconService.convert(
						pipeline[1].toString(), "USD",
						((Integer) pipeline[0]).doubleValue()));
				hasValues = true;
			}

		}
		performanceBean.setPipelineSum(pipelineSum);

		List<Object[]> winList = opportunityRepository.findDealValueForWins(
				userId, fromDate, toDate);
		BigDecimal winSum = new BigDecimal(0);
		for (Object[] win : winList) {
			if (win[1] != null && win[0] != null)
				winSum = winSum.add(beaconService.convert(win[1].toString(),
						"USD", ((Integer) win[0]).doubleValue()));
			hasValues = true;
		}
		performanceBean.setWinSum(winSum);

		if (!hasValues) {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Not Data found for the performance Chart");
		}
		return performanceBean;
	}
	
	/**
	 * This service returns the target, wins and pipeline 
	 * amount values of all subordinates under a supervisor  
	 * 
	 * @param supervisorId
	 * @param financialYear
	 * @return
	 * @throws Exception
	 */
	public PerformaceChartBean getTeamChartValues(String supervisorId,
			String financialYear) throws Exception {

		logger.debug("Inside getTeamChartValues() service");
		
		boolean hasValues = false;

		PerformaceChartBean performanceBean = null;

		// Get all users under a supervisor
		List<String> users = userRepository
				.getAllSubordinatesIdBySupervisorId(supervisorId);

		if ((users != null) && (users.size() > 0)) {

			performanceBean = new PerformaceChartBean();

			// Get the financial year if parameter is empty
			financialYear = financialYear.equals("") ? DateUtils
					.getCurrentFinancialYear() : financialYear;

			// Get the sum of targets
			List<BigDecimal> targetList = bdmTargetRepository
					.findSumOfTargetBySubordinatesPerSupervisorAndYear(users,
							financialYear);
			if (targetList != null && !targetList.isEmpty()) {
				performanceBean.setTarget(targetList.get(0));
				if (targetList.get(0) != null)
					hasValues = true;
			}

			// Manipulate fromDate and toDate
			String year = financialYear.substring(3, 7);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(year));
			cal.set(Calendar.MONTH, Calendar.APRIL);
			cal.set(Calendar.DATE, 1);
			Date fromDate = new Date(cal.getTimeInMillis());
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
			Date toDate = new Date(cal.getTimeInMillis());

			// Get the opportunities which are in the pipeline and find the sum
			// in USD
			List<Object[]> pipelineList = opportunityRepository
					.findDealValueForPipelineBySubordinatesPerSupervisor(users,
							new Timestamp(toDate.getTime()));

			BigDecimal pipelineSum = new BigDecimal(0);

			for (Object[] pipeline : pipelineList) {
				if (pipeline[1] != null && pipeline[0] != null) {
					pipelineSum = pipelineSum.add(beaconService.convert(
							pipeline[1].toString(), "USD",
							((Integer) pipeline[0]).doubleValue()));
					hasValues = true;
				}

			}
			performanceBean.setPipelineSum(pipelineSum);

			// Get the opportunities which have been won and find the sum in USD
			List<Object[]> winList = opportunityRepository
					.findDealValueForWinsBySubordinatesPerSupervisor(users,
							fromDate, toDate);
			BigDecimal winSum = new BigDecimal(0);
			for (Object[] win : winList) {
				if (win[1] != null && win[0] != null)
					winSum = winSum.add(beaconService.convert(
							win[1].toString(), "USD",
							((Integer) win[0]).doubleValue()));
				hasValues = true;
			}

			performanceBean.setWinSum(winSum);
			
			if (!hasValues) {
				logger.error("NOT FOUND : No Data found for Team Performance Chart with supervisor Id {}"+supervisorId);
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Data found for Team Performance Chart with supervisor Id "+supervisorId);
			}
		} else {
			logger.error(
					"NOT_FOUND: No subordinate found for supervisor id : {}",
					supervisorId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No subordinate found for supervisor id " + supervisorId);
		}
		
		return performanceBean;
	}
	
	/**
	     * This service gets the Team Connects of SI/Geo Heads/IOU Heads based on geography
	     * 
	     * @param userId
	     * @param fromDate
	     * @param toDate
	     * @param geography
	     * @return
	     * @throws Exception
	     */
	    public LeadershipConnectsDTO getLeadershipConnectsByGeography(String userId,
		    Date fromDate, Date toDate, String geography) throws Exception {
		logger.debug("Inside getLeadershipConnectsByGeography()");

		LeadershipConnectsDTO leadershipConnectsDTO = null;
		UserT user = userService.findByUserId(userId);

		if (user != null) {

		    String userGroup = user.getUserGroupMappingT().getUserGroup();

		    if (UserGroup.contains(userGroup)) {
			// Validate user group, BDM's & BDM supervisor's are not
			// authorized for this service
			switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
			case BDM:
			case BDM_SUPERVISOR:
			    logger.error("User is not authorized to access this service");
			    throw new DestinationException(HttpStatus.UNAUTHORIZED,
				    "User is not authorised to access this service");
			default:
			    leadershipConnectsDTO = getTeamConnectsBasedOnUserPrivileges(
				    userId, fromDate, toDate, geography);
			}
		    }

		} else {
		    logger.error("NOT_FOUND: User not found: {}", userId);
		    throw new DestinationException(HttpStatus.NOT_FOUND,
			    "User not found: " + userId);
		}

		return leadershipConnectsDTO;

	    }

	    /**
	     * This method helps in retrieving the past and upcoming connects by 
	     * generating a query dynamically using privileges of the users
	     * 
	     * @param supervisorId
	     * @param startDate
	     * @param endDate
	     * @param geography
	     * @return
	     * @throws Exception
	     */
	    private LeadershipConnectsDTO getTeamConnectsBasedOnUserPrivileges(
		    String supervisorId, Date startDate, Date endDate, String geography)
		    throws Exception {

		String privilegesQuery = "";
		LeadershipConnectsDTO leadershipConnectsDTO = null;
		
		Timestamp fromDateTs = new Timestamp(startDate.getTime());
		Timestamp toDateTs = new Timestamp(endDate.getTime()
			+ Constants.ONE_DAY_IN_MILLIS - 1);
		Date now = new Date(); // Get current DateTime
		Timestamp nowTs = new Timestamp(now.getTime()); // Get the current timestamp
		Timestamp nowNextMsTs = new Timestamp(now.getTime()+1); // Get the next millisecond's timestamp w.r.t now
		
		// Construct the Query for Past Connects 
		StringBuffer queryBufferForPastConnects = new StringBuffer();
		queryBufferForPastConnects.append(constructQueryForLeadershipDashboardTeamConnects(
			supervisorId, geography, fromDateTs, nowTs));

		// Get the privileges for the user and append to the query constructed above
		privilegesQuery = constructPrivilegesQueryForLeadershipDashboard(supervisorId);
		queryBufferForPastConnects.append(privilegesQuery);
		
		// Get the connects using the constructed query
		List<ConnectT> listOfPastConnects = getConnectsFromQueryBuffer(queryBufferForPastConnects, supervisorId);

		// Construct the Query for Upcoming Connects 
		StringBuffer queryBufferForUpcomingConnects = new StringBuffer();
		queryBufferForUpcomingConnects
			.append(constructQueryForLeadershipDashboardTeamConnects(supervisorId,
				geography, nowNextMsTs, toDateTs));

		// Append privileges obtained above
		queryBufferForUpcomingConnects.append(privilegesQuery);
				
		// Get the Connects using the constructed query 
		List<ConnectT> listOfUpcomingConnects = getConnectsFromQueryBuffer(queryBufferForUpcomingConnects, supervisorId);
	
		// Throw Exception if both list are null else populate the bean
		if ((listOfPastConnects == null) && (listOfUpcomingConnects == null)) {
		    logger.error("NOT_FOUND: Connects not found for user : {}"
			    + supervisorId);
		    throw new DestinationException(HttpStatus.NOT_FOUND,
			    "Connects not found for user : " + supervisorId);
		} else {
		    leadershipConnectsDTO = new LeadershipConnectsDTO();
		    if (listOfPastConnects != null) {
			leadershipConnectsDTO.setPastConnects(listOfPastConnects);
			leadershipConnectsDTO.setSizeOfPastConnects(listOfPastConnects.size());
		    }
		    if (listOfUpcomingConnects != null) {
			leadershipConnectsDTO
				.setUpcomingConnects(listOfUpcomingConnects);
			leadershipConnectsDTO.setSizeOfUpcomingConnects(listOfUpcomingConnects.size());
		    }
		}

		return leadershipConnectsDTO;
	    }

	    /**
	     * This method returns a list of Connects based on the query string formed
	     * 
	     * @param queryBuffer
	     * @return
	     */
	    private List<ConnectT> getConnectsFromQueryBuffer(StringBuffer queryBuffer, String userId) throws Exception{
		List<String> resultList = null;
		List<ConnectT> listOfConnects = null;

		try {
		// Get the Connect Ids 
		Query teamConnects = entityManager.createNativeQuery(queryBuffer
			.toString());

		// Get list of Connects using the result of the above query
		if ((teamConnects != null) && !(teamConnects.getResultList().isEmpty())) {
		    resultList = teamConnects.getResultList();
		    if ((resultList != null) && !(resultList.isEmpty())) {
			listOfConnects = connectRepository
				.findByConnectIdInOrderByLocationAsc(resultList);
		    }
		}
		} catch(Exception e){
		    logger.error("NOT_FOUND: An Internal Error has occured while processing request for {} : ", userId);
		    throw new DestinationException(HttpStatus.NOT_FOUND,  "An Internal Error has occured while processing request for userId "+userId);
		}
		return listOfConnects;
	    }

	    /**
	     * This method returns the query string for privileges for a user
	     * 
	     * @param supervisorId
	     * @return
	     * @throws Exception
	     */
	    private String constructPrivilegesQueryForLeadershipDashboard(
		    String supervisorId) throws Exception {

		String privilegesQuery = "";

		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
			.getQueryPrefixMap(TEAM_CONNECTS_GEO_COND_PREFIX,
				TEAM_CONNECTS_SUBSP_COND_PREFIX,
				TEAM_CONNECTS_IOU_COND_PREFIX,
				TEAM_CONNECTS_CUSTOMER_COND_PREFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
			.getUserAccessPrivilegeWhereConditionClause(supervisorId,
				queryPrefixMap);

		if (whereClause != null && !whereClause.isEmpty()) {
		    privilegesQuery = Constants.AND_CLAUSE + whereClause;
		}

		return privilegesQuery;
	    }

	    /**
	     * This method appends the query to retrieve the connects sans the privileges.
	     * 
	     * @param supervisorId
	     * @param geography
	     * @param fromDateTs
	     * @param toDateTs
	     * @return
	     */
	    private StringBuffer constructQueryForLeadershipDashboardTeamConnects(
		    String supervisorId, String geography, Timestamp fromDateTs,
		    Timestamp toDateTs) throws Exception{

		StringBuffer queryBuffer = new StringBuffer(TEAM_CONNECTS_QUERY_PART1);

		queryBuffer.append(supervisorId);

		queryBuffer.append(TEAM_CONNECTS_QUERY_PART2);

		queryBuffer.append(supervisorId);

		queryBuffer.append(TEAM_CONNECTS_QUERY_PART3);

		queryBuffer.append(fromDateTs);

		queryBuffer.append(TEAM_CONNECTS_QUERY_PART4);

		queryBuffer.append(toDateTs);

		queryBuffer.append(TEAM_CONNECTS_QUERY_PART5);

		queryBuffer.append(geography);

		queryBuffer.append(TEAM_CONNECTS_QUERY_PART6);

		queryBuffer.append(geography);

		queryBuffer.append(TEAM_CONNECTS_QUERY_PART7);

		return queryBuffer;
	    }
	    
	    /**
	     * This service retrieves the WON opportunities of all users under a supervisor based on his access privileges. 
	     * This module is for Strategic Initiatives/GEO Heads/IOU Heads
	     *  
	     * @param userId
	     * @param fromDate
	     * @param toDate
	     * @param geography
	     * @return LeadershipOverallWinsDTO
	     * @throws Exception
	     */
	    public LeadershipOverallWinsDTO getLeadershipWinsByGeography(String userId,
		    Date fromDate, Date toDate, String geography) throws Exception {
		logger.debug("Inside getLeadershipWinsByGeography()");

		LeadershipOverallWinsDTO leadershipOverallWinsDTO = null;
		UserT user = userService.findByUserId(userId);

		if (user != null) {

		    String userGroup = user.getUserGroupMappingT().getUserGroup();

		    if (UserGroup.contains(userGroup)) {
			// Validate user group, BDM's & BDM supervisor's are not
			// authorized for this service
			switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
			case BDM:
			case BDM_SUPERVISOR:
			    logger.error("User is not authorized to access this service");
			    throw new DestinationException(HttpStatus.UNAUTHORIZED,
				    "User is not authorised to access this service");
			default:
			    leadershipOverallWinsDTO = getLeadershipWinsByUserPrivileges(
				    userId, fromDate, toDate, geography);
			}
		    }

		} else {
		    logger.error("NOT_FOUND: User not found: {}", userId);
		    throw new DestinationException(HttpStatus.NOT_FOUND,
			    "User not found: " + userId);
		}

		return leadershipOverallWinsDTO;

	    }

            /**
             * This method returns the WON opportunities of all users under a supervisor
             * based on his access privileges.
             * 
             * @param userId
             * @param fromDate
             * @param toDate
             * @param geography
             * @return LeadershipOverallWinsDTO
             * @throws Exception
             */
            private LeadershipOverallWinsDTO getLeadershipWinsByUserPrivileges(
        	    String userId, Date fromDate, Date toDate, String geography)
        	    throws Exception {
        
        	String privilegesQuery = "";
        	LeadershipOverallWinsDTO leadershipTotalWinsDTO = null;
        
        	Timestamp fromDateTs = new Timestamp(fromDate.getTime());
        	Timestamp toDateTs = new Timestamp(toDate.getTime()
        		+ Constants.ONE_DAY_IN_MILLIS - 1);
        
        	// Get the privileges for the user and append to the query constructed
        	// above
        	privilegesQuery = constructPrivilegesQueryForLeadershipDashboard(userId);
        	
        
        	// Construct the Query for Wins
        	StringBuffer queryBufferForWins = constructQueryForLeadershipDashboardWinsWithPrivileges(
        		userId, geography, fromDateTs, toDateTs, privilegesQuery);
        	// Get wins using the constructed query
        	LeadershipWinsDTO leadershipWins = getWinsFromQueryBuffer(
        		queryBufferForWins, userId);

        	// Get Wins Greater than 5 Million and 1 Million
        	List<OpportunityT> oppWinsFiveMillion = null;
        	List<OpportunityT> oppWinsOneMillion = null;
        	Double sumOfDigitalDealValueFiveMillion = null;
        	Double sumOfDigitalDealValueOneMillion = null;
        	LeadershipWinsDTO leadershipWinsAboveFiveMillion = null;
        	LeadershipWinsDTO leadershipWinsAboveOneMillion = null;
        
        	if ((leadershipWins != null)
        		&& (!leadershipWins.getListOfWins().isEmpty())) {
        	    oppWinsFiveMillion = new ArrayList<OpportunityT>();
        	    oppWinsOneMillion = new ArrayList<OpportunityT>();
        	    sumOfDigitalDealValueFiveMillion = 0.0;
        	    sumOfDigitalDealValueOneMillion = 0.0;
        
        	    // Loop through the win list and get the digital deal value greater
        	    // than 1M and 5M
        	    for (OpportunityT oppWins : leadershipWins.getListOfWins()) {
        		if ((oppWins.getDealCurrency() != null)
        			&& (oppWins.getDigitalDealValue() != null)) {
        		    // use beaconConverterService service to convert the
        		    // existing value of digital deal value to USD
        		    Double convertedDigitalDealValue = beaconConverterService
        			    .convert(oppWins.getDealCurrency(), Constants.USD,
        				    oppWins.getDigitalDealValue())
        			    .doubleValue();
        
        		    if (convertedDigitalDealValue >= Constants.FIVE_MILLION) {
        			if (leadershipWinsAboveFiveMillion == null) {
        			    leadershipWinsAboveFiveMillion = new LeadershipWinsDTO();
        			}
        			sumOfDigitalDealValueFiveMillion = sumOfDigitalDealValueFiveMillion
        				+ convertedDigitalDealValue;
        			oppWinsFiveMillion.add(oppWins);
        
        		    }
        		    if (convertedDigitalDealValue >= Constants.ONE_MILLION) {
        			if (leadershipWinsAboveOneMillion == null) {
        			    leadershipWinsAboveOneMillion = new LeadershipWinsDTO();
        			}
        			sumOfDigitalDealValueOneMillion = sumOfDigitalDealValueOneMillion
        				+ convertedDigitalDealValue;
        			oppWinsOneMillion.add(oppWins);
        
        		    }
        		}
        	    }
        	    // Populate the bean if not empty
        	    if ((leadershipWinsAboveFiveMillion != null)
        		    && (!oppWinsFiveMillion.isEmpty())) {
        		leadershipWinsAboveFiveMillion
        			.setListOfWins(oppWinsFiveMillion);
        		leadershipWinsAboveFiveMillion.setSizeOfWins(oppWinsFiveMillion
        			.size());
        		leadershipWinsAboveFiveMillion
        			.setSumOfdigitalDealValue(Double.parseDouble((new DecimalFormat("##.##").format(sumOfDigitalDealValueFiveMillion))));
        	    }
        	    if ((leadershipWinsAboveOneMillion != null)
        		    && (!oppWinsOneMillion.isEmpty())) {
        		leadershipWinsAboveOneMillion.setListOfWins(oppWinsOneMillion);
        		leadershipWinsAboveOneMillion.setSizeOfWins(oppWinsOneMillion
        			.size());
        		leadershipWinsAboveOneMillion
        			.setSumOfdigitalDealValue(Double.parseDouble((new DecimalFormat("##.##").format(sumOfDigitalDealValueOneMillion))));
        	    }
        	}
        
        	// Throw Exception if both list are null else populate the bean
        	if ((leadershipWins == null)
        		&& (leadershipWinsAboveFiveMillion == null)
        		&& (leadershipWinsAboveOneMillion == null)) {
        	    logger.error("NOT_FOUND: No Opportuniy found for user : {}" + userId);
        	    throw new DestinationException(HttpStatus.NOT_FOUND,
        		    "No Opportuniy found for user : " + userId);
        	} else {
        	    leadershipTotalWinsDTO = new LeadershipOverallWinsDTO();
        	    if (leadershipWins != null) {
        		leadershipTotalWinsDTO.setLeadershipWins(leadershipWins);
        	    }
        	    if (leadershipWinsAboveFiveMillion != null) {
        		leadershipTotalWinsDTO
        			.setLeadershipWinsAboveFiveMillions(leadershipWinsAboveFiveMillion);
        	    }
        	    if (leadershipWinsAboveOneMillion != null) {
        		leadershipTotalWinsDTO
        			.setLeadershipWinsAboveOneMillion(leadershipWinsAboveOneMillion);
        	    }
        	}
        
        	return leadershipTotalWinsDTO;
            }
        
            /**
             * This method constructs the queries dynamically and provides the output
             * 
             * @param userId
             * @param geography
             * @param fromDateTs
             * @param toDateTs
             * @param privileges
             * @param dealValueFilter
             * @return StringBuffer
             * @throws Exception
             */
            private StringBuffer constructQueryForLeadershipDashboardWinsWithPrivileges(
        	    String userId, String geography, Timestamp fromDateTs,
        	    Timestamp toDateTs, String privileges) throws Exception {
        
        	StringBuffer query = new StringBuffer();
        
        	query.append(constructQueryForLeadershipDashboardWins(userId,
        		geography, fromDateTs, toDateTs));
        
        	query.append(privileges);
        
        	return query;
        
            }
        
            /**
             * This method performs operations to retrieve values from the database
             * 
             * @param queryBuffer
             * @return LeadershipWinsDTO
             * @throws Exception
             */
            private LeadershipWinsDTO getWinsFromQueryBuffer(StringBuffer queryBuffer,
        	    String userId) throws Exception {
        
        	List<String> resultList = null;
        	LeadershipWinsDTO leadershipWinsDTO = null;
        	List<OpportunityT> opportunityList = new ArrayList<OpportunityT>();
        	try {
        	    Query teamWins = entityManager.createNativeQuery(queryBuffer
        		    .toString());
        
	    if ((teamWins != null) && !(teamWins.getResultList().isEmpty())) {
		leadershipWinsDTO = new LeadershipWinsDTO();
		resultList = teamWins.getResultList();

		leadershipWinsDTO.setSizeOfWins(resultList.size());
		leadershipWinsDTO.setSumOfdigitalDealValue(Double
			.parseDouble((new DecimalFormat("##.##")
				.format(opportunityRepository
					.findDigitalDealValueByOpportunityIdIn(
						resultList).doubleValue()))));
		opportunityList = opportunityRepository
			.findByOpportunityIdInOrderByCountryAsc(resultList);
		leadershipWinsDTO.setListOfWins(opportunityList);
	    }
        	} catch (Exception e) {
        	    logger.error(
        		    "NOT_FOUND: An Internal Error has occured while processing request for {} : ",
        		    userId);
        	    throw new DestinationException(HttpStatus.NOT_FOUND,
        		    "An Internal Error has occured while processing request for userId "
        			    + userId);
        	}
        
        	return leadershipWinsDTO;
            }
        
            /**
             * This method returns the dynamically generated query
             * 
             * @param userId
             * @param geography
             * @param fromDateTs
             * @param toDateTs
             * @return StringBuffer
             */
            private StringBuffer constructQueryForLeadershipDashboardWins(
        	    String userId, String geography, Timestamp fromDateTs,
        	    Timestamp toDateTs) throws Exception {
        
        	StringBuffer queryBuffer = new StringBuffer(
        		TEAM_OPPORTUNITY_WIN_QUERY_PART1);
        
        	queryBuffer.append(geography);
        	
        	queryBuffer.append(TEAM_OPPORTUNITY_WIN_QUERY_PART1a);
        	
        	queryBuffer.append(geography);
        
        	queryBuffer.append(TEAM_OPPORTUNITY_WIN_QUERY_PART2);
        
        	queryBuffer.append(userId);
        
        	queryBuffer.append(TEAM_OPPORTUNITY_WIN_QUERY_PART3);
        
        	queryBuffer.append(userId);
        
        	queryBuffer.append(TEAM_OPPORTUNITY_WIN_QUERY_PART4);
        
        	queryBuffer.append(userId);
        
        	queryBuffer.append(TEAM_OPPORTUNITY_WIN_QUERY_PART5);
        
        	queryBuffer.append(fromDateTs);
        
        	queryBuffer.append(TEAM_OPPORTUNITY_WIN_QUERY_PART6);
        
        	queryBuffer.append(toDateTs);
        
        	queryBuffer.append(TEAM_OPPORTUNITY_WIN_QUERY_PART7);
        
        	return queryBuffer;
        
            }
        
            /**
             * This service gives the Opportunities under a supervisor with stages such
             * as Prospects, Qualified Pipeline, Won, Lost and shelved
             * 
             * @param userId
             * @param fromDate
             * @param toDate
             * @param geography
             * @return LeadershipOpportunitiesDTO
             * @throws Exception
             */
            public LeadershipOpportunitiesDTO getLeadershipOpportunitiesByGeography(
        	    String userId, Date fromDate, Date toDate, String geography)
        	    throws Exception {
        
        	logger.debug("Inside getLeadershipOpportunitiesByGeography()");
        
        	LeadershipOpportunitiesDTO listOfOppportunities = null;
        	UserT user = userService.findByUserId(userId);
        
        	if (user != null) {
        
        	    String userGroup = user.getUserGroupMappingT().getUserGroup();
        
        	    if (UserGroup.contains(userGroup)) {
        		// Validate user group, BDM's & BDM supervisor's are not
        		// authorized for this service
        		switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
        		case BDM:
        		case BDM_SUPERVISOR:
        		    logger.error("User is not authorized to access this service");
        		    throw new DestinationException(HttpStatus.UNAUTHORIZED,
        			    "User is not authorised to access this service");
        		default:
        		    listOfOppportunities = getLeadershipOpportunitiesByUserPrivileges(
        			    userId, fromDate, toDate, geography);
        		}
        	    }
        
        	} else {
        	    logger.error("NOT_FOUND: User not found: {}", userId);
        	    throw new DestinationException(HttpStatus.NOT_FOUND,
        		    "User not found: " + userId);
        	}
        
        	return listOfOppportunities;
        
            }
        
            /**
             * This method returns the Leadership Opportunities Details
             * 
             * @param userId
             * @param fromDate
             * @param toDate
             * @param geography
             * @return LeadershipOpportunitiesDTO
             * @throws Exception
             */
            private LeadershipOpportunitiesDTO getLeadershipOpportunitiesByUserPrivileges(
        	    String userId, Date fromDate, Date toDate, String geography)
        	    throws Exception {
        
        	LeadershipOpportunitiesDTO listOfOppportunities = null;
        
        	List<LeadershipAllOpportunityDTO> listOfOpportunitiesBySalesCode = null;
        	String privilegesQuery = "";
        
        	Timestamp fromDateTs = new Timestamp(fromDate.getTime());
        	Timestamp toDateTs = new Timestamp(toDate.getTime()
        		+ Constants.ONE_DAY_IN_MILLIS - 1);
        
        	// Get the privileges for the user and append to the query constructed
        	// above
        	privilegesQuery = constructPrivilegesQueryForLeadershipDashboard(userId);
        
        	// Get the query constructed
        	StringBuffer query = constructQueryForLeadershipDashboardOpportunitiesWithPrivileges(
        		userId, geography, fromDateTs, toDateTs, privilegesQuery);
        
        	// Get Opportunity_id, Digital Deal Value and Sales Stage Code for given
        	// supervisorId
        	listOfOpportunitiesBySalesCode = getAllOpportunitiesUsingQuery(query,
        		userId);
        
        	// Get ListOfOpp, sum Of digital deal value based on Sales Stage Code
        	// i.e. 1(Prospecting), 4-8(Qualified Pipeline), 9(won), 10(lost),
        	// 12(shelved)
        	listOfOppportunities = getOpportunitiesBySalesStageCode(
        		listOfOpportunitiesBySalesCode, userId);
        
        	return listOfOppportunities;
        
            }
        
            /**
             * This method provides the opportunities based on sales stage code
             * 
             * @param listOfOpportunitiesBySalesCode
             * @return LeadershipOpportunitiesDTO
             * @throws Exception
             */
            private LeadershipOpportunitiesDTO getOpportunitiesBySalesStageCode(
        	    List<LeadershipAllOpportunityDTO> listOfOpportunitiesBySalesCode,
        	    String userId) throws Exception {
        
        	LeadershipOpportunitiesDTO leadershipOpportunitiesDTO = null;
        	boolean checkOppExists = false;
        	List<String> oppIdProspects = null;
        	List<String> oppIdPipeline = null;
        	List<String> oppIdWon = null;
        	List<String> oppIdLost = null;
        	List<String> oppIdShelved = null;
        	Double sumOfDealValueProspects = new Double(0);
        	Double sumOfDealValuePipeline = new Double(0);
        	Double sumOfDealValueWon = new Double(0);
        	Double sumOfDealValueLost = new Double(0);
        	Double sumOfDealValueShelved = new Double(0);
        
        	if ((listOfOpportunitiesBySalesCode != null)
        		&& (!listOfOpportunitiesBySalesCode.isEmpty())) {
        
        	    oppIdProspects = new ArrayList<String>();
        	    oppIdPipeline = new ArrayList<String>();
        	    oppIdWon = new ArrayList<String>();
        	    oppIdLost = new ArrayList<String>();
        	    oppIdShelved = new ArrayList<String>();
        
        	    leadershipOpportunitiesDTO = new LeadershipOpportunitiesDTO();
        
        	    for (LeadershipAllOpportunityDTO opp : listOfOpportunitiesBySalesCode) {
        		if (opp.getSalesStageCode() != null) {
        		    if (opp.getSalesStageCode() == 1) { // For Prospects
        
        			oppIdProspects.add(opp.getOpportunityId());
        			if (opp.getDigitalDealValue() != null) {
        			    sumOfDealValueProspects = sumOfDealValueProspects
        				    + opp.getDigitalDealValue().doubleValue();
        			}
        		    } else if ((opp.getSalesStageCode() == 4)
        			    || (opp.getSalesStageCode() == 5)
        			    || (opp.getSalesStageCode() == 6)
        			    || (opp.getSalesStageCode() == 7)
        			    || (opp.getSalesStageCode() == 8)) { // For Pipeline
        
        			oppIdPipeline.add(opp.getOpportunityId());
        			if (opp.getDigitalDealValue() != null) {
        			    sumOfDealValuePipeline = sumOfDealValuePipeline
        				    + opp.getDigitalDealValue().doubleValue();
        			}
        		    } else if (opp.getSalesStageCode() == 9) { // For Won
        
        			oppIdWon.add(opp.getOpportunityId());
        			if (opp.getDigitalDealValue() != null) {
        			    sumOfDealValueWon = sumOfDealValueWon
        				    + opp.getDigitalDealValue().doubleValue();
        			}
        		    } else if (opp.getSalesStageCode() == 10) { // For Lost
        
        			oppIdLost.add(opp.getOpportunityId());
        			if (opp.getDigitalDealValue() != null) {
        			    sumOfDealValueLost = sumOfDealValueLost
        				    + opp.getDigitalDealValue().doubleValue();
        			}
        		    } else if (opp.getSalesStageCode() == 12) { // For Shelved
        
        			oppIdShelved.add(opp.getOpportunityId());
        			if (opp.getDigitalDealValue() != null) {
        			    sumOfDealValueShelved = sumOfDealValueShelved
        				    + opp.getDigitalDealValue().doubleValue();
        			}
        		    }
        		}
        	    }
        	    if (!oppIdProspects.isEmpty()) {
        		leadershipOpportunitiesDTO
        			.setOppProspects(getLeadershipOpportunityObjectBySalesStageCode(
        				oppIdProspects, sumOfDealValueProspects, userId));
        		checkOppExists = true;
        	    }
        	    if (!oppIdPipeline.isEmpty()) {
        		leadershipOpportunitiesDTO
        			.setOppPipeline(getLeadershipOpportunityObjectBySalesStageCode(
        				oppIdPipeline, sumOfDealValuePipeline, userId));
        		checkOppExists = true;
        	    }
        	    if (!oppIdWon.isEmpty()) {
        		leadershipOpportunitiesDTO
        			.setOppWon(getLeadershipOpportunityObjectBySalesStageCode(
        				oppIdWon, sumOfDealValueWon, userId));
        		checkOppExists = true;
        	    }
        	    if (!oppIdLost.isEmpty()) {
        		leadershipOpportunitiesDTO
        			.setOppLost(getLeadershipOpportunityObjectBySalesStageCode(
        				oppIdLost, sumOfDealValueLost, userId));
        		checkOppExists = true;
        	    }
        	    if (!oppIdShelved.isEmpty()) {
        		leadershipOpportunitiesDTO
        			.setOppShelved(getLeadershipOpportunityObjectBySalesStageCode(
        				oppIdShelved, sumOfDealValueShelved, userId));
        		checkOppExists = true;
        	    }
        	}
        	if (!checkOppExists) {
        	    logger.error("NOT_FOUND: No Opportunity Found for user Id : {}",
        		    userId);
        	    throw new DestinationException(HttpStatus.NOT_FOUND,
        		    "No Opportunity Found for user Id : " + userId);
        	}
        	return leadershipOpportunitiesDTO;
            }
        
            /**
             * This method returns the opportunity object per sales stage code
             * 
             * @param oppIdForCodeOne
             * @param sumOfDealValueForCodeOne
             * @return LeadershipOpportunityBySalesStageCodeDTO
             * @throws Exception
             */
            private LeadershipOpportunityBySalesStageCodeDTO getLeadershipOpportunityObjectBySalesStageCode(
        	    List<String> oppId, Double sumOfDealValue, String userId)
        	    throws Exception {
        
        	LeadershipOpportunityBySalesStageCodeDTO oppBySalesCode = new LeadershipOpportunityBySalesStageCodeDTO();
        	try {
        	    List<OpportunityT> listOfOpp = opportunityRepository
        		    .findByOpportunityIdInOrderByCountryAsc(oppId);
        	    if (listOfOpp != null) {
        		oppBySalesCode.setOpportunities(listOfOpp);
        		oppBySalesCode.setSizeOfOpportunities(listOfOpp.size());
        	    }
        	    oppBySalesCode.setSumOfDigitalDealValue(Double.parseDouble((new DecimalFormat("##.##").format(sumOfDealValue))));
        	} catch (Exception e) {
        	    logger.error(
        		    "NOT_FOUND: An Internal Error has occured while processing request for {} : ",
        		    userId);
        	    throw new DestinationException(HttpStatus.NOT_FOUND,
        		    "An Internal Error has occured while processing request for userId "
        			    + userId);
        	}
        	return oppBySalesCode;
            }
        
            /**
             * This method helps in retrieving the values from the database and sets the
             * approporate object
             * 
             * @param query
             * @return List<LeadershipAllOpportunityDTO>
             * @throws Exception
             */
            private List<LeadershipAllOpportunityDTO> getAllOpportunitiesUsingQuery(
        	    StringBuffer query, String userId) throws Exception {
        	List<LeadershipAllOpportunityDTO> listOfOpp = null;
        	try {
        	    TypedQuery<Object[]> teamOpportunities = (TypedQuery<Object[]>) entityManager
        		    .createNativeQuery(query.toString());
        
        	    if ((teamOpportunities != null)
        		    && !(teamOpportunities.getResultList().isEmpty())) {
        
        		listOfOpp = new ArrayList<LeadershipAllOpportunityDTO>();
        
        		for (Object[] opportunity : teamOpportunities.getResultList()) {
        		    LeadershipAllOpportunityDTO opp = new LeadershipAllOpportunityDTO();
        		    if (opportunity[2] != null) {
        			opp.setSalesStageCode(((Integer) opportunity[2])
        				.intValue());
        		    }
        		    if (opportunity[1] != null) {
        			opp.setDigitalDealValue(BigDecimal.valueOf(Double
        				.parseDouble(opportunity[1].toString())));
        		    }
        		    if (opportunity[0] != null) {
        			opp.setOpportunityId(opportunity[0].toString());
        		    }
        		    listOfOpp.add(opp);
        		}
        
        	    }
        	} catch (Exception e) {
        	    logger.error(
        		    "NOT_FOUND: An Internal Error has occured while processing request for {} : ",
        		    userId);
        	    throw new DestinationException(HttpStatus.NOT_FOUND,
        		    "An Internal Error has occured while processing request for userId "
        			    + userId);
        	}
        	return listOfOpp;
            }
        
            /**
             * This method helps in generating the query string based on the input
             * provided
             * 
             * @param userId
             * @param geography
             * @param fromDateTs
             * @param toDateTs
             * @param privilegesQueryString
             * @return StringBuffer
             */
            private StringBuffer constructQueryForLeadershipDashboardOpportunitiesWithPrivileges(
        	    String userId, String geography, Timestamp fromDateTs,
        	    Timestamp toDateTs, String privilegesQueryString) {
        
        	StringBuffer queryBuffer = new StringBuffer();
        
        	queryBuffer.append(TEAM_OPPORTUNITY_QUERY_PART1);
        
        	queryBuffer.append(geography);
        	
        	queryBuffer.append(TEAM_OPPORTUNITY_QUERY_PART1a);
        	
        	queryBuffer.append(geography);
        
        	queryBuffer.append(TEAM_OPPORTUNITY_QUERY_PART2);
        
        	queryBuffer.append(userId);
        
        	queryBuffer.append(TEAM_OPPORTUNITY_QUERY_PART3);
        
        	queryBuffer.append(userId);
        
        	queryBuffer.append(TEAM_OPPORTUNITY_QUERY_PART4);
        
        	queryBuffer.append(userId);
        
        	queryBuffer.append(TEAM_OPPORTUNITY_QUERY_PART5);
        
        	queryBuffer.append(fromDateTs);
        
        	queryBuffer.append(TEAM_OPPORTUNITY_QUERY_PART6);
        
        	queryBuffer.append(toDateTs);
        
        	queryBuffer.append(TEAM_OPPORTUNITY_QUERY_PART7);
        
        	queryBuffer.append(privilegesQueryString);
        
        	queryBuffer.append(TEAM_OPPORTUNITY_QUERY_SUFFIX);
        
        	return queryBuffer;
            }
        
}