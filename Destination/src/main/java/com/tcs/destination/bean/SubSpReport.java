package com.tcs.destination.bean;

import java.math.BigDecimal;

public class SubSpReport {

	private String displaySubSp;
	private BigDecimal actualRevenue;
	private BigDecimal digitalDealValue;

	public String getDisplaySubSp() {
		return displaySubSp;
	}

	public void setDisplaySubSp(String displaySubSp) {
		this.displaySubSp = displaySubSp;
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
