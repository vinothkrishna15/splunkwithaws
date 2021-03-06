package com.tcs.destination.bean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class TargetVsActualYearToDate {

	private String financialYear;

	private List<TargetVsActualQuarter> quarterList;

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

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	/**
	 * Get Target value
	 * 
	 * @return Value in INR (as in DB)
	 */
	public BigDecimal getTarget() {
		if (target == null) {
			target = new BigDecimal(0);
			if (quarterList != null) {
				for (TargetVsActualQuarter quarter : quarterList) {
					if (quarter.getTarget() != null)
						target = target.add(quarter.getTarget());
				}
			}
		}

		return target;
	}

	/**
	 * Get Actual value
	 * 
	 * @return Value in INR (as in DB)
	 */
	public BigDecimal getActual() {
		if (actual == null) {
			actual = new BigDecimal(0);
			if (quarterList != null) {
				for (TargetVsActualQuarter quarter : quarterList) {
					if (quarter.getActual() != null)
						actual = actual.add(quarter.getActual());
				}
			}
		}
		return actual;
	}

	/**
	 * Get Projected value
	 * 
	 * @return Value in INR (as in DB)
	 */
	public BigDecimal getProjected() {
		if (projected == null) {
			projected = new BigDecimal(0);
			if (quarterList != null) {
				for (TargetVsActualQuarter quarter : quarterList) {
					if (quarter.getProjected() != null) {
						projected = projected.add(quarter.getProjected());
					}
				}
			}
		}
		return projected;
	}
	
	public BigDecimal getRevenue() {
		if (revenue == null) {
			revenue = new BigDecimal(0);
			if (actual != null)
				revenue = revenue.add(getActual());
			if (projected != null)
				revenue = revenue.add(getProjected());
		}
		return revenue;
	}
	

	/**
	 * Manipulates the target achieved and returns the value based on values
	 * available in the bean
	 * 
	 * @return Target Achieved in % , ranging (0,1)
	 */
	public BigDecimal getTargetAchieved() {

		if (target == null || target.compareTo(BigDecimal.ZERO) == 0)
			return BigDecimal.ZERO;
		else {
			return getRevenue().divide(target, 2, RoundingMode.HALF_UP);
		}
	}

	public List<TargetVsActualQuarter> getQuarterList() {
		return quarterList;
	}

	public void setQuarterList(List<TargetVsActualQuarter> quarterList) {
		this.quarterList = quarterList;
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
