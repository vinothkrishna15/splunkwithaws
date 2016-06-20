package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_workflow_customer_t database table.
 * 
 */
@Entity
@Table(name="audit_workflow_customer_t")
@NamedQuery(name="AuditWorkflowCustomerT.findAll", query="SELECT a FROM AuditWorkflowCustomerT a")
public class AuditWorkflowCustomerT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_workflow_customer_id")
	private Long auditWorkflowCustomerId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="new_corporate_hq_address")
	private String newCorporateHqAddress;

	@Column(name="new_customer_name")
	private String newCustomerName;

	@Column(name="new_documents_attached")
	private String newDocumentsAttached;

	@Column(name="new_facebook")
	private String newFacebook;

	@Column(name="new_geography")
	private String newGeography;

	@Column(name="new_group_customer_name")
	private String newGroupCustomerName;

	@Column(name="new_iou")
	private String newIou;

	@Column(name="new_logo")
	private byte[] newLogo;

	@Column(name="new_remarks")
	private String newRemarks;

	@Column(name="new_website")
	private String newWebsite;

	private Boolean notified;

	@Column(name="old_corporate_hq_address")
	private String oldCorporateHqAddress;

	@Column(name="old_customer_name")
	private String oldCustomerName;

	@Column(name="old_documents_attached")
	private String oldDocumentsAttached;

	@Column(name="old_facebook")
	private String oldFacebook;

	@Column(name="old_geography")
	private String oldGeography;

	@Column(name="old_group_customer_name")
	private String oldGroupCustomerName;

	@Column(name="old_iou")
	private String oldIou;

	@Column(name="old_logo")
	private byte[] oldLogo;

	@Column(name="old_remarks")
	private String oldRemarks;

	@Column(name="old_website")
	private String oldWebsite;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="workflow_customer_id")
	private String workflowCustomerId;

	public AuditWorkflowCustomerT() {
	}

	public Long getAuditWorkflowCustomerId() {
		return this.auditWorkflowCustomerId;
	}

	public void setAuditWorkflowCustomerId(Long auditWorkflowCustomerId) {
		this.auditWorkflowCustomerId = auditWorkflowCustomerId;
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

	public String getNewCustomerName() {
		return this.newCustomerName;
	}

	public void setNewCustomerName(String newCustomerName) {
		this.newCustomerName = newCustomerName;
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

	public String getNewGroupCustomerName() {
		return this.newGroupCustomerName;
	}

	public void setNewGroupCustomerName(String newGroupCustomerName) {
		this.newGroupCustomerName = newGroupCustomerName;
	}

	public String getNewIou() {
		return this.newIou;
	}

	public void setNewIou(String newIou) {
		this.newIou = newIou;
	}

	public byte[] getNewLogo() {
		return this.newLogo;
	}

	public void setNewLogo(byte[] newLogo) {
		this.newLogo = newLogo;
	}

	public String getNewRemarks() {
		return this.newRemarks;
	}

	public void setNewRemarks(String newRemarks) {
		this.newRemarks = newRemarks;
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

	public String getOldCustomerName() {
		return this.oldCustomerName;
	}

	public void setOldCustomerName(String oldCustomerName) {
		this.oldCustomerName = oldCustomerName;
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

	public String getOldGroupCustomerName() {
		return this.oldGroupCustomerName;
	}

	public void setOldGroupCustomerName(String oldGroupCustomerName) {
		this.oldGroupCustomerName = oldGroupCustomerName;
	}

	public String getOldIou() {
		return this.oldIou;
	}

	public void setOldIou(String oldIou) {
		this.oldIou = oldIou;
	}

	public byte[] getOldLogo() {
		return this.oldLogo;
	}

	public void setOldLogo(byte[] oldLogo) {
		this.oldLogo = oldLogo;
	}

	public String getOldRemarks() {
		return this.oldRemarks;
	}

	public void setOldRemarks(String oldRemarks) {
		this.oldRemarks = oldRemarks;
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

	public String getWorkflowCustomerId() {
		return this.workflowCustomerId;
	}

	public void setWorkflowCustomerId(String workflowCustomerId) {
		this.workflowCustomerId = workflowCustomerId;
	}

}