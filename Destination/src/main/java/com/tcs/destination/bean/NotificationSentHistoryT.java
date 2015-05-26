package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the notification_sent_history_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="notificationSentHistoryId")
@Entity
@Table(name="notification_sent_history_t")
@NamedQuery(name="NotificationSentHistoryT.findAll", query="SELECT n FROM NotificationSentHistoryT n")
public class NotificationSentHistoryT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="notification_sent_history_id")
	private String notificationSentHistoryId;

	@Column(name="notification_sent_datetime")
	private Timestamp notificationSentDatetime;

	//bi-directional many-to-one association to NotificationSettingsModeMappingT
	@ManyToOne
	@JoinColumn(name="mode_id")
	private NotificationSettingsModeMappingT notificationSettingsModeMappingT;

	//bi-directional many-to-one association to UserNotificationsT
	@ManyToOne
	@JoinColumn(name="user_notification_id")
	private UserNotificationsT userNotificationsT;

	public NotificationSentHistoryT() {
	}

	public String getNotificationSentHistoryId() {
		return this.notificationSentHistoryId;
	}

	public void setNotificationSentHistoryId(String notificationSentHistoryId) {
		this.notificationSentHistoryId = notificationSentHistoryId;
	}

	public Timestamp getNotificationSentDatetime() {
		return this.notificationSentDatetime;
	}

	public void setNotificationSentDatetime(Timestamp notificationSentDatetime) {
		this.notificationSentDatetime = notificationSentDatetime;
	}

	public NotificationSettingsModeMappingT getNotificationSettingsModeMappingT() {
		return this.notificationSettingsModeMappingT;
	}

	public void setNotificationSettingsModeMappingT(NotificationSettingsModeMappingT notificationSettingsModeMappingT) {
		this.notificationSettingsModeMappingT = notificationSettingsModeMappingT;
	}

	public UserNotificationsT getUserNotificationsT() {
		return this.userNotificationsT;
	}

	public void setUserNotificationsT(UserNotificationsT userNotificationsT) {
		this.userNotificationsT = userNotificationsT;
	}

}