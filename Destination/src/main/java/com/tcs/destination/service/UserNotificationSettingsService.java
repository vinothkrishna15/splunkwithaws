package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.NotificationEventGroupMappingT;
import com.tcs.destination.bean.NotificationSettingsEventMappingT;
import com.tcs.destination.bean.NotificationSettingsGroupMappingT;
import com.tcs.destination.bean.UserNotificationSettingsT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.NotificationSettingsGroupMappingRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsConditionRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.UserGroup;
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

	@Autowired
	UserRepository userRepository;

	@Transactional
	public boolean saveUserNotifications(
			List<UserNotificationSettingsT> userNotificationSettingsList)
			throws DestinationException {
		logger.debug("Inside addOrUpdateUserNotifications() service");
		// Save notification settings conditions first
		for (UserNotificationSettingsT userNotificationSettings : userNotificationSettingsList) {
			if (userNotificationSettings
					.getUserNotificationSettingsConditionsTs() != null) {
				try {
					userNotificationSettingsConditionRepository
							.save(userNotificationSettings
									.getUserNotificationSettingsConditionsTs());
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

		if (!userId.equalsIgnoreCase(DestinationUtils.getCurrentUserDetails()
				.getUserId())) {
			throw new DestinationException(HttpStatus.UNAUTHORIZED,
					"This user is not authorised to view the deetings of the specified user");
		}

		List<NotificationSettingsGroupMappingT> notificationSettingsGroupMappingTs = (List<NotificationSettingsGroupMappingT>) notificationSettingsGroupMappingRepository
				.findAll(new Sort(Sort.Direction.ASC, "groupId"));
		if (notificationSettingsGroupMappingTs != null)
			for (NotificationSettingsGroupMappingT notificationSettingsGroupMappingT : notificationSettingsGroupMappingTs) {
				List<NotificationEventGroupMappingT> notificationEventGroupMappingTs = notificationSettingsGroupMappingT
						.getNotificationEventGroupMappingTs();
				if (notificationEventGroupMappingTs != null) {
					for (NotificationEventGroupMappingT notificationEventGroupMappingT : notificationEventGroupMappingTs) {
						NotificationSettingsEventMappingT notificationSettingsEventMappingT = notificationEventGroupMappingT
								.getNotificationSettingsEventMappingT();
						List<UserNotificationSettingsT> userNotificationSettingsTs = userNotificationSettingsRepository
								.findByUserIdAndEventIdOrderByEventIdAsc(
										userId,
										notificationSettingsEventMappingT
												.getEventId());
						if (userNotificationSettingsTs != null)
							for (UserNotificationSettingsT userNotificationSettingsT : userNotificationSettingsTs) {
								userNotificationSettingsT.getUserT()
										.setUserNotificationSettingsTs(null);
							}
						notificationSettingsEventMappingT
								.setUserNotificationSettingsTs(userNotificationSettingsTs);
						notificationSettingsEventMappingT
								.setUserNotificationSettingsConditionsTs(userNotificationSettingsConditionRepository
										.findByUserIdAndEventId(userId,
												notificationSettingsEventMappingT
														.getEventId()));
					}
				}
			}

		UserT userT = userRepository.findOne(userId);
		if (UserGroup.contains(userT.getUserGroup())) {
			int index = Integer.MAX_VALUE;
			switch (UserGroup.valueOf(UserGroup.getName(userT.getUserGroup()))) {
			case BDM:
				index = 3;
				removeNotificationSettingsFromIndex(
						notificationSettingsGroupMappingTs, index);
				removeNotificationSettingsFromIndex(
						notificationSettingsGroupMappingTs, index);
				break;
			case BDM_SUPERVISOR:
				index = 4;
				removeNotificationSettingsFromIndex(
						notificationSettingsGroupMappingTs, index);
				break;
			}
		}

		return notificationSettingsGroupMappingTs;
	}

	private void removeNotificationSettingsFromIndex(
			List<NotificationSettingsGroupMappingT> notificationSettingsGroupMappingTs,
			int index) {
		if (index < notificationSettingsGroupMappingTs.size())
			notificationSettingsGroupMappingTs.remove(index);
	}
}