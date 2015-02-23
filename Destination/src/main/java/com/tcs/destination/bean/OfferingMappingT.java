package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the offering_mapping_t database table.
 * 
 */
@Entity
@Table(name="offering_mapping_t")
@NamedQuery(name="OfferingMappingT.findAll", query="SELECT o FROM OfferingMappingT o")
public class OfferingMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="offering_id")
	private Integer offeringId;

	@Column(name="offering")
	private String offering;

	@Column(name="sub_sp")
	private String subSp;

	public OfferingMappingT() {
	}

	public Integer getOfferingId() {
		return this.offeringId;
	}

	public void setOfferingId(Integer offeringId) {
		this.offeringId = offeringId;
	}

	public String getOffering() {
		return this.offering;
	}

	public void setOffering(String offering) {
		this.offering = offering;
	}

	public String getSubSp() {
		return this.subSp;
	}

	public void setSubSp(String subSp) {
		this.subSp = subSp;
	}

}