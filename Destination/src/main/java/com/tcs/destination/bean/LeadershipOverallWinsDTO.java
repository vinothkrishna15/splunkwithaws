package com.tcs.destination.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * This DTO holds wins, wins above 5M and wins above 1M 
 * for Leadership Dashboard 
 * 
 * @author bnpp
 *
 */
@JsonFilter(Constants.FILTER)
public class LeadershipOverallWinsDTO implements Serializable {

    private static final long serialVersionUID = -4240687062965580304L;
  
    private LeadershipWinsDTO leadershipWins;
    
    private LeadershipWinsDTO leadershipWinsAboveFiveMillions;
    
    private LeadershipWinsDTO leadershipWinsAboveOneMillion;

    public LeadershipWinsDTO getLeadershipWins() {
        return leadershipWins;
    }

    public void setLeadershipWins(LeadershipWinsDTO leadershipWins) {
        this.leadershipWins = leadershipWins;
    }

    public LeadershipWinsDTO getLeadershipWinsAboveFiveMillions() {
        return leadershipWinsAboveFiveMillions;
    }

    public void setLeadershipWinsAboveFiveMillions(
    	LeadershipWinsDTO leadershipWinsAboveFiveMillions) {
        this.leadershipWinsAboveFiveMillions = leadershipWinsAboveFiveMillions;
    }

    public LeadershipWinsDTO getLeadershipWinsAboveOneMillion() {
        return leadershipWinsAboveOneMillion;
    }

    public void setLeadershipWinsAboveOneMillion(
    	LeadershipWinsDTO leadershipWinsAboveOneMillion) {
        this.leadershipWinsAboveOneMillion = leadershipWinsAboveOneMillion;
    }
    

}
