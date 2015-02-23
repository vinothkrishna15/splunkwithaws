package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class RecentlyAddedCustPart implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private String groupCustomerName;

	private GeographyMappingT geographyMappingT;

	private byte[] logo;
	
	private Timestamp createdModifiedDatetime;

	public Timestamp getCreatedModifiedDatetime() {
		return createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
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

	public String getGroupCustomerName() {
		return groupCustomerName;
	}

	public void setGroupCustomerName(String groupCustomerName) {
		this.groupCustomerName = groupCustomerName;
	}

	public GeographyMappingT getGeographyMappingT() {
		return geographyMappingT;
	}

	public void setGeographyMappingT(GeographyMappingT geographyMappingT) {
		this.geographyMappingT = geographyMappingT;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}



}
