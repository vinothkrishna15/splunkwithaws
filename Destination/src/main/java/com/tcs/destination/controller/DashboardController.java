package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
		logger.debug("Inside ConnectController /connect/teamchart" + "GET");
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

}
