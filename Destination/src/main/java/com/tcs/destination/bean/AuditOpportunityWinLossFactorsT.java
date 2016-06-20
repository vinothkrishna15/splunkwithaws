package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_opportunity_win_loss_factors_t database table.
 * 
 */
@Entity
@Table(name="audit_opportunity_win_loss_factors_t")
@NamedQuery(name="AuditOpportunityWinLossFactorsT.findAll", query="SELECT a FROM AuditOpportunityWinLossFactorsT a")
public class AuditOpportunityWinLossFactorsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_opportunity_win_loss_factors_id")
	private Long auditOpportunityWinLossFactorsId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	private Boolean notified;

	@Column(name="old_opportunity_id")
	private String oldOpportunityId;

	@Column(name="old_rank")
	private Integer oldRank;

	@Column(name="old_win_loss_factor")
	private String oldWinLossFactor;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="opportunity_win_loss_factors_id")
	private String opportunityWinLossFactorsId;

	public AuditOpportunityWinLossFactorsT() {
	}

	public Long getAuditOpportunityWinLossFactorsId() {
		return this.auditOpportunityWinLossFactorsId;
	}

	public void setAuditOpportunityWinLossFactorsId(Long auditOpportunityWinLossFactorsId) {
		this.auditOpportunityWinLossFactorsId = auditOpportunityWinLossFactorsId;
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

	public Integer getOldRank() {
		return this.oldRank;
	}

	public void setOldRank(Integer oldRank) {
		this.oldRank = oldRank;
	}

	public String getOldWinLossFactor() {
		return this.oldWinLossFactor;
	}

	public void setOldWinLossFactor(String oldWinLossFactor) {
		this.oldWinLossFactor = oldWinLossFactor;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public String getOpportunityWinLossFactorsId() {
		return this.opportunityWinLossFactorsId;
	}

	public void setOpportunityWinLossFactorsId(String opportunityWinLossFactorsId) {
		this.opportunityWinLossFactorsId = opportunityWinLossFactorsId;
	}

}