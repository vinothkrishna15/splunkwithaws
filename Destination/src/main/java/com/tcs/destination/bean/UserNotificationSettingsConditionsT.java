package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the user_notification_settings_conditions_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "notificationSettingsConditionsId")
@Entity
@Table(name="user_notification_settings_conditions_t")
@NamedQuery(name="UserNotificationSettingsConditionsT.findAll", query="SELECT u FROM UserNotificationSettingsConditionsT u")
public class UserNotificationSettingsConditionsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="notification_settings_conditions_id")
	private String notificationSettingsConditionsId;

	@Column(name="condition_value")
	private String conditionValue;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="condition_id")
	private Integer conditionId;

	//bi-directional many-to-one association to NotificationSettingsEventConditionsT
	@ManyToOne
	@JoinColumn(name="condition_id", insertable=false, updatable=false)
	private NotificationSettingsEventConditionsT notificationSettingsEventConditionsT;

	@Column(name="event_id")
	private Integer eventId;

	//bi-directional many-to-one association to NotificationSettingsEventMappingT
	@ManyToOne
	@JoinColumn(name="event_id", insertable=false, updatable=false)
	private NotificationSettingsEventMappingT notificationSettingsEventMappingT;

	@Column(name="user_id")
	private String userId;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="user_id", insertable=false, updatable=false)
	private UserT userT;

	public UserNotificationSettingsConditionsT() {
	}

	public Integer getConditionId() {
		return conditionId;
	}

	public void setConditionId(Integer conditionId) {
		this.conditionId = conditionId;
	}

	public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNotificationSettingsConditionsId() {
		return this.notificationSettingsConditionsId;
	}

	public void setNotificationSettingsConditionsId(String notificationSettingsConditionsId) {
		this.notificationSettingsConditionsId = notificationSettingsConditionsId;
	}

	public String getConditionValue() {
		return this.conditionValue;
	}

	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public NotificationSettingsEventConditionsT getNotificationSettingsEventConditionsT() {
		return this.notificationSettingsEventConditionsT;
	}

	public void setNotificationSettingsEventConditionsT(NotificationSettingsEventConditionsT notificationSettingsEventConditionsT) {
		this.notificationSettingsEventConditionsT = notificationSettingsEventConditionsT;
	}

	public NotificationSettingsEventMappingT getNotificationSettingsEventMappingT() {
		return this.notificationSettingsEventMappingT;
	}

	public void setNotificationSettingsEventMappingT(NotificationSettingsEventMappingT notificationSettingsEventMappingT) {
		this.notificationSettingsEventMappingT = notificationSettingsEventMappingT;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

}