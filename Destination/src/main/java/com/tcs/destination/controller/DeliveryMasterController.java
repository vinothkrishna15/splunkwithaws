package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.AsyncJobRequest;
import com.tcs.destination.bean.DeliveryMasterT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.PageDTO;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.Status;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.JobName;
import com.tcs.destination.enums.OperationType;
import com.tcs.destination.enums.Switch;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.DeliveryMasterService;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * This Controller handles the opportunity module
 * 
 * @author TCS
 *
 */
@RestController
@RequestMapping("/delivery")
public class DeliveryMasterController {

	private static final Logger logger = LoggerFactory
			.getLogger(DeliveryMasterController.class);

	@Autowired
	DeliveryMasterService deliveryMasterService;
	
	/**
	 * This method retrieves the delivery master list
	 * @param page
	 * @param count
	 * @param order
	 * @param sortBy
	 * @param stage
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody String findAll(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "order", defaultValue = "DESC") String order,
			@RequestParam(value = "sortBy", defaultValue = "deliveryMasterId") String sortBy,
			@RequestParam(value = "stage", defaultValue = "-1") Integer stage,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside DeliveryMasterController: Start of /delivery/all GET");
		String response = null;
		PageDTO deliveryMasterDTO = null;
		try {
			
			deliveryMasterDTO = deliveryMasterService.findEngagements(stage,sortBy, order,
					page, count);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, deliveryMasterDTO);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the deliveryMaster list");
		}
		logger.info("Inside DeliveryMasterController: End of /delivery/all GET");
		return response;
	}
	
	/**
	 * This method is used to get the delivery master details for the given
	 * delivery master id
	 * 
	 * @param deliveryMasterId
	 * @param fields
	 * @param view
	 * @return deliveryMasterT
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findByDeliveryMasterId(
			@PathVariable("id") Integer deliveryMasterId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {

		logger.info("Inside DeliveryMasterController: Start of search by id");
		String response = null;
		DeliveryMasterT deliveryMasterT;
		try {
			deliveryMasterT = deliveryMasterService.findByDeliveryMasterId(deliveryMasterId);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, deliveryMasterT);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the delivery master details for the id:"
							+ deliveryMasterId);
		}
		logger.info("Inside DeliveryMasterController: End of search by id");
		return response;
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editEngagement(
			@RequestBody DeliveryMasterT deliveryMaster,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {

		logger.info("Inside DeliveryMasterController: Start of Edit DeliveryMaster");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (deliveryMasterService.updateDelivery(deliveryMaster)) {
				status.setStatus(Status.SUCCESS, new Integer(deliveryMaster.getDeliveryMasterId()).toString());
				//jobLauncherController.asyncJobLaunchForNotification(JobName.notification, EntityType.CONNECT, connect.getConnectId(),OperationType.CONNECT_EDIT,connect.getModifiedBy());
			}
			logger.info("Inside DeliveryMasterController: End of Edit delivery master");
			
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while updating delivery master");
		}

	}
	
	
}
