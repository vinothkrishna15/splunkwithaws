package com.tcs.destination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.GeographyCountryMappingT;
import com.tcs.destination.service.CountryService;
import com.tcs.destination.utils.Constants;

import java.util.ArrayList;

@RestController
@RequestMapping("/country")
public class CountryController {
	
	@Autowired 
	CountryService countryService;
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAll(
	@RequestParam(value = "fields", defaultValue = "all") String fields,
	@RequestParam(value = "view", defaultValue = "") String view)
	{
		ArrayList<GeographyCountryMappingT> geographyCountryMapping=new ArrayList<GeographyCountryMappingT>();
		geographyCountryMapping=(ArrayList<GeographyCountryMappingT>) countryService.findAll();
		return Constants.filterJsonForFieldAndViews(fields, view,
				geographyCountryMapping);
	}

}
