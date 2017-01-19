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
public class CustomerConnectDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<String> connectedCustomerList;
	private int numberOfCustomersConnected;
	private int cxoCount;
	private int othersCount;
	private List<Periodicaldata> periodicaldata;
	
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
	
	/**
	 * @return the periodicaldata
	 */
	public List<Periodicaldata> getPeriodicaldata() {
		return periodicaldata;
	}
	/**
	 * @param periodicaldata the periodicaldata to set
	 */
	public void setPeriodicaldata(List<Periodicaldata> periodicaldata) {
		this.periodicaldata = periodicaldata;
	}
	/**
	 * @return the connectedCustomerList
	 */
	public List<String> getConnectedCustomerList() {
		return connectedCustomerList;
	}
	/**
	 * @param connectedCustomerList the connectedCustomerList to set
	 */
	public void setConnectedCustomerList(List<String> connectedCustomerList) {
		this.connectedCustomerList = connectedCustomerList;
	}
}
