package com.tcs.destination.helper;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.AutoCommentsEntityFieldsT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.data.repository.AutoCommentsEntityFieldsTRepository;
import com.tcs.destination.data.repository.AutoCommentsEntityTRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.utils.Constants;

@Component
public class AutoCommentsLazyLoader {

	private static final Logger logger = LoggerFactory.getLogger(AutoCommentsLazyLoader.class);

	// This method is used to load object with auto comments eligible lazy collections
	public static Object loadLazyCollections(String entityId, String entityType, CrudRepository crudRepository, 
			AutoCommentsEntityTRepository entityRepo, AutoCommentsEntityFieldsTRepository entityFieldsRepo, EntityManagerFactory emf) throws Exception {
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
						logger.info("Loading auto comments eligible lazy collections for Connect: {}", entityId);
						// Load auto comments eligible Top level lazy collections
						List<AutoCommentsEntityFieldsT> fields = entityFieldsRepo.findByEntityTypeAndFieldType(entityType, Constants.COLLECTION);
						if (fields != null & fields.size() > 0) {
							loadCollections(fields, connect, entityFieldsRepo);
						} else {
							logger.info("No eligible collections for Auto comments, Connect: {}", entityId);
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
						logger.debug("Loading auto comments eligible lazy collections for Opportunity: {}", entityId);
						// Load auto comments eligible Top level lazy collections
						List<AutoCommentsEntityFieldsT> fields = entityFieldsRepo.findByEntityTypeAndFieldType(entityType, Constants.COLLECTION);
						if (fields != null & fields.size() > 0) {
							loadCollections(fields, opportunity, entityFieldsRepo);
						} else {
							logger.info("No eligible collections for Auto comments, Opportunity: {}", entityId);
						}
						return opportunity;
					} 
					break;
				}
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

	// This method is used to load auto comments eligible collections recursively
	private static void loadCollections(List<AutoCommentsEntityFieldsT> fields, Object object, 
			AutoCommentsEntityFieldsTRepository entityFieldsRepo) throws Exception {
		logger.debug("Inside loadLazyCollections() method");
		if (fields != null & fields.size() > 0) {
			logger.debug("Auto comments eligible collections size: {}", fields.size());
			for (AutoCommentsEntityFieldsT field: fields) {
				logger.debug("Collection Field Name: {}" , field.getName());
				List<Object> collections = (List<Object>) PropertyUtils.getProperty(object, field.getName());
				// do not remove as list.size() loads lazy collections 
				logger.debug("Collection Field Size: {}", collections.size());
				// Load any inner collections eligible for auto comments
				if (collections.size() > 0) {
					List<AutoCommentsEntityFieldsT> fieldsList =  
							entityFieldsRepo.findByEntityIdAndTypeAndIsactive(field.getLinkedEntityId(), Constants.COLLECTION, Constants.Y);
					if (fieldsList != null && fieldsList.size() > 0) {
						// Load inner collections for all objects
						for (Object innerObj: collections) {
							loadCollections(fieldsList, innerObj, entityFieldsRepo);
						}
					}
				}
			}
		}
	}
}