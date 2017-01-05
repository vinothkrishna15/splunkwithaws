package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The ActualRevenuesDataDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class CompetitorOpportunityWrapperDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String competitorName;
	private BigDecimal winValue;
	private BigInteger winCount;
	private BigDecimal lossValue;
	private BigInteger lossCount;
	private BigDecimal piplineValue;
	private BigInteger pipelineCount;

	public CompetitorOpportunityWrapperDTO() {
		super();
	}

	public String getCompetitorName() {
		return competitorName;
	}

	public void setCompetitorName(String competitorName) {
		this.competitorName = competitorName;
	}

	public BigDecimal getWinValue() {
		return winValue;
	}

	public void setWinValue(BigDecimal winValue) {
		this.winValue = winValue;
	}

	public BigInteger getWinCount() {
		return winCount;
	}

	public void setWinCount(BigInteger winCount) {
		this.winCount = winCount;
	}

	public BigDecimal getLossValue() {
		return lossValue;
	}

	public void setLossValue(BigDecimal lossValue) {
		this.lossValue = lossValue;
	}

	public BigInteger getLossCount() {
		return lossCount;
	}

	public void setLossCount(BigInteger lossCount) {
		this.lossCount = lossCount;
	}

	public BigDecimal getPiplineValue() {
		return piplineValue;
	}

	public void setPiplineValue(BigDecimal piplineValue) {
		this.piplineValue = piplineValue;
	}

	public BigInteger getPipelineCount() {
		return pipelineCount;
	}

	public void setPipelineCount(BigInteger pipelineCount) {
		this.pipelineCount = pipelineCount;
	}

}
