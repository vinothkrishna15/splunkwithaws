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
 * The persistent class for the bid_office_group_owner_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "bidOfficeGroupOwnerLinkId")
@Entity
@Table(name = "bid_office_group_owner_link_t")
@NamedQuery(name = "BidOfficeGroupOwnerLinkT.findAll", query = "SELECT b FROM BidOfficeGroupOwnerLinkT b")
public class BidOfficeGroupOwnerLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bid_office_group_owner_link_id")
	private String bidOfficeGroupOwnerLinkId;

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
	@JoinColumn(name = "bid_office_group_owner", insertable = false, updatable = false)
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

	public UserT getBidOfficeGroupOwnerUser() {
		return this.bidOfficeGroupOwnerUser;
	}

	public void setBidOfficeGroupOwnerUser(UserT bidOfficeGroupOwnerUser) {
		this.bidOfficeGroupOwnerUser = bidOfficeGroupOwnerUser;
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

	public BidDetailsT getBidDetailsT() {
		return this.bidDetailsT;
	}

	public void setBidDetailsT(BidDetailsT bidDetailsT) {
		this.bidDetailsT = bidDetailsT;
	}

	public String getBidOfficeGroupOwner() {
		return bidOfficeGroupOwner;
	}

	public void setBidOfficeGroupOwner(String bidOfficeGroupOwner) {
		this.bidOfficeGroupOwner = bidOfficeGroupOwner;
	}

}