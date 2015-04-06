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
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the bid_office_group_owner_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Entity
@Table(name = "bid_office_group_owner_link_t")
@NamedQuery(name = "BidOfficeGroupOwnerLinkT.findAll", query = "SELECT b FROM BidOfficeGroupOwnerLinkT b")
public class BidOfficeGroupOwnerLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bid_office_group_owner_link_id")
	private String bidOfficeGroupOwnerLinkId;

	@Column(name = "created_modified_by")
	private String createdModifiedBy;

	@Column(name = "created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name = "bid_office_group_owner")
	private String bidOfficeGroupOwner;

	@Column(name = "bid_id")
	private String bidId;

	// bi-directional many-to-one association to BidDetailsT
	@ManyToOne
	@JoinColumn(name = "bid_id", insertable = false, updatable = false)
	private BidDetailsT bidDetailsT;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_modified_by", insertable = false, updatable = false)
	private UserT createdModifiedUser;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="bid_office_group_owner", insertable = false, updatable = false)
	private UserT bidOfficeGroupOwnerUser;

	public BidOfficeGroupOwnerLinkT() {
	}

	public String getBidId() {
		return bidId;
	}

	public void setBidId(String bidId) {
		this.bidId = bidId;
	}

	public String getBidOfficeGroupOwnerLinkId() {
		return this.bidOfficeGroupOwnerLinkId;
	}

	public void setBidOfficeGroupOwnerLinkId(String bidOfficeGroupOwnerLinkId) {
		this.bidOfficeGroupOwnerLinkId = bidOfficeGroupOwnerLinkId;
	}

	public String getCreatedModifiedBy() {
		return this.createdModifiedBy;
	}

	public void setCreatedModifiedBy(String createdModifiedBy) {
		this.createdModifiedBy = createdModifiedBy;
	}

	public UserT getBidOfficeGroupOwnerUser() {
		return this.bidOfficeGroupOwnerUser;
	}

	public void setBidOfficeGroupOwnerUser(UserT bidOfficeGroupOwnerUser) {
		this.bidOfficeGroupOwnerUser = bidOfficeGroupOwnerUser;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return this.createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
	}

	public BidDetailsT getBidDetailsT() {
		return this.bidDetailsT;
	}

	public void setBidDetailsT(BidDetailsT bidDetailsT) {
		this.bidDetailsT = bidDetailsT;
	}

	public UserT getCreatedModifiedUser() {
		return this.createdModifiedUser;
	}

	public void setCreatedModifiedUser(UserT createdModifiedUser) {
		this.createdModifiedUser = createdModifiedUser;
	}

	public String getBidOfficeGroupOwner() {
		return bidOfficeGroupOwner;
	}

	public void setBidOfficeGroupOwner(String bidOfficeGroupOwner) {
		this.bidOfficeGroupOwner = bidOfficeGroupOwner;
	}

}