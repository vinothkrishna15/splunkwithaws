package com.tcs.destination.bean;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class OpportunityNameKeywordSearch {

	String result;

	OpportunityT opportunityT;

	String isName;

	public OpportunityT getOpportunityT() {
		return opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getIsName() {
		return isName;
	}
	
	public void setIsName(String isName) {
		this.isName = isName;
	}
}
