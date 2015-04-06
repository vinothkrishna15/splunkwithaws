package com.tcs.destination.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.service.OpportunityService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/opportunity")
public class OpportunityController {
	// @Autowired
	// OpportunityRepository opportunityRepository;

	@Autowired
	OpportunityService opportunityService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@RequestParam("nameWith") String nameWith,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		OpportunityT opportunity = opportunityService
				.findByOpportunityName(nameWith);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, opportunity);
	}

	@RequestMapping(value = "/recent", method = RequestMethod.GET)
	public @ResponseBody String findByCustomerId(
			@RequestParam("customerId") String customerId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		List<OpportunityT> opportunities = opportunityService
				.findRecentOpportunities(customerId);
		return ResponseConstructors
				.filterJsonForFieldAndViews(fields, view, opportunities);
	}

	@RequestMapping(value = "/taskOwner", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findByTaskOwner(
			@RequestParam("id") String taskOwner,
			@RequestParam(value="role",defaultValue="ALL") String opportunityRole,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception {

		List<OpportunityT> opportunities = opportunityService
				.findByTaskOwnerForRole(taskOwner,opportunityRole);
		return new ResponseEntity<String>(ResponseConstructors
				.filterJsonForFieldAndViews(fields, view, opportunities),HttpStatus.OK);
		
		
	}

}
