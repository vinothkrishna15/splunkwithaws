package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AuditOpportunityHistoryDTO implements Serializable, Comparable<AuditOpportunityHistoryDTO>{

	private static final long serialVersionUID = 1L;
	
	private int salesStageCode;
	private Date startDate;
	private List<AuditHistoryDTO> histories;
	private String userName;
	
	public int getSalesStageCode() {
		return salesStageCode;
	}
	public void setSalesStageCode(int salesStageCode) {
		this.salesStageCode = salesStageCode;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	@Override
	public int compareTo(AuditOpportunityHistoryDTO o) {
		return this.startDate.after(o.getStartDate()) ? 1 : (this.startDate.before(o.getStartDate()) ? -1 : 0);
	}
	
}
