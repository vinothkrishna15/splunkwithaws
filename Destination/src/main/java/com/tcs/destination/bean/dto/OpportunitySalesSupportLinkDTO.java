package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The OpportunitySalesSupportLinkDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class OpportunitySalesSupportLinkDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String opportunitySalesSupportLinkId;
	private Timestamp createdDatetime;
	private UserDTO createdByUser;
	private Timestamp modifiedDatetime;
	private UserDTO modifiedByUser;

	private String opportunityId;
	private UserDTO salesSupportOwnerUser;

	public OpportunitySalesSupportLinkDTO() {
		super();
	}

	public String getOpportunitySalesSupportLinkId() {
		return opportunitySalesSupportLinkId;
	}

	public void setOpportunitySalesSupportLinkId(
			String opportunitySalesSupportLinkId) {
		this.opportunitySalesSupportLinkId = opportunitySalesSupportLinkId;
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

	public UserDTO getSalesSupportOwnerUser() {
		return salesSupportOwnerUser;
	}

	public void setSalesSupportOwnerUser(UserDTO salesSupportOwnerUser) {
		this.salesSupportOwnerUser = salesSupportOwnerUser;
	}

}