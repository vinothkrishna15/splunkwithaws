package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_opportunity_partner_link_t database table.
 * 
 */
@Entity
@Table(name="audit_opportunity_partner_link_t")
@NamedQuery(name="AuditOpportunityPartnerLinkT.findAll", query="SELECT a FROM AuditOpportunityPartnerLinkT a")
public class AuditOpportunityPartnerLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_opportunity_partner_link_id")
	private Long auditOpportunityPartnerLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	private Boolean notified;

	@Column(name="old_opportunity_id")
	private String oldOpportunityId;

	@Column(name="old_partner_id")
	private String oldPartnerId;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="opportunity_partner_link_id")
	private String opportunityPartnerLinkId;

	public AuditOpportunityPartnerLinkT() {
	}

	public Long getAuditOpportunityPartnerLinkId() {
		return this.auditOpportunityPartnerLinkId;
	}

	public void setAuditOpportunityPartnerLinkId(Long auditOpportunityPartnerLinkId) {
		this.auditOpportunityPartnerLinkId = auditOpportunityPartnerLinkId;
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

	public String getOldOpportunityId() {
		return this.oldOpportunityId;
	}

	public void setOldOpportunityId(String oldOpportunityId) {
		this.oldOpportunityId = oldOpportunityId;
	}

	public String getOldPartnerId() {
		return this.oldPartnerId;
	}

	public void setOldPartnerId(String oldPartnerId) {
		this.oldPartnerId = oldPartnerId;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public String getOpportunityPartnerLinkId() {
		return this.opportunityPartnerLinkId;
	}

	public void setOpportunityPartnerLinkId(String opportunityPartnerLinkId) {
		this.opportunityPartnerLinkId = opportunityPartnerLinkId;
	}

}