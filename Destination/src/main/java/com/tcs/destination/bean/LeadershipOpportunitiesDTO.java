package com.tcs.destination.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * This DTO holds the Details of Prospects, Qualified Pipeline, 
 * Won, Lost and shelved for Opportunities
 * 
 * @author bnpp
 *
 */
@JsonFilter(Constants.FILTER)
public class LeadershipOpportunitiesDTO implements Serializable{

    private static final long serialVersionUID = 4061888506114061511L;

    private LeadershipOpportunityBySalesStageCodeDTO oppProspects;
    
    private LeadershipOpportunityBySalesStageCodeDTO oppPipeline;
    
    private LeadershipOpportunityBySalesStageCodeDTO oppWon;
    
    private LeadershipOpportunityBySalesStageCodeDTO oppLost;
    
    private LeadershipOpportunityBySalesStageCodeDTO oppShelved;

    public LeadershipOpportunityBySalesStageCodeDTO getOppProspects() {
        return oppProspects;
    }

    public void setOppProspects(
    	LeadershipOpportunityBySalesStageCodeDTO oppProspects) {
        this.oppProspects = oppProspects;
    }

    public LeadershipOpportunityBySalesStageCodeDTO getOppPipeline() {
        return oppPipeline;
    }

    public void setOppPipeline(LeadershipOpportunityBySalesStageCodeDTO oppPipeline) {
        this.oppPipeline = oppPipeline;
    }

    public LeadershipOpportunityBySalesStageCodeDTO getOppWon() {
        return oppWon;
    }

    public void setOppWon(LeadershipOpportunityBySalesStageCodeDTO oppWon) {
        this.oppWon = oppWon;
    }

    public LeadershipOpportunityBySalesStageCodeDTO getOppLost() {
        return oppLost;
    }

    public void setOppLost(LeadershipOpportunityBySalesStageCodeDTO oppLost) {
        this.oppLost = oppLost;
    }

    public LeadershipOpportunityBySalesStageCodeDTO getOppShelved() {
        return oppShelved;
    }

    public void setOppShelved(LeadershipOpportunityBySalesStageCodeDTO oppShelved) {
        this.oppShelved = oppShelved;
    }
    
}
