package com.tcs.destination.bean.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;


/**
 * The CompetitorMappingDTO.
 * 
 */
@JsonFilter(Constants.FILTER)
public class CompetitorMappingDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String competitorName;
	private boolean active;
	private String website;

	public CompetitorMappingDTO() {
		super();
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