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

import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UserNotificationSettingsT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.UserNotificationSettingsService;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/userNotification")
public class UserNotificationSettingsController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserNotificationSettingsController.class);

	@Autowired
	UserNotificationSettingsService userNotificationSettingsService;

		
	@RequestMapping(value = "/setting",method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addUserNotificationSetting(
			@RequestBody List<UserNotificationSettingsT> userNotificationSettings,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside UserDetailsController /userNotification/setting POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		if (userNotificationSettingsService.addOrUpdateUserNotifications(userNotificationSettings)) {
			logger.debug("User Notification Settings inserted Successfully");
			status.setStatus(Status.SUCCESS,"User Notification Settings for UserId :"+userNotificationSettings.get(0).getUserT().getUserId() +" inserted Successfully" );
			}
		
		return new ResponseEntity<String>(ResponseConstructors.filterJsonForFieldAndViews(
				"all", "", status), HttpStatus.OK);
		}


	@RequestMapping(value = "/setting",method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> updateUserNotificationSetting(
			@RequestBody List<UserNotificationSettingsT> userNotificationSettings,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside UserDetailsController /userNotification/setting PUT");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		if (userNotificationSettingsService.addOrUpdateUserNotifications(userNotificationSettings)) {
			logger.debug("User Notification Settings updated Successfully");
			status.setStatus(Status.SUCCESS,"User Notification Settings updated Successfully" );
			}
		
		return new ResponseEntity<String>(ResponseConstructors.filterJsonForFieldAndViews(
				"all", "", status), HttpStatus.OK);
		}
		
}

