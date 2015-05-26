package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the notification_settings_event_conditions_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="eventConditionsMappingId")
@Entity
@Table(name="notification_settings_event_conditions_mapping_t")
@NamedQuery(name="NotificationSettingsEventConditionsMappingT.findAll", query="SELECT n FROM NotificationSettingsEventConditionsMappingT n")
public class NotificationSettingsEventConditionsMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="event_conditions_mapping_id")
	private Integer eventConditionsMappingId;

	//bi-directional many-to-one association to NotificationSettingsEventConditionsT
	@ManyToOne
	@JoinColumn(name="condition_id")
	private NotificationSettingsEventConditionsT notificationSettingsEventConditionsT;

	//bi-directional many-to-one association to NotificationSettingsEventMappingT
	@ManyToOne
	@JoinColumn(name="event_id")
	private NotificationSettingsEventMappingT notificationSettingsEventMappingT;

	public NotificationSettingsEventConditionsMappingT() {
	}

	public Integer getEventConditionsMappingId() {
		return this.eventConditionsMappingId;
	}

	public void setEventConditionsMappingId(Integer eventConditionsMappingId) {
		this.eventConditionsMappingId = eventConditionsMappingId;
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

}