package com.tcs.destination.bean;

import java.math.BigDecimal;

public class IOUReport {

	private String displayIOU;
	private BigDecimal actualRevenue;
	private BigDecimal digitalDealValue;

	public String getDisplayIOU() {
		return displayIOU;
	}

	public void setDisplayIOU(String displayIOU) {
		this.displayIOU = displayIOU;
	}

	public BigDecimal getActualRevenue() {
		return actualRevenue;
	}

	public void setActualRevenue(BigDecimal actualRevenue) {
		this.actualRevenue = actualRevenue;
	}

	public BigDecimal getDigitalDealValue() {
		return digitalDealValue;
	}

	public void setDigitalDealValue(BigDecimal digitalDealValue) {
		this.digitalDealValue = digitalDealValue;
	}

}
