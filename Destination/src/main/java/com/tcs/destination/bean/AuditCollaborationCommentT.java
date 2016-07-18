package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_collaboration_comment_t database table.
 * 
 */
@Entity
@Table(name="audit_collaboration_comment_t")
@NamedQuery(name="AuditCollaborationCommentT.findAll", query="SELECT a FROM AuditCollaborationCommentT a")
public class AuditCollaborationCommentT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_comment_id")
	private Long auditCommentId;

	@Column(name="comment_id")
	private String commentId;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="new_comments")
	private String newComments;

	@Column(name="new_documents_attached")
	private String newDocumentsAttached;

	private Boolean notified;

	@Column(name="old_comment_type")
	private String oldCommentType;

	@Column(name="old_comments")
	private String oldComments;

	@Column(name="old_connect_id")
	private String oldConnectId;

	@Column(name="old_documents_attached")
	private String oldDocumentsAttached;

	@Column(name="old_entity_id")
	private String oldEntityId;

	@Column(name="old_entity_type")
	private String oldEntityType;

	@Column(name="old_opportunity_id")
	private String oldOpportunityId;

	@Column(name="old_task_id")
	private String oldTaskId;

	@Column(name="old_user_id")
	private String oldUserId;

	@Column(name="operation_type")
	private Integer operationType;

	public AuditCollaborationCommentT() {
	}

	public Long getAuditCommentId() {
		return this.auditCommentId;
	}

	public void setAuditCommentId(Long auditCommentId) {
		this.auditCommentId = auditCommentId;
	}

	public String getCommentId() {
		return this.commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
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

	public String getNewDocumentsAttached() {
		return this.newDocumentsAttached;
	}

	public void setNewDocumentsAttached(String newDocumentsAttached) {
		this.newDocumentsAttached = newDocumentsAttached;
	}

	public Boolean getNotified() {
		return this.notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public String getOldCommentType() {
		return this.oldCommentType;
	}

	public void setOldCommentType(String oldCommentType) {
		this.oldCommentType = oldCommentType;
	}

	public String getOldComments() {
		return this.oldComments;
	}

	public void setOldComments(String oldComments) {
		this.oldComments = oldComments;
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

	public String getOldEntityId() {
		return this.oldEntityId;
	}

	public void setOldEntityId(String oldEntityId) {
		this.oldEntityId = oldEntityId;
	}

	public String getOldEntityType() {
		return this.oldEntityType;
	}

	public void setOldEntityType(String oldEntityType) {
		this.oldEntityType = oldEntityType;
	}

	public String getOldOpportunityId() {
		return this.oldOpportunityId;
	}

	public void setOldOpportunityId(String oldOpportunityId) {
		this.oldOpportunityId = oldOpportunityId;
	}

	public String getOldTaskId() {
		return this.oldTaskId;
	}

	public void setOldTaskId(String oldTaskId) {
		this.oldTaskId = oldTaskId;
	}

	public String getOldUserId() {
		return this.oldUserId;
	}

	public void setOldUserId(String oldUserId) {
		this.oldUserId = oldUserId;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

}