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
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the sales_stage_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "salesStageCode")
@Entity
@Table(name = "sales_stage_mapping_t")
@NamedQuery(name = "SalesStageMappingT.findAll", query = "SELECT s FROM SalesStageMappingT s")
public class SalesStageMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="sales_stage_code")
	private Integer salesStageCode;

	@Column(name="sales_stage_description")
	private String salesStageDescription;

	//bi-directional many-to-one association to OpportunityT
	@OneToMany(mappedBy="salesStageMappingT")
	private List<OpportunityT> opportunityTs;

	//bi-directional many-to-one association to OpportunityTimelineHistoryT
	@OneToMany(mappedBy="salesStageMappingT")
	private List<OpportunityTimelineHistoryT> opportunityTimelineHistoryTs;

	public SalesStageMappingT() {
	}

	public Integer getSalesStageCode() {
		return this.salesStageCode;
	}

	public void setSalesStageCode(Integer salesStageCode) {
		this.salesStageCode = salesStageCode;
	}

	public String getSalesStageDescription() {
		return this.salesStageDescription;
	}

	public void setSalesStageDescription(String salesStageDescription) {
		this.salesStageDescription = salesStageDescription;
	}

	public List<OpportunityT> getOpportunityTs() {
		return this.opportunityTs;
	}

	public void setOpportunityTs(List<OpportunityT> opportunityTs) {
		this.opportunityTs = opportunityTs;
	}

	public OpportunityT addOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().add(opportunityT);
		opportunityT.setSalesStageMappingT(this);

		return opportunityT;
	}

	public OpportunityT removeOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().remove(opportunityT);
		opportunityT.setSalesStageMappingT(null);

		return opportunityT;
	}

	public List<OpportunityTimelineHistoryT> getOpportunityTimelineHistoryTs() {
		return this.opportunityTimelineHistoryTs;
	}

	public void setOpportunityTimelineHistoryTs(List<OpportunityTimelineHistoryT> opportunityTimelineHistoryTs) {
		this.opportunityTimelineHistoryTs = opportunityTimelineHistoryTs;
	}

	public OpportunityTimelineHistoryT addOpportunityTimelineHistoryT(OpportunityTimelineHistoryT opportunityTimelineHistoryT) {
		getOpportunityTimelineHistoryTs().add(opportunityTimelineHistoryT);
		opportunityTimelineHistoryT.setSalesStageMappingT(this);

		return opportunityTimelineHistoryT;
	}

	public OpportunityTimelineHistoryT removeOpportunityTimelineHistoryT(OpportunityTimelineHistoryT opportunityTimelineHistoryT) {
		getOpportunityTimelineHistoryTs().remove(opportunityTimelineHistoryT);
		opportunityTimelineHistoryT.setSalesStageMappingT(null);

		return opportunityTimelineHistoryT;
	}

}