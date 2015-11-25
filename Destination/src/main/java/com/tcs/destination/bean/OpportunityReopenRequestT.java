package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;

/**
 * The persistent class for the opportunity_reopen_request_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Entity
@Table(name = "opportunity_reopen_request_t")
@NamedQuery(name = "OpportunityReopenRequestT.findAll", query = "SELECT o FROM OpportunityReopenRequestT o")
public class OpportunityReopenRequestT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "opportunity_reopen_request_id")
	private String opportunityReopenRequestId;

	@Column(name = "approved_flag")
	private String approvedFlag;

	@Column(name = "approved_rejected_by")
	private String approvedRejectedBy;

	@Column(name = "approved_rejected_comments")
	private String approvedRejectedComments;

	@Column(name = "approved_rejected_datetime")
	private Timestamp approvedRejectedDatetime;

	@Column(name = "opportunity_id")
	private String opportunityId;

	@Column(name = "reason_for_reopen")
	private String reasonForReopen;

	@Column(name = "request_received_datetime")
	private Timestamp requestReceivedDatetime;

	@Column(name = "requested_by",updatable=false)
	private String requestedBy;

	@ManyToOne
	@JoinColumn(name = "requested_by", insertable = false, updatable = false)
	private UserT requestedByUser;

	@ManyToOne
	@JoinColumn(name = "approved_rejected_by", insertable = false, updatable = false)
	private UserT approvedRejectedByUser;

	@ManyToOne
	@JoinColumn(name = "opportunity_id", insertable = false, updatable = false)
	private OpportunityT opportunityT;

	public OpportunityReopenRequestT() {
	}

	public String getOpportunityReopenRequestId() {
		return this.opportunityReopenRequestId;
	}

	public void setOpportunityReopenRequestId(String opportunityReopenRequestId) {
		this.opportunityReopenRequestId = opportunityReopenRequestId;
	}

	public String getApprovedFlag() {
		return this.approvedFlag;
	}

	public void setApprovedFlag(String approvedFlag) {
		this.approvedFlag = approvedFlag;
	}

	public String getApprovedRejectedBy() {
		return this.approvedRejectedBy;
	}

	public void setApprovedRejectedBy(String approvedRejectedBy) {
		this.approvedRejectedBy = approvedRejectedBy;
	}

	public String getApprovedRejectedComments() {
		return this.approvedRejectedComments;
	}

	public void setApprovedRejectedComments(String approvedRejectedComments) {
		this.approvedRejectedComments = approvedRejectedComments;
	}

	public Timestamp getApprovedRejectedDatetime() {
		return this.approvedRejectedDatetime;
	}

	public void setApprovedRejectedDatetime(Timestamp approvedRejectedDatetime) {
		this.approvedRejectedDatetime = approvedRejectedDatetime;
	}

	public String getOpportunityId() {
		return this.opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public String getReasonForReopen() {
		return this.reasonForReopen;
	}

	public void setReasonForReopen(String reasonForReopen) {
		this.reasonForReopen = reasonForReopen;
	}

	public Timestamp getRequestReceivedDatetime() {
		return this.requestReceivedDatetime;
	}

	public void setRequestReceivedDatetime(Timestamp requestReceivedDatetime) {
		this.requestReceivedDatetime = requestReceivedDatetime;
	}

	public String getRequestedBy() {
		return this.requestedBy;
	}

	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}

	public UserT getApprovedRejectedByUser() {
		return approvedRejectedByUser;
	}

	public OpportunityT getOpportunityT() {
		return opportunityT;
	}

	public UserT getRequestedByUser() {
		return requestedByUser;
	}

	public void setApprovedRejectedByUser(UserT approvedRejectedByUser) {
		this.approvedRejectedByUser = approvedRejectedByUser;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

	public void setRequestedByUser(UserT requestedByUser) {
		this.requestedByUser = requestedByUser;
	}

}