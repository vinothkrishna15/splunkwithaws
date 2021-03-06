package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_connect_customer_contact_link_t database table.
 * 
 */
@Entity
@Table(name="audit_connect_customer_contact_link_t")
@NamedQuery(name="AuditConnectCustomerContactLinkT.findAll", query="SELECT a FROM AuditConnectCustomerContactLinkT a")
public class AuditConnectCustomerContactLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_connect_customer_contact_link_id")
	private Long auditConnectCustomerContactLinkId;

	@Column(name="connect_customer_contact_link_id")
	private String connectCustomerContactLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	private Boolean notified;

	@Column(name="old_connect_id")
	private String oldConnectId;

	@Column(name="old_contact_id")
	private String oldContactId;

	@Column(name="operation_type")
	private Integer operationType;

	public AuditConnectCustomerContactLinkT() {
	}

	public Long getAuditConnectCustomerContactLinkId() {
		return this.auditConnectCustomerContactLinkId;
	}

	public void setAuditConnectCustomerContactLinkId(Long auditConnectCustomerContactLinkId) {
		this.auditConnectCustomerContactLinkId = auditConnectCustomerContactLinkId;
	}

	public String getConnectCustomerContactLinkId() {
		return this.connectCustomerContactLinkId;
	}

	public void setConnectCustomerContactLinkId(String connectCustomerContactLinkId) {
		this.connectCustomerContactLinkId = connectCustomerContactLinkId;
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

	public String getOldContactId() {
		return this.oldContactId;
	}

	public void setOldContactId(String oldContactId) {
		this.oldContactId = oldContactId;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

}