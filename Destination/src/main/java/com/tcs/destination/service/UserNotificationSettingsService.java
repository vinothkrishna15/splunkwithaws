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
import com.tcs.destination.bean.NotificationTypeEventMappingT;
import com.tcs.destination.bean.UserNotificationSettingsT;
import com.tcs.destination.bean.UserSubscriptions;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.NotificationSettingsGroupMappingRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsConditionRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.data.repository.UserSubscriptionsRepository;
import com.tcs.destination.enums.NotificationSettingEvent;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DestinationUtils;

/**
 * 
 * This service is used to handle user notification settings
 *
 */
@Service
public class UserNotificationSettingsService {

	private static final Logger logger = LoggerFactory
			.getLogger(UserNotificationSettingsService.class);

	@Autowired
	UserNotificationSettingsConditionRepository userNotificationSettingsConditionRepository;

	@Autowired
	UserNotificationSettingsRepository userNotificationSettingsRepository;
	

	@Autowired
	UserSubscriptionsRepository userSubscriptionRepository;


	@Autowired
	NotificationSettingsGroupMappingRepository notificationSettingsGroupMappingRepository;

	@Autowired
	UserRepository userRepository;

	/**
	 * This method is used to save details of user notification settings
	 * @param userNotificationSettingsList
	 * @return
	 * @throws DestinationException
	 */
	@Transactional
	public boolean saveUserNotifications(
			List<UserNotificationSettingsT> userNotificationSettingsList)
			throws DestinationException {
		logger.debug("Begin:Inside saveUserNotifications() UserNotificationSettings service");
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
				logger.debug("End:Inside saveUserNotifications() UserNotificationSettings service");
				return true;
			} else {
				logger.error("Error occurred while adding UserNotificationSettings settings");
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Error occurred while adding User notification settings");
			}
		}
		catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR: " + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
	}

	@Transactional
	public boolean saveUserNotificationsnew(
			List<UserSubscriptions> userSubscription)
			throws DestinationException {
		logger.debug("Begin:Inside saveUserNotifications() UserNotificationSettings service");
		// Save notification settings conditions first
		for ( UserSubscriptions userSubscriptions : userSubscription) {
			if (userSubscriptions
					.getUserNotificationSettingsConditionsTs() != null) {
				try {
					userNotificationSettingsConditionRepository
							.save(userSubscriptions
									.getUserNotificationSettingsConditionsTs());
				} catch (Exception e) {
					logger.error("INTERNAL_SERVER_ERROR: " + e.getMessage());
					throw new DestinationException(
							HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
				}
			}
		}
		try {
			if (userSubscriptionRepository
					.save(userSubscription) != null) {
				logger.debug("End:Inside saveUserNotifications() UserNotificationSettings service");
				return true;
			} else {
				logger.error("Error occurred while adding UserNotificationSettings settings");
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Error occurred while adding User notification settings");
			}
		}
		catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR: " + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
	}

	/**
	 * This method is used to retrieve the details of user notification settings
	 * based on the user Id specified
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public List<NotificationSettingsGroupMappingT> getUserNotificationSettings(
			String userId) throws Exception {
		logger.debug("Begin:Inside getUserNotificationSettings() UserNotificationSettings service");
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
			case PRACTICE_OWNER:	
				index = 3;
				removeNotificationSettingsFromIndex(
						notificationSettingsGroupMappingTs, index);
				removeNotificationSettingsFromIndex(
						notificationSettingsGroupMappingTs, index);
				break;
			case BDM_SUPERVISOR:
			case PRACTICE_HEAD:	
				index = 4;
				removeNotificationSettingsFromIndex(
						notificationSettingsGroupMappingTs, index);
				break;
			default:
			}
		}
		logger.debug("End:Inside getUserNotificationSettings() UserNotificationSettings service");
		return notificationSettingsGroupMappingTs;
	}
	
	


	/**
	 * This method is used to delete notification settings based on index 
	 * @param notificationSettingsGroupMappingTs
	 * @param index
	 */
	private void removeNotificationSettingsFromIndex(
			List<NotificationSettingsGroupMappingT> notificationSettingsGroupMappingTs,
			int index) {
		logger.debug("Begin:Inside removeNotificationSettingsFromIndex() UserNotificationSettings service");
		if (index < notificationSettingsGroupMappingTs.size())
			notificationSettingsGroupMappingTs.remove(index);
		logger.debug("End:Inside removeNotificationSettingsFromIndex() UserNotificationSettings service");

	}

	/**
	 * fetch all notification settings of current user 
	 * @return List of user subscription
	 */
	public List<UserSubscriptions> getUserNotificationSettingsNew() {
		logger.info("Begin-> UserNotificationSettingsService :: getUserNotificationSettingsNew service");
		
		String userId = DestinationUtils.getCurrentUserId();
		List<UserSubscriptions> subscriptions = userSubscriptionRepository.findByUserId(userId);
		
		for (UserSubscriptions userSubscription : subscriptions) {//add condition
			Integer eventId = userSubscription.getNotificationTypeEventMappingT().getEventId();
			NotificationSettingEvent event = NotificationSettingEvent.getByValue(eventId);
			if(event != null && event == NotificationSettingEvent.COLLAB_CONDITION) {//fetch conditions only for collab conditions
				userSubscription.setUserNotificationSettingsConditionsTs(userNotificationSettingsConditionRepository.findByUserIdAndEventId(userId, eventId));
			}
			
			prepareSubscriptions(userSubscription.getNotificationTypeEventMappingT());
			
		}
		logger.info("End-> UserNotificationSettingsService :: getUserNotificationSettingsNew service");
		return subscriptions;
	}

	/**
	 * remove the identity object to avoid the looping in json construction
	 * @param notificationTypeEventMappingT
	 */
	private void prepareSubscriptions(NotificationTypeEventMappingT notificationTypeEventMappingT) {
		notificationTypeEventMappingT.setUserSubscriptions(null);
	}
}