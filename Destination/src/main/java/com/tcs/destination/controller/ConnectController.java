package com.tcs.destination.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.service.ConnectService;


@RestController
@RequestMapping("/connect")
public class ConnectController {

	@Autowired
	ConnectService connectService;

	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody List<ConnectT> ajaxConnectSearch(@RequestParam("typed") String typed) {
		return connectService.searchForConnects(typed);

	}
}