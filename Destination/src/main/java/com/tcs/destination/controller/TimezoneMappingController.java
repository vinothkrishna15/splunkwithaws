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

import com.tcs.destination.bean.TimeZoneMappingT;
import com.tcs.destination.service.TimezoneMappingService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/timezone")
public class TimezoneMappingController {

	private static final Logger logger = LoggerFactory
			.getLogger(TimezoneMappingController.class);

	@Autowired
	TimezoneMappingService timezoneMappingService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAll(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside TimezoneMappingController /timezone GET");
		List<TimeZoneMappingT> timezoneMappingTs = timezoneMappingService
				.findAll();
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				timezoneMappingTs);
	}


}