package com.tcs.destination.bean;

import java.io.Serializable;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class ConnectSummaryResponse implements Serializable{

	private static final long serialVersionUID = 1L;

	
	private String RowLabel;

	private BigInteger connectCount;

	public String getRowLabel() {
		return RowLabel;
	}

	public void setRowLabel(String rowLabel) {
		RowLabel = rowLabel;
	}

	public BigInteger getConnectCount() {
		return connectCount;
	}

	public void setConnectCount(BigInteger connectCount) {
		this.connectCount = connectCount;
	}

}
