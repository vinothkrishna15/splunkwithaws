package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.OpportunityService;
import com.tcs.destination.service.PartnerService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/partner")
public class PartnerController {

	private static final Logger logger = LoggerFactory.getLogger(PartnerController.class);
	
	@Autowired
	PartnerService partnerService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@PathVariable("id") String partnerid,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		logger.debug("Inside PartnerController /partner/id="+partnerid+" GET");
		PartnerMasterT partner = partnerService.findById(partnerid);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, partner);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findByNameContaining(
			@RequestParam(value = "nameWith", defaultValue = "") String nameWith,
			@RequestParam(value = "startsWith", defaultValue = "") String startsWith,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception {
		logger.debug("Inside PartnerController /partner?nameWith="+nameWith+" GET");
		List<PartnerMasterT> partners = null;
		
		if (!nameWith.isEmpty()) {
			partners = partnerService.findByNameContaining(nameWith);
		} else if (!startsWith.isEmpty()) {
			partners = partnerService.findByNameStarting(startsWith);
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Either nameWith / startsWith is required");
		}
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, partners);
	}
}
