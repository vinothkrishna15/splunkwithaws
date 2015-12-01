package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.BDMPerfromanceGeoIouDashboardResponse;
import com.tcs.destination.bean.BDMSupervisorDashboardDTO;
import com.tcs.destination.bean.DashBoardBDMResponse;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.BDMService;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/widget")
public class BDMController {

	private static final Logger logger = LoggerFactory
			.getLogger(BDMController.class);

	@Autowired
	BDMService bdmService;

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
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "isDashboardByYear", defaultValue = "true") boolean isDashboardByYear,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {

		logger.debug("Inside BDMController /bdmPerformance GET");
		logger.info("Inside BDMController : Start of retrieving BDM Dashboard details");

		DashBoardBDMResponse dashBoardBDMResponse = null;
		try {
			dashBoardBDMResponse = bdmService.getOpportunityWinsByBDM(
					financialYear, isDashboardByYear);
			logger.info("Inside BDMController : End of retrieving BDM Dashboard details");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, dashBoardBDMResponse);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the BDM Dashboard details");
		}
	}

	/**
	 * This Controller retrieves the BDM Supervisors Performance Details
	 * 
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
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "isAlongWithSupervisor", defaultValue = "true") boolean isAlongWithSupervisor,
			@RequestParam(value = "isDashboardByYear", defaultValue = "true") boolean isDashboardByYear,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {

		logger.debug("Inside BDMController /supervisorPerformance GET");
		logger.info("Inside BDMController : Start of retrieving BDM Supervisor Dashboard details");
		BDMSupervisorDashboardDTO bdmSupervisorDashboardResponse = null;
		try {
			bdmSupervisorDashboardResponse = bdmService
					.getBDMSupervisorByUserId(financialYear, isDashboardByYear,
							isAlongWithSupervisor);
			logger.info("Inside BDMController : End of retrieving BDM Supervisor Dashboard details");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, bdmSupervisorDashboardResponse);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the BDM Supervisor dashboard details");
		}
	}

	/**
	 * This Controller retrieves the Geo Or Iou Heads Performance Widget
	 * 
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
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "isDashboardByYear", defaultValue = "true") boolean isDashboardByYear,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {

		logger.debug("Inside BDMController /geoIouHeadPerformance GET");
		logger.info("Inside BDMController : Start of retrieving the Dashboard details for BDM Performance by Geo IOU");
		BDMPerfromanceGeoIouDashboardResponse bdmPerfromanceGeoIouDashboardResponse = null;
		try {
			bdmPerfromanceGeoIouDashboardResponse = bdmService
					.getGeoIouPerformanceDashboard(financialYear,
							isDashboardByYear);
			logger.info("Inside BDMController : End of retrieving the Dashboard details for BDM Performance by Geo IOU");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, bdmPerfromanceGeoIouDashboardResponse);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(
					HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the dashboard details for BDM Performance by Geo IOU");
		}
	}

}
