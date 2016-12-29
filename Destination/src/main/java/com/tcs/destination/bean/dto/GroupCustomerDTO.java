package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

import javax.persistence.Transient;


@JsonInclude(Include.NON_NULL)
public class GroupCustomerDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String groupCustomerName;

	private byte[] logo;

	private List<CustomerMasterDTO> customerMasterTs;
	
	private int totalConnects;
	
	private int cxoConnects;
	
	private int otherConnects;
	
	private int associates;
	
	private int associatesDE;
	
	private int associatesNonDE;
	
	private BigDecimal totalRevenue;
	
	private BigDecimal consultingRevenue;
	
	private BigDecimal grossMargin;
	
	private BigDecimal cost;
	
	private int opportunities;
	
	private int prospectingOpportunities;
	
	private int pipelineOpportunities;
	
	private BigDecimal winRatio;
	
	private int totalWins;
	
	private int totalLoss;
	
	private BigDecimal winValue;
	
	private BigDecimal lossValue;
	
	public GroupCustomerDTO() {
	}

	public String getGroupCustomerName() {
		return this.groupCustomerName;
	}

	public void setGroupCustomerName(String groupCustomerName) {
		this.groupCustomerName = groupCustomerName;
	}

	public byte[] getLogo() {
		return this.logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public int getTotalConnects() {
		return totalConnects;
	}

	public void setTotalConnects(int totalConnects) {
		this.totalConnects = totalConnects;
	}

	public int getCxoConnects() {
		return cxoConnects;
	}

	public void setCxoConnects(int cxoConnects) {
		this.cxoConnects = cxoConnects;
	}

	public int getOtherConnects() {
		return otherConnects;
	}

	public void setOtherConnects(int otherConnects) {
		this.otherConnects = otherConnects;
	}

	public int getAssociates() {
		return associates;
	}

	public void setAssociates(int associates) {
		this.associates = associates;
	}

	public int getAssociatesDE() {
		return associatesDE;
	}

	public void setAssociatesDE(int associatesDE) {
		this.associatesDE = associatesDE;
	}

	public int getAssociatesNonDE() {
		return associatesNonDE;
	}

	public void setAssociatesNonDE(int associatesNonDE) {
		this.associatesNonDE = associatesNonDE;
	}

	public BigDecimal getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(BigDecimal totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

	public BigDecimal getConsultingRevenue() {
		return consultingRevenue;
	}

	public void setConsultingRevenue(BigDecimal consultingRevenue) {
		this.consultingRevenue = consultingRevenue;
	}

	public BigDecimal getGrossMargin() {
		return grossMargin;
	}

	public void setGrossMargin(BigDecimal grossMargin) {
		this.grossMargin = grossMargin;
	}

	public List<CustomerMasterDTO> getCustomerMasterTs() {
		return customerMasterTs;
	}

	public void setCustomerMasterTs(List<CustomerMasterDTO> customerMasterTs) {
		this.customerMasterTs = customerMasterTs;
	}

	public int getOpportunities() {
		return opportunities;
	}

	public void setOpportunities(int opportunities) {
		this.opportunities = opportunities;
	}

	public int getProspectingOpportunities() {
		return prospectingOpportunities;
	}

	public void setProspectingOpportunities(int prospectingOpportunities) {
		this.prospectingOpportunities = prospectingOpportunities;
	}

	public int getPipelineOpportunities() {
		return pipelineOpportunities;
	}

	public void setPipelineOpportunities(int pipelineOpportunities) {
		this.pipelineOpportunities = pipelineOpportunities;
	}

	public BigDecimal getWinRatio() {
		return winRatio;
	}

	public void setWinRatio(BigDecimal winRatio) {
		this.winRatio = winRatio;
	}

	public int getTotalWins() {
		return totalWins;
	}

	public void setTotalWins(int totalWins) {
		this.totalWins = totalWins;
	}

	public int getTotalLoss() {
		return totalLoss;
	}

	public void setTotalLoss(int totalLoss) {
		this.totalLoss = totalLoss;
	}

	public BigDecimal getWinValue() {
		return winValue;
	}

	public void setWinValue(BigDecimal winValue) {
		this.winValue = winValue;
	}

	public BigDecimal getLossValue() {
		return lossValue;
	}

	public void setLossValue(BigDecimal lossValue) {
		this.lossValue = lossValue;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
	
}