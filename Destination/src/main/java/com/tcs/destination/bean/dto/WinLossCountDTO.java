package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class WinLossCountDTO  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Date fromDate;
	private Date toDate;
	private Integer winCount;
	private Integer lossCount;
	private BigDecimal winValue;
	private BigDecimal lossValue;
	private BigDecimal winRatio;
	
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
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
