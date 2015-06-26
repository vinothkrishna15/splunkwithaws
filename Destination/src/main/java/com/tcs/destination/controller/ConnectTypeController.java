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

import com.tcs.destination.bean.ConnectTypeMappingT;
import com.tcs.destination.service.ConnectTypeService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/connecttype")
public class ConnectTypeController {
	
	private static final Logger logger = LoggerFactory.getLogger(ConnectTypeController.class);

	@Autowired
	ConnectTypeService conTypeService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAll(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		logger.debug("Inside connectTypeController /connecttype GET");
		ArrayList<ConnectTypeMappingT> connectTypeMapping = (ArrayList<ConnectTypeMappingT>) conTypeService
				.findAll();
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				connectTypeMapping);
	}

}
