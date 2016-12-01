package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The PartnerContactLinkDTO.
 * 
 */
public class PartnerContactLinkDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String partnerContactId;

	private String contactId;
	private String partnerId;
	
	private ContactDTO contactT;
	private PartnerMasterDTO partnerMasterT;
	
	private UserDTO createdByUser;
	private Timestamp createdDatetime;
	private UserDTO modifiedByUser;
	private Timestamp modifiedDatetime;

	public PartnerContactLinkDTO() {
		super();
	}

	public String getPartnerContactId() {
		return partnerContactId;
	}

	public void setPartnerContactId(String partnerContactId) {
		this.partnerContactId = partnerContactId;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public ContactDTO getContactT() {
		return contactT;
	}

	public void setContactT(ContactDTO contactT) {
		this.contactT = contactT;
	}

	public PartnerMasterDTO getPartnerMasterT() {
		return partnerMasterT;
	}

	public void setPartnerMasterT(PartnerMasterDTO partnerMasterT) {
		this.partnerMasterT = partnerMasterT;
	}

	public UserDTO getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserDTO createdByUser) {
		this.createdByUser = createdByUser;
	}

	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public UserDTO getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserDTO modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}

	public Timestamp getModifiedDatetime() {
		return modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

}