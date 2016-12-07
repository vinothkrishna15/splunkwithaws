package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The ContactCustomerLinkDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class ContactCustomerLinkDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String contactCustomerLinkId;
	private Timestamp createdModifiedDatetime;
	private ContactDTO contactT;
	private String contactId;
	private String customerId;
	private String createdModifiedBy;
	private CustomerMasterDTO customerMasterT;

	//created_modified_by
	private UserDTO userT;

	public ContactCustomerLinkDTO() {
		super();
	}

	public String getContactCustomerLinkId() {
		return contactCustomerLinkId;
	}

	public void setContactCustomerLinkId(String contactCustomerLinkId) {
		this.contactCustomerLinkId = contactCustomerLinkId;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
	}

	public ContactDTO getContactT() {
		return contactT;
	}

	public void setContactT(ContactDTO contactT) {
		this.contactT = contactT;
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

	public CustomerMasterDTO getCustomerMasterT() {
		return customerMasterT;
	}

	public void setCustomerMasterT(CustomerMasterDTO customerMasterT) {
		this.customerMasterT = customerMasterT;
	}

	public UserDTO getUserT() {
		return userT;
	}

	public void setUserT(UserDTO userT) {
		this.userT = userT;
	}
	
}