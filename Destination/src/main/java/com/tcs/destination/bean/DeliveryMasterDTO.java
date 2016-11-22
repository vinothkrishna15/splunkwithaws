package com.tcs.destination.bean;

import java.util.List;

public class DeliveryMasterDTO {
	private String viewEngagementBy;
	
	private List<EngagementDashboardDTO> engagementList;
	public List<EngagementDashboardDTO> getEngagementList() {
		return engagementList;
	}
	public void setEngagementList(List<EngagementDashboardDTO> engagementList) {
		this.engagementList = engagementList;
	}
	public String getViewEngagementBy() {
		return viewEngagementBy;
	}
	public void setViewEngagementBy(String viewEngagementBy) {
		this.viewEngagementBy = viewEngagementBy;
	}

}
