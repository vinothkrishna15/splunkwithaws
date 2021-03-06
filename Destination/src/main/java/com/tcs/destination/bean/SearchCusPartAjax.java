package com.tcs.destination.bean;

import java.io.Serializable;

public class SearchCusPartAjax implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private String entityType;

	public SearchCusPartAjax() {
	}

	public SearchCusPartAjax(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

}
