package com.tcs.destination.bean;

import java.math.BigDecimal;

public class UnallocationAssociate {
	private BigDecimal seniorPercentage;
	private BigDecimal juniorPercentage;
	private BigDecimal traineePercentage;
	public BigDecimal getSeniorPercentage() {
		return seniorPercentage;
	}
	public void setSeniorPercentage(BigDecimal seniorPercentage) {
		this.seniorPercentage = seniorPercentage;
	}
	public BigDecimal getJuniorPercentage() {
		return juniorPercentage;
	}
	public void setJuniorPercentage(BigDecimal juniorPercentage) {
		this.juniorPercentage = juniorPercentage;
	}
	public BigDecimal getTraineePercentage() {
		return traineePercentage;
	}
	public void setTraineePercentage(BigDecimal traineePercentage) {
		this.traineePercentage = traineePercentage;
	}
}
