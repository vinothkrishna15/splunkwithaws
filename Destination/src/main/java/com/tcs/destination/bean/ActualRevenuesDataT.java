package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the actual_revenues_data_t database table.
 * 
 */
@Entity
@Table(name="actual_revenues_data_t")
@NamedQuery(name="ActualRevenuesDataT.findAll", query="SELECT a FROM ActualRevenuesDataT a")
public class ActualRevenuesDataT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="actual_revenues_data_id")
	private String actualRevenuesDataId;

	@Column(name="client_country")
	private String clientCountry;

	@Column(name="financial_customer_name")
	private String financialCustomerName;

	@Column(name="financial_geography")
	private String financialGeography;

	@Column(name="financial_iou")
	private String financialIou;

	@Column(name="financial_year")
	private String financialYear;

	private String month;

	private String quarter;

	private BigDecimal revenue;

	@Column(name="sub_sp")
	private String subSp;

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

	public String getFinancialCustomerName() {
		return this.financialCustomerName;
	}

	public void setFinancialCustomerName(String financialCustomerName) {
		this.financialCustomerName = financialCustomerName;
	}

	public String getFinancialGeography() {
		return this.financialGeography;
	}

	public void setFinancialGeography(String financialGeography) {
		this.financialGeography = financialGeography;
	}

	public String getFinancialIou() {
		return this.financialIou;
	}

	public void setFinancialIou(String financialIou) {
		this.financialIou = financialIou;
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

}