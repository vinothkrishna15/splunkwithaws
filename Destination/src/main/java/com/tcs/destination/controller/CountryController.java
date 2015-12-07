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

import com.tcs.destination.bean.GeographyCountryMappingT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.CountryService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/country")
public class CountryController {
	
	private static final Logger logger = LoggerFactory.getLogger(CountryController.class);
	
	@Autowired 
	CountryService countryService;
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAll(
	@RequestParam(value = "fields", defaultValue = "all") String fields,
	@RequestParam(value = "view", defaultValue = "") String view) throws DestinationException
	{
		logger.debug("Inside CountryController /country GET");
		logger.info("Start of retrieving the Geography Country Mapping");
		ArrayList<GeographyCountryMappingT> geographyCountryMapping=new ArrayList<GeographyCountryMappingT>();
		try {
		geographyCountryMapping=(ArrayList<GeographyCountryMappingT>) countryService.findAll();
		logger.info("End of retrieving the Geography Country Mapping");
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				geographyCountryMapping);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Geography Country Mapping");
	   }
	}

}
