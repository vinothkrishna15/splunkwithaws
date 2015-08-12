package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

/**
 * This DTO holds the response for WINS module in Leadership Dashboard 
 * 
 *
 */

public class LeadershipWinsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer sizeOfWins;

    private Double sumOfdigitalDealValue;

    private List<OpportunityT> listOfWins;

    public Integer getSizeOfWins() {
	return sizeOfWins;
    }

    public void setSizeOfWins(Integer sizeOfWins) {
	this.sizeOfWins = sizeOfWins;
    }

    public Double getSumOfdigitalDealValue() {
        return sumOfdigitalDealValue;
    }

    public void setSumOfdigitalDealValue(Double sumOfdigitalDealValue) {
        this.sumOfdigitalDealValue = sumOfdigitalDealValue;
    }

    public List<OpportunityT> getListOfWins() {
	return listOfWins;
    }

    public void setListOfWins(List<OpportunityT> listOfWins) {
	this.listOfWins = listOfWins;
    }

}
