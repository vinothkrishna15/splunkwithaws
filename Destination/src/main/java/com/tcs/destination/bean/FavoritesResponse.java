package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class FavoritesResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<UserFavoritesT> userFavoritesTs;
	
	private long totalCount;

	public List<UserFavoritesT> getUserFavoritesTs() {
		return userFavoritesTs;
	}

	public void setUserFavoritesTs(List<UserFavoritesT> userFavoritesTs) {
		this.userFavoritesTs = userFavoritesTs;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
}