package com.tcs.destination.controller;

import java.sql.Timestamp;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(TrendingController.class);
	
	@Autowired
	TrendingService trendService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getAll(
			@RequestParam(value = "count",defaultValue = "10") String count,
			@RequestParam(value= "token",defaultValue="") String token,
			@RequestParam(value = "entity",defaultValue = "all") String entityType,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "timeline") String view) throws Exception{
		logger.info("Timeline Request Received : token - " + token + " , count - " + count + " , view - " + view);
		Timestamp timeStamp;
//		Integer p = Integer.parseInt(page);
		Integer c = Integer.parseInt(count);
		if(token.equalsIgnoreCase("")){
			timeStamp=new Timestamp(Calendar.getInstance().getTimeInMillis());
		} else {
			Calendar cal=Calendar.getInstance();
			cal.setTimeInMillis(Long.parseLong(token));
			timeStamp=new Timestamp(cal.getTimeInMillis());
		}
		logger.info("Derived Timestamp(token) - " + timeStamp);
		return new ResponseEntity<String>(ResponseConstructors
				.filterJsonForFieldAndViews(fields, view, trendService.getDistinctComment(timeStamp,c.intValue(),entityType)),HttpStatus.OK);
		
	}

	@RequestMapping(value = "/opportunity", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getOpportunities(
			@RequestParam(value = "count", defaultValue = "25") String count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		Integer i = Integer.parseInt(count);
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews(fields, view,
						trendService.findtrendingOpportunities(i.intValue())),
				HttpStatus.OK);

	}

}
