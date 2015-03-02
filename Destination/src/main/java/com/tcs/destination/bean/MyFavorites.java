package com.tcs.destination.bean;

import java.io.Serializable;

public class MyFavorites implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private GeographyMappingT geography;
	
	private String entityType;

	private byte[] logo;

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

	public GeographyMappingT getGeography() {
		return geography;
	}

	public void setGeography(GeographyMappingT geography) {
		this.geography = geography;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	
	

}
