package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The ActualRevenuesDataDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class ActualRevenuesDataDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String actualRevenuesDataId;
	private String clientCountry;
	private String financialYear;
	private String month;
	private String quarter;
	private BigDecimal revenue;
	private Long revenueCustomerMapId;
	private String subSp;

	private SubSpMappingDTO subSpMappingT;

	public ActualRevenuesDataDTO() {
		super();
	}

	public String getActualRevenuesDataId() {
		return actualRevenuesDataId;
	}

	public void setActualRevenuesDataId(String actualRevenuesDataId) {
		this.actualRevenuesDataId = actualRevenuesDataId;
	}

	public String getClientCountry() {
		return clientCountry;
	}

	public void setClientCountry(String clientCountry) {
		this.clientCountry = clientCountry;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getQuarter() {
		return quarter;
	}

	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}

	public BigDecimal getRevenue() {
		return revenue;
	}

	public void setRevenue(BigDecimal revenue) {
		this.revenue = revenue;
	}

	public Long getRevenueCustomerMapId() {
		return revenueCustomerMapId;
	}

	public void setRevenueCustomerMapId(Long revenueCustomerMapId) {
		this.revenueCustomerMapId = revenueCustomerMapId;
	}

	public String getSubSp() {
		return subSp;
	}

	public void setSubSp(String subSp) {
		this.subSp = subSp;
	}

	public SubSpMappingDTO getSubSpMappingT() {
		return subSpMappingT;
	}

	public void setSubSpMappingT(SubSpMappingDTO subSpMappingT) {
		this.subSpMappingT = subSpMappingT;
	}
	
}
