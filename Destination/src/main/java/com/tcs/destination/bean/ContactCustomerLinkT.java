package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;

/**
 * The persistent class for the contact_customer_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "contactCustomerLinkId")
@Entity
@Table(name = "contact_customer_link_t")
@NamedQuery(name = "ContactCustomerLinkT.findAll", query = "SELECT c FROM ContactCustomerLinkT c")
public class ContactCustomerLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "contact_customer_link_id")
	private String contactCustomerLinkId;

	@Column(name = "created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	// bi-directional many-to-one association to ContactT
	@ManyToOne
	@JoinColumn(name = "contact_id", insertable = false, updatable = false)
	private ContactT contactT;

	@Column(name = "contact_id")
	private String contactId;

	@Column(name = "customer_id")
	private String customerId;

	@Column(name = "created_modified_by")
	private String createdModifiedBy;

	// bi-directional many-to-one association to CustomerMasterT
	@ManyToOne
	@JoinColumn(name = "customer_id", insertable = false, updatable = false)
	private CustomerMasterT customerMasterT;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "created_modified_by", insertable = false, updatable = false)
	private UserT userT;

	public ContactCustomerLinkT() {
	}

	public String getContactCustomerLinkId() {
		return this.contactCustomerLinkId;
	}

	public void setContactCustomerLinkId(String contactCustomerLinkId) {
		this.contactCustomerLinkId = contactCustomerLinkId;
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

	public CustomerMasterT getCustomerMasterT() {
		return this.customerMasterT;
	}

	public void setCustomerMasterT(CustomerMasterT customerMasterT) {
		this.customerMasterT = customerMasterT;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCreatedModifiedBy() {
		return createdModifiedBy;
	}

	public void setCreatedModifiedBy(String createdModifiedBy) {
		this.createdModifiedBy = createdModifiedBy;
	}
}