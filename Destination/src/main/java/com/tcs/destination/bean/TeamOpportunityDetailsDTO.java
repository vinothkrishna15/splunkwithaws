package com.tcs.destination.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class TeamOpportunityDetailsDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String opportunityName;
	
	private String customerName;
	
	private String geography;
	
	private String owner;
	
	private int salesStageCode;
	
	private String createdDate;

	public String getOpportunityName() {
		return opportunityName;
	}

	public void setOpportunityName(String opportunityName) {
		this.opportunityName = opportunityName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public int getSalesStageCode() {
		return salesStageCode;
	}

	public void setSalesStageCode(int salesStageCode) {
		this.salesStageCode = salesStageCode;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

}
