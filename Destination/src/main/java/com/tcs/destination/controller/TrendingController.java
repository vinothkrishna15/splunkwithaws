package com.tcs.destination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.service.TrendingService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/trending")
public class TrendingController {
	
	@Autowired
	TrendingService trendService;
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getAll(
			@RequestParam(value = "count",defaultValue = "10") String count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		Integer i = Integer.parseInt(count);
		return new ResponseEntity<String>(ResponseConstructors
				.filterJsonForFieldAndViews(fields, view, trendService.getDistinctComment(i.intValue())),HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/opportunity", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getOpportunities(@RequestParam(value = "count",defaultValue = "25") String count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		Integer i = Integer.parseInt(count);
		return new ResponseEntity<String>(ResponseConstructors
				.filterJsonForFieldAndViews(fields, view, trendService.findtrendingOpportunities(i.intValue())),HttpStatus.OK);
		
	}
	
	

}
