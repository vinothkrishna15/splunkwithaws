package com.tcs.destination.bean;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class CurrencyValue {

	private String currency;

	private BigDecimal value;

	public String getCurrency() {
		return currency;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

}
