package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the opportunity_offering_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "opportunityOfferingLinkId")
@Entity
@Table(name = "opportunity_offering_link_t")
@NamedQuery(name = "OpportunityOfferingLinkT.findAll", query = "SELECT o FROM OpportunityOfferingLinkT o")
public class OpportunityOfferingLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "opportunity_offering_link_id")
	private String opportunityOfferingLinkId;

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

	@ManyToOne
	@JoinColumn(name = "modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;

	@Column(name = "opportunity_id")
	private String opportunityId;

	@Column(name = "offering")
	private String offering;

	// bi-directional many-to-one association to OfferingMappingT
	@ManyToOne
	@JoinColumn(name = "offering", insertable = false, updatable = false)
	private OfferingMappingT offeringMappingT;

	// bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name = "opportunity_id", insertable = false, updatable = false)
	private OpportunityT opportunityT;

	public OpportunityOfferingLinkT() {
	}

	public String getOpportunityOfferingLinkId() {
		return this.opportunityOfferingLinkId;
	}

	public void setOpportunityOfferingLinkId(String opportunityOfferingLinkId) {
		this.opportunityOfferingLinkId = opportunityOfferingLinkId;
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

	public OfferingMappingT getOfferingMappingT() {
		return this.offeringMappingT;
	}

	public void setOfferingMappingT(OfferingMappingT offeringMappingT) {
		this.offeringMappingT = offeringMappingT;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

	public String getOffering() {
		return offering;
	}

	public void setOffering(String offering) {
		this.offering = offering;
	}

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}
}