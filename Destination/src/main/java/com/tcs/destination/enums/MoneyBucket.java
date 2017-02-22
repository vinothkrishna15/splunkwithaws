package com.tcs.destination.enums;

import com.tcs.destination.utils.Constants;

public enum MoneyBucket {
	
	LESS_HUNDRED("<100K",0,100000),
	LESS_FIVE_HUNDRED("100K-500K", 100000, 500000),
	LESS_ONE_MILLION("500K-1M", 500000,1000000),
	ONE_MILLION_PLUS("1M+", 1000000, Constants.MAX_DEAL_VALUE);
	
	private final String label;
	private final Integer minValue;
	private final Integer maxValue;

	private MoneyBucket(String label, Integer minValue, Integer maxValue) {
		this.label = label;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public String getLabel() {
		return label;
	}

	public Integer getMinValue() {
		return minValue;
	}

	public Integer getMaxValue() {
		return maxValue;
	}

	public static boolean contains(String test) {

		for (MoneyBucket c : MoneyBucket.values()) {
			if (c.name().equals(test)) {
				return true;
			}
		}

		return false;
	}

}
