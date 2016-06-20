package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_opportunity_sales_support_link_t database table.
 * 
 */
@Entity
@Table(name="audit_opportunity_sales_support_link_t")
@NamedQuery(name="AuditOpportunitySalesSupportLinkT.findAll", query="SELECT a FROM AuditOpportunitySalesSupportLinkT a")
public class AuditOpportunitySalesSupportLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_opportunity_sales_support_link_id")
	private Long auditOpportunitySalesSupportLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	private Boolean notified;

	@Column(name="old_opportunity_id")
	private String oldOpportunityId;

	@Column(name="old_sales_support_owner")
	private String oldSalesSupportOwner;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="opportunity_sales_support_link_id")
	private String opportunitySalesSupportLinkId;

	public AuditOpportunitySalesSupportLinkT() {
	}

	public Long getAuditOpportunitySalesSupportLinkId() {
		return this.auditOpportunitySalesSupportLinkId;
	}

	public void setAuditOpportunitySalesSupportLinkId(Long auditOpportunitySalesSupportLinkId) {
		this.auditOpportunitySalesSupportLinkId = auditOpportunitySalesSupportLinkId;
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

	public String getOldOpportunityId() {
		return this.oldOpportunityId;
	}

	public void setOldOpportunityId(String oldOpportunityId) {
		this.oldOpportunityId = oldOpportunityId;
	}

	public String getOldSalesSupportOwner() {
		return this.oldSalesSupportOwner;
	}

	public void setOldSalesSupportOwner(String oldSalesSupportOwner) {
		this.oldSalesSupportOwner = oldSalesSupportOwner;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public String getOpportunitySalesSupportLinkId() {
		return this.opportunitySalesSupportLinkId;
	}

	public void setOpportunitySalesSupportLinkId(String opportunitySalesSupportLinkId) {
		this.opportunitySalesSupportLinkId = opportunitySalesSupportLinkId;
	}

}