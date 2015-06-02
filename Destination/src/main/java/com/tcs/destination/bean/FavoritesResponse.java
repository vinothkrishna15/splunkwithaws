package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

public class FavoritesResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<UserFavoritesT> userFavorites;
	
	private long totalCount;

	public List<UserFavoritesT> getUserFavorites() {
		return userFavorites;
	}

	public void setUserFavorites(List<UserFavoritesT> userFavorites) {
		this.userFavorites = userFavorites;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
}