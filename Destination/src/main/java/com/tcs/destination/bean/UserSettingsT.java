package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the user_settings_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
@Entity
@Table(name="user_settings_t")
@NamedQuery(name="UserSettingsT.findAll", query="SELECT u FROM UserSettingsT u")
public class UserSettingsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="user_id")
	private String userId;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="email_digest")
	private String emailDigest;

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

	public UserSettingsT() {
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