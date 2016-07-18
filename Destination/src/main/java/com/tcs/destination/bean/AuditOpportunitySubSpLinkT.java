package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_opportunity_sub_sp_link_t database table.
 * 
 */
@Entity
@Table(name="audit_opportunity_sub_sp_link_t")
@NamedQuery(name="AuditOpportunitySubSpLinkT.findAll", query="SELECT a FROM AuditOpportunitySubSpLinkT a")
public class AuditOpportunitySubSpLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_opportunity_sub_sp_link_id")
	private Long auditOpportunitySubSpLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="new_subsp_primary")
	private Boolean newSubspPrimary;

	private Boolean notified;

	@Column(name="old_opportunity_id")
	private String oldOpportunityId;

	@Column(name="old_sub_sp")
	private String oldSubSp;

	@Column(name="old_subsp_primary")
	private Boolean oldSubspPrimary;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="opportunity_sub_sp_link_id")
	private String opportunitySubSpLinkId;

	public AuditOpportunitySubSpLinkT() {
	}

	public Long getAuditOpportunitySubSpLinkId() {
		return this.auditOpportunitySubSpLinkId;
	}

	public void setAuditOpportunitySubSpLinkId(Long auditOpportunitySubSpLinkId) {
		this.auditOpportunitySubSpLinkId = auditOpportunitySubSpLinkId;
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

	public Boolean getNewSubspPrimary() {
		return this.newSubspPrimary;
	}

	public void setNewSubspPrimary(Boolean newSubspPrimary) {
		this.newSubspPrimary = newSubspPrimary;
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

	public String getOldSubSp() {
		return this.oldSubSp;
	}

	public void setOldSubSp(String oldSubSp) {
		this.oldSubSp = oldSubSp;
	}

	public Boolean getOldSubspPrimary() {
		return this.oldSubspPrimary;
	}

	public void setOldSubspPrimary(Boolean oldSubspPrimary) {
		this.oldSubspPrimary = oldSubspPrimary;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public String getOpportunitySubSpLinkId() {
		return this.opportunitySubSpLinkId;
	}

	public void setOpportunitySubSpLinkId(String opportunitySubSpLinkId) {
		this.opportunitySubSpLinkId = opportunitySubSpLinkId;
	}

}