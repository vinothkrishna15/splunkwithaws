package com.tcs.destination.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.tcs.destination.bean.UserNotificationSettingsT;
import com.tcs.destination.bean.UserNotificationsT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.UserNotificationSettingsService;
import com.tcs.destination.service.UserNotificationsService;
import com.tcs.destination.utils.DestinationUtils;
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
			@RequestParam(value = "read", defaultValue = "") String read,
			@RequestParam("from") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam("to") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of retreiving the user notifications");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		logger.debug("Inside UserNotificationsController /user GET");
		try {
			List<UserNotificationsT> userNotificationsT = userNotificationsService
					.getNotifications(userId, read, fromDate.getTime(),
							toDate.getTime());
			logger.info("End of retreiving the user notifications");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews(fields,
							view, userNotificationsT), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the user notifications");
		}

		// List<UserNotificationsT> userNotificationsT =
		// userNotificationsService
		// .getNotifications(userId, read);

	}

	@RequestMapping(value = "/settings", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getUserNotificationSettings(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of retreiving the user notification settings");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		logger.debug("Inside UserNotificationSettingsController/usernotificationsettings?userId="
				+ userId + " GET");
		try {
			List<NotificationSettingsGroupMappingT> notificationSettingsGroupMappingT = userNotificationSettingsService
					.getUserNotificationSettings(userId);
			logger.info("End of retreiving the user notification settings");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews(fields,
							view, notificationSettingsGroupMappingT),
					HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retreiving the user notification settings");
		}
	}

	/**
	 * This method updates the user notification settings
	 * 
	 * @param userNotificationSettings
	 * @return Status
	 * @throws Exception
	 */

	@RequestMapping(value = "/settings", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> saveUserNotificationSettings(
			@RequestBody List<UserNotificationSettingsT> userNotificationSettings)
			throws DestinationException {
		logger.info("Start of update user notification settings");
		Status status = new Status();
		try {
			if (userNotificationsService
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
					"Backend error while updating the user notification settings");
		}

	}

	/**
	 * This controller updates the read status
	 * 
	 * @param userNotificationIds
	 * @param read
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */
	@RequestMapping(value = "/read", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> updateReadStatus(
			@RequestBody List<String> userNotificationIds,
			@RequestParam(value = "read") String read)
			throws DestinationException {
		logger.info("Start of update read status of list of user notifications");
		logger.debug("Inside UserNotificationsController /read PUT");
		String status = "";
		try {
			status = userNotificationsService.updateReadStatus(
					userNotificationIds, read);
			logger.info("End of update read status of list of user notifications");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while updating the read status for notifications :"
							+ userNotificationIds);
		}
	}
}