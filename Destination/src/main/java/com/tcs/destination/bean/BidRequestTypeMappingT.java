package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the bid_request_type_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="bidRequestType")
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
	@JsonIgnore
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