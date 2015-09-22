package com.tcs.destination.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.BidOfficeGroupOwnerLinkT;
import com.tcs.destination.bean.ConnectSecondaryOwnerLinkT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.NotificationEventFieldsT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.bean.UserNotificationSettingsT;
import com.tcs.destination.bean.UserNotificationsT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.NotificationsEventFieldsTRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.data.repository.UserNotificationsRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.utils.Constants;

public class FollowNotifications implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(FollowNotifications.class);

	private static final String TOKEN_USER = "user";
	private static final String TOKEN_ENTITY_NAME = "entityName";
	private static final String PATTERN = "\\<(.+?)\\>";

	private String entityId;
	private String entityName;
	private String entityType;
	private String followUserId;
	private String createdUserId;
	private UserT followUser;
	private UserT createdUser;

	private int eventId;
	private CrudRepository crudRepository; 
	private NotificationsEventFieldsTRepository notificationsEventFieldsTRepository;
	private UserNotificationsRepository userNotificationsTRepository;
	private UserNotificationSettingsRepository userNotificationSettingsRepo;
	private EntityManagerFactory entityManagerFactory;

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getFollowUserId() {
		return followUserId;
	}

	public void setFollowUserId(String followUserId) {
		this.followUserId = followUserId;
	}

	public String getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(String createdUserId) {
		this.createdUserId = createdUserId;
	}


	public UserT getFollowUser() {
		return followUser;
	}

	public void setFollowUser(UserT followUser) {
		this.followUser = followUser;
	}

	public UserT getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(UserT modifyUser) {
		this.createdUser = modifyUser;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public CrudRepository getCrudRepository() {
		return crudRepository;
	}

	public void setCrudRepository(CrudRepository crudRepository) {
		this.crudRepository = crudRepository;
	}

	public NotificationsEventFieldsTRepository getNotificationsEventFieldsTRepository() {
		return notificationsEventFieldsTRepository;
	}

	public void setNotificationsEventFieldsTRepository(
			NotificationsEventFieldsTRepository notificationsEventFieldsTRepository) {
		this.notificationsEventFieldsTRepository = notificationsEventFieldsTRepository;
	}

	public UserNotificationsRepository getUserNotificationsTRepository() {
		return userNotificationsTRepository;
	}

	public void setUserNotificationsTRepository(
			UserNotificationsRepository userNotificationsTRepository) {
		this.userNotificationsTRepository = userNotificationsTRepository;
	}

	public UserNotificationSettingsRepository getUserNotificationSettingsRepo() {
		return userNotificationSettingsRepo;
	}

	public void setUserNotificationSettingsRepo(
			UserNotificationSettingsRepository userNotificationSettingsRepo) {
		this.userNotificationSettingsRepo = userNotificationSettingsRepo;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	@Override
	public void run() {
		logger.debug("Inside processFollowNotifications() method");
		logger.info("Processing Notification events for entity: {}: {}", entityName, entityId);
		try {
			processFollowNotifications();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Finished processing Notification events for entity: {}: {}", entityName, entityId);
	}

	private void processFollowNotifications() throws Exception {
		Object dbObject = NotificationsLazyLoader.loadLazyCollections(entityId,
				entityType, crudRepository,
				notificationsEventFieldsTRepository, entityManagerFactory);

		// fetching event field list based on active event id
		List<NotificationEventFieldsT> notificationEventFieldsTs = getNotificationEventFields(
				eventId, entityType);

		for (NotificationEventFieldsT notificationEventField : notificationEventFieldsTs) {
			if (notificationEventField.getMessageTemplate() != null
					&& notificationEventField.getFieldType().equalsIgnoreCase(
							Constants.FIELD)) {
				switch (EntityType.valueOf(entityType)) {
				case TASK: {
					if (!createdUserId.equalsIgnoreCase(followUserId)) {
						TaskT task = (TaskT) dbObject;
						entityName = task.getTaskDescription();
						String msgTemplate = replaceTokens(notificationEventField.getMessageTemplate(),
								populateTokensFollow(createdUser.getUserName(),entityName));
						if (msgTemplate != null) {
							addUserNotifications(msgTemplate, followUserId,
									notificationEventField.getNotificationEventId());
						}
					}
					break;
				}
				case CONNECT: {
					if (!createdUserId.equalsIgnoreCase(followUserId)) {
						ConnectT connect = (ConnectT) dbObject;
						entityName = connect.getConnectName();
						String msgTemplate = replaceTokens(notificationEventField.getMessageTemplate(),
								populateTokensFollow(createdUser.getUserName(),entityName));
						List<String> recipientList = getRecipientsList(connect,entityType);
						for (String recipientId : recipientList) {
							if (msgTemplate != null) {
								addUserNotifications(msgTemplate, recipientId,
										notificationEventField.getNotificationEventId());
							}
						}
					}
					break;
				}
				case OPPORTUNITY: {
					if (!createdUserId.equalsIgnoreCase(followUserId)) {
						OpportunityT opportunity = (OpportunityT) dbObject;
						entityName = opportunity.getOpportunityName();
						String msgTemplate = replaceTokens(notificationEventField.getMessageTemplate(),
								populateTokensFollow(createdUser.getUserName(),entityName));
						List<String> recipientList = getRecipientsList(opportunity, entityType);
						for (String recipientId : recipientList) {
							if (msgTemplate != null) {
								addUserNotifications(msgTemplate, recipientId,
										notificationEventField.getNotificationEventId());
							}
						}
					}
					break;
				}
				}
			}
		}
	}

	/**
	 * @param obj - actual connect / opportunity object (collections loaded) for which the notification recipients are to be retrieved
	 * @param entityType - type of the entity i.e connect or opportunity
	 * @return - list of recipients ids to whom notifications are to be sent
	 */
	private List<String> getRecipientsList(Object obj,String entityType) {
		List<String> recipientsList = new ArrayList<String>();
		if(entityType.equalsIgnoreCase(EntityType.CONNECT.name())){
			ConnectT connect = (ConnectT) obj;	
			//obtaining primary owner 
			String primaryOwner = connect.getPrimaryOwner();
			recipientsList.add(primaryOwner);
			//obtaining secondary owners
			List<ConnectSecondaryOwnerLinkT> secondaryOwners = connect.getConnectSecondaryOwnerLinkTs();
			if(secondaryOwners!=null && !secondaryOwners.isEmpty()){
				for(ConnectSecondaryOwnerLinkT secondaryOwner : secondaryOwners){
					recipientsList.add(secondaryOwner.getSecondaryOwner());
				}
			}
		} else if(entityType.equalsIgnoreCase(EntityType.OPPORTUNITY.name())){
			OpportunityT opportunity = (OpportunityT) obj;
			//obtaining primary owner
			String primaryOwner = opportunity.getOpportunityOwner();
			recipientsList.add(primaryOwner);
			//obtaining secondary owners - sales support owners
			List<OpportunitySalesSupportLinkT> salesSupportOwners = opportunity.getOpportunitySalesSupportLinkTs();
			if(salesSupportOwners!=null && !salesSupportOwners.isEmpty()){
				for(OpportunitySalesSupportLinkT salesSupport : salesSupportOwners) {
					recipientsList.add(salesSupport.getSalesSupportOwner());
				}
			}
			//obtaining secondary owners - bid office group owners
			List<BidDetailsT> bidsList = opportunity.getBidDetailsTs();
			if(bidsList != null && !bidsList.isEmpty()){
				for(BidDetailsT bidDetails : bidsList) {
					List<BidOfficeGroupOwnerLinkT> bidOwners = bidDetails.getBidOfficeGroupOwnerLinkTs();
					if(bidOwners != null && !bidOwners.isEmpty()){
						for(BidOfficeGroupOwnerLinkT bidOwner : bidOwners) {
							recipientsList.add(bidOwner.getBidOfficeGroupOwner());
						}
					}
				}
			}

		}
		return recipientsList;
	}

	// This method is used to get NotificationEventFieldsT details for the given EntityType
	private List<NotificationEventFieldsT> getNotificationEventFields(int eventId, String entityType) throws Exception {
		logger.debug("Inside getEntity() method");
		return(notificationsEventFieldsTRepository.findByNotificationEventIdAndEntityTypeAndIsactive(eventId, entityType, Constants.Y));
	}


	private HashMap<String, String> populateTokensFollow(String user, String entityName) 
			throws Exception {
		logger.debug("Inside populateTokens() method");
		HashMap<String, String> tokensMap = new HashMap<String, String>();
		if (user != null)
			tokensMap.put(TOKEN_USER, user);
		if (entityName != null)
			tokensMap.put(TOKEN_ENTITY_NAME, entityName);

		return tokensMap;
	}

	// This method is used to replace tokens in the notifications message template
	private String replaceTokens(String message, Map<String, String> tokens) throws Exception {
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

	// This method is used to add user notifications based on the message, eventId and the recipient
	private void addUserNotifications(String message, String recipient, int eventId) throws Exception {
		logger.debug("Inside addUserNotifications() method");

		List<UserNotificationSettingsT> userNotificationSettingsList = userNotificationSettingsRepo.findByUserIdAndEventIdAndIsactive(recipient, eventId, Constants.Y);
		if( userNotificationSettingsList != null && !userNotificationSettingsList.isEmpty() ){
			if ( (entityId != null) && (entityType != null) & (message != null) ) {
				UserNotificationsT notification = new UserNotificationsT();
				notification.setEntityType(entityType);
				notification.setRead(Constants.NO);
				notification.setComments(message);
				notification.setUserId(Constants.SYSTEM_USER);
				notification.setRecipient(recipient);
				notification.setEventId(eventId);

				if (EntityType.CONNECT.equalsName(entityType))
					notification.setConnectId(entityId);
				else if (EntityType.TASK.equalsName(entityType))
					notification.setTaskId(entityId);
				else if (EntityType.OPPORTUNITY.equalsName(entityType))
					notification.setOpportunityId(entityId);
				try {
					notification = userNotificationsTRepository.save(notification);
					logger.info("User notifications added successfully, notificationId: {}", notification.getUserNotificationId());
				} catch (Exception e) {
					logger.error("Error occurred while saving User notifications: " + e.getMessage());
					throw new Exception("Error occurred while saving User notifications");
				}
			}
		} else {
			logger.info("Notification settings not available.");
		}
	}

}
