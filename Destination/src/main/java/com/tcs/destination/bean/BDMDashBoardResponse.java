package com.tcs.destination.bean;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class BDMDashBoardResponse {

	private String timeLine;
	
	private BigDecimal primaryOrBidOppWinsAchieved;
	
	private BigDecimal salesOwnerOppWinsAchieved;
	
	private BigDecimal totalOppWinsAchieved;
	
	private int primaryProposalSupportAchieved;
	
	private int salesProposalSupportAchieved;
	
	private int totalProposalSupportAchieved;
	
	private int connectPrimary;
	
	private int connectSecondary;
	
	private int totalConnects;
	
	
	
	public String getTimeLine() {
		return timeLine;
	}

	public void setTimeLine(String timeLine) {
		this.timeLine = timeLine;
	}

	public BigDecimal getPrimaryOrBidOppWinsAchieved() {
		if(primaryOrBidOppWinsAchieved!=null){
		primaryOrBidOppWinsAchieved = primaryOrBidOppWinsAchieved.setScale(2, RoundingMode.CEILING);
		}
		return primaryOrBidOppWinsAchieved;
	}

	public void setPrimaryOrBidOppWinsAchieved(
			BigDecimal primaryOrBidOppWinsAchieved) {
		this.primaryOrBidOppWinsAchieved = primaryOrBidOppWinsAchieved;
	}

	public BigDecimal getSalesOwnerOppWinsAchieved() {
		if(salesOwnerOppWinsAchieved!=null){
		salesOwnerOppWinsAchieved = salesOwnerOppWinsAchieved.setScale(2, RoundingMode.CEILING);
		}
		return salesOwnerOppWinsAchieved;
	}

	public void setSalesOwnerOppWinsAchieved(BigDecimal salesOwnerOppWinsAchieved) {
		this.salesOwnerOppWinsAchieved = salesOwnerOppWinsAchieved;
	}

	public BigDecimal getTotalOppWinsAchieved() {
		if(totalOppWinsAchieved!=null){
			totalOppWinsAchieved = totalOppWinsAchieved.setScale(2, RoundingMode.CEILING);
			}
		//totalOppWinsAchieved = primaryOrBidOppWinsAchieved.add(salesOwnerOppWinsAchieved);
		return totalOppWinsAchieved;
	}

	public void setTotalOppWinsAchieved(BigDecimal totalOppWinsAchieved) {
		this.totalOppWinsAchieved = totalOppWinsAchieved;
	}

	public int getPrimaryProposalSupportAchieved() {
		return primaryProposalSupportAchieved;
	}

	public void setPrimaryProposalSupportAchieved(int primaryProposalSupportAchieved) {
		this.primaryProposalSupportAchieved = primaryProposalSupportAchieved;
	}

	public int getSalesProposalSupportAchieved() {
		return salesProposalSupportAchieved;
	}

	public void setSalesProposalSupportAchieved(int salesProposalSupportAchieved) {
		this.salesProposalSupportAchieved = salesProposalSupportAchieved;
	}

	public int getTotalProposalSupportAchieved() {
		totalProposalSupportAchieved = primaryProposalSupportAchieved+salesProposalSupportAchieved;
		return totalProposalSupportAchieved;
	}

	public void setTotalProposalSupportAchieved(int totalProposalSupportAchieved) {
		this.totalProposalSupportAchieved = totalProposalSupportAchieved;
	}

	public int getConnectPrimary() {
		return connectPrimary;
	}

	public void setConnectPrimary(int connectPrimary) {
		this.connectPrimary = connectPrimary;
	}

	public int getConnectSecondary() {
		return connectSecondary;
	}

	public void setConnectSecondary(int connectSecondary) {
		this.connectSecondary = connectSecondary;
	}

	public int getTotalConnects() {
		totalConnects = connectPrimary+connectSecondary;
		return totalConnects;
	}

	public void setTotalConnects(int totalConnects) {
		this.totalConnects = totalConnects;
	}
	
}
