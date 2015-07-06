package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the bid_details_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "bidId")
@Entity
@Table(name = "bid_details_t")
@NamedQuery(name = "BidDetailsT.findAll", query = "SELECT b FROM BidDetailsT b")
public class BidDetailsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bid_id")
	private String bidId;

	@Temporal(TemporalType.DATE)
	@Column(name = "actual_bid_submission_date")
	private Date actualBidSubmissionDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "bid_request_receive_date")
	private Date bidRequestReceiveDate;

	@Column(name = "core_attributes_used_for_winning")
	private String coreAttributesUsedForWinning;

	@Column(name = "created_by", updatable = false)
	private String createdBy;

	@Column(name = "created_datetime", updatable = false)
	private Timestamp createdDatetime;

	@ManyToOne
	@JoinColumn(name = "created_by", insertable = false, updatable = false)
	private UserT createdByUser;

	@Column(name = "modified_by")
	private String modifiedBy;

	@Column(name = "modified_datetime")
	private Timestamp modifiedDatetime;

	@ManyToOne
	@JoinColumn(name = "modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;

	@Temporal(TemporalType.DATE)
	@Column(name = "expected_date_of_outcome")
	private Date expectedDateOfOutcome;

	@Temporal(TemporalType.DATE)
	@Column(name = "target_bid_submission_date")
	private Date targetBidSubmissionDate;

	@Column(name = "win_probability")
	private String winProbability;

	@Column(name = "opportunity_id")
	private String opportunityId;

	@Column(name = "bid_request_type")
	private String bidRequestType;

	// bi-directional many-to-one association to BidRequestTypeMappingT
	@ManyToOne
	@JoinColumn(name = "bid_request_type", insertable = false, updatable = false)
	private BidRequestTypeMappingT bidRequestTypeMappingT;

	// bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name = "opportunity_id", insertable = false, updatable = false)
	private OpportunityT opportunityT;

	// bi-directional many-to-one association to BidOfficeGroupOwnerLinkT
	@OneToMany(mappedBy = "bidDetailsT")
	private List<BidOfficeGroupOwnerLinkT> bidOfficeGroupOwnerLinkTs;

	// bi-directional many-to-one association to OpportunityTimelineHistoryT
	@OneToMany(mappedBy = "bidDetailsT")
	private List<OpportunityTimelineHistoryT> opportunityTimelineHistoryTs;

	public BidDetailsT() {
	}

	public String getBidId() {
		return this.bidId;
	}

	public void setBidId(String bidId) {
		this.bidId = bidId;
	}

	public Date getActualBidSubmissionDate() {
		return this.actualBidSubmissionDate;
	}

	public void setActualBidSubmissionDate(Date actualBidSubmissionDate) {
		this.actualBidSubmissionDate = actualBidSubmissionDate;
	}

	public Date getBidRequestReceiveDate() {
		return this.bidRequestReceiveDate;
	}

	public void setBidRequestReceiveDate(Date bidRequestReceiveDate) {
		this.bidRequestReceiveDate = bidRequestReceiveDate;
	}

	public String getCoreAttributesUsedForWinning() {
		return this.coreAttributesUsedForWinning;
	}

	public void setCoreAttributesUsedForWinning(
			String coreAttributesUsedForWinning) {
		this.coreAttributesUsedForWinning = coreAttributesUsedForWinning;
	}

	public String getCreatedBy() {

		return this.createdBy;

	}

	public void setCreatedBy(String createdBy) {

		this.createdBy = createdBy;

	}

	public Timestamp getCreatedDatetime() {

		return this.createdDatetime;

	}

	public void setCreatedDatetime(Timestamp createdDatetime) {

		this.createdDatetime = createdDatetime;

	}

	public UserT getCreatedByUser() {

		return this.createdByUser;

	}

	public void setCreatedByUser(UserT createdByUser) {

		this.createdByUser = createdByUser;

	}

	public String getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;

	}

	public UserT getModifiedByUser() {
		return this.modifiedByUser;
	}

	public void setModifiedByUser(UserT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;

	}

	public Date getExpectedDateOfOutcome() {
		return this.expectedDateOfOutcome;
	}

	public void setExpectedDateOfOutcome(Date expectedDateOfOutcome) {
		this.expectedDateOfOutcome = expectedDateOfOutcome;
	}

	public Date getTargetBidSubmissionDate() {
		return this.targetBidSubmissionDate;
	}

	public void setTargetBidSubmissionDate(Date targetBidSubmissionDate) {
		this.targetBidSubmissionDate = targetBidSubmissionDate;
	}

	public String getWinProbability() {
		return this.winProbability;
	}

	public void setWinProbability(String winProbability) {
		this.winProbability = winProbability;
	}

	public BidRequestTypeMappingT getBidRequestTypeMappingT() {
		return this.bidRequestTypeMappingT;
	}

	public void setBidRequestTypeMappingT(
			BidRequestTypeMappingT bidRequestTypeMappingT) {
		this.bidRequestTypeMappingT = bidRequestTypeMappingT;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

	public List<BidOfficeGroupOwnerLinkT> getBidOfficeGroupOwnerLinkTs() {
		return this.bidOfficeGroupOwnerLinkTs;
	}

	public void setBidOfficeGroupOwnerLinkTs(
			List<BidOfficeGroupOwnerLinkT> bidOfficeGroupOwnerLinkTs) {
		this.bidOfficeGroupOwnerLinkTs = bidOfficeGroupOwnerLinkTs;
	}

	public BidOfficeGroupOwnerLinkT addBidOfficeGroupOwnerLinkT(
			BidOfficeGroupOwnerLinkT bidOfficeGroupOwnerLinkT) {
		getBidOfficeGroupOwnerLinkTs().add(bidOfficeGroupOwnerLinkT);
		bidOfficeGroupOwnerLinkT.setBidDetailsT(this);

		return bidOfficeGroupOwnerLinkT;
	}

	public BidOfficeGroupOwnerLinkT removeBidOfficeGroupOwnerLinkT(
			BidOfficeGroupOwnerLinkT bidOfficeGroupOwnerLinkT) {
		getBidOfficeGroupOwnerLinkTs().remove(bidOfficeGroupOwnerLinkT);
		bidOfficeGroupOwnerLinkT.setBidDetailsT(null);

		return bidOfficeGroupOwnerLinkT;
	}

	public List<OpportunityTimelineHistoryT> getOpportunityTimelineHistoryTs() {
		return this.opportunityTimelineHistoryTs;
	}

	public void setOpportunityTimelineHistoryTs(
			List<OpportunityTimelineHistoryT> opportunityTimelineHistoryTs) {
		this.opportunityTimelineHistoryTs = opportunityTimelineHistoryTs;
	}

	public OpportunityTimelineHistoryT addOpportunityTimelineHistoryT(
			OpportunityTimelineHistoryT opportunityTimelineHistoryT) {
		getOpportunityTimelineHistoryTs().add(opportunityTimelineHistoryT);
		opportunityTimelineHistoryT.setBidDetailsT(this);

		return opportunityTimelineHistoryT;
	}

	public OpportunityTimelineHistoryT removeOpportunityTimelineHistoryT(
			OpportunityTimelineHistoryT opportunityTimelineHistoryT) {
		getOpportunityTimelineHistoryTs().remove(opportunityTimelineHistoryT);
		opportunityTimelineHistoryT.setBidDetailsT(null);

		return opportunityTimelineHistoryT;
	}

	public String getBidRequestType() {
		return bidRequestType;
	}

	public void setBidRequestType(String bidRequestType) {
		this.bidRequestType = bidRequestType;
	}

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

}