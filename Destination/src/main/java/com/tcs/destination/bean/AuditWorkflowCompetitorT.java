package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_workflow_competitor_t database table.
 * 
 */
@Entity
@Table(name="audit_workflow_competitor_t")
@NamedQuery(name="AuditWorkflowCompetitorT.findAll", query="SELECT a FROM AuditWorkflowCompetitorT a")
public class AuditWorkflowCompetitorT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_workflow_competitor_id")
	private Long auditWorkflowCompetitorId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="new_workflow_competitor_name")
	private String newWorkflowCompetitorName;

	@Column(name="new_workflow_competitor_notes")
	private String newWorkflowCompetitorNotes;

	@Column(name="new_workflow_competitor_website")
	private String newWorkflowCompetitorWebsite;

	private Boolean notified;

	@Column(name="old_workflow_competitor_name")
	private String oldWorkflowCompetitorName;

	@Column(name="old_workflow_competitor_notes")
	private String oldWorkflowCompetitorNotes;

	@Column(name="old_workflow_competitor_website")
	private String oldWorkflowCompetitorWebsite;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="workflow_competitor_id")
	private String workflowCompetitorId;

	public AuditWorkflowCompetitorT() {
	}

	public Long getAuditWorkflowCompetitorId() {
		return this.auditWorkflowCompetitorId;
	}

	public void setAuditWorkflowCompetitorId(Long auditWorkflowCompetitorId) {
		this.auditWorkflowCompetitorId = auditWorkflowCompetitorId;
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

	public String getNewWorkflowCompetitorName() {
		return this.newWorkflowCompetitorName;
	}

	public void setNewWorkflowCompetitorName(String newWorkflowCompetitorName) {
		this.newWorkflowCompetitorName = newWorkflowCompetitorName;
	}

	public String getNewWorkflowCompetitorNotes() {
		return this.newWorkflowCompetitorNotes;
	}

	public void setNewWorkflowCompetitorNotes(String newWorkflowCompetitorNotes) {
		this.newWorkflowCompetitorNotes = newWorkflowCompetitorNotes;
	}

	public String getNewWorkflowCompetitorWebsite() {
		return this.newWorkflowCompetitorWebsite;
	}

	public void setNewWorkflowCompetitorWebsite(String newWorkflowCompetitorWebsite) {
		this.newWorkflowCompetitorWebsite = newWorkflowCompetitorWebsite;
	}

	public Boolean getNotified() {
		return this.notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public String getOldWorkflowCompetitorName() {
		return this.oldWorkflowCompetitorName;
	}

	public void setOldWorkflowCompetitorName(String oldWorkflowCompetitorName) {
		this.oldWorkflowCompetitorName = oldWorkflowCompetitorName;
	}

	public String getOldWorkflowCompetitorNotes() {
		return this.oldWorkflowCompetitorNotes;
	}

	public void setOldWorkflowCompetitorNotes(String oldWorkflowCompetitorNotes) {
		this.oldWorkflowCompetitorNotes = oldWorkflowCompetitorNotes;
	}

	public String getOldWorkflowCompetitorWebsite() {
		return this.oldWorkflowCompetitorWebsite;
	}

	public void setOldWorkflowCompetitorWebsite(String oldWorkflowCompetitorWebsite) {
		this.oldWorkflowCompetitorWebsite = oldWorkflowCompetitorWebsite;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public String getWorkflowCompetitorId() {
		return this.workflowCompetitorId;
	}

	public void setWorkflowCompetitorId(String workflowCompetitorId) {
		this.workflowCompetitorId = workflowCompetitorId;
	}

}