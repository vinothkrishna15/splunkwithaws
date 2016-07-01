package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the partner_contact_link_t database table.
 * 
 */
@Entity
@Table(name="partner_contact_link_t")
@NamedQuery(name="PartnerContactLinkT.findAll", query="SELECT p FROM PartnerContactLinkT p")
public class PartnerContactLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="partner_contact_id")
	private String partnerContactId;

	@Column(name="contact_id")
	private String contactId;

	@Column(name="created_by")
	private String createdBy;
	
	@ManyToOne
	@JoinColumn(name = "created_by", insertable = false, updatable = false)
	private UserT createdByUser;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="modified_by")
	private String modifiedBy;
	
	@ManyToOne
	@JoinColumn(name = "modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;

	@Column(name="partner_id")
	private String partnerId;
	
	@ManyToOne
	@JoinColumn(name="contact_id", insertable = false, updatable = false)
	private ContactT contactT;
	
	@ManyToOne
	@JoinColumn(name="partner_id", insertable = false, updatable = false)
	private PartnerMasterT partnerMasterT;
	

	public PartnerContactLinkT() {
	}

	public String getPartnerContactId() {
		return this.partnerContactId;
	}

	public void setPartnerContactId(String partnerContactId) {
		this.partnerContactId = partnerContactId;
	}

	public String getContactId() {
		return this.contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
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

	public String getPartnerId() {
		return this.partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	
	public PartnerMasterT getPartnerMasterT() {
		return this.partnerMasterT;
	}

	public void setPartnerMasterT(PartnerMasterT partnerMasterT) {
		this.partnerMasterT = partnerMasterT;
	}

	public ContactT getContactT() {
		return contactT;
	}

	public void setContactT(ContactT contactT) {
		this.contactT = contactT;
	}

	public UserT getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserT createdByUser) {
		this.createdByUser = createdByUser;
	}

	public UserT getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}

}