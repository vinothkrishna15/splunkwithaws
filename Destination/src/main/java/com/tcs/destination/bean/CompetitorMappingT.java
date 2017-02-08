package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the competitor_mapping_t database table.
 * 
 */
@JsonInclude(Include.NON_NULL)
@JsonFilter(Constants.FILTER)
//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="competitorName")
@Entity
@Table(name="competitor_mapping_t")
@NamedQuery(name="CompetitorMappingT.findAll", query="SELECT c FROM CompetitorMappingT c")
public class CompetitorMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Id
	@Column(name="competitor_name")
	private String competitorName;
	
	@Column(name="active")
	private boolean active;

	private String website;
	
	private byte[] logo;

	//bi-directional many-to-one association to OpportunityCompetitorLinkT
	@JsonIgnore
	@OneToMany(mappedBy="competitorMappingT")
	private List<OpportunityCompetitorLinkT> opportunityCompetitorLinkTs;

	public CompetitorMappingT() {
	}

	public String getCompetitorName() {
		return this.competitorName;
	}

	public void setCompetitorName(String competitorName) {
		this.competitorName = competitorName;
	}

	public List<OpportunityCompetitorLinkT> getOpportunityCompetitorLinkTs() {
		return this.opportunityCompetitorLinkTs;
	}

	public void setOpportunityCompetitorLinkTs(List<OpportunityCompetitorLinkT> opportunityCompetitorLinkTs) {
		this.opportunityCompetitorLinkTs = opportunityCompetitorLinkTs;
	}

	public OpportunityCompetitorLinkT addOpportunityCompetitorLinkT(OpportunityCompetitorLinkT opportunityCompetitorLinkT) {
		getOpportunityCompetitorLinkTs().add(opportunityCompetitorLinkT);
		opportunityCompetitorLinkT.setCompetitorMappingT(this);

		return opportunityCompetitorLinkT;
	}

	public OpportunityCompetitorLinkT removeOpportunityCompetitorLinkT(OpportunityCompetitorLinkT opportunityCompetitorLinkT) {
		getOpportunityCompetitorLinkTs().remove(opportunityCompetitorLinkT);
		opportunityCompetitorLinkT.setCompetitorMappingT(null);

		return opportunityCompetitorLinkT;
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

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}
}