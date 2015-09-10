package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.NotificationEventGroupMappingT;
import com.tcs.destination.bean.NotificationSettingsEventMappingT;
import com.tcs.destination.bean.NotificationSettingsGroupMappingT;
import com.tcs.destination.bean.UserNotificationSettingsT;
import com.tcs.destination.data.repository.NotificationSettingsGroupMappingRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsConditionRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DestinationUtils;

@Service
public class UserNotificationSettingsService {

	private static final Logger logger = LoggerFactory
			.getLogger(UserNotificationSettingsService.class);

	@Autowired
	UserNotificationSettingsConditionRepository userNotificationSettingsConditionRepository;

	@Autowired
	UserNotificationSettingsRepository userNotificationSettingsRepository;

	@Autowired
	NotificationSettingsGroupMappingRepository notificationSettingsGroupMappingRepository;

	@Transactional
	public boolean saveUserNotifications(
			List<UserNotificationSettingsT> userNotificationSettingsList)
			throws DestinationException {
		logger.debug("Inside addOrUpdateUserNotifications() service");
		// Save notification settings conditions first
		for (UserNotificationSettingsT userNotificationSettings : userNotificationSettingsList) {
			if (userNotificationSettings
					.getUserNotificationSettingsConditionsT() != null) {
				try {
					userNotificationSettingsConditionRepository
							.save(userNotificationSettings
									.getUserNotificationSettingsConditionsT());
				} catch (Exception e) {
					logger.error("INTERNAL_SERVER_ERROR: " + e.getMessage());
					throw new DestinationException(
							HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
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
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Error occurred while adding User notification settings");
			}
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR: " + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
	}

	public List<NotificationSettingsGroupMappingT> getUserNotificationSettings(
			String userId) throws Exception {

		if(!userId.equalsIgnoreCase(DestinationUtils.getCurrentUserDetails().getUserId())){
			throw new DestinationException(HttpStatus.UNAUTHORIZED, "This user is not authorised to view the deetings of the specified user");
		}
		
		List<NotificationSettingsGroupMappingT> notificationSettingsGroupMappingTs = (List<NotificationSettingsGroupMappingT>) notificationSettingsGroupMappingRepository
				.findAll();
		if (notificationSettingsGroupMappingTs != null)
			for (NotificationSettingsGroupMappingT notificationSettingsGroupMappingT : notificationSettingsGroupMappingTs) {
				List<NotificationEventGroupMappingT> notificationEventGroupMappingTs = notificationSettingsGroupMappingT
						.getNotificationEventGroupMappingTs();
				if (notificationEventGroupMappingTs != null) {
					for (NotificationEventGroupMappingT notificationEventGroupMappingT : notificationEventGroupMappingTs) {
						NotificationSettingsEventMappingT notificationSettingsEventMappingT = notificationEventGroupMappingT
								.getNotificationSettingsEventMappingT();
						List<UserNotificationSettingsT> userNotificationSettingsTs=userNotificationSettingsRepository
								.findByUserIdAndEventId(userId,
										notificationSettingsEventMappingT
												.getEventId());
						if(userNotificationSettingsTs!=null)
							for(UserNotificationSettingsT userNotificationSettingsT:userNotificationSettingsTs)
							{
								userNotificationSettingsT.setNotificationSettingsModeMappingT(null);
							}
						notificationSettingsEventMappingT
								.setUserNotificationSettingsTs(userNotificationSettingsTs);
					}
				}
			}

		return notificationSettingsGroupMappingTs;
	}
}