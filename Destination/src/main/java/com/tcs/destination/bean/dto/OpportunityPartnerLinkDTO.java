package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * The OpportunityPartnerLinkDTO.
 * 
 */
@JsonFilter(Constants.FILTER)
public class OpportunityPartnerLinkDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String opportunityPartnerLinkId;

	private Timestamp createdDatetime;

	private UserDTO createdByUser;

	private Timestamp modifiedDatetime;

	private UserDTO modifiedByUser;

	private OpportunityDTO opportunityT;

	private String partnerId;

	private String opportunityId;

	private PartnerMasterDTO partnerMasterT;

	public OpportunityPartnerLinkDTO() {
		super();
	}

	public String getOpportunityPartnerLinkId() {
		return opportunityPartnerLinkId;
	}

	public void setOpportunityPartnerLinkId(String opportunityPartnerLinkId) {
		this.opportunityPartnerLinkId = opportunityPartnerLinkId;
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

	public OpportunityDTO getOpportunityT() {
		return opportunityT;
	}

	public void setOpportunityT(OpportunityDTO opportunityT) {
		this.opportunityT = opportunityT;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public PartnerMasterDTO getPartnerMasterT() {
		return partnerMasterT;
	}

	public void setPartnerMasterT(PartnerMasterDTO partnerMasterT) {
		this.partnerMasterT = partnerMasterT;
	}
}