package com.tcs.destination.reader;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.tcs.destination.bean.OperationEventRecipientMappingT;
import com.tcs.destination.bean.Recipient;
import com.tcs.destination.enums.OperationType;
import com.tcs.destination.helper.NotificationBatchHelper;

/**
 * Reader to Get the recipient details for notification
 * @author TCS
 * 
 */
public class RecipientReader implements ItemReader<List<Recipient>>{

	private static final Logger logger = LoggerFactory
			.getLogger(RecipientReader.class);
	
	private String entityId;
	
	private String entityType;
	
	private String operationType;
	
	private NotificationBatchHelper notificationBatchHelper;
	
	private String currentUser;
	
	private int readCount = 0;
	
	
	@Override
	public List<Recipient> read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		
		List<Recipient> filteredRecipients = null;
		if(readCount==0) {
			logger.info("Inside Read Method");
			OperationType operationTypeE = OperationType.getByValue(operationType);
			
			List<OperationEventRecipientMappingT> eventRecipientMapping = notificationBatchHelper.pullEventsByOperation(operationTypeE);
			//Getting recipients
			List<Recipient> recipients = notificationBatchHelper.getRecipients(entityId,operationTypeE,eventRecipientMapping,currentUser);
			logger.info("Recipients retrieved");
			//Remove Modified user from the recipients
			filteredRecipients = notificationBatchHelper.removeModifiedByUserFromRecipients(currentUser, recipients);
			logger.info("Removed Modified user from the recipients");
			readCount++;
		}
		
		
		return filteredRecipients;
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

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public NotificationBatchHelper getNotificationBatchHelper() {
		return notificationBatchHelper;
	}

	public void setNotificationBatchHelper(
			NotificationBatchHelper notificationBatchHelper) {
		this.notificationBatchHelper = notificationBatchHelper;
	}

	public String getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(String currentUser) {
		this.currentUser = currentUser;
	}

	
}
