/**
 * 
 */
package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.util.Set;

/**
 * @author tcs2
 *
 */
public class PeriodicalData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int customerCount;
	private String range;
	private Set<String> customerList;
	/**
	 * @return the customerCount
	 */
	public int getCustomerCount() {
		return customerCount;
	}
	/**
	 * @param customerCount the customerCount to set
	 */
	public void setCustomerCount(int customerCount) {
		this.customerCount = customerCount;
	}
	/**
	 * @return the week
	 */
	public String getRange() {
		return range;
	}
	/**
	 * @param week the week to set
	 */
	public void setRange(String range) {
		this.range = range;
	}
	/**
	 * @return the customerList
	 */
	public Set<String> getCustomerList() {
		return customerList;
	}
	/**
	 * @param customerList the customerList to set
	 */
	public void setCustomerList(Set<String> customerList) {
		this.customerList = customerList;
	}


}
