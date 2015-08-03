package com.tcs.destination.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;
/**
 * This DTO holds the Tasks Details of all subordinates under a supervisor
 * 
 * @author bnpp
 *
 */
@JsonFilter(Constants.FILTER)
public class OpportunitiesBySupervisorIdDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private String salesStageCode;
	
	private String salesStageDescription;
	
	private String salesCount;
	
	private String digitalDealValue;

	public String getSalesStageCode() {
		return salesStageCode;
	}

	public void setSalesStageCode(String salesStageCode) {
		this.salesStageCode = salesStageCode;
	}

	public String getSalesStageDescription() {
		return salesStageDescription;
	}

	public void setSalesStageDescription(String salesStageDescription) {
		this.salesStageDescription = salesStageDescription;
	}

	public String getSalesCount() {
		return salesCount;
	}

	public void setSalesCount(String salesCount) {
		this.salesCount = salesCount;
	}

	public String getDigitalDealValue() {
		return digitalDealValue;
	}

	public void setDigitalDealValue(String digitalDealValue) {
		this.digitalDealValue = digitalDealValue;
	}

	
}
