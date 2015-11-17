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
import com.tcs.destination.bean.LeadershipWinsDTO;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.PerformaceChartBean;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.DashBoardService;
import com.tcs.destination.utils.DestinationUtils;
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
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
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
		
			chartValues = dashboardService.getTeamChartValues(supervisorId,
					financialYear);
		
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
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "fromDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam(value = "toDate", defaultValue = "01012099") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam(value = "fields", defaultValue = "all") String includeFields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		logger.debug("Inside CustomerController /dashboard/leadership/connect GET");
		LeadershipConnectsDTO connects = null;
		
		connects = dashboardService.getLeadershipConnectsByGeography(userId, fromDate, toDate, geography);
		
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
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "fromDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam(value = "toDate", defaultValue = "01012099") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam(value = "fields", defaultValue = "all") String includeFields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		logger.debug("Inside CustomerController /dashboard/leadership/wins GET");
		LeadershipOverallWinsDTO wins = null;
		
		    wins = dashboardService.getLeadershipWinsByGeography(userId, fromDate, toDate, geography);
		
		return ResponseConstructors.filterJsonForFieldAndViews(includeFields,
				view, wins);
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
	@RequestMapping(value = "/leadership/opp", method = RequestMethod.GET)
	public String getLeadershipOpportunitiesByGeography(
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "fromDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam(value = "toDate", defaultValue = "01012099") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam(value = "fields", defaultValue = "all") String includeFields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		logger.debug("Inside CustomerController /dashboard/leadership/opp GET");
		LeadershipOpportunitiesDTO opportunities = null;
		
		    opportunities = dashboardService.getLeadershipOpportunitiesByGeography(userId, fromDate, toDate, geography);
		
		return ResponseConstructors.filterJsonForFieldAndViews(includeFields,
				view, opportunities);
	}

}
