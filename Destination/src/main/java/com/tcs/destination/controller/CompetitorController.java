package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.CompetitorMappingT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.service.CompetitorService;
import com.tcs.destination.service.CustomerService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/competitor")
public class CompetitorController {
	
	private static final Logger logger = LoggerFactory.getLogger(CompetitorController.class);

	@Autowired
	CompetitorService compService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findNameWith(
			@RequestParam("nameWith") String chars,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception {
		logger.debug("Inside CompetitorController /competitor?namewith="+chars+" GET");
		List<CompetitorMappingT> compList = compService
				.findByNameContaining(chars);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, compList);
	}

}
