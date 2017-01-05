package com.tcs.destination.bean.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CarouselMetricsDTO {
	//Health Card Metrics
	private HealthCardMetrics healthCardMetrics;
	
	//Win Ratio Metrics
	private BigDecimal winsRatio;
	private Integer noOfWins;
	private Integer noOfLoss;
	private BigDecimal winValue;
	private BigDecimal lossValue;
	
	//Customer Metrics
	private Integer customersConnected;
	private Integer customersWithHundredPlusAssociates;
	private Integer customersConsulting;
	
	//Opportunity Metrics
	private OpportunityMetrics opportunityMetrics;

	public HealthCardMetrics getHealthCardMetrics() {
		return healthCardMetrics;
	}

	public void setHealthCardMetrics(HealthCardMetrics healthCardMetrics) {
		this.healthCardMetrics = healthCardMetrics;
	}

	public BigDecimal getWinsRatio() {
		return winsRatio;
	}

	public void setWinsRatio(BigDecimal winsRatio) {
		this.winsRatio = winsRatio;
	}

	public Integer getNoOfWins() {
		return noOfWins;
	}

	public void setNoOfWins(Integer noOfWins) {
		this.noOfWins = noOfWins;
	}

	public Integer getNoOfLoss() {
		return noOfLoss;
	}

	public void setNoOfLoss(Integer noOfLoss) {
		this.noOfLoss = noOfLoss;
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

	public Integer getCustomersConnected() {
		return customersConnected;
	}

	public void setCustomersConnected(Integer customersConnected) {
		this.customersConnected = customersConnected;
	}

	public Integer getCustomersWithHundredPlusAssociates() {
		return customersWithHundredPlusAssociates;
	}

	public void setCustomersWithHundredPlusAssociates(
			Integer customersWithHundredPlusAssociates) {
		this.customersWithHundredPlusAssociates = customersWithHundredPlusAssociates;
	}

	public Integer getCustomersConsulting() {
		return customersConsulting;
	}

	public void setCustomersConsulting(Integer customersConsulting) {
		this.customersConsulting = customersConsulting;
	}

	public OpportunityMetrics getOpportunityMetrics() {
		return opportunityMetrics;
	}

	public void setOpportunityMetrics(OpportunityMetrics opportunityMetrics) {
		this.opportunityMetrics = opportunityMetrics;
	}
	
}
