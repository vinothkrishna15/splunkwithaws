package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_workflow_partner_t database table.
 * 
 */
@Entity
@Table(name="audit_workflow_partner_t")
@NamedQuery(name="AuditWorkflowPartnerT.findAll", query="SELECT a FROM AuditWorkflowPartnerT a")
public class AuditWorkflowPartnerT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_workflow_partner_id")
	private Long auditWorkflowPartnerId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

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

	@Column(name="workflow_partner_id")
	private String workflowPartnerId;

	public AuditWorkflowPartnerT() {
	}

	public Long getAuditWorkflowPartnerId() {
		return this.auditWorkflowPartnerId;
	}

	public void setAuditWorkflowPartnerId(Long auditWorkflowPartnerId) {
		this.auditWorkflowPartnerId = auditWorkflowPartnerId;
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

	public String getWorkflowPartnerId() {
		return this.workflowPartnerId;
	}

	public void setWorkflowPartnerId(String workflowPartnerId) {
		this.workflowPartnerId = workflowPartnerId;
	}

}