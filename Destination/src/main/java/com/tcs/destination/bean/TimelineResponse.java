package com.tcs.destination.bean;

import java.sql.Timestamp;
import java.util.List;

public class TimelineResponse {

	private Timestamp token;

	private List<EntityBean> bean;

	public Timestamp getToken() {
		return token;
	}

	public void setToken(Timestamp token) {
		this.token = token;
	}

	public List<EntityBean> getBean() {
		return bean;
	}

	public void setBean(List<EntityBean> bean) {
		this.bean = bean;
	}

}
