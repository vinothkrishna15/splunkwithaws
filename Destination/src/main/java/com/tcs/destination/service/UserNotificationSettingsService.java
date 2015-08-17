package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.UserNotificationSettingsConditionsT;
import com.tcs.destination.bean.UserNotificationSettingsT;
import com.tcs.destination.data.repository.UserNotificationSettingsConditionRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.StringUtils;

@Service
public class UserNotificationSettingsService {

	private static final Logger logger = LoggerFactory
			.getLogger(UserNotificationSettingsService.class);

	@Autowired
	UserNotificationSettingsConditionRepository userNotificationSettingsConditionRepository;

	@Autowired
	UserNotificationSettingsRepository userNotificationSettingsRepository;

	@Transactional
	public boolean saveUserNotifications(List<UserNotificationSettingsT> userNotificationSettingsList)
			throws DestinationException {
		logger.debug("Inside addOrUpdateUserNotifications() service");
		//Save notification settings conditions first
		for (UserNotificationSettingsT userNotificationSettings : userNotificationSettingsList) {
			if (userNotificationSettings.getUserNotificationSettingsConditionsT() != null) {
				try {
					userNotificationSettingsConditionRepository.save(userNotificationSettings
							.getUserNotificationSettingsConditionsT());
				} catch (Exception e) {
					logger.error("INTERNAL_SERVER_ERROR: " + e.getMessage());
					throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
				}
			}
		}
		try {
			if (userNotificationSettingsRepository
					.save(userNotificationSettingsList) != null) {
				logger.debug("User notification settings have been added successfully");
				return true;
			} else {
				logger.error("Error occurred while adding User notification settings");
				throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Error occurred while adding User notification settings");
			}
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR: " + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	public List<UserNotificationSettingsT> getUserNotificationSettings(String userId) throws Exception {
		List<UserNotificationSettingsT> userNotificationSettingsList = null;
		if (!StringUtils.isEmpty(userId)) {
			userNotificationSettingsList = userNotificationSettingsRepository.findByUserIdAndIsactive(userId, Constants.Y);
			if (userNotificationSettingsList != null && !userNotificationSettingsList.isEmpty()) {
				// Add user notification settings conditions
				for (UserNotificationSettingsT userNotificationSettings : userNotificationSettingsList) {
					List<UserNotificationSettingsConditionsT> notificationSettingsConditions = 
							userNotificationSettingsConditionRepository.findByUserIdAndEventId(
									userNotificationSettings.getUserId(), userNotificationSettings.getEventId());
					if (notificationSettingsConditions != null && !notificationSettingsConditions.isEmpty()) {
						userNotificationSettings.setUserNotificationSettingsConditionsT(notificationSettingsConditions);
					}
				}
			} else {
				logger.error("NOT_FOUND: User notification settings not found for user: {}", userId);
				throw new DestinationException(HttpStatus.NOT_FOUND, "User notification settings not found");
			}
		} else {
			logger.error("BAD_REQUEST: UserId is required");
			throw new DestinationException(HttpStatus.BAD_REQUEST, "UserId is required");
		}
		return userNotificationSettingsList;
	}
}