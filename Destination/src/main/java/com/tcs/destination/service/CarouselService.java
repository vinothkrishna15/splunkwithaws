package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.tcs.destination.bean.dto.CarouselMetricsDTO;
import com.tcs.destination.bean.dto.HealthCardMetrics;
import com.tcs.destination.bean.dto.OpportunityMetrics;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.DeliveryCentreUnallocationRepository;
import com.tcs.destination.data.repository.DeliveryCentreUtilizationRepository;
import com.tcs.destination.data.repository.MobileDashboardRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.enums.HealthCardCategory;

import static com.tcs.destination.enums.SalesStageCode.WIN;
import static com.tcs.destination.enums.SalesStageCode.CLOSED_AND_DISQUALIFIED;
import static com.tcs.destination.enums.SalesStageCode.CLOSED_AND_SCRAPPED;
import static com.tcs.destination.enums.SalesStageCode.LOST;

import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;

import static com.tcs.destination.enums.CarouselMetricsType.ALL;
import static com.tcs.destination.enums.CarouselMetricsType.CUSTOMER;
import static com.tcs.destination.enums.CarouselMetricsType.OPPORTUNITY;
import static com.tcs.destination.enums.CarouselMetricsType.WIN_RATIO;
import static com.tcs.destination.enums.CarouselMetricsType.HEALTH_CARD;

@Service
public class CarouselService {

	private static final Logger logger = LoggerFactory
			.getLogger(CarouselService.class);

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	CustomerService customerService;

	@Autowired
	MobileDashboardRepository mobileDashboardRepository;

	@Autowired
	DeliveryCentreUtilizationRepository deliveryCentreUtilizationRepository;

	@Autowired
	DeliveryCentreUnallocationRepository deliveryCentreUnallocationRepository;

	/**
	 * gets the carousel metrics values
	 * 
	 * @param type
	 * @param fromDate
	 * @param toDate
	 * @param associateCount
	 * @return
	 */
	public CarouselMetricsDTO getCarouselMetrics(String type, Date fromDate,
			Date toDate, Long associateCount) {
		logger.info("Start of getCarouselMetrics");
		CarouselMetricsDTO carouselMetricsDTO = new CarouselMetricsDTO();
		Date startDate = fromDate != null ? fromDate : DateUtils
				.getFinancialYrStartDate();
		Date endDate = toDate != null ? toDate : new Date();
		String userId = DestinationUtils.getCurrentUserId();
		if (type.equals(HEALTH_CARD.getType()) || type.equals(ALL.getType())) {
			setHealthCardMetrics(carouselMetricsDTO, startDate, endDate, userId);
		}

		if (type.equals(CUSTOMER.getType()) || type.equals(ALL.getType())) {
			setCustomerMetrics(associateCount, carouselMetricsDTO, startDate,
					endDate);
		}

		if (type.equals(OPPORTUNITY.getType()) || type.equals(ALL.getType())) {
			setOpportunityMetrics(carouselMetricsDTO, startDate, endDate);
		}

		if (type.equals(WIN_RATIO.getType()) || type.equals(ALL.getType())) {
			setWinRatioMetrics(carouselMetricsDTO, startDate, endDate, false,
					null);
		}
		logger.info("End of getCarouselMetrics");
		return carouselMetricsDTO;
	}

	/**
	 * This method is used to set the health card metrics
	 * @param carouselMetricsDTO
	 * @param startDate
	 * @param endDate
	 * @param userId
	 */
	private void setHealthCardMetrics(CarouselMetricsDTO carouselMetricsDTO,
			Date startDate, Date endDate, String userId) {
		logger.debug("Start of setHealthCardMetrics method");
		List<Integer> categories = mobileDashboardRepository
				.getFirstThreeCategoriesInHealthCard(userId);
		HealthCardMetrics healthCardMetrics = new HealthCardMetrics();
		for (Integer category : categories) {
			switch (HealthCardCategory.valueOfCategory(category)) {
			case WINS_RATIO:
				setWinRatioMetrics(carouselMetricsDTO, startDate, endDate,
						true, healthCardMetrics);
				break;
			case UTILIZATION:
				BigDecimal utilization = deliveryCentreUtilizationRepository
						.getOverallPercentage(startDate, endDate,
								HealthCardCategory.UTILIZATION.getCategoryId());
				healthCardMetrics.setUtilization(utilization);
				break;
			case UNALLOCATION:
				BigDecimal unallocation = new BigDecimal(0);
				healthCardMetrics.setUnallocation(unallocation);
				//TODO calcualte unallocation
				break;
			case BILABILITY:
				BigDecimal bilability = deliveryCentreUtilizationRepository
						.getOverallPercentage(startDate, endDate,
								HealthCardCategory.BILABILITY.getCategoryId());
				healthCardMetrics.setBilability(bilability);
				break;
			case ATTRITION:
				BigDecimal attrition = deliveryCentreUtilizationRepository
						.getOverallPercentage(startDate, endDate,
								HealthCardCategory.ATTRITION.getCategoryId());
				healthCardMetrics.setAttrition(attrition);
				break;
			case SENIOR_RATIO:
				BigDecimal seniorRatio = deliveryCentreUtilizationRepository
						.getOverallPercentage(startDate, endDate,
								HealthCardCategory.SENIOR_RATIO.getCategoryId());
				healthCardMetrics.setSeniorRatio(seniorRatio);
				break;
			case SKILL_CATEGORY:
				// BigDecimal skillCategory =
				// deliveryCentreUtilizationRepository
				// .getOverallUtilizationPercentage(startDate, endDate,
				// HealthCardCategory.UTILIZATION.getCategoryId());
				// healthCardMetrics.setUtilization(utilization);
				break;
			case TRAINEE_PERCENTAGE:
				BigDecimal trainee = deliveryCentreUtilizationRepository
						.getOverallPercentage(startDate, endDate,
								HealthCardCategory.TRAINEE_PERCENTAGE
										.getCategoryId());
				healthCardMetrics.setTraineePercentage(trainee);
				break;
			default:
				break;
			}
		}
		carouselMetricsDTO.setHealthCardMetrics(healthCardMetrics);
		logger.debug("End of setHealthCardMetrics method");
	}

	/**
	 * This method is used to set the opportunity metrics
	 * @param carouselMetricsDTO
	 * @param startDate
	 * @param endDate
	 */
	private void setOpportunityMetrics(CarouselMetricsDTO carouselMetricsDTO,
			Date startDate, Date endDate) {
		logger.debug("Start of setOpportunityMetrics method");
		List<Object[]> qualifiedList = opportunityRepository
				.getQualifiedValues();
		Object[] qualifiedObj = qualifiedList.get(0);
		Integer noOfQualifiedOpp = ((BigInteger) qualifiedObj[0]).intValue();
		BigDecimal qualifiedValue = (BigDecimal) qualifiedObj[1];
		Object[] bidsSubmittedObj = getBidsSubmittedAndRequestReceived(
				startDate, endDate, true);
		Integer noOfbidsSubmittedOpp = ((BigInteger) bidsSubmittedObj[0])
				.intValue();
		BigDecimal bidsSubmittedValue = (BigDecimal) bidsSubmittedObj[1];
		Object[] requestReceivedObj = getBidsSubmittedAndRequestReceived(
				startDate, endDate, false);
		Integer requestReceivedOpp = ((BigInteger) requestReceivedObj[0])
				.intValue();
		BigDecimal requestReceivedValue = (BigDecimal) requestReceivedObj[1];
		carouselMetricsDTO.setOpportunityMetrics(constructOpportunityMetrics(
				noOfQualifiedOpp, qualifiedValue, noOfbidsSubmittedOpp,
				bidsSubmittedValue, requestReceivedOpp, requestReceivedValue));
		logger.debug("End of setOpportunityMetrics method");
	}

	private OpportunityMetrics constructOpportunityMetrics(
			Integer noOfQualifiedOpp, BigDecimal qualifiedValue,
			Integer noOfbidsSubmittedOpp, BigDecimal bidsSubmittedValue,
			Integer requestReceivedOpp, BigDecimal requestReceivedValue) {
		OpportunityMetrics opportunityMetrics = new OpportunityMetrics();
		opportunityMetrics.setBidsSubmittedValue(bidsSubmittedValue);
		opportunityMetrics.setNoOfBidsSubmitted(noOfbidsSubmittedOpp);
		opportunityMetrics.setNoOfQualified(noOfQualifiedOpp);
		opportunityMetrics.setNoOfRequestReceived(requestReceivedOpp);
		opportunityMetrics.setQualifiedValue(qualifiedValue);
		opportunityMetrics.setRequestReceivedValue(requestReceivedValue);
		return opportunityMetrics;
	}

	private Object[] getBidsSubmittedAndRequestReceived(Date startDate,
			Date endDate, boolean forBids) {
		if (forBids) {
			List<Object[]> bidsList = opportunityRepository
					.getBidsSubmittedCountAndValues(startDate, endDate);
			return bidsList.get(0);
		} else {
			List<Object[]> requestReceived = opportunityRepository
					.getRequestReceivedCountAndValues(startDate, endDate);
			return requestReceived.get(0);
		}
	}

	/**
	 * This method is used to set the customer metrics
	 * @param associateCount
	 * @param carouselMetricsDTO
	 * @param startDate
	 * @param endDate
	 */
	private void setCustomerMetrics(Long associateCount,
			CarouselMetricsDTO carouselMetricsDTO, Date startDate, Date endDate) {
		logger.debug("Start of setCustomerMetrics Method");
		String financialYear = DateUtils.getCurrentFinancialYear();
		Integer customerConnected = customerRepository
				.getCountOfUniqueCustomersConnected(startDate, endDate);
		List<Object[]> custWithHundredPlusObj = customerRepository
				.getCountOfCustomersByAssociates(associateCount);
		Integer count = custWithHundredPlusObj.size();
		Integer consultingCustomers = customerRepository
				.getCountOfConsultingCustomers(financialYear);
		carouselMetricsDTO.setCustomersWithHundredPlusAssociates(count);
		carouselMetricsDTO.setCustomersConnected(customerConnected);
		carouselMetricsDTO.setCustomersConsulting(consultingCustomers);
		logger.debug("End of setCustomerMetrics Method");
	}

	/**
	 * This method is used to set the win ratio metrics
	 * @param carouselMetricsDTO
	 * @param startDate
	 * @param endDate
	 * @param forHealthCard
	 * @param healthCardMetrics
	 */
	private void setWinRatioMetrics(CarouselMetricsDTO carouselMetricsDTO,
			Date startDate, Date endDate, Boolean forHealthCard,
			HealthCardMetrics healthCardMetrics) {
		logger.debug("Start of setWinRatioMetrics method");
		Object[] winObject = getWinLossValue(startDate, endDate,
				Lists.newArrayList(WIN.getCode()));
		Integer wins = ((BigInteger) winObject[0]).intValue();
		BigDecimal winValue = (BigDecimal) winObject[1];
		Object[] lossObject = getWinLossValue(startDate, endDate, Lists.newArrayList(LOST.getCode()));
		Integer loss = ((BigInteger) lossObject[0]).intValue();
		BigDecimal lossValue = (BigDecimal) lossObject[1];
		BigDecimal winRatio = customerService.getWinRatio(wins, loss);
		if (!forHealthCard) {
			carouselMetricsDTO.setLossValue(lossValue);
			carouselMetricsDTO.setNoOfLoss(loss);
			carouselMetricsDTO.setNoOfWins(wins);
			carouselMetricsDTO.setWinValue(winValue);
			carouselMetricsDTO.setWinsRatio(winRatio);
		} else {
			healthCardMetrics.setWinsRatio(winRatio);
		}
		logger.debug("End of setWinRatioMetrics method");
	}

	private Object[] getWinLossValue(Date startDate, Date endDate,
			ArrayList<Integer> salesStage) {
		List<Object[]> values = opportunityRepository.getWinAndLossValue(
				startDate, endDate, salesStage);
		return values.get(0);

	}

}
