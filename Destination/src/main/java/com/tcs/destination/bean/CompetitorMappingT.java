package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the competitor_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="competitorName")
@Entity
@Table(name="competitor_mapping_t")
@NamedQuery(name="CompetitorMappingT.findAll", query="SELECT c FROM CompetitorMappingT c")
public class CompetitorMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="competitor_name")
	private String competitorName;

	@Column(name="active_flag")
	private String activeFlag;

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

	public String getActiveFlag() {
		return this.activeFlag;
	}

	public void setActiveFlag(String activeFlag) {
		this.activeFlag = activeFlag;
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

}