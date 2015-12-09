package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UserAccessRequestT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.UserAccessRequestService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/useraccess")
public class UserAccessRequestController {

	private static final Logger logger = LoggerFactory
			.getLogger(UserAccessRequestController.class);

	@Autowired
	UserAccessRequestService userAccessRequestService;

	@RequestMapping(value = "/{reqid}", method = RequestMethod.GET)
	public @ResponseBody String getRequest(
			@PathVariable("reqid") String reqId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of retrieving the user request by id");
		logger.debug("Inside searchforRequestsById service");
		try {
			UserAccessRequestT userAccessRequest = userAccessRequestService
					.findUserRequestById(reqId);
			logger.info("End of retrieving the user request by id");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, userAccessRequest);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the user access request for request id :"
							+ reqId);
		}
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public @ResponseBody String getAllNewUserAccessRequests(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of retrieving the all user access requests");
		logger.debug("Inside getAllUserAccessRequests service");
		try {
			List<UserAccessRequestT> userAccessRequestList = userAccessRequestService
					.findAllUserAccessRequests();
			logger.info("End of retrieving the all user access requests");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, userAccessRequestList);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving all user access requests");
		}
	}

	@RequestMapping(value = "/request", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertToNewUserAccessRequest(
			@RequestBody UserAccessRequestT userAccessRequest)
			throws DestinationException {
		logger.info("Start of Inserting a new User Access Request");
		logger.debug("User Access Insert Request Received /useraccess/request POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (userAccessRequestService.insertUserRequest(userAccessRequest)) {
				status.setStatus(Status.SUCCESS,
						"Access request has been saved successfully");
				logger.debug("Access request has been saved successfully: "
						+ userAccessRequest.getUserId());
			}
			logger.info("End of Inserting a new User Access Request");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while Inserting a new User Access Request");
		}

	}

	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editNewUserAccessRequest(
			@RequestBody UserAccessRequestT userAccessRequest)
			throws DestinationException {
		logger.info("Start of Edit user access request");
		logger.debug("User Access Edit Request Received PUT");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (userAccessRequestService.editUserRequest(userAccessRequest)) {
				status.setStatus(Status.SUCCESS,
						"Access request has been updated successfully");
				logger.debug("Access request has been updated successfully: "
						+ userAccessRequest.getRequestId());
			}
			logger.info("End of Edit user access request");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while Editing a new User Access Request");
		}
	}
}