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

import com.tcs.destination.bean.GeographyReport;
import com.tcs.destination.bean.IOUReport;
import com.tcs.destination.bean.SubSpReport;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.service.PerformanceReportService;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/perfreport")
public class PerformanceReportController {

	private static final Logger logger = LoggerFactory
			.getLogger(PerformanceReportController.class);

	@Autowired
	PerformanceReportService perfService;

	@RequestMapping(value = "/revenue", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getActualRevenue(
			@RequestParam(value = "year", defaultValue = "", required = false) String financialYear,
			@RequestParam(value = "quarter", defaultValue = "", required = false) String quarter,
			@RequestParam(value = "geography", defaultValue = "", required = false) String geography,
			@RequestParam(value = "serviceLine", defaultValue = "", required = false) String serviceLine,
			@RequestParam(value = "iou", defaultValue = "", required = false) String iou,
			@RequestParam(value = "customer", defaultValue = "", required = false) String customerName,
			@RequestParam(value = "currency", defaultValue = "USD", required = false) String currency,
			@RequestParam(value = "fields", defaultValue = "all", required = false) String fields,
			@RequestParam(value = "view", defaultValue = "", required = false) String view)
			throws Exception {

		List<TargetVsActualResponse> response = perfService
				.getTargetVsActualRevenueSummary(financialYear, quarter,
						geography, serviceLine, iou, customerName, currency);
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews(fields, view,
						response), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/iou")
	public @ResponseBody String getIOU(
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "serviceLine", defaultValue = "") String serviceLine,
			@RequestParam(value = "currency", defaultValue = "USD") String currency,
			@RequestParam(value = "pipelines", defaultValue = "") String pipelines,
			@RequestParam(value = "wins", defaultValue = "") String wins,
			@RequestParam(value = "connects", defaultValue = "") String connects,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		if (financialYear.isEmpty()) {
			financialYear = DateUtils.getCurrentFinancialYear();
		}
		List<IOUReport> iouList = perfService.getRevenuesByIOU(financialYear,
				quarter, geography, serviceLine, currency);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				iouList);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/subsp")
	public @ResponseBody String getSubSp(
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "iou", defaultValue = "") String iou,
			@RequestParam(value = "customer", defaultValue = "") String customerName,
			@RequestParam(value = "currency", defaultValue = "USD") String currency,
			@RequestParam(value = "pipelines", defaultValue = "") String pipelines,
			@RequestParam(value = "wins", defaultValue = "") String wins,
			@RequestParam(value = "connects", defaultValue = "") String connects,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		if (financialYear.isEmpty()) {
			financialYear = DateUtils.getCurrentFinancialYear();
		}
		List<SubSpReport> subSpList = perfService.getRevenuesBySubSp(
				financialYear, quarter, geography, customerName, iou, currency);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				subSpList);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/geo")
	public @ResponseBody String getGeo(
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "geography",defaultValue = "") String geography,
			@RequestParam(value = "iou", defaultValue = "") String iou,
			@RequestParam(value = "subSp", defaultValue = "") String serviceLine,
			@RequestParam(value = "customer", defaultValue = "") String customerName,
			@RequestParam(value = "currency", defaultValue = "USD") String currency,
			@RequestParam(value = "pipelines", defaultValue = "") String pipelines,
			@RequestParam(value = "wins", defaultValue = "") String wins,
			@RequestParam(value = "connects", defaultValue = "") String connects,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		if (geography.equals("")) {

			if (financialYear.isEmpty()) {
				financialYear = DateUtils.getCurrentFinancialYear();
			}
			List<GeographyReport> dispGeoList = perfService
					.getRevenuesByDispGeography(financialYear, quarter,
							customerName, serviceLine, iou, currency);
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, dispGeoList);

		} else {
			if (financialYear.isEmpty()) {
				financialYear = DateUtils.getCurrentFinancialYear();
			}
			List<GeographyReport> subGeoList = perfService
					.getRevenuesBySubGeography(financialYear, quarter,
							customerName, serviceLine, iou, geography, currency);
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, subGeoList);
		}
	}
}
