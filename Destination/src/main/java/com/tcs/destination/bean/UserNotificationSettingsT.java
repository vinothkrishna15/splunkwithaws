package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the user_notification_settings_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userNotificationSettingsId")
@Entity
@Table(name="user_notification_settings_t")
@NamedQuery(name="UserNotificationSettingsT.findAll", query="SELECT u FROM UserNotificationSettingsT u")
public class UserNotificationSettingsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_notification_settings_id")
	private String userNotificationSettingsId;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="isactive")
	private String isactive;

	//bi-directional many-to-one association to NotificationSettingsEventMappingT
	@ManyToOne
	@JoinColumn(name="event_id")
	private NotificationSettingsEventMappingT notificationSettingsEventMappingT;

	//bi-directional many-to-one association to NotificationSettingsModeMappingT
	@ManyToOne
	@JoinColumn(name="mode_id")
	private NotificationSettingsModeMappingT notificationSettingsModeMappingT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="user_id")
	private UserT userT;

	public UserNotificationSettingsT() {
	}

	public String getUserNotificationSettingsId() {
		return this.userNotificationSettingsId;
	}

	public void setUserNotificationSettingsId(String userNotificationSettingsId) {
		this.userNotificationSettingsId = userNotificationSettingsId;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return this.createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
	}

	public String getIsactive() {
		return this.isactive;
	}

	public void setIsactive(String isactive) {
		this.isactive = isactive;
	}

	public NotificationSettingsEventMappingT getNotificationSettingsEventMappingT() {
		return this.notificationSettingsEventMappingT;
	}

	public void setNotificationSettingsEventMappingT(NotificationSettingsEventMappingT notificationSettingsEventMappingT) {
		this.notificationSettingsEventMappingT = notificationSettingsEventMappingT;
	}

	public NotificationSettingsModeMappingT getNotificationSettingsModeMappingT() {
		return this.notificationSettingsModeMappingT;
	}

	public void setNotificationSettingsModeMappingT(NotificationSettingsModeMappingT notificationSettingsModeMappingT) {
		this.notificationSettingsModeMappingT = notificationSettingsModeMappingT;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

}