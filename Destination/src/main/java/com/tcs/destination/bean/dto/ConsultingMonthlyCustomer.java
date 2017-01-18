/**
 * 
 */
package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author tcs2
 *
 */
@JsonInclude(Include.NON_NULL)
public class ConsultingMonthlyCustomer implements Serializable {

	private static final long serialVersionUID = 1L;

	private String month;
	private Integer customerCount;



	/**
	 * @return the month
	 */
	public String getMonth() {
		return month;
	}

	/**
	 * @param month
	 *            the month to set
	 */
	public void setMonth(String month) {
		this.month = month;
	}

	/**
	 * @return the customerCount
	 */
	public Integer getCustomerCount() {
		return customerCount;
	}

	/**
	 * @param customerCount the customerCount to set
	 */
	public void setCustomerCount(Integer customerCount) {
		this.customerCount = customerCount;
	}

}
