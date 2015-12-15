package com.tcs.destination.controller;

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

import com.tcs.destination.bean.PushNotificationRegistrationT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.PushNotificationService;
import com.tcs.destination.utils.ResponseConstructors;
/**
 * 
 * This Controller handles notifications
 *
 */
@RestController
@RequestMapping("/push")
public class PushNotificationController {

	private static final Logger logger = LoggerFactory
			.getLogger(PushNotificationController.class);

	@Autowired
	PushNotificationService pushNotificationService;

	/**
	 * This method is used to register for push notification
	 * @param pushNotification
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addPushNotificationReg(
			@RequestBody PushNotificationRegistrationT pushNotification,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside PushNotificationController / Start of add Push Notification");
		try {
			Status status = new Status();
			status.setStatus(Status.FAILED, "");
			if (pushNotificationService.addPushNotification(pushNotification)) {
				status.setStatus(Status.SUCCESS,
						"Push Notification Registration done Successfully");
			}
			logger.info("Inside PushNotificationController / End of add Push Notification");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			logger.error("Destination Exception" + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while registering for push notification");
		}
	}

	/**
	 * This method is used to update push notification registration
	 * @param pushNotification
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> updatePushNotificationReg(
			@RequestBody PushNotificationRegistrationT pushNotification,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside PushNotificationController / Start of edit Push Notification");
		try {
			Status status = new Status();
			status.setStatus(Status.FAILED, "");
			if (pushNotificationService
					.updatePushNotification(pushNotification)) {
				status.setStatus(Status.SUCCESS,
						"Push Notification Registration Update Successfully");
			}
			logger.info("Inside PushNotificationController / End of edit Push Notification");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			logger.error("Destination Exception" + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while updating push notification registration");
		}
	}

	/**
	 * This method is used to delete push notification registration
	 * @param userId
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	public @ResponseBody ResponseEntity<String> deletePushNotificationReg(
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside PushNotificationController / Start of delete Push Notification");
		try {
			Status status = new Status();
			status.setStatus(Status.FAILED, "");
			if (pushNotificationService.deletePushNotificRecords(userId)) {
				status.setStatus(Status.SUCCESS,
						"Push Notification Registration Deleted Successfully");
			}
			logger.info("Inside PushNotificationController / End of delete Push Notification");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			logger.error("Destination Exception" + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while deleting push notification registration");
		}
	}

}
