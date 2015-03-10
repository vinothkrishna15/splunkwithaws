package com.tcs.destination.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.exception.CustomerNotFoundException;
import com.tcs.destination.service.ConnectService;

/**
 * Controller to handle connection details search requests.
 * 
 */
@RestController
@RequestMapping("/connect")
public class ConnectController {

	@Autowired
	ConnectService connectService;
	/**
	 * This Method is used to find connection details for the given connection id.
	 * @param typed is the connect id.
	 * @return connection details for the particular connection id.
	 */
	@RequestMapping(value ="/searchById", method = RequestMethod.GET)
	public @ResponseBody List<ConnectT> ajaxConnectSearchById(
			@RequestParam("typed") String typed){
				
		return connectService.searchforConnectsById(typed);
	}
	
	/**
	 * This Method is used to find connection details for the given connection name.
	 * @param typed is the connection name.
	 * @return connection details for the particular connection name.
	 */
	@RequestMapping(value ="/searchByName", method = RequestMethod.GET)
	public @ResponseBody List<ConnectT> search(
			@RequestParam("typed") String typed) {
		
		return connectService.searchforConnectsByName(typed);
	}
}