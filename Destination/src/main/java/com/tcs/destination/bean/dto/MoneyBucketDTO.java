package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class MoneyBucketDTO  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer minValue;
	private Integer maxValue;
	private String bucketLabel;
	private BigInteger winCount;
	private BigInteger lossCount;
	private BigDecimal winValue;
	private BigDecimal lossValue;
	private BigDecimal winRatio;
	
	private BigInteger count;
	private BigDecimal Value;
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
	public BigInteger getWinCount() {
		return winCount;
	}
	public void setWinCount(BigInteger winCount) {
		this.winCount = winCount;
	}
	public BigInteger getLossCount() {
		return lossCount;
	}
	public void setLossCount(BigInteger lossCount) {
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
	public String getBucketLabel() {
		return bucketLabel;
	}
	public void setBucketLabel(String bucketLabel) {
		this.bucketLabel = bucketLabel;
	}
	public BigInteger getCount() {
		return count;
	}
	public void setCount(BigInteger count) {
		this.count = count;
	}
	public BigDecimal getValue() {
		return Value;
	}
	public void setValue(BigDecimal value) {
		Value = value;
	}
	
	
}
