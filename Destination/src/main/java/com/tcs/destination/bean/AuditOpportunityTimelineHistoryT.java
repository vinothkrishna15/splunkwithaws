package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_opportunity_timeline_history_t database table.
 * 
 */
@Entity
@Table(name="audit_opportunity_timeline_history_t")
@NamedQuery(name="AuditOpportunityTimelineHistoryT.findAll", query="SELECT a FROM AuditOpportunityTimelineHistoryT a")
public class AuditOpportunityTimelineHistoryT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_opportunity_timeline_history_id")
	private Long auditOpportunityTimelineHistoryId;

	@Column(name="bid_id")
	private String bidId;

	@Column(name="opportunity_id")
	private String opportunityId;

	@Column(name="opportunity_timeline_history_id")
	private String opportunityTimelineHistoryId;

	@Column(name="sales_stage_code")
	private Integer salesStageCode;

	@Column(name="updated_datetime")
	private Timestamp updatedDatetime;

	@Column(name="user_updated")
	private String userUpdated;

	public AuditOpportunityTimelineHistoryT() {
	}

	public Long getAuditOpportunityTimelineHistoryId() {
		return this.auditOpportunityTimelineHistoryId;
	}

	public void setAuditOpportunityTimelineHistoryId(Long auditOpportunityTimelineHistoryId) {
		this.auditOpportunityTimelineHistoryId = auditOpportunityTimelineHistoryId;
	}

	public String getBidId() {
		return this.bidId;
	}

	public void setBidId(String bidId) {
		this.bidId = bidId;
	}

	public String getOpportunityId() {
		return this.opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public String getOpportunityTimelineHistoryId() {
		return this.opportunityTimelineHistoryId;
	}

	public void setOpportunityTimelineHistoryId(String opportunityTimelineHistoryId) {
		this.opportunityTimelineHistoryId = opportunityTimelineHistoryId;
	}

	public Integer getSalesStageCode() {
		return this.salesStageCode;
	}

	public void setSalesStageCode(Integer salesStageCode) {
		this.salesStageCode = salesStageCode;
	}

	public Timestamp getUpdatedDatetime() {
		return this.updatedDatetime;
	}

	public void setUpdatedDatetime(Timestamp updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
	}

	public String getUserUpdated() {
		return this.userUpdated;
	}

	public void setUserUpdated(String userUpdated) {
		this.userUpdated = userUpdated;
	}

}