package com.tcs.destination.bean;

import java.math.BigDecimal;

public class GeographyReport {

	private String geography;
    private	BigDecimal actualRevenue;
	
	public String getGeography() {
		return geography;
	}
	public void setGeography(String geography) {
		this.geography = geography;
	}
	
	public BigDecimal getActualRevenue() {
		return actualRevenue;
	}
	public void setActualRevenue(BigDecimal actualRevenue) {
		this.actualRevenue = actualRevenue;
	}
	
}
