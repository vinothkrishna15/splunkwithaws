package com.tcs.destination.bean;

import java.io.Serializable;

public class RevenuesResponse implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private byte[] logo;
	
	private String customerName;
	
	private String groupCustomerName;

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getGroupCustomerName() {
		return groupCustomerName;
	}

	public void setGroupCustomerName(String groupCustomerName) {
		this.groupCustomerName = groupCustomerName;
	}
	
	
}
