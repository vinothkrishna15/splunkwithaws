package com.tcs.destination.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class TargetVsActualDetailed {

	private CustomerMasterT customerMasterT;

	private List<TargetVsActualYearToDate> yearToDate;

	public CustomerMasterT getCustomerMasterT() {
		return customerMasterT;
	}

	public void setCustomerMasterT(CustomerMasterT customerMasterT) {
		this.customerMasterT = customerMasterT;
	}

	public List<TargetVsActualYearToDate> getYearToDate() {
		return yearToDate;
	}

	public void setYearToDate(List<TargetVsActualYearToDate> yearToDate) {
		this.yearToDate = yearToDate;
	}

}
