package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * The primary key class for the revenue_customer_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Embeddable
public class RevenueCustomerMappingTPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="finance_customer_name", insertable = false, updatable = false)
	private String financeCustomerName;

	@Column(name="finance_iou", insertable=false, updatable=false)
	private String financeIou;

	@Column(name="customer_geography", insertable = false, updatable = false)
	private String customerGeography;

	public RevenueCustomerMappingTPK() {
	}
	public String getFinanceCustomerName() {
		return this.financeCustomerName;
	}
	public void setFinanceCustomerName(String financeCustomerName) {
		this.financeCustomerName = financeCustomerName;
	}
	public String getFinanceIou() {
		return this.financeIou;
	}
	public void setFinanceIou(String financeIou) {
		this.financeIou = financeIou;
	}
	public String getCustomerGeography() {
		return this.customerGeography;
	}
	public void setCustomerGeography(String customerGeography) {
		this.customerGeography = customerGeography;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RevenueCustomerMappingTPK)) {
			return false;
		}
		RevenueCustomerMappingTPK castOther = (RevenueCustomerMappingTPK)other;
		return 
			this.financeCustomerName.equals(castOther.financeCustomerName)
			&& this.financeIou.equals(castOther.financeIou)
			&& this.customerGeography.equals(castOther.customerGeography);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.financeCustomerName.hashCode();
		hash = hash * prime + this.financeIou.hashCode();
		hash = hash * prime + this.customerGeography.hashCode();
		
		return hash;
	}
}