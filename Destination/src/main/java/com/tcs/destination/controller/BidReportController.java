package com.tcs.destination.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.service.BidReportService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/report")
public class BidReportController {

	private static final Logger logger = LoggerFactory
			.getLogger(BidReportController.class);

	@Autowired
	BidReportService bidReportService;

	@RequestMapping(value = "/biddetails", method = RequestMethod.GET)
	public @ResponseBody String report(
			@RequestParam(value = "year", defaultValue = "") String year,
			@RequestParam(value="from",defaultValue="")@DateTimeFormat(iso = ISO.DATE) Date fromDate,
			@RequestParam(value="to",defaultValue="") @DateTimeFormat(iso = ISO.DATE) Date toDate,
			@RequestParam(value = "bidOwner", defaultValue = "") List<String> bidOwner,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "iou", defaultValue = "") List<String> iou,
			@RequestParam(value = "geography", defaultValue = "") List<String> geography,
			@RequestParam(value = "country", defaultValue = "") List<String> country,
			@RequestParam(value = "serviceLines", defaultValue = "") List<String> serviceLines,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside BidReportController /report/biddetails GET");
		List<BidDetailsT> biddetails = bidReportService.getBidDetailedReport(year, fromDate, toDate,bidOwner, currency,
				iou, geography, country,serviceLines);

		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				biddetails);

	}
}