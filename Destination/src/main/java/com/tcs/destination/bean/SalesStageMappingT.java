package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the sales_stage_mapping_t database table.
 * 
 */
@Entity
@Table(name="sales_stage_mapping_t")
@NamedQuery(name="SalesStageMappingT.findAll", query="SELECT s FROM SalesStageMappingT s")
public class SalesStageMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="sales_stage_code")
	private Integer salesStageCode;

	@Column(name="sales_stage_description")
	private String salesStageDescription;

	//bi-directional many-to-one association to OpportunityT
	@OneToMany(mappedBy="salesStageMappingT")
	private List<OpportunityT> opportunityTs;

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

}