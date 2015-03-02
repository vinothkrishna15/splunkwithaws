package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;


/**
 * The persistent class for the bid_office_group_owner_link_t database table.
 * 
 */
@Entity
@Table(name="bid_office_group_owner_link_t")
@NamedQuery(name="BidOfficeGroupOwnerLinkT.findAll", query="SELECT b FROM BidOfficeGroupOwnerLinkT b")
public class BidOfficeGroupOwnerLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="bid_office_group_owner_link_id")
	private String bidOfficeGroupOwnerLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	//bi-directional many-to-one association to BidDetailsT
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="bid_id")
	private BidDetailsT bidDetailsT;

	//bi-directional many-to-one association to UserT
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="bid_office_group_owner")
	private UserT userT;

	public BidOfficeGroupOwnerLinkT() {
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

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

}