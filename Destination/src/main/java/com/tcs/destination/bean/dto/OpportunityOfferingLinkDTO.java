package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * OpportunityOfferingLinkDTO
 * 
 */
@JsonFilter(Constants.FILTER)
public class OpportunityOfferingLinkDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String opportunityOfferingLinkId;
	private Timestamp createdDatetime;
	private UserDTO createdByUser;

	private Timestamp modifiedDatetime;
	private UserDTO modifiedByUser;
	private String opportunityId;

	private OfferingMappingDTO offeringMappingT;

	public OpportunityOfferingLinkDTO() {
		super();
	}

	public String getOpportunityOfferingLinkId() {
		return opportunityOfferingLinkId;
	}

	public void setOpportunityOfferingLinkId(String opportunityOfferingLinkId) {
		this.opportunityOfferingLinkId = opportunityOfferingLinkId;
	}

	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public UserDTO getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserDTO createdByUser) {
		this.createdByUser = createdByUser;
	}

	public Timestamp getModifiedDatetime() {
		return modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public UserDTO getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserDTO modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public OfferingMappingDTO getOfferingMappingT() {
		return offeringMappingT;
	}

	public void setOfferingMappingT(OfferingMappingDTO offeringMappingT) {
		this.offeringMappingT = offeringMappingT;
	}

}