package com.tcs.destination.bean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;
@JsonFilter(Constants.FILTER)
public class ReportSummaryOpportunity {

	private String subSp;

	private String geography;

	private String iou;

	private String salesStageDescription;

	private int salesStageCode;

	private List<OpportunitySummaryValue> opportunitySummaryValueList;
	
	
	@Transient 
	private BigInteger totalCount;
	
	@Transient
	private BigDecimal totalBidValueFirstCurrency;
	
	@Transient
	private BigDecimal totalBidValueSecondCurrency;

	public String getSubSp() {
		return subSp;
	}

	public void setSubSp(String subSp) {
		this.subSp = subSp;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getIou() {
		return iou;
	}

	public void setIou(String iou) {
		this.iou = iou;
	}

	public String getSalesStageDescription() {
		return salesStageDescription;
	}

	public void setSalesStageDescription(String salesStageDescription) {
		this.salesStageDescription = salesStageDescription;
	}

	public int getSalesStageCode() {
		return salesStageCode;
	}

	public void setSalesStageCode(int salesStageCode) {
		this.salesStageCode = salesStageCode;
	}

	public List<OpportunitySummaryValue> getOpportunitySummaryValueList() {
		return opportunitySummaryValueList;
	}

	public void setOpportunitySummaryValueList(
			List<OpportunitySummaryValue> opportunitySummaryValueList) {
		this.opportunitySummaryValueList = opportunitySummaryValueList;
	}

	public BigInteger getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(BigInteger totalCount) {
		this.totalCount = totalCount;
	}

	public BigDecimal getTotalBidValueFirstCurrency() {
		return totalBidValueFirstCurrency;
	}

	public void setTotalBidValueFirstCurrency(BigDecimal totalBidValueFirstCurrency) {
		this.totalBidValueFirstCurrency = totalBidValueFirstCurrency;
	}

	public BigDecimal getTotalBidValueSecondCurrency() {
		return totalBidValueSecondCurrency;
	}

	public void setTotalBidValueSecondCurrency(BigDecimal totalBidValueSecondCurrency) {
		this.totalBidValueSecondCurrency = totalBidValueSecondCurrency;
	}
	
	

}
