package com.tcs.destination.bean;

import java.util.List;

public class ReportsOpportunity {

	private List<ReportsSalesStage> salesStageList;

	private String overallBidValue;

	public List<ReportsSalesStage> getSalesStageList() {
		return salesStageList;
	}

	public void setSalesStageList(List<ReportsSalesStage> salesStageList) {
		this.salesStageList = salesStageList;
	}

	public String getOverallBidValue() {
		return overallBidValue;
	}

	public void setOverallBidValue(String overallBidValue) {
		this.overallBidValue = overallBidValue;
	}

}
