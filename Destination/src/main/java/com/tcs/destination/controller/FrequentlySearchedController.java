package com.tcs.destination.controller;

import java.sql.Timestamp;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.FrequentlySearchedCustomerPartnerT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.FrequentlySearchedService;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/frequent")
public class FrequentlySearchedController {

	private static final Logger logger = LoggerFactory
			.getLogger(FrequentlySearchedController.class);

	@Autowired
	FrequentlySearchedService frequentService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findFrequent(
			@RequestParam(value = "entityType") String entityType,
			@RequestParam(value = "count", defaultValue = "4") int count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "owner", defaultValue = "all") String owner)
			throws DestinationException {
		logger.info("Inside Frequently searched controller: Start of find");
		try {
			logger.info("Inside Frequently searched controller: End of find");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, frequentService.findFrequent(entityType, count));
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while retrieving frequently used details");
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertToFrequent(
			@RequestBody FrequentlySearchedCustomerPartnerT frequent,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {

		logger.info("Inside Frequently searched controller: Start of insert");
		try {
			Status status = new Status();
			status.setStatus(Status.FAILED, "");
			if (frequentService.insertFrequent(frequent)) {
				status.setStatus(Status.SUCCESS,
						frequent.getFrequentlySearchedId());
			}
			logger.info("Inside Frequently searched controller: Start of find");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while updating frequent customer/partner details");
		}
	}
}
