package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the user_notifications_type_t database table.
 * 
 */
@Entity
@Table(name="user_notifications_type_t")
@NamedQuery(name="UserNotificationsTypeT.findAll", query="SELECT u FROM UserNotificationsTypeT u")
public class UserNotificationsTypeT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="notification_type")
	private String notificationType;

	public UserNotificationsTypeT() {
	}

	public String getNotificationType() {
		return this.notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

}