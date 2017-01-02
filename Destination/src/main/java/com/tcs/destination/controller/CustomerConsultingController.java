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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.dto.CustomerConsultingDTO;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.CustomerConsultingService;
import com.tcs.destination.utils.DateUtils;

/**
 * @author tcs2
 *
 */
@RestController
@RequestMapping("/customerConsultingDetails")
public class CustomerConsultingController {

	private static final Logger logger = LoggerFactory
			.getLogger(CustomerConsultingController.class);

	@Autowired
	CustomerConsultingService customerConsultingService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public CustomerConsultingDTO findTotalGroupCustomerConsulted(
			@RequestParam(value = "financialYear", defaultValue = "") String financialYear
			)
			throws DestinationException {
		logger.info("Inside Customer Consulting Controller: Start of fetching associate customer details");

		if (financialYear.isEmpty()) {
			financialYear = DateUtils.getCurrentFinancialYear();
		}
		
		CustomerConsultingDTO response = null;
		try {

			response = customerConsultingService
					.findRevenueDetailsOfConsultedCustomers(financialYear);

			logger.info("Inside Customer Consulting Controller: Exit");

		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while retrieving customer consulting details");
		}
		return response;

	}
}