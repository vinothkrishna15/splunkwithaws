package com.tcs.destination.service;

import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_CONNECTS_CUSTOMER_COND_PREFIX;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_CONNECTS_GEO_COND_PREFIX;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_CONNECTS_IOU_COND_PREFIX;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_CONNECTS_QUERY_CUSTOMER;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_CONNECTS_QUERY_PART1;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_CONNECTS_QUERY_PART2;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_CONNECTS_QUERY_PARTNER;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_CONNECTS_SUBSP_COND_PREFIX;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_OPPORTUNITY_QUERY_PART1;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_OPPORTUNITY_QUERY_PART1a;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_OPPORTUNITY_QUERY_PART2;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_OPPORTUNITY_QUERY_PART3;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_OPPORTUNITY_QUERY_PART4;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_OPPORTUNITY_QUERY_PART5;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_OPPORTUNITY_QUERY_SUFFIX;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_OPPORTUNITY_WIN_QUERY_PART1;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_OPPORTUNITY_WIN_QUERY_PART1a;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_OPPORTUNITY_WIN_QUERY_PART2;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_OPPORTUNITY_WIN_QUERY_PART3;
import static com.tcs.destination.utils.LeadershipQueryConstants.TEAM_OPPORTUNITY_WIN_QUERY_PART4;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.ContentDTO;
import com.tcs.destination.bean.HealthCardOverallPercentage;
import com.tcs.destination.bean.LeadershipConnectsDTO;
import com.tcs.destination.bean.LeadershipOpportunitiesDTO;
import com.tcs.destination.bean.LeadershipOpportunityBySalesStageCodeDTO;
import com.tcs.destination.bean.LeadershipOverallWinsDTO;
import com.tcs.destination.bean.LeadershipWinsDTO;
import com.tcs.destination.bean.MobileDashboardComponentT;
import com.tcs.destination.bean.MobileDashboardT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.PerformaceChartBean;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.BdmTargetTRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.HealthCardOverallPercentageRepository;
import com.tcs.destination.data.repository.MobileDashboardComponentRepository;
import com.tcs.destination.data.repository.MobileDashboardRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.HealthCardComponent;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.UserAccessPrivilegeQueryBuilder;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.StringUtils;

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
	
	@Autowired
	MobileDashboardRepository mobileDashboardRepository;
	
	@Autowired
	HealthCardOverallPercentageRepository healthCardOverallPercentageRepository;

	@Autowired
	private MobileDashboardComponentRepository mobileDashboardComponentRepo;

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

		if (!StringUtils.isEmpty(supervisorId)) {

			// Get all users under a supervisor
			List<String> users = userRepository
					.getAllSubordinatesIdBySupervisorId(supervisorId);

			// Adding supervisor ID
			users.add(supervisorId);

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
			logger.error("NOT_FOUND: Supervisor Id is empty");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Supervisor Id is empty");
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
			String userId, Date fromDate, Date toDate, String geography, String connectCategory, 
			String searchedUserId, boolean teamFlag)
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
//				case CONSULTING_HEAD:
				case CONSULTING_USER:	
					logger.error("User is not authorized to access this service");
					throw new DestinationException(HttpStatus.FORBIDDEN,
							"User is not authorised to access this service");
				default:
					leadershipConnectsDTO = getTeamConnectsBasedOnUserPrivileges(
							userId, fromDate, toDate, geography, connectCategory, searchedUserId, teamFlag);
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
			String supervisorId, Date startDate, Date endDate, String geography, String connectCategory,
			String searchedUserId, boolean teamFlag)
					throws Exception {

		logger.debug("Start:Inside  getTeamConnectsBasedOnUserPrivileges() of DashBoardService");
		boolean upComingConnects = false;
		LeadershipConnectsDTO leadershipConnectsDTO = null;
		List<ConnectT> listOfPastConnects = null;
		List<ConnectT> listOfUpcomingConnects = null;
		Timestamp fromDateTs = new Timestamp(startDate.getTime());
		Timestamp toDateTs = new Timestamp(endDate.getTime()
				+ Constants.ONE_DAY_IN_MILLIS-1);
		Date now = new Date(); // Get current DateTime
		Date endDateTime= new DateTime(endDate.getTime()).plusDays(1).toDate();
		Timestamp nowTs = new Timestamp(endDateTime.getTime()-1);
		Timestamp nowNextMsTs = new Timestamp(now.getTime()+1); // Get the next millisecond's timestamp w.r.t now
		
		if(toDateTs.after(new Timestamp(now.getTime()))){
			nowTs = new Timestamp(now.getTime()); // Get the current timestamp
			upComingConnects = true;
		}

		// If user to search is empty, get the Dashboard details for Sales Heads/SI
		if(!StringUtils.isEmpty(searchedUserId)) {
		
			// Get the Past connects for the searched user
			listOfPastConnects = getLeadershipDashboardConnectsForUsers
					(searchedUserId, teamFlag, fromDateTs, nowTs, connectCategory);
			// Get the Upcoming Connects for the searched user
			listOfUpcomingConnects = new ArrayList<ConnectT>();
			if(upComingConnects) {
				listOfUpcomingConnects = getLeadershipDashboardConnectsForUsers
						(searchedUserId, teamFlag, nowNextMsTs, toDateTs, connectCategory);
			}

			
		} else {

			// Get the Past connects
			listOfPastConnects = getLeadershipDashboardTeamConnects(
					supervisorId, geography, fromDateTs, nowTs, connectCategory);
			listOfUpcomingConnects = new ArrayList<ConnectT>();
			if(upComingConnects) {
			// Get the Future Connects using the constructed query
				listOfUpcomingConnects = getLeadershipDashboardTeamConnects(
						supervisorId, geography, nowNextMsTs, toDateTs, connectCategory);
			}
		}

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
			if (CollectionUtils.isNotEmpty(listOfUpcomingConnects)) {
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

	/**
	 * Get Leadership Dashboard Connects for Users
	 * 
	 * @param searchedUserId
	 * @param teamFlag
	 * @param fromDateTs
	 * @param toDateTs
	 * @param category
	 * @return
	 * @throws Exception
	 */
	private List<ConnectT> getLeadershipDashboardConnectsForUsers(String searchedUserId, boolean teamFlag, 
			Timestamp fromDateTs, Timestamp toDateTs, String category) throws Exception {
		List<ConnectT> connectTs = null;

		List<String> searchUserList = new ArrayList<String>();
		searchUserList.add(searchedUserId);

		if (teamFlag) { // If Team Flag is false, subordinate details will not be shown

			// Get all subordinates for the supervisor
			List<String> subordinates = userRepository
					.getAllSubordinatesIdBySupervisorId(searchedUserId);

			// Add the users to the resultant list
			for (String user : subordinates) {
				searchUserList.add(user);
			}
		}

		// Get the connects for the users
		connectTs = connectRepository.getConnectsForUsers(searchUserList, fromDateTs, toDateTs, category);

		return connectTs;
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
	 * This method appends the query to retrieve the connects for partner or customer
	 * 
	 * @param supervisorId
	 * @param displayGeography
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	private List<ConnectT> getLeadershipDashboardTeamConnects(
			String supervisorId, String displayGeography, Timestamp fromDate,
			Timestamp toDate, String connectCategory) throws Exception {
		logger.debug("Start:Inside  getLeadershipDashboardTeamConnects() of DashBoardService");		
		List<ConnectT> leadershipConnects = null;

		//For retrieving customer connects alone
		if(connectCategory.equalsIgnoreCase(Constants.CUSTOMER)){			

			leadershipConnects = getLeadershipDashboardConnectsForCustomer(supervisorId, displayGeography,
					fromDate, toDate);
		}
		//For retrieving partner connects, without access privileges logic
		else if(connectCategory.equalsIgnoreCase(Constants.PARTNER)){
			leadershipConnects = getLeadershipDashboardConnectsForPartner(
					fromDate, toDate);

		}
		logger.debug("End:Inside  getLeadershipDashboardTeamConnects() of DashBoardService");
		return leadershipConnects;
	}

	/**
	 * This method is used to retrieve partner connects for leadership dashboard without access privileges logic
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	private List<ConnectT> getLeadershipDashboardConnectsForPartner(
			Timestamp fromDate, Timestamp toDate) throws Exception {
		logger.debug("Start:Inside  getLeadershipDashboardConnectsForPartner() of DashBoardService");

		//Query to get partner connects
		StringBuffer queryBufferForPartner = new StringBuffer(TEAM_CONNECTS_QUERY_PARTNER);
		Query queryForPartnerConnects = entityManager.createNativeQuery(queryBufferForPartner.toString(),
				ConnectT.class);

		queryForPartnerConnects.setParameter("fromDate", fromDate);
		queryForPartnerConnects.setParameter("toDate", toDate);		

		logger.debug("End:Inside  getLeadershipDashboardConnectsForPartner() of DashBoardService");
		return queryForPartnerConnects.getResultList();
	}

	/**
	 * This method is used to retrieve customer connects for leadership dashboard based on access privileges
	 * @param supervisorId
	 * @param displayGeography
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @throws Exception
	 */
	private List<ConnectT> getLeadershipDashboardConnectsForCustomer(String supervisorId,
			String displayGeography, Timestamp fromDate, Timestamp toDate)
					throws Exception {
		logger.debug("Start: Inside getConnectsForCustomer() method of Dashboard Service");

		//Connects for a customer are stored
		Set<ConnectT> customerConnects = null;

		StringBuffer queryBufferForCustomerConnects = new StringBuffer(TEAM_CONNECTS_QUERY_PART1);
		// Append privileges obtained above. Note that access privilege is only
		// for customers and not for partners
		queryBufferForCustomerConnects
		.append(constructPrivilegesQueryForLeadershipDashboard(supervisorId));
		queryBufferForCustomerConnects.append(TEAM_CONNECTS_QUERY_PART2);
		Query queryForCustomerConnects = entityManager.createNativeQuery(queryBufferForCustomerConnects.toString(),
				ConnectT.class);

		queryForCustomerConnects.setParameter("fromDate", fromDate);
		queryForCustomerConnects.setParameter("toDate", toDate);
		queryForCustomerConnects.setParameter("displayGeography", displayGeography);
		customerConnects= new HashSet<ConnectT>(queryForCustomerConnects.getResultList());

		//Check if user is a GEO HEAD or IOU HEAD
		UserT user = userService.findByUserId(supervisorId);
		if (user != null) {
			String userGroup = user.getUserGroupMappingT().getUserGroup();
			if (UserGroup.contains(userGroup)) {
				if((userGroup.equalsIgnoreCase(UserGroup.GEO_HEADS.getValue()))||
						(userGroup.equalsIgnoreCase(UserGroup.IOU_HEADS.getValue()))){

					//Query to get the user's subordinates till n-level
					List<String> userIds = userRepository
							.getAllSubordinatesIdBySupervisorId(supervisorId);
					//User is also added to the list of userIds
					userIds.add(supervisorId);

					//Query to get subordinate's connects
					StringBuffer queryBufferForSub = new StringBuffer(TEAM_CONNECTS_QUERY_CUSTOMER);
					Query queryForSubordinateConnects = entityManager.createNativeQuery(queryBufferForSub.toString(),
							ConnectT.class);
					queryForSubordinateConnects.setParameter("fromDate", fromDate);
					queryForSubordinateConnects.setParameter("toDate", toDate);						
					queryForSubordinateConnects.setParameter("userIdList", userIds);
					// The customer connects owned by sub-ordinates and user is retrieved and added to the connects obtained based on geography
					List<ConnectT> resultSet = queryForSubordinateConnects.getResultList();
					if(resultSet!=null && resultSet.size()!=0){
						customerConnects.addAll(resultSet);
					}						
				}
			}

		}	
		logger.debug("End: Inside getConnectsForCustomer() method of Dashboard Service");
		return new ArrayList<ConnectT>(customerConnects);
	}

	/**
	 * This service retrieves the WON opportunities of all users under a
	 * supervisor based on his access privileges. This module is for Strategic
	 * Initiatives/GEO Head/IOU Heads
	 * 
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param geography
	 * @return LeadershipOverallWinsDTO
	 * @throws Exception
	 */
	public LeadershipOverallWinsDTO getLeadershipWinsByGeography(String userId,
			Date fromDate, Date toDate, String geography, String searchedUserId, boolean teamFlag) throws Exception {

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
//				case CONSULTING_HEAD:
				case CONSULTING_USER:
					logger.error("User is not authorized to access this service");
					throw new DestinationException(HttpStatus.FORBIDDEN,
							"User is not authorised to access this service");
				default:
					leadershipOverallWinsDTO = getLeadershipWinsByUserPrivileges(
							userId, fromDate, toDate, geography, searchedUserId, teamFlag);
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
			String userId, Date fromDate, Date toDate, String geography, String searchedUserId, boolean teamFlag)
					throws Exception {

		logger.debug("Start: Inside getLeadershipWinsByUserPrivileges() of DashBoardService");
		String privilegesQuery = "";
		LeadershipOverallWinsDTO leadershipTotalWinsDTO = null;
		LeadershipWinsDTO leadershipWins = null;

		Timestamp fromDateTs = new Timestamp(fromDate.getTime());
		Timestamp toDateTs = new Timestamp(toDate.getTime()
				+ Constants.ONE_DAY_IN_MILLIS - 1);

		// If user to search is empty, get the Dashboard details for Sales Heads/SI
		if(StringUtils.isEmpty(searchedUserId)) {

			// Get the privileges for the user and append to the query constructed
			// above
			privilegesQuery = constructPrivilegesQueryForLeadershipDashboard(userId);

			// Construct the Query for Wins
			StringBuffer queryBufferForWins = constructQueryForLeadershipDashboardWinsWithPrivileges(
					userId, geography, fromDateTs, toDateTs, privilegesQuery);

			// Get wins using the constructed query
			leadershipWins = getWinsFromQueryBuffer(
					queryBufferForWins, userId);
		} else {
			// Get Opportunity Wins for users and his subordinates 
			leadershipWins = getLeadershipWinsForSearchedUsers(searchedUserId,
					teamFlag, fromDateTs, toDateTs);
		}

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
	 * Get Leadership Wins for searched users
	 * 
	 * @param searchedUserId
	 * @param teamFlag
	 * @param fromDateTs
	 * @param toDateTs
	 * @return
	 * @throws Exception
	 */
	LeadershipWinsDTO getLeadershipWinsForSearchedUsers(String searchedUserId,
			boolean teamFlag, Timestamp fromDateTs, Timestamp toDateTs)
					throws Exception {
		LeadershipWinsDTO leadershipWins = null;

		List<String> searchUserList = new ArrayList<String>();
		searchUserList.add(searchedUserId);

		if (teamFlag) { // If Team Flag is false, subordinate details will not be shown
			List<String> subordinates = userRepository
					.getAllSubordinatesIdBySupervisorId(searchedUserId);

			// Add the subordinates
			for (String user : subordinates) {
				searchUserList.add(user);
			}
		}

		// Get the Opportunity Wins for users
		List<OpportunityT> oppWinsByUsers = opportunityRepository
				.getOpportunityWinsForUsers(searchUserList, fromDateTs,
						toDateTs);

		leadershipWins = new LeadershipWinsDTO();
		leadershipWins.setListOfWins(oppWinsByUsers);
		leadershipWins.setSizeOfWins(oppWinsByUsers.size());

		// To Get the Sum of Wins in USD
		Double convertedDigitalDealValueForSumOfWins = 0.0;
		for (OpportunityT oppWins : oppWinsByUsers) {
			convertedDigitalDealValueForSumOfWins = convertedDigitalDealValueForSumOfWins
					+ beaconConverterService.convert(oppWins.getDealCurrency(),
							Constants.USD, oppWins.getDigitalDealValue())
							.doubleValue();
		}
		leadershipWins
		.setSumOfdigitalDealValue(convertedDigitalDealValueForSumOfWins);
		return leadershipWins;
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
			String userId, Date fromDate, Date toDate, String geography,String searchedUserId,boolean teamFlag)
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
//				case CONSULTING_HEAD:
				case CONSULTING_USER:
					logger.error("User is not authorized to access this service");
					throw new DestinationException(HttpStatus.FORBIDDEN,
							"User is not authorised to access this service");
				default:
					listOfOppportunities = getLeadershipOpportunitiesByUserPrivileges(
							userId, fromDate, toDate, geography,searchedUserId,teamFlag);
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
			String userId, Date fromDate, Date toDate, String geography,String searchedUserId,boolean teamFlag)
					throws Exception {

		logger.debug("Start: Inside getLeadershipOpportunitiesByUserPrivileges() of DashBoardService");
		LeadershipOpportunitiesDTO listOfOppportunities = null;
		List<OpportunityT> opportunitiesBySalesCode=new ArrayList<OpportunityT>();
		List<String> searchedIdList=new ArrayList<String>();
		if((!StringUtils.isEmpty(searchedUserId))&&(teamFlag==false))
		{
			searchedIdList.add(searchedUserId);
			opportunitiesBySalesCode = getOpportunitiesBasedOnSearchedUserId(
					searchedIdList, fromDate, toDate, geography);

		}
		else if((!StringUtils.isEmpty(searchedUserId))&&(teamFlag==true))
		{
			searchedIdList=userRepository.getAllSubordinatesIdBySupervisorId(searchedUserId);
			searchedIdList.add(searchedUserId);
			opportunitiesBySalesCode = getOpportunitiesBasedOnSearchedUserId(
					searchedIdList, fromDate, toDate, geography);

		}
		else
		{
			opportunitiesBySalesCode = getPrevilegedOpportunities(
					userId, fromDate, toDate, geography);

			getPrevilegedOpportunities(
					userId, fromDate, toDate, geography);
		}
		// Get ListOfOpp, sum Of digital deal value based on Sales Stage Code
		// i.e. 0-3(Prospecting), 4-8(Qualified Pipeline), 9(won),
		// 10,11,13(lost),
		// 12(shelved)
		listOfOppportunities = getOpportunitiesBySalesStageCode(
				opportunitiesBySalesCode, userId);
		logger.debug("End: Inside getLeadershipOpportunitiesByUserPrivileges() of DashBoardService");

		return listOfOppportunities;


	}

	/**
	 * To fetch the opportunities based on searched user id
	 * @param searchedUserId
	 * @param fromDate
	 * @param toDate
	 * @param geography
	 * @return
	 */
	List<OpportunityT> getOpportunitiesBasedOnSearchedUserId(List<String> searchedUserId,
			Date fromDate, Date toDate, String geography)
			{
		logger.debug("Start: Inside getOpportunitiesBasedOnSearchedUserId() of DashBoardService");
		List<OpportunityT> opportunitiesBySalesCode = null;
		Timestamp fromDateTs = new Timestamp(fromDate.getTime());
		Timestamp toDateTs = new Timestamp(toDate.getTime()
				+ Constants.ONE_DAY_IN_MILLIS - 1);
		opportunitiesBySalesCode = opportunityRepository.getAllOpportunitiesBySearchedIdQuery(searchedUserId,fromDateTs,toDateTs);
		return opportunitiesBySalesCode;
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
				} else if (opp.getSalesStageCode() == 10) { // For Lost
					// 10
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
	/**
	 * The Service is Used to get the users of the LeaderShipDashBoard By AjaxSearch
	 * @param nameWith
	 * @return 
	 * @throws Exception
	 */
	public List<UserT>findUsersAjaxSearch(String nameWith) throws Exception{
		logger.info("Inside findUsersAjaxSearch() method"); 
		List<UserT> users=null;

		nameWith = "%" + nameWith + "%";
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		UserT user = userRepository.findByUserId(userId);

		String userGroup = ( (UserT) user).getUserGroupMappingT().getUserGroup();

		if (UserGroup.contains(userGroup)) {
			switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
			case STRATEGIC_INITIATIVES:
				users= userRepository.getUsersByUserNameKeyword(nameWith);
				break;
			case GEO_HEADS:
			case IOU_HEADS:
			case CONSULTING_HEAD:
				users = userRepository.getSubordinatesIdBySupervisorId(userId, nameWith);
				break;

			default:
				logger.error("UNAUTHORIZED : User is not authorized to access this service");
				throw new DestinationException(HttpStatus.FORBIDDEN, "User is not authorised to access this service");
			}
		}
		if((users == null)||(users.isEmpty())){
			logger.error("NOT_FOUND : No user found");
			throw new DestinationException(HttpStatus.NOT_FOUND, "No user found");
		}
		logger.info("End of findUsersAjaxSearch() method");
		return users;

	}
	
	/**
	 * this service is used to get the mobile dashboard values
	 * @param dashboardCategory
	 * @return
	 * @throws Exception
	 */
	public ContentDTO<MobileDashboardComponentT> getAllMobileDashboardValues(
			int dashboardCategory) throws Exception {
		ContentDTO<MobileDashboardComponentT> content = new ContentDTO<MobileDashboardComponentT>();
		logger.info("Start: Inside getMobileDashboardValues() of DashBoardService");
		List<MobileDashboardComponentT> mobileDashboardValues = mobileDashboardComponentRepo.findByCategoryIdOrderByComponentIdAsc(dashboardCategory);
		prepareMobileComponentLists(mobileDashboardValues);
		content.setContent(mobileDashboardValues);
		logger.info("End getMobileDashboardValues() of DashBoardService");
		return content;
	}

	private void prepareMobileComponentLists(List<MobileDashboardComponentT> mobileDashboardValues) {
		if(CollectionUtils.isNotEmpty(mobileDashboardValues)) {
			for (MobileDashboardComponentT mobileDashboardComponentT : mobileDashboardValues) {
				mobileDashboardComponentT.setMobileDashboardTs(null);
				mobileDashboardComponentT.setCategory(null);
				mobileDashboardComponentT.setDeliveryCentreUtilizationTs(null);
				mobileDashboardComponentT.setHealthCardOverallPercentages(null);
			}
		}
	}

	/**
	 * this service is used to get the mobile dashboard values
	 * @param dashboardCategory
	 * @return
	 * @throws Exception
	 */
	public ContentDTO<MobileDashboardT> getFavourMobileDashboardValues(
			int dashboardCategory) throws Exception {
		ContentDTO<MobileDashboardT> content = new ContentDTO<MobileDashboardT>();
		logger.info("Start: Inside getMobileDashboardValues() of DashBoardService");
		String userId = DestinationUtils.getCurrentUserId();
		List<MobileDashboardT> mobileDashboardValues = mobileDashboardRepository
				.findByUserIdAndDashboardCategoryOrderByOrderNumberAsc(userId,
						dashboardCategory);
		removeCyclicForMobileDashboard(mobileDashboardValues);
		content.setContent(mobileDashboardValues);
		logger.info("End getMobileDashboardValues() of DashBoardService");
		return content;
	}

	private void removeCyclicForMobileDashboard(
			List<MobileDashboardT> mobiledashboardvalues) {
		Date startDate = DateUtils
				.getFinancialYrStartDate();
		Date endDate = new Date();
		for (MobileDashboardT mobileDashboardT : mobiledashboardvalues) {
			mobileDashboardT.setMobileDashboardCategory(null);
			mobileDashboardT.getMobileDashboardComponentT().setMobileDashboardTs(null);
			mobileDashboardT.getMobileDashboardComponentT().setCategory(null);
			mobileDashboardT.getMobileDashboardComponentT().setDeliveryCentreUtilizationTs(null);
			mobileDashboardT.getMobileDashboardComponentT().setHealthCardOverallPercentages(null);
			if(HealthCardComponent.WIN_RATIO.getCategoryId()==mobileDashboardT.getComponentId()) {
				List<Object[]> winLoss = opportunityRepository.getNumberOfWinsAndLosses(startDate,endDate);
				if(CollectionUtils.isNotEmpty(winLoss)) {
					Object[] winLossObj = winLoss.get(0);
					Integer noOfWins = winLossObj[0]!=null ? ((BigInteger) winLossObj[0]).intValue() : 0;
					Integer noOfLoss = winLossObj[1]!=null ? ((BigInteger) winLossObj[1]).intValue() : 0;
					BigDecimal winRatio = DestinationUtils.getWinRatio(noOfWins, noOfLoss);
					mobileDashboardT.setValue(winRatio);
				} else {
					mobileDashboardT.setValue(BigDecimal.ZERO);
				}
			} else {
				BigDecimal overallPercentage = healthCardOverallPercentageRepository.getOverallPercentage(mobileDashboardT.getComponentId());
				mobileDashboardT.setValue(DestinationUtils.scaleToTwoDigits(overallPercentage,true));
			}
		}
	}

	@Transactional
	public void updateMobileDashboard(ContentDTO<MobileDashboardT> mobileDashboardContent) {
		List<MobileDashboardT> mobileDashboardTs = mobileDashboardContent.getContent();
		Integer category = mobileDashboardTs.get(0).getDashboardCategory();
		String userId = DestinationUtils.getCurrentUserId();
		deleteMobileDashboardList(category,userId);
		saveMobileDashboardList(mobileDashboardTs,userId);
	}

	private void deleteMobileDashboardList(Integer category, String userId) {
		mobileDashboardRepository.deleteByDashboardCategoryAndUserId(category,userId);
	}

	private void saveMobileDashboardList(
			List<MobileDashboardT> mobileDashboardTs, String userId) {
		for(MobileDashboardT dashboardT : mobileDashboardTs) {
			dashboardT.setUserId(userId);
			mobileDashboardRepository.save(dashboardT);
		}
		
	}

	private void deleteMobileDashboardList(List<MobileDashboardT> mobileDashboardTs) {
		mobileDashboardRepository.delete(mobileDashboardTs);
		
	}
}