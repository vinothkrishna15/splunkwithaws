package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;

/**
 * The persistent class for the opportunity_win_loss_factors_t database table.
 * 
 */
@Entity
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "opportunityWinLossFactorsId")
@Table(name = "opportunity_win_loss_factors_t")
@NamedQuery(name = "OpportunityWinLossFactorsT.findAll", query = "SELECT o FROM OpportunityWinLossFactorsT o")
public class OpportunityWinLossFactorsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "opportunity_win_loss_factors_id")
	private String opportunityWinLossFactorsId;

	@Column(name = "created_modified_by")
	private String createdModifiedBy;

	@Column(name = "win_loss_factor")
	private String winLossFactor;

	@Column(name = "created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	private Integer rank;

	// bi-directional many-to-one association to WinLossFactorMappingT
	@ManyToOne
	@JoinColumn(name = "win_loss_factor",insertable=false,updatable=false)
	private WinLossFactorMappingT winLossFactorMappingT;

	// bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name = "opportunity_id", insertable = false, updatable = false)
	private OpportunityT opportunityT;

	@Column(name = "opportunity_id")
	private String opportunityId;

	public OpportunityWinLossFactorsT() {
	}

	public String getOpportunityWinLossFactorsId() {
		return this.opportunityWinLossFactorsId;
	}

	public void setOpportunityWinLossFactorsId(
			String opportunityWinLossFactorsId) {
		this.opportunityWinLossFactorsId = opportunityWinLossFactorsId;
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

	public Integer getRank() {
		return this.rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public WinLossFactorMappingT getWinLossFactorMappingT() {
		return this.winLossFactorMappingT;
	}

	public void setWinLossFactorMappingT(
			WinLossFactorMappingT winLossFactorMappingT) {
		this.winLossFactorMappingT = winLossFactorMappingT;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public String getWinLossFactor() {
		return winLossFactor;
	}

	public void setWinLossFactor(String winLossFactor) {
		this.winLossFactor = winLossFactor;
	}

}