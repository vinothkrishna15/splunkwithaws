package com.tcs.destination.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class BDMSupervisorDashboardDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BigDecimal totalOpportunityWinsAchieved;
	
	private int totalProposalSupportedAchieved;
	
	private int totalConnectSupportedAchieved;
	
	private List<DashBoardBDMResponse> bdmSupervisorDashboard;

	public BigDecimal getTotalOpportunityWinsAchieved() {
		if(totalOpportunityWinsAchieved!=null)
		totalOpportunityWinsAchieved = totalOpportunityWinsAchieved.setScale(2, RoundingMode.CEILING);
		return totalOpportunityWinsAchieved;
	}

	public void setTotalOpportunityWinsAchieved(
			BigDecimal totalOpportunityWinsAchieved) {
		this.totalOpportunityWinsAchieved = totalOpportunityWinsAchieved;
	}

	public int getTotalProposalSupportedAchieved() {
		return totalProposalSupportedAchieved;
	}

	public void setTotalProposalSupportedAchieved(int totalProposalSupportedAchieved) {
		this.totalProposalSupportedAchieved = totalProposalSupportedAchieved;
	}

	public int getTotalConnectSupportedAchieved() {
		return totalConnectSupportedAchieved;
	}

	public void setTotalConnectSupportedAchieved(int totalConnectSupportedAchieved) {
		this.totalConnectSupportedAchieved = totalConnectSupportedAchieved;
	}

	public List<DashBoardBDMResponse> getBdmSupervisorDashboard() {
		return bdmSupervisorDashboard;
	}

	public void setBdmSupervisorDashboard(
			List<DashBoardBDMResponse> bdmSupervisorDashboard) {
		this.bdmSupervisorDashboard = bdmSupervisorDashboard;
	}

}
