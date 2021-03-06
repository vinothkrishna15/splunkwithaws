package com.tcs.destination.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class DashBoardConnectsResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int weekCount;

	private int monthCount;
	
	private PaginatedResponse paginatedConnectResponse;
	
	
	
	public PaginatedResponse getPaginatedConnectResponse() {
		return paginatedConnectResponse;
	}

	public void setPaginatedConnectResponse(
			PaginatedResponse paginatedConnectResponse) {
		this.paginatedConnectResponse = paginatedConnectResponse;
	}

	public int getWeekCount() {
		return weekCount;
	}

	public void setWeekCount(int weekCount) {
		this.weekCount = weekCount;
	}

	public int getMonthCount() {
		return monthCount;
	}

	public void setMonthCount(int monthCount) {
		this.monthCount = monthCount;
	}
	
	
}
