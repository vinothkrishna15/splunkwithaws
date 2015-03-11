package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.List;


/**
 * The persistent class for the deal_type_mapping_t database table.
 * 
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="dealType")
@Entity
@Table(name="deal_type_mapping_t")
@NamedQuery(name="DealTypeMappingT.findAll", query="SELECT d FROM DealTypeMappingT d")
public class DealTypeMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="deal_type")
	private String dealType;

	//bi-directional many-to-one association to OpportunityT
	@OneToMany(mappedBy="dealTypeMappingT")
	private List<OpportunityT> opportunityTs;

	public DealTypeMappingT() {
	}

	public String getDealType() {
		return this.dealType;
	}

	public void setDealType(String dealType) {
		this.dealType = dealType;
	}

	public List<OpportunityT> getOpportunityTs() {
		return this.opportunityTs;
	}

	public void setOpportunityTs(List<OpportunityT> opportunityTs) {
		this.opportunityTs = opportunityTs;
	}

	public OpportunityT addOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().add(opportunityT);
		opportunityT.setDealTypeMappingT(this);

		return opportunityT;
	}

	public OpportunityT removeOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().remove(opportunityT);
		opportunityT.setDealTypeMappingT(null);

		return opportunityT;
	}

}