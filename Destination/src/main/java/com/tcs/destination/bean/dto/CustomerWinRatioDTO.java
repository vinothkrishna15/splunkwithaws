package com.tcs.destination.bean.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The WinRatioDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class CustomerWinRatioDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String customerName;
	private MoneyBucketDTO bucket;

	public CustomerWinRatioDTO() {
		super();
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public MoneyBucketDTO getBucket() {
		return bucket;
	}

	public void setBucket(MoneyBucketDTO bucket) {
		this.bucket = bucket;
	}

}
