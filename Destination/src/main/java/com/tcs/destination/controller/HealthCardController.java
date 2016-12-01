package com.tcs.destination.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.ContentDTO;
import com.tcs.destination.bean.DeliveryCentreUnallocationT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.HealthCardService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/healthCard")
public class HealthCardController {
	private static final Logger logger = LoggerFactory
			.getLogger(HealthCardController.class);
	
	@Autowired
	HealthCardService healthCardService;
	
	@RequestMapping(value = "/unallocation", method = RequestMethod.GET)
	public @ResponseBody String findUnallocation(
			@RequestParam(value = "fromDate", defaultValue = "") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam(value = "toDate", defaultValue = "") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam(value = "fields", defaultValue = "all", required = false) String fields,
			@RequestParam(value = "view", defaultValue = "", required = false) String view)
			throws DestinationException {
		logger.info("Start of retrieving unallocation details");
		String response = null;
		ContentDTO<DeliveryCentreUnallocationT> content;
		try {
			content = healthCardService.getDeliveryCentreUnallocation(fromDate,toDate);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, content);
			logger.info("End of retrieving unallocation details");
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving unallocation details");
		}
		return response;
	}
}
