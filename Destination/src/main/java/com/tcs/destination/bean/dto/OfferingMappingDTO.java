package com.tcs.destination.bean.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The persistent class for the offering_mapping_t database table.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class OfferingMappingDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String offering;
	private Boolean active;
	private Integer offeringId;
	private String subSp;

	public OfferingMappingDTO() {
		super();
	}

	public String getOffering() {
		return this.offering;
	}

	public void setOffering(String offering) {
		this.offering = offering;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Integer getOfferingId() {
		return this.offeringId;
	}

	public void setOfferingId(Integer offeringId) {
		this.offeringId = offeringId;
	}

	public String getSubSp() {
		return this.subSp;
	}

	public void setSubSp(String subSp) {
		this.subSp = subSp;
	}

}