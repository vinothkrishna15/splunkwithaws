package com.tcs.destination.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.CustPartResultCard;
import com.tcs.destination.service.RecentlyAddedService;

@RestController
@RequestMapping("/recent")
public class RecentlyAddedController {

	@Autowired
	RecentlyAddedService recentlyAddedService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<CustPartResultCard> recentlyAdded(@RequestParam("entityType") String entityType,
			@RequestParam(value="count",defaultValue = "5") int count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) {
		return recentlyAddedService.recentlyAdded(entityType,count);
	}
}
