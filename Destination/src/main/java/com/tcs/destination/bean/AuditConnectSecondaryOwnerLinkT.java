package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_connect_secondary_owner_link_t database table.
 * 
 */
@Entity
@Table(name="audit_connect_secondary_owner_link_t")
@NamedQuery(name="AuditConnectSecondaryOwnerLinkT.findAll", query="SELECT a FROM AuditConnectSecondaryOwnerLinkT a")
public class AuditConnectSecondaryOwnerLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_connect_secondary_owner_link_id")
	private Long auditConnectSecondaryOwnerLinkId;

	@Column(name="connect_secondary_owner_link_id")
	private String connectSecondaryOwnerLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	private Boolean notified;

	@Column(name="old_connect_id")
	private String oldConnectId;

	@Column(name="old_secondary_owner")
	private String oldSecondaryOwner;

	@Column(name="operation_type")
	private Integer operationType;

	public AuditConnectSecondaryOwnerLinkT() {
	}

	public Long getAuditConnectSecondaryOwnerLinkId() {
		return this.auditConnectSecondaryOwnerLinkId;
	}

	public void setAuditConnectSecondaryOwnerLinkId(Long auditConnectSecondaryOwnerLinkId) {
		this.auditConnectSecondaryOwnerLinkId = auditConnectSecondaryOwnerLinkId;
	}

	public String getConnectSecondaryOwnerLinkId() {
		return this.connectSecondaryOwnerLinkId;
	}

	public void setConnectSecondaryOwnerLinkId(String connectSecondaryOwnerLinkId) {
		this.connectSecondaryOwnerLinkId = connectSecondaryOwnerLinkId;
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

	public String getOldSecondaryOwner() {
		return this.oldSecondaryOwner;
	}

	public void setOldSecondaryOwner(String oldSecondaryOwner) {
		this.oldSecondaryOwner = oldSecondaryOwner;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

}