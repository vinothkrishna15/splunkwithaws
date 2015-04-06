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
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the opportunity_offering_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Entity
@Table(name="opportunity_offering_link_t")
@NamedQuery(name="OpportunityOfferingLinkT.findAll", query="SELECT o FROM OpportunityOfferingLinkT o")
public class OpportunityOfferingLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="opportunity_offering_link_id")
	private String opportunityOfferingLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	//bi-directional many-to-one association to OfferingMappingT
	@ManyToOne
	@JoinColumn(name="offering")
	private OfferingMappingT offeringMappingT;

	//bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name="opportunity_id")
	private OpportunityT opportunityT;

//bi-directional many-to-one association to UserT
@ManyToOne
@JoinColumn(name="created_modified_by",insertable=false,updatable=false)
private UserT createdModifiedByUser;
	public OpportunityOfferingLinkT() {
	}

	public String getOpportunityOfferingLinkId() {
		return this.opportunityOfferingLinkId;
	}

	public void setOpportunityOfferingLinkId(String opportunityOfferingLinkId) {
		this.opportunityOfferingLinkId = opportunityOfferingLinkId;
	}

	public String getCreatedModifiedBy() {
		return this.createdModifiedBy;
	}

	public void setCreatedModifiedBy(String createdModifiedBy) {
		this.createdModifiedBy = createdModifiedBy;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return this.createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
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

public UserT getCreatedModifiedByUser() {
return this.createdModifiedByUser;
}

public void setCreatedModifiedByUser(UserT createdModifiedByUser) {
this.createdModifiedByUser = createdModifiedByUser;
}
}