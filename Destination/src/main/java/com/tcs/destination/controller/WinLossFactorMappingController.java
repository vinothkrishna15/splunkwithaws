package com.tcs.destination.controller;

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

import com.tcs.destination.bean.WinLossFactorMappingT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.WinLossMappingService;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * 
 * This class retrieves win loss factors
 *
 */
 
@RestController
@RequestMapping("/winloss")
public class WinLossFactorMappingController {

	private static final Logger logger = LoggerFactory
			.getLogger(WinLossFactorMappingController.class);

	@Autowired
	WinLossMappingService winLossMappingService;

	/**
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAll(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of retreiving win loss factors mapping");
		try {
			List<WinLossFactorMappingT> winLossMapping = winLossMappingService.findAllActive();
			logger.info("End of retreiving win loss factors mapping");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, winLossMapping);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retreiving win loss factors ");
		}
	}
}
