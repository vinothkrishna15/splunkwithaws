package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the user_subscriptions database table.
 * 
 */
@Entity
@Table(name="user_subscriptions")
@NamedQuery(name="UserSubscriptions.findAll", query="SELECT u FROM UserSubscriptions u")
public class UserSubscriptions implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_subscription_id")
	private Long userSubscriptionId;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	private String isactive;

	@Column(name="notification_type_event_mapping_id")
	private Integer notificationTypeEventMappingId;

	@Column(name="user_id")
	private String userId;

	// bi-directional many-to-one association to NotificationTypeEventMappingT
	@ManyToOne
	@JoinColumn(name = "notification_type_event_mapping_id", insertable = false, updatable = false)
	private NotificationTypeEventMappingT notificationTypeEventMappingT;
	
	@Transient
	List<UserNotificationSettingsConditionsT> userNotificationSettingsConditionsTs;
	
	public UserSubscriptions() {
	}

	public Long getUserSubscriptionId() {
		return this.userSubscriptionId;
	}

	public void setUserSubscriptionId(Long userSubscriptionId) {
		this.userSubscriptionId = userSubscriptionId;
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

	public Integer getNotificationTypeEventMappingId() {
		return this.notificationTypeEventMappingId;
	}

	public void setNotificationTypeEventMappingId(Integer notificationTypeEventMappingId) {
		this.notificationTypeEventMappingId = notificationTypeEventMappingId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setNotificationTypeEventMappingT(
			NotificationTypeEventMappingT notificationTypeEventMappingT) {
		this.notificationTypeEventMappingT =  notificationTypeEventMappingT;	
	}

	public List<UserNotificationSettingsConditionsT> getUserNotificationSettingsConditionsTs() {
		return userNotificationSettingsConditionsTs;
	}

	public void setUserNotificationSettingsConditionsTs(
			List<UserNotificationSettingsConditionsT> userNotificationSettingsConditionsTs) {
		this.userNotificationSettingsConditionsTs = userNotificationSettingsConditionsTs;
	}

	public NotificationTypeEventMappingT getNotificationTypeEventMappingT() {
		return notificationTypeEventMappingT;
	}
	
}