package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.WorkflowCustomerT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {

	private static final Logger logger = LoggerFactory
			.getLogger(WorkflowController.class);


	@RequestMapping(value = "/requestCustomer", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertRequestedCustomer(
			@RequestBody WorkflowCustomerT workflowCustomerT)
			throws DestinationException {

		logger.info("Inside WorkflowController: Start of inserting requested customer");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {

			status.setStatus(Status.SUCCESS,
					"Request for creation of customer Submitted");

			logger.info("End of inserting requested customer");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while inserting requested customer");
		}

	}
}
