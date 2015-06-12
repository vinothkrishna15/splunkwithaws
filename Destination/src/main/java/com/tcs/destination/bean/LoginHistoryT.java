package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the login_history_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Entity
@Table(name="login_history_t")
@NamedQuery(name="LoginHistoryT.findAll", query="SELECT l FROM LoginHistoryT l")
public class LoginHistoryT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="login_id")
	private Integer loginId;

	private String browser;

	@Column(name="browser_version")
	private String browserVersion;

	private String device;

	@Column(name="login_datetime")
	private Timestamp loginDatetime;

	private String os;

	@Column(name="os_version")
	private String osVersion;

	@Column(name="session_id")
	private String sessionId;

	@Column(name="user_id")
	private String userId;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="user_id",insertable=false, updatable=false)
	private UserT userT;

	public LoginHistoryT() {
	}

	public Integer getLoginId() {
		return this.loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}

	public String getBrowser() {
		return this.browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getBrowserVersion() {
		return this.browserVersion;
	}

	public void setBrowserVersion(String browserVersion) {
		this.browserVersion = browserVersion;
	}

	public String getDevice() {
		return this.device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public Timestamp getLoginDatetime() {
		return this.loginDatetime;
	}

	public void setLoginDatetime(Timestamp loginDatetime) {
		this.loginDatetime = loginDatetime;
	}

	public String getOs() {
		return this.os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOsVersion() {
		return this.osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

}