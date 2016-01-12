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
import com.tcs.destination.bean.NotificationEventGroupMappingT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.bean.UserNotificationSettingsT;
import com.tcs.destination.bean.UserNotificationsT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.NotificationEventGroupMappingTRepository;
import com.tcs.destination.data.repository.NotificationsEventFieldsTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.data.repository.UserNotificationsRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.utils.Constants;

public class FollowNotifications implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(FollowNotifications.class);

	private static final String TOKEN_USER = "user";
	private static final String TOKEN_ENTITY_NAME = "entityName";
	private static final String TOKEN_ENTITY_TYPE = "entityType";
	private static final String PATTERN = "\\<(.+?)\\>";

	private static final String TOKEN_CUSTOMER_OR_PARTNER = "customerOrPartner";

	private static final String TOKEN_CUSTOMER_NAME = "customerName";

	private static final String TOKEN_PARENT_ENTITY = "parentEntity";

	private static final String TOKEN_PARENT_ENTITY_NAME = "parentEntityName";

	private String entityId;
	private String entityName;
	private String entityType;
	private String followUserId;
	private String createdUserId;
	private UserT followUser;
	private UserT createdUser;

	private int eventId;
	private CrudRepository crudRepository;
	private NotificationEventGroupMappingTRepository notificationEventGroupMappingTRepository;
	private UserNotificationsRepository userNotificationsTRepository;
	private UserNotificationSettingsRepository userNotificationSettingsRepo;
	private EntityManagerFactory entityManagerFactory;
	private OpportunityRepository opportunityRepository;
	private ConnectRepository connectRepository;
	private TaskRepository taskRepository;

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

	public NotificationEventGroupMappingTRepository getNotificationEventGroupMappingTRepository() {
		return notificationEventGroupMappingTRepository;
	}

	public void setNotificationEventGroupMappingTRepository(
			NotificationEventGroupMappingTRepository notificationEventGroupMappingTRepository) {
		this.notificationEventGroupMappingTRepository = notificationEventGroupMappingTRepository;
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

	public void setEntityManagerFactory(
			EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public ConnectRepository getConnectRepository() {
		return connectRepository;
	}

	public void setConnectRepository(ConnectRepository connectRepository) {
		this.connectRepository = connectRepository;
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	public void setTaskRepository(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public OpportunityRepository getOpportunityRepository() {
		return opportunityRepository;
	}

	public void setOpportunityRepository(
			OpportunityRepository opportunityRepository) {
		this.opportunityRepository = opportunityRepository;
	}

	@Override
	public void run() {
		logger.debug("Inside processFollowNotifications() method");
		logger.info("Processing Notification events for entity: {}: {}",
				entityName, entityId);
		try {
			processFollowNotifications();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info(
				"Finished processing Notification events for entity: {}: {}",
				entityName, entityId);
	}

	private void processFollowNotifications() throws Exception {

		// fetching event field list based on active event id
		NotificationEventGroupMappingT notificationEventGroupMappingT = getNotificationEventFields(
				eventId, entityType);
		String customerOrPartner = null;
		String customerOrPartnerName = null;
		String parentEntity = null;
		String parentEntityName = null;
		
		if (notificationEventGroupMappingT.getMessageTemplate() != null) {
			switch (EntityType.valueOf(entityType)) {
			case TASK: {
				if (!createdUserId.equalsIgnoreCase(Constants.SYSTEM_USER)) {
					TaskT task = (TaskT) taskRepository.findOne(entityId);
					entityName = task.getTaskDescription();
					//If task is for a connect
					if(task.getEntityReference().equalsIgnoreCase("CONNECT"))
					{
						customerOrPartner = task.getConnectT().getConnectCategory().toLowerCase();
						customerOrPartnerName = validateConnectCategory(task.getConnectT(), customerOrPartner);
						parentEntity = task.getEntityReference().toLowerCase();
						parentEntityName = task.getConnectT().getConnectName();						
						
					}
					// Task for opportunity
					else {
						customerOrPartner = "Customer";
						customerOrPartnerName = task.getOpportunityT().getCustomerMasterT().getCustomerName();
						parentEntity = task.getEntityReference().toLowerCase();
						parentEntityName = task.getOpportunityT().getOpportunityName();
						
					}
					String msgTemplate = replaceTokens(
							notificationEventGroupMappingT.getMessageTemplate(),
							populateTokensFollow(createdUser.getUserName(),
									entityName, Constants.TASK,customerOrPartner, customerOrPartnerName, parentEntity, parentEntityName));
					if (!followUserId.equals(createdUserId)) {
						String recipientId = followUserId;
						if (msgTemplate != null) {
							if (!recipientId
									.equalsIgnoreCase(Constants.SYSTEM_USER))
								addUserNotifications(msgTemplate, recipientId,
										notificationEventGroupMappingT
												.getEventId());
						}
					}
				}
				break;
			}
			case CONNECT: {
				if (!createdUserId.equalsIgnoreCase(Constants.SYSTEM_USER)) {
					ConnectT connect = (ConnectT) connectRepository
							.findOne(entityId);
					entityName = connect.getConnectName();
					
					customerOrPartner = connect.getConnectCategory().toLowerCase();
					customerOrPartnerName = validateConnectCategory(connect, customerOrPartner);
					
					
					String msgTemplate = replaceTokens(
							notificationEventGroupMappingT.getMessageTemplate(),
							populateTokensFollow(createdUser.getUserName(),
									entityName, Constants.CONNECT, customerOrPartner, customerOrPartnerName, null, null));
					List<String> recipientList = getRecipientsList(entityId,
							entityType);
					for (String recipientId : recipientList) {
						if (msgTemplate != null) {
							if (!recipientId
									.equalsIgnoreCase(Constants.SYSTEM_USER)
									&& (!recipientId.equals(createdUser
											.getUserId())))
								addUserNotifications(msgTemplate, recipientId,
										notificationEventGroupMappingT
												.getEventId());
						}
					}
				}
				break;
			}
			case OPPORTUNITY: {
				if (!createdUserId.equalsIgnoreCase(Constants.SYSTEM_USER)) {
					OpportunityT opportunity = (OpportunityT) opportunityRepository
							.findOne(entityId);
					entityName = opportunity.getOpportunityName();
					customerOrPartner = "Customer";
					customerOrPartnerName= opportunity.getCustomerMasterT().getCustomerName();
					
					String msgTemplate = replaceTokens(
							notificationEventGroupMappingT.getMessageTemplate(),
							populateTokensFollow(followUser.getUserName(),
									entityName, Constants.OPPORTUNITY, customerOrPartner, customerOrPartnerName, null, null));
					List<String> recipientList = getRecipientsList(entityId,
							entityType);
					for (String recipientId : recipientList) {
						if (msgTemplate != null) {
							if (!recipientId
									.equalsIgnoreCase(Constants.SYSTEM_USER)
									&& (!recipientId.equals(createdUser
											.getUserId()))) {
								addUserNotifications(msgTemplate, recipientId,
										notificationEventGroupMappingT
												.getEventId());
							}
						}
					}
				}
				break;
			}
			}
		}
	}

	private String validateConnectCategory(ConnectT connect,
			String connectCategory) {
		String customerOrPartnerName = null;
		if(connectCategory.equalsIgnoreCase("CUSTOMER")) {
			customerOrPartnerName = connect.getCustomerMasterT().getCustomerName();
		}
		else if(connectCategory.equalsIgnoreCase("PARTNER")) {
			customerOrPartnerName = connect.getPartnerMasterT().getPartnerName();
		}
		return customerOrPartnerName;
	}

	/**
	 * @param obj
	 *            - actual connect / opportunity object (collections loaded) for
	 *            which the notification recipients are to be retrieved
	 * @param entityType
	 *            - type of the entity i.e connect or opportunity
	 * @return - list of recipients ids to whom notifications are to be sent
	 */
	private List<String> getRecipientsList(String entityId, String entityType) {
		switch (EntityType.valueOf(entityType)) {
		case CONNECT:
			return connectRepository.findOwnersOfConnect(entityId);
		case OPPORTUNITY:
			return opportunityRepository.getAllOwners(entityId);
		default:
			return null;
		}
	}

	// This method is used to get NotificationEventFieldsT details for the given
	// EntityType
	private NotificationEventGroupMappingT getNotificationEventFields(
			int eventId, String entityType) throws Exception {
		logger.debug("Inside getEntity() method");
		switch (EntityType.valueOf(entityType)) {
		case TASK:
			return notificationEventGroupMappingTRepository.findByEventId(2)
					.get(0);
		case CONNECT:
			return notificationEventGroupMappingTRepository.findByEventId(3)
					.get(0);
		case OPPORTUNITY:
			return notificationEventGroupMappingTRepository.findByEventId(3)
					.get(0);
		default:
			return null;
		}

	}

	private HashMap<String, String> populateTokensFollow(String user,
			String entityName, String entityType, String customerOrPartner, String customerOrPartnerName, String parentEntity, String parentEntityName) throws Exception {
		logger.debug("Inside populateTokens() method");
		HashMap<String, String> tokensMap = new HashMap<String, String>();
		if (user != null)
			tokensMap.put(TOKEN_USER, user);
		if (entityName != null)
			tokensMap.put(TOKEN_ENTITY_NAME, entityName);
		if (entityType != null)
			tokensMap.put(TOKEN_ENTITY_TYPE, entityType);
		if (entityType != null)
			tokensMap.put(TOKEN_CUSTOMER_OR_PARTNER, customerOrPartner);
		if (entityType != null)
			tokensMap.put(TOKEN_CUSTOMER_NAME, customerOrPartnerName);
		if (entityType != null)
			tokensMap.put(TOKEN_PARENT_ENTITY, parentEntity);
		if (entityType != null)
			tokensMap.put(TOKEN_PARENT_ENTITY_NAME, parentEntityName);
		return tokensMap;
	}

	// This method is used to replace tokens in the notifications message
	// template
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

	// This method is used to add user notifications based on the message,
	// eventId and the recipient
	private void addUserNotifications(String message, String recipient,
			int eventId) throws Exception {
		logger.debug("Inside addUserNotifications() method");

		List<UserNotificationSettingsT> userNotificationSettingsList = userNotificationSettingsRepo
				.findByUserIdAndEventIdAndIsactive(recipient, eventId,
						Constants.Y);
		if (userNotificationSettingsList != null
				&& !userNotificationSettingsList.isEmpty()) {
			if ((entityId != null) && (entityType != null) & (message != null)) {
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
					notification = userNotificationsTRepository
							.save(notification);
					logger.info(
							"User notifications added successfully, notificationId: {}",
							notification.getUserNotificationId());
				} catch (Exception e) {
					logger.error("Error occurred while saving User notifications: "
							+ e.getMessage());
					throw new Exception(
							"Error occurred while saving User notifications");
				}
			}
		} else {
			logger.info("Notification settings not available.");
		}
	}

}
