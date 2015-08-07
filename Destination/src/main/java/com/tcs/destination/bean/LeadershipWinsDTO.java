package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

/**
 * This DTO holds the response for WINS module in Leadership Dashboard 
 * 
 * @author bnpp
 *
 */

public class LeadershipWinsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer sizeOfWins;

    private Integer digitalDealValueSum;

    private List<OpportunityT> listOfWins;

    public Integer getSizeOfWins() {
	return sizeOfWins;
    }

    public void setSizeOfWins(Integer sizeOfWins) {
	this.sizeOfWins = sizeOfWins;
    }

    public Integer getDigitalDealValueSum() {
	return digitalDealValueSum;
    }

    public void setDigitalDealValueSum(Integer digitalDealValueSum) {
	this.digitalDealValueSum = digitalDealValueSum;
    }

    public List<OpportunityT> getListOfWins() {
	return listOfWins;
    }

    public void setListOfWins(List<OpportunityT> listOfWins) {
	this.listOfWins = listOfWins;
    }

}
