package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.util.List;


/**
 * The persistent class for the win_loss_factor_mapping_t database table.
 * 
 */
@Entity
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "winLossFactor")
@Table(name="win_loss_factor_mapping_t")
@NamedQuery(name="WinLossFactorMappingT.findAll", query="SELECT w FROM WinLossFactorMappingT w")
public class WinLossFactorMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="win_loss_factor")
	private String winLossFactor;

	private String type;
	
	private boolean active;

	//bi-directional many-to-one association to OpportunityWinLossFactorsT
	@JsonIgnore
	@OneToMany(mappedBy="winLossFactorMappingT")
	private List<OpportunityWinLossFactorsT> opportunityWinLossFactorsTs;

	public WinLossFactorMappingT() {
	}

	public String getWinLossFactor() {
		return this.winLossFactor;
	}

	public void setWinLossFactor(String winLossFactor) {
		this.winLossFactor = winLossFactor;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<OpportunityWinLossFactorsT> getOpportunityWinLossFactorsTs() {
		return this.opportunityWinLossFactorsTs;
	}

	public void setOpportunityWinLossFactorsTs(List<OpportunityWinLossFactorsT> opportunityWinLossFactorsTs) {
		this.opportunityWinLossFactorsTs = opportunityWinLossFactorsTs;
	}

	public OpportunityWinLossFactorsT addOpportunityWinLossFactorsT(OpportunityWinLossFactorsT opportunityWinLossFactorsT) {
		getOpportunityWinLossFactorsTs().add(opportunityWinLossFactorsT);
		opportunityWinLossFactorsT.setWinLossFactorMappingT(this);

		return opportunityWinLossFactorsT;
	}

	public OpportunityWinLossFactorsT removeOpportunityWinLossFactorsT(OpportunityWinLossFactorsT opportunityWinLossFactorsT) {
		getOpportunityWinLossFactorsTs().remove(opportunityWinLossFactorsT);
		opportunityWinLossFactorsT.setWinLossFactorMappingT(null);

		return opportunityWinLossFactorsT;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
}