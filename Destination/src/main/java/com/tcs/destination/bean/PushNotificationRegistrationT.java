package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the push_notification_registration_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Entity
@Table(name="push_notification_registration_t")
@NamedQuery(name="PushNotificationRegistrationT.findAll", query="SELECT p FROM PushNotificationRegistrationT p")
public class PushNotificationRegistrationT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="user_id")
	private String userId;

	@Column(name="device_platform")
	private String devicePlatform;

	@Column(name="device_type")
	private String deviceType;

	@Column(name="notification_register")
	private String notificationRegister;

	@Column(name="token_number")
	private String tokenNumber;

	@Column(name="updated_datetime")
	private Timestamp updatedDatetime;

	//bi-directional one-to-one association to UserT
	@OneToOne
	@JoinColumn(name="user_id")
	private UserT userT;

	public PushNotificationRegistrationT() {
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDevicePlatform() {
		return this.devicePlatform;
	}

	public void setDevicePlatform(String devicePlatform) {
		this.devicePlatform = devicePlatform;
	}

	public String getDeviceType() {
		return this.deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getNotificationRegister() {
		return this.notificationRegister;
	}

	public void setNotificationRegister(String notificationRegister) {
		this.notificationRegister = notificationRegister;
	}

	public String getTokenNumber() {
		return this.tokenNumber;
	}

	public void setTokenNumber(String tokenNumber) {
		this.tokenNumber = tokenNumber;
	}

	public Timestamp getUpdatedDatetime() {
		return this.updatedDatetime;
	}

	public void setUpdatedDatetime(Timestamp updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

}