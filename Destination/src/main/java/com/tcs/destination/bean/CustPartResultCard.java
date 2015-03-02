package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class CustPartResultCard implements Serializable,
		Comparable<CustPartResultCard> {

	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private String groupCustomerName;

	private GeographyMappingT geographyMappingT;

	private byte[] logo;

	private Timestamp createdModifiedDatetime;

	private int connects;

	private int opportunities;
	
	private String entityType;

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

	public int getConnects() {
		return connects;
	}

	public void setConnects(int connects) {
		this.connects = connects;
	}

	public int getOpportunities() {
		return opportunities;
	}

	public void setOpportunities(int opportunities) {
		this.opportunities = opportunities;
	}

	@Override
	public int compareTo(CustPartResultCard o) {
		// TODO Auto-generated method stub

		if (o.getCreatedModifiedDatetime() != null
				&& this.createdModifiedDatetime != null) {
			long newTs = o.getCreatedModifiedDatetime().getTime();
			return newTs - this.createdModifiedDatetime.getTime() > 0 ? 1 : -1;
		} else
			return 0;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	
	

}
