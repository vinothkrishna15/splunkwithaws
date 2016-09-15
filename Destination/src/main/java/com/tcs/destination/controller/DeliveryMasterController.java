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

import com.tcs.destination.bean.PaginatedResponse;
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
	 * This method retrieves all the opportunities
	 * 
	 * @param page
	 * @param count
	 * @param isCurrentFinancialYear
	 * @param order
	 * @param sortBy
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public @ResponseBody String findAll(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "isCurrentFinancialYear", defaultValue = "false") Boolean isCurrentFinancialYear,
			@RequestParam(value = "order", defaultValue = "DESC") String order,
			@RequestParam(value = "sortBy", defaultValue = "modifiedDatetime") String sortBy,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside DeliveryMasterController: Start of /delivery/all GET");
		String response = null;
		PaginatedResponse deliveryMasterResponse = null;
		try {
			deliveryMasterResponse = deliveryMasterService.findAll(sortBy, order,
					isCurrentFinancialYear, page, count);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, deliveryMasterResponse);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the deliveryMaster details");
		}
		logger.info("Inside DeliveryMasterController: End of /delivery/all GET");
		return response;
	}
	
	
}