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

import com.tcs.destination.bean.OfferingMappingT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.OfferingService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/offering")
public class OfferingController {
	
	private static final Logger logger = LoggerFactory.getLogger(OfferingController.class);

	@Autowired
	OfferingService offeringService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAll(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws DestinationException{
		try {
		logger.debug("Inside OfferingController /offering GET");
		ArrayList<OfferingMappingT> offeringMapping = (ArrayList<OfferingMappingT>) offeringService
				.findAll();
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				offeringMapping);
		} catch (DestinationException e) {
		    logger.error("Destination Exception" + e.getMessage());
		    throw e;
		} catch (Exception e) {
		    logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
		    throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
			    "Backend Error while retrieving offering details");
		}
	}

}
