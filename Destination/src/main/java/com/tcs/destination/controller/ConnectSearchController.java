package com.tcs.destination.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.CustPartResultCard;
import com.tcs.destination.service.SearchService;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/search")
public class ConnectSearchController {
	

		@Autowired
		SearchService searchService;

		@RequestMapping(value ="/ConnectPart/{typed}", method = RequestMethod.GET)
		public @ResponseBody List<ConnectT> search(
				@PathVariable("typed") String typed) {
			System.out.println("im here");
			return searchService.searchforConnectPartDetail(typed);
		}
}

