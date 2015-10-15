package com.tcs.destination.bean;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class PipelineDTO {

	private String salesStageCode;
	
	private BigDecimal achieved;

	public String getSalesStageCode() {
		return salesStageCode;
	}

	public void setSalesStageCode(String salesStageCode) {
		this.salesStageCode = salesStageCode;
	}

	public BigDecimal getAchieved() {
		achieved = achieved.setScale(2, RoundingMode.CEILING);
		return achieved;
	}

	public void setAchieved(BigDecimal achieved) {
		this.achieved = achieved;
	}
	
	
	
}
