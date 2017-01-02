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
public class ConsultingMonthlyRevenue implements Serializable {

	private static final long serialVersionUID = 1L;

	private String month;
	private BigDecimal revenue;



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
	 * @return the revenue
	 */
	public BigDecimal getRevenue() {
		return revenue;
	}

	/**
	 * @param revenue
	 *            the revenue to set
	 */
	public void setRevenue(BigDecimal revenue) {
		this.revenue = revenue;
	}

}
