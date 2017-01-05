package com.tcs.destination.bean.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class OpportunityMetrics {
	
	private Integer noOfQualified;
	private BigDecimal qualifiedValue;
	private Integer noOfBidsSubmitted;
	private BigDecimal bidsSubmittedValue;
	private Integer noOfRequestReceived;
	private BigDecimal requestReceivedValue;
	public Integer getNoOfQualified() {
		return noOfQualified;
	}
	public void setNoOfQualified(Integer noOfQualified) {
		this.noOfQualified = noOfQualified;
	}
	public BigDecimal getQualifiedValue() {
		return qualifiedValue;
	}
	public void setQualifiedValue(BigDecimal qualifiedValue) {
		this.qualifiedValue = qualifiedValue;
	}
	public Integer getNoOfBidsSubmitted() {
		return noOfBidsSubmitted;
	}
	public void setNoOfBidsSubmitted(Integer noOfBidsSubmitted) {
		this.noOfBidsSubmitted = noOfBidsSubmitted;
	}
	public BigDecimal getBidsSubmittedValue() {
		return bidsSubmittedValue;
	}
	public void setBidsSubmittedValue(BigDecimal bidsSubmittedValue) {
		this.bidsSubmittedValue = bidsSubmittedValue;
	}
	public Integer getNoOfRequestReceived() {
		return noOfRequestReceived;
	}
	public void setNoOfRequestReceived(Integer noOfRequestReceived) {
		this.noOfRequestReceived = noOfRequestReceived;
	}
	public BigDecimal getRequestReceivedValue() {
		return requestReceivedValue;
	}
	public void setRequestReceivedValue(BigDecimal requestReceivedValue) {
		this.requestReceivedValue = requestReceivedValue;
	}
}
