package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_partner_master_t database table.
 * 
 */
@Entity
@Table(name="audit_partner_master_t")
@NamedQuery(name="AuditPartnerMasterT.findAll", query="SELECT a FROM AuditPartnerMasterT a")
public class AuditPartnerMasterT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_partner_id")
	private Long auditPartnerId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="new_active")
	private Boolean newActive;

	@Column(name="new_corporate_hq_address")
	private String newCorporateHqAddress;

	@Column(name="new_documents_attached")
	private String newDocumentsAttached;

	@Column(name="new_facebook")
	private String newFacebook;

	@Column(name="new_geography")
	private String newGeography;

	@Column(name="new_logo")
	private byte[] newLogo;

	@Column(name="new_notes")
	private String newNotes;

	@Column(name="new_partner_name")
	private String newPartnerName;

	@Column(name="new_website")
	private String newWebsite;

	private Boolean notified;

	@Column(name="old_active")
	private Boolean oldActive;

	@Column(name="old_corporate_hq_address")
	private String oldCorporateHqAddress;

	@Column(name="old_documents_attached")
	private String oldDocumentsAttached;

	@Column(name="old_facebook")
	private String oldFacebook;

	@Column(name="old_geography")
	private String oldGeography;

	@Column(name="old_logo")
	private byte[] oldLogo;

	@Column(name="old_notes")
	private String oldNotes;

	@Column(name="old_partner_name")
	private String oldPartnerName;

	@Column(name="old_website")
	private String oldWebsite;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="partner_id")
	private String partnerId;
	
	//Added for partner changes
	@Column(name="old_group_partner_name")
	private String oldGroupPartnerName;
	
	@Column(name="new_group_partner_name")
	private String newGroupPartnerName;
	
	@Column(name="old_country")
	private String oldCountry;
	
	@Column(name="new_country")
	private String newCountry;
	
	@Column(name="old_city")
	private String oldCity;
	
	@Column(name="new_city")
	private String newCity;
	
	@Column(name="old_text1")
	private String oldText1;
	
	@Column(name="new_text1")
	private String newText1;
	
	@Column(name="old_text2")
	private String oldText2;
	
	@Column(name="new_text2")
	private String newText2;
	
	@Column(name="old_text3")
	private String oldText3;
	
	@Column(name="new_text3")
	private String newText3;
	
	@Column(name="old_hq_partner_link_id")
	private String oldHqPartnerLinkId;
	
	@Column(name="new_hq_partner_link_id")
	private String newHqPartnerLinkId;

	public AuditPartnerMasterT() {
	}

	public Long getAuditPartnerId() {
		return this.auditPartnerId;
	}

	public void setAuditPartnerId(Long auditPartnerId) {
		this.auditPartnerId = auditPartnerId;
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

	public Boolean getNewActive() {
		return this.newActive;
	}

	public void setNewActive(Boolean newActive) {
		this.newActive = newActive;
	}

	public String getNewCorporateHqAddress() {
		return this.newCorporateHqAddress;
	}

	public void setNewCorporateHqAddress(String newCorporateHqAddress) {
		this.newCorporateHqAddress = newCorporateHqAddress;
	}

	public String getNewDocumentsAttached() {
		return this.newDocumentsAttached;
	}

	public void setNewDocumentsAttached(String newDocumentsAttached) {
		this.newDocumentsAttached = newDocumentsAttached;
	}

	public String getNewFacebook() {
		return this.newFacebook;
	}

	public void setNewFacebook(String newFacebook) {
		this.newFacebook = newFacebook;
	}

	public String getNewGeography() {
		return this.newGeography;
	}

	public void setNewGeography(String newGeography) {
		this.newGeography = newGeography;
	}

	public byte[] getNewLogo() {
		return this.newLogo;
	}

	public void setNewLogo(byte[] newLogo) {
		this.newLogo = newLogo;
	}

	public String getNewNotes() {
		return this.newNotes;
	}

	public void setNewNotes(String newNotes) {
		this.newNotes = newNotes;
	}

	public String getNewPartnerName() {
		return this.newPartnerName;
	}

	public void setNewPartnerName(String newPartnerName) {
		this.newPartnerName = newPartnerName;
	}

	public String getNewWebsite() {
		return this.newWebsite;
	}

	public void setNewWebsite(String newWebsite) {
		this.newWebsite = newWebsite;
	}

	public Boolean getNotified() {
		return this.notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public Boolean getOldActive() {
		return this.oldActive;
	}

	public void setOldActive(Boolean oldActive) {
		this.oldActive = oldActive;
	}

	public String getOldCorporateHqAddress() {
		return this.oldCorporateHqAddress;
	}

	public void setOldCorporateHqAddress(String oldCorporateHqAddress) {
		this.oldCorporateHqAddress = oldCorporateHqAddress;
	}

	public String getOldDocumentsAttached() {
		return this.oldDocumentsAttached;
	}

	public void setOldDocumentsAttached(String oldDocumentsAttached) {
		this.oldDocumentsAttached = oldDocumentsAttached;
	}

	public String getOldFacebook() {
		return this.oldFacebook;
	}

	public void setOldFacebook(String oldFacebook) {
		this.oldFacebook = oldFacebook;
	}

	public String getOldGeography() {
		return this.oldGeography;
	}

	public void setOldGeography(String oldGeography) {
		this.oldGeography = oldGeography;
	}

	public byte[] getOldLogo() {
		return this.oldLogo;
	}

	public void setOldLogo(byte[] oldLogo) {
		this.oldLogo = oldLogo;
	}

	public String getOldNotes() {
		return this.oldNotes;
	}

	public void setOldNotes(String oldNotes) {
		this.oldNotes = oldNotes;
	}

	public String getOldPartnerName() {
		return this.oldPartnerName;
	}

	public void setOldPartnerName(String oldPartnerName) {
		this.oldPartnerName = oldPartnerName;
	}

	public String getOldWebsite() {
		return this.oldWebsite;
	}

	public void setOldWebsite(String oldWebsite) {
		this.oldWebsite = oldWebsite;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public String getPartnerId() {
		return this.partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	
	public String getOldGroupPartnerName() {
		return oldGroupPartnerName;
	}

	public void setOldGroupPartnerName(String oldGroupPartnerName) {
		this.oldGroupPartnerName = oldGroupPartnerName;
	}

	public String getNewGroupPartnerName() {
		return newGroupPartnerName;
	}

	public void setNewGroupPartnerName(String newGroupPartnerName) {
		this.newGroupPartnerName = newGroupPartnerName;
	}

	public String getOldCountry() {
		return oldCountry;
	}

	public void setOldCountry(String oldCountry) {
		this.oldCountry = oldCountry;
	}

	public String getNewCountry() {
		return newCountry;
	}

	public void setNewCountry(String newCountry) {
		this.newCountry = newCountry;
	}

	public String getOldCity() {
		return oldCity;
	}

	public void setOldCity(String oldCity) {
		this.oldCity = oldCity;
	}

	public String getNewCity() {
		return newCity;
	}

	public void setNewCity(String newCity) {
		this.newCity = newCity;
	}

	public String getOldText1() {
		return oldText1;
	}

	public void setOldText1(String oldText1) {
		this.oldText1 = oldText1;
	}

	public String getNewText1() {
		return newText1;
	}

	public void setNewText1(String newText1) {
		this.newText1 = newText1;
	}

	public String getOldText2() {
		return oldText2;
	}

	public void setOldText2(String oldText2) {
		this.oldText2 = oldText2;
	}

	public String getNewText2() {
		return newText2;
	}

	public void setNewText2(String newText2) {
		this.newText2 = newText2;
	}

	public String getOldText3() {
		return oldText3;
	}

	public void setOldText3(String oldText3) {
		this.oldText3 = oldText3;
	}

	public String getNewText3() {
		return newText3;
	}

	public void setNewText3(String newText3) {
		this.newText3 = newText3;
	}

	public String getOldHqPartnerLinkId() {
		return oldHqPartnerLinkId;
	}

	public void setOldHqPartnerLinkId(String oldHqPartnerLinkId) {
		this.oldHqPartnerLinkId = oldHqPartnerLinkId;
	}

	public String getNewHqPartnerLinkId() {
		return newHqPartnerLinkId;
	}

	public void setNewHqPartnerLinkId(String newHqPartnerLinkId) {
		this.newHqPartnerLinkId = newHqPartnerLinkId;
	}

}