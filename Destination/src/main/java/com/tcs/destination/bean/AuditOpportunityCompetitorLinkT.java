package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_opportunity_competitor_link_t database table.
 * 
 */
@Entity
@Table(name="audit_opportunity_competitor_link_t")
@NamedQuery(name="AuditOpportunityCompetitorLinkT.findAll", query="SELECT a FROM AuditOpportunityCompetitorLinkT a")
public class AuditOpportunityCompetitorLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_opportunity_competitor_link_id")
	private Long auditOpportunityCompetitorLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="new_incumbent_flag")
	private String newIncumbentFlag;

	private Boolean notified;

	@Column(name="old_competitor_name")
	private String oldCompetitorName;

	@Column(name="old_incumbent_flag")
	private String oldIncumbentFlag;

	@Column(name="old_opportunity_id")
	private String oldOpportunityId;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="opportunity_competitor_link_id")
	private String opportunityCompetitorLinkId;

	public AuditOpportunityCompetitorLinkT() {
	}

	public Long getAuditOpportunityCompetitorLinkId() {
		return this.auditOpportunityCompetitorLinkId;
	}

	public void setAuditOpportunityCompetitorLinkId(Long auditOpportunityCompetitorLinkId) {
		this.auditOpportunityCompetitorLinkId = auditOpportunityCompetitorLinkId;
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

	public String getNewIncumbentFlag() {
		return this.newIncumbentFlag;
	}

	public void setNewIncumbentFlag(String newIncumbentFlag) {
		this.newIncumbentFlag = newIncumbentFlag;
	}

	public Boolean getNotified() {
		return this.notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public String getOldCompetitorName() {
		return this.oldCompetitorName;
	}

	public void setOldCompetitorName(String oldCompetitorName) {
		this.oldCompetitorName = oldCompetitorName;
	}

	public String getOldIncumbentFlag() {
		return this.oldIncumbentFlag;
	}

	public void setOldIncumbentFlag(String oldIncumbentFlag) {
		this.oldIncumbentFlag = oldIncumbentFlag;
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

	public String getOpportunityCompetitorLinkId() {
		return this.opportunityCompetitorLinkId;
	}

	public void setOpportunityCompetitorLinkId(String opportunityCompetitorLinkId) {
		this.opportunityCompetitorLinkId = opportunityCompetitorLinkId;
	}

}