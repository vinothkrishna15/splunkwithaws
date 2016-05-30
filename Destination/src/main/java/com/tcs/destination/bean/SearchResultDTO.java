package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import com.tcs.destination.enums.SmartSearchType;

public class SearchResultDTO<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private SmartSearchType searchType;
	private List<T> values;
	
	public SmartSearchType getSearchType() {
		return searchType;
	}
	public void setSearchType(SmartSearchType type) {
		this.searchType = type;
	}
	public List<T> getValues() {
		return values;
	}
	public void setValues(List<T> values) {
		this.values = values;
	}
	
}
