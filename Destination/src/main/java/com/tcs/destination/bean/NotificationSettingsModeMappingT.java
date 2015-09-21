package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.util.List;


/**
 * The persistent class for the notification_settings_mode_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="modeId")
@Entity
@Table(name="notification_settings_mode_mapping_t")
@NamedQuery(name="NotificationSettingsModeMappingT.findAll", query="SELECT n FROM NotificationSettingsModeMappingT n")
public class NotificationSettingsModeMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="mode_id")
	private Integer modeId;

	@Column(name="mode_description")
	private String modeDescription;

	@Column(name="notification_mode")
	private String notificationMode;

	//bi-directional many-to-one association to NotificationSentHistoryT
	@JsonIgnore
	@OneToMany(mappedBy="notificationSettingsModeMappingT")
	private List<NotificationSentHistoryT> notificationSentHistoryTs;

	//bi-directional many-to-one association to UserNotificationSettingsT
	@JsonIgnore
	@OneToMany(mappedBy="notificationSettingsModeMappingT")
	private List<UserNotificationSettingsT> userNotificationSettingsTs;

	public NotificationSettingsModeMappingT() {
	}

	public Integer getModeId() {
		return this.modeId;
	}

	public void setModeId(Integer modeId) {
		this.modeId = modeId;
	}

	public String getModeDescription() {
		return this.modeDescription;
	}

	public void setModeDescription(String modeDescription) {
		this.modeDescription = modeDescription;
	}

	public String getNotificationMode() {
		return this.notificationMode;
	}

	public void setNotificationMode(String notificationMode) {
		this.notificationMode = notificationMode;
	}

	public List<NotificationSentHistoryT> getNotificationSentHistoryTs() {
		return this.notificationSentHistoryTs;
	}

	public void setNotificationSentHistoryTs(List<NotificationSentHistoryT> notificationSentHistoryTs) {
		this.notificationSentHistoryTs = notificationSentHistoryTs;
	}

	public NotificationSentHistoryT addNotificationSentHistoryT(NotificationSentHistoryT notificationSentHistoryT) {
		getNotificationSentHistoryTs().add(notificationSentHistoryT);
		notificationSentHistoryT.setNotificationSettingsModeMappingT(this);

		return notificationSentHistoryT;
	}

	public NotificationSentHistoryT removeNotificationSentHistoryT(NotificationSentHistoryT notificationSentHistoryT) {
		getNotificationSentHistoryTs().remove(notificationSentHistoryT);
		notificationSentHistoryT.setNotificationSettingsModeMappingT(null);

		return notificationSentHistoryT;
	}

	public List<UserNotificationSettingsT> getUserNotificationSettingsTs() {
		return this.userNotificationSettingsTs;
	}

	public void setUserNotificationSettingsTs(List<UserNotificationSettingsT> userNotificationSettingsTs) {
		this.userNotificationSettingsTs = userNotificationSettingsTs;
	}

	public UserNotificationSettingsT addUserNotificationSettingsT(UserNotificationSettingsT userNotificationSettingsT) {
		getUserNotificationSettingsTs().add(userNotificationSettingsT);
		userNotificationSettingsT.setNotificationSettingsModeMappingT(this);

		return userNotificationSettingsT;
	}

	public UserNotificationSettingsT removeUserNotificationSettingsT(UserNotificationSettingsT userNotificationSettingsT) {
		getUserNotificationSettingsTs().remove(userNotificationSettingsT);
		userNotificationSettingsT.setNotificationSettingsModeMappingT(null);

		return userNotificationSettingsT;
	}

}