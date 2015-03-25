package com.tcs.destination.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.OfferingMappingT;
import com.tcs.destination.service.OfferingService;
import com.tcs.destination.utils.Constants;

@RestController
@RequestMapping("/offering")
public class OfferingController {

	@Autowired
	OfferingService offeringService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAll(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) {
		ArrayList<OfferingMappingT> offeringMapping = (ArrayList<OfferingMappingT>) offeringService
				.findAll();
		return Constants.filterJsonForFieldAndViews(fields, view,
				offeringMapping);
	}

}
