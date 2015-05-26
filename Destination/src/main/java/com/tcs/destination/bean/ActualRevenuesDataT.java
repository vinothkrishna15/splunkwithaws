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
 * The persistent class for the actual_revenues_data_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="actualRevenuesDataId")
@Entity
@Table(name = "actual_revenues_data_t")
@NamedQuery(name = "ActualRevenuesDataT.findAll", query = "SELECT a FROM ActualRevenuesDataT a")
public class ActualRevenuesDataT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "actual_revenues_data_id")
	private String actualRevenuesDataId;

	@Column(name = "client_country")
	private String clientCountry;

	@Column(name = "finance_geography")
	private String financeGeography;

	@Column(name = "finance_iou")
	private String financeIou;

	@Column(name = "financial_year")
	private String financialYear;

	private String month;

	private String quarter;

	private BigDecimal revenue;

	//bi-directional many-to-one association to IouCustomerMappingT
	@ManyToOne
	@JoinColumn(name="finance_iou", insertable = false, updatable = false)
	private IouCustomerMappingT iouCustomerMappingT;

	@Column(name = "sub_sp")
	private String subSp;

	// bi-directional many-to-one association to RevenueCustomerMappingT
	@ManyToOne
	@JoinColumns(value = {
			@JoinColumn(name = "finance_customer_name", referencedColumnName = "finance_customer_name", insertable = false, updatable = false),
			@JoinColumn(name = "finance_geography", referencedColumnName = "customer_geography", insertable = false, updatable = false) })
	private RevenueCustomerMappingT revenueCustomerMappingT;

	//bi-directional many-to-one association to SubSpMappingT
	@ManyToOne
	@JoinColumn(name="sub_sp", insertable = false, updatable = false)
	private SubSpMappingT subSpMappingT;

	public ActualRevenuesDataT() {
	}

	public String getActualRevenuesDataId() {
		return this.actualRevenuesDataId;
	}

	public void setActualRevenuesDataId(String actualRevenuesDataId) {
		this.actualRevenuesDataId = actualRevenuesDataId;
	}

	public String getClientCountry() {
		return this.clientCountry;
	}

	public void setClientCountry(String clientCountry) {
		this.clientCountry = clientCountry;
	}

	public String getFinanceGeography() {
		return this.financeGeography;
	}

	public void setFinanceGeography(String financeGeography) {
		this.financeGeography = financeGeography;
	}

	public String getFinanceIou() {
		return this.financeIou;
	}

	public void setFinanceIou(String financeIou) {
		this.financeIou = financeIou;
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

	public IouCustomerMappingT getIouCustomerMappingT() {
		return this.iouCustomerMappingT;
	}

	public void setIouCustomerMappingT(IouCustomerMappingT iouCustomerMappingT) {
		this.iouCustomerMappingT = iouCustomerMappingT;
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