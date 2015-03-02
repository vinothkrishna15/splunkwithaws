package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;


/**
 * The persistent class for the opportunity_competitor_link_t database table.
 * 
 */
@Entity
@Table(name="opportunity_competitor_link_t")
@NamedQuery(name="OpportunityCompetitorLinkT.findAll", query="SELECT o FROM OpportunityCompetitorLinkT o")
public class OpportunityCompetitorLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="opportunity_competitor_link_id")
	private String opportunityCompetitorLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	//bi-directional many-to-one association to CompetitorMappingT
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="competitor_name")
	private CompetitorMappingT competitorMappingT;

	//bi-directional many-to-one association to OpportunityT
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="opportunity_id")
	private OpportunityT opportunityT;

	public OpportunityCompetitorLinkT() {
	}

	public String getOpportunityCompetitorLinkId() {
		return this.opportunityCompetitorLinkId;
	}

	public void setOpportunityCompetitorLinkId(String opportunityCompetitorLinkId) {
		this.opportunityCompetitorLinkId = opportunityCompetitorLinkId;
	}

	public String getCreatedModifiedBy() {
		return this.createdModifiedBy;
	}

	public void setCreatedModifiedBy(String createdModifiedBy) {
		this.createdModifiedBy = createdModifiedBy;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return this.createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
	}

	public CompetitorMappingT getCompetitorMappingT() {
		return this.competitorMappingT;
	}

	public void setCompetitorMappingT(CompetitorMappingT competitorMappingT) {
		this.competitorMappingT = competitorMappingT;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

}