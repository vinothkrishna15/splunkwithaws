package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.PushNotificationRegistrationT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UserNotificationSettingsT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.bean.UserTaggedFollowedT;
import com.tcs.destination.service.PushNotificationService;
import com.tcs.destination.service.SubSpService;
import com.tcs.destination.service.UserService;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/push")
public class PushNotificationController {
	
	private static final Logger logger = LoggerFactory.getLogger(PushNotificationController.class);

	@Autowired
	PushNotificationService pushNotificationService;

		
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addPushNotificationReg(
			@RequestBody PushNotificationRegistrationT pushNotification,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside PushNotificationController /push POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		if (pushNotificationService.addPushNotification(pushNotification)) {
			logger.debug("Push Notification Registration done Successfully");
			status.setStatus(Status.SUCCESS,"Push Notification Registration done Successfully" );
		}
		
		return new ResponseEntity<String>(ResponseConstructors.filterJsonForFieldAndViews(
				"all", "", status), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> updatePushNotificationReg(
			@RequestBody PushNotificationRegistrationT pushNotification,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside PushNotificationController /push PUT");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		if (pushNotificationService.updatePushNotification(pushNotification)) {
			logger.debug("Push Notification Registration Updated Successfully");
			status.setStatus(Status.SUCCESS,"Push Notification Registration Update Successfully" );
		}
		
		return new ResponseEntity<String>(ResponseConstructors.filterJsonForFieldAndViews(
				"all", "", status), HttpStatus.OK);
	}


	@RequestMapping(method = RequestMethod.DELETE)
	public @ResponseBody ResponseEntity<String> deletePushNotificationReg(
			@RequestParam(value="userId") String userId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside PushNotificationController /push DELETE");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		if (pushNotificationService.deletePushNotificRecords(userId)) {
			logger.debug("Push Notification Registration Deleted Successfully");
			status.setStatus(Status.SUCCESS,"Push Notification Registration Deleted Successfully" );
		}
		
		return new ResponseEntity<String>(ResponseConstructors.filterJsonForFieldAndViews(
				"all", "", status), HttpStatus.OK);
	}
		
		
}

