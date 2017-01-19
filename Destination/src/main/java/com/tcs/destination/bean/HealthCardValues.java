package com.tcs.destination.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.NON_NULL)
public class HealthCardValues {

	private Date date;
	private BigDecimal overallPercentage;
	private String category;
	private List<ClusterList> clusterList;
	private Timestamp uploadedDate;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public BigDecimal getOverallPercentage() {
		return overallPercentage;
	}
	public void setOverallPercentage(BigDecimal overallPercentage) {
		this.overallPercentage = overallPercentage;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public List<ClusterList> getClusterList() {
		return clusterList;
	}
	public void setClusterList(List<ClusterList> clusterList) {
		this.clusterList = clusterList;
	}
	public Timestamp getUploadedDate() {
		return uploadedDate;
	}
	public void setUploadedDate(Timestamp uploadedDate) {
		this.uploadedDate = uploadedDate;
	}
}
