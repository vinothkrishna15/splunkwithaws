package com.tcs.destination.bean;

public class EngagementDashboardDTO {
	private String engagementGroupedBy;
	private long engagementCount;
	
	public String getEngagementGroupedBy() {
		return engagementGroupedBy;
	}
	public void setEngagementGroupedBy(String engagementGroupedBy) {
		this.engagementGroupedBy = engagementGroupedBy;
	}
	public long getEngagementCount() {
		return engagementCount;
	}
	public void setEngagementCount(long engagementCount) {
		this.engagementCount = engagementCount;
	}
}
