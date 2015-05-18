package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.service.PerformanceReportService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/perfreport")
public class PerformanceReportController {

	private static final Logger logger = LoggerFactory
			.getLogger(PerformanceReportController.class);

	@Autowired
	private PerformanceReportService perfReportService;

	@RequestMapping(value = "/revenue", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getActualRevenue(
			@RequestParam(value = "year", defaultValue = "",required=false) String financialYear,
			@RequestParam(value = "quarter", defaultValue = "",required=false) String quarter,
			@RequestParam(value = "geography", defaultValue = "",required=false) String geography,
			@RequestParam(value = "serviceLine", defaultValue = "",required=false) String serviceLine,
			@RequestParam(value = "iou", defaultValue = "",required=false) String iou,
			@RequestParam(value = "customer", defaultValue = "",required=false) String customerName,
			@RequestParam(value = "currency", defaultValue = "USD",required=false) String currency,
			@RequestParam(value = "fields", defaultValue = "all",required=false) String fields,
			@RequestParam(value = "view", defaultValue = "",required=false) String view)
			throws Exception {

		List<TargetVsActualResponse> response = perfReportService
				.getTargetVsActualRevenueSummary(financialYear, quarter, geography,
						serviceLine, iou, customerName, currency);
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews(fields, view,
						response), HttpStatus.OK);

	}

}
