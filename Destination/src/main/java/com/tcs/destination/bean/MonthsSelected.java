package com.tcs.destination.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

import java.util.Date;

/**
 * The persistent class for the deal_closure_reporting_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
public class MonthsSelected implements Serializable {
	private static final long serialVersionUID = 1L;

	private Date endDate;

	private Date startDate;

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

}