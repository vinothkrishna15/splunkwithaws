package com.tcs.destination.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.ContactT;
import com.tcs.destination.service.ContactService;
import com.tcs.destination.utils.Constants;

/**
 * Controller to handle contact details search requests.
 * 
 */
@RestController
@RequestMapping("/contact")
public class ContactController {

	@Autowired
	ContactService contactService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String ContactSearchByNameorCustPartId(
			@RequestParam(value = "customerId", defaultValue = "") String customerId,
			@RequestParam(value = "partnerId", defaultValue = "") String partnerId,
			@RequestParam(value = "nameWith", defaultValue = "") String nameWith,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		List<ContactT> contactlist = contactService.searchforContact(
				customerId, partnerId, nameWith);
		return Constants.filterJsonForFieldAndViews(fields, view, contactlist);
	}

}