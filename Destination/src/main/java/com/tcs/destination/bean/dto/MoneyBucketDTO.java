package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class MoneyBucketDTO  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer minValue;
	private Integer maxValue;
	private Integer winCount;
	private Integer lossCount;
	private BigDecimal winValue;
	private BigDecimal lossValue;
	private BigDecimal winRatio;
	
	public Integer getMinValue() {
		return minValue;
	}
	public void setMinValue(Integer minValue) {
		this.minValue = minValue;
	}
	public Integer getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}
	public Integer getWinCount() {
		return winCount;
	}
	public void setWinCount(Integer winCount) {
		this.winCount = winCount;
	}
	public Integer getLossCount() {
		return lossCount;
	}
	public void setLossCount(Integer lossCount) {
		this.lossCount = lossCount;
	}
	public BigDecimal getWinRatio() {
		return winRatio;
	}
	public void setWinRatio(BigDecimal winRatio) {
		this.winRatio = winRatio;
	}
	public BigDecimal getWinValue() {
		return winValue;
	}
	public void setWinValue(BigDecimal winValue) {
		this.winValue = winValue;
	}
	public BigDecimal getLossValue() {
		return lossValue;
	}
	public void setLossValue(BigDecimal lossValue) {
		this.lossValue = lossValue;
	}
	
}
