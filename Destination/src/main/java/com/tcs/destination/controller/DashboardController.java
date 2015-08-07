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
import com.tcs.destination.bean.LeadershipOverallWinsDTO;
import com.tcs.destination.bean.LeadershipWinsDTO;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.PerformaceChartBean;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.DashBoardService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

	@Autowired
	DashBoardService dashboardService;

	private static final Logger logger = LoggerFactory
			.getLogger(DashboardController.class);

	@RequestMapping(value = "/chart", method = RequestMethod.GET)
	public String chart(
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		PerformaceChartBean chartValues = dashboardService.getChartValues(
				userId, financialYear);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				chartValues);
	}
	
	/**
	 * This controller retrieves the details of performance details of all the users under a supervisor
	 * 
	 * @param supervisorId
	 * @param financialYear
	 * @param fields
	 * @param view
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/teamchart", method = RequestMethod.GET)
	public String teamChart(
			@RequestParam(value = "supervisorId") String supervisorId,
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside ConnectController /dashboard/teamchart" + "GET");
		PerformaceChartBean chartValues = null;
		try {
			chartValues = dashboardService.getTeamChartValues(supervisorId,
					financialYear);
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				chartValues);
	}
	
	/**
	 * This Controller retrieves a list of Connects based on the user (SI, Geo Heads, IOU Heads)
	 * 
	 * @param userId
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
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "fromDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam(value = "toDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam(value = "fields", defaultValue = "all") String includeFields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside CustomerController /dashboard/leadership/connect GET");
		LeadershipConnectsDTO connects = null;
		try {
		connects = dashboardService.getLeadershipConnectsByGeography(userId, fromDate, toDate, geography);
		}
		catch(Exception e){
		    logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		return ResponseConstructors.filterJsonForFieldAndViews(includeFields,
				view, connects);
	}
	
	/**
	 * This Controller retrieves Opportunities Won based on the user (SI, Geo Heads, IOU Heads)
	 * 
	 * @param userId
	 * @param geography
	 * @param fromDate
	 * @param toDate
	 * @param includeFields
	 * @param view
	 * @return String
	 * @throws Exception
	 */
	@RequestMapping(value = "/leadership/wins", method = RequestMethod.GET)
	public String getLeadershipWinsByGeography(
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "fromDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam(value = "toDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam(value = "fields", defaultValue = "all") String includeFields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside CustomerController /dashboard/leadership/wins GET");
		LeadershipOverallWinsDTO wins = null;
		try {
		    wins = dashboardService.getLeadershipWinsByGeography(userId, fromDate, toDate, geography);
		}
		catch(Exception e){
		    logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		return ResponseConstructors.filterJsonForFieldAndViews(includeFields,
				view, wins);
	}

}
