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

import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.service.IOUService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/iou")
public class IOUController {
	
	private static final Logger logger = LoggerFactory.getLogger(IOUController.class);
	
	@Autowired 
	IOUService iouService;
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAll(
	@RequestParam(value = "fields", defaultValue = "all") String fields,
	@RequestParam(value = "view", defaultValue = "") String view) throws Exception
	{
		logger.debug("Inside IOUController /iou GET");
		ArrayList<IouCustomerMappingT> iouCustomerMappingTs=new ArrayList<IouCustomerMappingT>();
		iouCustomerMappingTs=(ArrayList<IouCustomerMappingT>) iouService.findAll();
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				iouCustomerMappingTs);
	}

}
