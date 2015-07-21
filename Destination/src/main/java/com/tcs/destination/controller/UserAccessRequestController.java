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
			throws Exception {
		logger.debug("Inside searchforRequestsById service");
		UserAccessRequestT userAccessRequest = userAccessRequestService.findUserRequestById(reqId);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				userAccessRequest);
	}
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public @ResponseBody String getAllNewUserAccessRequests(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside getAllUserAccessRequests service");
		List<UserAccessRequestT> userAccessRequestList = userAccessRequestService.findAllUserAccessRequests();
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				userAccessRequestList);
	}

	@RequestMapping(value="request",method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertToNewUserAccessRequest(
			@RequestBody UserAccessRequestT userAccessRequest) throws Exception {
		logger.debug("User Access Insert Request Received /useraccess/request POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (userAccessRequestService.insertUserRequest(userAccessRequest)) {
				status.setStatus(Status.SUCCESS, userAccessRequest.getUserId() + " Request saved");
				logger.debug("User Access Request saved successfully"
						+ userAccessRequest.getUserId());
			}
		}
//		}catch (DataIntegrityViolationException dataIntegrityViolationEx){
//			logger.error("INTERNAL_SERVER_ERROR" + dataIntegrityViolationEx.getMessage());
//			if(dataIntegrityViolationEx.getMessage().contains("unique")){
//			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
//					"Duplicate Request for the user : " + newUserRequest.getUserId());
//			} else {
//				throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
//						 dataIntegrityViolationEx.getMessage());
//			}
//		}
		catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews("all", "",
						status), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editNewUserAccessRequest(
			@RequestBody UserAccessRequestT userAccessRequest) throws Exception {
		logger.debug("User Access Edit Request Received PUT");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (userAccessRequestService.editUserRequest(userAccessRequest)) {
				status.setStatus(Status.SUCCESS, userAccessRequest.getUserId());
				logger.debug("userAccessRequest updated successfully"
						+ userAccessRequest.getRequestId());
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

	

}
