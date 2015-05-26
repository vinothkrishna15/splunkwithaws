package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.util.List;


/**
 * The persistent class for the notification_settings_group_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="groupId")
@Entity
@Table(name="notification_settings_group_mapping_t")
@NamedQuery(name="NotificationSettingsGroupMappingT.findAll", query="SELECT n FROM NotificationSettingsGroupMappingT n")
public class NotificationSettingsGroupMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="group_id")
	private Integer groupId;

	@Column(name="event_group")
	private String eventGroup;

	@Column(name="group_description")
	private String groupDescription;

	//bi-directional many-to-one association to NotificationEventGroupMappingT
	@OneToMany(mappedBy="notificationSettingsGroupMappingT")
	private List<NotificationEventGroupMappingT> notificationEventGroupMappingTs;

	public NotificationSettingsGroupMappingT() {
	}

	public Integer getGroupId() {
		return this.groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getEventGroup() {
		return this.eventGroup;
	}

	public void setEventGroup(String eventGroup) {
		this.eventGroup = eventGroup;
	}

	public String getGroupDescription() {
		return this.groupDescription;
	}

	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}

	public List<NotificationEventGroupMappingT> getNotificationEventGroupMappingTs() {
		return this.notificationEventGroupMappingTs;
	}

	public void setNotificationEventGroupMappingTs(List<NotificationEventGroupMappingT> notificationEventGroupMappingTs) {
		this.notificationEventGroupMappingTs = notificationEventGroupMappingTs;
	}

	public NotificationEventGroupMappingT addNotificationEventGroupMappingT(NotificationEventGroupMappingT notificationEventGroupMappingT) {
		getNotificationEventGroupMappingTs().add(notificationEventGroupMappingT);
		notificationEventGroupMappingT.setNotificationSettingsGroupMappingT(this);

		return notificationEventGroupMappingT;
	}

	public NotificationEventGroupMappingT removeNotificationEventGroupMappingT(NotificationEventGroupMappingT notificationEventGroupMappingT) {
		getNotificationEventGroupMappingTs().remove(notificationEventGroupMappingT);
		notificationEventGroupMappingT.setNotificationSettingsGroupMappingT(null);

		return notificationEventGroupMappingT;
	}

}