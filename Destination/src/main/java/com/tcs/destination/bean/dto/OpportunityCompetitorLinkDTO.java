package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * The OpportunityCompetitorLinkDTO.
 * 
 */
@JsonFilter(Constants.FILTER)
public class OpportunityCompetitorLinkDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String opportunityCompetitorLinkId;

	private Timestamp createdDatetime;
	private UserDTO createdByUser;
	private Timestamp modifiedDatetime;
	private UserDTO modifiedByUser;

	private String competitorName;
	private String opportunityId;

	private CompetitorMappingDTO competitorMappingT;
	private OpportunityDTO opportunityT;

	private String incumbentFlag;
	private Boolean isLostCompetitor;

	public OpportunityCompetitorLinkDTO() {
		super();
	}

	public String getOpportunityCompetitorLinkId() {
		return opportunityCompetitorLinkId;
	}

	public void setOpportunityCompetitorLinkId(String opportunityCompetitorLinkId) {
		this.opportunityCompetitorLinkId = opportunityCompetitorLinkId;
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

	public String getCompetitorName() {
		return competitorName;
	}

	public void setCompetitorName(String competitorName) {
		this.competitorName = competitorName;
	}

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public CompetitorMappingDTO getCompetitorMappingT() {
		return competitorMappingT;
	}

	public void setCompetitorMappingT(CompetitorMappingDTO competitorMappingT) {
		this.competitorMappingT = competitorMappingT;
	}

	public OpportunityDTO getOpportunityT() {
		return opportunityT;
	}

	public void setOpportunityT(OpportunityDTO opportunityT) {
		this.opportunityT = opportunityT;
	}

	public String getIncumbentFlag() {
		return incumbentFlag;
	}

	public void setIncumbentFlag(String incumbentFlag) {
		this.incumbentFlag = incumbentFlag;
	}

	public Boolean getIsLostCompetitor() {
		return isLostCompetitor;
	}

	public void setIsLostCompetitor(Boolean isLostCompetitor) {
		this.isLostCompetitor = isLostCompetitor;
	}

}