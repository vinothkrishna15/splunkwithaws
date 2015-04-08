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
 * The persistent class for the offering_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="offering")
@Entity
@Table(name = "offering_mapping_t")
@NamedQuery(name = "OfferingMappingT.findAll", query = "SELECT o FROM OfferingMappingT o")
public class OfferingMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String offering;

	private String active;

	@Column(name = "offering_id")
	private Integer offeringId;

	@Column(name="sub_sp")
	private String subSp;

	//bi-directional many-to-one association to ConnectOfferingLinkT
	@OneToMany(mappedBy="offeringMappingT")
	private List<ConnectOfferingLinkT> connectOfferingLinkTs;

	//bi-directional many-to-one association to OpportunityOfferingLinkT
	@OneToMany(mappedBy="offeringMappingT")
	private List<OpportunityOfferingLinkT> opportunityOfferingLinkTs;

	public OfferingMappingT() {
	}

	public String getOffering() {
		return this.offering;
	}

	public void setOffering(String offering) {
		this.offering = offering;
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

	public String getSubSp() {
		return this.subSp;
	}

	public void setSubSp(String subSp) {
		this.subSp = subSp;
	}

	public List<ConnectOfferingLinkT> getConnectOfferingLinkTs() {
		return this.connectOfferingLinkTs;
	}

	public void setConnectOfferingLinkTs(List<ConnectOfferingLinkT> connectOfferingLinkTs) {
		this.connectOfferingLinkTs = connectOfferingLinkTs;
	}

	public ConnectOfferingLinkT addConnectOfferingLinkT(ConnectOfferingLinkT connectOfferingLinkT) {
		getConnectOfferingLinkTs().add(connectOfferingLinkT);
		connectOfferingLinkT.setOfferingMappingT(this);

		return connectOfferingLinkT;
	}

	public ConnectOfferingLinkT removeConnectOfferingLinkT(ConnectOfferingLinkT connectOfferingLinkT) {
		getConnectOfferingLinkTs().remove(connectOfferingLinkT);
		connectOfferingLinkT.setOfferingMappingT(null);

		return connectOfferingLinkT;
	}

	public List<OpportunityOfferingLinkT> getOpportunityOfferingLinkTs() {
		return this.opportunityOfferingLinkTs;
	}

	public void setOpportunityOfferingLinkTs(List<OpportunityOfferingLinkT> opportunityOfferingLinkTs) {
		this.opportunityOfferingLinkTs = opportunityOfferingLinkTs;
	}

	public OpportunityOfferingLinkT addOpportunityOfferingLinkT(OpportunityOfferingLinkT opportunityOfferingLinkT) {
		getOpportunityOfferingLinkTs().add(opportunityOfferingLinkT);
		opportunityOfferingLinkT.setOfferingMappingT(this);

		return opportunityOfferingLinkT;
	}

	public OpportunityOfferingLinkT removeOpportunityOfferingLinkT(OpportunityOfferingLinkT opportunityOfferingLinkT) {
		getOpportunityOfferingLinkTs().remove(opportunityOfferingLinkT);
		opportunityOfferingLinkT.setOfferingMappingT(null);

		return opportunityOfferingLinkT;
	}

}