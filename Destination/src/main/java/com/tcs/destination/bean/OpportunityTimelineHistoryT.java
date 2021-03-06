package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the opportunity_timeline_history_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "opportunityTimelineHistoryId")
@Entity
@Table(name = "opportunity_timeline_history_t")
@NamedQuery(name = "OpportunityTimelineHistoryT.findAll", query = "SELECT o FROM OpportunityTimelineHistoryT o")
public class OpportunityTimelineHistoryT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "opportunity_timeline_history_id")
	private String opportunityTimelineHistoryId;

	@Column(name = "updated_datetime")
	private Timestamp updatedDatetime;

	@Column(name = "bid_id")
	private String bidId;

	@Column(name = "opportunity_id")
	private String opportunityId;

	@Column(name = "sales_stage_code")
	private int salesStageCode;

	@Column(name = "user_updated")
	private String userUpdated;

	// bi-directional many-to-one association to BidDetailsT
	@ManyToOne
	@JoinColumn(name = "bid_id", insertable = false, updatable = false)
	private BidDetailsT bidDetailsT;

	// bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name = "opportunity_id", insertable = false, updatable = false)
	private OpportunityT opportunityT;

	// bi-directional many-to-one association to SalesStageMappingT
	@ManyToOne
	@JoinColumn(name = "sales_stage_code", insertable = false, updatable = false)
	private SalesStageMappingT salesStageMappingT;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "user_updated", insertable = false, updatable = false)
	private UserT userT;

	public OpportunityTimelineHistoryT() {
	}

	public String getOpportunityTimelineHistoryId() {
		return this.opportunityTimelineHistoryId;
	}

	public void setOpportunityTimelineHistoryId(
			String opportunityTimelineHistoryId) {
		this.opportunityTimelineHistoryId = opportunityTimelineHistoryId;
	}

	public Timestamp getUpdatedDatetime() {
		return this.updatedDatetime;
	}

	public void setUpdatedDatetime(Timestamp updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
	}

	public BidDetailsT getBidDetailsT() {
		return this.bidDetailsT;
	}

	public void setBidDetailsT(BidDetailsT bidDetailsT) {
		this.bidDetailsT = bidDetailsT;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

	public SalesStageMappingT getSalesStageMappingT() {
		return this.salesStageMappingT;
	}

	public void setSalesStageMappingT(SalesStageMappingT salesStageMappingT) {
		this.salesStageMappingT = salesStageMappingT;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

	public String getBidId() {
		return bidId;
	}

	public void setBidId(String bidId) {
		this.bidId = bidId;
	}

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public int getSalesStageCode() {
		return salesStageCode;
	}

	public void setSalesStageCode(int salesStageCode) {
		this.salesStageCode = salesStageCode;
	}

	public String getUserUpdated() {
		return userUpdated;
	}

	public void setUserUpdated(String userUpdated) {
		this.userUpdated = userUpdated;
	}
	
	

}