package com.tcs.destination.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.service.CustomerService;
import com.tcs.destination.utils.Constants;

@RestController
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	CustomerService customerService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@PathVariable("id") String customerid,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		CustomerMasterT customer = customerService.findById(customerid);
		return Constants.filterJsonForFieldAndViews(fields, view, customer);
	}

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findNameWith(
			@RequestParam("nameWith") String chars,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception {
		List<CustomerMasterT> customer = customerService
				.findByNameContaining(chars);
		return Constants.filterJsonForFieldAndViews(fields, view, customer);
	}

	@RequestMapping(value = "/targetVsActual", method = RequestMethod.GET)
	public @ResponseBody String findTargetVsActual(
			@RequestParam("name") String name,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "currency", defaultValue = "USD") String currency) throws Exception {
		List<TargetVsActualResponse> tarVsAct = customerService
				.findTargetVsActual(name, currency);
		return Constants.filterJsonForFieldAndViews(fields, view, tarVsAct);
	}

	@RequestMapping(value = "/topRevenue", method = RequestMethod.GET)
	public @ResponseBody String findTopRevenue(
			@RequestParam(value = "count", defaultValue = "5") int count,
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "fields", defaultValue = "all") String includeFields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		List<CustomerMasterT> topRevenueCustomers = customerService
				.findTopRevenue(count, financialYear);

		return Constants.filterJsonForFieldAndViews(includeFields, view,
				topRevenueCustomers);
	}
	
	@RequestMapping(value = "/group", method = RequestMethod.GET)
	public @ResponseBody String findByGroupCustomerName(
			@RequestParam("nameWith") String nameWith,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception {
		List<CustomerMasterT> customer = (List<CustomerMasterT>)customerService.findByGroupCustomerName(nameWith);
		return Constants.filterJsonForFieldAndViews(fields, view, customer);
	}

}
