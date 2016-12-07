package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * The ProductContactLinkDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class ProductContactLinkDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String productContactId;
	private Timestamp createdDatetime;
	private Timestamp modifiedDatetime;
	private String contactId;
	private ContactDTO contactT;

	private String productId;
	
	private UserDTO createdByUser;
	private UserDTO modifiedByUser;

	public ProductContactLinkDTO() {
		super();
	}

	public String getProductContactId() {
		return productContactId;
	}

	public void setProductContactId(String productContactId) {
		this.productContactId = productContactId;
	}

	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public Timestamp getModifiedDatetime() {
		return modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public ContactDTO getContactT() {
		return contactT;
	}

	public void setContactT(ContactDTO contactT) {
		this.contactT = contactT;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public UserDTO getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserDTO createdByUser) {
		this.createdByUser = createdByUser;
	}

	public UserDTO getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserDTO modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}

}