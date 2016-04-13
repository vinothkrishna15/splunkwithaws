package com.tcs.destination.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the projected_revenues_data_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "projectedRevenuesDataId")
@Entity
@Table(name = "projected_revenues_data_t")
@NamedQuery(name = "ProjectedRevenuesDataT.findAll", query = "SELECT p FROM ProjectedRevenuesDataT p")
public class ProjectedRevenuesDataT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "projected_revenues_data_id")
	private String projectedRevenuesDataId;

	@Column(name = "client_country")
	private String clientCountry;

	@Column(name = "financial_year")
	private String financialYear;

	private String month;

	private String quarter;

	private BigDecimal revenue;

	@Column(name = "sub_sp")
	private String subSp;

	// bi-directional many-to-one association to RevenueCustomerMappingT
	@ManyToOne
	@JoinColumn(name="revenue_customer_map_id")
	private RevenueCustomerMappingT revenueCustomerMappingT;

	// bi-directional many-to-one association to SubSpMappingT
	@ManyToOne
	@JoinColumn(name = "sub_sp", referencedColumnName = "actual_sub_sp", insertable = false, updatable = false)
	private SubSpMappingT subSpMappingT;

	public ProjectedRevenuesDataT() {
	}

	public String getProjectedRevenuesDataId() {
		return this.projectedRevenuesDataId;
	}

	public void setProjectedRevenuesDataId(String projectedRevenuesDataId) {
		this.projectedRevenuesDataId = projectedRevenuesDataId;
	}

	public String getClientCountry() {
		return this.clientCountry;
	}

	public void setClientCountry(String clientCountry) {
		this.clientCountry = clientCountry;
	}

	public String getFinancialYear() {
		return this.financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getMonth() {
		return this.month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getQuarter() {
		return this.quarter;
	}

	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}

	public BigDecimal getRevenue() {
		return this.revenue;
	}

	public void setRevenue(BigDecimal revenue) {
		this.revenue = revenue;
	}

	public String getSubSp() {
		return this.subSp;
	}

	public void setSubSp(String subSp) {
		this.subSp = subSp;
	}

	public RevenueCustomerMappingT getRevenueCustomerMappingT() {
		return this.revenueCustomerMappingT;
	}

	public void setRevenueCustomerMappingT(
			RevenueCustomerMappingT revenueCustomerMappingT) {
		this.revenueCustomerMappingT = revenueCustomerMappingT;
	}

	public SubSpMappingT getSubSpMappingT() {
		return this.subSpMappingT;
	}

	public void setSubSpMappingT(SubSpMappingT subSpMappingT) {
		this.subSpMappingT = subSpMappingT;
	}

}
