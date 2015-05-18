package com.tcs.destination.bean;

import java.math.BigDecimal;

public class SubSpReport {

	private String displaySubSp;
    private	BigDecimal actualRevenue;
	
	
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
	
}
