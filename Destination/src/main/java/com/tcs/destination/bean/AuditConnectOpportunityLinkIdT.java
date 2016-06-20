package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_connect_opportunity_link_id_t database table.
 * 
 */
@Entity
@Table(name="audit_connect_opportunity_link_id_t")
@NamedQuery(name="AuditConnectOpportunityLinkIdT.findAll", query="SELECT a FROM AuditConnectOpportunityLinkIdT a")
public class AuditConnectOpportunityLinkIdT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_connect_opportunity_link_id")
	private Long auditConnectOpportunityLinkId;

	@Column(name="connect_opportunity_link_id")
	private String connectOpportunityLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	private Boolean notified;

	@Column(name="old_connect_id")
	private String oldConnectId;

	@Column(name="old_opportunity_id")
	private String oldOpportunityId;

	@Column(name="operation_type")
	private Integer operationType;

	public AuditConnectOpportunityLinkIdT() {
	}

	public Long getAuditConnectOpportunityLinkId() {
		return this.auditConnectOpportunityLinkId;
	}

	public void setAuditConnectOpportunityLinkId(Long auditConnectOpportunityLinkId) {
		this.auditConnectOpportunityLinkId = auditConnectOpportunityLinkId;
	}

	public String getConnectOpportunityLinkId() {
		return this.connectOpportunityLinkId;
	}

	public void setConnectOpportunityLinkId(String connectOpportunityLinkId) {
		this.connectOpportunityLinkId = connectOpportunityLinkId;
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

	public String getOldConnectId() {
		return this.oldConnectId;
	}

	public void setOldConnectId(String oldConnectId) {
		this.oldConnectId = oldConnectId;
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

}