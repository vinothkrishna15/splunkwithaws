package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class PaginatedResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<OpportunityT> opportunityTs;

	private List<PartnerMasterT> partnerMasterTs;

	private List<CustomerMasterT> customerMasterTs;

	private List<UserFavoritesT> userFavoritesTs;

	private List<ConnectT> connectTs;
	
	private List<MyWorklistDTO> myWorklists;

	private long totalCount;

	public List<OpportunityT> getOpportunityTs() {
		return opportunityTs;
	}

	public void setOpportunityTs(List<OpportunityT> opportunityTs) {
		this.opportunityTs = opportunityTs;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public List<UserFavoritesT> getUserFavoritesTs() {
		return userFavoritesTs;
	}

	public void setUserFavoritesTs(List<UserFavoritesT> userFavoritesTs) {
		this.userFavoritesTs = userFavoritesTs;
	}

	public List<PartnerMasterT> getPartnerMasterTs() {
		return partnerMasterTs;
	}

	public void setPartnerMasterTs(List<PartnerMasterT> partnerMasterTs) {
		this.partnerMasterTs = partnerMasterTs;
	}

	public List<CustomerMasterT> getCustomerMasterTs() {
		return customerMasterTs;
	}

	public void setCustomerMasterTs(List<CustomerMasterT> customerMasterTs) {
		this.customerMasterTs = customerMasterTs;
	}

	public List<ConnectT> getConnectTs() {
		return connectTs;
	}

	public void setConnectTs(List<ConnectT> connectTs) {
		this.connectTs = connectTs;
	}

	public List<MyWorklistDTO> getMyWorklists() {
		return myWorklists;
	}

	public void setMyWorklists(List<MyWorklistDTO> myWorklists) {
		this.myWorklists = myWorklists;
	}
	
}