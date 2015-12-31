package com.tcs.destination.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.UserT;
import com.tcs.destination.bean.UserTaggedFollowedT;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.FollowedRepository;
import com.tcs.destination.data.repository.NotificationEventGroupMappingTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.data.repository.UserNotificationsRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.FollowNotifications;
import com.tcs.destination.utils.DateUtils;

/**
 * This service handles Followed and its other functionalities.
 */
@Service
public class FollowedService {

	private static final Logger logger = LoggerFactory
			.getLogger(FollowedService.class);

	@Autowired
	FollowedRepository followedRepository;

	@Autowired
	UserNotificationsRepository userNotificationsTRepository;

	@Autowired
	UserNotificationSettingsRepository userNotificationSettingsRepo;

	@Autowired
	ThreadPoolTaskExecutor notificationsTaskExecutor;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	NotificationEventGroupMappingTRepository notificationEventGroupMappingTRepository;

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * 
	 * @param userId
	 * @param entityType
	 * @return
	 * @throws Exception
	 */
	public List<UserTaggedFollowedT> findFollowedFor(String userId,
			String entityType) throws Exception {

		logger.debug("satrt: Inside findFollowedFor Service");

		if (EntityType.contains(entityType)) {

			switch (EntityType.valueOf(entityType)) {

			case CONNECT:
			case OPPORTUNITY:
			case TASK:
				List<UserTaggedFollowedT> userFollowed = followedRepository
						.findByUserIdAndEntityTypeOrderByCreatedModifiedDatetimeDesc(
								userId, entityType);

				if (userFollowed.isEmpty()) {
					logger.error("NOT_FOUND: No Relevent Data Found in the database");
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"No Relevent Data Found in the database");
				} else {
					logger.debug("End: Inside findFollowedFor Service");
					return userFollowed;
				}

			default:
				logger.error("NOT_FOUND: You cannot follow " + entityType);
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"You cannot follow " + entityType);
			}
		} else {
			logger.error("NOT_FOUND: No such Entity type exists. Please ensure your entity type.");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No such Entity type exists. Please ensure your entity type");
		}
	}

	/**
	 * method to add a followed
	 * @param followed
	 * @return
	 * @throws Exception
	 */
	public boolean addFollow(UserTaggedFollowedT followed) throws Exception {
		logger.debug("Start:Inside addFollowed Followed Service");
		if (EntityType.contains(followed.getEntityType())) {
			switch (EntityType.valueOf(followed.getEntityType())) {
			case CONNECT:
				if (followed.getConnectId() == null) {
					logger.error("BAD_REQUEST: Connect ID can not be empty");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Connect ID can not be empty");
				} else {
					followed.setOpportunityId(null);
					followed.setTaskId(null);
					// Prevent users from following the same Connect once Again
					if (followedRepository.findByUserIdAndConnectId(
							followed.getUserId(), followed.getConnectId()) != null)
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"This Entity is already followed by the user");
					// Prevent users from following their own entities
					List<String> ownerIds = connectRepository
							.findOwnersOfConnect(followed.getConnectId());
					if (ownerIds.contains(followed.getUserId()))
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"You cannot follow your own Connect");
				}
				break;
			case OPPORTUNITY:
				if (followed.getOpportunityId() == null) {
					logger.error("BAD_REQUEST: Opportunity ID can not be empty");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Opportunity ID can not be empty");
				} else {
					followed.setConnectId(null);
					followed.setTaskId(null);
					// Prevent users from following the same Opportunity once
					// Again
					if (followedRepository.findByUserIdAndOpportunityId(
							followed.getUserId(), followed.getOpportunityId()) != null)
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"This Entity is already followed by the user");
					// Prevent users from following their own entities
					List<String> ownerIds = opportunityRepository
							.getAllOwners(followed.getOpportunityId());
					if (ownerIds.contains(followed.getUserId()))
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"You cannot follow your own Opportunity");
				}
				break;

			case TASK:
				if (followed.getTaskId() == null) {
					logger.error("BAD_REQUEST: Task ID can not be empty");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"TASK ID can not be empty");
				} else {
					followed.setConnectId(null);
					followed.setOpportunityId(null);
					// Prevent users from following the same Task once Again
					if (followedRepository.findByUserIdAndTaskId(
							followed.getUserId(), followed.getTaskId()) != null)
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"This Entity is already followed by the user");
					// Prevent users from following their own entities
					List<String> ownerIds = taskRepository
							.findOwnersOfTask(followed.getOpportunityId());
					if (ownerIds.contains(followed.getUserId()))
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"You cannot follow your own Task");
				}
				break;

			default:
				logger.error("NOT_FOUND: You cannot follow "
						+ followed.getEntityType());
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"You cannot follow " + followed.getEntityType());
			}
			followed.setCreatedModifiedDatetime(DateUtils.getCurrentTimeStamp());
			try {
				UserTaggedFollowedT followDBObj = followedRepository
						.save(followed);
				processNotification(followDBObj);
				logger.debug("End: Inside addFollowed Service");
				return followDBObj != null;
			} catch (Exception e) {
				logger.error("BAD_REQUEST" + e.getMessage());
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						e.getMessage());
			}
		}
		logger.error("BAD_REQUEST: Invalid Entity Type");
		throw new DestinationException(HttpStatus.BAD_REQUEST,
				"Invalid Entity Type");
	}

	/**
	 * This method initializes the required fields(according to each entity) for
	 *  the notifications executor and starts it
	 * @param followDBObj
	 */
	private void processNotification(UserTaggedFollowedT followDBObj) {
		logger.debug("start:Calling processNotifications() Followed Service");

		FollowNotifications followNotificationsHelper = new FollowNotifications();
		String taskId = followDBObj.getTaskId();
		String connectId = followDBObj.getConnectId();
		String opportunityId = followDBObj.getOpportunityId();

		String followUserId = followDBObj.getUserId();
		String createdUserId = followDBObj.getCreatedModifiedBy();

		UserT followUser = userRepository.findOne(followUserId);
		UserT createdUser = userRepository.findOne(createdUserId);

		followNotificationsHelper.setFollowUserId(followUserId);
		followNotificationsHelper.setCreatedUserId(createdUserId);
		followNotificationsHelper.setFollowUser(followUser);
		followNotificationsHelper.setCreatedUser(createdUser);
		followNotificationsHelper
				.setOpportunityRepository(opportunityRepository);
		followNotificationsHelper.setConnectRepository(connectRepository);
		followNotificationsHelper.setTaskRepository(taskRepository);
		followNotificationsHelper
				.setNotificationEventGroupMappingTRepository(notificationEventGroupMappingTRepository);
		followNotificationsHelper
				.setUserNotificationsTRepository(userNotificationsTRepository);
		followNotificationsHelper
				.setUserNotificationSettingsRepo(userNotificationSettingsRepo);
		followNotificationsHelper.setEntityManagerFactory(entityManager
				.getEntityManagerFactory());

		if (taskId != null) {
			followNotificationsHelper.setEntityId(taskId);
			followNotificationsHelper.setEntityType(EntityType.TASK.name());
			followNotificationsHelper.setCrudRepository(taskRepository);
			followNotificationsHelper.setEventId(2);
			notificationsTaskExecutor.execute(followNotificationsHelper);
		} else if (connectId != null) {
			followNotificationsHelper.setEntityId(connectId);
			followNotificationsHelper.setEntityType(EntityType.CONNECT.name());
			followNotificationsHelper.setCrudRepository(connectRepository);
			followNotificationsHelper.setEventId(3);
			notificationsTaskExecutor.execute(followNotificationsHelper);
		} else if (opportunityId != null) {
			followNotificationsHelper.setEntityId(opportunityId);
			followNotificationsHelper.setEntityType(EntityType.OPPORTUNITY
					.name());
			followNotificationsHelper.setCrudRepository(opportunityRepository);
			followNotificationsHelper.setEventId(3);
			notificationsTaskExecutor.execute(followNotificationsHelper);
		}
		logger.debug("End:Calling processNotifications() Followed Service");
	}

	/**
	 * to implement un-follow functionalities
	 * @param userTaggedFollowedId
	 * @throws Exception
	 */
	public void unFollow(String userTaggedFollowedId) throws Exception {
		logger.debug("start:Calling unFollow() method");
		try {
			if (userTaggedFollowedId != null)
				followedRepository.delete(userTaggedFollowedId);
			logger.debug("End:Calling unFollow() method");
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR: " + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}

	}
}