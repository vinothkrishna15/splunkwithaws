package com.tcs.destination.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.PushNotificationRegistrationT;
import com.tcs.destination.data.repository.PushNotificationRepository;
import com.tcs.destination.exception.DestinationException;

/**
 * This service handle push notification operations
 * @author tcs2
 *
 */
@Service
public class PushNotificationService {

	private static final Logger logger = LoggerFactory
			.getLogger(PushNotificationService.class);

	@Autowired
	PushNotificationRepository pushNotificationRepository;

	/**
	 * This method deletes a particular push notification record
	 * based on userId
	 * @param userId
	 * @return
	 * @throws DestinationException
	 */
	public boolean deletePushNotificRecords(String userId)
			throws DestinationException {
		logger.info("begin: inside deletePushNotificRecords() of PushNotificationService");
		PushNotificationRegistrationT pushNotificationRegistration = pushNotificationRepository
				.findByUserId(userId);

		if (pushNotificationRegistration != null) {
			pushNotificationRepository.delete(pushNotificationRegistration);
			logger.info("End: inside deletePushNotificRecords() of PushNotificationService");
			return true;

		} else {
			logger.error("UserNotificationSettingsId : " + userId
					+ " does not exist");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"UserNotificationSettingsId : " + userId
					+ " does not exist");
		}
	}

	public boolean addPushNotification(
			PushNotificationRegistrationT pushNotification)
					throws DestinationException {
		logger.info("begin: inside addPushNotification() of PushNotificationService");
		PushNotificationRegistrationT pushNotificationRegistration = pushNotificationRepository
				.findByUserId(pushNotification.getUserId());
		if (pushNotificationRegistration == null) {
			logger.info("End: inside addPushNotification() of PushNotificationService");
			return pushNotificationRepository.save(pushNotification) != null;
		} else {
			logger.error("UserNotificationSettingsId : "
					+ pushNotification.getUserId() + " already exist");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"UserNotificationSettingsId : "
							+ pushNotification.getUserId() + " already exist");
		}
	}

	public boolean updatePushNotification(
			PushNotificationRegistrationT pushNotification)
					throws DestinationException {
		try {
			logger.info("inside updatePushNotification() of PushNotificationService");
			return pushNotificationRepository.save(pushNotification) != null;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR " + e.getMessage());
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					e.getMessage());
		}
	}
}
