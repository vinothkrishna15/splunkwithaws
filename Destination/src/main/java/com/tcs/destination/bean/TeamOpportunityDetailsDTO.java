package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;




import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * This DTO holds a list of Opportunity Details and 
 * count of the opportunity details 
 * 
 * @author bnpp
 *
 */
@JsonFilter(Constants.FILTER)
public class TeamOpportunityDetailsDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int sizeOfOpportunityDetails;
	
	private List<OpportunityDetailsDTO> opportunityDetails;

	public int getSizeOfOpportunityDetails() {
		return sizeOfOpportunityDetails;
	}

	public void setSizeOfOpportunityDetails(int sizeOfOpportunityDetails) {
		this.sizeOfOpportunityDetails = sizeOfOpportunityDetails;
	}

	public List<OpportunityDetailsDTO> getOpportunityDetails() {
		return opportunityDetails;
	}

	public void setOpportunityDetails(List<OpportunityDetailsDTO> opportunityDetails) {
		this.opportunityDetails = opportunityDetails;
	}
	

}
