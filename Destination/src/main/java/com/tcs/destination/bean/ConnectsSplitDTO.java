package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class ConnectsSplitDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	List<ConnectT> pastConnects;
	
	List<ConnectT> upcomingConnects;
	
	int pastConnectsCount;
	
	int upcomingConnectsCount;

	public int getPastConnectsCount() {
		return this.pastConnects!=null ? this.pastConnects.size() : 0;
	}

	public void setPastConnectsCount(int pastConnectsCount) {
		this.pastConnectsCount = pastConnectsCount;
	}

	public int getUpcomingConnectsCount() {
		return this.upcomingConnects!=null ? this.upcomingConnects.size() : 0;
	}

	public void setUpcomingConnectsCount(int upcomingConnectsCount) {
		this.upcomingConnectsCount = upcomingConnectsCount;
	}

	public List<ConnectT> getPastConnects() {
		return pastConnects;
	}

	public void setPastConnects(List<ConnectT> pastConnects) {
		this.pastConnects = pastConnects;
	}

	public List<ConnectT> getUpcomingConnects() {
		return upcomingConnects;
	}

	public void setUpcomingConnects(List<ConnectT> upcomingConnects) {
		this.upcomingConnects = upcomingConnects;
	}
		
}
