package com.tcs.destination.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.SearchCusPartAjax;
import com.tcs.destination.service.SearchService;

@RestController
@RequestMapping("/search")
public class SearchController {

	@Autowired
	SearchService searchService;

	@RequestMapping(value = "/customer/{typed}", method = RequestMethod.GET)
	public @ResponseBody List<CustomerMasterT> search(
			@PathVariable("typed") String typed) {
		return searchService.searchforCustomer(typed);
	}

	@RequestMapping(value = "/ajax/{typed}", method = RequestMethod.GET)
	public @ResponseBody List<SearchCusPartAjax> ajaxSearch(
			@PathVariable("typed") String typed) {
		return searchService.searchForCustPartContaining(typed);

	}

	// @RequestMapping(value = "/contact/{typed}", method = RequestMethod.GET)
	// public @ResponseBody List<ContactT> contactSearch(
	// @PathVariable("typed") String id) {
	// return searchService.searchContacts(id);
	//
	// }
//
//	@RequestMapping(value = "/oppurtunity/{typed}", method = RequestMethod.GET)
//	public @ResponseBody OpportunityT oppcreate(
//			@PathVariable("typed") String searchId) {
//		return searchService.searchOppurtunity(searchId);
//	}

}
