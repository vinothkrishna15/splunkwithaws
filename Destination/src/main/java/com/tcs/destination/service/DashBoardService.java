package com.tcs.destination.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.LeadershipConnectsDTO;
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
	
	private static final String TEAM_CONNECTS_GEO_COND_PREFIX = "RCMT.customer_geography in (";
	    
	    private static final String TEAM_CONNECTS_IOU_COND_PREFIX = "ICMT.display_iou in (";
	    
	    private static final String TEAM_CONNECTS_SUBSP_COND_PREFIX = "SSMT.display_sub_sp in (";
	    
	    private static final String TEAM_CONNECTS_CUSTOMER_COND_PREFIX = "RCMT.customer_name in (";

	    private static final String TEAM_CONNECTS_QUERY_PART1 = "SELECT DISTINCT c2.connect_id FROM connect_t c2 JOIN geography_country_mapping_t GCMT ON GCMT.country=c2.country JOIN geography_mapping_t GMT ON GCMT.geography=GMT.geography JOIN customer_master_t CMT ON CMT.geography=GMT.geography JOIN iou_customer_mapping_t ICMT ON CMT.iou=ICMT.iou JOIN revenue_customer_mapping_t RCMT ON GMT.geography=RCMT.customer_geography JOIN connect_sub_sp_link_t CSL ON c2.connect_id=CSL.connect_id JOIN sub_sp_mapping_t SSMT ON CSL.sub_sp=SSMT.sub_sp WHERE (((c2.connect_id IN ((SELECT c1.connect_id FROM Connect_T c1 WHERE c1.primary_owner IN (WITH RECURSIVE U1 AS (SELECT * FROM user_t WHERE supervisor_user_id = '";
	    
	    private static final String TEAM_CONNECTS_QUERY_PART2 = "' UNION ALL SELECT U2.* FROM user_t U2 JOIN U1 ON U2.supervisor_user_id = U1.user_id) SELECT U1.user_id FROM U1 ORDER BY U1.user_id asc)) UNION (SELECT c.connect_id FROM Connect_T c, connect_secondary_owner_link_T cs WHERE (c.connect_id=cs.connect_id) AND (cs.secondary_owner IN (WITH RECURSIVE U1 AS (SELECT * FROM user_t WHERE supervisor_user_id = '";

	    private static final String TEAM_CONNECTS_QUERY_PART3 = "' UNION ALL SELECT U2.* FROM user_t U2 JOIN U1 ON U2.supervisor_user_id = U1.user_id) SELECT U1.user_id FROM U1 ORDER BY U1.user_id asc)))))) AND (c2.start_datetime_of_connect between '";

	    private static final String TEAM_CONNECTS_QUERY_PART4 = "' AND '";

	    private static final String TEAM_CONNECTS_QUERY_PART5 = "' )) AND (GMT.display_geography='";

	    private static final String TEAM_CONNECTS_QUERY_PART6 = "' OR '";

	    private static final String TEAM_CONNECTS_QUERY_PART7 = "' = '')";

	    private static final String TEAM_CONNECTS_SUFFIX = " ORDER By c2.location";

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
	    public LeadershipConnectsDTO getTeamConnectsByGeography(String userId,
		    Date fromDate, Date toDate, String geography) throws Exception {
		logger.debug("Inside getTeamConnectsByGeography()");

		LeadershipConnectsDTO leadershipConnectsDTO = null;
		UserT user = userService.findByUserId(userId);

		if (user != null) {

		    String userGroup = user.getUserGroupMappingT().getUserGroup();
		    System.out.println("userGroup : " + userGroup);

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
			    // Validate financial year and set default value
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
		Timestamp nowTs = new Timestamp(new Date().getTime()); // Get the current timestamp

		// Construct the Query for Past Connects 
		StringBuffer queryBufferForPastConnects = new StringBuffer();
		queryBufferForPastConnects.append(constructQueryForLeadershipDashboard(
			supervisorId, geography, fromDateTs, nowTs));

		// Get the privileges for the user and append to the query constructed above
		privilegesQuery = constructPrivilegesQueryForLeadershipDashboard(supervisorId);
		queryBufferForPastConnects.append(privilegesQuery);
		
		// Get the connects using the constructed query
		List<ConnectT> listOfPastConnects = getConnectsFromQueryBuffer(queryBufferForPastConnects);

		System.out.println("*************************");

		System.out.println(queryBufferForPastConnects);

		if (listOfPastConnects != null) {
		    for (ConnectT res : listOfPastConnects) {
			System.out.println(res.getConnectId() + " "
				+ res.getConnectName());
		    }
		}

		System.out.println("*************************");

		// Construct the Query for Upcoming Connects 
		StringBuffer queryBufferForUpcomingConnects = new StringBuffer();
		queryBufferForUpcomingConnects
			.append(constructQueryForLeadershipDashboard(supervisorId,
				geography, nowTs, toDateTs));

		// Append privileges obtained above
		queryBufferForUpcomingConnects.append(privilegesQuery);
		
		// Get the Connects using the constructed query 
		List<ConnectT> listOfUpcomingConnects = getConnectsFromQueryBuffer(queryBufferForUpcomingConnects);
		System.out.println();
		System.out.println();

		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

		System.out.println(queryBufferForUpcomingConnects.toString());

		if (listOfUpcomingConnects != null) {
		    for (ConnectT res : listOfUpcomingConnects) {
			System.out.println(res.getConnectId() + " "
				+ res.getConnectName());
		    }
		}

		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

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
		    }
		    if (listOfUpcomingConnects != null) {
			leadershipConnectsDTO
				.setUpcomingConnects(listOfUpcomingConnects);
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
	    private List<ConnectT> getConnectsFromQueryBuffer(StringBuffer queryBuffer) throws Exception{
		List<String> resultList = null;
		List<ConnectT> listOfConnects = null;

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
		System.out.println("whereClause " + whereClause);
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
	    private StringBuffer constructQueryForLeadershipDashboard(
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

}