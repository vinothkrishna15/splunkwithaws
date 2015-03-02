package com.tcs.destination.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.CustPartResultCard;
import com.tcs.destination.service.RecentlyAddedService;

@RestController
@RequestMapping("/recentcp")
public class RecentlyAddedCustPartController {

	@Autowired
	RecentlyAddedService recentlyAddedService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<CustPartResultCard> recentlyAdded() {
		return recentlyAddedService.recentlyAdded();
	}

}
