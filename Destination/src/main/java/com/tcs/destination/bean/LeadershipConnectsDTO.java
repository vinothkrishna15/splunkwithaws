package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * This DTO holds the output data required for Leadership Connects in Dashboard
 * 
 * @author bnpp
 *
 */
@JsonFilter(Constants.FILTER)
public class LeadershipConnectsDTO implements Serializable {
    
private static final long serialVersionUID = 1L;
    
    private List<ConnectT> pastConnects;
    
    private List<ConnectT> upcomingConnects;

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
