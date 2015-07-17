package com.tcs.destination.helper;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.AutoCommentsEntityFieldsT;
import com.tcs.destination.bean.AutoCommentsEntityT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.OpportunityT;
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
			AutoCommentsEntityTRepository autoCommentsEntityRepo, EntityManagerFactory emf) throws Exception {
		logger.debug("Inside loadLazyCollections() method");
		logger.info("EntityType: {}. EntityId: {}" , entityType, entityId);

		Object object = null;
		EntityManager em = null;
		
		try {
			// EntityManagerFactory will be passed from async thread
			if (emf != null) {
				logger.info("Entity Manager Factory is not null, creating Entity Manager");
				em = emf.createEntityManager();
			}
			
			if (EntityType.contains(entityType)) {
				// Handling different Entity types
				switch (EntityType.valueOf(entityType)) {
				case CONNECT: {
					ConnectT connect = null;	
					if (em != null) {
						logger.info("Loading Connect from database using entity manager");
						connect = em.find(ConnectT.class, entityId);
						em.refresh(connect);
					} else {
						logger.info("Loading Connect from database using JPA Repository");
						connect = ((ConnectRepository) crudRepository).findOne(entityId);
					}
					if (connect != null) {
						logger.info("Loading auto comments eligible lazy collections for Connect: {}", entityId);
						List<AutoCommentsEntityFieldsT> fields = getAutoCommentsEntityCollections(entityType, autoCommentsEntityRepo);
						// Load auto comments eligible lazy collections
						if (fields != null & fields.size() > 0) {
							logger.debug("Collections size: {}", fields.size());
							for (AutoCommentsEntityFieldsT field: fields) {
								logger.info("Field Name: {}" , field.getName());
								List<Object> list = (List<Object>) PropertyUtils.getProperty(connect, field.getName());
								// do not remove as list.size() loads lazy collections 
								logger.info("Field Size: {}", list.size());
							}
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
						logger.info("Loading Opportunity from database using entity manager");
						opportunity = em.find(OpportunityT.class, entityId);
						em.refresh(opportunity);
					} else {
						logger.info("Loading Opportunity from database using JPA Repository");
						opportunity = ((OpportunityRepository) crudRepository).findOne(entityId);
					}
					if (opportunity != null) {
						logger.info("Loading auto comments eligible lazy collections for Opportunity: {}", entityId);
						List<AutoCommentsEntityFieldsT> fields = getAutoCommentsEntityCollections(entityType, autoCommentsEntityRepo);
						// Load auto comments eligible lazy collections
						if (fields != null & fields.size() > 0) {
							logger.debug("Collections size: {}", fields.size());
							for (AutoCommentsEntityFieldsT field: fields) {
								logger.info("Field Name: {}" , field.getName());
								List<Object> list = (List<Object>) PropertyUtils.getProperty(opportunity, field.getName());
								// do not remove as list.size() loads lazy collections 
								logger.info("Field Size: {}", list.size());
							}
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

	private static List<AutoCommentsEntityFieldsT> getAutoCommentsEntityCollections(String entityType, 
			AutoCommentsEntityTRepository autoCommentsEntityRepo) throws Exception {
		logger.debug("Inside getAutoCommentsEntityCollections() method");
		// Load AutoCommentsEntityT by entityType
		List<AutoCommentsEntityFieldsT> fields = null;
		AutoCommentsEntityT entity = autoCommentsEntityRepo.findByNameIgnoreCaseAndIsactive(entityType, Constants.Y);
		if (entity != null) {
			fields = new ArrayList<AutoCommentsEntityFieldsT>();
			// Add only collections
			for (AutoCommentsEntityFieldsT field: entity.getEntityFields()) {
				if ((field.getType().equalsIgnoreCase(Constants.COLLECTION))
					&& (field.getIsactive().equalsIgnoreCase(Constants.Y))) {
					fields.add(field);
				}
			}
		}
		return fields;
	}

}