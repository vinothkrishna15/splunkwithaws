/**
 * 
 * ReminderProcessor.java 
 *
 * @author TCS
 * @Version 1.0 - 2015
 * 
 * @Copyright 2015 Tata Consultancy 
 */
package com.tcs.destination.processor;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.tcs.destination.bean.UserNotificationsT;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.helper.NotificationProcessHelper;
import com.tcs.destination.utils.DateUtils;

/**
 * This ReminderProcessor class holds the functionality for sending user notifications
 * 
 */
public class ReminderProcessor implements ItemProcessor<Object[], UserNotificationsT> {
	
	private static final Logger logger = LoggerFactory.getLogger(ReminderProcessor.class);
	
	private NotificationProcessHelper notificationProcessHelper;
	
	private String dateType;
	
	private EntityType entityType;
	
	private int eventId;
	
	@Override
	public UserNotificationsT process(Object[] items) throws Exception {
		
		logger.debug("Inside process method:");
		
		String date = null;
		String subordinateId = null;
		String subordinateName = null;
		String recipientId= null;
		String recipientName= null;
		String entityId = null;
		String entityName = null;
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
					subordinateId = (String)item;
					break;
				case 6:
					subordinateName = (String)item;
					break;
				default: break;
			}
			i++;
				
		}
		
		return notificationProcessHelper.processNotification(entityType, entityId, entityName, eventId, dateType, date, recipientId, recipientName, subordinateId, subordinateName);
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
	

}
