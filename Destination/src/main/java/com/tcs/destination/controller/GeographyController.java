package com.tcs.destination.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.service.GeographyService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/geography")
public class GeographyController {
	
	private static final Logger logger = LoggerFactory.getLogger(GeographyController.class);
	
	@Autowired 
	GeographyService geographyService;
	
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAll(
	@RequestParam(value = "fields", defaultValue = "all") String fields,
	@RequestParam(value = "view", defaultValue = "") String view) throws Exception
	{
		logger.debug("Inside GeographyController /iou GET");
		ArrayList<GeographyMappingT> geogaraphyMappingTs=new ArrayList<GeographyMappingT>();
		geogaraphyMappingTs=(ArrayList<GeographyMappingT>) geographyService.findAll();
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				geogaraphyMappingTs);
	}

}
