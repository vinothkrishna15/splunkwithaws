package com.tcs.destination.bean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class TargetVsActualQuarter {

	private String quarter;

	@JsonIgnore
	private BigDecimal target;

	@JsonIgnore
	private BigDecimal actual;

	@JsonIgnore
	private BigDecimal projected;

	@JsonIgnore
	private BigDecimal revenue;

	private List<CurrencyValue> targetValues;

	private List<CurrencyValue> actualValues;

	private List<CurrencyValue> projectedValues;

	private List<CurrencyValue> revenueValues;

	public String getQuarter() {
		return quarter;
	}

	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}

	/**
	 * Get Target value
	 * 
	 * @return Value in INR (as in DB)
	 */
	public BigDecimal getTarget() {
		return target;
	}

	public void setTarget(BigDecimal target) {
		this.target = target;
	}

	/**
	 * Get Actual value
	 * 
	 * @return Value in INR (as in DB)
	 */
	public BigDecimal getActual() {
		return actual;
	}

	public void setActual(BigDecimal actual) {
		this.actual = actual;
	}

	/**
	 * Get Projected value
	 * 
	 * @return Value in INR (as in DB)
	 */
	public BigDecimal getProjected() {
		return projected;
	}

	public void setProjected(BigDecimal projected) {
		this.projected = projected;
	}

	/**
	 * Manipulates the target achieved and returns the value based on values
	 * available in the bean
	 * 
	 * @return Target Achieved in % , in range (0,1)
	 */
	public BigDecimal getTargetAchieved() {
		BigDecimal targetAchieved = new BigDecimal(0);
		if (actual != null)
			targetAchieved = targetAchieved.add(actual);
		if (projected != null)
			targetAchieved = targetAchieved.add(projected);
		if (target == null || target.compareTo(BigDecimal.ZERO) == 0)
			return BigDecimal.ZERO;
		else
			return targetAchieved.divide(target, 2, RoundingMode.HALF_UP);
	}

	public List<CurrencyValue> getTargetValues() {
		if (targetValues == null) {
			targetValues = new ArrayList<CurrencyValue>();
		}
		return targetValues;
	}

	public void setTargetValues(List<CurrencyValue> targetValues) {
		this.targetValues = targetValues;
	}

	public List<CurrencyValue> getActualValues() {
		if (actualValues == null) {
			actualValues = new ArrayList<CurrencyValue>();
		}
		return actualValues;
	}

	public void setActualValues(List<CurrencyValue> actualValues) {
		this.actualValues = actualValues;
	}

	public List<CurrencyValue> getProjectedValues() {
		if (projectedValues == null) {
			projectedValues = new ArrayList<CurrencyValue>();
		}
		return projectedValues;
	}

	public void setProjectedValues(List<CurrencyValue> projectedValues) {
		this.projectedValues = projectedValues;
	}

	public BigDecimal getRevenue() {
		if (revenue == null) {
			revenue = new BigDecimal(0);
			if (actual != null)
				revenue = revenue.add(actual);
			if (projected != null)
				revenue = revenue.add(projected);
		}
		return revenue;
	}

	public List<CurrencyValue> getRevenueValues() {
		if (revenueValues == null) {
			revenueValues = new ArrayList<CurrencyValue>();
		}
		return revenueValues;
	}

	public void setRevenueValues(List<CurrencyValue> revenueValues) {
		this.revenueValues = revenueValues;
	}

}
