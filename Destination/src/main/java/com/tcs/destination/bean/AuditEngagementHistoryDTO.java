package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AuditEngagementHistoryDTO implements Serializable, Comparable<AuditEngagementHistoryDTO>{

	private static final long serialVersionUID = 1L;
	
	private int engagementStage;
	private Date startDate;
	private List<AuditHistoryDTO> histories;
	
	public int getEngagementStage() {
		return engagementStage;
	}
	public void setEngagementStage(int engagementStage) {
		this.engagementStage = engagementStage;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public List<AuditHistoryDTO> getHistories() {
		return histories;
	}
	public void setHistories(List<AuditHistoryDTO> histories) {
		this.histories = histories;
	}
	@Override
	public int compareTo(AuditEngagementHistoryDTO o) {
		return this.startDate.after(o.getStartDate()) ? 1 : (this.startDate.before(o.getStartDate()) ? -1 : 0);
	}
	
}
