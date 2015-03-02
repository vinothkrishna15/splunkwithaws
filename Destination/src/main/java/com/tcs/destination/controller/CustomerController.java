package com.tcs.destination.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.RevenuesResponse;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.service.CustomerService;

@RestController
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	CustomerService customerService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody CustomerMasterT findOne(
			@PathVariable("id") String customerid) {
		return customerService.findById(customerid);
	}

	@RequestMapping(value = "/comp", method = RequestMethod.GET)
	public @ResponseBody List<TargetVsActualResponse> findTargetVsActual(
			@RequestParam("name") String name) {
		return customerService.findTargetVsActual(name);
	}

	@RequestMapping(value = "/top10", method = RequestMethod.GET)
	public @ResponseBody List<RevenuesResponse> findTop10Customers() {
		return customerService.findTop10Customers();
	}

}
