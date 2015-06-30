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

	@Column(name = "created_by", updatable = false)
	private String createdBy;

	@Column(name = "created_datetime", updatable = false)
	private Timestamp createdDatetime;

	@ManyToOne
	@JoinColumn(name = "created_by", insertable = false, updatable = false)
	private UserT createdByUser;

	@Column(name = "modified_by")
	private String modifiedBy;

	@Column(name = "modified_datetime")
	private Timestamp modifiedDatetime;

	@ManyToOne
	@JoinColumn(name = "modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;

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
	@JoinColumn(name = "opportunity_id", insertable = false, updatable = false)
	private OpportunityT opportunityT;

	@Column(name = "incumbent_flag")
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

	public String getCreatedBy() {

		return this.createdBy;

	}

	public void setCreatedBy(String createdBy) {

		this.createdBy = createdBy;

	}

	public Timestamp getCreatedDatetime() {

		return this.createdDatetime;

	}

	public void setCreatedDatetime(Timestamp createdDatetime) {

		this.createdDatetime = createdDatetime;

	}

	public UserT getCreatedByUser() {

		return this.createdByUser;

	}

	public void setCreatedByUser(UserT createdByUser) {

		this.createdByUser = createdByUser;

	}

	public String getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;

	}

	public UserT getModifiedByUser() {
		return this.modifiedByUser;
	}

	public void setModifiedByUser(UserT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;

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
	}
}