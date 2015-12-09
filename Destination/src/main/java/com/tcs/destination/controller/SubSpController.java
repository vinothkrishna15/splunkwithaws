package com.tcs.destination.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.RecentlyAddedService;
import com.tcs.destination.service.SubSpService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/subsp")
public class SubSpController {
	
	private static final Logger logger = LoggerFactory.getLogger(SubSpController.class);
	
	@Autowired
	SubSpService subSpService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAll(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws DestinationException{
		logger.info("Start of retrieving the subSps");
		logger.debug("Inside SubSpController /subsp GET");
		try {
		ArrayList<SubSpMappingT> subSpMapping = (ArrayList<SubSpMappingT>) subSpService
				.findAll();
		logger.info("End of retrieving the subSps");
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, subSpMapping);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the SubSps");
	   }
	}
}
