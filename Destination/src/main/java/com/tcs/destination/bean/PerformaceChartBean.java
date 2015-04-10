package com.tcs.destination.bean;

import java.math.BigDecimal;
import java.math.BigInteger;

import scala.math.BigInt;

public class PerformaceChartBean {

	private BigDecimal target;

	private BigInteger winSum;

	private BigInteger pipelineSum;

	public BigDecimal getTarget() {
		return target;
	}

	public void setTarget(BigDecimal target) {
		this.target = target;
	}

	public BigInteger getWinSum() {
		return winSum;
	}

	public void setWinSum(BigInteger winSum) {
		this.winSum = winSum;
	}

	public BigInteger getPipelineSum() {
		return pipelineSum;
	}

	public void setPipelineSum(BigInteger pipelineSum) {
		this.pipelineSum = pipelineSum;
	}

}
