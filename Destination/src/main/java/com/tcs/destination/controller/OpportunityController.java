package com.tcs.destination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.service.OpportunityService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.bean.OpportunityT;

@RestController
@RequestMapping("/search")
public class OpportunityController {
//	@Autowired
//	OpportunityRepository opportunityRepository;
	
	@Autowired
	OpportunityService opportunityService;

	@RequestMapping(value = "/opportunity", method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@RequestParam("nameWith") String opportunityname,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) {
		OpportunityT partner = opportunityService.findByOpportunityName(opportunityname);
		return Constants.filterJsonForFieldAndViews(fields, view, partner);
	}
}
