package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the notification_type_event_mapping_t database table.
 * 
 */
@Entity
@Table(name="notification_type_event_mapping_t")
@NamedQuery(name="NotificationTypeEventMappingT.findAll", query="SELECT n FROM NotificationTypeEventMappingT n")
public class NotificationTypeEventMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="notification_type_event_mapping_id")
	private Integer notificationTypeEventMappingId;
	
	@Column(name="event_id")
	private Integer eventId;
	
	@Column(name="mode_id")
	private Integer modeId;
	
	@Column(name="notification_type")
	private String notificationType;

	//bi-directional many-to-one association to NotificationSettingsEventMappingT
	@ManyToOne
	@JoinColumn(name="event_id", insertable = false, updatable = false)
	private NotificationSettingsEventMappingT notificationSettingsEventMappingT;

	//bi-directional many-to-one association to NotificationSettingsModeMappingT
	@ManyToOne
	@JoinColumn(name="mode_id", insertable = false, updatable = false)
	private NotificationSettingsModeMappingT notificationSettingsModeMappingT;

	//bi-directional many-to-one association to UserNotificationsTypeT
	@ManyToOne
	@JoinColumn(name="notification_type", insertable = false, updatable = false)
	private UserNotificationsTypeT userNotificationsTypeT;

	//bi-directional many-to-one association to UserSubscription
	@OneToMany(mappedBy="notificationTypeEventMappingT")
	private List<UserSubscriptions> userSubscriptions;

	public NotificationTypeEventMappingT() {
	}

	public Integer getNotificationTypeEventMappingId() {
		return this.notificationTypeEventMappingId;
	}

	public void setNotificationTypeEventMappingId(Integer notificationTypeEventMappingId) {
		this.notificationTypeEventMappingId = notificationTypeEventMappingId;
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

	public UserNotificationsTypeT getUserNotificationsTypeT() {
		return this.userNotificationsTypeT;
	}

	public void setUserNotificationsTypeT(UserNotificationsTypeT userNotificationsTypeT) {
		this.userNotificationsTypeT = userNotificationsTypeT;
	}

	public List<UserSubscriptions> getUserSubscriptions() {
		return this.userSubscriptions;
	}

	public void setUserSubscriptions(List<UserSubscriptions> userSubscriptions) {
		this.userSubscriptions = userSubscriptions;
	}

//	public UserSubscriptions addUserSubscription(UserSubscriptions userSubscription) {
//		getUserSubscriptions().add(userSubscription);
//		userSubscription.setNotificationTypeEventMappingT(this);
//
//		return userSubscription;
//	}
//
//	public UserSubscriptions removeUserSubscription(UserSubscriptions userSubscription) {
//		getUserSubscriptions().remove(userSubscription);
//		userSubscription.setNotificationTypeEventMappingT(null);
//
//		return userSubscription;
//	}

	public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	public Integer getModeId() {
		return modeId;
	}

	public void setModeId(Integer modeId) {
		this.modeId = modeId;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}
	
	

}