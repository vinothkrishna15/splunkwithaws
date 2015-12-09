package com.tcs.destination.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.CityService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/city")
public class CityController {
	
	private static final Logger logger = LoggerFactory.getLogger(CityController.class);
	
	@Autowired 
	CityService cityService;
	
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAll(
	@RequestParam(value = "nameWith") String nameWith,	
	@RequestParam(value = "fields", defaultValue = "all") String fields,
	@RequestParam(value = "view", defaultValue = "") String view) throws DestinationException
	{
		logger.debug("Inside CityController GET");
		logger.info("Inside CityController : Start of retrieving the city names");
		List<String> cityNameList=new ArrayList<String>();
		try {
		cityNameList= cityService.getCityByCityName(nameWith);
		logger.info("Inside CityController : End of retrieving the city names");
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				cityNameList);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the city name for :" + nameWith);
		}
	}

}
