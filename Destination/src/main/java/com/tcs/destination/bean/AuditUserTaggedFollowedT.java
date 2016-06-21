package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_user_tagged_followed_t database table.
 * 
 */
@Entity
@Table(name="audit_user_tagged_followed_t")
@NamedQuery(name="AuditUserTaggedFollowedT.findAll", query="SELECT a FROM AuditUserTaggedFollowedT a")
public class AuditUserTaggedFollowedT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_user_tagged_followed_id")
	private Long auditUserTaggedFollowedId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	private Boolean notified;

	@Column(name="old_connect_id")
	private String oldConnectId;

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

	@Column(name="user_tagged_followed_id")
	private String userTaggedFollowedId;

	public AuditUserTaggedFollowedT() {
	}

	public Long getAuditUserTaggedFollowedId() {
		return this.auditUserTaggedFollowedId;
	}

	public void setAuditUserTaggedFollowedId(Long auditUserTaggedFollowedId) {
		this.auditUserTaggedFollowedId = auditUserTaggedFollowedId;
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

	public String getUserTaggedFollowedId() {
		return this.userTaggedFollowedId;
	}

	public void setUserTaggedFollowedId(String userTaggedFollowedId) {
		this.userTaggedFollowedId = userTaggedFollowedId;
	}

}