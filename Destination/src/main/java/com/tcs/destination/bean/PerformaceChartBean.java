package com.tcs.destination.bean;

import java.math.BigDecimal;
import java.math.BigInteger;

import scala.math.BigInt;

public class PerformaceChartBean {

	private BigDecimal target;

	private BigDecimal winSum;

	private BigDecimal pipelineSum;

	public BigDecimal getTarget() {
		return target;
	}

	public void setTarget(BigDecimal target) {
		this.target = target;
	}

	public BigDecimal getWinSum() {
		return winSum;
	}

	public void setWinSum(BigDecimal winSum) {
		this.winSum = winSum;
	}

	public BigDecimal getPipelineSum() {
		return pipelineSum;
	}

	public void setPipelineSum(BigDecimal pipelineSum) {
		this.pipelineSum = pipelineSum;
	}

}
