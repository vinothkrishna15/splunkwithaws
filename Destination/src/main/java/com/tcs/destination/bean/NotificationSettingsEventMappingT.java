package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.util.List;


/**
 * The persistent class for the notification_settings_event_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="eventId")
@Entity
@Table(name="notification_settings_event_mapping_t")
@NamedQuery(name="NotificationSettingsEventMappingT.findAll", query="SELECT n FROM NotificationSettingsEventMappingT n")
public class NotificationSettingsEventMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="event_id")
	private Integer eventId;

	@Column(name="event_description")
	private String eventDescription;

	@Column(name="event_name")
	private String eventName;

	//bi-directional many-to-one association to NotificationEventGroupMappingT
	@OneToMany(mappedBy="notificationSettingsEventMappingT")
	private List<NotificationEventGroupMappingT> notificationEventGroupMappingTs;

	//bi-directional many-to-one association to NotificationSettingsEventConditionsMappingT
	@OneToMany(mappedBy="notificationSettingsEventMappingT")
	private List<NotificationSettingsEventConditionsMappingT> notificationSettingsEventConditionsMappingTs;

	//bi-directional many-to-one association to UserNotificationSettingsConditionsT
	@OneToMany(mappedBy="notificationSettingsEventMappingT")
	private List<UserNotificationSettingsConditionsT> userNotificationSettingsConditionsTs;

	//bi-directional many-to-one association to UserNotificationSettingsT
	@OneToMany(mappedBy="notificationSettingsEventMappingT")
	private List<UserNotificationSettingsT> userNotificationSettingsTs;

	//bi-directional many-to-one association to UserNotificationsT
	@OneToMany(mappedBy="notificationSettingsEventMappingT")
	private List<UserNotificationsT> userNotificationsTs;
	
	//bi-directional many-to-one association to OperationEventRecipientMappingT
	@OneToMany(mappedBy="notificationSettingsEventMappingT")
	private List<OperationEventRecipientMappingT> operationEventRecipientMappingTs;

	public NotificationSettingsEventMappingT() {
	}

	public Integer getEventId() {
		return this.eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	public String getEventDescription() {
		return this.eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	public String getEventName() {
		return this.eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public List<NotificationEventGroupMappingT> getNotificationEventGroupMappingTs() {
		return this.notificationEventGroupMappingTs;
	}

	public void setNotificationEventGroupMappingTs(List<NotificationEventGroupMappingT> notificationEventGroupMappingTs) {
		this.notificationEventGroupMappingTs = notificationEventGroupMappingTs;
	}

	public NotificationEventGroupMappingT addNotificationEventGroupMappingT(NotificationEventGroupMappingT notificationEventGroupMappingT) {
		getNotificationEventGroupMappingTs().add(notificationEventGroupMappingT);
		notificationEventGroupMappingT.setNotificationSettingsEventMappingT(this);

		return notificationEventGroupMappingT;
	}

	public NotificationEventGroupMappingT removeNotificationEventGroupMappingT(NotificationEventGroupMappingT notificationEventGroupMappingT) {
		getNotificationEventGroupMappingTs().remove(notificationEventGroupMappingT);
		notificationEventGroupMappingT.setNotificationSettingsEventMappingT(null);

		return notificationEventGroupMappingT;
	}

	public List<NotificationSettingsEventConditionsMappingT> getNotificationSettingsEventConditionsMappingTs() {
		return this.notificationSettingsEventConditionsMappingTs;
	}

	public void setNotificationSettingsEventConditionsMappingTs(List<NotificationSettingsEventConditionsMappingT> notificationSettingsEventConditionsMappingTs) {
		this.notificationSettingsEventConditionsMappingTs = notificationSettingsEventConditionsMappingTs;
	}

	public NotificationSettingsEventConditionsMappingT addNotificationSettingsEventConditionsMappingT(NotificationSettingsEventConditionsMappingT notificationSettingsEventConditionsMappingT) {
		getNotificationSettingsEventConditionsMappingTs().add(notificationSettingsEventConditionsMappingT);
		notificationSettingsEventConditionsMappingT.setNotificationSettingsEventMappingT(this);

		return notificationSettingsEventConditionsMappingT;
	}

	public NotificationSettingsEventConditionsMappingT removeNotificationSettingsEventConditionsMappingT(NotificationSettingsEventConditionsMappingT notificationSettingsEventConditionsMappingT) {
		getNotificationSettingsEventConditionsMappingTs().remove(notificationSettingsEventConditionsMappingT);
		notificationSettingsEventConditionsMappingT.setNotificationSettingsEventMappingT(null);

		return notificationSettingsEventConditionsMappingT;
	}

	public List<UserNotificationSettingsConditionsT> getUserNotificationSettingsConditionsTs() {
		return this.userNotificationSettingsConditionsTs;
	}

	public void setUserNotificationSettingsConditionsTs(List<UserNotificationSettingsConditionsT> userNotificationSettingsConditionsTs) {
		this.userNotificationSettingsConditionsTs = userNotificationSettingsConditionsTs;
	}

	public UserNotificationSettingsConditionsT addUserNotificationSettingsConditionsT(UserNotificationSettingsConditionsT userNotificationSettingsConditionsT) {
		getUserNotificationSettingsConditionsTs().add(userNotificationSettingsConditionsT);
		userNotificationSettingsConditionsT.setNotificationSettingsEventMappingT(this);

		return userNotificationSettingsConditionsT;
	}

	public UserNotificationSettingsConditionsT removeUserNotificationSettingsConditionsT(UserNotificationSettingsConditionsT userNotificationSettingsConditionsT) {
		getUserNotificationSettingsConditionsTs().remove(userNotificationSettingsConditionsT);
		userNotificationSettingsConditionsT.setNotificationSettingsEventMappingT(null);

		return userNotificationSettingsConditionsT;
	}

	public List<UserNotificationSettingsT> getUserNotificationSettingsTs() {
		return this.userNotificationSettingsTs;
	}

	public void setUserNotificationSettingsTs(List<UserNotificationSettingsT> userNotificationSettingsTs) {
		this.userNotificationSettingsTs = userNotificationSettingsTs;
	}

	public UserNotificationSettingsT addUserNotificationSettingsT(UserNotificationSettingsT userNotificationSettingsT) {
		getUserNotificationSettingsTs().add(userNotificationSettingsT);
		userNotificationSettingsT.setNotificationSettingsEventMappingT(this);

		return userNotificationSettingsT;
	}

	public UserNotificationSettingsT removeUserNotificationSettingsT(UserNotificationSettingsT userNotificationSettingsT) {
		getUserNotificationSettingsTs().remove(userNotificationSettingsT);
		userNotificationSettingsT.setNotificationSettingsEventMappingT(null);

		return userNotificationSettingsT;
	}

	public List<UserNotificationsT> getUserNotificationsTs() {
		return this.userNotificationsTs;
	}

	public void setUserNotificationsTs(List<UserNotificationsT> userNotificationsTs) {
		this.userNotificationsTs = userNotificationsTs;
	}

	public UserNotificationsT addUserNotificationsT(UserNotificationsT userNotificationsT) {
		getUserNotificationsTs().add(userNotificationsT);
		userNotificationsT.setNotificationSettingsEventMappingT(this);

		return userNotificationsT;
	}

	public UserNotificationsT removeUserNotificationsT(UserNotificationsT userNotificationsT) {
		getUserNotificationsTs().remove(userNotificationsT);
		userNotificationsT.setNotificationSettingsEventMappingT(null);

		return userNotificationsT;
	}

	public List<OperationEventRecipientMappingT> getOperationEventRecipientMappingTs() {
		return operationEventRecipientMappingTs;
	}

	public void setOperationEventRecipientMappingTs(
			List<OperationEventRecipientMappingT> operationEventRecipientMappingTs) {
		this.operationEventRecipientMappingTs = operationEventRecipientMappingTs;
	}
	
	

}