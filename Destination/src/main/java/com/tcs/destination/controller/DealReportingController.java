/**
 * 
 * DealReportingController.java 
 *
 * @author TCS
 * @Version 1.0 - 2016
 * 
 * @Copyright 2016 Tata Consultancy 
 */
package com.tcs.destination.controller;

import java.util.List;

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

import com.tcs.destination.bean.DealClosureReportingT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.DealReportingService;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * Controller to deal with reporting activities related to deal closure
 * for governance purpose
 * 
 * @author tcs2
 */

@RestController
@RequestMapping("/dealReporting")
public class DealReportingController {

	private static final Logger logger = LoggerFactory.getLogger(DealReportingController.class);

	@Autowired
	DealReportingService dealReportingService;

	/**
	 * method to allow system admin to set month or
	 * multiple months for deal closure date for reporting purpose
	 * @param dealClosureReportingt
	 * @return
	 * @throws DestinationException 
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> createMonthReporting(
			@RequestBody List<DealClosureReportingT> monthsSelected,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws DestinationException{
		logger.info("Begin dealReporting controller: inside . craete method");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try{
			dealReportingService.createDealMonthReporting(monthsSelected,status);
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		}
		catch(DestinationException e){
			throw e;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR, "Backend error while creating reporting for deal closure month!");
		}
	}
	
	/**
	 * Method to retrieve deal closure data
	 * 
	 * @return List of deal closure data
	 * @throws DestinationException 
	 */
	@RequestMapping(value = "/retrieve", method = RequestMethod.GET)
	public @ResponseBody String getDealMonthReporting(
			@RequestParam(value = "fields", defaultValue = "dealReportingStartDate, dealReportingEndDate") String fields,
			@RequestParam(value = "view", defaultValue = "dealClosureData") String view) throws DestinationException{
		logger.info("Begin dealReporting controller: inside . craete method");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try{
			List<DealClosureReportingT> dealClosureList = dealReportingService.getDealMonthReporting();
			
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
							view, dealClosureList);
		}
		catch(DestinationException e){
			throw e;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR, "Backend error while retriving deal closure reporting data");
		}
		
	}
}
