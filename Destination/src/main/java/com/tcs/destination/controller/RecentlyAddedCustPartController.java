package com.tcs.destination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.RecentlyAddedCustPart;

@RestController
@RequestMapping("/recentcp")
public class RecentlyAddedCustPartController {

	@Autowired
	ApplicationContext appContext;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody RecentlyAddedCustPart recentlyAdded() {
		return null;
	}

}
