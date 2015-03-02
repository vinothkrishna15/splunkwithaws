package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.util.List;


/**
 * The persistent class for the offering_mapping_t database table.
 * 
 */
@Entity
@Table(name="offering_mapping_t")
@NamedQuery(name="OfferingMappingT.findAll", query="SELECT o FROM OfferingMappingT o")
public class OfferingMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	private String active;

	@Column(name="offering_id")
	private Integer offeringId;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="offering")
	private String offering;
	
	@Column(name="sub_sp")
	private String subSp;

	//bi-directional many-to-one association to ConnectT
	@OneToMany(mappedBy="offeringMappingT")
	private List<ConnectT> connectTs;

	//bi-directional many-to-one association to OpportunityT
	@OneToMany(mappedBy="offeringMappingT")
	private List<OpportunityT> opportunityTs;

	public OfferingMappingT() {
	}

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public Integer getOfferingId() {
		return this.offeringId;
	}

	public void setOfferingId(Integer offeringId) {
		this.offeringId = offeringId;
	}

	public String getOffering() {
		return offering;
	}

	public void setOffering(String offering) {
		this.offering = offering;
	}

	public String getSubSp() {
		return this.subSp;
	}

	public void setSubSp(String subSp) {
		this.subSp = subSp;
	}

	public List<ConnectT> getConnectTs() {
		return this.connectTs;
	}

	public void setConnectTs(List<ConnectT> connectTs) {
		this.connectTs = connectTs;
	}

	public ConnectT addConnectT(ConnectT connectT) {
		getConnectTs().add(connectT);
		connectT.setOfferingMappingT(this);

		return connectT;
	}

	public ConnectT removeConnectT(ConnectT connectT) {
		getConnectTs().remove(connectT);
		connectT.setOfferingMappingT(null);

		return connectT;
	}

	public List<OpportunityT> getOpportunityTs() {
		return this.opportunityTs;
	}

	public void setOpportunityTs(List<OpportunityT> opportunityTs) {
		this.opportunityTs = opportunityTs;
	}

	public OpportunityT addOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().add(opportunityT);
		opportunityT.setOfferingMappingT(this);

		return opportunityT;
	}

	public OpportunityT removeOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().remove(opportunityT);
		opportunityT.setOfferingMappingT(null);

		return opportunityT;
	}

}