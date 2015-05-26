package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.util.List;


/**
 * The persistent class for the notification_settings_event_conditions_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="eventConditionsId")
@Entity
@Table(name="notification_settings_event_conditions_t")
@NamedQuery(name="NotificationSettingsEventConditionsT.findAll", query="SELECT n FROM NotificationSettingsEventConditionsT n")
public class NotificationSettingsEventConditionsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="event_conditions_id")
	private Integer eventConditionsId;

	@Column(name="condition_name")
	private String conditionName;

	private String description;

	//bi-directional many-to-one association to NotificationSettingsEventConditionsMappingT
	@OneToMany(mappedBy="notificationSettingsEventConditionsT")
	private List<NotificationSettingsEventConditionsMappingT> notificationSettingsEventConditionsMappingTs;

	//bi-directional many-to-one association to UserNotificationSettingsConditionsT
	@OneToMany(mappedBy="notificationSettingsEventConditionsT")
	private List<UserNotificationSettingsConditionsT> userNotificationSettingsConditionsTs;

	public NotificationSettingsEventConditionsT() {
	}

	public Integer getEventConditionsId() {
		return this.eventConditionsId;
	}

	public void setEventConditionsId(Integer eventConditionsId) {
		this.eventConditionsId = eventConditionsId;
	}

	public String getConditionName() {
		return this.conditionName;
	}

	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<NotificationSettingsEventConditionsMappingT> getNotificationSettingsEventConditionsMappingTs() {
		return this.notificationSettingsEventConditionsMappingTs;
	}

	public void setNotificationSettingsEventConditionsMappingTs(List<NotificationSettingsEventConditionsMappingT> notificationSettingsEventConditionsMappingTs) {
		this.notificationSettingsEventConditionsMappingTs = notificationSettingsEventConditionsMappingTs;
	}

	public NotificationSettingsEventConditionsMappingT addNotificationSettingsEventConditionsMappingT(NotificationSettingsEventConditionsMappingT notificationSettingsEventConditionsMappingT) {
		getNotificationSettingsEventConditionsMappingTs().add(notificationSettingsEventConditionsMappingT);
		notificationSettingsEventConditionsMappingT.setNotificationSettingsEventConditionsT(this);

		return notificationSettingsEventConditionsMappingT;
	}

	public NotificationSettingsEventConditionsMappingT removeNotificationSettingsEventConditionsMappingT(NotificationSettingsEventConditionsMappingT notificationSettingsEventConditionsMappingT) {
		getNotificationSettingsEventConditionsMappingTs().remove(notificationSettingsEventConditionsMappingT);
		notificationSettingsEventConditionsMappingT.setNotificationSettingsEventConditionsT(null);

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
		userNotificationSettingsConditionsT.setNotificationSettingsEventConditionsT(this);

		return userNotificationSettingsConditionsT;
	}

	public UserNotificationSettingsConditionsT removeUserNotificationSettingsConditionsT(UserNotificationSettingsConditionsT userNotificationSettingsConditionsT) {
		getUserNotificationSettingsConditionsTs().remove(userNotificationSettingsConditionsT);
		userNotificationSettingsConditionsT.setNotificationSettingsEventConditionsT(null);

		return userNotificationSettingsConditionsT;
	}

}