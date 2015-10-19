package com.tcs.destination.helper;

import java.util.ArrayList;
import java.util.List;

import com.tcs.destination.bean.UserGeneralSettingsT;
import com.tcs.destination.bean.UserNotificationSettingsT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.enums.EntityType;
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
	
	public static List<UserNotificationSettingsT> getUserNotificationSettingsList(UserT user){
		List<UserNotificationSettingsT> userNotificationSettingsList = new ArrayList<UserNotificationSettingsT>();
		
		userNotificationSettingsList.addAll(getUserNotificationGeneralSettingsList(user));
		userNotificationSettingsList.addAll(getUserReminderList(user));
		userNotificationSettingsList.addAll(getUserCollaborationSettingsList(user));
		userNotificationSettingsList.addAll(getUserNotificationSupervisorList(user));
		userNotificationSettingsList.addAll(getUserNotificationLeaderShipList(user));
		return userNotificationSettingsList;
	}

	private static List<UserNotificationSettingsT> getUserNotificationLeaderShipList(
			UserT user) {
		List<UserNotificationSettingsT> userNotificationLeaderShipList = new ArrayList<UserNotificationSettingsT>();
		switch (UserGroup.valueOf(UserGroup.getName(user.getUserGroup()))) {
		case BDM: 
		case BDM_SUPERVISOR: {
			userNotificationLeaderShipList.add(getNotification(user.getUserId(),16,1,Constants.N));
			userNotificationLeaderShipList.add(getNotification(user.getUserId(),16,2,Constants.N));
			userNotificationLeaderShipList.add(getNotification(user.getUserId(),16,3,Constants.N));
		    break;
		}
		case GEO_HEADS: 
		case IOU_HEADS:
		case STRATEGIC_INITIATIVES:
		case BID_OFFICE:	
		{
			userNotificationLeaderShipList.add(getNotification(user.getUserId(),16,1,Constants.Y));
			userNotificationLeaderShipList.add(getNotification(user.getUserId(),16,2,Constants.Y));
			userNotificationLeaderShipList.add(getNotification(user.getUserId(),16,3,Constants.Y));
		    break;
		}
		}
		return userNotificationLeaderShipList;
	}

	private static List<UserNotificationSettingsT> getUserNotificationSupervisorList(
			UserT user) {
		List<UserNotificationSettingsT> userNotificationSupervisorList = new ArrayList<UserNotificationSettingsT>();
		switch (UserGroup.valueOf(UserGroup.getName(user.getUserGroup()))) {
		case BDM: {
			userNotificationSupervisorList.add(getNotification(user.getUserId(),11,1,Constants.N));
			userNotificationSupervisorList.add(getNotification(user.getUserId(),11,2,Constants.N));
			userNotificationSupervisorList.add(getNotification(user.getUserId(),11,3,Constants.N));
			
			userNotificationSupervisorList.add(getNotification(user.getUserId(),12,1,Constants.N));
			userNotificationSupervisorList.add(getNotification(user.getUserId(),12,2,Constants.N));
			userNotificationSupervisorList.add(getNotification(user.getUserId(),12,3,Constants.N));
			
			userNotificationSupervisorList.add(getNotification(user.getUserId(),13,1,Constants.N));
			userNotificationSupervisorList.add(getNotification(user.getUserId(),13,2,Constants.N));
			userNotificationSupervisorList.add(getNotification(user.getUserId(),13,3,Constants.N));
			
			userNotificationSupervisorList.add(getNotification(user.getUserId(),14,1,Constants.N));
			userNotificationSupervisorList.add(getNotification(user.getUserId(),14,2,Constants.N));
			userNotificationSupervisorList.add(getNotification(user.getUserId(),14,3,Constants.N));
			
			userNotificationSupervisorList.add(getNotification(user.getUserId(),15,1,Constants.N));
			userNotificationSupervisorList.add(getNotification(user.getUserId(),15,2,Constants.N));
			userNotificationSupervisorList.add(getNotification(user.getUserId(),15,3,Constants.N));
			break;
		}
		case BDM_SUPERVISOR: 
		case GEO_HEADS: 
		case IOU_HEADS:
		case STRATEGIC_INITIATIVES:
		case BID_OFFICE:	
		{
		userNotificationSupervisorList.add(getNotification(user.getUserId(),11,1,Constants.Y));
		userNotificationSupervisorList.add(getNotification(user.getUserId(),11,2,Constants.Y));
		userNotificationSupervisorList.add(getNotification(user.getUserId(),11,3,Constants.Y));
		
		userNotificationSupervisorList.add(getNotification(user.getUserId(),12,1,Constants.Y));
		userNotificationSupervisorList.add(getNotification(user.getUserId(),12,2,Constants.Y));
		userNotificationSupervisorList.add(getNotification(user.getUserId(),12,3,Constants.Y));
		
		userNotificationSupervisorList.add(getNotification(user.getUserId(),13,1,Constants.Y));
		userNotificationSupervisorList.add(getNotification(user.getUserId(),13,2,Constants.Y));
		userNotificationSupervisorList.add(getNotification(user.getUserId(),13,3,Constants.N));
		
		userNotificationSupervisorList.add(getNotification(user.getUserId(),14,1,Constants.Y));
		userNotificationSupervisorList.add(getNotification(user.getUserId(),14,2,Constants.Y));
		userNotificationSupervisorList.add(getNotification(user.getUserId(),14,3,Constants.Y));
		
		userNotificationSupervisorList.add(getNotification(user.getUserId(),15,1,Constants.Y));
		userNotificationSupervisorList.add(getNotification(user.getUserId(),15,2,Constants.Y));
		userNotificationSupervisorList.add(getNotification(user.getUserId(),15,3,Constants.Y));
		}
		}
		return userNotificationSupervisorList;
	}

	private static List<UserNotificationSettingsT> getUserCollaborationSettingsList(
			UserT user) {
		List<UserNotificationSettingsT> userNotificationCollaborationList = new ArrayList<UserNotificationSettingsT>();
		
		userNotificationCollaborationList.add(getNotification(user.getUserId(),8,1,Constants.Y));
		userNotificationCollaborationList.add(getNotification(user.getUserId(),8,2,Constants.Y));
		userNotificationCollaborationList.add(getNotification(user.getUserId(),8,3,Constants.N));
		
		userNotificationCollaborationList.add(getNotification(user.getUserId(),9,1,Constants.Y));
		userNotificationCollaborationList.add(getNotification(user.getUserId(),9,2,Constants.Y));
		userNotificationCollaborationList.add(getNotification(user.getUserId(),9,3,Constants.N));
		
		switch (UserGroup.valueOf(UserGroup.getName(user.getUserGroup()))) {
		case BDM: 
		case BDM_SUPERVISOR: {
			userNotificationCollaborationList.add(getNotification(user.getUserId(),10,1,Constants.N));
			userNotificationCollaborationList.add(getNotification(user.getUserId(),10,2,Constants.N));
			userNotificationCollaborationList.add(getNotification(user.getUserId(),10,3,Constants.N));
		    break;
		}
		case GEO_HEADS: 
		case IOU_HEADS:
		case STRATEGIC_INITIATIVES:
		case BID_OFFICE:	
		{
			userNotificationCollaborationList.add(getNotification(user.getUserId(),10,1,Constants.Y));
			userNotificationCollaborationList.add(getNotification(user.getUserId(),10,2,Constants.Y));
			userNotificationCollaborationList.add(getNotification(user.getUserId(),10,3,Constants.N));
		    break;
		}
		}
		
		
		
		
		return userNotificationCollaborationList;
	}

	private static List<UserNotificationSettingsT> getUserReminderList(
			UserT user) {
		List<UserNotificationSettingsT> userNotificationReminderList = new ArrayList<UserNotificationSettingsT>();
		
		userNotificationReminderList.add(getNotification(user.getUserId(),5,1,Constants.Y));
		userNotificationReminderList.add(getNotification(user.getUserId(),5,2,Constants.Y));
		userNotificationReminderList.add(getNotification(user.getUserId(),5,3,Constants.Y));
		
		userNotificationReminderList.add(getNotification(user.getUserId(),6,1,Constants.Y));
		userNotificationReminderList.add(getNotification(user.getUserId(),6,2,Constants.Y));
		userNotificationReminderList.add(getNotification(user.getUserId(),6,3,Constants.Y));
		
		userNotificationReminderList.add(getNotification(user.getUserId(),7,1,Constants.Y));
		userNotificationReminderList.add(getNotification(user.getUserId(),7,2,Constants.Y));
		userNotificationReminderList.add(getNotification(user.getUserId(),7,3,Constants.Y));
		
		return userNotificationReminderList;
	}

	private static List<UserNotificationSettingsT> getUserNotificationGeneralSettingsList(UserT user) {
		List<UserNotificationSettingsT> userNotificationGeneralSettingsList = new ArrayList<UserNotificationSettingsT>();
		
		userNotificationGeneralSettingsList.add(getNotification(user.getUserId(),1,1,Constants.Y));
		userNotificationGeneralSettingsList.add(getNotification(user.getUserId(),1,2,Constants.Y));
		userNotificationGeneralSettingsList.add(getNotification(user.getUserId(),1,3,Constants.N));
		
		userNotificationGeneralSettingsList.add(getNotification(user.getUserId(),2,1,Constants.Y));
		userNotificationGeneralSettingsList.add(getNotification(user.getUserId(),2,2,Constants.Y));
		userNotificationGeneralSettingsList.add(getNotification(user.getUserId(),2,3,Constants.N));
		
		userNotificationGeneralSettingsList.add(getNotification(user.getUserId(),3,1,Constants.Y));
		userNotificationGeneralSettingsList.add(getNotification(user.getUserId(),3,2,Constants.Y));
		userNotificationGeneralSettingsList.add(getNotification(user.getUserId(),3,3,Constants.N));
		
		userNotificationGeneralSettingsList.add(getNotification(user.getUserId(),4,1,Constants.Y));
		userNotificationGeneralSettingsList.add(getNotification(user.getUserId(),4,2,Constants.Y));
		userNotificationGeneralSettingsList.add(getNotification(user.getUserId(),4,3,Constants.N));
		
		return userNotificationGeneralSettingsList;

	}
	
	private static UserNotificationSettingsT getNotification(String userId,int eventId,int modeId,String isActive){
		UserNotificationSettingsT notification = new UserNotificationSettingsT();
		notification.setUserId(userId);
		notification.setEventId(eventId);
		notification.setModeId(modeId);
		notification.setIsactive(isActive);
		return notification;
	}
	
	
	
}
