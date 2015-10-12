package com.tcs.destination.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class BDMPerfromanceGeoIouDashboardResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BigDecimal winsTarget;
	
	private BigDecimal pipelineFunnelTarget;
	
	private int dealsAboveTwentyMillionTarget;
	
	private int dealsAboveTenMillionTarget;
	
	private int digitalReimaginationDealsTarget;

	private double overAllWinRatioTarget;
	
	private double accountsWithSpPenetrationAboveThreeTarget;
	
	private List<GeoIouDashboardDTO> geoOrIouHeadAchieved;
	
	private List<PipelineDTO> pipelineFunnelAchieved;
	
	public List<PipelineDTO> getPipelineFunnelAchieved() {
		return pipelineFunnelAchieved;
	}

	public void setPipelineFunnelAchieved(List<PipelineDTO> pipelineFunnelAchieved) {
		this.pipelineFunnelAchieved = pipelineFunnelAchieved;
	}

	public BigDecimal getWinsTarget() {
		return winsTarget;
	}

	public void setWinsTarget(BigDecimal winsTarget) {
		this.winsTarget = winsTarget;
	}

	public BigDecimal getPipelineFunnelTarget() {
		return pipelineFunnelTarget;
	}

	public void setPipelineFunnelTarget(BigDecimal pipelineFunnelTarget) {
		this.pipelineFunnelTarget = pipelineFunnelTarget;
	}

	public int getDealsAboveTwentyMillionTarget() {
		return dealsAboveTwentyMillionTarget;
	}

	public void setDealsAboveTwentyMillionTarget(int dealsAboveTwentyMillionTarget) {
		this.dealsAboveTwentyMillionTarget = dealsAboveTwentyMillionTarget;
	}

	public int getDealsAboveTenMillionTarget() {
		return dealsAboveTenMillionTarget;
	}

	public void setDealsAboveTenMillionTarget(int dealsAboveTenMillionTarget) {
		this.dealsAboveTenMillionTarget = dealsAboveTenMillionTarget;
	}

	public int getDigitalReimaginationDealsTarget() {
		return digitalReimaginationDealsTarget;
	}

	public void setDigitalReimaginationDealsTarget(
			int digitalReimaginationDealsTarget) {
		this.digitalReimaginationDealsTarget = digitalReimaginationDealsTarget;
	}

	public double getOverAllWinRatioTarget() {
		return overAllWinRatioTarget;
	}

	public void setOverAllWinRatioTarget(double overAllWinRatioTarget) {
		this.overAllWinRatioTarget = overAllWinRatioTarget;
	}

	public double getAccountsWithSpPenetrationAboveThreeTarget() {
		return accountsWithSpPenetrationAboveThreeTarget;
	}

	public void setAccountsWithSpPenetrationAboveThreeTarget(
			double accountsWithSpPenetrationAboveThreeTarget) {
		this.accountsWithSpPenetrationAboveThreeTarget = accountsWithSpPenetrationAboveThreeTarget;
	}

	public List<GeoIouDashboardDTO> getGeoOrIouHeadAchieved() {
		return geoOrIouHeadAchieved;
	}

	public void setGeoOrIouHeadAchieved(
			List<GeoIouDashboardDTO> geoOrIouHeadAchieved) {
		this.geoOrIouHeadAchieved = geoOrIouHeadAchieved;
	}


}
