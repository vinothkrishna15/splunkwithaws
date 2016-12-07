package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The OpportunityWinLossFactorsDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class OpportunityWinLossFactorsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String opportunityWinLossFactorsId;
	private String winLossFactor;
	private String winLossOthersDescription;
	private Integer rank;
	private String opportunityId;

	private Timestamp createdDatetime;
	private UserDTO createdByUser;
	private Timestamp modifiedDatetime;
	private UserDTO modifiedByUser;

	private WinLossFactorMappingDTO winLossFactorMappingT;

	public OpportunityWinLossFactorsDTO() {
		super();
	}


	public String getOpportunityWinLossFactorsId() {
		return opportunityWinLossFactorsId;
	}


	public void setOpportunityWinLossFactorsId(String opportunityWinLossFactorsId) {
		this.opportunityWinLossFactorsId = opportunityWinLossFactorsId;
	}


	public String getWinLossFactor() {
		return winLossFactor;
	}


	public void setWinLossFactor(String winLossFactor) {
		this.winLossFactor = winLossFactor;
	}


	public String getWinLossOthersDescription() {
		return winLossOthersDescription;
	}


	public void setWinLossOthersDescription(String winLossOthersDescription) {
		this.winLossOthersDescription = winLossOthersDescription;
	}


	public Integer getRank() {
		return rank;
	}


	public void setRank(Integer rank) {
		this.rank = rank;
	}


	public String getOpportunityId() {
		return opportunityId;
	}


	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
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


	public WinLossFactorMappingDTO getWinLossFactorMappingT() {
		return winLossFactorMappingT;
	}


	public void setWinLossFactorMappingT(
			WinLossFactorMappingDTO winLossFactorMappingT) {
		this.winLossFactorMappingT = winLossFactorMappingT;
	}

}