package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the connect_opportunity_link_id_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
public class ConnectOpportunityLinkDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String connectOpportunityLinkId;

	private ConnectDTO connectT;
	private OpportunityDTO opportunityT;

	private Timestamp createdDatetime;
	private Timestamp modifiedDatetime;
	// created by user
	private UserDTO userT2;
	// modified_by
	private UserDTO userT3;
	
	public ConnectOpportunityLinkDTO() {
		super();
	}

	public String getConnectOpportunityLinkId() {
		return connectOpportunityLinkId;
	}

	public void setConnectOpportunityLinkId(String connectOpportunityLinkId) {
		this.connectOpportunityLinkId = connectOpportunityLinkId;
	}

	public ConnectDTO getConnectT() {
		return connectT;
	}

	public void setConnectT(ConnectDTO connectT) {
		this.connectT = connectT;
	}

	public OpportunityDTO getOpportunityT() {
		return opportunityT;
	}

	public void setOpportunityT(OpportunityDTO opportunityT) {
		this.opportunityT = opportunityT;
	}

	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public Timestamp getModifiedDatetime() {
		return modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public UserDTO getUserT2() {
		return userT2;
	}

	public void setUserT2(UserDTO userT2) {
		this.userT2 = userT2;
	}

	public UserDTO getUserT3() {
		return userT3;
	}

	public void setUserT3(UserDTO userT3) {
		this.userT3 = userT3;
	}
	
}