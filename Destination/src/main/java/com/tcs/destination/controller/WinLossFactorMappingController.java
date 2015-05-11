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

import com.tcs.destination.bean.WinLossFactorMappingT;
import com.tcs.destination.service.WinLossMappingService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/winloss")
public class WinLossFactorMappingController {

	private static final Logger logger = LoggerFactory
			.getLogger(WinLossFactorMappingController.class);

	@Autowired
	WinLossMappingService winLossMappingService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAll(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside WinLoss Controller /winloss GET");
		ArrayList<WinLossFactorMappingT> winLossMapping = (ArrayList<WinLossFactorMappingT>) winLossMappingService
				.findAll();
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				winLossMapping);
	}
}
