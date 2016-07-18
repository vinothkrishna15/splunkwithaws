package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_opportunity_offering_link_t database table.
 * 
 */
@Entity
@Table(name="audit_opportunity_offering_link_t")
@NamedQuery(name="AuditOpportunityOfferingLinkT.findAll", query="SELECT a FROM AuditOpportunityOfferingLinkT a")
public class AuditOpportunityOfferingLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_opportunity_offering_link_id")
	private Long auditOpportunityOfferingLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	private Boolean notified;

	@Column(name="old_offering")
	private String oldOffering;

	@Column(name="old_opportunity_id")
	private String oldOpportunityId;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="opportunity_offering_link_id")
	private String opportunityOfferingLinkId;

	public AuditOpportunityOfferingLinkT() {
	}

	public Long getAuditOpportunityOfferingLinkId() {
		return this.auditOpportunityOfferingLinkId;
	}

	public void setAuditOpportunityOfferingLinkId(Long auditOpportunityOfferingLinkId) {
		this.auditOpportunityOfferingLinkId = auditOpportunityOfferingLinkId;
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

	public String getOldOffering() {
		return this.oldOffering;
	}

	public void setOldOffering(String oldOffering) {
		this.oldOffering = oldOffering;
	}

	public String getOldOpportunityId() {
		return this.oldOpportunityId;
	}

	public void setOldOpportunityId(String oldOpportunityId) {
		this.oldOpportunityId = oldOpportunityId;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public String getOpportunityOfferingLinkId() {
		return this.opportunityOfferingLinkId;
	}

	public void setOpportunityOfferingLinkId(String opportunityOfferingLinkId) {
		this.opportunityOfferingLinkId = opportunityOfferingLinkId;
	}

}