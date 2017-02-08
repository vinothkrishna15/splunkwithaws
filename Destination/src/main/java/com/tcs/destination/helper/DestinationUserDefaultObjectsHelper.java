package com.tcs.destination.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tcs.destination.bean.NotificationTypeEventMappingT;
import com.tcs.destination.bean.UserGeneralSettingsT;
import com.tcs.destination.bean.UserSubscriptions;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.utils.Constants;

public class DestinationUserDefaultObjectsHelper {

	public static UserGeneralSettingsT getDefaultSettings(String userId, String timeZoneDesc){
		UserGeneralSettingsT userGenSettings = new UserGeneralSettingsT();
		userGenSettings.setUserId(userId);
		userGenSettings.setTimeZoneDesc(timeZoneDesc);
		userGenSettings.setWidgetOrder("1,2,3,4");
		userGenSettings.setPushSubscribed("N");
		userGenSettings.setEmailDigest("7");
		userGenSettings.setEventReminder("2");
		userGenSettings.setMissedUpdateReminder("1");
		userGenSettings.setTheme("Default");
		return userGenSettings;
	}
	
	public static List<UserSubscriptions> getUserNotificationSettingsList(UserT user, Map<String, NotificationTypeEventMappingT> notifyTypeEventMap) {
		List<UserSubscriptions> userNotificationSettingsList = new ArrayList<UserSubscriptions>();
		
		String userId = user.getUserId();
		UserGroup userGroup = UserGroup.getUserGroup(user.getUserGroup());
		userNotificationSettingsList.addAll(getUserNotificationGeneralSettingsList(userId, userGroup, notifyTypeEventMap));
		userNotificationSettingsList.addAll(getUserReminderList(userId, userGroup, notifyTypeEventMap));
		userNotificationSettingsList.addAll(getUserCollaborationSettingsList(userId, userGroup, notifyTypeEventMap));
		userNotificationSettingsList.addAll(getUserNotificationSupervisorList(userId, userGroup, notifyTypeEventMap));
		userNotificationSettingsList.addAll(getUserNotificationLeaderShipList(userId, userGroup, notifyTypeEventMap));
		
		return userNotificationSettingsList;
	}

	private static List<UserSubscriptions> getUserNotificationLeaderShipList(
			String userId, UserGroup userGroup, Map<String, NotificationTypeEventMappingT> notifyTypeEventMap) {
		List<UserSubscriptions> userNotificationLeaderShipList = new ArrayList<UserSubscriptions>();
		switch (userGroup) {
		case BDM: 
		case DELIVERY_MANAGER:	
		case BDM_SUPERVISOR:
		case DELIVERY_CLUSTER_HEAD:
		case DELIVERY_CENTRE_HEAD:		
		case CONSULTING_USER:
		case CONSULTING_HEAD:
		case PMO:	
		case PMO_DELIVERY:	
			userNotificationLeaderShipList.add(getNotification(userId,16,1,Constants.N, notifyTypeEventMap));
			userNotificationLeaderShipList.add(getNotification(userId,16,2,Constants.N, notifyTypeEventMap));
			userNotificationLeaderShipList.add(getNotification(userId,16,3,Constants.N, notifyTypeEventMap));
		    break;
		case GEO_HEADS: 
		case IOU_HEADS:
		case STRATEGIC_INITIATIVES:
		case BID_OFFICE:	
			userNotificationLeaderShipList.add(getNotification(userId,16,1,Constants.Y, notifyTypeEventMap));
			userNotificationLeaderShipList.add(getNotification(userId,16,2,Constants.Y, notifyTypeEventMap));
			userNotificationLeaderShipList.add(getNotification(userId,16,3,Constants.Y, notifyTypeEventMap));
		    break;
		case REPORTING_TEAM:
			userNotificationLeaderShipList.add(getNotification(userId,16,1,Constants.N, notifyTypeEventMap));
			userNotificationLeaderShipList.add(getNotification(userId,16,2,Constants.N, notifyTypeEventMap));
			userNotificationLeaderShipList.add(getNotification(userId,16,3,Constants.N, notifyTypeEventMap));
		    break;
		default:
			break;
		}
		return userNotificationLeaderShipList;
	}

	private static List<UserSubscriptions> getUserNotificationSupervisorList(
			String userId, UserGroup userGroup, Map<String, NotificationTypeEventMappingT> notifyTypeEventMap) {
		List<UserSubscriptions> userNotificationSupervisorList = new ArrayList<UserSubscriptions>();
		switch (userGroup) {
		case BDM:
		case DELIVERY_MANAGER:{
			userNotificationSupervisorList.add(getNotification(userId,11,1,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,11,2,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,11,3,Constants.N, notifyTypeEventMap));
			
			userNotificationSupervisorList.add(getNotification(userId,12,1,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,12,2,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,12,3,Constants.N, notifyTypeEventMap));
			
			userNotificationSupervisorList.add(getNotification(userId,13,1,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,13,2,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,13,3,Constants.N, notifyTypeEventMap));
			
			userNotificationSupervisorList.add(getNotification(userId,14,1,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,14,2,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,14,3,Constants.N, notifyTypeEventMap));
			
			userNotificationSupervisorList.add(getNotification(userId,15,1,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,15,2,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,15,3,Constants.N, notifyTypeEventMap));
			break;
		}
		case BDM_SUPERVISOR: 
		case DELIVERY_CLUSTER_HEAD:
		case DELIVERY_CENTRE_HEAD:		
		case GEO_HEADS: 
		case CONSULTING_USER:
		case CONSULTING_HEAD:
		case IOU_HEADS:
		case STRATEGIC_INITIATIVES:
		case BID_OFFICE:
		case PMO:	
		case PMO_DELIVERY:	
		{
		userNotificationSupervisorList.add(getNotification(userId,11,1,Constants.Y, notifyTypeEventMap));
		userNotificationSupervisorList.add(getNotification(userId,11,2,Constants.Y, notifyTypeEventMap));
		userNotificationSupervisorList.add(getNotification(userId,11,3,Constants.Y, notifyTypeEventMap));
		
		userNotificationSupervisorList.add(getNotification(userId,12,1,Constants.Y, notifyTypeEventMap));
		userNotificationSupervisorList.add(getNotification(userId,12,2,Constants.Y, notifyTypeEventMap));
		userNotificationSupervisorList.add(getNotification(userId,12,3,Constants.Y, notifyTypeEventMap));
		
		userNotificationSupervisorList.add(getNotification(userId,13,1,Constants.Y, notifyTypeEventMap));
		userNotificationSupervisorList.add(getNotification(userId,13,2,Constants.Y, notifyTypeEventMap));
		userNotificationSupervisorList.add(getNotification(userId,13,3,Constants.N, notifyTypeEventMap));
		
		userNotificationSupervisorList.add(getNotification(userId,14,1,Constants.Y, notifyTypeEventMap));
		userNotificationSupervisorList.add(getNotification(userId,14,2,Constants.Y, notifyTypeEventMap));
		userNotificationSupervisorList.add(getNotification(userId,14,3,Constants.Y, notifyTypeEventMap));
		
		userNotificationSupervisorList.add(getNotification(userId,15,1,Constants.Y, notifyTypeEventMap));
		userNotificationSupervisorList.add(getNotification(userId,15,2,Constants.Y, notifyTypeEventMap));
		userNotificationSupervisorList.add(getNotification(userId,15,3,Constants.Y, notifyTypeEventMap));
		break;
		}
		case REPORTING_TEAM:
		{
			userNotificationSupervisorList.add(getNotification(userId,11,1,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,11,2,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,11,3,Constants.N, notifyTypeEventMap));
			
			userNotificationSupervisorList.add(getNotification(userId,12,1,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,12,2,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,12,3,Constants.N, notifyTypeEventMap));
			
			userNotificationSupervisorList.add(getNotification(userId,13,1,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,13,2,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,13,3,Constants.N, notifyTypeEventMap));
			
			userNotificationSupervisorList.add(getNotification(userId,14,1,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,14,2,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,14,3,Constants.N, notifyTypeEventMap));
			
			userNotificationSupervisorList.add(getNotification(userId,15,1,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,15,2,Constants.N, notifyTypeEventMap));
			userNotificationSupervisorList.add(getNotification(userId,15,3,Constants.N, notifyTypeEventMap));
			break;
		
		}
		default:
			break;
		}
		return userNotificationSupervisorList;
	}

	private static List<UserSubscriptions> getUserCollaborationSettingsList(
			String userId, UserGroup userGroup, Map<String, NotificationTypeEventMappingT> notifyTypeEventMap) {
		List<UserSubscriptions> userNotificationCollaborationList = new ArrayList<UserSubscriptions>();
		

		
		switch (userGroup) {
		case BDM: 
		case DELIVERY_MANAGER:	
		case BDM_SUPERVISOR:
		case DELIVERY_CLUSTER_HEAD:
		case DELIVERY_CENTRE_HEAD:	
		case CONSULTING_USER:
		case CONSULTING_HEAD:
		case PMO:	
		case PMO_DELIVERY:	
			userNotificationCollaborationList.add(getNotification(userId,8,1,Constants.Y, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,8,2,Constants.Y, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,8,3,Constants.N, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,9,1,Constants.Y, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,9,2,Constants.Y, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,9,3,Constants.N, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,10,1,Constants.N, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,10,2,Constants.N, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,10,3,Constants.N, notifyTypeEventMap));
		    break;
		case GEO_HEADS: 
		case IOU_HEADS:
		case STRATEGIC_INITIATIVES:
		case BID_OFFICE:	
			userNotificationCollaborationList.add(getNotification(userId,8,1,Constants.Y, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,8,2,Constants.Y, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,8,3,Constants.N, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,9,1,Constants.Y, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,9,2,Constants.Y, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,9,3,Constants.N, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,10,1,Constants.Y, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,10,2,Constants.Y, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,10,3,Constants.N, notifyTypeEventMap));
		    break;
		case REPORTING_TEAM:
			userNotificationCollaborationList.add(getNotification(userId,8,1,Constants.N, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,8,2,Constants.N, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,8,3,Constants.N, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,9,1,Constants.N, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,9,2,Constants.N, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,9,3,Constants.N, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,10,1,Constants.N, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,10,2,Constants.N, notifyTypeEventMap));
			userNotificationCollaborationList.add(getNotification(userId,10,3,Constants.N, notifyTypeEventMap));
			break;
		default:
			break;
		}
		return userNotificationCollaborationList;
	}

	private static List<UserSubscriptions> getUserReminderList(
			String userId, UserGroup userGroup, Map<String, NotificationTypeEventMappingT> notifyTypeEventMap) {
		List<UserSubscriptions> userNotificationReminderList = new ArrayList<UserSubscriptions>();
		switch (userGroup) {
		case BDM:
		case DELIVERY_MANAGER:	
		case BDM_SUPERVISOR:
		case DELIVERY_CLUSTER_HEAD:
		case DELIVERY_CENTRE_HEAD:	
		case CONSULTING_USER:
		case CONSULTING_HEAD:
		case GEO_HEADS: 
		case IOU_HEADS:
		case STRATEGIC_INITIATIVES:
		case BID_OFFICE:
		case PMO:
		case PMO_DELIVERY:	
			userNotificationReminderList.add(getNotification(userId,5,1,Constants.Y, notifyTypeEventMap));
			userNotificationReminderList.add(getNotification(userId,5,2,Constants.Y, notifyTypeEventMap));
			userNotificationReminderList.add(getNotification(userId,5,3,Constants.Y, notifyTypeEventMap));

			userNotificationReminderList.add(getNotification(userId,6,1,Constants.Y, notifyTypeEventMap));
			userNotificationReminderList.add(getNotification(userId,6,2,Constants.Y, notifyTypeEventMap));
			userNotificationReminderList.add(getNotification(userId,6,3,Constants.Y, notifyTypeEventMap));

			userNotificationReminderList.add(getNotification(userId,7,1,Constants.Y, notifyTypeEventMap));
			userNotificationReminderList.add(getNotification(userId,7,2,Constants.Y, notifyTypeEventMap));
			userNotificationReminderList.add(getNotification(userId,7,3,Constants.Y, notifyTypeEventMap));
			break;
		case REPORTING_TEAM:
			userNotificationReminderList.add(getNotification(userId,5,1,Constants.N, notifyTypeEventMap));
			userNotificationReminderList.add(getNotification(userId,5,2,Constants.N, notifyTypeEventMap));
			userNotificationReminderList.add(getNotification(userId,5,3,Constants.N, notifyTypeEventMap));

			userNotificationReminderList.add(getNotification(userId,6,1,Constants.N, notifyTypeEventMap));
			userNotificationReminderList.add(getNotification(userId,6,2,Constants.N, notifyTypeEventMap));
			userNotificationReminderList.add(getNotification(userId,6,3,Constants.N, notifyTypeEventMap));

			userNotificationReminderList.add(getNotification(userId,7,1,Constants.N, notifyTypeEventMap));
			userNotificationReminderList.add(getNotification(userId,7,2,Constants.N, notifyTypeEventMap));
			userNotificationReminderList.add(getNotification(userId,7,3,Constants.N, notifyTypeEventMap));
			break;
		default:
			break;
		}
		return userNotificationReminderList;
	}

	private static List<UserSubscriptions> getUserNotificationGeneralSettingsList(String userId, UserGroup userGroup, Map<String, NotificationTypeEventMappingT> notifyTypeEventMap) {
		List<UserSubscriptions> userNotificationGeneralSettingsList = new ArrayList<UserSubscriptions>();
		
		switch (userGroup) {
		case BDM:
		case DELIVERY_MANAGER:	
		case BDM_SUPERVISOR:
		case DELIVERY_CLUSTER_HEAD:
		case DELIVERY_CENTRE_HEAD:
		case CONSULTING_USER:
		case CONSULTING_HEAD:
		case GEO_HEADS: 
		case IOU_HEADS:
		case STRATEGIC_INITIATIVES:
		case BID_OFFICE:
		case PMO:
		case PMO_DELIVERY:	
		{
		userNotificationGeneralSettingsList.add(getNotification(userId,1,1,Constants.Y, notifyTypeEventMap));
		userNotificationGeneralSettingsList.add(getNotification(userId,1,2,Constants.Y, notifyTypeEventMap));
		userNotificationGeneralSettingsList.add(getNotification(userId,1,3,Constants.N, notifyTypeEventMap));
		
		userNotificationGeneralSettingsList.add(getNotification(userId,2,1,Constants.Y, notifyTypeEventMap));
		userNotificationGeneralSettingsList.add(getNotification(userId,2,2,Constants.Y, notifyTypeEventMap));
		userNotificationGeneralSettingsList.add(getNotification(userId,2,3,Constants.N, notifyTypeEventMap));
		
		userNotificationGeneralSettingsList.add(getNotification(userId,3,1,Constants.Y, notifyTypeEventMap));
		userNotificationGeneralSettingsList.add(getNotification(userId,3,2,Constants.Y, notifyTypeEventMap));
		userNotificationGeneralSettingsList.add(getNotification(userId,3,3,Constants.N, notifyTypeEventMap));
		
		userNotificationGeneralSettingsList.add(getNotification(userId,4,1,Constants.Y, notifyTypeEventMap));
		userNotificationGeneralSettingsList.add(getNotification(userId,4,2,Constants.Y, notifyTypeEventMap));
		userNotificationGeneralSettingsList.add(getNotification(userId,4,3,Constants.N, notifyTypeEventMap));
		break;
		}
		case REPORTING_TEAM:
		{
			userNotificationGeneralSettingsList.add(getNotification(userId,1,1,Constants.N, notifyTypeEventMap));
			userNotificationGeneralSettingsList.add(getNotification(userId,1,2,Constants.N, notifyTypeEventMap));
			userNotificationGeneralSettingsList.add(getNotification(userId,1,3,Constants.N, notifyTypeEventMap));
			
			userNotificationGeneralSettingsList.add(getNotification(userId,2,1,Constants.N, notifyTypeEventMap));
			userNotificationGeneralSettingsList.add(getNotification(userId,2,2,Constants.N, notifyTypeEventMap));
			userNotificationGeneralSettingsList.add(getNotification(userId,2,3,Constants.N, notifyTypeEventMap));
			
			userNotificationGeneralSettingsList.add(getNotification(userId,3,1,Constants.N, notifyTypeEventMap));
			userNotificationGeneralSettingsList.add(getNotification(userId,3,2,Constants.N, notifyTypeEventMap));
			userNotificationGeneralSettingsList.add(getNotification(userId,3,3,Constants.N, notifyTypeEventMap));
			
			userNotificationGeneralSettingsList.add(getNotification(userId,4,1,Constants.N, notifyTypeEventMap));
			userNotificationGeneralSettingsList.add(getNotification(userId,4,2,Constants.N, notifyTypeEventMap));
			userNotificationGeneralSettingsList.add(getNotification(userId,4,3,Constants.N, notifyTypeEventMap));
			break;
		}
		default:
			break;
		}
		return userNotificationGeneralSettingsList;

	}
	
	private static UserSubscriptions getNotification(String userId,int eventId,int modeId,String isActive, Map<String, NotificationTypeEventMappingT> notifyTypeEventMap){
		UserSubscriptions notification = new UserSubscriptions();
		
		notification.setUserId(userId);
		notification.setIsactive(isActive);
		NotificationTypeEventMappingT notifyTypeEvent = notifyTypeEventMap.get(String.format("%d%d", eventId,modeId));
		notification.setNotificationTypeEventMappingId(notifyTypeEvent.getNotificationTypeEventMappingId());
		
		return notification;
	}
	
	
	
}
