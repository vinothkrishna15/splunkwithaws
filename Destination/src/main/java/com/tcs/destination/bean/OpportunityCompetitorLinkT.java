package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the opportunity_competitor_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "opportunityCompetitorLinkId")
@Entity
@Table(name = "opportunity_competitor_link_t")
@NamedQuery(name = "OpportunityCompetitorLinkT.findAll", query = "SELECT o FROM OpportunityCompetitorLinkT o")
public class OpportunityCompetitorLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "opportunity_competitor_link_id")
	private String opportunityCompetitorLinkId;

	@Column(name = "created_modified_by")
	private String createdModifiedBy;

	@Column(name = "created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name = "competitor_name")
	private String competitorName;

	@Column(name = "opportunity_id")
	private String opportunityId;
	
	// bi-directional many-to-one association to CompetitorMappingT
	@ManyToOne
	@JoinColumn(name = "competitor_name", insertable = false, updatable = false)
	private CompetitorMappingT competitorMappingT;

	// bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name = "opportunity_id",insertable=false,updatable=false)
	private OpportunityT opportunityT;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "created_modified_by", insertable = false, updatable = false)
	private UserT createdModifiedByUser;

	@Column(name="incumbent_flag")
	private String incumbentFlag;
	
	public OpportunityCompetitorLinkT() {
	}

	public String getOpportunityCompetitorLinkId() {
		return this.opportunityCompetitorLinkId;
	}

	public void setOpportunityCompetitorLinkId(
			String opportunityCompetitorLinkId) {
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

	public UserT getCreatedModifiedByUser() {
		return this.createdModifiedByUser;
	}

	public void setCreatedModifiedByUser(UserT createdModifiedByUser) {
		this.createdModifiedByUser = createdModifiedByUser;
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

	public String getIncumbentFlag() {
		return this.incumbentFlag;
	}

	public void setIncumbentFlag(String incumbentFlag) {
		this.incumbentFlag = incumbentFlag;
	}}