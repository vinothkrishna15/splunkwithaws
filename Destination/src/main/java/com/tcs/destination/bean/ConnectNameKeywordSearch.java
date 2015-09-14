package com.tcs.destination.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * This DTO holds the search results for findConnectNameOrKeyword() service in Connect Controller
 * 
 * @author bnpp
 *
 */
@JsonFilter(Constants.FILTER)
public class ConnectNameKeywordSearch implements Serializable{

	private static final long serialVersionUID = -6777627192645202282L;

	String result;

	ConnectT connectT;

	String isName;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public ConnectT getConnectT() {
		return connectT;
	}

	public void setConnectT(ConnectT connectT) {
		this.connectT = connectT;
	}

	public String getIsName() {
		return isName;
	}

	public void setIsName(String isName) {
		this.isName = isName;
	}

	
}
