package com.tcs.destination.bean;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class FrequentlySearchedResponse {

	private int count;
	private Object entity;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
	}

	public Object getEntity() {
		return entity;
	}
}
