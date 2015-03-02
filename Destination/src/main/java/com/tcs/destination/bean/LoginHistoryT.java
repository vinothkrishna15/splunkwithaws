package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the login_history_t database table.
 * 
 */
@Entity
@Table(name="login_history_t")
@NamedQuery(name="LoginHistoryT.findAll", query="SELECT l FROM LoginHistoryT l")
public class LoginHistoryT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="login_id")
	private Integer loginId;

	@Column(name="login_datetime")
	private Timestamp loginDatetime;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="user_id")
	private UserT userT;

	public LoginHistoryT() {
	}

	public Integer getLoginId() {
		return this.loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}

	public Timestamp getLoginDatetime() {
		return this.loginDatetime;
	}

	public void setLoginDatetime(Timestamp loginDatetime) {
		this.loginDatetime = loginDatetime;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

}