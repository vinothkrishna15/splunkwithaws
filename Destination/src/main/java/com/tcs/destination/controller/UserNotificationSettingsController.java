package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.NotificationSettingsGroupMappingT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.bean.UserNotificationSettingsT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.UserNotificationSettingsService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/usernotificationsettings")
public class UserNotificationSettingsController {

	private static final Logger logger = LoggerFactory
			.getLogger(UserNotificationSettingsController.class);

	@Autowired
	UserNotificationSettingsService userNotificationSettingsService;

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addUserNotificationSettings(
			@RequestBody List<UserNotificationSettingsT> userNotificationSettings,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of add user notification settings");
		logger.debug("Inside UserNotificationSettingsController /usernotificationsettings POST");
		Status status = new Status();
		try {
			if (userNotificationSettingsService
					.saveUserNotifications(userNotificationSettings)) {
				logger.debug("User notification settings have been added successfully");
				status.setStatus(Status.SUCCESS,
						"User notification settings have been added successfully");
			}
			logger.info("End of add user notification settings");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while adding user notification settings");
		}

	}

	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> updateUserNotificationSettings(
			@RequestBody List<UserNotificationSettingsT> userNotificationSettings,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of update user notification settings");
		logger.debug("Inside UserNotificationSettingsController /usernotificationsettings PUT");
		Status status = new Status();
		try {
			if (userNotificationSettingsService
					.saveUserNotifications(userNotificationSettings)) {
				logger.debug("User notification settings have been updated successfully");
				status.setStatus(Status.SUCCESS,
						"User notification settings have been updated successfully");
			}
			logger.info("End of update user notification settings");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while updating user notification settings");
		}

	}

}