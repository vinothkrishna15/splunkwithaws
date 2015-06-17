package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.UserNotificationSettingsConditionsT;
import com.tcs.destination.bean.UserNotificationSettingsT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.controller.UserDetailsController;
import com.tcs.destination.data.repository.UserNotificationSettingsConditionRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.exception.DestinationException;

@Component
public class UserNotificationSettingsService {

	private static final Logger logger = LoggerFactory
			.getLogger(UserNotificationSettingsService.class);

	@Autowired
	UserNotificationSettingsConditionRepository userNotificationSettingsConditionRepository;

	@Autowired
	UserNotificationSettingsRepository userNotificationSettingsRepository;

	public boolean addOrUpdateUserNotifications(
			List<UserNotificationSettingsT> userNotificationSettingsList)
			throws DestinationException {
		logger.debug("Inside addOrUpdateUserNotifications Service");
		for (UserNotificationSettingsT userNotificationSettings : userNotificationSettingsList) {
			if (userNotificationSettings
					.getUserNotificationSettingsConditionsT() != null) {
				try {
					userNotificationSettingsConditionRepository
							.save(userNotificationSettings
									.getUserNotificationSettingsConditionsT());
				} catch (Exception e) {
					logger.error("INTERNAL_SERVER_ERROR " + e.getMessage());
					throw new DestinationException(
							HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
				}
			}
		}
		if (userNotificationSettingsRepository
				.save(userNotificationSettingsList) != null) {
			logger.debug("UserNotificationSettings Inserted successfully");
			return true;
		} else {
			logger.error("UserNotificationSettings are not inserted/updated successfully");
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"UserNotificationSettings are not inserted/updated successfully");
		}
	}

	// public boolean updateUserNotifications(
	// List<UserNotificationSettingsT> userNotificationSettings)
	// throws DestinationException {
	//
	// for (UserNotificationSettingsT uns : userNotificationSettings) {
	// if (uns.getUserNotificationSettingsConditionsT() != null) {
	// try {
	// List<UserNotificationSettingsConditionsT> unsc =
	// (List<UserNotificationSettingsConditionsT>)
	// userNotificationSettingsConditionRepository
	// .save(uns.getUserNotificationSettingsConditionsT());
	// } catch (Exception e) {
	// throw new DestinationException(HttpStatus.BAD_REQUEST,
	// e.getMessage());
	// }
	// }
	// }
	// try {
	// return userNotificationSettingsRepository
	// .save(userNotificationSettings) != null;
	// } catch (Exception e) {
	// throw new DestinationException(HttpStatus.BAD_REQUEST,
	// e.getMessage());
	// }
	// }

}
