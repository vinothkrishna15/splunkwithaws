package com.tcs.destination.bean.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class HealthCardMetrics {
	
	private BigDecimal winsRatio;
	private BigDecimal utilization;
	private BigDecimal unallocation;
	private BigDecimal bilability;
	private BigDecimal attrition;
	private BigDecimal skillCategory;
	private BigDecimal seniorRatio;
	private BigDecimal traineePercentage;

	public BigDecimal getWinsRatio() {
		return winsRatio;
	}

	public void setWinsRatio(BigDecimal winsRatio) {
		this.winsRatio = winsRatio;
	}

	public BigDecimal getUtilization() {
		return utilization;
	}

	public void setUtilization(BigDecimal utilization) {
		this.utilization = utilization;
	}

	public BigDecimal getUnallocation() {
		return unallocation;
	}

	public void setUnallocation(BigDecimal unallocation) {
		this.unallocation = unallocation;
	}

	public BigDecimal getBilability() {
		return bilability;
	}

	public void setBilability(BigDecimal bilability) {
		this.bilability = bilability;
	}

	public BigDecimal getAttrition() {
		return attrition;
	}

	public void setAttrition(BigDecimal attrition) {
		this.attrition = attrition;
	}

	public BigDecimal getSkillCategory() {
		return skillCategory;
	}

	public void setSkillCategory(BigDecimal skillCategory) {
		this.skillCategory = skillCategory;
	}

	public BigDecimal getSeniorRatio() {
		return seniorRatio;
	}

	public void setSeniorRatio(BigDecimal seniorRatio) {
		this.seniorRatio = seniorRatio;
	}

	public BigDecimal getTraineePercentage() {
		return traineePercentage;
	}

	public void setTraineePercentage(BigDecimal traineePercentage) {
		this.traineePercentage = traineePercentage;
	}
}
