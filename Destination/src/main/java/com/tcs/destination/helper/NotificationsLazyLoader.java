package com.tcs.destination.helper;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CollaborationCommentT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.NotificationEventFieldsT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.NotificationsEventFieldsTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.utils.Constants;

@Component
public class NotificationsLazyLoader {

	private static final Logger logger = LoggerFactory.getLogger(NotificationsLazyLoader.class);

	// This method is used to load object with notification eligible lazy collections
	public static Object loadLazyCollections(String entityId, String entityType, CrudRepository crudRepository, 
			NotificationsEventFieldsTRepository entityFieldsRepo, EntityManagerFactory emf) throws Exception {
		logger.debug("Inside loadLazyCollections() method");
		logger.info("EntityType: {}. EntityId: {}" , entityType, entityId);

		Object object = null;
		EntityManager em = null;
		
		try {
			// EntityManagerFactory will be passed from async thread
			if (emf != null) {
				logger.debug("Entity Manager Factory is not null, creating Entity Manager");
				em = emf.createEntityManager();
			}
			
			if (EntityType.contains(entityType)) {
				// Handling different Entity types
				switch (EntityType.valueOf(entityType)) {
				case CONNECT: {
					ConnectT connect = null;	
					if (em != null) {
						logger.debug("Loading Connect from database using entity manager for Asynchronous Thread");
						connect = em.find(ConnectT.class, entityId);
						em.refresh(connect);
					} else {
						logger.debug("Loading Connect from database using JPA Repository for Main Thread");
						connect = ((ConnectRepository) crudRepository).findOne(entityId);
					}
					if (connect != null) {
						logger.info("Loading Notifications eligible lazy collections for Connect: {}", entityId);
						// Load notifications eligible Top level lazy collections
						List<NotificationEventFieldsT> fields = 
								entityFieldsRepo.findByEntityTypeAndFieldTypeAndIsactiveAndParentFieldIdIsNull(entityType, Constants.COLLECTION, Constants.Y);
						if (fields != null & fields.size() > 0) {
							loadCollections(fields, connect, entityFieldsRepo);
						} else {
							logger.info("No eligible collections for Notification, Connect: {}", entityId);
						}
						return connect;
					} 
					break;
				}
				case OPPORTUNITY: {
					OpportunityT opportunity = null;	
					if (em != null) {
						logger.debug("Loading Opportunity from database using entity manager for Asynchronous Thread");
						opportunity = em.find(OpportunityT.class, entityId);
						em.refresh(opportunity);
					} else {
						logger.debug("Loading Opportunity from database using JPA Repository for Main Thread");
						opportunity = ((OpportunityRepository) crudRepository).findOne(entityId);
					}
					if (opportunity != null) {
						logger.debug("Loading notifications eligible lazy collections for Opportunity: {}", entityId);
						// Load notification eligible Top level lazy collections
						List<NotificationEventFieldsT> fields = 
								entityFieldsRepo.findByEntityTypeAndFieldTypeAndIsactiveAndParentFieldIdIsNull(entityType, Constants.COLLECTION, Constants.Y);
						if (fields != null & fields.size() > 0) {
							loadCollections(fields, opportunity, entityFieldsRepo);
						} else {
							logger.info("No eligible collections for Notification, Opportunity: {}", entityId);
						}
						return opportunity;
					} 
					break;
				}
				case TASK: {
					TaskT task = null;	
					if (em != null) {
						logger.debug("Loading Opportunity from database using entity manager for Asynchronous Thread");
						task = em.find(TaskT.class, entityId);
						em.refresh(task);
					} else {
						logger.debug("Loading Opportunity from database using JPA Repository for Main Thread");
						task = ((TaskRepository) crudRepository).findOne(entityId);
					}
					if (task != null) {
						logger.debug("Loading notifications eligible lazy collections for Task: {}", entityId);
						// Load notification eligible Top level lazy collections
						List<NotificationEventFieldsT> fields = 
								entityFieldsRepo.findByEntityTypeAndFieldTypeAndIsactiveAndParentFieldIdIsNull(entityType, Constants.COLLECTION, Constants.Y);
						if (fields != null & fields.size() > 0) {
							loadCollections(fields, task, entityFieldsRepo);
						} else {
							logger.info("No eligible collections for Notification, Task: {}", entityId);
						}
						return task;
					} 
					break;
				}
				case COMMENT:
					CollaborationCommentT collaborationCommentT=null;
					if (em != null) {
						logger.debug("Loading Comments from database using entity manager for Asynchronous Thread");
						collaborationCommentT = em.find(CollaborationCommentT.class, entityId);
						em.refresh(collaborationCommentT);
					} else {
						logger.debug("Loading Comments from database using JPA Repository for Main Thread");
						collaborationCommentT = ((CollaborationCommentsRepository) crudRepository).findOne(entityId);
					}
					if(collaborationCommentT!=null)
						return collaborationCommentT;
					break;
				default:
					logger.error("Invalid Entity Type: " + entityType);
					throw new Exception("Invalid Entity Type: " + entityType);
				}
			}
		} catch (Exception e) {
			logger.error("Exception occurred while loading lazy collections:" + e.getMessage());
			throw new Exception ("Exception occurred while loading lazy collections");
		} finally {
			if (em != null) {
				logger.info("Closing entity manager");
				em.close();
			}
		}
		logger.info("Loaded LazyCollections successfully");
		return object;
	}

	// This method is used to load notifications eligible collections recursively
	private static void loadCollections(List<NotificationEventFieldsT> fields, Object object, 
			NotificationsEventFieldsTRepository entityFieldsRepo) throws Exception {
		logger.debug("Inside loadLazyCollections() method");
		logger.info("Object Instance : {}", object.getClass().getName());

		if (fields != null & fields.size() > 0) {
			logger.info("Notifications eligible collections size: {}", fields.size());
			for (NotificationEventFieldsT field: fields) {
				logger.info("Collection Field Name: {}" , field.getFieldName());
				List<Object> collections = (List<Object>) PropertyUtils.getProperty(object, field.getFieldName());
				// do not remove as list.size() loads lazy collections 
				logger.info("Collection Field Size: {}", collections.size());
				// Load any inner collections eligible for notifications
				if (collections.size() > 0) {
					List<NotificationEventFieldsT> fieldsList =  
							entityFieldsRepo.findByParentFieldIdAndFieldTypeAndIsactive(field.getFieldId(), Constants.COLLECTION, Constants.Y);
					if (fieldsList != null && fieldsList.size() > 0) {
						// Load inner collections for all objects
						for (Object innerObj: collections) {
							logger.info("Object Instance : {}", innerObj.getClass().getName());
							loadCollections(fieldsList, innerObj, entityFieldsRepo);
						}
					}
				}
			}
		}
	}
}