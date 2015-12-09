package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.Status;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.BeaconConverterService;

@RestController
@RequestMapping("/currency")
public class CurrencyController {

	@Autowired
	BeaconConverterService beaconService;
	
	private static final Logger logger = LoggerFactory.getLogger(CurrencyController.class);
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody Status getConvertedValue(
			@RequestParam("from") String base,
			@RequestParam(value="to", defaultValue = "USD") String target,
			@RequestParam("value") String value) throws DestinationException{
		logger.info("Start of Converting the currency value");
		    try {
	        Double d = Double.parseDouble(value);
	        Status status = new Status();
	        status.setStatus(Status.SUCCESS,beaconService.convert(base, target, d.doubleValue()).toString());
	        logger.info("End of Converting the currency value");
	        return status;
		    } catch (DestinationException e) {
				throw e;
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Backend error while converting the currency");
		   }
		
	}

}
