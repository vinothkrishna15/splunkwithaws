package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.service.PartnerService;
import com.tcs.destination.service.RecentlyAddedService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/recent")
public class RecentlyAddedController {
	
	private static final Logger logger = LoggerFactory.getLogger(RecentlyAddedController.class);

	@Autowired
	RecentlyAddedService recentlyAddedService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String recentlyAdded(@RequestParam("entityType") String entityType,
			@RequestParam(value="count",defaultValue = "5") int count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		logger.debug("Inside RecentlyAddedController /recent?entityType="+entityType+" GET");
		Object response=recentlyAddedService.recentlyAdded(entityType,count);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, response);
	}
}
