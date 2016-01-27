/**
 * 
 * ConnectReminderProcessor.java 
 *
 * @author TCS
 * @Version 1.0 - 2015
 * 
 * @Copyright 2015 Tata Consultancy 
 */
package com.tcs.destination.processor;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.StringUtils;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.UserNotificationsT;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.helper.NotificationProcessHelper;
import com.tcs.destination.utils.DateUtils;

/**
 * This ConnectReminderProcessor class holds the functionality for sending user notifications
 * 
 */
public class ConnectReminderProcessor implements ItemProcessor<Object[], UserNotificationsT> {
	
	private static final Logger logger = LoggerFactory.getLogger(ConnectReminderProcessor.class);
	
	private NotificationProcessHelper notificationProcessHelper;
	
	private ConnectRepository connectRepository;

	private UserRepository userRepository;
	
	private String dateType;
	
	private EntityType entityType;
	
	private int eventId;
	
	@Override
	public UserNotificationsT process(Object[] items) throws Exception {
		
		logger.debug("Inside process method:");
		
		String date = null;
		String recipientId= null;
		String recipientName= null;
		String entityId = null;
		String entityName = null;
		String entityReference = null;
		String referenceValue = null;
		String primaryOwner = null;
		String secondaryOwners = null;
		int i = 0;
		
		for (Object item: items) {
			
			
			switch (i) {
				case 0:
					entityId = (String)item;
					break;
				case 1:
					entityName = (String)item;
					break;
				case 2:
					recipientId = (String)item;
					break;
				case 3:
					recipientName = (String)item;
					break;
				case 4:
					date = DateUtils.convertDtToStringForUser((Date) item);
					break;
				case 5:
					entityReference = (String)item;
					break;
				case 6:
					referenceValue = (String)item;
					break;
				default: break;
			}
			i++;
				
		}
		
		ConnectT connect = connectRepository.findByConnectId(entityId);
		if (connect != null) {
			entityReference = connect.getConnectCategory();
			if (entityReference.equals(EntityType.CUSTOMER.name())) {
				referenceValue = connect.getCustomerMasterT()
						.getCustomerName();
			} else {
				referenceValue = connect.getPartnerMasterT()
						.getPartnerName();
			}
			primaryOwner = userRepository.findUserNameByUserId(connect
					.getPrimaryOwner());
			List<String> secOwners = connectRepository
					.getSecondaryOwnerByConnectId(entityId);
			List<String> secOwnerNames = userRepository
					.findUserNamesByUserIds(secOwners);
			if (CollectionUtils.isNotEmpty(secOwnerNames)) {
				secondaryOwners = StringUtils
						.collectionToCommaDelimitedString(secOwnerNames);
			}
		} else {
			logger.error("Connect not found for the Id: {}", entityId);
		}

		
		return notificationProcessHelper.processNotification(entityType, entityId, entityName, eventId, dateType, date, recipientId, recipientName, null, entityReference, referenceValue,
				primaryOwner, secondaryOwners);
		
	}

	public NotificationProcessHelper getNotificationProcessHelper() {
		return notificationProcessHelper;
	}

	public void setNotificationProcessHelper(
			NotificationProcessHelper notificationProcessHelper) {
		this.notificationProcessHelper = notificationProcessHelper;
	}

	public String getDateType() {
		return dateType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public ConnectRepository getConnectRepository() {
		return connectRepository;
	}

	public void setConnectRepository(ConnectRepository connectRepository) {
		this.connectRepository = connectRepository;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	

}
