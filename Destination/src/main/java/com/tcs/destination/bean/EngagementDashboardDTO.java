package com.tcs.destination.bean;

public class EngagementDashboardDTO {
	private String engagementGroupedBy;
	private Integer engagementCount;
	
	public String getEngagementGroupedBy() {
		return engagementGroupedBy;
	}
	public void setEngagementGroupedBy(String engagementGroupedBy) {
		this.engagementGroupedBy = engagementGroupedBy;
	}
	public Integer getEngagementCount() {
		return engagementCount;
	}
	public void setEngagementCount(Integer engagementCount) {
		this.engagementCount = engagementCount;
	}
}
