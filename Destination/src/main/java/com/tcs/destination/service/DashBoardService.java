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
import java.util.Map;

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

/**
 * This service is used to fetch leadership dash board connects,wins and team connects
 *
 */
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
	
	private static Map<String, BigDecimal> beaconConverterMap = null;

	/**
	 * This method is used to get the performance chart values
	 * @param userId
	 * @param financialYear
	 * @return
	 * @throws Exception
	 */
	public PerformaceChartBean getChartValues(String userId,
			String financialYear) throws Exception {

		logger.debug("start: Inside  getChartValues() of DashBoardService");
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
		logger.debug("End: Inside  getChartValues() of DashBoardService");
		return performanceBean;
	}

	/**
	 * This service returns the target, wins and pipeline amount values of all
	 * subordinates under a supervisor
	 * 
	 * @param supervisorId
	 * @param financialYear
	 * @return
	 * @throws Exception
	 */
	public PerformaceChartBean getTeamChartValues(String supervisorId,
			String financialYear) throws Exception {

		logger.debug("Start: Inside getTeamChartValues() service");

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
				logger.error("NOT FOUND : No Data found for Team Performance Chart with supervisor Id {}"
						+ supervisorId);
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Data found for Team Performance Chart with supervisor Id "
								+ supervisorId);
			}
		} else {
			logger.error(
					"NOT_FOUND: No subordinate found for supervisor id : {}",
					supervisorId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No subordinate found for supervisor id " + supervisorId);
		}
		logger.debug("End: Inside  getTeamChartValues() of DashBoardService");
		return performanceBean;
	}

	/**
	 * This service gets the Team Connects of SI/Geo Heads/IOU Heads based on
	 * geography
	 * 
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param geography
	 * @return
	 * @throws Exception
	 */
	public LeadershipConnectsDTO getLeadershipConnectsByGeography(
			String userId, Date fromDate, Date toDate, String geography)
			throws Exception {
		logger.debug("Start:Inside getLeadershipConnectsByGeography() of DashBoardService");

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
		logger.debug("End:Inside getLeadershipConnectsByGeography() of DashBoardService");
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
		
		logger.debug("Start:Inside  getTeamConnectsBasedOnUserPrivileges() of DashBoardService");

		String privilegesQuery = "";
		LeadershipConnectsDTO leadershipConnectsDTO = null;

		Timestamp fromDateTs = new Timestamp(startDate.getTime());
		Timestamp toDateTs = new Timestamp(endDate.getTime()
				+ Constants.ONE_DAY_IN_MILLIS - 1);
		Date now = new Date(); // Get current DateTime
		Timestamp nowTs = new Timestamp(now.getTime()); // Get the current
														// timestamp
		Timestamp nowNextMsTs = new Timestamp(now.getTime() + 1); // Get the
																	// next
																	// millisecond's
																	// timestamp
																	// w.r.t now

		// Get the Past connects
		List<ConnectT> listOfPastConnects = getLeadershipDashboardTeamConnects(
				supervisorId, geography, fromDateTs, nowTs);

		// Get the Future Connects using the constructed query
		List<ConnectT> listOfUpcomingConnects = getLeadershipDashboardTeamConnects(
				supervisorId, geography, nowNextMsTs, toDateTs);

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
				leadershipConnectsDTO.setSizeOfPastConnects(listOfPastConnects
						.size());
			}
			if (listOfUpcomingConnects != null) {
				leadershipConnectsDTO
						.setUpcomingConnects(listOfUpcomingConnects);
				leadershipConnectsDTO
						.setSizeOfUpcomingConnects(listOfUpcomingConnects
								.size());
			}
		}
		logger.debug("End:Inside  getTeamConnectsBasedOnUserPrivileges() of DashBoardService");
		return leadershipConnectsDTO;
	}

//	/**
//	 * This method returns a list of Connects based on the query string formed
//	 * 
//	 * @param queryBuffer
//	 * @return
//	 */
//	private List<ConnectT> getConnectsFromQueryBuffer(StringBuffer queryBuffer,
//			String userId) throws Exception {
//		List<String> resultList = null;
//		List<ConnectT> listOfConnects = null;
//
//		try {
//			// Get the Connect Ids
//			Query teamConnects = entityManager.createNativeQuery(queryBuffer
//					.toString());
//
//			// Get list of Connects using the result of the above query
//			if ((teamConnects != null)
//					&& !(teamConnects.getResultList().isEmpty())) {
//
//				resultList = teamConnects.getResultList();
//
//				if ((resultList != null) && !(resultList.isEmpty())) {
//					listOfConnects = connectRepository
//							.findByConnectIdInOrderByLocationAsc(resultList);
//
//				}
//			}
//		} catch (Exception e) {
//			logger.error(
//					"NOT_FOUND: An Internal Error has occured while processing request for {} : ",
//					userId);
//			throw new DestinationException(HttpStatus.NOT_FOUND,
//					"An Internal Error has occured while processing request for userId "
//							+ userId);
//		}
//		return listOfConnects;
//	}

	/**
	 * This method returns the query string for privileges for a user
	 * 
	 * @param supervisorId
	 * @return
	 * @throws Exception
	 */
	private String constructPrivilegesQueryForLeadershipDashboard(
			String supervisorId) throws Exception {
        
		logger.debug("Start:Inside  constructPrivilegesQueryForLeadershipDashboard() of DashBoardService");
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
		logger.debug("End:Inside  constructPrivilegesQueryForLeadershipDashboard() of DashBoardService");
		return privilegesQuery;
	}

	/**
	 * This method appends the query to retrieve the connects sans the
	 * privileges.
	 * 
	 * @param supervisorId
	 * @param displayGeography
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	private List<ConnectT> getLeadershipDashboardTeamConnects(
			String supervisorId, String displayGeography, Timestamp fromDate,
			Timestamp toDate) throws Exception {

		logger.debug("Start:Inside  getLeadershipDashboardTeamConnects() of DashBoardService");
		StringBuffer queryBuffer = new StringBuffer(TEAM_CONNECTS_QUERY_PART1);

		// Append privileges obtained above. Note that access privilege is only
		// for customers and not for partners
		queryBuffer
				.append(constructPrivilegesQueryForLeadershipDashboard(supervisorId));
        queryBuffer.append(TEAM_CONNECTS_QUERY_PART2);

		Query query = entityManager.createNativeQuery(queryBuffer.toString(),
				ConnectT.class);

		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);
		query.setParameter("displayGeography", displayGeography);
		logger.debug("End:Inside  getLeadershipDashboardTeamConnects() of DashBoardService");
		return query.getResultList();
	  }

	/**
	 * This service retrieves the WON opportunities of all users under a
	 * supervisor based on his access privileges. This module is for Strategic
	 * Initiatives/GEO Heads/IOU Heads
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
		
		logger.debug("Start: Inside getLeadershipWinsByGeography() of DashBoardService");

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
		logger.debug("End: Inside getLeadershipWinsByGeography() of DashBoardService");
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
        
		logger.debug("Start: Inside getLeadershipWinsByUserPrivileges() of DashBoardService");
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

		// Get Wins Greater than 5 Million and 1 Million and upto 1 Million
		List<OpportunityT> oppWinsFiveMillion = null;
		List<OpportunityT> oppWinsOneMillion = null;
		List<OpportunityT> oppWinsUptoOneMillion = null;
		Double sumOfDigitalDealValueFiveMillion = null;
		Double sumOfDigitalDealValueOneMillion = null;
		Double sumOfDigitalDealValueUptoOneMillion = null;
		LeadershipWinsDTO leadershipWinsAboveFiveMillion = null;
		LeadershipWinsDTO leadershipWinsAboveOneMillion = null;
		LeadershipWinsDTO leadershipWinsUptoOneMillion = null;

		if ((leadershipWins != null)
				&& (!leadershipWins.getListOfWins().isEmpty())) {
			oppWinsFiveMillion = new ArrayList<OpportunityT>();
			oppWinsOneMillion = new ArrayList<OpportunityT>();
			oppWinsUptoOneMillion = new ArrayList<OpportunityT>();
			sumOfDigitalDealValueFiveMillion = 0.0;
			sumOfDigitalDealValueOneMillion = 0.0;
			sumOfDigitalDealValueUptoOneMillion = 0.0;

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

					if (convertedDigitalDealValue > Constants.FIVE_MILLION) {
						if (leadershipWinsAboveFiveMillion == null) {
							leadershipWinsAboveFiveMillion = new LeadershipWinsDTO();
						}
						sumOfDigitalDealValueFiveMillion = sumOfDigitalDealValueFiveMillion
								+ convertedDigitalDealValue;
						oppWinsFiveMillion.add(oppWins);

					}
					if (convertedDigitalDealValue > Constants.ONE_MILLION
							&& convertedDigitalDealValue <= Constants.FIVE_MILLION) {
						if (leadershipWinsAboveOneMillion == null) {
							leadershipWinsAboveOneMillion = new LeadershipWinsDTO();
						}
						sumOfDigitalDealValueOneMillion = sumOfDigitalDealValueOneMillion
								+ convertedDigitalDealValue;
						oppWinsOneMillion.add(oppWins);

					}
					if (convertedDigitalDealValue <= Constants.ONE_MILLION) {
						if (leadershipWinsUptoOneMillion == null) {
							leadershipWinsUptoOneMillion = new LeadershipWinsDTO();
						}
						sumOfDigitalDealValueUptoOneMillion = sumOfDigitalDealValueUptoOneMillion
								+ convertedDigitalDealValue;
						oppWinsUptoOneMillion.add(oppWins);

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
				leadershipWinsAboveFiveMillion.setSumOfdigitalDealValue(Double
						.parseDouble((new DecimalFormat("##.##")
								.format(sumOfDigitalDealValueFiveMillion))));
			}
			if ((leadershipWinsAboveOneMillion != null)
					&& (!oppWinsOneMillion.isEmpty())) {
				leadershipWinsAboveOneMillion.setListOfWins(oppWinsOneMillion);
				leadershipWinsAboveOneMillion.setSizeOfWins(oppWinsOneMillion
						.size());
				leadershipWinsAboveOneMillion.setSumOfdigitalDealValue(Double
						.parseDouble((new DecimalFormat("##.##")
								.format(sumOfDigitalDealValueOneMillion))));
			}
			if ((leadershipWinsUptoOneMillion != null)
					&& (!oppWinsUptoOneMillion.isEmpty())) {
				leadershipWinsUptoOneMillion
						.setListOfWins(oppWinsUptoOneMillion);
				leadershipWinsUptoOneMillion
						.setSizeOfWins(oppWinsUptoOneMillion.size());
				leadershipWinsUptoOneMillion.setSumOfdigitalDealValue(Double
						.parseDouble((new DecimalFormat("##.##")
								.format(sumOfDigitalDealValueUptoOneMillion))));
			}
		}

		// Throw Exception if both list are null else populate the bean
		if ((leadershipWins == null)
				&& (leadershipWinsAboveFiveMillion == null)
				&& (leadershipWinsAboveOneMillion == null)
				&& (leadershipWinsUptoOneMillion == null)) {
			logger.error("NOT_FOUND: No Opportunity found for user : {}"
					+ userId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Opportunity found for user : " + userId);
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
			if (leadershipWinsUptoOneMillion != null) {
				leadershipTotalWinsDTO
						.setLeadershipWinsUptoOneMillion(leadershipWinsUptoOneMillion);
			}

		}
		logger.debug("End: Inside getLeadershipWinsByUserPrivileges() of DashBoardService");
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

		logger.debug("Start: Inside constructQueryForLeadershipDashboardWinsWithPrivileges() of DashBoardService");
		StringBuffer query = new StringBuffer();

		query.append(constructQueryForLeadershipDashboardWins(userId,
				geography, fromDateTs, toDateTs));

		query.append(privileges);
		logger.debug("End: Inside constructQueryForLeadershipDashboardWinsWithPrivileges() of DashBoardService");
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

		logger.debug("Start: Inside getWinsFromQueryBuffer() of DashBoardService");
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
		logger.debug("End: Inside getWinsFromQueryBuffer() of DashBoardService");
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

		logger.debug("Start: Inside constructQueryForLeadershipDashboardWins() of DashBoardService");
		StringBuffer queryBuffer = new StringBuffer(
				TEAM_OPPORTUNITY_WIN_QUERY_PART1);

		queryBuffer.append(geography);

		queryBuffer.append(TEAM_OPPORTUNITY_WIN_QUERY_PART1a);

		queryBuffer.append(geography);

		queryBuffer.append(TEAM_OPPORTUNITY_WIN_QUERY_PART2);

        queryBuffer.append(fromDateTs);

	    queryBuffer.append(TEAM_OPPORTUNITY_WIN_QUERY_PART3);

		queryBuffer.append(toDateTs);

		queryBuffer.append(TEAM_OPPORTUNITY_WIN_QUERY_PART4);

		logger.debug("End: Inside constructQueryForLeadershipDashboardWins() of DashBoardService");
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

		logger.debug("Start:Inside getLeadershipOpportunitiesByGeography()");

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
		logger.debug("End: Inside getLeadershipOpportunitiesByGeography() of DashBoardService");
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

		logger.debug("Start: Inside getLeadershipOpportunitiesByUserPrivileges() of DashBoardService");
		LeadershipOpportunitiesDTO listOfOppportunities = null;

		List<OpportunityT> opportunitiesBySalesCode = getPrevilegedOpportunities(
				userId, fromDate, toDate, geography);

		// Get ListOfOpp, sum Of digital deal value based on Sales Stage Code
		// i.e. 0-3(Prospecting), 4-8(Qualified Pipeline), 9(won),
		// 10,11,13(lost),
		// 12(shelved)
		listOfOppportunities = getOpportunitiesBySalesStageCode(
				opportunitiesBySalesCode, userId);

		getPrevilegedOpportunities(
				userId, fromDate, toDate, geography);
		logger.debug("End: Inside getLeadershipOpportunitiesByUserPrivileges() of DashBoardService");
		return listOfOppportunities;

	}

	/**
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param geography
	 * @return
	 * @throws Exception
	 */
	public List<OpportunityT> getPrevilegedOpportunities(String userId,
			Date fromDate, Date toDate, String geography) throws Exception {
		logger.debug("Start: Inside getPrevilegedOpportunities() of DashBoardService");
		List<OpportunityT> opportunitiesBySalesCode = null;
		Timestamp fromDateTs = new Timestamp(fromDate.getTime());
		Timestamp toDateTs = new Timestamp(toDate.getTime()
				+ Constants.ONE_DAY_IN_MILLIS - 1);

		String privilegesQuery = "";
		// Get the privileges for the user and append to the query constructed
		// above
		privilegesQuery = constructPrivilegesQueryForLeadershipDashboard(userId);

		// Get the query constructed
		StringBuffer query = constructQueryForLeadershipDashboardOpportunitiesWithPrivileges(
				userId, geography, fromDateTs, toDateTs, privilegesQuery);

		// Get Opportunity Details for given
		// supervisorId
		opportunitiesBySalesCode = getAllOpportunitiesUsingQuery(query,
				userId);
		logger.debug("End: Inside getPrevilegedOpportunities() of DashBoardService");
		return opportunitiesBySalesCode;
	}

	/**
	 * This method provides the opportunities based on sales stage code
	 * 
	 * @param opportunitiesBySalesCode
	 * @return LeadershipOpportunitiesDTO
	 * @throws Exception
	 */
	private LeadershipOpportunitiesDTO getOpportunitiesBySalesStageCode(
			List<OpportunityT> opportunitiesBySalesCode,
			String userId) throws Exception {
		logger.debug("Start: Inside getOpportunitiesBySalesStageCode() of DashBoardService");
		LeadershipOpportunitiesDTO leadershipOpportunitiesDTO = null;
		boolean checkOppExists = false;
		List<OpportunityT> opportunitiesProspects = null;
		List<OpportunityT> opportunitiesPipeline = null;
		List<OpportunityT> opportunitiesWon = null;
		List<OpportunityT> opportunitiesLost = null;
		List<OpportunityT> opportunitiesShelved = null;
		Double sumOfDealValueProspects = new Double(0);
		Double sumOfDealValuePipeline = new Double(0);
		Double sumOfDealValueWon = new Double(0);
		Double sumOfDealValueLost = new Double(0);
		Double sumOfDealValueShelved = new Double(0);

		if ((opportunitiesBySalesCode != null)
				&& (!opportunitiesBySalesCode.isEmpty())) {
			
			if(beaconConverterMap==null){
	    		beaconConverterMap = beaconConverterService.getCurrencyNameAndRate();
	    	}

			opportunitiesProspects = new ArrayList<OpportunityT>();
			opportunitiesPipeline = new ArrayList<OpportunityT>();
			opportunitiesWon = new ArrayList<OpportunityT>();
			opportunitiesLost = new ArrayList<OpportunityT>();
			opportunitiesShelved = new ArrayList<OpportunityT>();

			leadershipOpportunitiesDTO = new LeadershipOpportunitiesDTO();

			for (OpportunityT opp : opportunitiesBySalesCode) {
					if (opp.getSalesStageCode() < 4) { // For Prospects 0-3
						opportunitiesProspects.add(opp);
						if (opp.getDigitalDealValue() != null) {
							sumOfDealValueProspects = sumOfDealValueProspects +
									beaconConverterService.convertCurrencyRateUsingBeaconConverterMap(opp.getDealCurrency(), "USD", opp.getDigitalDealValue(), beaconConverterMap).doubleValue();
						}
					} else if ((opp.getSalesStageCode() >= 4)
							&& (opp.getSalesStageCode() <= 8)) { // For Pipeline

						opportunitiesPipeline.add(opp);
						if (opp.getDigitalDealValue() != null) {
							sumOfDealValuePipeline = sumOfDealValuePipeline + 
									beaconConverterService.convertCurrencyRateUsingBeaconConverterMap(opp.getDealCurrency(), "USD", opp.getDigitalDealValue(), beaconConverterMap).doubleValue();
						}
					} else if (opp.getSalesStageCode() == 9) { // For Won

						opportunitiesWon.add(opp);
						if (opp.getDigitalDealValue() != null) {
							sumOfDealValueWon = sumOfDealValueWon + 
									beaconConverterService.convertCurrencyRateUsingBeaconConverterMap(opp.getDealCurrency(), "USD", opp.getDigitalDealValue(), beaconConverterMap).doubleValue();
						}
					} else if ((opp.getSalesStageCode() == 10)
							|| (opp.getSalesStageCode() == 11)
							|| (opp.getSalesStageCode() == 13)) { // For Lost
																	// 10,11,13
						opportunitiesLost.add(opp);
						if (opp.getDigitalDealValue() != null) {
							sumOfDealValueLost = sumOfDealValueLost + 
									beaconConverterService.convertCurrencyRateUsingBeaconConverterMap(opp.getDealCurrency(), "USD", opp.getDigitalDealValue(), beaconConverterMap).doubleValue();
						}
					} else if (opp.getSalesStageCode() == 12) { // For Shelved

						opportunitiesShelved.add(opp);
						if (opp.getDigitalDealValue() != null) {
							sumOfDealValueShelved = sumOfDealValueShelved + 
									beaconConverterService.convertCurrencyRateUsingBeaconConverterMap(opp.getDealCurrency(), "USD", opp.getDigitalDealValue(), beaconConverterMap).doubleValue();
						}
					}
			}
			if (!opportunitiesProspects.isEmpty()) {
				leadershipOpportunitiesDTO
						.setOppProspects(getLeadershipOpportunityObjectBySalesStageCode(
								opportunitiesProspects, sumOfDealValueProspects, userId));
				checkOppExists = true;
			}
			if (!opportunitiesPipeline.isEmpty()) {
				leadershipOpportunitiesDTO
						.setOppPipeline(getLeadershipOpportunityObjectBySalesStageCode(
								opportunitiesPipeline, sumOfDealValuePipeline, userId));
				checkOppExists = true;
			}
			if (!opportunitiesWon.isEmpty()) {
				leadershipOpportunitiesDTO
						.setOppWon(getLeadershipOpportunityObjectBySalesStageCode(
								opportunitiesWon, sumOfDealValueWon, userId));
				checkOppExists = true;
			}
			if (!opportunitiesLost.isEmpty()) {
				leadershipOpportunitiesDTO
						.setOppLost(getLeadershipOpportunityObjectBySalesStageCode(
								opportunitiesLost, sumOfDealValueLost, userId));
				checkOppExists = true;
			}
			if (!opportunitiesShelved.isEmpty()) {
				leadershipOpportunitiesDTO
						.setOppShelved(getLeadershipOpportunityObjectBySalesStageCode(
								opportunitiesShelved, sumOfDealValueShelved, userId));
				checkOppExists = true;
			}
		}
		if (!checkOppExists) {
			logger.error("NOT_FOUND: No Opportunity found for user Id : {}",
					userId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Opportunity found for user Id : " + userId);
		}
		logger.debug("End: Inside getOpportunitiesBySalesStageCode() of DashBoardService");
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
			List<OpportunityT> opportunitTs, Double sumOfDealValue, String userId)
			throws Exception {

		logger.debug("Start: Inside getLeadershipOpportunityObjectBySalesStageCode() of DashBoardService");
		LeadershipOpportunityBySalesStageCodeDTO oppBySalesCode = new LeadershipOpportunityBySalesStageCodeDTO();
		try {
			if (opportunitTs != null) {
				oppBySalesCode.setOpportunities(opportunitTs);
				oppBySalesCode.setSizeOfOpportunities(opportunitTs.size());
			}
			//TODO: Calculate the sum of digital deal value
			oppBySalesCode.setSumOfDigitalDealValue(Double
					.parseDouble((new DecimalFormat("##.##")
							.format(sumOfDealValue))));
		} catch (Exception e) {
			logger.error(
					"NOT_FOUND: An Internal Error has occured while processing request for {} : ",
					userId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"An Internal Error has occured while processing request for userId "
							+ userId);
		}
		logger.debug("End: Inside getLeadershipOpportunityObjectBySalesStageCode() of DashBoardService");
		return oppBySalesCode;
	}

	/**
	 * This method helps in retrieving the values from the database and sets the
	 * appropriate object
	 * 
	 * @param query
	 * @return List<LeadershipAllOpportunityDTO>
	 * @throws Exception
	 */
	private List<OpportunityT> getAllOpportunitiesUsingQuery(
			StringBuffer query, String userId) throws Exception {
		logger.debug("Start: Inside getAllOpportunitiesUsingQuery() of DashBoardService");
		TypedQuery<OpportunityT> opportunitiesTypedQuery=null;
		try {
			opportunitiesTypedQuery = (TypedQuery<OpportunityT>) entityManager
					.createNativeQuery(query.toString(),OpportunityT.class);

		} catch (Exception e) {
			logger.error(
					"NOT_FOUND: An Internal Error has occured while processing request for {} : ",
					userId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"An Internal Error has occured while processing request for userId "
							+ userId);
		}
		logger.debug("End: Inside getAllOpportunitiesUsingQuery() of DashBoardService");
		return opportunitiesTypedQuery.getResultList();
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
		
		logger.debug("Start: Inside constructQueryForLeadershipDashboardOpportunitiesWithPrivileges() of DashBoardService");

		StringBuffer queryBuffer = new StringBuffer();

		queryBuffer.append(TEAM_OPPORTUNITY_QUERY_PART1);

		queryBuffer.append(geography);

		queryBuffer.append(TEAM_OPPORTUNITY_QUERY_PART1a);

		queryBuffer.append(geography);

		queryBuffer.append(TEAM_OPPORTUNITY_QUERY_PART2);

		queryBuffer.append(fromDateTs);

        queryBuffer.append(TEAM_OPPORTUNITY_QUERY_PART3);

		queryBuffer.append(toDateTs);

        queryBuffer.append(TEAM_OPPORTUNITY_QUERY_PART4);

        queryBuffer.append(TEAM_OPPORTUNITY_QUERY_PART5);

	    queryBuffer.append(privilegesQueryString);

		queryBuffer.append(TEAM_OPPORTUNITY_QUERY_SUFFIX);

		logger.debug("End: Inside constructQueryForLeadershipDashboardOpportunitiesWithPrivileges() of DashBoardService");
		
		return queryBuffer;
	}

}