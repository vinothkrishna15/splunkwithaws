package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the audit_task_t database table.
 * 
 */
@Entity
@Table(name="audit_task_t")
@NamedQuery(name="AuditTaskT.findAll", query="SELECT a FROM AuditTaskT a")
public class AuditTaskT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_task_id")
	private Long auditTaskId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="new_collaboration_preference")
	private String newCollaborationPreference;

	@Column(name="new_documents_attached")
	private String newDocumentsAttached;

	@Temporal(TemporalType.DATE)
	@Column(name="new_target_date_for_completion")
	private Date newTargetDateForCompletion;

	@Column(name="new_task_description")
	private String newTaskDescription;

	@Column(name="new_task_owner")
	private String newTaskOwner;

	@Column(name="new_task_status")
	private String newTaskStatus;

	@Column(name="new_type")
	private String newType;

	private Boolean notified;

	@Column(name="old_collaboration_preference")
	private String oldCollaborationPreference;

	@Column(name="old_connect_id")
	private String oldConnectId;

	@Column(name="old_documents_attached")
	private String oldDocumentsAttached;

	@Column(name="old_entity_reference")
	private String oldEntityReference;

	@Column(name="old_opportunity_id")
	private String oldOpportunityId;

	@Temporal(TemporalType.DATE)
	@Column(name="old_target_date_for_completion")
	private Date oldTargetDateForCompletion;

	@Column(name="old_task_description")
	private String oldTaskDescription;

	@Column(name="old_task_owner")
	private String oldTaskOwner;

	@Column(name="old_task_status")
	private String oldTaskStatus;

	@Column(name="old_type")
	private String oldType;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="task_id")
	private String taskId;

	public AuditTaskT() {
	}

	public Long getAuditTaskId() {
		return this.auditTaskId;
	}

	public void setAuditTaskId(Long auditTaskId) {
		this.auditTaskId = auditTaskId;
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

	public String getNewCollaborationPreference() {
		return this.newCollaborationPreference;
	}

	public void setNewCollaborationPreference(String newCollaborationPreference) {
		this.newCollaborationPreference = newCollaborationPreference;
	}

	public String getNewDocumentsAttached() {
		return this.newDocumentsAttached;
	}

	public void setNewDocumentsAttached(String newDocumentsAttached) {
		this.newDocumentsAttached = newDocumentsAttached;
	}

	public Date getNewTargetDateForCompletion() {
		return this.newTargetDateForCompletion;
	}

	public void setNewTargetDateForCompletion(Date newTargetDateForCompletion) {
		this.newTargetDateForCompletion = newTargetDateForCompletion;
	}

	public String getNewTaskDescription() {
		return this.newTaskDescription;
	}

	public void setNewTaskDescription(String newTaskDescription) {
		this.newTaskDescription = newTaskDescription;
	}

	public String getNewTaskOwner() {
		return this.newTaskOwner;
	}

	public void setNewTaskOwner(String newTaskOwner) {
		this.newTaskOwner = newTaskOwner;
	}

	public String getNewTaskStatus() {
		return this.newTaskStatus;
	}

	public void setNewTaskStatus(String newTaskStatus) {
		this.newTaskStatus = newTaskStatus;
	}

	public String getNewType() {
		return this.newType;
	}

	public void setNewType(String newType) {
		this.newType = newType;
	}

	public Boolean getNotified() {
		return this.notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public String getOldCollaborationPreference() {
		return this.oldCollaborationPreference;
	}

	public void setOldCollaborationPreference(String oldCollaborationPreference) {
		this.oldCollaborationPreference = oldCollaborationPreference;
	}

	public String getOldConnectId() {
		return this.oldConnectId;
	}

	public void setOldConnectId(String oldConnectId) {
		this.oldConnectId = oldConnectId;
	}

	public String getOldDocumentsAttached() {
		return this.oldDocumentsAttached;
	}

	public void setOldDocumentsAttached(String oldDocumentsAttached) {
		this.oldDocumentsAttached = oldDocumentsAttached;
	}

	public String getOldEntityReference() {
		return this.oldEntityReference;
	}

	public void setOldEntityReference(String oldEntityReference) {
		this.oldEntityReference = oldEntityReference;
	}

	public String getOldOpportunityId() {
		return this.oldOpportunityId;
	}

	public void setOldOpportunityId(String oldOpportunityId) {
		this.oldOpportunityId = oldOpportunityId;
	}

	public Date getOldTargetDateForCompletion() {
		return this.oldTargetDateForCompletion;
	}

	public void setOldTargetDateForCompletion(Date oldTargetDateForCompletion) {
		this.oldTargetDateForCompletion = oldTargetDateForCompletion;
	}

	public String getOldTaskDescription() {
		return this.oldTaskDescription;
	}

	public void setOldTaskDescription(String oldTaskDescription) {
		this.oldTaskDescription = oldTaskDescription;
	}

	public String getOldTaskOwner() {
		return this.oldTaskOwner;
	}

	public void setOldTaskOwner(String oldTaskOwner) {
		this.oldTaskOwner = oldTaskOwner;
	}

	public String getOldTaskStatus() {
		return this.oldTaskStatus;
	}

	public void setOldTaskStatus(String oldTaskStatus) {
		this.oldTaskStatus = oldTaskStatus;
	}

	public String getOldType() {
		return this.oldType;
	}

	public void setOldType(String oldType) {
		this.oldType = oldType;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public String getTaskId() {
		return this.taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}