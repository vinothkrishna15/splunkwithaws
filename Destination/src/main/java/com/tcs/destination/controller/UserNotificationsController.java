package com.tcs.destination.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.NotificationSettingsGroupMappingT;
import com.tcs.destination.bean.UserNotificationsT;
import com.tcs.destination.service.UserNotificationSettingsService;
import com.tcs.destination.service.UserNotificationsService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/notification")
public class UserNotificationsController {

	@Autowired
	UserNotificationsService userNotificationsService;

	@Autowired
	UserNotificationSettingsService userNotificationSettingsService;

	private static final Logger logger = LoggerFactory
			.getLogger(UserNotificationsController.class);

	@RequestMapping(value = "/portal", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findNotifications(
			@RequestParam(value = "userId", defaultValue = "") String userId,
			@RequestParam(value = "read", defaultValue = "") String read,
			@RequestParam("from") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam("to") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside UserNotificationsController /user GET");
		List<UserNotificationsT> userNotificationsT = userNotificationsService.getNotifications(userId, read, fromDate.getTime(), toDate.getTime());
		return new ResponseEntity<String>(ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				userNotificationsT), HttpStatus.OK);

		// List<UserNotificationsT> userNotificationsT =
		// userNotificationsService
		// .getNotifications(userId, read);

	}

	@RequestMapping(value = "/settings", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getUserNotificationSettings(
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside UserNotificationSettingsController/usernotificationsettings?userId="
				+ userId + " GET");
		List<NotificationSettingsGroupMappingT> notificationSettingsGroupMappingT = userNotificationSettingsService
				.getUserNotificationSettings(userId);
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews(fields, view,
						notificationSettingsGroupMappingT), HttpStatus.OK);
	}                                                                      

}
