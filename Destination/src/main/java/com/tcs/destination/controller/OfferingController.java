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

import com.tcs.destination.bean.OfferingMappingT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.OfferingService;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * This controller deals with the offering related functionalities
 * 
 * @author TCS
 *
 */
@RestController
@RequestMapping("/offering")
public class OfferingController {

	private static final Logger logger = LoggerFactory
			.getLogger(OfferingController.class);

	@Autowired
	OfferingService offeringService;

	/**
	 * This method is used to retrieve all offering mappings (SubSp and
	 * Offering)
	 * 
	 * @param fields
	 * @param view
	 * @return offeringMapping
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAll(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		try {
			logger.info("Inside OfferingController: Start of search");
			List<OfferingMappingT> offeringMapping = offeringService
					.findAllActive();
			logger.info("Inside OfferingController: End of search");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, offeringMapping);
		} catch (DestinationException e) {
			logger.error("Destination Exception" + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while retrieving offering details");
		}
	}

}
