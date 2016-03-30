package com.tcs.destination.bean;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class BDMDealValueDTO {

	private String userName;
	
	private String userGroup;
	
	private int winsOpportunityCount;
	
	private BigDecimal winsDigitalDealValue;
	
	private int lossOpportunityCount;
	
	private BigDecimal lossDigitalDealValue;
	
	private int otherLossOpportunityCount;
	
	private BigDecimal otherLossDigitalDealValue;
	
	private int pipelineOpportunityCount;
	
	private BigDecimal pipelineDigitalDealValue;
	
	private int prospectsOpportunityCount;
	
	private BigDecimal prospectsDigitalDealValue;
	

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}
	
	public int getWinsOpportunityCount() {
		return winsOpportunityCount;
	}

	public void setWinsOpportunityCount(int winsOpportunityCount) {
		this.winsOpportunityCount = winsOpportunityCount;
	}

	public BigDecimal getWinsDigitalDealValue() {
		return winsDigitalDealValue;
	}

	public void setWinsDigitalDealValue(BigDecimal winsDigitalDealValue) {
		this.winsDigitalDealValue = winsDigitalDealValue;
	}

	public int getLossOpportunityCount() {
		return lossOpportunityCount;
	}

	public void setLossOpportunityCount(int lossOpportunityCount) {
		this.lossOpportunityCount = lossOpportunityCount;
	}

	public BigDecimal getLossDigitalDealValue() {
		return lossDigitalDealValue;
	}

	public void setLossDigitalDealValue(BigDecimal lossDigitalDealValue) {
		this.lossDigitalDealValue = lossDigitalDealValue;
	}

	public int getOtherLossOpportunityCount() {
		return otherLossOpportunityCount;
	}

	public void setOtherLossOpportunityCount(int otherLossOpportunityCount) {
		this.otherLossOpportunityCount = otherLossOpportunityCount;
	}

	public BigDecimal getOtherLossDigitalDealValue() {
		return otherLossDigitalDealValue;
	}

	public void setOtherLossDigitalDealValue(BigDecimal otherLossDigitalDealValue) {
		this.otherLossDigitalDealValue = otherLossDigitalDealValue;
	}

	public int getPipelineOpportunityCount() {
		return pipelineOpportunityCount;
	}

	public void setPipelineOpportunityCount(int pipelineOpportunityCount) {
		this.pipelineOpportunityCount = pipelineOpportunityCount;
	}

	public BigDecimal getPipelineDigitalDealValue() {
		return pipelineDigitalDealValue;
	}

	public void setPipelineDigitalDealValue(BigDecimal pipelineDigitalDealValue) {
		this.pipelineDigitalDealValue = pipelineDigitalDealValue;
	}

	public int getProspectsOpportunityCount() {
		return prospectsOpportunityCount;
	}

	public void setProspectsOpportunityCount(int prospectsOpportunityCount) {
		this.prospectsOpportunityCount = prospectsOpportunityCount;
	}

	public BigDecimal getProspectsDigitalDealValue() {
		return prospectsDigitalDealValue;
	}

	public void setProspectsDigitalDealValue(BigDecimal prospectsDigitalDealValue) {
		this.prospectsDigitalDealValue = prospectsDigitalDealValue;
	}

}
