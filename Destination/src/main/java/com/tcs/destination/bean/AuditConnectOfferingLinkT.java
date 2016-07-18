package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_connect_offering_link_t database table.
 * 
 */
@Entity
@Table(name="audit_connect_offering_link_t")
@NamedQuery(name="AuditConnectOfferingLinkT.findAll", query="SELECT a FROM AuditConnectOfferingLinkT a")
public class AuditConnectOfferingLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_connect_offering_link_id")
	private Long auditConnectOfferingLinkId;

	@Column(name="connect_offering_link_id")
	private String connectOfferingLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	private Boolean notified;

	@Column(name="old_connect_id")
	private String oldConnectId;

	@Column(name="old_offering")
	private String oldOffering;

	@Column(name="operation_type")
	private Integer operationType;

	public AuditConnectOfferingLinkT() {
	}

	public Long getAuditConnectOfferingLinkId() {
		return this.auditConnectOfferingLinkId;
	}

	public void setAuditConnectOfferingLinkId(Long auditConnectOfferingLinkId) {
		this.auditConnectOfferingLinkId = auditConnectOfferingLinkId;
	}

	public String getConnectOfferingLinkId() {
		return this.connectOfferingLinkId;
	}

	public void setConnectOfferingLinkId(String connectOfferingLinkId) {
		this.connectOfferingLinkId = connectOfferingLinkId;
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

	public String getOldOffering() {
		return this.oldOffering;
	}

	public void setOldOffering(String oldOffering) {
		this.oldOffering = oldOffering;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

}