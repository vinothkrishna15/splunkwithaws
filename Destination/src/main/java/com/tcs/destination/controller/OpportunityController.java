package com.tcs.destination.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.service.OpportunityService;
import com.tcs.destination.utils.Constants;

@RestController
@RequestMapping("/opportunity")
public class OpportunityController {
//	@Autowired
//	OpportunityRepository opportunityRepository;
	
	@Autowired
	OpportunityService opportunityService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@RequestParam("nameWith") String nameWith,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) {
		OpportunityT opportunity = opportunityService.findByOpportunityName(nameWith);
		return Constants.filterJsonForFieldAndViews(fields, view, opportunity);
	}
	
	@RequestMapping(value="/recent",method= RequestMethod.GET)
	public @ResponseBody String toFetchOpportunityUsingCustomerId(
			@RequestParam("customerId") String customerId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) 	{
        List<OpportunityT> opportunities = opportunityService.findRecentOpportunities(customerId);
		return Constants.filterJsonForFieldAndViews(fields, view, opportunities);
		
	}
}
