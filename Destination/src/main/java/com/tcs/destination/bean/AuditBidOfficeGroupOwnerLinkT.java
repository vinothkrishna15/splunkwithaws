package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_bid_office_group_owner_link_t database table.
 * 
 */
@Entity
@Table(name="audit_bid_office_group_owner_link_t")
@NamedQuery(name="AuditBidOfficeGroupOwnerLinkT.findAll", query="SELECT a FROM AuditBidOfficeGroupOwnerLinkT a")
public class AuditBidOfficeGroupOwnerLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_bid_office_group_owner_link_id")
	private Long auditBidOfficeGroupOwnerLinkId;

	@Column(name="bid_office_group_owner_link_id")
	private String bidOfficeGroupOwnerLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="new_bid_office_group_owner")
	private String newBidOfficeGroupOwner;

	private Boolean notified;

	@Column(name="old_bid_id")
	private String oldBidId;

	@Column(name="old_bid_office_group_owner")
	private String oldBidOfficeGroupOwner;

	@Column(name="operation_type")
	private Integer operationType;

	public AuditBidOfficeGroupOwnerLinkT() {
	}

	public Long getAuditBidOfficeGroupOwnerLinkId() {
		return this.auditBidOfficeGroupOwnerLinkId;
	}

	public void setAuditBidOfficeGroupOwnerLinkId(Long auditBidOfficeGroupOwnerLinkId) {
		this.auditBidOfficeGroupOwnerLinkId = auditBidOfficeGroupOwnerLinkId;
	}

	public String getBidOfficeGroupOwnerLinkId() {
		return this.bidOfficeGroupOwnerLinkId;
	}

	public void setBidOfficeGroupOwnerLinkId(String bidOfficeGroupOwnerLinkId) {
		this.bidOfficeGroupOwnerLinkId = bidOfficeGroupOwnerLinkId;
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

	public String getNewBidOfficeGroupOwner() {
		return this.newBidOfficeGroupOwner;
	}

	public void setNewBidOfficeGroupOwner(String newBidOfficeGroupOwner) {
		this.newBidOfficeGroupOwner = newBidOfficeGroupOwner;
	}

	public Boolean getNotified() {
		return this.notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public String getOldBidId() {
		return this.oldBidId;
	}

	public void setOldBidId(String oldBidId) {
		this.oldBidId = oldBidId;
	}

	public String getOldBidOfficeGroupOwner() {
		return this.oldBidOfficeGroupOwner;
	}

	public void setOldBidOfficeGroupOwner(String oldBidOfficeGroupOwner) {
		this.oldBidOfficeGroupOwner = oldBidOfficeGroupOwner;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

}