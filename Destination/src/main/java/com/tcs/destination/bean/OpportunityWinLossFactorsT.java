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

	@Column(name = "win_loss_factor")
	private String winLossFactor;

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

	@Column(name="win_loss_others_description")
	private String winLossOthersDescription;

	@ManyToOne
	@JoinColumn(name = "modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;

	private Integer rank;

	// bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name = "opportunity_id", insertable = false, updatable = false)
	private OpportunityT opportunityT;

	// bi-directional many-to-one association to WinLossFactorMappingT
	@ManyToOne
	@JoinColumn(name = "win_loss_factor", insertable = false, updatable = false)
	private WinLossFactorMappingT winLossFactorMappingT;

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

	public String getWinLossOthersDescription() {
		return winLossOthersDescription;
	}

	public void setWinLossOthersDescription(String winLossOthersDescription) {
		this.winLossOthersDescription = winLossOthersDescription;
	}

	public void setWinLossFactor(String winLossFactor) {
		this.winLossFactor = winLossFactor;
	}

}