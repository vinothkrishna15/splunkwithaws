/**
 * 
 */
package com.tcs.destination.bean.dto;

import java.io.Serializable;

/**
 * @author tcs2
 *
 */
public class CustomerConnectDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int numberOfCustomersConnected;
	private int cxoCount;
	private int othersCount;
	/**
	 * @return the numberOfCustomersConnected
	 */
	public int getNumberOfCustomersConnected() {
		return numberOfCustomersConnected;
	}
	/**
	 * @param numberOfCustomersConnected the numberOfCustomersConnected to set
	 */
	public void setNumberOfCustomersConnected(int numberOfCustomersConnected) {
		this.numberOfCustomersConnected = numberOfCustomersConnected;
	}
	/**
	 * @return the cxoCount
	 */
	public int getCxoCount() {
		return cxoCount;
	}
	/**
	 * @param cxoCount the cxoCount to set
	 */
	public void setCxoCount(int cxoCount) {
		this.cxoCount = cxoCount;
	}
	/**
	 * @return the othersCount
	 */
	public int getOthersCount() {
		return othersCount;
	}
	/**
	 * @param othersCount the othersCount to set
	 */
	public void setOthersCount(int othersCount) {
		this.othersCount = othersCount;
	}
}
