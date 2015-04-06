package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the user_general_settings_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Entity
@Table(name="user_general_settings_t")
@NamedQuery(name="UserGeneralSettingsT.findAll", query="SELECT u FROM UserGeneralSettingsT u")
public class UserGeneralSettingsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="user_id")
	private String userId;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="email_digest")
	private String emailDigest;

	@Column(name="event_reminder")
	private String eventReminder;

	@Column(name="missed_update_reminder")
	private String missedUpdateReminder;

	private String theme;

	@Column(name="widget_order")
	private String widgetOrder;

	//bi-directional many-to-one association to TimeZoneMappingT
	@ManyToOne
	@JoinColumn(name="user_time_zone")
	private TimeZoneMappingT timeZoneMappingT;

	//bi-directional one-to-one association to UserT
	@OneToOne
	@JoinColumn(name="user_id")
	private UserT userT;

	public UserGeneralSettingsT() {
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return this.createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
	}

	public String getEmailDigest() {
		return this.emailDigest;
	}

	public void setEmailDigest(String emailDigest) {
		this.emailDigest = emailDigest;
	}

	public String getEventReminder() {
		return this.eventReminder;
	}

	public void setEventReminder(String eventReminder) {
		this.eventReminder = eventReminder;
	}

	public String getMissedUpdateReminder() {
		return this.missedUpdateReminder;
	}

	public void setMissedUpdateReminder(String missedUpdateReminder) {
		this.missedUpdateReminder = missedUpdateReminder;
	}

	public String getTheme() {
		return this.theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getWidgetOrder() {
		return this.widgetOrder;
	}

	public void setWidgetOrder(String widgetOrder) {
		this.widgetOrder = widgetOrder;
	}

	public TimeZoneMappingT getTimeZoneMappingT() {
		return this.timeZoneMappingT;
	}

	public void setTimeZoneMappingT(TimeZoneMappingT timeZoneMappingT) {
		this.timeZoneMappingT = timeZoneMappingT;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

}