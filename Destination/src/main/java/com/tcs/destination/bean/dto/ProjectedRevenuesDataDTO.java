package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The ProjectedRevenuesDataDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class ProjectedRevenuesDataDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String projectedRevenuesDataId;
	private String clientCountry;
	private String financialYear;
	private String month;
	private String quarter;
	private BigDecimal revenue;
	private String subSp;

	private SubSpMappingDTO subSpMappingT;

	public ProjectedRevenuesDataDTO() {
		super();
	}

	public String getProjectedRevenuesDataId() {
		return projectedRevenuesDataId;
	}

	public void setProjectedRevenuesDataId(String projectedRevenuesDataId) {
		this.projectedRevenuesDataId = projectedRevenuesDataId;
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
