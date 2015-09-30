package com.tcs.destination.controller;

import java.util.Date;
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
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.ReportsOpportunity;
import com.tcs.destination.bean.SubSpReport;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.exception.DestinationException;
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
			@RequestParam(value = "displayGeography", defaultValue = "", required = false) String displayGeography,
			@RequestParam(value = "geography", defaultValue = "", required = false) String geography,
			@RequestParam(value = "serviceline", defaultValue = "", required = false) String serviceLine,
			@RequestParam(value = "iou", defaultValue = "", required = false) String iou,
			@RequestParam(value = "wins", defaultValue = "false") boolean wins,
			@RequestParam(value = "customer", defaultValue = "", required = false) String customerName,
			@RequestParam(value = "groupCustomer", defaultValue = "", required = false) String groupCustomer,
			@RequestParam(value = "currency", defaultValue = "INR", required = false) String currency,
			@RequestParam(value = "fields", defaultValue = "all", required = false) String fields,
			@RequestParam(value = "view", defaultValue = "", required = false) String view)
			throws Exception {
		List<TargetVsActualResponse> response = perfService
				.getTargetVsActualRevenueSummary(financialYear, quarter,
						displayGeography, geography, serviceLine, iou,
						customerName, currency, groupCustomer, wins);
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
			@RequestParam(value = "stagefrom", defaultValue = "-1") int salesStageFrom,
			@RequestParam(value = "stageto", defaultValue = "-1") int salesStageTo,
			@RequestParam(value = "currency", defaultValue = "INR") String currency,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		if (financialYear.isEmpty()) {
			financialYear = DateUtils.getCurrentFinancialYear();
		}
		List<IOUReport> iouList = null;
		if (salesStageFrom != salesStageTo || salesStageFrom != -1) {
			iouList = perfService.getOpportunitiesByIOU(financialYear, quarter,
					geography, serviceLine, currency, salesStageFrom,
					salesStageTo);

		} else {
			iouList = perfService.getRevenuesByIOU(financialYear, quarter,
					geography, serviceLine, currency);
		}
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				iouList);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/subsp")
	public @ResponseBody String getSubSp(
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "displayGeography", defaultValue = "", required = false) String displayGeography,
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "iou", defaultValue = "") String iou,
			@RequestParam(value = "customer", defaultValue = "") String customerName,
			@RequestParam(value = "groupCustomer", defaultValue = "") String groupCustomer,
			@RequestParam(value = "currency", defaultValue = "INR") String currency,
			@RequestParam(value = "stagefrom", defaultValue = "-1") int salesStageFrom,
			@RequestParam(value = "stageto", defaultValue = "-1") int salesStageTo,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		if (financialYear.isEmpty()) {
			financialYear = DateUtils.getCurrentFinancialYear();
		}
		List<SubSpReport> subSpList = null;
		if (salesStageFrom != salesStageTo || salesStageFrom != -1) {
			subSpList = perfService.getOpportunitiesBySubSp(financialYear,
					quarter, displayGeography, geography, iou, currency,
					salesStageFrom, salesStageTo);

		} else {
			subSpList = perfService.getRevenuesBySubSp(financialYear, quarter,
					displayGeography, geography, customerName, iou, currency,
					groupCustomer);
		}
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				subSpList);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/geo")
	public @ResponseBody String getGeo(
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "displayGeography", defaultValue = "") String displayGeography,
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "iou", defaultValue = "") String iou,
			@RequestParam(value = "stagefrom", defaultValue = "-1") int salesStageFrom,
			@RequestParam(value = "stageto", defaultValue = "-1") int salesStageTo,
			@RequestParam(value = "serviceline", defaultValue = "") String serviceLine,
			@RequestParam(value = "customer", defaultValue = "") String customerName,
			@RequestParam(value = "groupCustomer", defaultValue = "") String groupCustomer,
			@RequestParam(value = "currency", defaultValue = "INR") String currency,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		List<GeographyReport> geoList = null;
		if (displayGeography.equals("") && geography.isEmpty()) {

			if (salesStageFrom != salesStageTo || salesStageFrom != -1) {
				if (financialYear.isEmpty() && quarter.isEmpty()) {
					financialYear = DateUtils.getCurrentFinancialYear();
				}
				geoList = perfService.getOpportunitiesByDispGeography(
						financialYear, quarter, serviceLine, iou, currency,
						salesStageFrom, salesStageTo);
			} else {
				if (financialYear.isEmpty()) {
					financialYear = DateUtils.getCurrentFinancialYear();
				}
				geoList = perfService.getRevenuesByDispGeography(financialYear,
						quarter, customerName, serviceLine, iou, currency,
						groupCustomer);
			}

		} else {
			if (financialYear.isEmpty()) {
				financialYear = DateUtils.getCurrentFinancialYear();
			}
			if (salesStageFrom != salesStageTo || salesStageFrom != -1) {
				geoList = perfService.getOpportunitiesBySubGeography(
						financialYear, quarter, customerName, serviceLine, iou,
						displayGeography, geography, currency, salesStageFrom,
						salesStageTo, groupCustomer);
			} else {
				geoList = perfService.getRevenuesBySubGeography(financialYear,
						quarter, customerName, serviceLine, iou,
						displayGeography, geography, currency, groupCustomer);
			}
		}
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				geoList);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/opportunity")
	public @ResponseBody String getOpportunities(
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "displayGeography", defaultValue = "") String geography,
			@RequestParam(value = "iou", defaultValue = "") String iou,
			@RequestParam(value = "serviceline", defaultValue = "") String serviceLine,
			@RequestParam(value = "currency", defaultValue = "INR") String currency,
			@RequestParam(value = "stagefrom", defaultValue = "-1") int salesStageFrom,
			@RequestParam(value = "stageto", defaultValue = "-1") int salesStageTo,
			@RequestParam(value = "customer", defaultValue = "") String customerName,
			@RequestParam(value = "groupCustomer", defaultValue = "") String groupCustomer,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		ReportsOpportunity reportsOpportunity = perfService.getOpportunity(
				financialYear, quarter, geography, iou, serviceLine, currency,
				salesStageFrom,salesStageTo, customerName, groupCustomer);
		return ResponseConstructors.filterJsonForFieldAndViews("all", "",
				reportsOpportunity);

	}

	@RequestMapping(method = RequestMethod.GET, value = "/topopps")
	public @ResponseBody String getTopOpportunities(
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "iou", defaultValue = "") String iou,
			@RequestParam(value = "serviceline", defaultValue = "") String serviceLine,
			@RequestParam(value = "stagefrom", defaultValue = "4") int salesStageFrom,
			@RequestParam(value = "stageto", defaultValue = "8") int salesStageTo,
			@RequestParam(value = "currency", defaultValue = "INR") String currency,
			@RequestParam(value = "count", defaultValue = "3") int count,
			@RequestParam(value = "customer", defaultValue = "") String customerName,
			@RequestParam(value = "groupCustomer", defaultValue = "") String groupCustomer,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		Date startDate, endDate;
		if (financialYear.isEmpty() && quarter.isEmpty()) {
			financialYear = DateUtils.getCurrentFinancialYear();
			startDate = DateUtils.getDateFromFinancialYear(financialYear, true);
			endDate = DateUtils.getDateFromFinancialYear(financialYear, false);
		} else if (financialYear.isEmpty() && !quarter.isEmpty()) {
			startDate = DateUtils.getDateFromQuarter(quarter, true);
			endDate = DateUtils.getDateFromQuarter(quarter, false);
		} else if (!financialYear.isEmpty() && quarter.isEmpty()) {
			startDate = DateUtils.getDateFromFinancialYear(financialYear, true);
			endDate = DateUtils.getDateFromFinancialYear(financialYear, false);
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Request - year and quarter cannot be set together");
		}
		List<OpportunityT> oppList = perfService.getTopOpportunities(currency,
				geography, salesStageFrom, salesStageTo, serviceLine, iou,
				startDate, endDate, count, customerName, groupCustomer);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				oppList);

	}

}
