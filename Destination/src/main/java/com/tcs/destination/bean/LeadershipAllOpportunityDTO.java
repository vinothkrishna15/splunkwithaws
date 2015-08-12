package com.tcs.destination.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * This DTO holds the opportunityId, digitalDealValue and salesStageCode for opportunities
 * 
 *
 */
public class LeadershipAllOpportunityDTO implements Serializable{

    private static final long serialVersionUID = 1L;

    private String opportunityId;
    
    private BigDecimal digitalDealValue;
    
    private Integer salesStageCode;

    public String getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(String opportunityId) {
        this.opportunityId = opportunityId;
    }

    public BigDecimal getDigitalDealValue() {
        return digitalDealValue;
    }

    public void setDigitalDealValue(BigDecimal digitalDealValue) {
        this.digitalDealValue = digitalDealValue;
    }

    public Integer getSalesStageCode() {
        return salesStageCode;
    }

    public void setSalesStageCode(Integer salesStageCode) {
        this.salesStageCode = salesStageCode;
    }


    
}
