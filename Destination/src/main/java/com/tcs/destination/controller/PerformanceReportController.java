package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.GeographyReport;
import com.tcs.destination.bean.IOUReport;
import com.tcs.destination.bean.SubSpReport;
import com.tcs.destination.service.PerformanceReportsService;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/report")
public class PerformanceReportController {

	private static final Logger logger = LoggerFactory.getLogger(PerformanceReportController.class);
	
	@Autowired
	PerformanceReportsService perfService;
	
	@RequestMapping(method = RequestMethod.GET,value = "/iou")
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
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception {
		if(financialYear.isEmpty()){
			financialYear = DateUtils.getCurrentFinancialYear();
		}
		List<IOUReport> iouList = perfService.getRevenuesByIOU(financialYear, quarter, geography, serviceLine,currency);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, iouList);
	}
	
	
	@RequestMapping(method = RequestMethod.GET,value = "/subSp")
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
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception {
		if(financialYear.isEmpty()){
			financialYear = DateUtils.getCurrentFinancialYear();
		}
		List<SubSpReport> subSpList = perfService.getRevenuesBySubSp(financialYear, quarter, geography, customerName, iou, currency);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, subSpList);
	}
	
	
	@RequestMapping(method = RequestMethod.GET,value = "/dispGeo")
	public @ResponseBody String getGeo(
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "iou", defaultValue = "") String iou,
			@RequestParam(value = "subSp", defaultValue = "") String serviceLine,
			@RequestParam(value = "customer", defaultValue = "") String customerName,
			@RequestParam(value = "currency", defaultValue = "USD") String currency,
			@RequestParam(value = "pipelines", defaultValue = "") String pipelines,
			@RequestParam(value = "wins", defaultValue = "") String wins,
			@RequestParam(value = "connects", defaultValue = "") String connects,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception {
		if(financialYear.isEmpty()){
			financialYear = DateUtils.getCurrentFinancialYear();
		}
		List<GeographyReport> dispGeoList = perfService.getRevenuesByDispGeography(financialYear, quarter, customerName, serviceLine, iou, currency);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, dispGeoList);
	}
	
	
	@RequestMapping(method = RequestMethod.GET,value = "/subGeo")
	public @ResponseBody String getGeo(
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "geography") String geography,
			@RequestParam(value = "iou", defaultValue = "") String iou,
			@RequestParam(value = "subSp", defaultValue = "") String serviceLine,
			@RequestParam(value = "customer", defaultValue = "") String customerName,
			@RequestParam(value = "currency", defaultValue = "USD") String currency,
			@RequestParam(value = "pipelines", defaultValue = "") String pipelines,
			@RequestParam(value = "wins", defaultValue = "") String wins,
			@RequestParam(value = "connects", defaultValue = "") String connects,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception {
		if(financialYear.isEmpty()){
			financialYear = DateUtils.getCurrentFinancialYear();
		}
		List<GeographyReport> subGeoList = perfService.getRevenuesBySubGeography(financialYear, quarter, customerName, serviceLine, iou, geography, currency);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, subGeoList);
	}

}
