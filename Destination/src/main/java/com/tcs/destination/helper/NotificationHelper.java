package com.tcs.destination.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tcs.destination.bean.AutoCommentsEntityFieldsT;
import com.tcs.destination.bean.AutoCommentsEntityT;
import com.tcs.destination.bean.CollaborationCommentT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.NotificationEventFieldsT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.bean.UserNotificationSettingsT;
import com.tcs.destination.bean.UserNotificationsT;
import com.tcs.destination.data.repository.AutoCommentsEntityFieldsTRepository;
import com.tcs.destination.data.repository.AutoCommentsEntityTRepository;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.NotificationsEventFieldsTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.data.repository.UserNotificationsTRepository;
import com.tcs.destination.enums.CommentType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.utils.Constants;

import org.springframework.data.repository.CrudRepository;

public class NotificationHelper implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(NotificationHelper.class);

	private static final String TOKEN_USER = "user";
	private static final String TOKEN_ENTITY_NAME = "entityName";
	private static final String TOKEN_PARENT_ENTITY = "parentEntityName";
	private static final String TOKEN_FROM = "from";
	private static final String TOKEN_TO = "to";
	private static final String PATTERN = "\\<(.+?)\\>";

	private Object oldObject;
	private String entityId;
	private String entityType;
	private CrudRepository crudRepository; 
	private NotificationsEventFieldsTRepository notificationsEventFieldsTRepository;
	private UserNotificationsTRepository userNotificationsTRepository;
	private UserNotificationSettingsRepository userNotificationSettingsRepo;
	private EntityManagerFactory entityManagerFactory;

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
	public UserNotificationsTRepository getUserNotificationsTRepository() {
		return userNotificationsTRepository;
	}
	public void setUserNotificationsTRepository(
			UserNotificationsTRepository userNotificationsTRepository) {
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
		logger.debug("Inside processNotifications() method");
		logger.info("Processing Notification events for entity: {}: {}", entityType, entityId);
		List<NotificationEventFieldsT> notificationEventFields = null;

		try {
			// Wait for sometime for hibernate to flush updates in database
			Thread.sleep(8000);

			if (entityType != null) {
				// Get the notification eligible entity & fields
				notificationEventFields = getNotificationEventFields();
				if (notificationEventFields != null && !notificationEventFields.isEmpty()) {
					processNotifications(notificationEventFields);
				}
			}
		} catch (Exception e) {
			logger.error("Error occurred while processing notifications " + e.getMessage());
			e.printStackTrace();
		}
		logger.info("Finished processing Notification events for entity: {}: {}", entityType, entityId);
	}

	// This method is used to get NotificationEventFieldsT details for the given EntityType
	private List<NotificationEventFieldsT> getNotificationEventFields() throws Exception {
		logger.debug("Inside getEntity() method");
		return(notificationsEventFieldsTRepository.findByEntityTypeAndIsactiveAndParentFieldIdIsNull(entityType, Constants.Y));
	}

	// This method is used to process notification for new entity Adds
	private void processNotifications(List<NotificationEventFieldsT> notificationEventFieldsList) throws Exception {
		logger.info("Inside processAdds() method");
		String user = null;
		String entityName = null;
		String msgTemplate = null;
		String recipient = null;
		Object dbObject = NotificationsLazyLoader.loadLazyCollections(entityId, entityType, crudRepository, notificationsEventFieldsTRepository, entityManagerFactory);
		if (notificationEventFieldsList != null && !notificationEventFieldsList.isEmpty()) {
			if (EntityType.contains(entityType)) {
				switch (EntityType.valueOf(entityType)) {
				case CONNECT: {
					logger.debug("Processing Notifications for Add, ConnectId: {}", entityId);
					ConnectT connect =  (ConnectT) dbObject;
					//((ConnectRepository) crudRepository).findOne(entityId);
					user = connect.getModifiedByUser().getUserName();
					entityName = connect.getConnectName();
					String fieldType = null;
					if (connect != null) {
						for (NotificationEventFieldsT notificationField : notificationEventFieldsList) {
							fieldType = notificationField.getFieldType();
							if (fieldType.equalsIgnoreCase(Constants.FIELD)) {
								// Handling update
								if (oldObject != null) {
									String oldValue = PropertyUtils.getProperty(oldObject, notificationField.getFieldName()).toString();
									String newValue = PropertyUtils.getProperty(connect, notificationField.getFieldName()).toString();
									if (!oldValue.equalsIgnoreCase(newValue)) {
										// Get notification recipient user id 
										recipient = (String) PropertyUtils.getProperty(connect, notificationField.getUseridField());
									} else {
										continue;
									}
								} else {
									// Get notification recipient user id 
									recipient = (String) PropertyUtils.getProperty(connect, notificationField.getUseridField());
								}

								msgTemplate = replaceTokens(notificationField.getMessageTemplate(), 
										populateTokens(user, entityName, null, null));
								if (msgTemplate != null) {
									addUserNotifications(msgTemplate, recipient,notificationField.getNotificationEventId());
								}
							} else if (fieldType.equalsIgnoreCase(Constants.COLLECTION)) {
								processCollections(user, entityName, notificationField, oldObject, connect);
							}
						}
					} else {
						logger.error("Invalid Connect Id: {}", entityId);
						throw new Exception("Invalid Connect Id: " + entityId);
					}
					break;
				}
				case OPPORTUNITY : {
					logger.debug("Processing Notifications for Add, OpportunityId: {}", entityId);
					OpportunityT opportunity =  (OpportunityT) dbObject;
					user = opportunity.getModifiedByUser().getUserName();
					entityName = opportunity.getOpportunityName();
					String fieldType = null;
					if (opportunity != null) {
						for (NotificationEventFieldsT notificationField : notificationEventFieldsList) {
							fieldType = notificationField.getFieldType();
							if (fieldType.equalsIgnoreCase(Constants.FIELD)) {
								// Handling update
								if (oldObject != null) {
									String oldValue = PropertyUtils.getProperty(oldObject, notificationField.getFieldName()).toString();
									String newValue = PropertyUtils.getProperty(opportunity, notificationField.getFieldName()).toString();
									if (!oldValue.equalsIgnoreCase(newValue)) {
										// Get notification recipient user id 
										recipient = (String) PropertyUtils.getProperty(opportunity, notificationField.getUseridField());
									} else {
										continue;
									}
								} else {
									// Get notification recipient user id 
									recipient = (String) PropertyUtils.getProperty(opportunity, notificationField.getUseridField());
								}

								msgTemplate = replaceTokens(notificationField.getMessageTemplate(), 
										populateTokens(user, entityName, null, null));
								if (msgTemplate != null) {
									addUserNotifications(msgTemplate, recipient,notificationField.getNotificationEventId());
								}
							} else if (fieldType.equalsIgnoreCase(Constants.COLLECTION)) {
								processCollections(user, entityName, notificationField, oldObject, opportunity);
							}
						}
					} else {
						logger.error("Invalid Opportunity Id: {}", entityId);
						throw new Exception("Invalid Opportunity Id: " + entityId);
					}
					break;
				}
				case TASK : {
					logger.debug("Processing Notifications for Add, TaskId: {}", entityId);
					TaskT task =  (TaskT) dbObject;
					user = task.getModifiedByUser().getUserName();
					entityName = task.getTaskDescription();
					String fieldType = null;
					if (task != null) {
						for (NotificationEventFieldsT notificationField : notificationEventFieldsList) {
							fieldType = notificationField.getFieldType();
							if (fieldType.equalsIgnoreCase(Constants.FIELD)) {
								// Handling update
								if (oldObject != null) {
									String oldValue = PropertyUtils.getProperty(oldObject, notificationField.getFieldName()).toString();
									String newValue = PropertyUtils.getProperty(task, notificationField.getFieldName()).toString();
									if (!oldValue.equalsIgnoreCase(newValue)) {
										// Get notification recipient user id 
										recipient = (String) PropertyUtils.getProperty(task, notificationField.getUseridField());
									} else {
										continue;
									}
								} else {
									// Get notification recipient user id
									if(!notificationField.getUseridField().equalsIgnoreCase("targetDateForCompletion")){
										recipient = (String) PropertyUtils.getProperty(task, notificationField.getUseridField());
									} else {
										
									}
								}

								msgTemplate = replaceTokens(notificationField.getMessageTemplate(), 
										populateTokens(user, entityName, null, null));
								if (msgTemplate != null) {
									addUserNotifications(msgTemplate, recipient,notificationField.getNotificationEventId());
								}
							} else if (fieldType.equalsIgnoreCase(Constants.COLLECTION)) {
								processCollections(user, entityName, notificationField, oldObject, task);
							}
						}
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

	private void processCollections(String user, String entityName, NotificationEventFieldsT notificationField, Object oldObj, Object newObj) 
			throws Exception {
		logger.debug("Inside processCollections() method");;
		Object beforeUpdate = null;
		Object afterUpdate = null;

		if (notificationField != null) {
			logger.debug("Field: {}", notificationField.getFieldName());
			if (oldObj != null) {
				beforeUpdate = PropertyUtils.getProperty(oldObj, notificationField.getFieldName());
			}
			afterUpdate = PropertyUtils.getProperty(newObj, notificationField.getFieldName());

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
					return;
				}
			}

			// Create a map with db objects before update for comparison
			HashMap<String, Object> beforeObjectMap = null;
			if (beforeUpdate != null) {
				String key = null;
				beforeObjectMap = new HashMap<String, Object>();
				List<Object> beforeUpdateObjectList = (List<Object>) beforeUpdate;
				logger.debug("fromObjectList collection size: {}", beforeUpdateObjectList.size());
				for (Object beforeUpdateObject: beforeUpdateObjectList) {
					key = PropertyUtils.getProperty(beforeUpdateObject, notificationField.getPrimaryKeyField()).toString();
					logger.info("Key ID: {}", key);
					beforeObjectMap.put(key, beforeUpdateObject);
				}
				logger.debug("beforeObjectMap size: {}", beforeObjectMap.size());
			}

			// Get the child fields
			List<NotificationEventFieldsT> notificationEventFieldsTs = 
					notificationsEventFieldsTRepository.findByParentFieldIdAndIsactive(notificationField.getFieldId(), Constants.Y);

			String recipient = null;
			String msgTemplate = null;
			for (NotificationEventFieldsT eventField : notificationEventFieldsTs) {
				logger.info("Field Name: {}", eventField.getFieldName()); 
				String fieldType = eventField.getFieldType();
				if (fieldType.equalsIgnoreCase(Constants.FIELD)) {
					// Check the value in afterUpdate
					if (afterUpdate != null) {
						List<Object> afterUpdateObjectList = (List<Object>) afterUpdate;
						logger.debug("toObjectList collection size: {}", afterUpdateObjectList.size());
						for (Object afterUpdateObject: afterUpdateObjectList) {
							String keyId = PropertyUtils.getProperty(afterUpdateObject, notificationField.getPrimaryKeyField()).toString();
							String fieldValue = PropertyUtils.getProperty(afterUpdateObject, eventField.getFieldName()).toString();
							logger.info("After Update Field Value: {}", fieldValue);
							if (beforeObjectMap != null && !beforeObjectMap.isEmpty()) {
								// If key is present in beforeUpdate, then check the field value
								if (beforeObjectMap.containsKey(keyId)) {
									// Field value not same
									if (!fieldValue.equalsIgnoreCase(PropertyUtils.getProperty(beforeObjectMap.get(keyId), eventField.getFieldName()).toString())) {
										// Get notification recipient user id 
										recipient = (String) PropertyUtils.getProperty(afterUpdateObject, eventField.getUseridField());
									} else {
										// Field value same
										continue;
									}
								} else {
									// If value is not present in beforeUpdate, then it was added in update
									// Get notification recipient user id 
									recipient = (String) PropertyUtils.getProperty(afterUpdateObject, eventField.getUseridField());
								}
							} else {
								// No element in collection in before update object
								// Get notification recipient user id 
								recipient = (String) PropertyUtils.getProperty(afterUpdateObject, eventField.getUseridField());
							}
							// Add Notification with the message template
							msgTemplate = replaceTokens(eventField.getMessageTemplate(), populateTokens(user, entityName, null, null));
							if (msgTemplate != null) {
								addUserNotifications(msgTemplate, recipient, eventField.getNotificationEventId());
							}
						}
					}
				} else if (fieldType.equalsIgnoreCase(Constants.COLLECTION)) {
					// Process inner collections
					if (afterUpdate != null) {
						List<Object> afterUpdateObjectList = (List<Object>) afterUpdate;
						logger.debug("toObjectList collection size: {}", afterUpdateObjectList.size());
						for (Object afterUpdateObject: afterUpdateObjectList) {
							String keyId = PropertyUtils.getProperty(afterUpdateObject, notificationField.getPrimaryKeyField()).toString();
							if (beforeObjectMap != null && !beforeObjectMap.isEmpty()) {
								processCollections(user, entityName, eventField, beforeObjectMap.get(keyId), afterUpdateObject);
							} else {
								processCollections(user, entityName, eventField, null, afterUpdateObject);
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
	//		if (EntityType.contains(entityType)) {
	//			switch (EntityType.valueOf(entityType)) {
	//			case TASK: {
	//				logger.debug("Processing Notifications for Add, TaskId: {}", entityId);
	//				TaskT task = ((TaskRepository) crudRepository).findOne(entityId);
	//				if (task != null) {
	//					user = task.getModifiedByUser().getUserName();
	//					entityName = task.getTaskDescription();
	//					if (task.getConnectT() != null) {
	//						parentEntityName = task.getConnectT().getConnectName();
	//					} else if (task.getOpportunityT() != null) {
	//						parentEntityName = task.getOpportunityT().getOpportunityName();
	//					} else {
	//						logger.error("Invalid Task Parent Entity");
	//						throw new Exception("Invalid Task Parent Entity");
	//					}
	//					msgTemplate = replaceTokens(notificationEventFields.getMessageTemplate(), 
	//							populateTokens(user, entityName, parentEntityName, null, null));
	//				} else {
	//					logger.error("Invalid Task Id: {}", entityId);
	//					throw new Exception("Invalid Task Id: " + entityId);
	//				}
	//				break;
	//			}
	//			case CONNECT: {
	//				logger.debug("Processing Notifications for Add, ConnectId: {}", entityId);
	//				ConnectT connect = ((ConnectRepository) crudRepository).findOne(entityId);
	//				if (connect != null) {
	//					user = connect.getModifiedByUser().getUserName();
	//					entityName = connect.getConnectName();
	//					if (connect.getCustomerMasterT() != null) {
	//						parentEntityName = connect.getCustomerMasterT().getCustomerName();
	//					} else if (connect.getPartnerMasterT() != null) {
	//						parentEntityName = connect.getPartnerMasterT().getPartnerName();
	//					} else {
	//						logger.error("Invalid Connect Parent Entity");
	//						throw new Exception("Invalid Connect Parent Entity");
	//					}
	//					msgTemplate = replaceTokens(notificationEventFields.getMessageTemplate(), 
	//							populateTokens(user, entityName, parentEntityName, null, null));
	//				} else {
	//					logger.error("Invalid Connect Id: {}", entityId);
	//					throw new Exception("Invalid Connect Id: " + entityId);
	//				}
	//				break;
	//			}
	//			case OPPORTUNITY: {
	//				logger.debug("Processing Notifications for Add, OpportunityId: {}", entityId);
	//				OpportunityT opportunity = ((OpportunityRepository) crudRepository).findOne(entityId);
	//				if (opportunity != null) {
	//					user = opportunity.getModifiedByUser().getUserName();
	//					entityName = opportunity.getOpportunityName();
	//					if (opportunity.getCustomerMasterT() != null) {
	//						parentEntityName = opportunity.getCustomerMasterT().getCustomerName();
	//					} else {
	//						logger.error("Invalid Opportunity Parent Entity");
	//						throw new Exception("Invalid Opportunity Parent Entity");
	//					}
	//					msgTemplate = replaceTokens(notificationEventFields.getMessageTemplate(), 
	//							populateTokens(user, entityName, parentEntityName, null, null));
	//				} else {
	//					logger.error("Invalid Opportunity Id: {}", entityId);
	//					throw new Exception("Invalid Opportunity Id: " + entityId);
	//				}
	//				break;
	//			}
	//			default:
	//				logger.error("Invalid Entity Type: " + entityType);
	//				throw new Exception("Invalid Entity Type: " + entityType);
	//			}
	//Add auto comments
	//			if (msgTemplate != null) {
	//			 addUserNotifications(msgTemplate);
	//			}
	//		} else {
	//			logger.error("Invalid Entity Type: " + entityType);
	//			throw new Exception("Invalid Entity Type: " + entityType);
	//		}
	//	}

	// This method is used to process auto comments for entity updates
	//	public void processUpdates(AutoCommentsEntityT autoCommentsEntity) throws Exception {
	//		logger.info("Inside processUpdates() method");
	//
	//		if (EntityType.contains(entityType)) {
	//			switch (EntityType.valueOf(entityType)) {
	//			case TASK: {
	//				processTaskUpdate(autoCommentsEntity);
	//				break;
	//			}
	//			case CONNECT: {
	//				processConnectUpdate(autoCommentsEntity);
	//				break;
	//			}
	//			case OPPORTUNITY: {
	//				processOpportunityUpdate(autoCommentsEntity);
	//				break;
	//			}
	//			default:
	//				logger.error("Invalid Entity Type: " + entityType);
	//				throw new Exception("Invalid Entity Type: " + entityType);
	//			}
	//		}
	//	}

	// This method is used to process auto comments events for Task
	//	private void processTaskUpdate(AutoCommentsEntityT autoCommentsEntity) throws Exception {
	//		logger.info("Processing Auto comments for Update, TaskId: {}", entityId);
	//		String user = null;
	//		String entityName = null;
	//		String parentEntityName = null;
	//
	//		// Get the fields eligible for Auto comments
	//		List<AutoCommentsEntityFieldsT> fields = notificationsEventFieldsTRepository.
	//				findByEntityIdAndIsactiveOrderByTypeAsc(autoCommentsEntity.getEntityId(), Constants.Y);
	//		if (fields != null && !fields.isEmpty()) {
	//			TaskT task = ((TaskRepository) crudRepository).findOne(entityId);;
	//			if (task != null) {
	//				user = task.getModifiedByUser().getUserName();
	//				entityName = task.getTaskDescription();
	//				// Iterate auto comments eligible fields and add auto comments
	//				for (AutoCommentsEntityFieldsT field: fields) {
	//					if (field.getType().equalsIgnoreCase(Constants.FIELD)) {
	//						processEntityFieldUpdate(user, entityName, parentEntityName, field, oldObject, task);
	//					} else {
	//						// To-Do for Child Objects
	//					}
	//				}
	//			} else {
	//				logger.error("Invalid Task Id: {}", entityId);
	//				throw new Exception("Invalid Task Id: " + entityId);
	//			}
	//		} else {
	//			logger.info("No eligible fields for Auto comments, TaskId :{}", entityId);
	//		}
	//		logger.debug("Finished processing Auto comments for Update, TaskId: {}", entityId);
	//	}
	//
	//	// This method is used to process auto comments events for Connect
	//	public void processConnectUpdate(AutoCommentsEntityT autoCommentsEntity) throws Exception {
	//		logger.info("Processing Auto comments for Update, ConnectId: {}", entityId);
	//		String user = null;
	//		String entityName = null;
	//		String parentEntityName = null;
	//
	//		// Get the fields eligible for Auto comments
	//		List<AutoCommentsEntityFieldsT> fields = notificationsEventFieldsTRepository.
	//				findByEntityIdAndIsactiveOrderByTypeAsc(autoCommentsEntity.getEntityId(), Constants.Y);
	//		if (fields != null && !fields.isEmpty()) {
	//			// Load object with auto comments eligible lazy collections
	//			ConnectT connect = (ConnectT) NotificationsLazyLoader.loadLazyCollections(entityId, EntityType.CONNECT.name(), 
	//					crudRepository, notificationsEventFieldsTRepository, entityManagerFactory);
	//			if (connect != null) {
	//				user = connect.getModifiedByUser().getUserName();
	//				entityName = connect.getConnectName();
	//				// Iterate auto comments eligible fields and add auto comments
	//				for (AutoCommentsEntityFieldsT field: fields) {
	//					if (field.getType().equalsIgnoreCase(Constants.FIELD)) {
	//						processEntityFieldUpdate(user, entityName, parentEntityName, field, oldObject, connect);
	//					} else if (field.getType().equalsIgnoreCase(Constants.COLLECTION)){
	//						// Handle Collections Objects
	//						processCollections(user, entityName, entityName, field, oldObject, connect);
	//					} else {
	//						logger.error("Invalid Field Type: {}", field.getType());
	//						throw new Exception("Invalid Field Type: " + field.getType());
	//					}
	//				}
	//			} else {
	//				logger.error("Invalid Connect Id: {}", entityId);
	//				throw new Exception("Invalid Connect Id: " + entityId);
	//			}
	//		} else {
	//			logger.info("No eligible fields for Auto comments, ConnectId :{}", entityId);
	//		}
	//		logger.debug("Finished processing Auto comments for Update, ConnectId: {}", entityId);
	//	}
	//
	//	// This method is used to process auto comments events for Opportunity
	//	private void processOpportunityUpdate(AutoCommentsEntityT autoCommentsEntity) throws Exception {
	//		logger.info("Processing Auto comments for Update, OpportunityId: {}", entityId);
	//		String user = null;
	//		String entityName = null;
	//		String parentEntityName = null;
	//
	//		// Get the fields eligible for Auto comments
	//		List<AutoCommentsEntityFieldsT> fields = n+otificationsEventFieldsTRepository.
	//				findByEntityIdAndIsactiveOrderByTypeAsc(autoCommentsEntity.getEntityId(), Constants.Y);
	//		if (fields != null && !fields.isEmpty()) {
	//			OpportunityT opportunity = (OpportunityT) AutoCommentsLazyLoader.loadLazyCollections(entityId, EntityType.OPPORTUNITY.name(), 
	//					crudRepository, autoCommentsEntityTRepository, n+otificationsEventFieldsTRepository, entityManagerFactory);
	//			if (opportunity != null) {
	//				user = opportunity.getModifiedByUser().getUserName();
	//				entityName = opportunity.getOpportunityName();
	//				// Iterate auto comments eligible fields and add auto comments
	//				for (AutoCommentsEntityFieldsT field: fields) {
	//					if (field.getType().equalsIgnoreCase(Constants.FIELD)) {
	//						processEntityFieldUpdate(user, entityName, parentEntityName, field, oldObject, opportunity);
	//					} else if (field.getType().equalsIgnoreCase(Constants.COLLECTION)){
	//						// Handle Collections Objects
	//						processCollections(user, entityName, entityName, field, oldObject, opportunity);
	//					} else {
	//						logger.error("Invalid Field Type: {}", field.getType());
	//						throw new Exception("Invalid Field Type: " + field.getType());
	//					}
	//				}
	//			} else {
	//				logger.error("Invalid Opportunity Id: {}", entityId);
	//				throw new Exception("Invalid Opportunity Id: " + entityId);
	//			}
	//		} else {
	//			logger.info("No eligible fields for Auto comments, OpportunityId :{}", entityId);
	//		}
	//		logger.debug("Finished processing Auto comments for Update, OpportunityId: {}", entityId);
	//	}
	//
	//	// This method is used to add auto comments for a particular entity field update
	//	private void processEntityFieldUpdate(String user, String entityName, String parentEntityName, AutoCommentsEntityFieldsT field, Object oldEntity, Object newEntity)
	//			throws Exception {
	//		logger.debug("Inside processEntityFieldUpdate() method");;
	//		Object fromValue = null;
	//		Object toValue = null;
	//		String msgTemplate = null;
	//
	//		if (field != null) {
	//			logger.debug("Field: {}", field.getName());
	//			fromValue = PropertyUtils.getProperty(oldEntity, field.getName());
	//			toValue = PropertyUtils.getProperty(newEntity, field.getName());
	//			logger.debug("fromValue: {}", fromValue);
	//			logger.debug("toValue: {}", toValue);
	//			// Field value updated in update
	//			if (fromValue != null) {
	//				if (toValue != null && !fromValue.equals(toValue)) { 
	//					msgTemplate = replaceTokens(field.getUpdateMessageTemplate(), 
	//							populateTokens(user, entityName, parentEntityName, fromValue.toString(), toValue.toString()));
	//				}
	//			} else {
	//				// Field value add newly in update
	//				if (toValue != null) {
	//					msgTemplate = replaceTokens(field.getAddMessageTemplate(), 
	//							populateTokens(user, entityName, parentEntityName, null, toValue.toString()));
	//				}
	//			}
	//			//Add auto comments
	//			if (msgTemplate != null) {
	//				addCollaborationComments(msgTemplate);
	//			}
	//		}
	//	}
	//
	//	// This method is used to add auto comments for collections objects
	//	public void processCollections(String user, String entityName, String parentEntityName, AutoCommentsEntityFieldsT entityField, Object oldEntity, Object newEntity) 
	//			throws Exception {
	//		logger.debug("Inside processCollections() method");;
	//		Object beforeUpdate = null;
	//		Object afterUpdate = null;
	//
	//		if (entityField != null) {
	//			logger.debug("Field: {}", entityField.getName());
	//			beforeUpdate = PropertyUtils.getProperty(oldEntity, entityField.getName());
	//			afterUpdate = PropertyUtils.getProperty(newEntity, entityField.getName());
	//
	//			// Handle empty collections
	//			if (beforeUpdate != null && (beforeUpdate instanceof List)) {
	//				if (((List) beforeUpdate).size() == 0)
	//					beforeUpdate = null;
	//			}
	//			if (afterUpdate != null && (afterUpdate instanceof List)) {
	//				if (((List) afterUpdate).size() == 0)
	//					afterUpdate = null;
	//			}
	//
	//			if (beforeUpdate == null) {
	//				// No add or update during update
	//				if (afterUpdate == null) {
	//					logger.debug("No values before and after update");
	//					return;
	//				}
	//			} else {
	//				// All db values were deleted during update 
	//				if (afterUpdate == null) {
	//					logger.debug("All db values before update were deleted during update");
	//					return;
	//				}
	//			}
	//			
	//			// Linked entity is required
	//			AutoCommentsEntityT linkedEntity = null;
	//			if (entityField.getLinkedEntity() != null) {
	//				linkedEntity = entityField.getLinkedEntity();
	//			}
	//			if (linkedEntity == null) {
	//				logger.error("Missing Linked Entity for field: {}", entityField.getName());
	//				throw new Exception("Missing Linked Entity for field: " + entityField.getName());
	//			}
	//
	//			// Get the fields for linkedEntity
	//			List<NotificationEventFieldsT> linkedEntityFields = notificationsEventFieldsTRepository.
	//					findByEntityIdAndIsactiveOrderByTypeAsc(linkedEntity.getEntityId(), Constants.Y);
	//			
	//			// Linked entity should have fields to compare
	//			// It must have a record with type as I (Objects Identifier) with compare field  
	//			if ((linkedEntityFields == null) 
	//					|| (linkedEntityFields.size() == 0)) {
	//				logger.error("Missing Linked Entity Fields for field: {}", entityField.getName());
	//				throw new Exception("Missing Linked Entity Fields for field: " + entityField.getName());
	//			}
	//
	//			// Get the entity field record with Identifier
	//			NotificationEventFieldsT keyIdField = null;
	//			for (NotificationEventFieldsT field: linkedEntityFields) {
	//				if (field.getType().equalsIgnoreCase(Constants.ID_FIELD)) {
	//					keyIdField = field;
	//					logger.debug("Key ID Entity Field: {}" , keyIdField.getName());
	//					break;
	//				}
	//			}
	//
	//			// Key ID field is required for HashMap comparison
	//			if (keyIdField == null) {
	//				logger.error("Missing Key ID Entity Field for field: {}", entityField.getName());
	//				throw new Exception("Missing Key ID Entity Field for field: " + entityField.getName());
	//			}
	//			
	//			// Compare field is also required for Key ID field
	//			if (keyIdField.getCompareField() == null) {
	//				logger.error("Missing Compare Field for Linked Entity Field: {}", keyIdField.getName());
	//				throw new Exception("Missing Compare Field for Linked Entity Field: " + keyIdField.getName());
	//			}
	//			logger.debug("Key ID Entity Compare Field: {}" , keyIdField.getCompareField());
	//
	//			// Create a map with db objects before update for comparison
	//			HashMap<String, Object> beforeObjectMap = null;
	//			if (beforeUpdate != null) {
	//				String key = null;
	//				beforeObjectMap = new HashMap<String, Object>();
	//				List<Object> beforeUpdateObjectList = (List<Object>) beforeUpdate;
	//				logger.debug("fromObjectList collection size: {}", beforeUpdateObjectList.size());
	//				for (Object beforeUpdateObject: beforeUpdateObjectList) {
	//					key = PropertyUtils.getProperty(beforeUpdateObject, keyIdField.getName()).toString();
	//					logger.info("Key ID: {}", key);
	//					beforeObjectMap.put(key, beforeUpdateObject);
	//				}
	//				logger.debug("beforeObjectMap size: {}", beforeObjectMap.size());
	//			}
	//			
	//			// Process db objects after update
	//			if (afterUpdate != null) {
	//				List<Object> afterUpdateObjectList = (List<Object>) afterUpdate;
	//				logger.debug("toObjectList collection size: {}", afterUpdateObjectList.size());
	//				// Process Key ID compare field first
	//				// Remove the Key ID field from the list as it is processed first 
	//				linkedEntityFields.remove(keyIdField);
	//
	//				StringBuffer newAdds = new StringBuffer();
	//				String key = null;
	//				String newValue = null;
	//				String oldValue = null;
	//				for (Object afterUpdateObject: afterUpdateObjectList) {
	//					key = PropertyUtils.getProperty(afterUpdateObject, keyIdField.getName()).toString();
	//					newValue = PropertyUtils.getProperty(afterUpdateObject, keyIdField.getCompareField()).toString();
	//					if (beforeObjectMap != null) {
	//						// Check if the Key ID already exists before update in db
	//						// Same Key ID 
	//						if (beforeObjectMap.containsKey(key)) {
	//							oldValue = PropertyUtils.getProperty(beforeObjectMap.get(key), keyIdField.getCompareField()).toString();
	//							// Value not changed
	//							if (oldValue.equalsIgnoreCase(newValue)) {
	//								logger.debug("Value not changed for Key ID: {}, Value: {}", key, newValue);
	//								// continue;
	//							} else {
	//								// Value changed, add auto comments
	//								logger.debug("Value changed for Key ID: {}, adding auto comments", key);
	//								logger.debug("Old Value: {}, New Value: {}", oldValue, newValue);
	//								String msgTemplate = replaceTokens(keyIdField.getUpdateMessageTemplate(), 
	//										populateTokens(user, entityName, parentEntityName, oldValue, newValue));
	//								//Add auto comments
	//								if (msgTemplate != null) {
	//									addCollaborationComments(msgTemplate);
	//								}
	//							}
	//
	//							// Process other fields in the collection object eligible for auto comments after removing Key ID field
	//							if (linkedEntityFields.size() > 0) {
	//								logger.debug("Linked Entity has more fields eligible for auto comments");	
	//								for (NotificationEventFieldsT field: linkedEntityFields) {
	//									if (field.getType().equalsIgnoreCase(Constants.FIELD)) {
	//										processEntityFieldUpdate(user, entityName, parentEntityName, field, beforeObjectMap.get(key), afterUpdateObject);
	//									} else if (field.getType().equalsIgnoreCase(Constants.COLLECTION)) {
	//										// Handle Collections Objects
	//										processCollections(user, entityName, parentEntityName, field, beforeObjectMap.get(key), afterUpdateObject);
	//									} else {
	//										logger.error("Invalid Field Type: {}", field.getType());
	//										throw new Exception("Invalid Field Type: " + field.getType());
	//									}
	//								}
	//							}
	//						} else {
	//							logger.debug("Inside new adds during update");
	//							// New add during update
	//							newAdds.append(newValue).append(",");
	//						}
	//					} else {
	//						logger.debug("Only new adds during update");
	//						// beforeImage did not have any objects
	//						// Only new adds during update
	//						newAdds.append(newValue).append(",");
	//					}
	//				}
	//				
	//				// Add auto comments for new Adds during update
	//				if (newAdds.length() > 0) {
	//					logger.debug("Adding auto comments for new Adds during update");
	//					String entityValues = newAdds.substring(0, newAdds.length()-1);
	//					String msgTemplate = replaceTokens(keyIdField.getAddMessageTemplate(), 
	//							populateTokens(user, entityValues, parentEntityName, null, null));
	//					//Add auto comments
	//					if (msgTemplate != null) {
	//						addCollaborationComments(msgTemplate);
	//					}
	//				}
	//			}
	//		}
	//	}

	// This method is used to populate the replacement tokens in the auto comments message template
	private HashMap<String, String> populateTokens(String user, String entityName, String from, String to) 
			throws Exception {
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
		return tokensMap;
	}

	// This method is used to replace tokens in the auto comments message template
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

	// This method is used to add notification entries for UserNotifications
	private void addUserNotifications(String message, String recipient, int eventId) throws Exception {
		logger.debug("Inside addUserNotifications() method");

		List<UserNotificationSettingsT> userNotificationSettings = userNotificationSettingsRepo.findByUserIdAndEventIdAndIsactive(recipient, eventId, Constants.Y);
		if(userNotificationSettings != null && !userNotificationSettings.isEmpty()){
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