package com.tcs.destination.bean;

import java.util.HashMap;
import java.lang.String;

public class QueryBufferDTO 
{ 
	String query;
	HashMap<Integer,String> parameterMap;

	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public HashMap<Integer, String> getParameterMap() {
		return parameterMap;
	}
	public void setParameterMap(HashMap<Integer, String> parameterMap) {
		this.parameterMap = parameterMap;
	}
	
}
