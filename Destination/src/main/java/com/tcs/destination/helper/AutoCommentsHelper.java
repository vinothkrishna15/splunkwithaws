package com.tcs.destination.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.AutoCommentsEntityFieldsT;
import com.tcs.destination.bean.AutoCommentsEntityT;
import com.tcs.destination.bean.CollaborationCommentT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.data.repository.AutoCommentsEntityFieldsTRepository;
import com.tcs.destination.data.repository.AutoCommentsEntityTRepository;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.enums.CommentType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.utils.Constants;

import org.springframework.data.repository.CrudRepository;

@Component
public class AutoCommentsHelper implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(AutoCommentsHelper.class);
	
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
	private AutoCommentsEntityTRepository autoCommentsEntityTRepository;
	private AutoCommentsEntityFieldsTRepository autoCommentsEntityFieldsTRepository;
	private CollaborationCommentsRepository collaborationCommentsRepository;

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
	public AutoCommentsEntityTRepository getAutoCommentsEntityTRepository() {
		return autoCommentsEntityTRepository;
	}
	public void setAutoCommentsEntityTRepository(
			AutoCommentsEntityTRepository autoCommentsEntityTRepository) {
		this.autoCommentsEntityTRepository = autoCommentsEntityTRepository;
	}
	public AutoCommentsEntityFieldsTRepository getAutoCommentsEntityFieldsTRepository() {
		return autoCommentsEntityFieldsTRepository;
	}
	public void setAutoCommentsEntityFieldsTRepository(
			AutoCommentsEntityFieldsTRepository autoCommentsEntityFieldsTRepository) {
		this.autoCommentsEntityFieldsTRepository = autoCommentsEntityFieldsTRepository;
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
		logger.debug("Inside processAutoComments() method");
		logger.info("Processing Auto comments events for entity: {}", entityId);
		AutoCommentsEntityT autoCommentsEntity = null;
		
		try {
			if (entityType != null) {
				// Get the auto comments eligible fields
				autoCommentsEntity = getEntity();
				if (autoCommentsEntity != null) {
					// Process new adds	
					if (oldObject == null) {
						processAdds(autoCommentsEntity);
					} else {
						processUpdates(autoCommentsEntity);
					}
				}
			}
		} catch (Exception e) {
			logger.error("[Auto Comment]: Error occurred while processing auto comments " + e.getMessage());
			e.printStackTrace();
		}
		logger.info("Finished processing Auto comments events for entity: {}", entityId);
	}
	
	// This method is used to get AutoCommentsEntityT details for the given EntityType
	private AutoCommentsEntityT getEntity() throws Exception {
		logger.debug("Inside getEntity() method");
		return(autoCommentsEntityTRepository.findByNameIgnoreCaseAndIsactive(entityType, Constants.Y));
	}
	
	// This method is used to process auto comments for new entity Adds
	private void processAdds(AutoCommentsEntityT autoCommentsEntity) throws Exception {
		logger.debug("Inside processAdds() method");

		String user = null;
		String entityName = null;
		String parentEntityName = null;
		String msgTemplate = null;
		
		if (EntityType.contains(entityType)) {
			switch (EntityType.valueOf(entityType)) {
			case TASK: {
				logger.debug("Processing Auto comments for Add, TaskId: {}", entityId);
				TaskT task = ((TaskRepository) crudRepository).findOne(entityId);
				if (task != null) {
					user = task.getModifiedByUser().getUserName();
					entityName = task.getTaskDescription();
					if (task.getConnectT() != null) {
						parentEntityName = task.getConnectT().getConnectName();
					} else if (task.getOpportunityT() != null) {
						parentEntityName = task.getOpportunityT().getOpportunityName();
					} else {
						logger.error("Invalid Task Parent Entity");
						throw new Exception("Invalid Task Parent Entity");
					}
					msgTemplate = replaceTokens(autoCommentsEntity.getAddMessageTemplate(), 
							populateTokens(user, entityName, parentEntityName, null, null));
				} else {
					logger.error("Invalid Task Id: {}", entityId);
					throw new Exception("Invalid Task Id: " + entityId);
				}
				break;
			}
			case CONNECT: {
				logger.debug("Processing Auto comments for Add, ConnectId: {}", entityId);
				ConnectT connect = ((ConnectRepository) crudRepository).findOne(entityId);
				if (connect != null) {
					user = connect.getModifiedByUser().getUserName();
					entityName = connect.getConnectName();
					if (connect.getCustomerMasterT() != null) {
						parentEntityName = connect.getCustomerMasterT().getCustomerName();
					} else if (connect.getPartnerMasterT() != null) {
						parentEntityName = connect.getPartnerMasterT().getPartnerName();
					} else {
						logger.error("Invalid Connect Parent Entity");
						throw new Exception("Invalid Connect Parent Entity");
					}
					msgTemplate = replaceTokens(autoCommentsEntity.getAddMessageTemplate(), 
							populateTokens(user, entityName, parentEntityName, null, null));
				} else {
					logger.error("Invalid Connect Id: {}", entityId);
					throw new Exception("Invalid Connect Id: " + entityId);
				}
				break;
			}
			case OPPORTUNITY: {
				logger.debug("Processing Auto comments for Add, OpportunityId: {}", entityId);
				OpportunityT opportunity = ((OpportunityRepository) crudRepository).findOne(entityId);
				if (opportunity != null) {
					user = opportunity.getModifiedByUser().getUserName();
					entityName = opportunity.getOpportunityName();
					if (opportunity.getCustomerMasterT() != null) {
						parentEntityName = opportunity.getCustomerMasterT().getCustomerName();
					} else {
						logger.error("Invalid Opportunity Parent Entity");
						throw new Exception("Invalid Opportunity Parent Entity");
					}
					msgTemplate = replaceTokens(autoCommentsEntity.getAddMessageTemplate(), 
							populateTokens(user, entityName, parentEntityName, null, null));
				} else {
					logger.error("Invalid Opportunity Id: {}", entityId);
					throw new Exception("Invalid Opportunity Id: " + entityId);
				}
				break;
			}
			default:
				logger.error("Invalid Entity Type: " + entityType);
				throw new Exception("Invalid Entity Type: " + entityType);
			}
			//Add collaboration comments
			if (msgTemplate != null) {
				addCollaborationComments(msgTemplate);
			}
		} else {
			logger.error("Invalid Entity Type: " + entityType);
			throw new Exception("Invalid Entity Type: " + entityType);
		}
	}
	
	// This method is used to process auto comments for entity updates
	private void processUpdates(AutoCommentsEntityT autoCommentsEntity) throws Exception {
		logger.debug("Inside processUpdates() method");

		if (EntityType.contains(entityType)) {
			switch (EntityType.valueOf(entityType)) {
			case TASK: {
				processTaskUpdate(autoCommentsEntity);
				break;
			}
			case CONNECT: {
				processConnectUpdate(autoCommentsEntity);
				break;
			}
			case OPPORTUNITY: {
				processOpportunityUpdate(autoCommentsEntity);
				break;
			}
			default:
				logger.error("Invalid Entity Type: " + entityType);
				throw new Exception("Invalid Entity Type: " + entityType);
			}
		}
	}

	// This method is used to process auto comments events for Task
	private void processTaskUpdate(AutoCommentsEntityT autoCommentsEntity) throws Exception {
		logger.debug("Processing Auto comments for Update, TaskId: {}", entityId);
		String user = null;
		String entityName = null;
		String parentEntityName = null;

		// Get the fields eligible for Auto comments
		List<AutoCommentsEntityFieldsT> fields = autoCommentsEntityFieldsTRepository.
				findByEntityIdAndIsactiveOrderByTypeAsc(autoCommentsEntity.getEntityId(), Constants.Y);
		if (fields != null && !fields.isEmpty()) {
			TaskT task = ((TaskRepository) crudRepository).findOne(entityId);
			if (task != null) {
				user = task.getModifiedByUser().getUserName();
				entityName = task.getTaskDescription();
				// Iterate auto comments eligible fields and add auto comments
				for (AutoCommentsEntityFieldsT field: fields) {
					if (field.getType().equalsIgnoreCase(Constants.FIELD)) {
						processEntityFieldUpdate(user, entityName, parentEntityName, field, task);
					} else {
						// To-Do for Child Objects
					}
				}
			} else {
				logger.error("Invalid Task Id: {}", entityId);
				throw new Exception("Invalid Task Id: " + entityId);
			}
		} else {
			logger.info("No eligible fields for Auto comments, Task :{}", entityId);
		}
		logger.debug("Finished processing Auto comments for Update, TaskId: {}", entityId);
	}
	
	// This method is used to process auto comments events for Connect
	private void processConnectUpdate(AutoCommentsEntityT autoCommentsEntity) throws Exception {
		logger.debug("Processing Auto comments for Update, ConnectId: {}", entityId);
		String user = null;
		String entityName = null;
		String parentEntityName = null;

		// Get the fields eligible for Auto comments
		List<AutoCommentsEntityFieldsT> fields = autoCommentsEntityFieldsTRepository.
				findByEntityIdAndIsactiveOrderByTypeAsc(autoCommentsEntity.getEntityId(), Constants.Y);
		if (fields != null && !fields.isEmpty()) {
			ConnectT connect = ((ConnectRepository) crudRepository).findOne(entityId);
			if (connect != null) {
				user = connect.getModifiedByUser().getUserName();
				entityName = connect.getConnectName();
				// Iterate auto comments eligible fields and add auto comments
				for (AutoCommentsEntityFieldsT field: fields) {
					if (field.getType().equalsIgnoreCase(Constants.FIELD)) {
						processEntityFieldUpdate(user, entityName, parentEntityName, field, connect);
					} else {
						// To-Do for Child Objects
					}
				}
			} else {
				logger.error("Invalid Connect Id: {}", entityId);
				throw new Exception("Invalid Connect Id: " + entityId);
			}
		} else {
			logger.info("No eligible fields for Auto comments, Connect :{}", entityId);
		}
		logger.debug("Finished processing Auto comments for Update, ConnectId: {}", entityId);
	}

	// This method is used to process auto comments events for Opportunity
	private void processOpportunityUpdate(AutoCommentsEntityT autoCommentsEntity) throws Exception {
		logger.debug("Processing Auto comments for Update, OpportunityId: {}", entityId);
		String user = null;
		String entityName = null;
		String parentEntityName = null;

		// Get the fields eligible for Auto comments
		List<AutoCommentsEntityFieldsT> fields = autoCommentsEntityFieldsTRepository.
				findByEntityIdAndIsactiveOrderByTypeAsc(autoCommentsEntity.getEntityId(), Constants.Y);
		if (fields != null && !fields.isEmpty()) {
			OpportunityT opportunity = ((OpportunityRepository) crudRepository).findOne(entityId);
			if (opportunity != null) {
				user = opportunity.getModifiedByUser().getUserName();
				entityName = opportunity.getOpportunityName();
				// Iterate auto comments eligible fields and add auto comments
				for (AutoCommentsEntityFieldsT field: fields) {
					if (field.getType().equalsIgnoreCase(Constants.FIELD)) {
						processEntityFieldUpdate(user, entityName, parentEntityName, field, opportunity);
					} else {
						// To-Do for Child Objects
					}
				}
			} else {
				logger.error("Invalid Connect Id: {}", entityId);
				throw new Exception("Invalid Connect Id: " + entityId);
			}
		} else {
			logger.info("No eligible fields for Auto comments, Connect :{}", entityId);
		}
		logger.debug("Finished processing Auto comments for Update, ConnectId: {}", entityId);
	}

	// This method is used to add auto comments for a particular entity field update
	private void processEntityFieldUpdate(String user, String entityName, String parentEntityName, AutoCommentsEntityFieldsT field, Object newObject)
			throws Exception {
		logger.debug("Inside processEntityFieldUpdate() method");;
		Object fromValue = null;
		Object toValue = null;
		String msgTemplate = null;

		if (field != null) {
			logger.info("Field: {}", field.getName());
			fromValue = PropertyUtils.getProperty(oldObject, field.getName());
			toValue = PropertyUtils.getProperty(newObject, field.getName());
			logger.info("fromValue: {}", fromValue);
			logger.info("toValue: {}", toValue);
			// Field value updated in update
			if (fromValue != null) {
				if (toValue != null && !fromValue.equals(toValue)) { 
					msgTemplate = replaceTokens(field.getUpdateMessageTemplate(), 
						populateTokens(user, entityName, parentEntityName, fromValue.toString(), toValue.toString()));
				}
			} else {
				// Field value add newly in update
				if (toValue != null) {
					msgTemplate = replaceTokens(field.getAddMessageTemplate(), 
						populateTokens(user, entityName, parentEntityName, null, toValue.toString()));
				}
			}
			//Add collaboration comments
			if (msgTemplate != null) {
				addCollaborationComments(msgTemplate);
			}
		}
	}
	
	// This method is used to populate the replacement tokens in the auto comments message template
	private HashMap<String, String> populateTokens(String user, String entityName, String parentEntityName, String from, String to) 
			throws Exception {
		logger.debug("Inside populateTokens() method");
		HashMap<String, String> tokensMap = new HashMap<String, String>();
		if (user != null)
			tokensMap.put(TOKEN_USER, user);
		if (entityName != null)
			tokensMap.put(TOKEN_ENTITY_NAME, entityName);
		if (parentEntityName != null)
			tokensMap.put(TOKEN_PARENT_ENTITY, parentEntityName);
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
	
	// This method is used to add collaboration comments for Auto comments
	private void addCollaborationComments(String comments) throws Exception {
		logger.debug("Inside addCollaborationComments() method");
		
		if ((entityId != null) && (entityType != null) & (comments != null)) {
			CollaborationCommentT comment = new CollaborationCommentT();
			comment.setComments(comments);
			comment.setCommentType(CommentType.AUTO.name());
			comment.setDocumentsAttached(Constants.NO);
			comment.setEntityId(entityId);
			comment.setEntityType(entityType);
			if (EntityType.CONNECT.equalsName(entityType))
				comment.setConnectId(entityId);
			if (EntityType.TASK.equalsName(entityType))
				comment.setTaskId(entityId);
			if (EntityType.OPPORTUNITY.equalsName(entityType))
				comment.setOpportunityId(entityId);
			comment.setUserId(Constants.SYSTEM_USER);
			
			try {
				comment = collaborationCommentsRepository.save(comment);
				logger.info("entityType: {}, entityId: {}", entityType, entityId);
				logger.info("Auto comment added successfully, commentId: {}", comment.getCommentId());
			} catch (Exception e) {
				logger.error("Error occurred while saving Auto comments: " + e.getMessage());
				throw new Exception("Error occurred while saving Auto comments");
			}
		
		}
	}
}