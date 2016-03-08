package com.tcs.destination.bean;

import java.io.Serializable;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class ConnectSummaryResponse implements Serializable{

	private static final long serialVersionUID = 1L;

	private String rowLabel;
	
	private BigInteger customerConnectCount;

	private BigInteger partnerConnectCount;

	public BigInteger getCustomerConnectCount() {
		return customerConnectCount;
	}

	public void setCustomerConnectCount(BigInteger customerConnectCount) {
		this.customerConnectCount = customerConnectCount;
	}

	public BigInteger getPartnerConnectCount() {
		return partnerConnectCount;
	}

	public void setPartnerConnectCount(BigInteger partnerConnectCount) {
		this.partnerConnectCount = partnerConnectCount;
	}

	public String getRowLabel() {
		return rowLabel;
	}

	public void setRowLabel(String rowLabel) {
		this.rowLabel = rowLabel;
	}

}
