package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.CustomerService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/customer")
public class CustomerController {

	private static final Logger logger = LoggerFactory
			.getLogger(CustomerController.class);

	@Autowired
	CustomerService customerService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@PathVariable("id") String customerid,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside CustomerController /customer/id=" + customerid
				+ " GET");
		CustomerMasterT customer = customerService.findById(customerid);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				customer);
	}

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findNameWith(
			@RequestParam(value = "nameWith", defaultValue = "") String nameWith,
			@RequestParam(value = "startsWith", defaultValue = "") String startsWith,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside CustomerController /customer?namewith=" + nameWith
				+ " GET");
		List<CustomerMasterT> customers = null;

		if (!nameWith.isEmpty()) {
			customers = customerService.findByNameContaining(nameWith);
		} else if (!startsWith.isEmpty()) {
			customers = customerService.findByNameStarting(startsWith);
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Either nameWith / startsWith is required");
		}
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				customers);

	}

	@RequestMapping(value = "/targetVsActual", method = RequestMethod.GET)
	public @ResponseBody String findTargetVsActual(
			@RequestParam("name") String name,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "currency", defaultValue = "USD") String currency)
			throws Exception {
		logger.debug("Inside CustomerController /customer/targetVsActual?name="
				+ name + " GET");
		List<TargetVsActualResponse> tarVsAct = customerService
				.findTargetVsActual(name, currency, financialYear);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				tarVsAct);
	}

	@RequestMapping(value = "/topRevenue", method = RequestMethod.GET)
	public @ResponseBody String findTopRevenue(
			@RequestParam(value = "count", defaultValue = "5") int count,
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "fields", defaultValue = "all") String includeFields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside CustomerController /customer/topRevenue GET");
		List<CustomerMasterT> topRevenueCustomers = customerService
				.findTopRevenue(count, financialYear);

		return ResponseConstructors.filterJsonForFieldAndViews(includeFields,
				view, topRevenueCustomers);
	}

	@RequestMapping(value = "/group", method = RequestMethod.GET)
	public @ResponseBody String findByGroupCustomerName(
			@RequestParam("nameWith") String nameWith,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside CustomerController /customer/group?nameWith="
				+ nameWith + " GET");
		List<CustomerMasterT> customer = (List<CustomerMasterT>) customerService
				.findByGroupCustomerName(nameWith);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				customer);
	}

}
