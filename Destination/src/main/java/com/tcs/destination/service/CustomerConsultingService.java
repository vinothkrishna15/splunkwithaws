/**
 * 
 */
package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.dto.ConsultingMonthlyCustomer;
import com.tcs.destination.bean.dto.CustomerConsultingDTO;
import com.tcs.destination.data.repository.CustomerConsultingRepository;
import com.tcs.destination.exception.DestinationException;
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

		BigDecimal customerRevenueList = BigDecimal.ZERO;
		BigDecimal customerRevenueListBasedOnCost = BigDecimal.ZERO;

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

		BigDecimal totalDealPriceInUSDQualified = calculateDealValueForWinsAndQualified(totalRevenueAndCustomerListByQualified);

		custConsulting
				.setNumberOfConsultedQualifiedPipeline(totalRevenueAndCustomerListByQualified
						.size());
		custConsulting
				.setTotalConsultedQualifiedRevenue(totalDealPriceInUSDQualified);

		Date modifiedDate = customerConsultingRepository
				.findLastModifiedDate(financialYear);
		if (null != modifiedDate) {
			custConsulting.setLastModifiedDate(modifiedDate.getTime());
		} else {
			custConsulting.setLastModifiedDate(0L);
		}
		// Db call to fetch the list of revenue and month

		List<Object[]> monthlyRevenueDetails = customerConsultingRepository
				.findMonthwiseRevenueForConsulting(financialYear);

		List<ConsultingMonthlyCustomer> newList = new ArrayList<ConsultingMonthlyCustomer>();
		for (Object[] revenue : monthlyRevenueDetails) {
			ConsultingMonthlyCustomer consultingMonthlyCustomer = new ConsultingMonthlyCustomer();
			if (null != revenue[0] && null != revenue[0]) {
				consultingMonthlyCustomer.setMonth(revenue[0].toString());
				consultingMonthlyCustomer.setCustomerCount(Integer.valueOf(revenue[1].toString()));
				newList.add(consultingMonthlyCustomer);
			}
		}
		custConsulting.setConsultingMonthlyCustomer(newList);
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
		for (Object[] dealValueAndCustomer : totalDealValueAndCustomerList) {

			if (null != (dealValueAndCustomer[1])
					&& (null != dealValueAndCustomer[0])) {

				BigDecimal dealValue = (BigDecimal) dealValueAndCustomer[0];

				totalDealPriceInUSD = totalDealPriceInUSD.add(dealValue
						.setScale(2, RoundingMode.HALF_UP));
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
	private void calculateRevenueAndGrossMargin(BigDecimal revenueInUSD,
			BigDecimal costInUSD, CustomerConsultingDTO custConsulting,
			List<String> groupCustomerList, BigDecimal customerRevenueList,
			BigDecimal customerRevenueListBasedOnCost, String financialYear,
			List<String> groupCustomerListCost) throws DestinationException {
		BigDecimal totalGrossAmount = BigDecimal.ZERO;
		// Call to Database to fetch the list of group_customers and revenue
		List<Object[]> totalRevenueAndCustomerList = customerConsultingRepository
				.findSumOfRevenueWithGroupCustomerByRevenue(financialYear);

		revenueInUSD = calculateCustomersConsultedDetails(revenueInUSD,
				groupCustomerList, customerRevenueList,
				totalRevenueAndCustomerList, financialYear);

		custConsulting.setNumberOfCustomersConsulted(groupCustomerList);
/*		custConsulting.setTotalConsultedRevenueInUSD(revenueInUSD.setScale(2,
				RoundingMode.HALF_UP));*/

		// Need to write logic to calculate the
		List<Object[]> totalRevenueBycostWithCustomer = customerConsultingRepository
				.findSumOfRevenueWithGroupCustomerByCost(financialYear);

		costInUSD = (calculateCustomersConsultedDetails(costInUSD,
				groupCustomerListCost, customerRevenueListBasedOnCost,
				totalRevenueBycostWithCustomer, financialYear).setScale(2,
				RoundingMode.HALF_UP));

		if (revenueInUSD.compareTo(BigDecimal.ZERO) != 0) {
			totalGrossAmount = ((revenueInUSD.subtract(costInUSD)).divide(
					revenueInUSD, 4, RoundingMode.HALF_UP))
					.multiply(ONE_HUNDRED);
		}
		//custConsulting.setTotalGrossMargin(totalGrossAmount);
	}

	/**
	 * Method to calculate the total revenue based on cost or revenue and to
	 * return the value in INR
	 * 
	 * @param revenueInINR
	 * @param groupCustomerList
	 * @param customerRevenue
	 * @param totalRevenueAndCustomerList
	 * @return
	 * @throws DestinationException
	 */
	private BigDecimal calculateCustomersConsultedDetails(
			BigDecimal revenueInUSD, List<String> groupCustomerList,
			BigDecimal customerRevenue,
			List<Object[]> totalRevenueAndCustomerList, String financialYear) {
		if (!totalRevenueAndCustomerList.isEmpty()
				&& totalRevenueAndCustomerList != null) {
			for (Object[] revenueAndCustomer : totalRevenueAndCustomerList) {

				if (null != (revenueAndCustomer[1])
						&& (null != revenueAndCustomer[0])) {
					groupCustomerList.add(revenueAndCustomer[1].toString());
					customerRevenue = (BigDecimal) revenueAndCustomer[0];
					revenueInUSD = revenueInUSD.add(customerRevenue);
				}
			}

		} else {
			revenueInUSD = BigDecimal.ZERO;

		}
		return revenueInUSD;
	}

}
