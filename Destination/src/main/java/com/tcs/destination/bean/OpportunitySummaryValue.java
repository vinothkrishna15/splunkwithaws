package com.tcs.destination.bean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class OpportunitySummaryValue {

	private String title;

	private BigInteger count;

	private BigDecimal bidValue;
	
	private int salesStageCode;
	
	private List<CurrencyValue> bidValues;
	
	public List<CurrencyValue> getBidValues() {
		if(bidValues == null){
			bidValues= new ArrayList<CurrencyValue>();
		}
		return bidValues;
	}

	public void setBidValues(List<CurrencyValue> bidValues) {
		this.bidValues = bidValues;
	}

	public int getSalesStageCode() {
		return salesStageCode;
	}

	public void setSalesStageCode(int salesStageCode) {
		this.salesStageCode = salesStageCode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigInteger getCount() {
		return count;
	}

	public void setCount(BigInteger count) {
		this.count = count;
	}

	public BigDecimal getBidValue() {
		return bidValue;
	}

	public void setBidValue(BigDecimal bidValue) {
		this.bidValue = bidValue;
	}

}