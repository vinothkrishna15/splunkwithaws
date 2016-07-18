package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_workflow_request_t database table.
 * 
 */
@Entity
@Table(name="audit_workflow_request_t")
@NamedQuery(name="AuditWorkflowRequestT.findAll", query="SELECT a FROM AuditWorkflowRequestT a")
public class AuditWorkflowRequestT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_workflow_request_id")
	private Long auditWorkflowRequestId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="new_status")
	private String newStatus;

	private Boolean notified;

	@Column(name="old_entity_id")
	private String oldEntityId;

	@Column(name="old_entity_type_id")
	private Integer oldEntityTypeId;

	@Column(name="old_status")
	private String oldStatus;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="request_id")
	private Integer requestId;

	public AuditWorkflowRequestT() {
	}

	public Long getAuditWorkflowRequestId() {
		return this.auditWorkflowRequestId;
	}

	public void setAuditWorkflowRequestId(Long auditWorkflowRequestId) {
		this.auditWorkflowRequestId = auditWorkflowRequestId;
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

	public String getNewStatus() {
		return this.newStatus;
	}

	public void setNewStatus(String newStatus) {
		this.newStatus = newStatus;
	}

	public Boolean getNotified() {
		return this.notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public String getOldEntityId() {
		return this.oldEntityId;
	}

	public void setOldEntityId(String oldEntityId) {
		this.oldEntityId = oldEntityId;
	}

	public Integer getOldEntityTypeId() {
		return this.oldEntityTypeId;
	}

	public void setOldEntityTypeId(Integer oldEntityTypeId) {
		this.oldEntityTypeId = oldEntityTypeId;
	}

	public String getOldStatus() {
		return this.oldStatus;
	}

	public void setOldStatus(String oldStatus) {
		this.oldStatus = oldStatus;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public Integer getRequestId() {
		return this.requestId;
	}

	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}

}