package com.tcs.destination.bean;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class OpportunityDealValue {

	private String currency;

	private BigDecimal overallDealSize;

	private BigDecimal digitalDealValue;

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getOverallDealSize() {
		return overallDealSize;
	}

	public void setOverallDealSize(BigDecimal overallDealSize) {
		this.overallDealSize = overallDealSize;
	}

	public BigDecimal getDigitalDealValue() {
		return digitalDealValue;
	}

	public void setDigitalDealValue(BigDecimal digitalDealValue) {
		this.digitalDealValue = digitalDealValue;
	}

}
