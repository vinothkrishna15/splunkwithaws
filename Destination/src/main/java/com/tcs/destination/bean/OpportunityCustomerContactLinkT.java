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
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the opportunity_customer_contact_link_t database
 * table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "opportunityCustomerContactLinkId")
@Entity
@Table(name = "opportunity_customer_contact_link_t")
@NamedQuery(name = "OpportunityCustomerContactLinkT.findAll", query = "SELECT o FROM OpportunityCustomerContactLinkT o")
public class OpportunityCustomerContactLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "opportunity_customer_contact_link_id")
	private String opportunityCustomerContactLinkId;

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

	// bi-directional many-to-one association to ContactT

	@Column(name = "contact_id")
	private String contactId;

	@Column(name = "opportunity_id")
	private String opportunityId;

	@ManyToOne
	@JoinColumn(name = "contact_id", insertable = false, updatable = false)
	private ContactT contactT;

	// bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@OrderBy("created_modified_datetime DESC")
	@JoinColumn(name = "opportunity_id", insertable = false, updatable = false)
	private OpportunityT opportunityT;

	public OpportunityCustomerContactLinkT() {
	}

	public String getOpportunityCustomerContactLinkId() {
		return this.opportunityCustomerContactLinkId;
	}

	public void setOpportunityCustomerContactLinkId(
			String opportunityCustomerContactLinkId) {
		this.opportunityCustomerContactLinkId = opportunityCustomerContactLinkId;
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

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}
}