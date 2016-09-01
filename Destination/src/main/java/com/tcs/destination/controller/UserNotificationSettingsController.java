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

import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UserNotificationSettingsT;
import com.tcs.destination.bean.UserSubscriptions;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.UserNotificationSettingsService;
import com.tcs.destination.utils.DestinationMailUtils;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * This class deals with add and update of  UserNotificationSettings
 *
 */
@RestController
@RequestMapping("/usernotificationsettings")
public class UserNotificationSettingsController {

	private static final Logger logger = LoggerFactory
			.getLogger(UserNotificationSettingsController.class);

	@Autowired
	UserNotificationSettingsService userNotificationSettingsService;
	
	@Autowired
	DestinationMailUtils destinationMailUtils;

	/**
	 * @param userNotificationSettings
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addUserNotificationSettings(
			@RequestBody List<UserNotificationSettingsT> userNotificationSettings,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of add user notification settings");
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

	/**
	 * @param userNotificationSettings
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> updateUserNotificationSettings(
			@RequestBody List<UserNotificationSettingsT> userNotificationSettings,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of update user notification settings");
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
	
	/**
	 * used to update the user subscriptions
	 * @param userSubscription
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/updateseetingsnew", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> updateUserNotificationSettingsnew(
			@RequestBody List<UserSubscriptions> userSubscription,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of update user notification settings");
		Status status = new Status();
		try {
			if (userNotificationSettingsService
					.saveUserNotificationsnew(userSubscription)) {
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
	
	/**
	 * Retrieves the User subscriptions for a user
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getUserNotificationSettingsNew(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
					throws DestinationException {
		logger.info("UserNotificationSettingsController :: getUserNotificationSettingsNew - Start");
		try {
			List<UserSubscriptions> userSubscriptions = userNotificationSettingsService
					.getUserSubsriptions();
			logger.info("UserNotificationSettingsController :: End of getting user notification settings");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews(fields, view,
							userSubscriptions), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while getting user notification settings");
		}
		
	}
	
	/**
	 * Sends weekly report
	 * @param userSubscription
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/sendReport", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> mailWithImageBase64(
			@RequestParam(value = "size", defaultValue = "0") int size,
			@RequestParam(value = "ori", defaultValue = "0") int ori,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of sending weekly report");
		Status status = new Status();
		try {
			destinationMailUtils.sendWeeklyReport();
			logger.info("End of sending weekly report");
			status.setStatus(HttpStatus.OK.name(), "Weekly report sent");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while sending weekly report");
		}

	}

}