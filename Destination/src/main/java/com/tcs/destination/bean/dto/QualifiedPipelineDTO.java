/**
 * 
 */
package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author tcs2
 *
 */
public class QualifiedPipelineDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int salesStageCode;
	private BigInteger opportunitiesCount;
	private BigDecimal digitalDealValue;
	private BigInteger oneMillionOpportunityCount;
	private BigInteger proactiveCount;

	
	
	public QualifiedPipelineDTO() {
		super();
	}

	public QualifiedPipelineDTO(int salesStageCode, BigInteger opportunitiesCount, BigDecimal digitalDealValue,
			BigInteger oneMillionOpportunityCount, BigInteger proactiveCount) {
		super();
		this.salesStageCode = salesStageCode;
		this.opportunitiesCount = opportunitiesCount;
		this.digitalDealValue = digitalDealValue;
		this.oneMillionOpportunityCount = oneMillionOpportunityCount;
		this.proactiveCount = proactiveCount;
	}

	/**
	 * @return the salesStageCode
	 */
	public int getSalesStageCode() {
		return salesStageCode;
	}

	/**
	 * @param salesStageCode
	 *            the salesStageCode to set
	 */
	public void setSalesStageCode(int salesStageCode) {
		this.salesStageCode = salesStageCode;
	}

	/**
	 * @return the digitalDealValue
	 */
	public BigDecimal getDigitalDealValue() {
		return digitalDealValue;
	}

	/**
	 * @param digitalDealValue
	 *            the digitalDealValue to set
	 */
	public void setDigitalDealValue(BigDecimal digitalDealValue) {
		this.digitalDealValue = digitalDealValue;
	}

	/**
	 * @return the opportunitiesCount
	 */
	public BigInteger getOpportunitiesCount() {
		return opportunitiesCount;
	}

	/**
	 * @param opportunitiesCount
	 *            the opportunitiesCount to set
	 */
	public void setOpportunitiesCount(BigInteger opportunitiesCount) {
		this.opportunitiesCount = opportunitiesCount;
	}

	/**
	 * @return the oneMillionOpportunityCount
	 */
	public BigInteger getOneMillionOpportunityCount() {
		return oneMillionOpportunityCount;
	}

	/**
	 * @param oneMillionOpportunityCount
	 *            the oneMillionOpportunityCount to set
	 */
	public void setOneMillionOpportunityCount(
			BigInteger oneMillionOpportunityCount) {
		this.oneMillionOpportunityCount = oneMillionOpportunityCount;
	}

	/**
	 * @return the proactiveCount
	 */
	public BigInteger getProactiveCount() {
		return proactiveCount;
	}

	/**
	 * @param proactiveCount
	 *            the proactiveCount to set
	 */
	public void setProactiveCount(BigInteger proactiveCount) {
		this.proactiveCount = proactiveCount;
	}

}
