package com.tcs.destination.controller;

import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.LeadershipConnectsDTO;
import com.tcs.destination.bean.LeadershipOpportunitiesDTO;
import com.tcs.destination.bean.LeadershipOverallWinsDTO;
import com.tcs.destination.bean.PerformaceChartBean;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.DashBoardService;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * 
 * This controller is used to handle dashboard module related requests
 *
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

	@Autowired
	DashBoardService dashboardService;

	private static final Logger logger = LoggerFactory
			.getLogger(DashboardController.class);

	@RequestMapping(value = "/chart", method = RequestMethod.GET)
	public String chart(
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		logger.info("Start of retrieving the chart values");
		try {
			PerformaceChartBean chartValues = dashboardService.getChartValues(
					userId, financialYear);
			logger.info("End of retrieving the chart values");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, chartValues);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the chart values");
		}
	}

	/**
	 * This controller retrieves the team chart values of all the
	 * users under a supervisor
	 * 
	 * @param supervisorId
	 * @param financialYear
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/teamchart", method = RequestMethod.GET)
	public String teamChart(
			@RequestParam(value = "supervisorId") String supervisorId,
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of retrieving the Team chart values by supervisor id");
		PerformaceChartBean chartValues = null;
		try {
			chartValues = dashboardService.getTeamChartValues(supervisorId,
					financialYear);
			logger.info("End of retrieving the Team chart values by supervisor id");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, chartValues);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Team chart values for supervisor id "
							+ supervisorId);
		}
	}

	/**
	 * This Controller retrieves a list of Connects based on the geography
	 *
	 * @param geography
	 * @param fromDate
	 * @param toDate
	 * @param includeFields
	 * @param view
	 * @return String
	 * @throws Exception
	 */
	@RequestMapping(value = "/leadership/connect", method = RequestMethod.GET)
	public String getLeadershipConnectsByGeography(
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "fromDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam(value = "toDate", defaultValue = "01012099") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam(value = "fields", defaultValue = "all") String includeFields,
			@RequestParam(value = "connectCategory") String connectCategory,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "searchedUserId", defaultValue = "") String searchedUserId,
			@RequestParam(value = "teamFlag", defaultValue = "false") boolean teamFlag)
			throws DestinationException {
		logger.info("Start of retrieving a list of Connects based on the user (SI, Geo Heads, IOU Heads)");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		LeadershipConnectsDTO connects = null;
		try {
			connects = dashboardService.getLeadershipConnectsByGeography(
					userId, fromDate, toDate, geography, connectCategory, searchedUserId, teamFlag);
			logger.info("End of retrieving a list of Connects based on the user (SI, Geo Heads, IOU Heads)");
			return ResponseConstructors.filterJsonForFieldAndViews(
					includeFields, view, connects);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving a list of Connects based on the user");
		}
	}
	/**
	 * This method is used to generate stub for getLeadershipConnectsByGeography method
	 * @param geography
	 * @param fromDate
	 * @param toDate
	 * @param includeFields
	 * @param connectCategory
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
//	@RequestMapping(value = "/leadership/connect", method = RequestMethod.GET)
//	public String getLeadershipConnectsByGeographyStub(
//			@RequestParam(value = "geography", defaultValue = "") String geography,
//			@RequestParam(value = "fromDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
//			@RequestParam(value = "toDate", defaultValue = "01012099") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
//			@RequestParam(value = "fields", defaultValue = "all") String includeFields,
//			@RequestParam(value = "connectCategory") String connectCategory,
//			@RequestParam(value = "view", defaultValue = "") String view)
//			throws DestinationException {
//		logger.info("Start of stub to retrieve a list of Connects based on the user (SI, Geo Heads, IOU Heads)");
//		LeadershipConnectsDTO connects = new LeadershipConnectsDTO();
//		try {
//			if(connectCategory.equalsIgnoreCase("CUSTOMER")){
//				
//				ObjectMapper mapper = new ObjectMapper();
//				String json ="{\"pastConnects\":[{\"connectId\":\"CNN139\",\"connectName\":\"sample 2\",\"customerId\":\"CUS1\",\"location\":\"Kochi, Kerala, India\",\"cityMapping\":{\"city\":\"Kochi, Kerala, India\",\"latitude\":\"9.9312328\",\"longitude\":\"76.26730410000005\"},\"customerMasterT\":{\"customerId\":\"CUS1\",\"customerName\":\"1-800-FLOWERS.COM Americas\"}},{\"connectId\":\"CNN138\",\"connectName\":\"test sample\",\"customerId\":\"CUS1\",\"location\":\"Pune, Maharashtra 411001, India\",\"cityMapping\":{\"city\":\"Pune, Maharashtra 411001, India\",\"latitude\":\"18.5204303\",\"longitude\":\"73.85674369999992\"},\"customerMasterT\":\"CUS1\"}],\"sizeOfPastConnects\":2,\"upcomingConnects\":[{\"connectId\":\"CNN130\",\"connectName\":\"meeibf\",\"customerId\":\"CUS37\",\"location\":\"Chennai, Tamil Nadu, India\",\"cityMapping\":{\"city\":\"Chennai, Tamil Nadu, India\",\"latitude\":\"13.0826802\",\"longitude\":\"80.27071840000008\"},\"customerMasterT\":{\"customerId\":\"CUS37\",\"customerName\":\"Airtel India\"}}],\"sizeOfUpcomingConnects\":1}";
//				connects =  mapper.readValue(json, LeadershipConnectsDTO.class);
//				
//			}
//			else if(connectCategory.equalsIgnoreCase("PARTNER")){
//				ObjectMapper mapper = new ObjectMapper();
//				String json ="{\"pastConnects\": [{\"connectId\": \"CNN80\",\"connectName\": \"Discuss capability enablement for ABN Amro Digital Ambition\",\"partnerId\": \"PAT10\",\"location\": \"Amsterdam, Netherlands\",\"cityMapping\": {\"city\": \"Amsterdam, Netherlands\",\"latitude\": \"52.3702157\",\"longitude\": \"4.895167899999933\"},\"partnerMasterT\": {\"partnerId\": \"PAT10\",\"partnerName\": \"Backbase\"}}],\"sizeOfPastConnects\": 1,\"upcomingConnects\": [{\"connectId\": \"CNN218\",\"connectName\": \"Hortonworks pricing for Netherland opportunity\",\"partnerId\": \"PAT27\",\"location\": \"Mumbai, Maharashtra, India\",\"cityMapping\": {\"city\": \"Mumbai, Maharashtra, India\",\"latitude\": \"19.0759837\",\"longitude\": \"72.87765590000004\"},\"partnerMasterT\": {\"partnerId\": \"PAT27\",\"partnerName\": \"Hortonworks\"}}  ],\"sizeOfUpcomingConnects\": 1}";
//				connects =  mapper.readValue(json, LeadershipConnectsDTO.class);
//			}
//			logger.info("End of stub to retrieve a list of Connects based on the user (SI, Geo Heads, IOU Heads)");
//			return ResponseConstructors.filterJsonForFieldAndViews(
//					includeFields, view, connects);
//		} catch (DestinationException e) {
//			throw e;
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
//					"Backend error in retrieving a list of Connects based on the user");
//		}
//	}

	/**
	 * This Controller retrieves Opportunities Won based on the user (SI, Geo
	 * Heads, IOU Heads)
	 * 
	 * @param geography
	 * @param fromDate
	 * @param toDate
	 * @param includeFields
	 * @param view
	 * @return 
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/leadership/wins", method = RequestMethod.GET)
	public String getLeadershipWinsByGeography(
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "fromDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam(value = "toDate", defaultValue = "01012099") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam(value = "searchedUserId", defaultValue = "") String searchedUserId,
			@RequestParam(value = "teamFlag", defaultValue = "false") boolean teamFlag,
			@RequestParam(value = "fields", defaultValue = "all") String includeFields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of retrieving the leadership wins by geography");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		LeadershipOverallWinsDTO wins = null;
		try {
			wins = dashboardService.getLeadershipWinsByGeography(userId,
					fromDate, toDate, geography, searchedUserId, teamFlag);
			logger.info("End of retrieving the leadership wins by geography");
			return ResponseConstructors.filterJsonForFieldAndViews(
					includeFields, view, wins);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the leadership wins for geography "
							+ geography);
		}
	}

	/**
	 * This Controller retrieves Opportunities Won based on the user (SI, Geo
	 * Heads, IOU Heads)
	 * 
	 * @param geography
	 * @param fromDate
	 * @param toDate
	 * @param includeFields
	 * @param view
	 * @return 
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/leadership/opp", method = RequestMethod.GET)
	public String getLeadershipOpportunitiesByGeography(
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "fromDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam(value = "toDate", defaultValue = "01012099") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam(value="searchedUserId",defaultValue="")String searchedUserId,
			@RequestParam(value="teamFlag",defaultValue="false")boolean teamFlag,
            @RequestParam(value = "fields", defaultValue = "all") String includeFields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of retrieving the leadership opportunities by geography");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		LeadershipOpportunitiesDTO opportunities = null;
		try {
			opportunities = dashboardService
					.getLeadershipOpportunitiesByGeography(userId, fromDate,
							toDate, geography,searchedUserId,teamFlag);
			logger.info("End of retrieving the leadership opportunities by geography");
			return ResponseConstructors.filterJsonForFieldAndViews(
					includeFields, view, opportunities);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the leadership opportunities for geography "
							+ geography);
		}
	}

}
