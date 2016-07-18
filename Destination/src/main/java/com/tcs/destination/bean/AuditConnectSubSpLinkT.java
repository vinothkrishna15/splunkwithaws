package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_connect_sub_sp_link_t database table.
 * 
 */
@Entity
@Table(name="audit_connect_sub_sp_link_t")
@NamedQuery(name="AuditConnectSubSpLinkT.findAll", query="SELECT a FROM AuditConnectSubSpLinkT a")
public class AuditConnectSubSpLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_connect_sub_sp_link_id")
	private Long auditConnectSubSpLinkId;

	@Column(name="connect_sub_sp_link_id")
	private String connectSubSpLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	private Boolean notified;

	@Column(name="old_connect_id")
	private String oldConnectId;

	@Column(name="old_sub_sp")
	private String oldSubSp;

	@Column(name="operation_type")
	private Integer operationType;

	public AuditConnectSubSpLinkT() {
	}

	public Long getAuditConnectSubSpLinkId() {
		return this.auditConnectSubSpLinkId;
	}

	public void setAuditConnectSubSpLinkId(Long auditConnectSubSpLinkId) {
		this.auditConnectSubSpLinkId = auditConnectSubSpLinkId;
	}

	public String getConnectSubSpLinkId() {
		return this.connectSubSpLinkId;
	}

	public void setConnectSubSpLinkId(String connectSubSpLinkId) {
		this.connectSubSpLinkId = connectSubSpLinkId;
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

	public String getOldSubSp() {
		return this.oldSubSp;
	}

	public void setOldSubSp(String oldSubSp) {
		this.oldSubSp = oldSubSp;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

}