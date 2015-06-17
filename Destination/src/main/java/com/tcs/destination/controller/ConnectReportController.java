package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.ConnectSummaryResponse;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.service.ConnectReportService;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * Controller to handle connection details search requests.
 * 
 */
@RestController
@RequestMapping("/report")
public class ConnectReportController {

	private static final Logger logger = LoggerFactory
			.getLogger(ConnectReportController.class);

	@Autowired
	ConnectReportService connectReportService;

	@RequestMapping(value = "/connect/detailed", method = RequestMethod.GET)
	public @ResponseBody String connectDetailedReport(
			@RequestParam(value = "month", defaultValue = "") String month,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "year", defaultValue = "") String year,
			@RequestParam(value = "iou", defaultValue = "") List<String> iou,
			@RequestParam(value = "geography", defaultValue = "") List<String> geography,
			@RequestParam(value = "country", defaultValue = "") List<String> country,
			@RequestParam(value = "serviceLines", defaultValue = "") List<String> serviceLines,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside ConnectReportController /report/connect/detailed GET");
		List<ConnectT> connects = connectReportService
				.getConnectDetailedReports(month, quarter, year, iou,
						geography, country, serviceLines);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				connects);

	}

	@RequestMapping(value = "/connect/summary/{required}", method = RequestMethod.GET)
	public @ResponseBody String connectSummaryReport(
			@PathVariable("required") String required,
			@RequestParam(value = "month", defaultValue = "") String month,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "year", defaultValue = "") String year,
			@RequestParam(value = "iou", defaultValue = "") List<String> iou,
			@RequestParam(value = "geography", defaultValue = "") List<String> geography,
			@RequestParam(value = "country", defaultValue = "") List<String> country,
			@RequestParam(value = "serviceLines", defaultValue = "") List<String> serviceLines,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside ConnectReportController /report/connect/summary GET");
		List<ConnectSummaryResponse> connectSummaryResponses = connectReportService
				.getSummaryReports(required, month, quarter, year, iou,
						geography, country, serviceLines);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				connectSummaryResponses);

	}

}
