/**
 * 
 */
package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author tcs2
 *
 */
public class CustomerConsultingDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<String> numberOfCustomersConsulted;

/*	private BigDecimal totalConsultedRevenueInUSD;
	private BigDecimal totalGrossMargin;*/
	private int numberOfConsultedWins;
	private BigDecimal totalConsultedWinsRevenue;
	private int numberOfConsultedQualifiedPipeline;
	private BigDecimal totalConsultedQualifiedRevenue;

	private List<ConsultingMonthlyCustomer> consultingMonthlyCustomer;
	private Long lastModifiedDate;

	/**
	 * @return the numberOfCustomersConsulted
	 */
	public List<String> getNumberOfCustomersConsulted() {
		return numberOfCustomersConsulted;
	}

	/**
	 * @param numberOfCustomersConsulted
	 *            the numberOfCustomersConsulted to set
	 */
	public void setNumberOfCustomersConsulted(
			List<String> numberOfCustomersConsulted) {
		this.numberOfCustomersConsulted = numberOfCustomersConsulted;
	}

	/**
	 * @return the totalConsultedRevenueInUSD
	 *//*
	public BigDecimal getTotalConsultedRevenueInUSD() {
		return totalConsultedRevenueInUSD;
	}

	*//**
	 * @param totalConsultedRevenueInUSD
	 *            the totalConsultedRevenueInUSD to set
	 *//*
	public void setTotalConsultedRevenueInUSD(
			BigDecimal totalConsultedRevenueInUSD) {
		this.totalConsultedRevenueInUSD = totalConsultedRevenueInUSD;
	}

	*//**
	 * @return the totalGrossMargin
	 *//*
	public BigDecimal getTotalGrossMargin() {
		return totalGrossMargin;
	}

	*//**
	 * @param totalGrossMargin
	 *            the totalGrossMargin to set
	 *//*
	public void setTotalGrossMargin(BigDecimal totalGrossMargin) {
		this.totalGrossMargin = totalGrossMargin;
	}
*/
	/**
	 * @return the numberOfConsultedWins
	 */
	public int getNumberOfConsultedWins() {
		return numberOfConsultedWins;
	}

	/**
	 * @param numberOfConsultedWins
	 *            the numberOfConsultedWins to set
	 */
	public void setNumberOfConsultedWins(int numberOfConsultedWins) {
		this.numberOfConsultedWins = numberOfConsultedWins;
	}

	/**
	 * @return the numberOfConsultedQualifiedPipeline
	 */
	public int getNumberOfConsultedQualifiedPipeline() {
		return numberOfConsultedQualifiedPipeline;
	}

	/**
	 * @param numberOfConsultedQualifiedPipeline
	 *            the numberOfConsultedQualifiedPipeline to set
	 */
	public void setNumberOfConsultedQualifiedPipeline(
			int numberOfConsultedQualifiedPipeline) {
		this.numberOfConsultedQualifiedPipeline = numberOfConsultedQualifiedPipeline;
	}

	/**
	 * @return the totalConsultedWinsRevenue
	 */
	public BigDecimal getTotalConsultedWinsRevenue() {
		return totalConsultedWinsRevenue;
	}

	/**
	 * @param totalConsultedWinsRevenue
	 *            the totalConsultedWinsRevenue to set
	 */
	public void setTotalConsultedWinsRevenue(
			BigDecimal totalConsultedWinsRevenue) {
		this.totalConsultedWinsRevenue = totalConsultedWinsRevenue;
	}

	/**
	 * @return the totalConsultedQualifiedRevenue
	 */
	public BigDecimal getTotalConsultedQualifiedRevenue() {
		return totalConsultedQualifiedRevenue;
	}

	/**
	 * @param totalConsultedQualifiedRevenue
	 *            the totalConsultedQualifiedRevenue to set
	 */
	public void setTotalConsultedQualifiedRevenue(
			BigDecimal totalConsultedQualifiedRevenue) {
		this.totalConsultedQualifiedRevenue = totalConsultedQualifiedRevenue;
	}

	/**
	 * @return the consultingMonthlyRevenue
	 */
	public List<ConsultingMonthlyCustomer> getConsultingMonthlyCustomer() {
		return consultingMonthlyCustomer;
	}

	/**
	 * @param consultingMonthlyRevenue
	 *            the consultingMonthlyRevenue to set
	 */
	public void setConsultingMonthlyCustomer(
			List<ConsultingMonthlyCustomer> consultingMonthlyCustomer) {
		this.consultingMonthlyCustomer = consultingMonthlyCustomer;
	}

	/**
	 * @return the lastModifiedDate
	 */
	public Long getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate
	 *            the lastModifiedDate to set
	 */
	public void setLastModifiedDate(Long lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}
