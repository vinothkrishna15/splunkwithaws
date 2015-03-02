package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;


/**
 * The persistent class for the bid_request_type_mapping_t database table.
 * 
 */
@Entity
@Table(name="bid_request_type_mapping_t")
@NamedQuery(name="BidRequestTypeMappingT.findAll", query="SELECT b FROM BidRequestTypeMappingT b")
public class BidRequestTypeMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="bid_request_type")
	private String bidRequestType;

	//bi-directional many-to-one association to BidDetailsT
	@OneToMany(mappedBy="bidRequestTypeMappingT")
	private List<BidDetailsT> bidDetailsTs;

	public BidRequestTypeMappingT() {
	}

	public String getBidRequestType() {
		return this.bidRequestType;
	}

	public void setBidRequestType(String bidRequestType) {
		this.bidRequestType = bidRequestType;
	}

	public List<BidDetailsT> getBidDetailsTs() {
		return this.bidDetailsTs;
	}

	public void setBidDetailsTs(List<BidDetailsT> bidDetailsTs) {
		this.bidDetailsTs = bidDetailsTs;
	}

	public BidDetailsT addBidDetailsT(BidDetailsT bidDetailsT) {
		getBidDetailsTs().add(bidDetailsT);
		bidDetailsT.setBidRequestTypeMappingT(this);

		return bidDetailsT;
	}

	public BidDetailsT removeBidDetailsT(BidDetailsT bidDetailsT) {
		getBidDetailsTs().remove(bidDetailsT);
		bidDetailsT.setBidRequestTypeMappingT(null);

		return bidDetailsT;
	}

}