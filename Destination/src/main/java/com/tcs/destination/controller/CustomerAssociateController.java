/**
 * 
 */
package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.dto.CustomerAssociateAllocationDetailsDTO;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.CustomerAssociateService;

/**
 * Controller to handle customerAssociateDetails service
 *
 */
@RestController
@RequestMapping("/customerAssociateDetails")
public class CustomerAssociateController {

	private static final Logger logger = LoggerFactory
			.getLogger(CustomerAssociateController.class);

	@Autowired
	CustomerAssociateService customerAssociateService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public CustomerAssociateAllocationDetailsDTO findAssociatesByGroupCustomer()
			throws DestinationException {
		CustomerAssociateAllocationDetailsDTO response = null;
		logger.info("Inside Customer Associate Controller: Start of fetching associate customer details");
		try {
			response = customerAssociateService
					.findAssociatesAllocationDetailsByGroupCustomer();

			logger.info("Inside Customer Associate controller: End of retrieving data");

			return response;
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while retrieving associate customer allocation details");
		}
	}
}
