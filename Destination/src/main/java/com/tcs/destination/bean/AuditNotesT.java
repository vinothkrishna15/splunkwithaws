package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_notes_t database table.
 * 
 */
@Entity
@Table(name="audit_notes_t")
@NamedQuery(name="AuditNotesT.findAll", query="SELECT a FROM AuditNotesT a")
public class AuditNotesT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_note_id")
	private Long auditNoteId;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="new_notes_updated")
	private String newNotesUpdated;

	@Column(name="new_user_updated")
	private String newUserUpdated;

	@Column(name="note_id")
	private String noteId;

	private Boolean notified;

	@Column(name="old_connect_id")
	private String oldConnectId;

	@Column(name="old_customer_id")
	private String oldCustomerId;

	@Column(name="old_entity_type")
	private String oldEntityType;

	@Column(name="old_notes_updated")
	private String oldNotesUpdated;

	@Column(name="old_opportunity_id")
	private String oldOpportunityId;

	@Column(name="old_partner_id")
	private String oldPartnerId;

	@Column(name="old_task_id")
	private String oldTaskId;

	@Column(name="old_user_updated")
	private String oldUserUpdated;

	@Column(name="operation_type")
	private Integer operationType;

	public AuditNotesT() {
	}

	public Long getAuditNoteId() {
		return this.auditNoteId;
	}

	public void setAuditNoteId(Long auditNoteId) {
		this.auditNoteId = auditNoteId;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return this.createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
	}

	public String getNewNotesUpdated() {
		return this.newNotesUpdated;
	}

	public void setNewNotesUpdated(String newNotesUpdated) {
		this.newNotesUpdated = newNotesUpdated;
	}

	public String getNewUserUpdated() {
		return this.newUserUpdated;
	}

	public void setNewUserUpdated(String newUserUpdated) {
		this.newUserUpdated = newUserUpdated;
	}

	public String getNoteId() {
		return this.noteId;
	}

	public void setNoteId(String noteId) {
		this.noteId = noteId;
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

	public String getOldCustomerId() {
		return this.oldCustomerId;
	}

	public void setOldCustomerId(String oldCustomerId) {
		this.oldCustomerId = oldCustomerId;
	}

	public String getOldEntityType() {
		return this.oldEntityType;
	}

	public void setOldEntityType(String oldEntityType) {
		this.oldEntityType = oldEntityType;
	}

	public String getOldNotesUpdated() {
		return this.oldNotesUpdated;
	}

	public void setOldNotesUpdated(String oldNotesUpdated) {
		this.oldNotesUpdated = oldNotesUpdated;
	}

	public String getOldOpportunityId() {
		return this.oldOpportunityId;
	}

	public void setOldOpportunityId(String oldOpportunityId) {
		this.oldOpportunityId = oldOpportunityId;
	}

	public String getOldPartnerId() {
		return this.oldPartnerId;
	}

	public void setOldPartnerId(String oldPartnerId) {
		this.oldPartnerId = oldPartnerId;
	}

	public String getOldTaskId() {
		return this.oldTaskId;
	}

	public void setOldTaskId(String oldTaskId) {
		this.oldTaskId = oldTaskId;
	}

	public String getOldUserUpdated() {
		return this.oldUserUpdated;
	}

	public void setOldUserUpdated(String oldUserUpdated) {
		this.oldUserUpdated = oldUserUpdated;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

}