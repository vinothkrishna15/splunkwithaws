package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_connect_t database table.
 * 
 */
@Entity
@Table(name="audit_connect_t")
@NamedQuery(name="AuditConnectT.findAll", query="SELECT a FROM AuditConnectT a")
public class AuditConnectT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_connect_id")
	private Long auditConnectId;

	@Column(name="connect_id")
	private String connectId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="new_active")
	private Boolean newActive;

	@Column(name="new_country")
	private String newCountry;

	@Column(name="new_documents_attached")
	private String newDocumentsAttached;

	@Column(name="new_end_datetime_of_connect")
	private Timestamp newEndDatetimeOfConnect;

	@Column(name="new_location")
	private String newLocation;

	@Column(name="new_primary_owner")
	private String newPrimaryOwner;

	@Column(name="new_start_datetime_of_connect")
	private Timestamp newStartDatetimeOfConnect;

	@Column(name="new_time_zone_desc")
	private String newTimeZoneDesc;

	@Column(name="new_type")
	private String newType;

	private Boolean notified;

	@Column(name="old_active")
	private Boolean oldActive;

	@Column(name="old_connect_category")
	private String oldConnectCategory;

	@Column(name="old_connect_name")
	private String oldConnectName;

	@Column(name="old_country")
	private String oldCountry;

	@Column(name="old_customer_id")
	private String oldCustomerId;

	@Column(name="old_documents_attached")
	private String oldDocumentsAttached;

	@Column(name="old_end_datetime_of_connect")
	private Timestamp oldEndDatetimeOfConnect;

	@Column(name="old_location")
	private String oldLocation;

	@Column(name="old_partner_id")
	private String oldPartnerId;

	@Column(name="old_primary_owner")
	private String oldPrimaryOwner;

	@Column(name="old_start_datetime_of_connect")
	private Timestamp oldStartDatetimeOfConnect;

	@Column(name="old_time_zone_desc")
	private String oldTimeZoneDesc;

	@Column(name="old_type")
	private String oldType;

	@Column(name="operation_type")
	private Integer operationType;

	public AuditConnectT() {
	}

	public Long getAuditConnectId() {
		return this.auditConnectId;
	}

	public void setAuditConnectId(Long auditConnectId) {
		this.auditConnectId = auditConnectId;
	}

	public String getConnectId() {
		return this.connectId;
	}

	public void setConnectId(String connectId) {
		this.connectId = connectId;
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

	public Boolean getNewActive() {
		return this.newActive;
	}

	public void setNewActive(Boolean newActive) {
		this.newActive = newActive;
	}

	public String getNewCountry() {
		return this.newCountry;
	}

	public void setNewCountry(String newCountry) {
		this.newCountry = newCountry;
	}

	public String getNewDocumentsAttached() {
		return this.newDocumentsAttached;
	}

	public void setNewDocumentsAttached(String newDocumentsAttached) {
		this.newDocumentsAttached = newDocumentsAttached;
	}

	public Timestamp getNewEndDatetimeOfConnect() {
		return this.newEndDatetimeOfConnect;
	}

	public void setNewEndDatetimeOfConnect(Timestamp newEndDatetimeOfConnect) {
		this.newEndDatetimeOfConnect = newEndDatetimeOfConnect;
	}

	public String getNewLocation() {
		return this.newLocation;
	}

	public void setNewLocation(String newLocation) {
		this.newLocation = newLocation;
	}

	public String getNewPrimaryOwner() {
		return this.newPrimaryOwner;
	}

	public void setNewPrimaryOwner(String newPrimaryOwner) {
		this.newPrimaryOwner = newPrimaryOwner;
	}

	public Timestamp getNewStartDatetimeOfConnect() {
		return this.newStartDatetimeOfConnect;
	}

	public void setNewStartDatetimeOfConnect(Timestamp newStartDatetimeOfConnect) {
		this.newStartDatetimeOfConnect = newStartDatetimeOfConnect;
	}

	public String getNewTimeZoneDesc() {
		return this.newTimeZoneDesc;
	}

	public void setNewTimeZoneDesc(String newTimeZoneDesc) {
		this.newTimeZoneDesc = newTimeZoneDesc;
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

	public Boolean getOldActive() {
		return this.oldActive;
	}

	public void setOldActive(Boolean oldActive) {
		this.oldActive = oldActive;
	}

	public String getOldConnectCategory() {
		return this.oldConnectCategory;
	}

	public void setOldConnectCategory(String oldConnectCategory) {
		this.oldConnectCategory = oldConnectCategory;
	}

	public String getOldConnectName() {
		return this.oldConnectName;
	}

	public void setOldConnectName(String oldConnectName) {
		this.oldConnectName = oldConnectName;
	}

	public String getOldCountry() {
		return this.oldCountry;
	}

	public void setOldCountry(String oldCountry) {
		this.oldCountry = oldCountry;
	}

	public String getOldCustomerId() {
		return this.oldCustomerId;
	}

	public void setOldCustomerId(String oldCustomerId) {
		this.oldCustomerId = oldCustomerId;
	}

	public String getOldDocumentsAttached() {
		return this.oldDocumentsAttached;
	}

	public void setOldDocumentsAttached(String oldDocumentsAttached) {
		this.oldDocumentsAttached = oldDocumentsAttached;
	}

	public Timestamp getOldEndDatetimeOfConnect() {
		return this.oldEndDatetimeOfConnect;
	}

	public void setOldEndDatetimeOfConnect(Timestamp oldEndDatetimeOfConnect) {
		this.oldEndDatetimeOfConnect = oldEndDatetimeOfConnect;
	}

	public String getOldLocation() {
		return this.oldLocation;
	}

	public void setOldLocation(String oldLocation) {
		this.oldLocation = oldLocation;
	}

	public String getOldPartnerId() {
		return this.oldPartnerId;
	}

	public void setOldPartnerId(String oldPartnerId) {
		this.oldPartnerId = oldPartnerId;
	}

	public String getOldPrimaryOwner() {
		return this.oldPrimaryOwner;
	}

	public void setOldPrimaryOwner(String oldPrimaryOwner) {
		this.oldPrimaryOwner = oldPrimaryOwner;
	}

	public Timestamp getOldStartDatetimeOfConnect() {
		return this.oldStartDatetimeOfConnect;
	}

	public void setOldStartDatetimeOfConnect(Timestamp oldStartDatetimeOfConnect) {
		this.oldStartDatetimeOfConnect = oldStartDatetimeOfConnect;
	}

	public String getOldTimeZoneDesc() {
		return this.oldTimeZoneDesc;
	}

	public void setOldTimeZoneDesc(String oldTimeZoneDesc) {
		this.oldTimeZoneDesc = oldTimeZoneDesc;
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

}