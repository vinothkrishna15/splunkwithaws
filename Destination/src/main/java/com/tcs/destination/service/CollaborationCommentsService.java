package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.CollaborationCommentT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.ShareLinkDTO;
import com.tcs.destination.bean.UserNotificationsT;
import com.tcs.destination.bean.UserT;
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
	
	private static final String PATTERN = "\\<(.+?)\\>";

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
//				processNotifications(collaborationCommentT.getCommentId());
				statusFlag = true;
				
			} catch (Exception e) {
				logger.error("INTERNAL_SERVER_ERROR " + e.getMessage());
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Backend Error while editing comments");
			}
		}
		
		return statusFlag;
	}
	
	/**
	 * This method is used to validate the shareDTO before sharing  
	 * @param shareDTO
	 */
	private void validateShareDTO(ShareLinkDTO shareDTO){
		
		String entityType = shareDTO.getEntityType();
		if(StringUtils.isEmpty(entityType)){
			logger.error("BAD_REQUEST: entity Type cannot be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST, "entity Type cannot be empty");
		} else {
			if(!EntityType.contains(entityType)){
				logger.error("Invalid Entity type");
				throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid entity Type");
			}
		}
		
		String entityId = shareDTO.getEntityId();
		if(StringUtils.isEmpty(entityId)){
			logger.error("BAD_REQUEST: entity Id cannot be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST, "entity Id cannot be empty");
		} else {
			if(EntityType.contains(entityType)){
				switch (EntityType.valueOf(entityType)) {
				case CONNECT:
					             ConnectT connect = connectRepository.findOne(entityId);
					             if(connect==null){
					            	logger.error("Connect Entity Not found: {}",entityId);
					 				throw new DestinationException(HttpStatus.NOT_FOUND, "Connect Entity Not found");
					             }
							 	 break;
				case OPPORTUNITY:
								 OpportunityT opportunity = opportunityRepository.findOne(entityId);
								 if(opportunity==null){
						            	logger.error("opportunity Entity Not found: {}",entityId);
						 				throw new DestinationException(HttpStatus.NOT_FOUND, "Opportunity Entity Not found");
						             }
								 break;
				default:
					logger.error("BAD_REQUEST: Invalid entity type");
					throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid entity type");
				}
			} else {
				logger.error("Invalid Entity type");
				throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid entity Type");
			}
		}
		
		String url = shareDTO.getUrl();
		if(StringUtils.isEmpty(url)){
			logger.error("BAD_REQUEST: url cannot be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST, "url cannot be empty");
		}
		
		List<String> recipientIds = shareDTO.getRecipientIds();
		if(CollectionUtils.isEmpty(recipientIds)){
			logger.error("BAD_REQUEST: recipients cannot be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST, "recipients cannot be empty");
		} else {
			List<String> validRecipientIds = new ArrayList<String>();
			for(String id : recipientIds ){
				UserT user = userRepository.findOne(id);
			    if(user!=null){
			    	if(user.isActive()){
			    		validRecipientIds.add(user.getUserId());
			    	}
			    }
			}
			shareDTO.setRecipientIds(validRecipientIds);
		}
		
	}
	
	/**
	 * This method is used to share an entity through email or portal to group of users  
	 * @param shareDTO
	 */
	public void share(ShareLinkDTO shareDTO) throws Exception{
		validateShareDTO(shareDTO);
		boolean isPortalNotify = shareDTO.getPortalNotify();
		String entityType = shareDTO.getEntityType();
		List<String> recipientIds = shareDTO.getRecipientIds();
		String entityId = shareDTO.getEntityId();
		UserT currentUser = DestinationUtils.getCurrentUserDetails();
		
		//sharing portal notification
		if(isPortalNotify){
			for(String userId:recipientIds){
			UserNotificationsT notification = new UserNotificationsT();
			
			notification.setEntityType(entityType);
			if(EntityType.contains(entityType)){
				switch (EntityType.valueOf(entityType)) {
				
				case CONNECT :
					String connectId = entityId;
					notification.setConnectId(connectId);
					ConnectT connect = connectRepository.findOne(connectId);
					Map<String, String> tokensForConnect = new HashMap<String,String>();
					tokensForConnect.put("User", currentUser.getUserName());
					tokensForConnect.put("ConnectName",connect.getConnectName());
					notification.setComments(replaceTokens(Constants.shareConnectPortal,tokensForConnect));
					break;
					
				case OPPORTUNITY :
					String opportunityId = entityId;
					notification.setOpportunityId(opportunityId);
					OpportunityT opportunity = opportunityRepository.findOne(opportunityId);
					Map<String, String> tokensForOpportunity = new HashMap<String,String>();
					tokensForOpportunity.put("User", currentUser.getUserName());
					tokensForOpportunity.put("OpportunityName", opportunity.getOpportunityName());
					notification.setComments(replaceTokens(Constants.shareOpportunityPortal,tokensForOpportunity));
					break;
					
				default:
					break;
					
				}
			}
			notification.setRecipient(userId);
			UserT user = userRepository.findOne(userId); 
			notification.setUserT(user);
			notification.setEventId(17);
			notification.setRead("NO");
			notification.setUserId(currentUser.getUserId());
			userNotificationsTRepository.save(notification);
		 }
			
		}
	
		//sharing mail notification
		boolean isEmailNotify = shareDTO.getEmailNotify();
		if(isEmailNotify){
			String sender = currentUser.getUserId();
			StringBuffer recipientIdBuffer = new StringBuffer("");
			for(String recipientId : recipientIds){
				recipientIdBuffer.append(",");
				recipientIdBuffer.append(recipientId);
			}
			String url = shareDTO.getUrl();
			
			if(!StringUtils.isEmpty(recipientIdBuffer.toString())){
			 logger.info("launching share email asynchronously");	
			 jobLauncherController.asyncJobLaunchForShareEmailNotification(JobName.shareEmail, entityType, entityId,recipientIdBuffer.toString(),sender,url);
			}
		}
	}
	
	private String replaceTokens(String message, Map<String, String> tokens)
			throws Exception {
		logger.debug("Inside replaceTokens() method");
		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(message);
		StringBuilder builder = new StringBuilder();
		int i = 0;
		while (matcher.find()) {
			String replacement = tokens.get(matcher.group(1));
			builder.append(message.substring(i, matcher.start()));
			if (replacement == null)
				builder.append(matcher.group(0));
			else
				builder.append(replacement);
			i = matcher.end();
		}
		builder.append(message.substring(i, message.length()));
		return builder.toString();
	}
	
}
