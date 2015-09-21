package com.tcs.destination.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * This DTO holds the OpportunityDetails
 * 
 * @author bnpp
 *
 */
@JsonFilter(Constants.FILTER)
public class OpportunityDetailsDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String opportunityId;
	
	private String opportunityName;
	
	private String customerName;
	
	private String geography;
	
	private String owner;
	
	private int salesStageCode;
	
	private String modifiedDate;

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

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

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

}
