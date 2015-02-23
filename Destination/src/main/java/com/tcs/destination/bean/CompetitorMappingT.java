package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the competitor_mapping_t database table.
 * 
 */
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

	//bi-directional many-to-one association to OpportunityT
	@OneToMany(mappedBy="competitorMappingT")
	private List<OpportunityT> opportunityTs;

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

	public List<OpportunityT> getOpportunityTs() {
		return this.opportunityTs;
	}

	public void setOpportunityTs(List<OpportunityT> opportunityTs) {
		this.opportunityTs = opportunityTs;
	}

	public OpportunityT addOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().add(opportunityT);
		opportunityT.setCompetitorMappingT(this);

		return opportunityT;
	}

	public OpportunityT removeOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().remove(opportunityT);
		opportunityT.setCompetitorMappingT(null);

		return opportunityT;
	}

}