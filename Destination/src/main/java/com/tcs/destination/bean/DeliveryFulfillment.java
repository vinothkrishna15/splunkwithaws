package com.tcs.destination.bean;

public class DeliveryFulfillment {

	private Integer weekNumber;
	
	private Integer fulfilledCount;
	
	private Integer openCount;
	
	private boolean currentWeek;

	public Integer getWeekNumber() {
		return weekNumber;
	}

	public void setWeekNumber(Integer weekNumber) {
		this.weekNumber = weekNumber;
	}

	public Integer getFulfilledCount() {
		return fulfilledCount;
	}

	public void setFulfilledCount(Integer fulfilledCount) {
		this.fulfilledCount = fulfilledCount;
	}

	public Integer getOpenCount() {
		return openCount;
	}

	public void setOpenCount(Integer openCount) {
		this.openCount = openCount;
	}

	public boolean isCurrentWeek() {
		return currentWeek;
	}

	public void setCurrentWeek(boolean currentWeek) {
		this.currentWeek = currentWeek;
	}
	
	
}
