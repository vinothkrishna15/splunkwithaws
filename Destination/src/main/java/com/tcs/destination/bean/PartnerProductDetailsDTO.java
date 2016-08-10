package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the audit_bid_details_t database table.
 * 
 */
public class PartnerProductDetailsDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Transient
	private ContactT partnerProductContact;
	
	@Transient
	private List<Integer> subspList;
	
	@Transient
	private String productId;

	public ContactT getPartnerProductContact() {
		return partnerProductContact;
	}

	public void setPartnerProductContact(ContactT partnerProductContact) {
		this.partnerProductContact = partnerProductContact;
	}

	public List<Integer> getSubspList() {
		return subspList;
	}

	public void setSubspList(List<Integer> subspList) {
		this.subspList = subspList;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
}