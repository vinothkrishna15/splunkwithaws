package com.tcs.destination.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class DashBoardBDMResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userId;
	
	private BigDecimal winsTarget;
	
	private int proposalSupportedTarget;
	
	private int connectSupportedTarget;
	
	private List<BDMDashBoardResponse> bdmDashboard;
	
	public List<BDMDashBoardResponse> getBdmDashboard() {
		return bdmDashboard;
	}

	public void setBdmDashboard(List<BDMDashBoardResponse> bdmDashboard) {
		this.bdmDashboard = bdmDashboard;
	}

	public BigDecimal getWinsTarget() {
		if(winsTarget!=null)
		winsTarget = winsTarget.setScale(2, RoundingMode.CEILING);
		return winsTarget;
	}

	public void setWinsTarget(BigDecimal winsTarget) {
		this.winsTarget = winsTarget;
	}

	public int getProposalSupportedTarget() {
		return proposalSupportedTarget;
	}

	public void setProposalSupportedTarget(int proposalSupportedTarget) {
		this.proposalSupportedTarget = proposalSupportedTarget;
	}

	public int getConnectSupportedTarget() {
		return connectSupportedTarget;
	}

	public void setConnectSupportedTarget(int connectSupportedTarget) {
		this.connectSupportedTarget = connectSupportedTarget;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
