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
 * The persistent class for the opportunity_tcs_account_contact_link_t database
 * table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "opportunityTcsAccountContactLinkId")
@Entity
@Table(name = "opportunity_tcs_account_contact_link_t")
@NamedQuery(name = "OpportunityTcsAccountContactLinkT.findAll", query = "SELECT o FROM OpportunityTcsAccountContactLinkT o")
public class OpportunityTcsAccountContactLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "opportunity_tcs_account_contact_link_id")
	private String opportunityTcsAccountContactLinkId;

	@Column(name = "created_modified_by")
	private String createdModifiedBy;

	@Column(name = "created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name = "contact_id")
	private String contactId;

	@Column(name = "opportunity_id")
	private String opportunityId;

	// bi-directional many-to-one association to ContactT
	@ManyToOne
	@JoinColumn(name = "contact_id", insertable = false, updatable = false)
	private ContactT contactT;

	// bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name = "opportunity_id", insertable = false, updatable = false)
	private OpportunityT opportunityT;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "created_modified_by", insertable = false, updatable = false)
	private UserT createdModifiedByUser;

	public OpportunityTcsAccountContactLinkT() {
	}

	public String getOpportunityTcsAccountContactLinkId() {
		return this.opportunityTcsAccountContactLinkId;
	}

	public void setOpportunityTcsAccountContactLinkId(
			String opportunityTcsAccountContactLinkId) {
		this.opportunityTcsAccountContactLinkId = opportunityTcsAccountContactLinkId;
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

	public ContactT getContactT() {
		return this.contactT;
	}

	public void setContactT(ContactT contactT) {
		this.contactT = contactT;
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

	public String getOpportunityId() {
		return opportunityId;
	}

	public String getContactId() {
		return contactId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
}