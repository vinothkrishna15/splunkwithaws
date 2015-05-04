package com.tcs.destination.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody Status getConvertedValue(
			@RequestParam("from") String base,
			@RequestParam(value="to", defaultValue = "USD") String target,
			@RequestParam("value") String value) throws DestinationException{
		
	        Double d = Double.parseDouble(value);
	        Status status = new Status();
	        status.setStatus(Status.SUCCESS,beaconService.convert(base, target, d.doubleValue()).toString());	       	        
	        return status;
		
	}

}
