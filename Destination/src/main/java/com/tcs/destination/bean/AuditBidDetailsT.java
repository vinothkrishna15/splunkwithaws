package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the audit_bid_details_t database table.
 * 
 */
@Entity
@Table(name="audit_bid_details_t")
@NamedQuery(name="AuditBidDetailsT.findAll", query="SELECT a FROM AuditBidDetailsT a")
public class AuditBidDetailsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_bid_details_id")
	private Long auditBidDetailsId;

	@Column(name="bid_id")
	private String bidId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Temporal(TemporalType.DATE)
	@Column(name="new_actual_bid_submission_date")
	private Date newActualBidSubmissionDate;

	@Temporal(TemporalType.DATE)
	@Column(name="new_bid_request_receive_date")
	private Date newBidRequestReceiveDate;

	@Column(name="new_bid_request_type")
	private String newBidRequestType;

	@Column(name="new_core_attributes_used_for_winning")
	private String newCoreAttributesUsedForWinning;

	@Temporal(TemporalType.DATE)
	@Column(name="new_expected_date_of_outcome")
	private Date newExpectedDateOfOutcome;

	@Temporal(TemporalType.DATE)
	@Column(name="new_target_bid_submission_date")
	private Date newTargetBidSubmissionDate;

	@Column(name="new_win_probability")
	private String newWinProbability;

	private Boolean notified;

	@Temporal(TemporalType.DATE)
	@Column(name="old_actual_bid_submission_date")
	private Date oldActualBidSubmissionDate;

	@Temporal(TemporalType.DATE)
	@Column(name="old_bid_request_receive_date")
	private Date oldBidRequestReceiveDate;

	@Column(name="old_bid_request_type")
	private String oldBidRequestType;

	@Column(name="old_core_attributes_used_for_winning")
	private String oldCoreAttributesUsedForWinning;

	@Temporal(TemporalType.DATE)
	@Column(name="old_expected_date_of_outcome")
	private Date oldExpectedDateOfOutcome;

	@Column(name="old_opportunity_id")
	private String oldOpportunityId;

	@Temporal(TemporalType.DATE)
	@Column(name="old_target_bid_submission_date")
	private Date oldTargetBidSubmissionDate;

	@Column(name="old_win_probability")
	private String oldWinProbability;

	@Column(name="operation_type")
	private Integer operationType;

	public AuditBidDetailsT() {
	}

	public Long getAuditBidDetailsId() {
		return this.auditBidDetailsId;
	}

	public void setAuditBidDetailsId(Long auditBidDetailsId) {
		this.auditBidDetailsId = auditBidDetailsId;
	}

	public String getBidId() {
		return this.bidId;
	}

	public void setBidId(String bidId) {
		this.bidId = bidId;
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

	public Date getNewActualBidSubmissionDate() {
		return this.newActualBidSubmissionDate;
	}

	public void setNewActualBidSubmissionDate(Date newActualBidSubmissionDate) {
		this.newActualBidSubmissionDate = newActualBidSubmissionDate;
	}

	public Date getNewBidRequestReceiveDate() {
		return this.newBidRequestReceiveDate;
	}

	public void setNewBidRequestReceiveDate(Date newBidRequestReceiveDate) {
		this.newBidRequestReceiveDate = newBidRequestReceiveDate;
	}

	public String getNewBidRequestType() {
		return this.newBidRequestType;
	}

	public void setNewBidRequestType(String newBidRequestType) {
		this.newBidRequestType = newBidRequestType;
	}

	public String getNewCoreAttributesUsedForWinning() {
		return this.newCoreAttributesUsedForWinning;
	}

	public void setNewCoreAttributesUsedForWinning(String newCoreAttributesUsedForWinning) {
		this.newCoreAttributesUsedForWinning = newCoreAttributesUsedForWinning;
	}

	public Date getNewExpectedDateOfOutcome() {
		return this.newExpectedDateOfOutcome;
	}

	public void setNewExpectedDateOfOutcome(Date newExpectedDateOfOutcome) {
		this.newExpectedDateOfOutcome = newExpectedDateOfOutcome;
	}

	public Date getNewTargetBidSubmissionDate() {
		return this.newTargetBidSubmissionDate;
	}

	public void setNewTargetBidSubmissionDate(Date newTargetBidSubmissionDate) {
		this.newTargetBidSubmissionDate = newTargetBidSubmissionDate;
	}

	public String getNewWinProbability() {
		return this.newWinProbability;
	}

	public void setNewWinProbability(String newWinProbability) {
		this.newWinProbability = newWinProbability;
	}

	public Boolean getNotified() {
		return this.notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public Date getOldActualBidSubmissionDate() {
		return this.oldActualBidSubmissionDate;
	}

	public void setOldActualBidSubmissionDate(Date oldActualBidSubmissionDate) {
		this.oldActualBidSubmissionDate = oldActualBidSubmissionDate;
	}

	public Date getOldBidRequestReceiveDate() {
		return this.oldBidRequestReceiveDate;
	}

	public void setOldBidRequestReceiveDate(Date oldBidRequestReceiveDate) {
		this.oldBidRequestReceiveDate = oldBidRequestReceiveDate;
	}

	public String getOldBidRequestType() {
		return this.oldBidRequestType;
	}

	public void setOldBidRequestType(String oldBidRequestType) {
		this.oldBidRequestType = oldBidRequestType;
	}

	public String getOldCoreAttributesUsedForWinning() {
		return this.oldCoreAttributesUsedForWinning;
	}

	public void setOldCoreAttributesUsedForWinning(String oldCoreAttributesUsedForWinning) {
		this.oldCoreAttributesUsedForWinning = oldCoreAttributesUsedForWinning;
	}

	public Date getOldExpectedDateOfOutcome() {
		return this.oldExpectedDateOfOutcome;
	}

	public void setOldExpectedDateOfOutcome(Date oldExpectedDateOfOutcome) {
		this.oldExpectedDateOfOutcome = oldExpectedDateOfOutcome;
	}

	public String getOldOpportunityId() {
		return this.oldOpportunityId;
	}

	public void setOldOpportunityId(String oldOpportunityId) {
		this.oldOpportunityId = oldOpportunityId;
	}

	public Date getOldTargetBidSubmissionDate() {
		return this.oldTargetBidSubmissionDate;
	}

	public void setOldTargetBidSubmissionDate(Date oldTargetBidSubmissionDate) {
		this.oldTargetBidSubmissionDate = oldTargetBidSubmissionDate;
	}

	public String getOldWinProbability() {
		return this.oldWinProbability;
	}

	public void setOldWinProbability(String oldWinProbability) {
		this.oldWinProbability = oldWinProbability;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

}