package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * The OpportunitySubSpLinkDTO.
 * 
 */
@JsonFilter(Constants.FILTER)
public class OpportunitySubSpLinkDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String opportunitySubSpLinkId;

	private Timestamp createdDatetime;

	private UserDTO createdByUser;

	private Timestamp modifiedDatetime;

	private UserDTO modifiedByUser;

	private String opportunityId;

	private String subSp;
	
	private Boolean subspPrimary;

	private SubSpMappingDTO subSpMappingT;

	public OpportunitySubSpLinkDTO() {
		super();
	}

	public String getOpportunitySubSpLinkId() {
		return opportunitySubSpLinkId;
	}

	public void setOpportunitySubSpLinkId(String opportunitySubSpLinkId) {
		this.opportunitySubSpLinkId = opportunitySubSpLinkId;
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

	public String getSubSp() {
		return subSp;
	}

	public void setSubSp(String subSp) {
		this.subSp = subSp;
	}

	public Boolean getSubspPrimary() {
		return subspPrimary;
	}

	public void setSubspPrimary(Boolean subspPrimary) {
		this.subspPrimary = subspPrimary;
	}

	public SubSpMappingDTO getSubSpMappingT() {
		return subSpMappingT;
	}

	public void setSubSpMappingT(SubSpMappingDTO subSpMappingT) {
		this.subSpMappingT = subSpMappingT;
	}

}