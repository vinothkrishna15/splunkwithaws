package com.tcs.destination.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * This DTO holds the list of opportunities and sum of digital deal value per sales stage code 
 * 
 * @author bnpp
 *
 */
public class LeadershipOpportunityBySalesStageCodeDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private List<OpportunityT> opportunities;
    
    private Double sumOfDigitalDealValue;
    
    private Integer sizeOfOpportunities;

    public Integer getSizeOfOpportunities() {
        return sizeOfOpportunities;
    }

    public void setSizeOfOpportunities(Integer sizeOfOpportunities) {
        this.sizeOfOpportunities = sizeOfOpportunities;
    }

    public List<OpportunityT> getOpportunities() {
        return opportunities;
    }

    public void setOpportunities(List<OpportunityT> opportunities) {
        this.opportunities = opportunities;
    }

    public Double getSumOfDigitalDealValue() {
        return sumOfDigitalDealValue;
    }

    public void setSumOfDigitalDealValue(Double sumOfDigitalDealValue) {
        this.sumOfDigitalDealValue = sumOfDigitalDealValue;
    }

}
