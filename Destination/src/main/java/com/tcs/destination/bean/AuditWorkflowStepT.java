package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_workflow_step_t database table.
 * 
 */
@Entity
@Table(name="audit_workflow_step_t")
@NamedQuery(name="AuditWorkflowStepT.findAll", query="SELECT a FROM AuditWorkflowStepT a")
public class AuditWorkflowStepT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_workflow_step_id")
	private Long auditWorkflowStepId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="new_comments")
	private String newComments;

	@Column(name="new_step_status")
	private String newStepStatus;

	@Column(name="new_user_id")
	private String newUserId;

	private Boolean notified;

	@Column(name="old_request_id")
	private Integer oldRequestId;

	@Column(name="old_step")
	private Integer oldStep;

	@Column(name="old_step_status")
	private String oldStepStatus;

	@Column(name="old_user_group")
	private String oldUserGroup;

	@Column(name="old_user_role")
	private String oldUserRole;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="step_id")
	private Integer stepId;

	public AuditWorkflowStepT() {
	}

	public Long getAuditWorkflowStepId() {
		return this.auditWorkflowStepId;
	}

	public void setAuditWorkflowStepId(Long auditWorkflowStepId) {
		this.auditWorkflowStepId = auditWorkflowStepId;
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

	public String getNewComments() {
		return this.newComments;
	}

	public void setNewComments(String newComments) {
		this.newComments = newComments;
	}

	public String getNewStepStatus() {
		return this.newStepStatus;
	}

	public void setNewStepStatus(String newStepStatus) {
		this.newStepStatus = newStepStatus;
	}

	public String getNewUserId() {
		return this.newUserId;
	}

	public void setNewUserId(String newUserId) {
		this.newUserId = newUserId;
	}

	public Boolean getNotified() {
		return this.notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public Integer getOldRequestId() {
		return this.oldRequestId;
	}

	public void setOldRequestId(Integer oldRequestId) {
		this.oldRequestId = oldRequestId;
	}

	public Integer getOldStep() {
		return this.oldStep;
	}

	public void setOldStep(Integer oldStep) {
		this.oldStep = oldStep;
	}

	public String getOldStepStatus() {
		return this.oldStepStatus;
	}

	public void setOldStepStatus(String oldStepStatus) {
		this.oldStepStatus = oldStepStatus;
	}

	public String getOldUserGroup() {
		return this.oldUserGroup;
	}

	public void setOldUserGroup(String oldUserGroup) {
		this.oldUserGroup = oldUserGroup;
	}

	public String getOldUserRole() {
		return this.oldUserRole;
	}

	public void setOldUserRole(String oldUserRole) {
		this.oldUserRole = oldUserRole;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public Integer getStepId() {
		return this.stepId;
	}

	public void setStepId(Integer stepId) {
		this.stepId = stepId;
	}

}