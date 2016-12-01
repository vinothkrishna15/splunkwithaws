package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the opportunity_customer_contact_link_t database
 * table.
 * 
 */
@JsonFilter(Constants.FILTER)
public class OpportunityCustomerContactLinkDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String opportunityCustomerContactLinkId;
	private Timestamp createdDatetime;
	private UserDTO createdByUser;
	private Timestamp modifiedDatetime;
	private UserDTO modifiedByUser;

	private String contactId;
	private String opportunityId;
	private ContactDTO contactT;

	public OpportunityCustomerContactLinkDTO() {
		super();
	}

	public String getOpportunityCustomerContactLinkId() {
		return opportunityCustomerContactLinkId;
	}

	public void setOpportunityCustomerContactLinkId(
			String opportunityCustomerContactLinkId) {
		this.opportunityCustomerContactLinkId = opportunityCustomerContactLinkId;
	}

	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public UserDTO getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserDTO createdByUser) {
		this.createdByUser = createdByUser;
	}

	public Timestamp getModifiedDatetime() {
		return modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public UserDTO getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserDTO modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
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

	public ContactDTO getContactT() {
		return contactT;
	}

	public void setContactT(ContactDTO contactT) {
		this.contactT = contactT;
	}

}