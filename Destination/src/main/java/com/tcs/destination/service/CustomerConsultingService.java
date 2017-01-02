/**
 * 
 */
package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.dto.ConsultingMonthlyRevenue;
import com.tcs.destination.bean.dto.CustomerConsultingDTO;
import com.tcs.destination.data.repository.CustomerConsultingRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;

/**
 * @author tcs2
 *
 */
@Service
public class CustomerConsultingService {

	private static final Logger logger = LoggerFactory
			.getLogger(CustomerConsultingService.class);

	@Autowired
	CustomerConsultingRepository customerConsultingRepository;

	@Autowired
	BeaconConverterService beaconConverterService;

	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	public CustomerConsultingDTO findRevenueDetailsOfConsultedCustomers(
			String financialYear) {
		
		logger.debug("Entry for findRevenueDetailsOfConsultedCustomers() service");

		BigDecimal revenueByCustomerInINR = BigDecimal.ZERO;
		BigDecimal costByCustomerInINR = BigDecimal.ZERO;

		CustomerConsultingDTO custConsulting = new CustomerConsultingDTO();

		List<String> groupCustomerList = new ArrayList<String>();
		List<String> groupCustomerListCost = new ArrayList<String>();

		List<BigDecimal> customerRevenueList = new ArrayList<BigDecimal>();
		List<BigDecimal> customerRevenueListBasedOnCost = new ArrayList<BigDecimal>();

		Date fromDate = DateUtils.getDateFromFinancialYear(financialYear, true);
		Date toDate = DateUtils.getDateFromFinancialYear(financialYear, false);

		// Logic to calculate total revenue and cost based on group customer
		calculateRevenueAndGrossMargin(revenueByCustomerInINR,
				costByCustomerInINR, custConsulting, groupCustomerList,
				customerRevenueList, customerRevenueListBasedOnCost,
				financialYear, groupCustomerListCost);

		// query to fetch the WINS count by group customer where sales stage is
		// 9 (WINS)

		List<Object[]> totalDealValueAndCustomerListByWins = customerConsultingRepository
				.findSumOfDealvalueWithGroupCustomerByWins(fromDate, toDate);

		BigDecimal totalDealPriceInUSDWins = calculateDealValueForWinsAndQualified(totalDealValueAndCustomerListByWins);

		custConsulting
				.setNumberOfConsultedWins(totalDealValueAndCustomerListByWins
						.size());
		custConsulting.setTotalConsultedWinsRevenue(totalDealPriceInUSDWins);

		// Values for Qualified Pipeline
		// query to fetch the WINS count by group customer where sales stage is
		// 4-8
		List<Object[]> totalRevenueAndCustomerListByQualified = customerConsultingRepository
				.findSumOfDealvalueWithGroupCustomerByQualified(fromDate,
						toDate);

		BigDecimal totalDealPriceInUSDQualified = calculateDealValueForWinsAndQualified(

		totalRevenueAndCustomerListByQualified);

		custConsulting
				.setNumberOfConsultedQualifiedPipeline(totalRevenueAndCustomerListByQualified
						.size());
		custConsulting
				.setTotalConsultedQualifiedRevenue(totalDealPriceInUSDQualified);

		Date modifiedDate = customerConsultingRepository
				.findLastModifiedDate(financialYear);
		custConsulting.setLastModifiedDate(modifiedDate.getTime());

		// Db call to fetch the list of revenue and month

		List<Object[]> monthlyRevenueDetails = customerConsultingRepository
				.findMonthwiseRevenueForConsulting(financialYear);
		BigDecimal monthlyRevenueUSD = BigDecimal.ZERO;
		List<ConsultingMonthlyRevenue> newList = new ArrayList<ConsultingMonthlyRevenue>();
		for (Object[] revenue : monthlyRevenueDetails) {
			ConsultingMonthlyRevenue consultingMonthlyRevenue = new ConsultingMonthlyRevenue();
			if (null != revenue[0] && null != revenue[0]) {
				BigDecimal monthlyRevenueINR = (BigDecimal) revenue[1];
				monthlyRevenueUSD = beaconConverterService.convert("INR",
						"USD", monthlyRevenueINR);
				consultingMonthlyRevenue.setMonth(revenue[0].toString());
				consultingMonthlyRevenue.setRevenue(monthlyRevenueUSD);
				newList.add(consultingMonthlyRevenue);
			}
		}
		custConsulting.setConsultingMonthlyRevenue(newList);
		logger.debug("Exit for findRevenueDetailsOfConsultedCustomers() service");
		return custConsulting;

	}

	/**
	 * Method to calculate total deal value for Wins and qualified pipeline
	 * based on the financial year and deal closure date
	 * 
	 * @param dealPriceByWinsInUSD
	 * @param groupCustomerListWins
	 * @param totalDealValueAndCustomerListByWins
	 * @return
	 * @throws DestinationException
	 */
	private BigDecimal calculateDealValueForWinsAndQualified(

	List<Object[]> totalDealValueAndCustomerList) throws DestinationException {
		BigDecimal totalDealPriceInUSD = BigDecimal.ZERO;
		BigDecimal dealPriceByWinsInUSD = BigDecimal.ZERO;
		for (int i = 0; i < totalDealValueAndCustomerList.size(); i++) {

			Object[] dealValueAndCustomer = totalDealValueAndCustomerList
					.get(i);
			if (null != (dealValueAndCustomer[1])
					&& (null != dealValueAndCustomer[0])) {
				String dealCurrency = dealValueAndCustomer[1].toString();
				BigInteger dealValue = (BigInteger) dealValueAndCustomer[0];
				dealPriceByWinsInUSD = beaconConverterService.convert(
						dealCurrency, "USD", dealValue.doubleValue());
				totalDealPriceInUSD = totalDealPriceInUSD
						.add(dealPriceByWinsInUSD);
			}
		}
		return totalDealPriceInUSD;
	}

	/**
	 * Method to calculate the total revenue of consulted customers and also the
	 * total gross margin percentage based on revenue and cost
	 * 
	 * @param revenueInINR
	 * @param costInINR
	 * @param custConsulting
	 * @param groupCustomerList
	 * @param customerRevenueList
	 * @param customerRevenueListBasedOnCost
	 * @throws DestinationException
	 */
	private void calculateRevenueAndGrossMargin(BigDecimal revenueInINR,
			BigDecimal costInINR, CustomerConsultingDTO custConsulting,
			List<String> groupCustomerList,
			List<BigDecimal> customerRevenueList,
			List<BigDecimal> customerRevenueListBasedOnCost,
			String financialYear, List<String> groupCustomerListCost)
			throws DestinationException {
		BigDecimal totalGrossAmount = BigDecimal.ZERO;
		// Call to Database to fetch the list of group_customers and revenue
		List<Object[]> totalRevenueAndCustomerList = customerConsultingRepository
				.findSumOfRevenueWithGroupCustomerByRevenue(financialYear);

		revenueInINR = calculateCustomersConsultedDetails(revenueInINR,
				groupCustomerList, customerRevenueList,
				totalRevenueAndCustomerList, financialYear);
		BigDecimal revenueInUSD = beaconConverterService.convert("INR", "USD",
				revenueInINR);
		custConsulting.setNumberOfCustomersConsulted(groupCustomerList);
		custConsulting.setTotalConsultedRevenueInUSD(revenueInUSD);

		// Need to write logic to calculate the
		List<Object[]> totalRevenueBycostWithCustomer = customerConsultingRepository
				.findSumOfRevenueWithGroupCustomerByCost(financialYear);

		costInINR = calculateCustomersConsultedDetails(costInINR,
				groupCustomerListCost, customerRevenueListBasedOnCost,
				totalRevenueBycostWithCustomer, financialYear);
		BigDecimal costInUSD = beaconConverterService.convert("INR", "USD",
				costInINR);
		if (revenueInUSD.compareTo(BigDecimal.ZERO) != 0) {
			totalGrossAmount = ((revenueInUSD.subtract(costInUSD)).divide(
					revenueInUSD, 4, RoundingMode.HALF_UP))
					.multiply(ONE_HUNDRED);
		}
		custConsulting.setTotalGrossMargin(totalGrossAmount);
	}

	/**
	 * Method to calculate the total revenue based on cost or revenue and to
	 * return the value in INR
	 * 
	 * @param revenueInINR
	 * @param groupCustomerList
	 * @param customerRevenueList
	 * @param totalRevenueAndCustomerList
	 * @return
	 * @throws DestinationException
	 */
	private BigDecimal calculateCustomersConsultedDetails(
			BigDecimal revenueInINR, List<String> groupCustomerList,
			List<BigDecimal> customerRevenueList,
			List<Object[]> totalRevenueAndCustomerList, String financialYear) {
		if (!totalRevenueAndCustomerList.isEmpty()
				&& totalRevenueAndCustomerList != null) {
			for (int i = 0; i < totalRevenueAndCustomerList.size(); i++) {
				Object[] revenueAndCustomer = totalRevenueAndCustomerList
						.get(i);
				if (null != (revenueAndCustomer[1])
						&& (null != revenueAndCustomer[0])) {
					groupCustomerList.add(revenueAndCustomer[1].toString());
					customerRevenueList.add((BigDecimal) revenueAndCustomer[0]);
					revenueInINR = revenueInINR.add(customerRevenueList.get(i));
				}
			}

		} else {
			revenueInINR = BigDecimal.ZERO;

		}
		return revenueInINR;
	}

}
