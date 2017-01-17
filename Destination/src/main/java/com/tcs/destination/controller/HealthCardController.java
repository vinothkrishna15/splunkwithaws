package com.tcs.destination.controller;

import java.util.Date;
import java.util.List;

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
import com.tcs.destination.bean.HealthCardValues;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.dto.DeliveryCentreUtilizationDTO;
import com.tcs.destination.bean.dto.DeliveryClusterDTO;
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
	
	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/unallocation", method = RequestMethod.GET)
	public @ResponseBody ContentDTO<DeliveryClusterDTO> findUnallocation(
			@RequestParam(value = "fromDate", defaultValue = "") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam(value = "toDate", defaultValue = "") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam(value = "fields", defaultValue = "all", required = false) String fields,
			@RequestParam(value = "view", defaultValue = "", required = false) String view)
			throws DestinationException {
		logger.info("Start of retrieving unallocation details");
		ContentDTO<DeliveryClusterDTO> content;
		try {
			content = healthCardService.getDeliveryCentreUnallocation(fromDate,toDate);
			logger.info("End of retrieving unallocation details");
			
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving unallocation details");
		}
		return content;
	}
	
	
	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/utilization", method = RequestMethod.GET)
	public @ResponseBody String findUtilization(
			@RequestParam(value = "fromDate", defaultValue = "") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam(value = "toDate", defaultValue = "") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam(value = "fields", defaultValue = "all", required = false) String fields,
			@RequestParam(value = "view", defaultValue = "", required = false) String view)
			throws DestinationException {
		logger.info("Start of retrieving utilization details");
		String response = null;
		ContentDTO<DeliveryCentreUtilizationDTO> content;
		try {
			content = healthCardService.getDeliveryCentreUtilization(fromDate,toDate);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, content);
			logger.info("End of retrieving utilization details");
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving utilization details");
		}
		return response;
	}

	/**
	 * Method to insert new component from the total list of components to add
	 * into componentId.
	 * 
	 * @param componentId
	 * @return response - status and the description.
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String insertComponentInHealthCard(
			@RequestParam(value = "componentId") int componentId) {
		logger.info("Inside HealthCardController for insertComponentInHealthCard method: start");
		Status status = new Status();
		String response = null;
		try {
			status = healthCardService.insertNewComponentByuserID(componentId);

			response = ResponseConstructors.filterJsonForFieldAndViews("all",
					"", status);
			logger.info("Inside HealthCardController for insertComponentInHealthCard method: exit");
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while adding the details for user preferences");
		}

		return response;
	}
	
	/**
	 * This method is used to retrieve health card values
	 * @param fromDate
	 * @param toDate
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/values", method = RequestMethod.GET)
	public @ResponseBody ContentDTO<HealthCardValues> getHealthCardValues(
			@RequestParam(value = "fromDate", defaultValue = "") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam(value = "toDate", defaultValue = "") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam(value = "componentId", defaultValue = "2") int type)
			throws DestinationException {
		logger.info("Start of retrieving healthcard values");
		ContentDTO<HealthCardValues> healthCardValues;
		try {
			healthCardValues = healthCardService.getHealthCardValues(fromDate,toDate,type);
			logger.info("End of retrieving utilization details");
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving healthcard values");
		}
		return healthCardValues;
	}
	
}
