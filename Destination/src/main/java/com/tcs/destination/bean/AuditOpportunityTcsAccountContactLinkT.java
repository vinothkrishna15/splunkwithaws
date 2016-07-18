package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_opportunity_tcs_account_contact_link_t database table.
 * 
 */
@Entity
@Table(name="audit_opportunity_tcs_account_contact_link_t")
@NamedQuery(name="AuditOpportunityTcsAccountContactLinkT.findAll", query="SELECT a FROM AuditOpportunityTcsAccountContactLinkT a")
public class AuditOpportunityTcsAccountContactLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_opportunity_tcs_account_contact_link_id")
	private Long auditOpportunityTcsAccountContactLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	private Boolean notified;

	@Column(name="old_contact_id")
	private String oldContactId;

	@Column(name="old_opportunity_id")
	private String oldOpportunityId;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="opportunity_tcs_account_contact_link_id")
	private String opportunityTcsAccountContactLinkId;

	public AuditOpportunityTcsAccountContactLinkT() {
	}

	public Long getAuditOpportunityTcsAccountContactLinkId() {
		return this.auditOpportunityTcsAccountContactLinkId;
	}

	public void setAuditOpportunityTcsAccountContactLinkId(Long auditOpportunityTcsAccountContactLinkId) {
		this.auditOpportunityTcsAccountContactLinkId = auditOpportunityTcsAccountContactLinkId;
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

	public Boolean getNotified() {
		return this.notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public String getOldContactId() {
		return this.oldContactId;
	}

	public void setOldContactId(String oldContactId) {
		this.oldContactId = oldContactId;
	}

	public String getOldOpportunityId() {
		return this.oldOpportunityId;
	}

	public void setOldOpportunityId(String oldOpportunityId) {
		this.oldOpportunityId = oldOpportunityId;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public String getOpportunityTcsAccountContactLinkId() {
		return this.opportunityTcsAccountContactLinkId;
	}

	public void setOpportunityTcsAccountContactLinkId(String opportunityTcsAccountContactLinkId) {
		this.opportunityTcsAccountContactLinkId = opportunityTcsAccountContactLinkId;
	}

}