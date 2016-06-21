package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_task_bdms_tagged_link_t database table.
 * 
 */
@Entity
@Table(name="audit_task_bdms_tagged_link_t")
@NamedQuery(name="AuditTaskBdmsTaggedLinkT.findAll", query="SELECT a FROM AuditTaskBdmsTaggedLinkT a")
public class AuditTaskBdmsTaggedLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_task_bdms_tagged_link_id")
	private Long auditTaskBdmsTaggedLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	private Boolean notified;

	@Column(name="old_bdms_tagged")
	private String oldBdmsTagged;

	@Column(name="old_task_id")
	private String oldTaskId;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="task_bdms_tagged_link_id")
	private String taskBdmsTaggedLinkId;

	public AuditTaskBdmsTaggedLinkT() {
	}

	public Long getAuditTaskBdmsTaggedLinkId() {
		return this.auditTaskBdmsTaggedLinkId;
	}

	public void setAuditTaskBdmsTaggedLinkId(Long auditTaskBdmsTaggedLinkId) {
		this.auditTaskBdmsTaggedLinkId = auditTaskBdmsTaggedLinkId;
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

	public String getOldBdmsTagged() {
		return this.oldBdmsTagged;
	}

	public void setOldBdmsTagged(String oldBdmsTagged) {
		this.oldBdmsTagged = oldBdmsTagged;
	}

	public String getOldTaskId() {
		return this.oldTaskId;
	}

	public void setOldTaskId(String oldTaskId) {
		this.oldTaskId = oldTaskId;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public String getTaskBdmsTaggedLinkId() {
		return this.taskBdmsTaggedLinkId;
	}

	public void setTaskBdmsTaggedLinkId(String taskBdmsTaggedLinkId) {
		this.taskBdmsTaggedLinkId = taskBdmsTaggedLinkId;
	}

}