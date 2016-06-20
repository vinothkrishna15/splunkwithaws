package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_user_favorites_t database table.
 * 
 */
@Entity
@Table(name="audit_user_favorites_t")
@NamedQuery(name="AuditUserFavoritesT.findAll", query="SELECT a FROM AuditUserFavoritesT a")
public class AuditUserFavoritesT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_user_favorites_id")
	private Long auditUserFavoritesId;

	@Column(name="old_connect_id")
	private String oldConnectId;

	@Column(name="old_contact_id")
	private String oldContactId;

	@Column(name="old_created_datetime")
	private Timestamp oldCreatedDatetime;

	@Column(name="old_customer_id")
	private String oldCustomerId;

	@Column(name="old_document_id")
	private String oldDocumentId;

	@Column(name="old_entity_type")
	private String oldEntityType;

	@Column(name="old_opportunity_id")
	private String oldOpportunityId;

	@Column(name="old_partner_id")
	private String oldPartnerId;

	@Column(name="old_user_id")
	private String oldUserId;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="user_favorites_id")
	private String userFavoritesId;

	public AuditUserFavoritesT() {
	}

	public Long getAuditUserFavoritesId() {
		return this.auditUserFavoritesId;
	}

	public void setAuditUserFavoritesId(Long auditUserFavoritesId) {
		this.auditUserFavoritesId = auditUserFavoritesId;
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

	public Timestamp getOldCreatedDatetime() {
		return this.oldCreatedDatetime;
	}

	public void setOldCreatedDatetime(Timestamp oldCreatedDatetime) {
		this.oldCreatedDatetime = oldCreatedDatetime;
	}

	public String getOldCustomerId() {
		return this.oldCustomerId;
	}

	public void setOldCustomerId(String oldCustomerId) {
		this.oldCustomerId = oldCustomerId;
	}

	public String getOldDocumentId() {
		return this.oldDocumentId;
	}

	public void setOldDocumentId(String oldDocumentId) {
		this.oldDocumentId = oldDocumentId;
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

	public String getOldPartnerId() {
		return this.oldPartnerId;
	}

	public void setOldPartnerId(String oldPartnerId) {
		this.oldPartnerId = oldPartnerId;
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

	public String getUserFavoritesId() {
		return this.userFavoritesId;
	}

	public void setUserFavoritesId(String userFavoritesId) {
		this.userFavoritesId = userFavoritesId;
	}

}