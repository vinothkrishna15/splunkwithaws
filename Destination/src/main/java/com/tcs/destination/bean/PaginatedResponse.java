package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class PaginatedResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<OpportunityT> opportunityTs;

	private List<UserFavoritesT> userFavoritesTs;

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
}