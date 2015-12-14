package com.tcs.destination.controller;

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

import com.tcs.destination.bean.LeadershipConnectsDTO;
import com.tcs.destination.bean.LeadershipOpportunitiesDTO;
import com.tcs.destination.bean.LeadershipOverallWinsDTO;
import com.tcs.destination.bean.PerformaceChartBean;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.DashBoardService;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

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
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of retrieving a list of Connects based on the user (SI, Geo Heads, IOU Heads)");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		LeadershipConnectsDTO connects = null;
		try {
			connects = dashboardService.getLeadershipConnectsByGeography(
					userId, fromDate, toDate, geography);
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
			@RequestParam(value = "fields", defaultValue = "all") String includeFields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of retrieving the leadership wins by geography");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		LeadershipOverallWinsDTO wins = null;
		try {
			wins = dashboardService.getLeadershipWinsByGeography(userId,
					fromDate, toDate, geography);
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
			@RequestParam(value = "fields", defaultValue = "all") String includeFields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of retrieving the leadership opportunities by geography");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		LeadershipOpportunitiesDTO opportunities = null;
		try {
			opportunities = dashboardService
					.getLeadershipOpportunitiesByGeography(userId, fromDate,
							toDate, geography);
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
