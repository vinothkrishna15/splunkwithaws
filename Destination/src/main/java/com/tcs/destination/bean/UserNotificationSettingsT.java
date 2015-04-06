package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the user_notification_settings_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Entity
@Table(name="user_notification_settings_t")
@NamedQuery(name="UserNotificationSettingsT.findAll", query="SELECT u FROM UserNotificationSettingsT u")
public class UserNotificationSettingsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="user_id")
	private String userId;

	@Column(name="leadership_settings_group")
	private String leadershipSettingsGroup;

	@Column(name="notification_group_collaboration")
	private String notificationGroupCollaboration;

	@Column(name="notification_group_user_added")
	private String notificationGroupUserAdded;

	@Column(name="notification_group_user_reminders")
	private String notificationGroupUserReminders;

	@Column(name="notification_group_users_tagged")
	private String notificationGroupUsersTagged;

	@Column(name="supervisor_settings_collaboration_comments")
	private String supervisorSettingsCollaborationComments;

	@Column(name="supervisor_settings_group")
	private String supervisorSettingsGroup;

	@Column(name="supervisor_settings_subordinate_added")
	private String supervisorSettingsSubordinateAdded;

	@Column(name="supervisor_settings_subordinate_due_date")
	private String supervisorSettingsSubordinateDueDate;

	@Column(name="user_added_connect_owner")
	private String userAddedConnectOwner;

	@Column(name="user_added_opportunity_owner")
	private String userAddedOpportunityOwner;

	@Column(name="user_added_task_owner")
	private String userAddedTaskOwner;

	@Column(name="user_collaboration_new_updates")
	private String userCollaborationNewUpdates;

	@Column(name="user_collaboration_others_follow_me")
	private String userCollaborationOthersFollowMe;

	@Column(name="user_reminder_bid_task_due")
	private String userReminderBidTaskDue;

	@Column(name="user_reminder_bid_task_nearing")
	private String userReminderBidTaskNearing;

	@Column(name="user_reminder_connect_updates_missed")
	private String userReminderConnectUpdatesMissed;

	@Column(name="user_reminder_opportunity_updates_missed")
	private String userReminderOpportunityUpdatesMissed;

	@Column(name="user_tagged_follow_connect_and_opportunities")
	private String userTaggedFollowConnectAndOpportunities;

	@Column(name="user_tagged_follow_task")
	private String userTaggedFollowTask;

	@Column(name="user_tagged_others_follow")
	private String userTaggedOthersFollow;

	//bi-directional one-to-one association to UserT
	@OneToOne
	@JoinColumn(name="user_id")
	private UserT userT;

	public UserNotificationSettingsT() {
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLeadershipSettingsGroup() {
		return this.leadershipSettingsGroup;
	}

	public void setLeadershipSettingsGroup(String leadershipSettingsGroup) {
		this.leadershipSettingsGroup = leadershipSettingsGroup;
	}

	public String getNotificationGroupCollaboration() {
		return this.notificationGroupCollaboration;
	}

	public void setNotificationGroupCollaboration(String notificationGroupCollaboration) {
		this.notificationGroupCollaboration = notificationGroupCollaboration;
	}

	public String getNotificationGroupUserAdded() {
		return this.notificationGroupUserAdded;
	}

	public void setNotificationGroupUserAdded(String notificationGroupUserAdded) {
		this.notificationGroupUserAdded = notificationGroupUserAdded;
	}

	public String getNotificationGroupUserReminders() {
		return this.notificationGroupUserReminders;
	}

	public void setNotificationGroupUserReminders(String notificationGroupUserReminders) {
		this.notificationGroupUserReminders = notificationGroupUserReminders;
	}

	public String getNotificationGroupUsersTagged() {
		return this.notificationGroupUsersTagged;
	}

	public void setNotificationGroupUsersTagged(String notificationGroupUsersTagged) {
		this.notificationGroupUsersTagged = notificationGroupUsersTagged;
	}

	public String getSupervisorSettingsCollaborationComments() {
		return this.supervisorSettingsCollaborationComments;
	}

	public void setSupervisorSettingsCollaborationComments(String supervisorSettingsCollaborationComments) {
		this.supervisorSettingsCollaborationComments = supervisorSettingsCollaborationComments;
	}

	public String getSupervisorSettingsGroup() {
		return this.supervisorSettingsGroup;
	}

	public void setSupervisorSettingsGroup(String supervisorSettingsGroup) {
		this.supervisorSettingsGroup = supervisorSettingsGroup;
	}

	public String getSupervisorSettingsSubordinateAdded() {
		return this.supervisorSettingsSubordinateAdded;
	}

	public void setSupervisorSettingsSubordinateAdded(String supervisorSettingsSubordinateAdded) {
		this.supervisorSettingsSubordinateAdded = supervisorSettingsSubordinateAdded;
	}

	public String getSupervisorSettingsSubordinateDueDate() {
		return this.supervisorSettingsSubordinateDueDate;
	}

	public void setSupervisorSettingsSubordinateDueDate(String supervisorSettingsSubordinateDueDate) {
		this.supervisorSettingsSubordinateDueDate = supervisorSettingsSubordinateDueDate;
	}

	public String getUserAddedConnectOwner() {
		return this.userAddedConnectOwner;
	}

	public void setUserAddedConnectOwner(String userAddedConnectOwner) {
		this.userAddedConnectOwner = userAddedConnectOwner;
	}

	public String getUserAddedOpportunityOwner() {
		return this.userAddedOpportunityOwner;
	}

	public void setUserAddedOpportunityOwner(String userAddedOpportunityOwner) {
		this.userAddedOpportunityOwner = userAddedOpportunityOwner;
	}

	public String getUserAddedTaskOwner() {
		return this.userAddedTaskOwner;
	}

	public void setUserAddedTaskOwner(String userAddedTaskOwner) {
		this.userAddedTaskOwner = userAddedTaskOwner;
	}

	public String getUserCollaborationNewUpdates() {
		return this.userCollaborationNewUpdates;
	}

	public void setUserCollaborationNewUpdates(String userCollaborationNewUpdates) {
		this.userCollaborationNewUpdates = userCollaborationNewUpdates;
	}

	public String getUserCollaborationOthersFollowMe() {
		return this.userCollaborationOthersFollowMe;
	}

	public void setUserCollaborationOthersFollowMe(String userCollaborationOthersFollowMe) {
		this.userCollaborationOthersFollowMe = userCollaborationOthersFollowMe;
	}

	public String getUserReminderBidTaskDue() {
		return this.userReminderBidTaskDue;
	}

	public void setUserReminderBidTaskDue(String userReminderBidTaskDue) {
		this.userReminderBidTaskDue = userReminderBidTaskDue;
	}

	public String getUserReminderBidTaskNearing() {
		return this.userReminderBidTaskNearing;
	}

	public void setUserReminderBidTaskNearing(String userReminderBidTaskNearing) {
		this.userReminderBidTaskNearing = userReminderBidTaskNearing;
	}

	public String getUserReminderConnectUpdatesMissed() {
		return this.userReminderConnectUpdatesMissed;
	}

	public void setUserReminderConnectUpdatesMissed(String userReminderConnectUpdatesMissed) {
		this.userReminderConnectUpdatesMissed = userReminderConnectUpdatesMissed;
	}

	public String getUserReminderOpportunityUpdatesMissed() {
		return this.userReminderOpportunityUpdatesMissed;
	}

	public void setUserReminderOpportunityUpdatesMissed(String userReminderOpportunityUpdatesMissed) {
		this.userReminderOpportunityUpdatesMissed = userReminderOpportunityUpdatesMissed;
	}

	public String getUserTaggedFollowConnectAndOpportunities() {
		return this.userTaggedFollowConnectAndOpportunities;
	}

	public void setUserTaggedFollowConnectAndOpportunities(String userTaggedFollowConnectAndOpportunities) {
		this.userTaggedFollowConnectAndOpportunities = userTaggedFollowConnectAndOpportunities;
	}

	public String getUserTaggedFollowTask() {
		return this.userTaggedFollowTask;
	}

	public void setUserTaggedFollowTask(String userTaggedFollowTask) {
		this.userTaggedFollowTask = userTaggedFollowTask;
	}

	public String getUserTaggedOthersFollow() {
		return this.userTaggedOthersFollow;
	}

	public void setUserTaggedOthersFollow(String userTaggedOthersFollow) {
		this.userTaggedOthersFollow = userTaggedOthersFollow;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

}