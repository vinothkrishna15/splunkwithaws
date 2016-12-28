package com.tcs.destination.bean.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The persistent class for the customer_associate_t database table.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class CustomerAssociateDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String groupCustomerName;
	private long customerAssociateCount;

	public CustomerAssociateDTO(String groupCustomerName,
			long customerAssociateCount) {
		super();
		this.groupCustomerName = groupCustomerName;
		this.customerAssociateCount = customerAssociateCount;
	}

	/**
	 * @return the groupCustomerName
	 */
	public String getGroupCustomerName() {
		return groupCustomerName;
	}

	/**
	 * @return the customerAssociateCount
	 */
	public long getCustomerAssociateCount() {
		return customerAssociateCount;
	}

	/**
	 * @param groupCustomerName
	 *            the groupCustomerName to set
	 */
	public void setGroupCustomerName(String groupCustomerName) {
		this.groupCustomerName = groupCustomerName;
	}

	/**
	 * @param customerAssociateCount
	 *            the customerAssociateCount to set
	 */
	public void setCustomerAssociateCount(long customerAssociateCount) {
		this.customerAssociateCount = customerAssociateCount;
	}

}