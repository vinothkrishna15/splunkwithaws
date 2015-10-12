package com.tcs.destination.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class GeoIouDashboardDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String timeLine;
	
	private BigDecimal winsAchieved;
	
	private int dealsAboveTwentyMillionAchieved;
	
	private int dealsAboveTenMillionAchieved;
	
	private int digitalReimaginationDealsAchieved;

	private double overAllWinRatioAchieved;
	
	private double accountsWithSpPenetrationAboveThreeAchieved;

	public String getTimeLine() {
		return timeLine;
	}

	public void setTimeLine(String timeLine) {
		this.timeLine = timeLine;
	}

	public BigDecimal getWinsAchieved() {
		winsAchieved = winsAchieved.setScale(2, RoundingMode.CEILING);
		return winsAchieved;
	}

	public void setWinsAchieved(BigDecimal winsAchieved) {
		this.winsAchieved = winsAchieved;
	}

	public int getDealsAboveTwentyMillionAchieved() {
		return dealsAboveTwentyMillionAchieved;
	}

	public void setDealsAboveTwentyMillionAchieved(
			int dealsAboveTwentyMillionAchieved) {
		this.dealsAboveTwentyMillionAchieved = dealsAboveTwentyMillionAchieved;
	}

	public int getDealsAboveTenMillionAchieved() {
		return dealsAboveTenMillionAchieved;
	}

	public void setDealsAboveTenMillionAchieved(int dealsAboveTenMillionAchieved) {
		this.dealsAboveTenMillionAchieved = dealsAboveTenMillionAchieved;
	}

	public int getDigitalReimaginationDealsAchieved() {
		return digitalReimaginationDealsAchieved;
	}

	public void setDigitalReimaginationDealsAchieved(
			int digitalReimaginationDealsAchieved) {
		this.digitalReimaginationDealsAchieved = digitalReimaginationDealsAchieved;
	}

	public double getOverAllWinRatioAchieved() {
		 overAllWinRatioAchieved = (double) Math.round(overAllWinRatioAchieved * 100) / 100;
		return overAllWinRatioAchieved;
	}

	public void setOverAllWinRatioAchieved(double overAllWinRatioAchieved) {
		this.overAllWinRatioAchieved = overAllWinRatioAchieved;
	}

	public double getAccountsWithSpPenetrationAboveThreeAchieved() {
		accountsWithSpPenetrationAboveThreeAchieved = (double) Math.round(accountsWithSpPenetrationAboveThreeAchieved * 100) / 100;
		return accountsWithSpPenetrationAboveThreeAchieved;
	}

	public void setAccountsWithSpPenetrationAboveThreeAchieved(
			double accountsWithSpPenetrationAboveThreeAchieved) {
		this.accountsWithSpPenetrationAboveThreeAchieved = accountsWithSpPenetrationAboveThreeAchieved;
	}

}
