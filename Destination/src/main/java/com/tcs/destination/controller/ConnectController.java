package com.tcs.destination.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.service.ConnectService;
import com.tcs.destination.utils.Constants;

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
	 * This Method is used to find connection details for the given connection
	 * id.
	 * 
	 * @param Id
	 *            is the connection id.
	 * @return connection details for the particular connection id.
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String ConnectSearchById(
			@PathVariable("id") String connectId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
	     	@RequestParam(value = "view", defaultValue = "") String view) {
	  ConnectT connect=connectService.searchforConnectsById(connectId);
	  return Constants.filterJsonForFieldAndViews(fields, view, connect);
	}

	/**
	 * This Method is used to find connection details for the given connection
	 * name.
	 * 
	 * @param name
	 *            is the connection name.
	 * @return connection details for the particular connection name.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String ConnectSearchByName(
			@RequestParam("nameWith") String connectName,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
	     	@RequestParam(value = "view", defaultValue = "") String view) {
	  List<ConnectT> connectlist=connectService.searchforConnectsByNameContaining(connectName);
	  return Constants.filterJsonForFieldAndViews(fields, view, connectlist);
	}

	/**
	 * This Method is used to find connection details for the given connection
	 * name.
	 * 
	 * @param typed
	 *            is the connection name.
	 * @return connection details for the particular connection name.
	 */
	@RequestMapping(value = "/date", method = RequestMethod.GET)
	public @ResponseBody String search(
			@RequestParam("from") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam("to") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "owner", defaultValue ="ALL") String owner) {
		List<ConnectT> connects = connectService.searchforConnectsBetweenForUser(
				fromDate, toDate,Constants.getCurrentUserDetails(),owner);
		return Constants.filterJsonForFieldAndViews(fields, view, connects);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertToConnect(
			@RequestBody ConnectT connect) {
		Status status = new Status();
		status.setStatus(Status.FAILED,"");
		try {
			if(connectService.insertConnect(connect)){
				status.setStatus(Status.SUCCESS,connect.getConnectId());
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			status.setStatus(Status.FAILED,e.getMessage());
			//status.setDescription();
			return new ResponseEntity<String>(Constants.filterJsonForFieldAndViews("all", "", status),HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(Constants.filterJsonForFieldAndViews("all", "", status),HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editConnect(
			@RequestBody ConnectT connect) {
		Status status = new Status();
		status.setStatus(Status.FAILED,"");
		try{
			if(connectService.editConnect(connect)){
				status.setStatus(Status.SUCCESS,connect.getConnectId());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			status.setStatus(Status.FAILED,e.getMessage());
			//status.setDescription(e.getMessage());
			return new ResponseEntity<String>(Constants.filterJsonForFieldAndViews("all", "", status),HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(Constants.filterJsonForFieldAndViews("all", "", status),HttpStatus.OK);
	}
	
}