package com.tcs.destination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.CustomerRepository;

@RestController
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	ApplicationContext appContext;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody CustomerMasterT findOne(
			@PathVariable("id") String customerid) {
		CustomerRepository repository = appContext
				.getBean(CustomerRepository.class);
		return repository.findByCustomerId(customerid).get(0);
	}
}
