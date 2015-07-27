package com.tcs.destination.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.tcs.destination.bean.DashBoardConnectsResponse;
import com.tcs.destination.bean.Status;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.ConnectService;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * Controller to handle connection details search requests.
 * 
 */
@RestController
@RequestMapping("/connect")
public class ConnectController {

	private static final Logger logger = LoggerFactory
			.getLogger(ConnectController.class);

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
	public @ResponseBody String findConnectById(
			@PathVariable("id") String connectId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside ConnectController /connect/id=" + connectId
				+ " GET");
		ConnectT connect = connectService.findConnectById(connectId);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				connect);
	}

	/**
	 * This Method is used to find connection details for the given connection
	 * name.
	 * 
	 * @param name
	 *            is the connection name.
	 * @return connection details for the particular connection name.
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String ConnectSearchByName(
			@RequestParam("nameWith") String connectName,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "customerId", defaultValue = "") String customerId,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside ConnectController /connect?nameWith="
				+ connectName + " GET");
		List<ConnectT> connectlist = connectService
				.searchforConnectsByNameContaining(connectName, customerId);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				connectlist);
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
			@RequestParam(value = "owner", defaultValue = "ALL") String owner,
			@RequestParam(value = "userId", defaultValue = "") String userId,
			@RequestParam(value = "customerId", defaultValue = "") String customerId,
			@RequestParam(value = "partnerId", defaultValue = "") String partnerId,
			@RequestParam(value = "weekStartDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date weekStartDate,
			@RequestParam(value = "weekEndDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date weekEndDate,
			@RequestParam(value = "monthStartDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date monthStartDate,
			@RequestParam(value = "monthEndDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date monthEndDate)
			throws Exception {
		logger.debug("Inside ConnectController /connect/date?from=" + fromDate
				+ "&to=" + toDate + "GET");
		if (weekStartDate.getTime() == weekEndDate.getTime()
				&& monthStartDate.getTime() == monthEndDate.getTime()) {
			System.out.println("Old Code");
			List<ConnectT> connects = connectService
					.searchforConnectsBetweenForUserOrCustomerOrPartner(
							fromDate, toDate, userId, owner, customerId,
							partnerId, false);
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, connects);
		} else {
			DashBoardConnectsResponse dashBoardConnectsResponse = connectService
					.searchDateRangwWithWeekAndMonthCount(fromDate, toDate,
							userId, owner, customerId, partnerId,
							weekStartDate, weekEndDate, monthStartDate,
							monthEndDate);
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, dashBoardConnectsResponse);
		}

	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertToConnect(
			@RequestBody ConnectT connect) throws Exception {
		logger.debug("Connect Insert Request Received /connect POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (connectService.insertConnect(connect)) {
				status.setStatus(Status.SUCCESS, connect.getConnectId());
				logger.debug("CONNECT CREATED SUCCESS" + connect.getConnectId());
			}
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews("all", "",
						status), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editConnect(
			@RequestBody ConnectT connect) throws Exception {
		logger.debug("Connect Edit Request Received /connect PUT");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
        try{
		if (connectService.updateConnect(connect)) {
			status.setStatus(Status.SUCCESS, connect.getConnectId());
		}
        } catch(Exception e){
        	logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
        	throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
        }

		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews("all", "",
						status), HttpStatus.OK);
	}

	/**
	 * This controller retrieves all the connects of users under a supervisor between dates 
	 * and also gives a count of connects on a weekly and monthly basis
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param fields
	 * @param view
	 * @param supervisorId
	 * @param weekStartDate
	 * @param weekEndDate
	 * @param monthStartDate
	 * @param monthEndDate
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/team", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getTeamConnects(
			@RequestParam("from") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam("to") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam("supervisorId") String supervisorId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "weekStartDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date weekStartDate,
			@RequestParam(value = "weekEndDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date weekEndDate,
			@RequestParam(value = "monthStartDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date monthStartDate,
			@RequestParam(value = "monthEndDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date monthEndDate)
			throws Exception {

		logger.debug("Inside ConnectController /connect/team?from=" + fromDate
				+ "&to=" + toDate + "&supervisorId "+ supervisorId + "GET");
		DashBoardConnectsResponse dashBoardConnectsResponse = null;
		try {
			// Calling the service method
			dashBoardConnectsResponse = connectService.getTeamConnects(
					supervisorId, fromDate, toDate, weekStartDate, weekEndDate,
					monthStartDate, monthEndDate);
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews(fields, view,
						dashBoardConnectsResponse), HttpStatus.OK);

	}

}