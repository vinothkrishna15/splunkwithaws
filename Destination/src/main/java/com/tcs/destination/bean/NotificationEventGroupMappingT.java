package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the notification_event_group_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="eventGroupMappingId")
@Entity
@Table(name="notification_event_group_mapping_t")
@NamedQuery(name="NotificationEventGroupMappingT.findAll", query="SELECT n FROM NotificationEventGroupMappingT n")
public class NotificationEventGroupMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="event_group_mapping_id")
	private Integer eventGroupMappingId;

	@Column(name="message_template")
	private String messageTemplate;

	//bi-directional many-to-one association to NotificationSettingsEventMappingT
	@ManyToOne
	@JoinColumn(name="event_id")
	private NotificationSettingsEventMappingT notificationSettingsEventMappingT;

	//bi-directional many-to-one association to NotificationSettingsGroupMappingT
	@ManyToOne
	@JoinColumn(name="group_id")
	private NotificationSettingsGroupMappingT notificationSettingsGroupMappingT;

	public NotificationEventGroupMappingT() {
	}

	public Integer getEventGroupMappingId() {
		return this.eventGroupMappingId;
	}

	public void setEventGroupMappingId(Integer eventGroupMappingId) {
		this.eventGroupMappingId = eventGroupMappingId;
	}

	public String getMessageTemplate() {
		return this.messageTemplate;
	}

	public void setMessageTemplate(String messageTemplate) {
		this.messageTemplate = messageTemplate;
	}

	public NotificationSettingsEventMappingT getNotificationSettingsEventMappingT() {
		return this.notificationSettingsEventMappingT;
	}

	public void setNotificationSettingsEventMappingT(NotificationSettingsEventMappingT notificationSettingsEventMappingT) {
		this.notificationSettingsEventMappingT = notificationSettingsEventMappingT;
	}

	public NotificationSettingsGroupMappingT getNotificationSettingsGroupMappingT() {
		return this.notificationSettingsGroupMappingT;
	}

	public void setNotificationSettingsGroupMappingT(NotificationSettingsGroupMappingT notificationSettingsGroupMappingT) {
		this.notificationSettingsGroupMappingT = notificationSettingsGroupMappingT;
	}

}