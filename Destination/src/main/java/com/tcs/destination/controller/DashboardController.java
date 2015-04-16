package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.PerformaceChartBean;
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

}
