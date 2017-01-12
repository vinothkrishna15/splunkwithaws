/**
 * 
 */
package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author tcs2
 *
 */
public class UserFavouritesDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<String> favouriteList;

	/**
	 * @return the favouriteList
	 */
	public List<String> getFavouriteList() {
		return favouriteList;
	}

	/**
	 * @param favouriteList
	 *            the favouriteList to set
	 */
	public void setFavouriteList(List<String> favouriteList) {
		this.favouriteList = favouriteList;
	}

}
