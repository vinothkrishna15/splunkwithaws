package com.tcs.destination.controller;

import java.util.ArrayList;
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

import com.tcs.destination.bean.GeographyCountryMappingT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.CountryService;
import com.tcs.destination.utils.ResponseConstructors;
/**
 * 
 * This Controller is used to handle country related search requests
 *
 */
@RestController
@RequestMapping("/country")
public class CountryController {

	private static final Logger logger = LoggerFactory
			.getLogger(CountryController.class);

	@Autowired
	CountryService countryService;

	/**
	 * This method is used to retrieve geography country mapping
	 * 
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAllAcive(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside CountryController: Start of retrieving the Geography Country Mapping");
		List<GeographyCountryMappingT> geographyCountryMapping = new ArrayList<GeographyCountryMappingT>();
		try {
			geographyCountryMapping = countryService.findAllActive();
			logger.info("Inside CountryController: End of retrieving the Geography Country Mapping");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, geographyCountryMapping);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Geography Country Mapping");
		}
	}

}
