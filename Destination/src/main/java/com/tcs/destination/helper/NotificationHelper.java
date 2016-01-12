package com.tcs.destination.helper;

import static com.tcs.destination.utils.Constants.DATE;
import static com.tcs.destination.utils.Constants.DATE_TYPE;
import static com.tcs.destination.utils.Constants.TOKEN_CST_OR_PARTNER;
import static com.tcs.destination.utils.Constants.TOKEN_CST_OR_PARTNER_VALUE;
import static com.tcs.destination.utils.Constants.TOKEN_ENTITY_NAME;
import static com.tcs.destination.utils.Constants.TOKEN_ENTITY_TYPE;
import static com.tcs.destination.utils.Constants.TOKEN_FROM;
import static com.tcs.destination.utils.Constants.TOKEN_PRIMARY_OWNER;
import static com.tcs.destination.utils.Constants.TOKEN_SECONDARY_OWNERS;
import static com.tcs.destination.utils.Constants.TOKEN_SUBORDINATE;
import static com.tcs.destination.utils.Constants.TOKEN_TO;
import static com.tcs.destination.utils.Constants.TOKEN_USER;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.BidOfficeGroupOwnerLinkT;
import com.tcs.destination.bean.CollaborationCommentT;
import com.tcs.destination.bean.ConnectSecondaryOwnerLinkT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.NotificationEventFieldsT;
import com.tcs.destination.bean.NotificationEventGroupMappingT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.SearchKeywordsT;
import com.tcs.destination.bean.TaskBdmsTaggedLinkT;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.bean.UserNotificationSettingsT;
import com.tcs.destination.bean.UserNotificationsT;
import com.tcs.destination.data.repository.AutoCommentsEntityTRepository;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.FollowedRepository;
import com.tcs.destination.data.repository.NotificationEventGroupMappingTRepository;
import com.tcs.destination.data.repository.NotificationsEventFieldsTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.SearchKeywordsRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsConditionRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.data.repository.UserNotificationsRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.service.FollowedService;
import com.tcs.destination.utils.Constants;

public class NotificationHelper implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(NotificationHelper.class);

	private static final String TOKEN_USER = "user";
	private static final String TOKEN_ENTITY_NAME = "entityName";
	private static final String TOKEN_ENTITY_TYPE = "entityType";
	private static final String TOKEN_FROM = "from";
	private static final String TOKEN_TO = "to";
	private static final String PATTERN = "\\<(.+?)\\>";

	private static final String TOKEN_OWNERSHIP = "ownership";
	private static final String TOKEN_STATUS = "status";

	private static final String TOKEN_SALES_STAGE = "sales_stage";

	private static final String TOKEN_PARENT_ENTITY_NAME = "parentEntityName";
	
	private static final String TOKEN_CUSTOMER_NAME = "customerName";

	private static final String TOKEN_CONNECT_CATEGORY="connectCategory";

	private static final String TOKEN_PARENT_ENTITY = "parentEntity";
	private Object oldObject;
	private String entityId;
	private String entityType;
	private CrudRepository crudRepository;
	private NotificationEventGroupMappingTRepository notificationEventGroupMappingTRepository;
	private NotificationsEventFieldsTRepository notificationsEventFieldsTRepository;
	private UserNotificationsRepository userNotificationsTRepository;
	private UserNotificationSettingsRepository userNotificationSettingsRepo;
	private CollaborationCommentsRepository collaborationCommentsRepository;
	private EntityManagerFactory entityManagerFactory;
	private OpportunityRepository opportunityRepository;
	private ConnectRepository connectRepository;
	private TaskRepository taskRepository;
	private UserRepository userRepository;
	private FollowedRepository taggedFollowedRepository;
	private FollowedService followService;
	private UserNotificationSettingsConditionRepository userNotificationSettingsConditionRepository;
	private SearchKeywordsRepository searchKeywordsRepository;
	private AutoCommentsEntityTRepository autoCommentsEntityTRepository;

	public Object getOldObject() {
		return oldObject;
	}

	public void setOldObject(Object oldObject) {
		this.oldObject = oldObject;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
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

	public void setEntityManagerFactory(
			EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public CollaborationCommentsRepository getCollaborationCommentsRepository() {
		return collaborationCommentsRepository;
	}

	public void setCollaborationCommentsRepository(
			CollaborationCommentsRepository collaborationCommentsRepository) {
		this.collaborationCommentsRepository = collaborationCommentsRepository;
	}

	@Override
	public void run() {
		logger.debug("Inside processNotifications() method");
		logger.info("Processing Notification events for entity: {}: {}",
				entityType, entityId);
		List<NotificationEventFieldsT> notificationEventFields = null;

		try {
			// Wait for sometime for hibernate to flush updates in database
			Thread.sleep(8000);

			if (entityType != null) {
				// Get the notification eligible entity & fields
				if (EntityType.contains(entityType)) {
					switch (EntityType.valueOf(entityType)) {
					case COMMENT:
						// This is both for Manual Comments as well as Auto
						// Comments (Notification on Key fields change)
						notifyForComments();
						break;
					default:
						notificationEventFields = getNotificationEventFields();
						if (notificationEventFields != null
								&& !notificationEventFields.isEmpty()) {
							processNotifications(notificationEventFields);
						}
						break;

					}
				}

			}
		} catch (Exception e) {
			logger.error("Error occurred while processing notifications "
					+ e.getMessage());
			e.printStackTrace();
		}
		logger.info(
				"Finished processing Notification events for entity: {}: {}",
				entityType, entityId);
	}

	/**
	 * This method will notify users for both Manual comments or for
	 * Auto-comments. Note that Whenever a Key field is changed, the
	 * notification is processed for that using Auto Comments of that entity
	 * This handles event Id 8,13,9
	 * 
	 * @throws Exception
	 */
	private void notifyForComments() throws Exception {
		String ownerMessageTemplate = null;
		String taggedFollowedMessageTemplate = null;
		String ownersSupervisorMessageTemplate = null;
		String commentedEntityName = null;
		String commentedEntityType = null;
		String commentedEntityId = null;
		int ownerEventId = 0;
		int taggedEventId = 0;
		int ownerSupervisorEventId = 0;
		List<String> ownerIdList = null;
		List<String> taggedUserList = null;
		{
			List<NotificationEventGroupMappingT> notificationEventGroupMappingTs = getNotificationEventGroupMappingTs(8);

			if (notificationEventGroupMappingTs.size() > 0) {
				ownerMessageTemplate = notificationEventGroupMappingTs.get(0)
						.getMessageTemplate();
				ownerEventId = notificationEventGroupMappingTs.get(0)
						.getEventId();
			}
		}
		{
			List<NotificationEventGroupMappingT> notificationEventGroupMappingTs = getNotificationEventGroupMappingTs(13);
			if (notificationEventGroupMappingTs.size() > 0) {
				ownersSupervisorMessageTemplate = notificationEventGroupMappingTs
						.get(0).getMessageTemplate();
				ownerSupervisorEventId = notificationEventGroupMappingTs.get(0)
						.getEventId();
			}
		}
		{
			List<NotificationEventGroupMappingT> notificationEventGroupMappingTs = getNotificationEventGroupMappingTs(9);
			if (notificationEventGroupMappingTs.size() > 0) {
				taggedFollowedMessageTemplate = notificationEventGroupMappingTs
						.get(0).getMessageTemplate();
				taggedEventId = notificationEventGroupMappingTs.get(0)
						.getEventId();
			}
		}
		Object dbObject = NotificationsLazyLoader.loadLazyCollections(entityId,
				this.entityType, crudRepository,
				notificationsEventFieldsTRepository, entityManagerFactory);
		CollaborationCommentT commentT = (CollaborationCommentT) dbObject;
		if (commentT.getOpportunityId() != null
				&& (!commentT.getOpportunityId().isEmpty())) {
			OpportunityT opportunityT = opportunityRepository.findOne(commentT
					.getOpportunityId());
			commentedEntityName = opportunityT.getOpportunityName();
			commentedEntityType = Constants.OPPORTUNITY;
			commentedEntityId = commentT.getOpportunityId();
			ownerIdList = opportunityRepository.getAllOwners(commentT
					.getOpportunityId());
			// Don't send notifications to the user who
			// Commented
			if (ownerIdList != null && !ownerIdList.isEmpty()) {
				if (commentT.getUserT().getUserName()
						.equalsIgnoreCase(Constants.SYSTEM_USER)) {
					ownerIdList.remove(opportunityT.getModifiedBy());
				} else {
					ownerIdList.remove(commentT.getUserId());
				}
			}
			taggedUserList = taggedFollowedRepository
					.getOpportunityTaggedFollowedUsers(commentT
							.getOpportunityId());
			if (taggedUserList != null && !taggedUserList.isEmpty()) {
				if (commentT.getUserT().getUserName()
						.equalsIgnoreCase(Constants.SYSTEM_USER)) {
					taggedUserList.remove(opportunityT.getModifiedBy());
				} else {
					taggedUserList.remove(commentT.getUserId());
				}
			}
		} else if (commentT.getConnectId() != null
				&& (!commentT.getConnectId().isEmpty())) {
			ConnectT connectT = connectRepository.findOne(commentT
					.getConnectId());
			commentedEntityName = connectT.getConnectName();
			commentedEntityType = Constants.CONNECT;
			commentedEntityId = commentT.getConnectId();
			ownerIdList = connectRepository.findOwnersOfConnect(commentT
					.getConnectId());
			taggedUserList = taggedFollowedRepository
					.getConnectTaggedFollowedUsers(commentT.getConnectId());

			// Don't send notifications to the user who
			// Commented
			if (ownerIdList != null && !ownerIdList.isEmpty()) {
				if (commentT.getUserT().getUserName()
						.equalsIgnoreCase(Constants.SYSTEM_USER)) {
					ownerIdList.remove(connectT.getModifiedBy());
				} else {
					ownerIdList.remove(commentT.getUserId());
				}
			}

			if (taggedUserList != null && !taggedUserList.isEmpty()) {
				if (commentT.getUserT().getUserName()
						.equalsIgnoreCase(Constants.SYSTEM_USER)) {
					ownerIdList.remove(connectT.getModifiedBy());
					taggedUserList.remove(connectT.getModifiedBy());
				} else {
					ownerIdList.remove(commentT.getUserId());
					taggedUserList.remove(commentT.getUserId());
				}
			}
		} else if (commentT.getTaskId() != null
				&& (!commentT.getTaskId().isEmpty())) {
			TaskT taskT = taskRepository.findOne(commentT.getTaskId());
			commentedEntityName = taskT.getTaskDescription();
			commentedEntityType = Constants.TASK;
			commentedEntityId = commentT.getTaskId();
			ownerIdList = taskRepository.findOwnersOfTask(commentT.getTaskId());
			taggedUserList = taggedFollowedRepository
					.getTasksTaggedFollowedUsers(commentT.getTaskId());
			if (!ownerIdList.contains(taskT.getCreatedBy()))
				ownerIdList.add(taskT.getCreatedBy());
			if (commentT.getUserT().getUserName()
					.equalsIgnoreCase(Constants.SYSTEM_USER)) {
				ownerIdList.clear();
				// if (taggedUserList != null && !taggedUserList.isEmpty()) {
				// taggedUserList.removeAll(Collections.singleton(taskT
				// .getModifiedBy()));
				// }
				// ownerIdList.removeAll(Collections.singleton(taskT
				// .getModifiedBy()));
			} else {
				taggedUserList.removeAll(Collections.singleton(commentT
						.getUserId()));
				ownerIdList.removeAll(Collections.singleton(commentT
						.getUserId()));
			}
		}

		{

			if (commentT.getUserT().getUserName()
					.equalsIgnoreCase(Constants.SYSTEM_USER)) {
				ownerMessageTemplate = commentT.getComments().replace(
						"[Auto Comment]: ", "");
				ownerMessageTemplate = ownerMessageTemplate.trim();
				ownerMessageTemplate += " - <entityType> : <entityName>";
				taggedFollowedMessageTemplate = ownerMessageTemplate;
			}
			// Notify Entity Owners about the Comment
			{
				String msgTemplate = replaceTokens(
						ownerMessageTemplate,
						populateTokens(commentT.getUserT().getUserName(),
								commentedEntityName, null, null,
								commentedEntityType, null, null, null, null));
				if (msgTemplate != null) {
					for (String recipient : ownerIdList) {
						if (!commentT.getUserId().equals(recipient)) {
							addUserNotifications(msgTemplate, recipient,
									ownerEventId,
									getActualEntityType(commentedEntityType),
									commentedEntityId);
						}
					}
				}
			}

			// Notify Entity Owners supervisor about the Comment from USERS
			if (!commentT.getUserT().getUserName()
					.equalsIgnoreCase(Constants.SYSTEM_USER)) {
				List<String> ownersSupervisorIds = userRepository
						.getSupervisorUserId(ownerIdList);
				String msgTemplate = replaceTokens(
						ownersSupervisorMessageTemplate,
						populateTokens(commentT.getUserT().getUserName(),
								commentedEntityName, null, null,
								commentedEntityType, null, null, null, null));
				if (msgTemplate != null) {
					for (String recipient : ownersSupervisorIds) {
						if (!commentT.getUserId().equalsIgnoreCase(recipient))
							addUserNotifications(msgTemplate, recipient,
									ownerSupervisorEventId,
									getActualEntityType(commentedEntityType),
									commentedEntityId);
					}
				}
			}

			// Notify Tagged Followed users about the Comment
			{
				String msgTemplate = replaceTokens(
						taggedFollowedMessageTemplate,
						populateTokens(commentT.getUserT().getUserName(),
								commentedEntityName, null, null,
								commentedEntityType, null, null, null, null));
				logger.error("tagged : " + taggedUserList.size());
				if (msgTemplate != null) {
					for (String recipient : taggedUserList) {
						logger.error(msgTemplate + " is sent to " + recipient
								+ " for event " + taggedEventId);
						addUserNotifications(msgTemplate, recipient,
								taggedEventId,
								getActualEntityType(commentedEntityType),
								commentedEntityId);
					}
				}
			}
		}
	}

	/**
	 * Method to return the {@link EntityType} when the display entity type is
	 * given.
	 * 
	 * @param commentedEntityType
	 *            The actual entity type that is displayed
	 * @return The {@link EntityType} for that display entity
	 */
	private String getActualEntityType(String commentedEntityType) {
		switch (commentedEntityType) {
		case Constants.CONNECT:
			return EntityType.CONNECT.name();
		case Constants.TASK:
			return EntityType.TASK.name();
		case Constants.OPPORTUNITY:
			return EntityType.OPPORTUNITY.name();
		default:
			return null;
		}
	}

	/**
	 * Get the {@link NotificationEventGroupMappingT} for the eventId
	 * 
	 * @param eventId
	 *            The actual eventId
	 * @return The {@link NotificationEventGroupMappingT} related to that event.
	 */
	private List<NotificationEventGroupMappingT> getNotificationEventGroupMappingTs(
			int eventId) {
		return notificationEventGroupMappingTRepository.findByEventId(eventId);
	}

	// This method is used to get NotificationEventFieldsT details for the given
	// EntityType
	private List<NotificationEventFieldsT> getNotificationEventFields()
			throws Exception {
		logger.debug("Inside getEntity() method");
		return (notificationsEventFieldsTRepository
				.findByEntityTypeAndIsactiveAndParentFieldIdIsNull(entityType,
						Constants.Y));
	}

	// This method is used to process notification for new entity Adds
	private void processNotifications(
			List<NotificationEventFieldsT> notificationEventFieldsList)
			throws Exception {
		logger.info("Inside processAdds() method");
		String user = null;
		String entityName = null;
		String msgTemplate = null;
		String addRecipient = null;
		String removeReceipient = null;
		String customerOrPartnerName = null;
		String connectCategory = null;
		String customerOrPartner = null;
		String customerName = null;
		String parentEntity =null;
		String parentEntityName = null;
		Object dbObject = NotificationsLazyLoader.loadLazyCollections(entityId,
				entityType, crudRepository,
				notificationsEventFieldsTRepository, entityManagerFactory);
		if (notificationEventFieldsList != null
				&& !notificationEventFieldsList.isEmpty()) {
			if (EntityType.contains(entityType)) {
				switch (EntityType.valueOf(entityType)) {
				case CONNECT: {
					
					logger.debug(
							"Processing Notifications for Add, ConnectId: {}",
							entityId);
					ConnectT connect = (ConnectT) dbObject;
					String fieldType = null;
					if (connect != null) {
						
						user = connect.getModifiedByUser().getUserName();
						entityName = connect.getConnectName();
						connectCategory = connect.getConnectCategory().toLowerCase();
						customerOrPartnerName = validateConnectCategory(connect, connectCategory);
							
						
						logger.info("customer/partner name: "  + customerOrPartnerName);
						
						for (NotificationEventFieldsT notificationField : notificationEventFieldsList) {
							
							fieldType = notificationField.getFieldType();
							if (fieldType.equalsIgnoreCase(Constants.FIELD)) {
								
								// Handling update
								if (oldObject != null) {
									
									String oldValue = PropertyUtils
											.getProperty(
													oldObject,
													notificationField
															.getFieldName())
											.toString();
									String newValue = PropertyUtils
											.getProperty(
													connect,
													notificationField
															.getFieldName())
											.toString();
									if (!oldValue.equalsIgnoreCase(newValue)) {
										
										// Get notification recipient user id
										addRecipient = (String) PropertyUtils
												.getProperty(
														connect,
														notificationField
																.getUseridField());
										// Sending Removed Notification for
										// Primary Owner
										if (notificationField.getFieldId() == 101) {
											removeReceipient = oldValue;
										}
									} else {
										continue;
									}
								} else {
									// Get notification recipient user id
									addRecipient = (String) PropertyUtils
											.getProperty(connect,
													notificationField
															.getUseridField());
								}

								if (!connect.getModifiedBy().equals(
										addRecipient)) {
									String[] tokenValues = {user, entityName,
											null, null, null, null,
											"added", null, null, customerOrPartnerName, connectCategory,parentEntity};
									msgTemplate = replaceTokens(
											notificationField
													.getMessageTemplate(),
											populateTokens(tokenValues));
							        
									if (msgTemplate != null) {
										logger.info(msgTemplate);
										addUserNotifications(
												msgTemplate,
												addRecipient,
												notificationField
														.getNotificationEventId());
									}
								}

								if (!connect.getModifiedBy().equals(
										removeReceipient)) {
									String[] tokenValues = {user, entityName,
											null, null, null, null,
											"removed", null, null,customerOrPartnerName, connectCategory,parentEntity};

									msgTemplate = replaceTokens(
											notificationField
													.getMessageTemplate(),
											populateTokens(tokenValues));
									  
									if (msgTemplate != null) {
										addUserNotifications(
												msgTemplate,
												removeReceipient,
												notificationField
														.getNotificationEventId());
									}
								}
							} else if (fieldType
									.equalsIgnoreCase(Constants.COLLECTION)) {
								processCollections(user, entityName,
										notificationField, oldObject, connect, customerOrPartnerName, connectCategory);
							}
						}

						// Primary Owner Or Secondary Owner check
						sendNotificationsToSupervisorForConnectForSubordinateOwnerAddision(connect);

						if (oldObject == null)
							notifyNewDesiredConnects(connect);
					} else {
						logger.error("Invalid Connect Id: {}", entityId);
						throw new Exception("Invalid Connect Id: " + entityId);
					}
					break;
				}
				case OPPORTUNITY: {
					logger.debug(
							"Processing Notifications for Add, OpportunityId: {}",
							entityId);
					OpportunityT opportunity = (OpportunityT) dbObject;
					String fieldType = null;
					if (opportunity != null) {
						user = opportunity.getModifiedByUser().getUserName();
						entityName = opportunity.getOpportunityName();
						customerName = opportunity.getCustomerMasterT().getCustomerName();
						for (NotificationEventFieldsT notificationField : notificationEventFieldsList) {
							fieldType = notificationField.getFieldType();
							if (fieldType.equalsIgnoreCase(Constants.FIELD)) {
								// Handling update
								if (oldObject != null) {
									String oldValue = PropertyUtils
											.getProperty(
													oldObject,
													notificationField
															.getFieldName())
											.toString();
									String newValue = PropertyUtils
											.getProperty(
													opportunity,
													notificationField
															.getFieldName())
											.toString();
									if (!oldValue.equalsIgnoreCase(newValue)) {
										addRecipient = (String) PropertyUtils
												.getProperty(
														opportunity,
														notificationField
																.getUseridField());
										// Sending Removed Notification for
										// Primary Owner
										if (notificationField.getFieldId() == 201) {
											removeReceipient = oldValue;
										}
									} else {
										continue;
									}
								} else {
									// Get notification recipient user id
									addRecipient = (String) PropertyUtils
											.getProperty(opportunity,
													notificationField
															.getUseridField());
								}

								if (!opportunity.getModifiedBy().equals(
										addRecipient)) {
									String[] tokenValues={user, entityName,
											null, null, null, null,
											"added", null, null,customerName,null,null};
									msgTemplate = replaceTokens(
											notificationField
													.getMessageTemplate(),
											populateTokens(tokenValues));
									if (msgTemplate != null) {
										addUserNotifications(
												msgTemplate,
												addRecipient,
												notificationField
														.getNotificationEventId());
									}
								}

								if (!opportunity.getModifiedBy().equals(
										removeReceipient)) {
									String[] tokenValues={user, entityName,
											null, null, null, null,
											"removed", null, null,customerName, null,null};

									msgTemplate = replaceTokens(
											notificationField
													.getMessageTemplate(),
											populateTokens(tokenValues));
									if (msgTemplate != null) {
										addUserNotifications(
												msgTemplate,
												removeReceipient,
												notificationField
														.getNotificationEventId());
									}
								}
							} else if (fieldType
									.equalsIgnoreCase(Constants.COLLECTION)) {
								processCollections(user, entityName,
										notificationField, oldObject,
										opportunity, customerName, null);
							}
						}

						// Primary Owner Or Secondary Owner check
						sendNotificationsToSupervisorForOpportunityForSubordinateOwnerAddision(opportunity);

						// Send Win or lost notifications
						sendNotificationWhenSubordinateOwnedOpportunitiesWonOrLost(opportunity);

						if (oldObject == null) {
							// Send Notification for users selected customers,
							// IOU , Geo ,Digital deal value...
							notifyNewDesiredOpportunities(opportunity);

							// Send Notification for Digital Re-imagination
							notifyNewDigitalReimaginationOpportunities(opportunity);

							// Send Notification for Strategic Initiatives
							notifyNewStategicInitiativeOpportunities(opportunity);
						}

					} else {
						logger.error("Invalid Opportunity Id: {}", entityId);
						throw new Exception("Invalid Opportunity Id: "
								+ entityId);
					}
					break;
				}
				case TASK: {
					List<String> taskOwners = new ArrayList<String>();
					logger.debug(
							"Processing Notifications for Add, TaskId: {}",
							entityId);
					TaskT task = (TaskT) dbObject;
					String fieldType = null;
					if (task != null) {
						user = task.getModifiedByUser().getUserName();
						entityName = task.getTaskDescription();
						parentEntity = task.getEntityReference().toLowerCase();
						//If task is for a connect
						if(parentEntity.equalsIgnoreCase("CONNECT"))
						{
							parentEntityName = task.getConnectT().getConnectName();
							 customerOrPartner = task.getConnectT().getConnectCategory().toLowerCase();
							customerOrPartnerName = validateConnectCategory(task.getConnectT(), connectCategory);
							
						}
						// Task for opportunity
						else {
							parentEntityName = task.getOpportunityT().getOpportunityName();
							customerOrPartner  = "Customer";
							customerOrPartnerName = task.getOpportunityT().getCustomerMasterT().getCustomerName();
						}
						for (NotificationEventFieldsT notificationField : notificationEventFieldsList) {
							fieldType = notificationField.getFieldType();
							if (fieldType.equalsIgnoreCase(Constants.FIELD)) {
								// Handling update
								if (oldObject != null) {
									String oldValue = PropertyUtils
											.getProperty(
													oldObject,
													notificationField
															.getFieldName())
											.toString();
									String newValue = PropertyUtils
											.getProperty(
													task,
													notificationField
															.getFieldName())
											.toString();
									if (!oldValue.equalsIgnoreCase(newValue)) {
										// Get notification recipient user id
										taskOwners = ((TaskRepository) crudRepository)
												.findOwnersOfTask(entityId);
										taskOwners.add(task.getCreatedBy());
										// Sending Removed Notification for
										// Primary Owner
										if (notificationField.getFieldId() == 301) {
											removeReceipient = oldValue;
										}
									} else {
										continue;
									}
								} else {
									// Get notification recipient user id
									if (notificationField
											.getNotificationEventId() != 4) {
										taskOwners = ((TaskRepository) crudRepository)
												.findOwnersOfTask(entityId);
										taskOwners.add(task.getCreatedBy());
									} else {

									}
								}
								Set<String> taskOwnerSet = new HashSet<String>(
										taskOwners);
								for (String recipient : taskOwnerSet) {
									if (!task.getModifiedBy().equals(recipient)) {
										String[] tokenValues = {user,
												entityName, null, null,
												null, null, "added",
												null, parentEntityName, customerOrPartnerName, customerOrPartner, parentEntity};
										msgTemplate = replaceTokens(
												notificationField
														.getMessageTemplate(),
												populateTokens(tokenValues));
										if (msgTemplate != null) {
											addUserNotifications(
													msgTemplate,
													recipient,
													notificationField
															.getNotificationEventId());
										}
									}
								}

								if (!task.getModifiedBy().equals(
										removeReceipient)) {
									String[] tokenValues = {user, entityName,
											null, null, null, null,
											"removed", null, parentEntityName, customerOrPartnerName, customerOrPartner, parentEntity};

									msgTemplate = replaceTokens(
											notificationField
													.getMessageTemplate(),
											populateTokens(tokenValues));
									if (msgTemplate != null) {
										addUserNotifications(
												msgTemplate,
												removeReceipient,
												notificationField
														.getNotificationEventId());
									}
								}
							} else if (fieldType
									.equalsIgnoreCase(Constants.COLLECTION)) {
								processCollections(user, entityName,
										notificationField, oldObject, task, customerOrPartnerName, customerOrPartner);
							}
						}
						sendNotificationsToSupervisorForTaskForSubordinateOwnerAddision(task);
					} else {
						logger.error("Invalid Task Id: {}", entityId);
						throw new Exception("Invalid Task Id: " + entityId);
					}
					break;
				}
				default:
					logger.error("Invalid Entity Type: " + entityType);
					throw new Exception("Invalid Entity Type: " + entityType);
				}
			} else {
				logger.error("Invalid Entity Type: " + entityType);
				throw new Exception("Invalid Entity Type: " + entityType);
			}
		}
	}

	private void notifyNewDesiredOpportunities(OpportunityT opportunity)
			throws Exception {
		String customerName = opportunity.getCustomerMasterT()
				.getCustomerName();
		List<SearchKeywordsT> searchKeywordTs = searchKeywordsRepository
				.findByEntityTypeAndEntityId(EntityType.OPPORTUNITY.name(),
						opportunity.getOpportunityId());
		String displayIOU = opportunity.getCustomerMasterT()
				.getIouCustomerMappingT().getDisplayIou();
		Integer digitalDealValue = opportunity.getDigitalDealValue();
		String geography = opportunity.getGeographyCountryMappingT()
				.getGeography();
		String country = opportunity.getCountry();
		Set<String> userIds = userNotificationSettingsConditionRepository
				.findUserIdByConditionIdAndConditionValue(1, customerName);
		for (SearchKeywordsT searchKeywordsT : searchKeywordTs) {
			addToList(userIds,
					userNotificationSettingsConditionRepository
							.findUserIdByConditionIdAndConditionValue(2,
									searchKeywordsT.getSearchKeywords()));
		}
		addToList(
				userIds,
				userNotificationSettingsConditionRepository
						.findUserIdByConditionIdAndConditionValue(3, displayIOU));

		addToList(userIds,
				userNotificationSettingsConditionRepository
						.findUserIdByConditionIdAndConditionValue(4, geography));

		addToList(userIds,
				userNotificationSettingsConditionRepository
						.findUserIdByConditionIdAndConditionValue(5, country));
               if(digitalDealValue != null) {
		addToList(
				userIds,
				userNotificationSettingsConditionRepository
						.findUserIdByDigitalDealValueGreaterThan(digitalDealValue));
}

		List<String> opportunityOwners = ((OpportunityRepository) crudRepository)
				.getAllOwners(opportunity.getOpportunityId());
		int eventId = 10;
		int fieldId = 201;
		String messageTemplate = notificationEventGroupMappingTRepository
				.findByEventId(eventId).get(0).getMessageTemplate();
		notifyForNewOpportunities(opportunity, userIds, opportunityOwners,
				eventId, fieldId, messageTemplate);

	}

	private void notifyForNewOpportunities(OpportunityT opportunity,
			Set<String> notifyUserIds, List<String> opportunityOwners,
			int eventId, int fieldId, String addMessageTemplate)
			throws Exception {
		for (String userId : notifyUserIds) {
			if ((!opportunityOwners.contains(userId))
					&& (!opportunity.getCreatedBy().equals(userId))) {
				String[] tokenValues={opportunity.getCreatedByUser()
						.getUserName(), opportunity
						.getOpportunityName(), null, null, null, null,
						null, null, opportunity.getCustomerMasterT()
								.getCustomerName(), null, null,null};
				String notificationMessage = replaceTokens(
						addMessageTemplate,
						populateTokens(tokenValues));
				notificationMessage = notificationMessage.replace(
						"[Auto Comment]: ", "");
				addUserNotifications(notificationMessage, userId, eventId,
						EntityType.OPPORTUNITY.name(),
						opportunity.getOpportunityId());
			}
		}
	}

	private void notifyNewDesiredConnects(ConnectT connectT) throws Exception {
		String customerName = connectT.getCustomerMasterT().getCustomerName();
		List<SearchKeywordsT> searchKeywordTs = searchKeywordsRepository
				.findByEntityTypeAndEntityId(EntityType.CONNECT.name(),
						connectT.getConnectId());
		String displayIOU = connectT.getCustomerMasterT()
				.getIouCustomerMappingT().getDisplayIou();
		String geography = connectT.getGeographyCountryMappingT()
				.getGeography();
		String country = connectT.getCountry();
		Set<String> userIds = userNotificationSettingsConditionRepository
				.findUserIdByConditionIdAndConditionValue(1, customerName);
		for (SearchKeywordsT searchKeywordsT : searchKeywordTs) {
			addToList(userIds,
					userNotificationSettingsConditionRepository
							.findUserIdByConditionIdAndConditionValue(2,
									searchKeywordsT.getSearchKeywords()));
		}
		addToList(
				userIds,
				userNotificationSettingsConditionRepository
						.findUserIdByConditionIdAndConditionValue(3, displayIOU));

		addToList(userIds,
				userNotificationSettingsConditionRepository
						.findUserIdByConditionIdAndConditionValue(4, geography));

		addToList(userIds,
				userNotificationSettingsConditionRepository
						.findUserIdByConditionIdAndConditionValue(5, country));

		List<String> connectOwners = ((ConnectRepository) crudRepository)
				.findOwnersOfConnect(connectT.getConnectId());
		int fieldId = 101;
		int eventId = 10;
		notifyForNewConnects(connectT, userIds, connectOwners, fieldId, eventId);

	}

	private void notifyForNewConnects(ConnectT connectT,
			Set<String> notifyUserIds, List<String> connectOwners, int fieldId,
			int eventId) throws Exception {
		for (String userId : notifyUserIds) {
			if ((!connectOwners.contains(userId))
					&& (!connectT.getCreatedBy().equals(userId))) {
				String addMessageTemplate = autoCommentsEntityTRepository
						.findOne(fieldId).getAddMessageTemplate();
				String parentEntityName = "";
				if (connectT.getCustomerMasterT() != null)
					parentEntityName = connectT.getCustomerMasterT()
							.getCustomerName();
				else if (connectT.getPartnerMasterT() != null)
					parentEntityName = connectT.getPartnerMasterT()
							.getPartnerName();
				String tokenValues[] = {connectT.getCreatedByUser()
						.getUserName(), connectT.getConnectName(),
						null, null, null, null, null, null,
						parentEntityName,null,null,null};
				String notificationMessage = replaceTokens(
						addMessageTemplate,
						populateTokens(tokenValues));
				notificationMessage = notificationMessage.replace(
						"[Auto Comment]: ", "");
				addUserNotifications(notificationMessage, userId, eventId,
						EntityType.CONNECT.name(), connectT.getConnectId());
			}
		}
	}

	private void addToList(Set<String> mainList, Set<String> listToBeAdded) {
		if (mainList == null)
			mainList = new TreeSet<String>();
		if (listToBeAdded != null)
			mainList.addAll(listToBeAdded);

	}

	private void notifyNewDigitalReimaginationOpportunities(
			OpportunityT opportunity) throws Exception {
		int eventId = 15;
		int fieldId = 201;
		if (opportunity.getDigitalFlag() != null)
			if (opportunity.getDigitalFlag().equals("Y")) {
				List<String> owners = ((OpportunityRepository) crudRepository)
						.getAllOwners(opportunity.getOpportunityId());
				List<String> userIds = userRepository
						.getSupervisorUserId(owners);
				List<String> opportunityOwners = ((OpportunityRepository) crudRepository)
						.getAllOwners(entityId);
				String messageTemplate = notificationEventGroupMappingTRepository
						.findByEventId(eventId).get(0).getMessageTemplate();
				notifyForNewOpportunities(opportunity, new HashSet<String>(
						userIds), opportunityOwners, eventId, fieldId,
						messageTemplate);
			}
	}

	private void notifyNewStategicInitiativeOpportunities(
			OpportunityT opportunity) throws Exception {
		int eventId = 16;
		int fieldId = 201;
		if (opportunity.getStrategicInitiative() != null)
			if (opportunity.getStrategicInitiative().equals("YES")) {
				List<String> userIds = userRepository
						.findUserIdByUserGroup(UserGroup.STRATEGIC_INITIATIVES
								.toString());
				List<String> opportunityOwners = ((OpportunityRepository) crudRepository)
						.getAllOwners(entityId);
				String messageTemplate = notificationEventGroupMappingTRepository
						.findByEventId(eventId).get(0).getMessageTemplate();
				notifyForNewOpportunities(opportunity, new HashSet<String>(
						userIds), opportunityOwners, eventId, fieldId,
						messageTemplate);
			}
	}

	private void sendNotificationWhenSubordinateOwnedOpportunitiesWonOrLost(
			OpportunityT opportunity) throws Exception {
		List<NotificationEventGroupMappingT> notificationEventGroupMappingTs = notificationEventGroupMappingTRepository
				.findByEventId(14);
		String notificationTemplate = notificationEventGroupMappingTs.get(0)
				.getMessageTemplate();
		List<String> userIds = new ArrayList<String>();
		userIds.add(opportunity.getPrimaryOwnerUser().getUserId());
		String userNames = opportunity.getPrimaryOwnerUser().getUserName()
				+ " (Primary)";
		String ownership = null;
		
		int size = opportunity.getOpportunitySalesSupportLinkTs().size();
		if (opportunity.getBidDetailsTs() != null) {
			for (BidDetailsT bidDetailsT : opportunity.getBidDetailsTs()) {
				size += bidDetailsT.getBidOfficeGroupOwnerLinkTs().size();
			}
		}

		if (opportunity.getOpportunitySalesSupportLinkTs() != null) {
			for (int i = 0; i < size; i++) {
				if (i < (size - 1)) {
					userNames += " , ";
				} else {
					userNames += " and ";
				}
				userNames += opportunity.getOpportunitySalesSupportLinkTs()
						.get(i).getSalesSupportOwnerUser().getUserName()
						+ " (Sales)";
				userIds.add(opportunity.getOpportunitySalesSupportLinkTs()
						.get(i).getSalesSupportOwnerUser().getUserId());
			}
		}
		if (opportunity.getBidDetailsTs() != null) {
			for (BidDetailsT bidDetailsT : opportunity.getBidDetailsTs()) {
				for (int i = 0; i < bidDetailsT.getBidOfficeGroupOwnerLinkTs()
						.size(); i++) {

					if (i < (size - 1)) {
						userNames += " , ";
					} else {
						userNames += " and ";
					}
					userNames += bidDetailsT.getBidOfficeGroupOwnerLinkTs()
							.get(i).getBidOfficeGroupOwnerUser().getUserName()
							+ " (Bid)";
					userIds.add(bidDetailsT.getBidOfficeGroupOwnerLinkTs()
							.get(i).getBidOfficeGroupOwnerUser().getUserId());

				}
			}
		}
		String supervisorOwnerWonOrLost = null;
		if (opportunity.getSalesStageCode() == 9) {
			String[] tokenValues={userNames, opportunity.getOpportunityName(),
					null, null, null, null, "won", null, null,opportunity.getCustomerMasterT().getCustomerName(), null,null};
			supervisorOwnerWonOrLost = replaceTokens(
					notificationTemplate,
					populateTokens(tokenValues));
		} else if (opportunity.getSalesStageCode() == 10) {
			String[] tokenValues={userNames, opportunity.getOpportunityName(),
					null, null, null, null, "lost", null, null, opportunity.getCustomerMasterT().getCustomerName(), null,null};
			supervisorOwnerWonOrLost = replaceTokens(
					notificationTemplate,
					populateTokens(tokenValues));
		}
		List<String> supervisorIdList = userRepository
				.getSupervisorUserId(userIds);

		for (String supervisorId : supervisorIdList) {
			if (supervisorOwnerWonOrLost != null) {
				addUserNotifications(supervisorOwnerWonOrLost, supervisorId,
						14, EntityType.OPPORTUNITY.name(),
						opportunity.getOpportunityId());
			}
		}

	}

	private void sendNotificationsToSupervisorForConnectForSubordinateOwnerAddision(
			ConnectT connect) throws Exception {
		List<NotificationEventGroupMappingT> notificationEventGroupMappingTs = notificationEventGroupMappingTRepository
				.findByEventId(11);
		String notificationTemplate = notificationEventGroupMappingTs.get(0)
				.getMessageTemplate();
		String oldPrimaryOwner = null;
		List<String> oldSecondaryOwners = null;
		String connectCategory = null;
		String customerOrPartnerName= null;
		
		connectCategory = connect.getConnectCategory().toLowerCase();
		customerOrPartnerName = validateConnectCategory(connect, connectCategory);
		
		if (oldObject != null) {
			ConnectT oldConnect = (ConnectT) oldObject;
			oldPrimaryOwner = oldConnect.getPrimaryOwner();
			oldSecondaryOwners = new ArrayList<String>();
			for (ConnectSecondaryOwnerLinkT connectSecondaryOwnerLinkT : connect
					.getConnectSecondaryOwnerLinkTs()) {
				oldSecondaryOwners.add(connectSecondaryOwnerLinkT
						.getSecondaryOwner());
			}
		}
		if (oldPrimaryOwner == null
				|| connect.getPrimaryOwner() != oldPrimaryOwner) {

			String[] tokenValues={connect.getPrimaryOwnerUser().getUserName(),
					connect.getConnectName(), null, null,
					Constants.CONNECT, "Primary Owner", null, null,
					null,customerOrPartnerName , connectCategory,null};
			String supervisorOwner = replaceTokens(
					notificationTemplate,
					populateTokens(tokenValues));
			if (supervisorOwner != null) {
				if (!connect.getPrimaryOwnerUser().getSupervisorUserId()
						.equals(connect.getModifiedBy()))
					addUserNotifications(supervisorOwner, connect
							.getPrimaryOwnerUser().getSupervisorUserId(), 11,
							EntityType.CONNECT.name(), connect.getConnectId());
			}
		}
		if (connect.getConnectSecondaryOwnerLinkTs() != null
				&& connect.getConnectSecondaryOwnerLinkTs().size() > 0) {
			for (ConnectSecondaryOwnerLinkT connectSecondaryOwnerLinkT : connect
					.getConnectSecondaryOwnerLinkTs()) {
				if ((oldSecondaryOwners != null && !oldSecondaryOwners
						.contains(connectSecondaryOwnerLinkT
								.getSecondaryOwner()))
						|| oldSecondaryOwners == null) {
					String[] tokenValues={connectSecondaryOwnerLinkT
							.getSecondaryOwnerUser().getUserName(),
							connect.getConnectName(), null, null,
							EntityType.CONNECT.name(),
							"Secondary Owner", null, null, null,customerOrPartnerName , connectCategory,null};
					String supervisorOwner = replaceTokens(
							notificationTemplate,
							populateTokens(tokenValues));
					if (supervisorOwner != null) {
						if (!connectSecondaryOwnerLinkT.getSecondaryOwnerUser()
								.getSupervisorUserId()
								.equals(connect.getModifiedBy()))
							addUserNotifications(supervisorOwner,
									connectSecondaryOwnerLinkT
											.getSecondaryOwnerUser()
											.getSupervisorUserId(), 11,
									EntityType.CONNECT.name(),
									connect.getConnectId());
					}
				}
			}

		}
	}

	/**
	 * @param connect
	 * @param connectCategory
	 */
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

	private void sendNotificationsToSupervisorForTaskForSubordinateOwnerAddision(
			TaskT taskT) throws Exception {
		List<NotificationEventGroupMappingT> notificationEventGroupMappingTs = notificationEventGroupMappingTRepository
				.findByEventId(11);
		String notificationTemplate = notificationEventGroupMappingTs.get(0)
				.getMessageTemplate();
		String oldPrimaryOwner = null;
		String customerOrPartnerName=null;
		String customerOrPartner=null;
		List<String> oldSecondaryOwners = null;
		String parentEntityName = null;
		String parentEntity = null;
		parentEntity = taskT.getEntityReference().toLowerCase();
		//If task is for a connect
		if(parentEntity.equalsIgnoreCase("CONNECT"))
		{
			parentEntityName= taskT.getConnectT().getConnectName();
			customerOrPartner = taskT.getConnectT().getConnectCategory().toLowerCase();
			customerOrPartnerName = validateConnectCategory(taskT.getConnectT(), customerOrPartner);
			
		}
		// Task for opportunity
		else {
			parentEntityName= taskT.getOpportunityT().getOpportunityName();
			customerOrPartner = "Customer";
			customerOrPartnerName = taskT.getOpportunityT().getCustomerMasterT().getCustomerName();
		}
		if (oldObject != null) {
			TaskT oldTask = (TaskT) oldObject;
			oldPrimaryOwner = oldTask.getTaskOwner();
			oldSecondaryOwners = new ArrayList<String>();
			for (TaskBdmsTaggedLinkT taskBdmsTaggedLinkT : taskT
					.getTaskBdmsTaggedLinkTs()) {
				oldSecondaryOwners.add(taskBdmsTaggedLinkT.getUserT()
						.getUserId());
			}
		}
		if (oldPrimaryOwner == null || taskT.getTaskOwner() != oldPrimaryOwner) {

			String[] tokenValues={taskT.getTaskOwnerT().getUserName(),
					taskT.getTaskDescription(), null, null,
					Constants.TASK, "Primary Owner", null, null, parentEntityName,customerOrPartnerName, customerOrPartner, parentEntity};
			String supervisorOwner = replaceTokens(
					notificationTemplate,
					populateTokens(tokenValues));
			if (supervisorOwner != null) {
				if (!taskT.getTaskOwnerT().getSupervisorUserId()
						.equals(taskT.getModifiedBy()))
					addUserNotifications(supervisorOwner, taskT.getTaskOwnerT()
							.getSupervisorUserId(), 11, EntityType.TASK.name(),
							taskT.getTaskId());
			}
		}
		if (taskT.getTaskBdmsTaggedLinkTs() != null
				&& taskT.getTaskBdmsTaggedLinkTs().size() > 0) {
			for (TaskBdmsTaggedLinkT taskBdmsTaggedLinkT : taskT
					.getTaskBdmsTaggedLinkTs()) {
				if ((oldSecondaryOwners != null && !oldSecondaryOwners
						.contains(taskBdmsTaggedLinkT.getUserT().getUserId()))
						|| oldSecondaryOwners == null) {
					String[] tokenValues={taskBdmsTaggedLinkT.getUserT()
							.getUserName(), taskT.getTaskDescription(),
							null, null, EntityType.TASK.name(),
							"Secondary Owner", null, null, parentEntityName, customerOrPartnerName, customerOrPartner, parentEntity};
					String supervisorOwner = replaceTokens(
							notificationTemplate,
							populateTokens(tokenValues));
					if (supervisorOwner != null) {
						if (!taskBdmsTaggedLinkT.getUserT()
								.getSupervisorUserId()
								.equals(taskT.getModifiedBy()))
							addUserNotifications(supervisorOwner,
									taskBdmsTaggedLinkT.getUserT()
											.getSupervisorUserId(), 11,
									EntityType.TASK.name(), taskT.getTaskId());
					}
				}
			}

		}
	}

	private void sendNotificationsToSupervisorForOpportunityForSubordinateOwnerAddision(
			OpportunityT opportunity) throws Exception {
		List<NotificationEventGroupMappingT> notificationEventGroupMappingTs = notificationEventGroupMappingTRepository
				.findByEventId(11);
		String notificationTemplate = notificationEventGroupMappingTs.get(0)
				.getMessageTemplate();
		String oldPrimaryOwner = null;
		List<String> oldSalesSupportOwners = null;
		List<String> oldBidOfficeGroupOwners = null;
	
		if (oldObject != null) {
			OpportunityT oldOpportunity = (OpportunityT) oldObject;
			oldPrimaryOwner = oldOpportunity.getOpportunityOwner();
			oldSalesSupportOwners = new ArrayList<String>();
			oldBidOfficeGroupOwners = new ArrayList<String>();
			for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity
					.getOpportunitySalesSupportLinkTs()) {
				oldSalesSupportOwners.add(opportunitySalesSupportLinkT
						.getSalesSupportOwner());
			}

			for (BidDetailsT bidDetailsT : opportunity.getBidDetailsTs()) {
				for (BidOfficeGroupOwnerLinkT bidOfficeGroupOwnerLinkT : bidDetailsT
						.getBidOfficeGroupOwnerLinkTs())
					oldBidOfficeGroupOwners.add(bidOfficeGroupOwnerLinkT
							.getBidOfficeGroupOwner());
			}
		}
		if (oldPrimaryOwner == null
				|| opportunity.getOpportunityOwner() != oldPrimaryOwner) {

			String[] tokenValues={opportunity.getPrimaryOwnerUser()
					.getUserName(), opportunity.getOpportunityName(),
					null, null, Constants.OPPORTUNITY, "Primary Owner",
					null, null, null, opportunity.getCustomerMasterT().getCustomerName(), null, null};
			String supervisorOwner = replaceTokens(
					notificationTemplate,
					populateTokens(tokenValues));
			if (supervisorOwner != null) {
				if (!opportunity.getPrimaryOwnerUser().getSupervisorUserId()
						.equals(opportunity.getModifiedBy()))
					addUserNotifications(supervisorOwner, opportunity
							.getPrimaryOwnerUser().getSupervisorUserId(), 11,
							EntityType.OPPORTUNITY.name(),
							opportunity.getOpportunityId());
			}
		}
		if (opportunity.getOpportunitySalesSupportLinkTs() != null
				&& opportunity.getOpportunitySalesSupportLinkTs().size() > 0) {
			for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity
					.getOpportunitySalesSupportLinkTs()) {
				if ((oldSalesSupportOwners != null && !oldSalesSupportOwners
						.contains(opportunitySalesSupportLinkT
								.getSalesSupportOwner()))
						|| oldSalesSupportOwners == null) {
					String[] tokenValues={opportunitySalesSupportLinkT
							.getSalesSupportOwnerUser().getUserName(),
							opportunity.getOpportunityName(), null,
							null, EntityType.OPPORTUNITY.name(),
							"Sales Support Owner", null, null, null,opportunity.getCustomerMasterT().getCustomerName(), null,null};
					String supervisorOwner = replaceTokens(
							notificationTemplate,
							populateTokens(tokenValues));
					if (supervisorOwner != null) {
						if (!opportunitySalesSupportLinkT
								.getSalesSupportOwnerUser()
								.getSupervisorUserId()
								.equals(opportunity.getModifiedBy()))
							addUserNotifications(supervisorOwner,
									opportunitySalesSupportLinkT
											.getSalesSupportOwnerUser()
											.getSupervisorUserId(), 11,
									EntityType.OPPORTUNITY.name(),
									opportunity.getOpportunityId());
					}
				}
			}

		}
		if (opportunity.getBidDetailsTs() != null) {
			for (BidDetailsT bidDetailsT : opportunity.getBidDetailsTs()) {
				if (bidDetailsT.getBidOfficeGroupOwnerLinkTs() != null) {
					for (BidOfficeGroupOwnerLinkT bidOfficeGroupOwnerLinkT : bidDetailsT
							.getBidOfficeGroupOwnerLinkTs()) {
						if ((oldBidOfficeGroupOwners != null && oldBidOfficeGroupOwners
								.contains(bidOfficeGroupOwnerLinkT
										.getBidOfficeGroupOwner()))
								|| (oldBidOfficeGroupOwners == null)) {
							String[] tokenValues={bidOfficeGroupOwnerLinkT
									.getBidOfficeGroupOwnerUser()
									.getUserName(), opportunity
									.getOpportunityName(), null, null,
									EntityType.OPPORTUNITY.name(),
									"Bid Office Owner", null, null,
									null,opportunity.getCustomerMasterT().getCustomerName(), null, null};
							String supervisorOwner = replaceTokens(
									notificationTemplate,
									populateTokens(tokenValues));
							if (supervisorOwner != null) {
								if (!bidOfficeGroupOwnerLinkT
										.getBidOfficeGroupOwnerUser()
										.getSupervisorUserId()
										.equals(opportunity.getModifiedBy()))
									addUserNotifications(
											supervisorOwner,
											bidOfficeGroupOwnerLinkT
													.getBidOfficeGroupOwnerUser()
													.getSupervisorUserId(), 11,
											EntityType.OPPORTUNITY.name(),
											opportunity.getOpportunityId());
							}
						}

					}
				}
			}
		}
	}

	private void processCollections(String user, String entityName,
			NotificationEventFieldsT notificationField, Object oldObj,
			Object newObj, String customerOrPartnerName, String connectCategory) throws Exception {
		logger.debug("Inside processCollections() method");
		Object beforeUpdate = null;
		Object afterUpdate = null;
		String parentEntity =null;
		String parentEntityName =null;
		
		if(newObj.getClass() == TaskT.class)
		{
			TaskT task = (TaskT)newObj;
			parentEntity = task.getEntityReference().toLowerCase();
			//If task is for a connect
			if(parentEntity.equalsIgnoreCase("CONNECT"))
			{
				parentEntityName = task.getConnectT().getConnectName();	
				
			}
			// Task for opportunity
			else {
				parentEntityName = task.getOpportunityT().getOpportunityName();
				
			}
			
		}
		if (notificationField != null) {
			logger.debug("Field: {}", notificationField.getFieldName());
			if (oldObj != null) {
				beforeUpdate = PropertyUtils.getProperty(oldObj,
						notificationField.getFieldName());
			}
			afterUpdate = PropertyUtils.getProperty(newObj,
					notificationField.getFieldName());

			// Handle empty collections
			if (beforeUpdate != null && (beforeUpdate instanceof List)) {
				if (((List) beforeUpdate).size() == 0)
					beforeUpdate = null;
			}
			if (afterUpdate != null && (afterUpdate instanceof List)) {
				if (((List) afterUpdate).size() == 0)
					afterUpdate = null;
			}

			if (beforeUpdate == null) {
				// No add or update during update
				if (afterUpdate == null) {
					logger.debug("No values before and after update");
					return;
				}
			} else {
				// All db values were deleted during update
				if (afterUpdate == null) {
					logger.debug("All db values before update were deleted during update");
				}
			}

			// Create a map with db objects before update for comparison
			HashMap<String, Object> beforeObjectMap = null;
			if (beforeUpdate != null) {
				String key = null;
				beforeObjectMap = new HashMap<String, Object>();
				List<Object> beforeUpdateObjectList = (List<Object>) beforeUpdate;
				logger.debug("fromObjectList collection size: {}",
						beforeUpdateObjectList.size());
				for (Object beforeUpdateObject : beforeUpdateObjectList) {
					key = PropertyUtils.getProperty(beforeUpdateObject,
							notificationField.getPrimaryKeyField()).toString();
					logger.info("Key ID: {}", key);
					beforeObjectMap.put(key, beforeUpdateObject);
				}
				logger.debug("beforeObjectMap size: {}", beforeObjectMap.size());
			}

			// Get the child fields
			List<NotificationEventFieldsT> notificationEventFieldsTs = notificationsEventFieldsTRepository
					.findByParentFieldIdAndIsactive(
							notificationField.getFieldId(), Constants.Y);

			String recipient = null;
			String msgTemplate = null;
			for (NotificationEventFieldsT eventField : notificationEventFieldsTs) {
				logger.info("Field Name: {}", eventField.getFieldName());
				String fieldType = eventField.getFieldType();
				if (fieldType.equalsIgnoreCase(Constants.FIELD)) {
					// Check the value in afterUpdate
					if (afterUpdate != null) {
						List<Object> afterUpdateObjectList = (List<Object>) afterUpdate;
						logger.debug("toObjectList collection size: {}",
								afterUpdateObjectList.size());
						for (Object afterUpdateObject : afterUpdateObjectList) {
							String keyId = PropertyUtils.getProperty(
									afterUpdateObject,
									notificationField.getPrimaryKeyField())
									.toString();
							String fieldValue = PropertyUtils.getProperty(
									afterUpdateObject,
									eventField.getFieldName()).toString();
							logger.info("After Update Field Value: {}",
									fieldValue);
							if (beforeObjectMap != null
									&& !beforeObjectMap.isEmpty()) {
								// If key is present in beforeUpdate, then check
								// the field value
								if (beforeObjectMap.containsKey(keyId)) {
									// Field value not same
									if (!fieldValue
											.equalsIgnoreCase(PropertyUtils
													.getProperty(
															beforeObjectMap
																	.get(keyId),
															eventField
																	.getFieldName())
													.toString())) {
										// Get notification recipient user id
										recipient = (String) PropertyUtils
												.getProperty(
														afterUpdateObject,
														eventField
																.getUseridField());
									} else {
										// Field value same
										continue;
									}
								} else {
									// If value is not present in beforeUpdate,
									// then it was added in update
									// Get notification recipient user id
									recipient = (String) PropertyUtils
											.getProperty(afterUpdateObject,
													eventField.getUseridField());
								}
							} else {
								// No element in collection in before update
								// object
								// Get notification recipient user id
								recipient = (String) PropertyUtils.getProperty(
										afterUpdateObject,
										eventField.getUseridField());
							}

							// Add Notification with the message template
							String[] tokenValues={user, entityName, null,
									null, null, null, "added", null,
									parentEntityName, customerOrPartnerName, connectCategory, parentEntity};
							msgTemplate = replaceTokens(
									eventField.getMessageTemplate(),
									populateTokens(tokenValues));
							sendNotificationForCollections(newObj, recipient,
									msgTemplate, eventField);
						}
					}
					// Secondary owners are removed
					List<Object> afterUpdateObjectList = null;
					if (afterUpdate != null)
						afterUpdateObjectList = (List<Object>) afterUpdate;
					
					
					sendRemovedNotificationToSecondaryOwners(user, entityName,
							notificationField, newObj, afterUpdate,
							beforeObjectMap, msgTemplate, eventField,
							afterUpdateObjectList, customerOrPartnerName, connectCategory);
				} else if (fieldType.equalsIgnoreCase(Constants.COLLECTION)) {
					// Process inner collections
					if (afterUpdate != null) {
						List<Object> afterUpdateObjectList = (List<Object>) afterUpdate;
						logger.debug("toObjectList collection size: {}",
								afterUpdateObjectList.size());
						for (Object afterUpdateObject : afterUpdateObjectList) {
							String keyId = PropertyUtils.getProperty(
									afterUpdateObject,
									notificationField.getPrimaryKeyField())
									.toString();
							if (beforeObjectMap != null
									&& !beforeObjectMap.isEmpty()) {
								processCollections(user, entityName,
										eventField, beforeObjectMap.get(keyId),
										afterUpdateObject, customerOrPartnerName, connectCategory);
							} else {
								processCollections(user, entityName,
										eventField, null, afterUpdateObject, customerOrPartnerName, connectCategory);
							}
						}
					}
				} else {
					logger.error("Invalid Field Type: {}", fieldType);
					throw new Exception("Invalid Field Type: " + fieldType);
				}
			}
		}
	}

	private void sendRemovedNotificationToSecondaryOwners(String user,
			String entityName, NotificationEventFieldsT notificationField,
			Object newObj, Object afterUpdate,
			HashMap<String, Object> beforeObjectMap, String msgTemplate,
			NotificationEventFieldsT eventField,
			List<Object> afterUpdateObjectList, String customerOrPartnerName, String connectCategory) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, Exception {
		if (Arrays.asList(103, 203, 206, 311).contains(eventField.getFieldId())
				&& beforeObjectMap != null) {
			HashMap<String, Object> afterObjectMap = new HashMap<String, Object>();
			if (afterUpdate != null) {
				String key = null;
				logger.debug("fromObjectList collection size: {}",
						afterUpdateObjectList.size());
				for (Object afterTempUpdateObject : afterUpdateObjectList) {
					key = PropertyUtils.getProperty(afterTempUpdateObject,
							notificationField.getPrimaryKeyField()).toString();
					logger.info("Key ID: {}", key);
					afterObjectMap.put(key, afterTempUpdateObject);
				}
				logger.debug("afterObjectMap size: {}", afterObjectMap.size());
			}

			for (String key : beforeObjectMap.keySet()) {
				if (!afterObjectMap.containsKey(key)) {
					String deleteRecipient = null;
					switch (eventField.getFieldId()) {
					case 103:
						deleteRecipient = ((ConnectSecondaryOwnerLinkT) beforeObjectMap
								.get(key)).getSecondaryOwner();
						break;
					case 203:
						deleteRecipient = ((OpportunitySalesSupportLinkT) beforeObjectMap
								.get(key)).getSalesSupportOwner();

						break;
					case 206:
						deleteRecipient = ((BidOfficeGroupOwnerLinkT) beforeObjectMap
								.get(key)).getBidOfficeGroupOwner();
						break;
					case 311:
						deleteRecipient = ((TaskBdmsTaggedLinkT) beforeObjectMap
								.get(key)).getBdmsTagged();
						break;
					}
					// Add Notification with the message
					// template
					String[] tokenValues={user, entityName, null, null, null,
							null, "removed", null, null, customerOrPartnerName, connectCategory, null};
					msgTemplate = replaceTokens(
							eventField.getMessageTemplate(),
							populateTokens(tokenValues));
					sendNotificationForCollections(newObj, deleteRecipient,
							msgTemplate, eventField);
				}
			}
		}
	}

	private void sendNotificationForCollections(Object newObj,
			String recipient, String msgTemplate,
			NotificationEventFieldsT eventField) throws Exception {
		if (msgTemplate != null) {
			if (newObj instanceof OpportunityT || newObj instanceof BidDetailsT) {
				OpportunityT opportunityT = (OpportunityT) newObj;
				if (((opportunityT.getModifiedBy() != null) && (!opportunityT
						.getModifiedBy().equals(recipient)))
						|| (!opportunityT.getCreatedBy().equals(recipient)))
					addUserNotifications(msgTemplate, recipient,
							eventField.getNotificationEventId());
			} else if (newObj instanceof ConnectT) {
				ConnectT connectT = (ConnectT) newObj;
				if (((connectT.getModifiedBy() != null) && (!connectT
						.getModifiedBy().equals(recipient)))
						|| (!connectT.getCreatedBy().equals(recipient)))
					addUserNotifications(msgTemplate, recipient,
							eventField.getNotificationEventId());
			} else if (newObj instanceof TaskT) {
				TaskT taskT = (TaskT) newObj;
				if (((taskT.getModifiedBy() != null) && (!taskT.getModifiedBy()
						.equals(recipient)))
						|| (!taskT.getCreatedBy().equals(recipient)))
					addUserNotifications(msgTemplate, recipient,
							eventField.getNotificationEventId());
			}

		}
	}

	// This method is used to populate the replacement tokens in the auto
	// comments message template
	private HashMap<String, String> populateTokens(String user,
			String entityName, String from, String to, String entityType,
			String ownership, String status, String salesStageDesc,
			String parentEntityName, String customerOrPartnerName, String connectCategory, String parentEntity) throws Exception {
		logger.debug("Inside populateTokens() method");
		HashMap<String, String> tokensMap = new HashMap<String, String>();
		if (user != null)
			tokensMap.put(TOKEN_USER, user);
		if (entityName != null)
			tokensMap.put(TOKEN_ENTITY_NAME, entityName);
		if (from != null)
			tokensMap.put(TOKEN_FROM, from);
		if (to != null)
			tokensMap.put(TOKEN_TO, to);
		if (entityType != null)
			tokensMap.put(TOKEN_ENTITY_TYPE, entityType);
		if (ownership != null)
			tokensMap.put(TOKEN_OWNERSHIP, ownership);
		if (status != null)
			tokensMap.put(TOKEN_STATUS, status);
		if (salesStageDesc != null)
			tokensMap.put(TOKEN_SALES_STAGE, salesStageDesc);
		if (parentEntityName != null)
			tokensMap.put(TOKEN_PARENT_ENTITY_NAME, parentEntityName);
		if (customerOrPartnerName != null)
			tokensMap.put(TOKEN_CUSTOMER_NAME, customerOrPartnerName);
		if (connectCategory != null)
			tokensMap.put(TOKEN_CONNECT_CATEGORY, connectCategory);
		if (parentEntity != null)
			tokensMap.put(TOKEN_PARENT_ENTITY, parentEntity);

		return tokensMap;
		
	}
//This method is overloaded for populating customer name 
	private HashMap<String, String> populateTokens(String user,
			String entityName, String from, String to, String entityType,
			String ownership, String status, String salesStageDesc,
			String parentEntityName) throws Exception {
		logger.debug("Inside populateTokens() method");
		HashMap<String, String> tokensMap = new HashMap<String, String>();
		if (user != null)
			tokensMap.put(TOKEN_USER, user);
		if (entityName != null)
			tokensMap.put(TOKEN_ENTITY_NAME, entityName);
		if (from != null)
			tokensMap.put(TOKEN_FROM, from);
		if (to != null)
			tokensMap.put(TOKEN_TO, to);
		if (entityType != null)
			tokensMap.put(TOKEN_ENTITY_TYPE, entityType);
		if (ownership != null)
			tokensMap.put(TOKEN_OWNERSHIP, ownership);
		if (status != null)
			tokensMap.put(TOKEN_STATUS, status);
		if (salesStageDesc != null)
			tokensMap.put(TOKEN_SALES_STAGE, salesStageDesc);
		if (parentEntityName != null)
			tokensMap.put(TOKEN_PARENT_ENTITY_NAME, parentEntityName);
		return tokensMap;
	}
	// This method is used to replace tokens in the auto comments message
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

	// This method is used to add notification entries for UserNotifications
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
				if (EntityType.TASK.equalsName(entityType))
					notification.setTaskId(entityId);
				if (EntityType.OPPORTUNITY.equalsName(entityType))
					notification.setOpportunityId(entityId);
				try {
					userNotificationsTRepository.save(notification);
					// logger.info(
					// "User notifications added successfully, notificationId: {}",
					// notification.getUserNotificationId());
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

	private void addUserNotifications(String message, String recipient,
			int eventId, String entityType, String entityId) throws Exception {
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
				if (EntityType.TASK.equalsName(entityType))
					notification.setTaskId(entityId);
				if (EntityType.OPPORTUNITY.equalsName(entityType))
					notification.setOpportunityId(entityId);
				try {
					userNotificationsTRepository.save(notification);
					// logger.info(
					// "User notifications added successfully, notificationId: {}",
					// notification.getUserNotificationId());
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
	
	/**
	 * This method is used to get the tokens for message templates used for notification
	 * @param values
	 * @return
	 * @throws Exception
	 */
	private HashMap<String, String> populateTokens(String[] values)
			throws Exception {

		logger.debug("Inside populateTokens() method");

		HashMap<String, String> tokensMap = new HashMap<String, String>();
		if (values[0] != null) {
			tokensMap.put(TOKEN_USER, values[0]);
		}
		if (values[1] != null) {
			tokensMap.put(TOKEN_ENTITY_NAME, values[1]);
		}
		if (values[2] != null) {
			tokensMap.put(TOKEN_FROM, values[2]);
		}
		if (values[3] != null) {
			tokensMap.put(TOKEN_TO, values[3]);
		}
		if (values[4] != null) {
			tokensMap.put(TOKEN_ENTITY_TYPE, values[4]);
		}
		
		if (values[5] != null) {
			tokensMap.put(TOKEN_OWNERSHIP, values[5]);
		}
		if (values[6] != null) {
			tokensMap.put(TOKEN_STATUS, values[6]);
		}
		if (values[7] != null) {
			tokensMap.put(TOKEN_SALES_STAGE, values[7]);
		}
		if (values[8] != null) {
			tokensMap.put(TOKEN_PARENT_ENTITY_NAME, values[8]);
		}
		if (values[9] != null) {
			tokensMap.put(TOKEN_CUSTOMER_NAME, values[9]);
		}
		if (values[10] != null) {
			tokensMap.put(TOKEN_CONNECT_CATEGORY, values[10]);
		}
		if (values[11] != null) {
			tokensMap.put(TOKEN_PARENT_ENTITY, values[11]);
		}

		return tokensMap;
	}

	public void setOpportunityRepository(
			OpportunityRepository opportunityRepository) {
		this.opportunityRepository = opportunityRepository;
	}

	public void setTaggedFollowedRepostory(
			FollowedRepository taggedFollowedRepository) {
		this.taggedFollowedRepository = taggedFollowedRepository;

	}

	public void setConnectRepository(ConnectRepository connectRepository) {
		this.connectRepository = connectRepository;
	}

	public void setNotificationEventGroupMappingTRepository(
			NotificationEventGroupMappingTRepository notificationEventGroupMappingTRepository) {
		this.notificationEventGroupMappingTRepository = notificationEventGroupMappingTRepository;
	}

	public void setTaskRepository(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void setFollowService(FollowedService followService) {
		this.followService = followService;
	}

	public FollowedService getFollowService() {
		return followService;
	}

	public void setUserNotificationSettingsConditionsRepository(
			UserNotificationSettingsConditionRepository userNotificationSettingsConditionRepository) {
		this.userNotificationSettingsConditionRepository = userNotificationSettingsConditionRepository;
	}

	public void setSearchKeywordsRepository(
			SearchKeywordsRepository searchKeywordsRepository) {
		this.searchKeywordsRepository = searchKeywordsRepository;

	}

	public void setAutoCommentsEntityTRepository(
			AutoCommentsEntityTRepository autoCommentsEntityTRepository) {
		this.autoCommentsEntityTRepository = autoCommentsEntityTRepository;
	}
}