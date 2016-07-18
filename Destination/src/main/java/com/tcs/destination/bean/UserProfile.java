package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

public class UserProfile implements Serializable {

	private static final long serialVersionUID = 1L;

	private UserT userT;
	
	private List<UserT> reportingHierarchy;
	
	private List<UserT> reportees;

	public UserT getUserT() {
		return userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

	public List<UserT> getReportees() {
		return reportees;
	}

	public void setReportees(List<UserT> reportees) {
		this.reportees = reportees;
	}

	public List<UserT> getReportingHierarchy() {
		return reportingHierarchy;
	}

	public void setReportingHierarchy(List<UserT> reportingHierarchy) {
		this.reportingHierarchy = reportingHierarchy;
	}
	
}