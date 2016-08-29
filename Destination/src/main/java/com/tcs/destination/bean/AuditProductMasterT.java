package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;

/**
 * The persistent class for the audit_product_master_t database table.
 * 
 */
@Entity
@Table(name = "audit_product_master_t")
@NamedQuery(name = "AuditProductMasterT.findAll", query = "SELECT a FROM AuditProductMasterT a")
public class AuditProductMasterT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "audit_product_id")
	private Long auditProductId;

	@Column(name = "operation_type")
	private Integer operationType;

	@Column(name = "old_product_id")
	private String oldProductId;

	@Column(name = "old_product_name")
	private String oldProductName;

	@Column(name = "new_product_name")
	private String newProductName;

	@Column(name = "old_product_description")
	private String oldProductDescription;

	@Column(name = "new_product_description")
	private String newProductDescription;

	@Column(name = "created_modified_by")
	private String createdModifiedBy;

	@Column(name = "created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name = "new_active")
	private Boolean newActive;

	@Column(name = "old_active")
	private Boolean oldActive;

	private Boolean notified;

	public Long getAuditProductId() {
		return auditProductId;
	}

	public void setAuditProductId(Long auditProductId) {
		this.auditProductId = auditProductId;
	}

	public Integer getOperationType() {
		return operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public String getOldProductId() {
		return oldProductId;
	}

	public void setOldProductId(String oldProductId) {
		this.oldProductId = oldProductId;
	}

	public String getOldProductName() {
		return oldProductName;
	}

	public void setOldProductName(String oldProductName) {
		this.oldProductName = oldProductName;
	}

	public String getNewProductName() {
		return newProductName;
	}

	public void setNewProductName(String newProductName) {
		this.newProductName = newProductName;
	}

	public String getOldProductDescription() {
		return oldProductDescription;
	}

	public void setOldProductDescription(String oldProductDescription) {
		this.oldProductDescription = oldProductDescription;
	}

	public String getNewProductDescription() {
		return newProductDescription;
	}

	public void setNewProductDescription(String newProductDescription) {
		this.newProductDescription = newProductDescription;
	}

	public String getCreatedModifiedBy() {
		return createdModifiedBy;
	}

	public void setCreatedModifiedBy(String createdModifiedBy) {
		this.createdModifiedBy = createdModifiedBy;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
	}

	public Boolean getNewActive() {
		return newActive;
	}

	public void setNewActive(Boolean newActive) {
		this.newActive = newActive;
	}

	public Boolean getOldActive() {
		return oldActive;
	}

	public void setOldActive(Boolean oldActive) {
		this.oldActive = oldActive;
	}

	public Boolean getNotified() {
		return notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}
	
	

}