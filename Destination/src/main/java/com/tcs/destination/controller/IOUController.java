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

import com.tcs.destination.bean.IouBeaconMappingT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.IOUService;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * This controller deals with the IOU related functionalities
 * 
 * @author TCS
 *
 */
@RestController
@RequestMapping("/iou")
public class IOUController {

	private static final Logger logger = LoggerFactory
			.getLogger(IOUController.class);

	@Autowired
	IOUService iouService;

	/**
	 * This method retrieves all the IOU Customer mappings (Display IOU and IOU)
	 * 
	 * @param fields
	 * @param view
	 * @return iouCustomerMappingTs
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAll(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside IOUController: Start of search");
		try {
			List<IouCustomerMappingT> iouCustomerMappingTs = iouService.findAllActive();
			logger.info("Inside IOUController: End of search");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, iouCustomerMappingTs);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while retrieving iou details");
		}
	}
	
	/**
	 * This method retrieves all the IOU Customer mappings (Display IOU and IOU)
	 * 
	 * @param fields
	 * @param view
	 * @return iouCustomerMappingTs
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/beacon", method = RequestMethod.GET)
	public @ResponseBody String findAllIouBeacon(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside IOUController/beacon: Start of GET");
		try {
			List<IouBeaconMappingT> iouBeaconMappingTs = iouService.findAllBeaconIouActive();
			logger.info("Inside IOUController/beacon: End of GET");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, iouBeaconMappingTs);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while retrieving beacon iou details");
		}
	}


}
