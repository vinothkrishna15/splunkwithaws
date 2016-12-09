package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The persistent class for the bid_details_t database table.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class BidDetailsDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String bidId;
	private Date actualBidSubmissionDate;
	private Date bidRequestReceiveDate;
	private String coreAttributesUsedForWinning;
	private Timestamp createdDatetime;
	private UserDTO createdByUser;
	private Timestamp modifiedDatetime;
	private UserDTO modifiedByUser;
	private Date expectedDateOfOutcome;
	private Date targetBidSubmissionDate;
	private String winProbability;
	private String opportunityId;
	private String bidRequestType;
	private List<BidOfficeGroupOwnerLinkDTO> bidOfficeGroupOwnerLinkTs;
	
	public BidDetailsDTO() {
		super();
	}
	
	public String getBidId() {
		return bidId;
	}
	public void setBidId(String bidId) {
		this.bidId = bidId;
	}
	public Date getActualBidSubmissionDate() {
		return actualBidSubmissionDate;
	}
	public void setActualBidSubmissionDate(Date actualBidSubmissionDate) {
		this.actualBidSubmissionDate = actualBidSubmissionDate;
	}
	public Date getBidRequestReceiveDate() {
		return bidRequestReceiveDate;
	}
	public void setBidRequestReceiveDate(Date bidRequestReceiveDate) {
		this.bidRequestReceiveDate = bidRequestReceiveDate;
	}
	public String getCoreAttributesUsedForWinning() {
		return coreAttributesUsedForWinning;
	}
	public void setCoreAttributesUsedForWinning(String coreAttributesUsedForWinning) {
		this.coreAttributesUsedForWinning = coreAttributesUsedForWinning;
	}
	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}
	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}
	public UserDTO getCreatedByUser() {
		return createdByUser;
	}
	public void setCreatedByUser(UserDTO createdByUser) {
		this.createdByUser = createdByUser;
	}
	public Timestamp getModifiedDatetime() {
		return modifiedDatetime;
	}
	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}
	public UserDTO getModifiedByUser() {
		return modifiedByUser;
	}
	public void setModifiedByUser(UserDTO modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}
	public Date getExpectedDateOfOutcome() {
		return expectedDateOfOutcome;
	}
	public void setExpectedDateOfOutcome(Date expectedDateOfOutcome) {
		this.expectedDateOfOutcome = expectedDateOfOutcome;
	}
	public Date getTargetBidSubmissionDate() {
		return targetBidSubmissionDate;
	}
	public void setTargetBidSubmissionDate(Date targetBidSubmissionDate) {
		this.targetBidSubmissionDate = targetBidSubmissionDate;
	}
	public String getWinProbability() {
		return winProbability;
	}
	public void setWinProbability(String winProbability) {
		this.winProbability = winProbability;
	}
	public String getOpportunityId() {
		return opportunityId;
	}
	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}
	public String getBidRequestType() {
		return bidRequestType;
	}
	public void setBidRequestType(String bidRequestType) {
		this.bidRequestType = bidRequestType;
	}
	public List<BidOfficeGroupOwnerLinkDTO> getBidOfficeGroupOwnerLinkTs() {
		return bidOfficeGroupOwnerLinkTs;
	}
	public void setBidOfficeGroupOwnerLinkTs(
			List<BidOfficeGroupOwnerLinkDTO> bidOfficeGroupOwnerLinkTs) {
		this.bidOfficeGroupOwnerLinkTs = bidOfficeGroupOwnerLinkTs;
	}

}