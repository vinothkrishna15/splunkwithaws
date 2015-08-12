package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * This DTO holds the output data required for Leadership Connects in Dashboard
 * 
 *
 */
@JsonFilter(Constants.FILTER)
public class LeadershipConnectsDTO implements Serializable {
    
private static final long serialVersionUID = 1L;
    
    private List<ConnectT> pastConnects;
    
    private int sizeOfPastConnects;
    
    private List<ConnectT> upcomingConnects;
    
    private int sizeOfUpcomingConnects;

    public int getSizeOfPastConnects() {
        return sizeOfPastConnects;
    }

    public void setSizeOfPastConnects(int sizeOfPastConnects) {
        this.sizeOfPastConnects = sizeOfPastConnects;
    }

    public int getSizeOfUpcomingConnects() {
        return sizeOfUpcomingConnects;
    }

    public void setSizeOfUpcomingConnects(int sizeOfUpcomingConnects) {
        this.sizeOfUpcomingConnects = sizeOfUpcomingConnects;
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
