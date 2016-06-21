package com.tcs.destination.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.CollaborationCommentT;
import com.tcs.destination.controller.JobLauncherController;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.FollowedRepository;
import com.tcs.destination.data.repository.NotificationEventGroupMappingTRepository;
import com.tcs.destination.data.repository.NotificationsEventFieldsTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.data.repository.UserNotificationsRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.CommentType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.JobName;
import com.tcs.destination.enums.OperationType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.NotificationHelper;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.StringUtils;

/**
 * This service validates requests related to collaboration comments
 * and inserts them into the comments repository
 */
@Service
public class CollaborationCommentsService {

	private static final Logger logger = LoggerFactory
			.getLogger(CollaborationCommentsService.class);

	@Autowired
	CollaborationCommentsRepository commentsRepository;

	@Autowired
	NotificationsEventFieldsTRepository notificationEventFieldsTRepository;

	// Required for Notification
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private UserNotificationsRepository userNotificationsTRepository;

	@Autowired
	private UserNotificationSettingsRepository userNotificationSettingsRepo;

	@Autowired
	ThreadPoolTaskExecutor notificationsTaskExecutor;

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	FollowedRepository taggedFollowedRepository;

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	NotificationEventGroupMappingTRepository notificationEventGroupMappingTRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	JobLauncherController jobLauncherController;

	/**
	 * method to insert collaboration comments to comments repository
	 * @param comments
	 * @return
	 * @throws Exception
	 */
	public String insertComments(CollaborationCommentT comments)
			throws Exception {
		logger.debug("Begin:Inside insertComments() of CollaborationCommentsService");
		
		String returnVal = null;
		
		if(!StringUtils.isEmpty(comments.getCommentType())){
			if(!comments.getCommentType().equalsIgnoreCase(CommentType.AUTO.name())){
				comments.setUserId(DestinationUtils.getCurrentUserDetails().getUserId());
			} else {
				comments.setUserId(Constants.SYSTEM_USER);
			}
		}
		if (isValidComment(comments)) {
			try {
				CollaborationCommentT collaborationCommentT = commentsRepository
						.save(comments);
//				processNotifications(collaborationCommentT.getCommentId());
				returnVal = collaborationCommentT.getCommentId();
				if(collaborationCommentT.getCommentType().equals(CommentType.USER.name())) {
					
					if(collaborationCommentT.getEntityType().equals(EntityType.CONNECT.name())) {
						jobLauncherController.asyncJobLaunchForNotification(JobName.notification, EntityType.CONNECT, collaborationCommentT.getConnectId(),OperationType.CONNECT_COMMENT,collaborationCommentT.getUserId());
					}
					if(collaborationCommentT.getEntityType().equals(EntityType.OPPORTUNITY.name())) {
						jobLauncherController.asyncJobLaunchForNotification(JobName.notification, EntityType.OPPORTUNITY, collaborationCommentT.getOpportunityId(),OperationType.OPPORTUNITY_COMMENT,collaborationCommentT.getUserId());
					}
					
					if(collaborationCommentT.getEntityType().equals(EntityType.TASK.name())) {
						jobLauncherController.asyncJobLaunchForNotification(JobName.notification, EntityType.TASK, collaborationCommentT.getTaskId(),OperationType.TASK_COMMENT,collaborationCommentT.getUserId());
					}
					
				}
				
				
			} catch (Exception e) {
				logger.error("INTERNAL_SERVER_ERROR " + e.getMessage());
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Backend Error while posting comments");
			}
		}
		logger.debug("End:Inside insertComments() of CollaborationCommentsService");
		return returnVal;
	}

	/**
	 * method to process notifications
	 * @param commentId
	 */
	private void processNotifications(String commentId) {
		logger.debug("Begin:processNotifications() method of CollaborationCommentsService");
		NotificationHelper notificationsHelper = new NotificationHelper();
		notificationsHelper.setEntityId(commentId);
		notificationsHelper.setEntityType(EntityType.COMMENT.name());
		notificationsHelper
				.setNotificationsEventFieldsTRepository(notificationEventFieldsTRepository);
		notificationsHelper
				.setUserNotificationsTRepository(userNotificationsTRepository);
		notificationsHelper
				.setUserNotificationSettingsRepo(userNotificationSettingsRepo);
		notificationsHelper.setCrudRepository(commentsRepository);
		notificationsHelper.setOpportunityRepository(opportunityRepository);
		notificationsHelper
				.setTaggedFollowedRepostory(taggedFollowedRepository);
		notificationsHelper.setConnectRepository(connectRepository);
		notificationsHelper.setTaskRepository(taskRepository);
		notificationsHelper
				.setNotificationEventGroupMappingTRepository(notificationEventGroupMappingTRepository);
		notificationsHelper.setEntityManagerFactory(entityManager
				.getEntityManagerFactory());
		notificationsHelper.setUserRepository(userRepository);
		// Invoking Auto Comments Task Executor Thread
		notificationsTaskExecutor.execute(notificationsHelper);
		logger.debug("End:processNotifications() method of CollaborationCommentsService");
	}

	/**
	 * method to check whether the collaboration comment is valid or not
	 * @param comments
	 * @return
	 * @throws Exception
	 */
	private boolean isValidComment(CollaborationCommentT comments)
			throws Exception {
		logger.debug("Inside isValidComment() of CollaborationCommentsService");
		if (!CommentType.contains(comments.getCommentType())) {
			logger.error("BAD_REQUEST:Comment Type must be USER or AUTO");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Comment Type must be USER or AUTO");
		}
		if (comments.getComments() == null) {
			logger.error("BAD_REQUEST:Comment must not be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Comment must not be empty");
		}
		if (EntityType.contains(comments.getEntityType())) {
			switch (EntityType.valueOf(comments.getEntityType())) {
			case CONNECT:
				if (comments.getConnectId() != null) {
					comments.setEntityId(comments.getConnectId());
					return true;
				} else {
					logger.error("BAD_REQUEST: Connect ID cannot be Empty");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Connect ID cannot be Empty");
				}
			case OPPORTUNITY:
				if (comments.getOpportunityId() != null) {
					comments.setEntityId(comments.getOpportunityId());
					return true;
				}

				else {
					logger.error("BAD_REQUEST: Opportunity ID cannot be Empty");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Opportunity ID cannot be Empty");
				}
			case TASK:
				logger.debug("Task Found");
				if (comments.getTaskId() != null) {
					comments.setEntityId(comments.getTaskId());
					logger.debug("Task Id Available");
					return true;
				} else {
					logger.error("BAD_REQUEST: Task ID cannot be Empty");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Task ID cannot be Empty");
				}
			default: {
				logger.error("BAD_REQUEST: Invalid Entity Type");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Entity Type");
			}
			
			}
			
		} else {
			logger.error("BAD_REQUEST:Invalid Entity Type");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Entity Type");
		}
	}

	/**
	 * Edit Collaboration Comments 
	 * 
	 * @param comments
	 * @return
	 * @throws Exception
	 */
	public boolean editComments(CollaborationCommentT comments) throws Exception{
		
		boolean statusFlag=false;
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		
		if(StringUtils.isEmpty(comments.getCommentId())){
			logger.error("BAD_REQUEST: Comment ID cannot be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Comment ID cannot be empty");
		}
		
		if(!StringUtils.isEmpty(comments.getUserId())){
			if(!comments.getUserId().equals(userId)){
				String username = userRepository.findUserNameByUserId(comments.getUserId());
				logger.error("BAD_REQUEST: Only {} can edit this comment",username);
				throw new DestinationException(HttpStatus.FORBIDDEN, "Only "+username+" can edit this comment");
			}
		} else {
			logger.error("BAD_REQUEST: User ID cannot be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST, "User ID cannot be empty");
		}
		
		if(!StringUtils.isEmpty(comments.getCommentType())){
			if(!comments.getCommentType().equals(CommentType.USER.toString())){
				logger.error("FORBIDDEN: Only USER comments can be edited");
				throw new DestinationException(HttpStatus.FORBIDDEN, "Only USER comments can be edited");
			}
		} else {
			logger.error("BAD_REQUEST: Comment Type cannot be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Comment Type cannot be empty");
		}
		
		if (isValidComment(comments)) {
			try {
				CollaborationCommentT collaborationCommentT = commentsRepository
						.save(comments);
				processNotifications(collaborationCommentT.getCommentId());
				statusFlag = true;
				
			} catch (Exception e) {
				logger.error("INTERNAL_SERVER_ERROR " + e.getMessage());
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Backend Error while editing comments");
			}
		}
		
		return statusFlag;
	}
}
