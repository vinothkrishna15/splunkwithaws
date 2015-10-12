package com.tcs.destination.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.BDMPerfromanceGeoIouDashboardResponse;
import com.tcs.destination.bean.BDMSupervisorDashboardDTO;
import com.tcs.destination.bean.DashBoardBDMResponse;
import com.tcs.destination.bean.LeadershipOpportunitiesDTO;
import com.tcs.destination.bean.LeadershipOverallWinsDTO;
import com.tcs.destination.service.BDMService;
import com.tcs.destination.service.DashBoardService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/widget")
public class BDMController {

	private static final Logger logger = LoggerFactory.getLogger(BDMController.class);

	@Autowired
	BDMService bdmService;

	@Autowired
	DashBoardService dashboardService;

	/**
	 * This Controller retrieves the BDM Performance Details
	 * 
	 * @param supervisorUserId
	 * @param fields
	 * @param view
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/bdmPerformance", method = RequestMethod.GET)
	public @ResponseBody String findBDMDashBoardDetailsByUserId(
			@RequestParam("userId") String userId,
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "isDashboardByYear", defaultValue = "true") boolean isDashboardByYear,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {

		logger.debug("Inside BDMController /bdmPerformance?userId=" + userId + " GET");

		DashBoardBDMResponse dashBoardBDMResponse = null;

		dashBoardBDMResponse = bdmService.getOpportunityWinsByBDM(userId, financialYear, isDashboardByYear);
		
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, dashBoardBDMResponse);
	}

	/**
	 * This Controller retrieves the BDM Supervisors Performance Details
	 * @param userId
	 * @param financialYear
	 * @param isDashboardByYear
	 * @param fields
	 * @param view
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/supervisorPerformance", method = RequestMethod.GET)
	public @ResponseBody String findBDMSupervisorDashBoardDetailsByUserId(
			@RequestParam("userId") String userId,
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "isAlongWithSupervisor", defaultValue = "true") boolean isAlongWithSupervisor,
			@RequestParam(value = "isDashboardByYear", defaultValue = "true") boolean isDashboardByYear,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {

		logger.debug("Inside BDMController /supervisorPerformance?userId=" + userId + " GET");

		BDMSupervisorDashboardDTO bdmSupervisorDashboardResponse = null;

		bdmSupervisorDashboardResponse = bdmService.getBDMSupervisorByUserId(userId, financialYear, isDashboardByYear, isAlongWithSupervisor);
		
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, bdmSupervisorDashboardResponse);
	}

	
	
	/**
	 * This Controller retrieves the Geo Or Iou Heads Performance Widget
	 * @param userId
	 * @param financialYear
	 * @param isDashboardByYear
	 * @param fields
	 * @param view
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/geoIouHeadPerformance", method = RequestMethod.GET)
	public @ResponseBody String findBDMPerformanceByGeoIouDashBoardDetailsByUserId(
			@RequestParam("userId") String userId,
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "isDashboardByYear", defaultValue = "true") boolean isDashboardByYear,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {

		logger.debug("Inside BDMController /geoIouHeadPerformance?userId=" + userId + " GET");

		BDMPerfromanceGeoIouDashboardResponse bdmPerfromanceGeoIouDashboardResponse = null;

		bdmPerfromanceGeoIouDashboardResponse = bdmService.getGeoIouPerformanceDashboard(userId, financialYear, isDashboardByYear);
		
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, bdmPerfromanceGeoIouDashboardResponse);
	}
	
}
