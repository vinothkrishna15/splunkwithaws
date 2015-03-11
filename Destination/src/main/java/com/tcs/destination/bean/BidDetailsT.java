package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the bid_details_t database table.
 * 
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="bidId")
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
	@Column(name = "bid_request_receive_date")
	private Date bidRequestReceiveDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "bid_submission_date")
	private Date bidSubmissionDate;

	@Column(name = "created_modified_by")
	private String createdModifiedBy;

	@Column(name = "created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Temporal(TemporalType.DATE)
	@Column(name = "expected_date_of_outcome")
	private Date expectedDateOfOutcome;

	@Column(name = "core_attributes_used_for_winning")
	private String coreAttributesUsedForWinning;

	@Column(name = "win_probability")
	private String winProbability;

	// bi-directional many-to-one association to BidRequestTypeMappingT
	@ManyToOne
	@JoinColumn(name = "bid_request_type")
	private BidRequestTypeMappingT bidRequestTypeMappingT;

	// bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name = "opportunity_id")
	private OpportunityT opportunityT;

	// bi-directional many-to-one association to BidOfficeGroupOwnerLinkT
	@OneToMany(mappedBy = "bidDetailsT")
	private List<BidOfficeGroupOwnerLinkT> bidOfficeGroupOwnerLinkTs;

	public BidDetailsT() {
	}

	public String getBidId() {
		return this.bidId;
	}

	public void setBidId(String bidId) {
		this.bidId = bidId;
	}

	public Date getBidRequestReceiveDate() {
		return this.bidRequestReceiveDate;
	}

	public void setBidRequestReceiveDate(Date bidRequestReceiveDate) {
		this.bidRequestReceiveDate = bidRequestReceiveDate;
	}

	public Date getBidSubmissionDate() {
		return this.bidSubmissionDate;
	}

	public void setBidSubmissionDate(Date bidSubmissionDate) {
		this.bidSubmissionDate = bidSubmissionDate;
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

	public Date getExpectedDateOfOutcome() {
		return this.expectedDateOfOutcome;
	}

	public void setExpectedDateOfOutcome(Date expectedDateOfOutcome) {
		this.expectedDateOfOutcome = expectedDateOfOutcome;
	}

	public String getCoreAttributesUsedForWinning() {
		return this.coreAttributesUsedForWinning;
	}

	public void setCoreAttributesUsedForWinning(
			String coreAttributesUsedForWinning) {
		this.coreAttributesUsedForWinning = coreAttributesUsedForWinning;
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

}