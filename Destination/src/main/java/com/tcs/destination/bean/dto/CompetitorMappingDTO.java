package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * The CompetitorMappingDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class CompetitorMappingDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String competitorName;
	private Boolean active;
	private String website;
	
	private List<OpportunityCompetitorLinkDTO> opportunityCompetitorLinkTs;

	public CompetitorMappingDTO() {
		super();
	}
	
	public List<OpportunityCompetitorLinkDTO> getOpportunityCompetitorLinkTs() {
		return opportunityCompetitorLinkTs;
	}

	public void setOpportunityCompetitorLinkTs(
			List<OpportunityCompetitorLinkDTO> opportunityCompetitorLinkTs) {
		this.opportunityCompetitorLinkTs = opportunityCompetitorLinkTs;
	}

	public String getCompetitorName() {
		return competitorName;
	}

	public void setCompetitorName(String competitorName) {
		this.competitorName = competitorName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

}