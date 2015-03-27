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
import com.tcs.destination.bean.Status;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.OpportunityService;
import com.tcs.destination.utils.Constants;

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
			@RequestParam(value = "view", defaultValue = "") String view) {
		OpportunityT opportunity = opportunityService
				.findByOpportunityName(nameWith);
		return Constants.filterJsonForFieldAndViews(fields, view, opportunity);
	}

	@RequestMapping(value = "/recent", method = RequestMethod.GET)
	public @ResponseBody String findByCustomerId(
			@RequestParam("customerId") String customerId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) {
		List<OpportunityT> opportunities = opportunityService
				.findRecentOpportunities(customerId);
		return Constants
				.filterJsonForFieldAndViews(fields, view, opportunities);
	}

	@RequestMapping(value = "/taskOwner", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findByTaskOwner(
			@RequestParam("id") String taskOwner,
			@RequestParam(value="role",defaultValue="ALL") String opportunityRole,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) {
		try{
		List<OpportunityT> opportunities = opportunityService
				.findByTaskOwnerForRole(taskOwner,opportunityRole);
		return new ResponseEntity<String>(Constants
				.filterJsonForFieldAndViews(fields, view, opportunities),HttpStatus.OK);
		}catch(DestinationException de){
			System.out.println("No data found " + de.getMessage());
			return new ResponseEntity<String>
			(Constants.filterJsonForFieldAndViews(fields, view, de.getMessage()), de.getHttpStatus());
		}
		
	}

}
