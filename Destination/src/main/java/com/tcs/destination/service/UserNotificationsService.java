package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.Calendar;
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
import com.tcs.destination.bean.UserNotificationsT;
import com.tcs.destination.data.repository.UserNotificationSettingsConditionRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.data.repository.UserNotificationsRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DestinationUtils;

@Service
public class UserNotificationsService {

	private static final Logger logger = LoggerFactory
			.getLogger(UserNotificationsService.class);

	@Autowired
	UserNotificationsRepository userNotificationsRepository;

	@Autowired
	UserNotificationSettingsConditionRepository userNotificationSettingsConditionRepository;

	@Autowired
	UserNotificationSettingsRepository userNotificationSettingsRepository;

	public List<UserNotificationsT> getNotifications(String userId,
			String read, long fromTime, long toTime)
			throws DestinationException {
		try {
			if (!DestinationUtils.getCurrentUserDetails().getUserId()
					.equalsIgnoreCase(userId))
				throw new DestinationException(HttpStatus.FORBIDDEN,
						"User Id and Login User Detail does not match");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<UserNotificationsT> userNotificationsTs = null;

		{
			Calendar toCalendar = Calendar.getInstance();
			toCalendar.setTimeInMillis(toTime);
			toCalendar.set(Calendar.DATE, toCalendar.get(Calendar.DATE) + 1);
			toTime = toCalendar.getTimeInMillis();
		}

		if (read.equals("")) {
			userNotificationsTs = userNotificationsRepository
					.getOptedPortalNotifications(userId,
							new Timestamp(fromTime), new Timestamp(toTime));
		} else {
			userNotificationsTs = userNotificationsRepository
					.getOptedPortalNotificationsWithRead(userId, new Timestamp(
							fromTime), new Timestamp(toTime), read);
		}
		if (userNotificationsTs == null || userNotificationsTs.size() == 0)
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Notification is available.");

		for (UserNotificationsT notification : userNotificationsTs) {
			NotificationSettingsEventMappingT notificationSettingsEventMappingT = notification
					.getNotificationSettingsEventMappingT();

			if (notificationSettingsEventMappingT != null) {
				List<NotificationEventGroupMappingT> notificationEventGroupMappingTs = notificationSettingsEventMappingT
						.getNotificationEventGroupMappingTs();
				if (notificationEventGroupMappingTs != null
						&& !notificationEventGroupMappingTs.isEmpty()) {
					for (NotificationEventGroupMappingT notificationEventGroupMappingT : notificationEventGroupMappingTs) {
						NotificationSettingsGroupMappingT notificationSettingsGroupMappingT = notificationEventGroupMappingT
								.getNotificationSettingsGroupMappingT();
						if (notificationSettingsGroupMappingT != null) {
							notificationSettingsGroupMappingT
									.setNotificationEventGroupMappingTs(null);
						}

					}
				}
			}
		}
		return userNotificationsTs;
	}

	/**
	 * this method updates the user notification settings
	 * 
	 * @param userNotificationSettings
	 * @return boolean
	 * @throws Exception
	 */

	@Transactional
	public boolean saveUserNotifications(
			List<UserNotificationSettingsT> userNotificationSettings)
			throws Exception {
		boolean isUpdated = false;
		for (UserNotificationSettingsT userNotificationSettingsT : userNotificationSettings) {
			if (userNotificationSettingsT.getUserNotificationSettingsId() != null) {
				userNotificationSettingsRepository
						.save(userNotificationSettingsT);

				if (userNotificationSettingsT
						.getUserNotificationSettingsConditionsTs() != null) {
					userNotificationSettingsConditionRepository
							.save(userNotificationSettingsT
									.getUserNotificationSettingsConditionsTs());
				}

				if (userNotificationSettingsT
						.getDeleteUserNotificationSettingsConditionsTs() != null)
					userNotificationSettingsConditionRepository
							.delete(userNotificationSettingsT
									.getDeleteUserNotificationSettingsConditionsTs());

				logger.debug("User notification settings have been added successfully for "
						+ userNotificationSettingsT
								.getUserNotificationSettingsId());
				isUpdated = true;
			} else {
				throw new DestinationException(HttpStatus.NOT_FOUND,
						userNotificationSettingsT
								.getUserNotificationSettingsId()
								+ " is invalid user notification settings id");
			}
		}
		return isUpdated;
	}

	/**
	 * This service updates the read status
	 * 
	 * @param userNotificationIds
	 * @param read
	 * @return String
	 * @throws Exception
	 */
	public String updateReadStatus(List<String> userNotificationIds, String read)
			throws Exception {
		String status = "";
		String message = "No User Notification Id provided";

		if (userNotificationIds != null && userNotificationIds.size() != 0) {
			if (read.equalsIgnoreCase(Constants.NO)) {
				status = Constants.YES;
				message = "Marked as read";
			} else if (read.equalsIgnoreCase(Constants.YES)) {
				status = Constants.NO;
				message = "Marked as unread";
			} else {
				logger.error("BAD_REQUEST - Invalid read parameter provided");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid read parameter provided");
			}

			int updateCount = userNotificationsRepository.updateReadStatus(
					userNotificationIds, status);
			if (updateCount == 0) {
				message = "Invalid User Notification Id Provided";
			} else if (updateCount > 0) {
				message = updateCount + " " + message;
			}
		}

		return message;
	}
}
