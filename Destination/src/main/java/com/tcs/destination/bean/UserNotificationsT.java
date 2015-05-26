package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the user_notifications_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="userNotificationId")
@Entity
@Table(name="user_notifications_t")
@NamedQuery(name="UserNotificationsT.findAll", query="SELECT u FROM UserNotificationsT u")
public class UserNotificationsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_notification_id")
	private String userNotificationId;

	private String comments;

	@Column(name="entity_type")
	private String entityType;

	private String read;

	@Column(name="updated_datetime")
	private Timestamp updatedDatetime;

	//bi-directional many-to-one association to NotificationSentHistoryT
	@OneToMany(mappedBy="userNotificationsT")
	private List<NotificationSentHistoryT> notificationSentHistoryTs;

	//bi-directional many-to-one association to ConnectT
	@ManyToOne
	@JoinColumn(name="connect_id")
	private ConnectT connectT;

	//bi-directional many-to-one association to NotificationSettingsEventMappingT
	@ManyToOne
	@JoinColumn(name="event_id")
	private NotificationSettingsEventMappingT notificationSettingsEventMappingT;

	//bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name="opportunity_id")
	private OpportunityT opportunityT;

	//bi-directional many-to-one association to TaskT
	@ManyToOne
	@JoinColumn(name="task_id")
	private TaskT taskT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="recipient")
	private UserT userT1;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="user_id")
	private UserT userT2;

	public UserNotificationsT() {
	}

	public String getUserNotificationId() {
		return this.userNotificationId;
	}

	public void setUserNotificationId(String userNotificationId) {
		this.userNotificationId = userNotificationId;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getEntityType() {
		return this.entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getRead() {
		return this.read;
	}

	public void setRead(String read) {
		this.read = read;
	}

	public Timestamp getUpdatedDatetime() {
		return this.updatedDatetime;
	}

	public void setUpdatedDatetime(Timestamp updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
	}

	public List<NotificationSentHistoryT> getNotificationSentHistoryTs() {
		return this.notificationSentHistoryTs;
	}

	public void setNotificationSentHistoryTs(List<NotificationSentHistoryT> notificationSentHistoryTs) {
		this.notificationSentHistoryTs = notificationSentHistoryTs;
	}

	public NotificationSentHistoryT addNotificationSentHistoryT(NotificationSentHistoryT notificationSentHistoryT) {
		getNotificationSentHistoryTs().add(notificationSentHistoryT);
		notificationSentHistoryT.setUserNotificationsT(this);

		return notificationSentHistoryT;
	}

	public NotificationSentHistoryT removeNotificationSentHistoryT(NotificationSentHistoryT notificationSentHistoryT) {
		getNotificationSentHistoryTs().remove(notificationSentHistoryT);
		notificationSentHistoryT.setUserNotificationsT(null);

		return notificationSentHistoryT;
	}

	public ConnectT getConnectT() {
		return this.connectT;
	}

	public void setConnectT(ConnectT connectT) {
		this.connectT = connectT;
	}

	public NotificationSettingsEventMappingT getNotificationSettingsEventMappingT() {
		return this.notificationSettingsEventMappingT;
	}

	public void setNotificationSettingsEventMappingT(NotificationSettingsEventMappingT notificationSettingsEventMappingT) {
		this.notificationSettingsEventMappingT = notificationSettingsEventMappingT;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

	public TaskT getTaskT() {
		return this.taskT;
	}

	public void setTaskT(TaskT taskT) {
		this.taskT = taskT;
	}

	public UserT getUserT1() {
		return this.userT1;
	}

	public void setUserT1(UserT userT1) {
		this.userT1 = userT1;
	}

	public UserT getUserT2() {
		return this.userT2;
	}

	public void setUserT2(UserT userT2) {
		this.userT2 = userT2;
	}

}