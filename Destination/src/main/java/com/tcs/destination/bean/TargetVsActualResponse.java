package com.tcs.destination.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class TargetVsActualResponse implements Serializable{

	private static final long serialVersionUID = 1L;

	private BigDecimal target;

	private BigDecimal actual;

	private String quarter;

	public BigDecimal getTarget() {
		return target;
	}

	public void setTarget(BigDecimal target) {
		this.target = target;
	}

	public BigDecimal getActual() {
		return actual;
	}

	public void setActual(BigDecimal actual) {
		this.actual = actual;
	}

	public String getQuarter() {
		return quarter;
	}

	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}

}
