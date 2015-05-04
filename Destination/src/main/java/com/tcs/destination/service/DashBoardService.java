package com.tcs.destination.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.PerformaceChartBean;
import com.tcs.destination.data.repository.BdmTargetTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DateUtils;

@Component
public class DashBoardService {

	private static final Logger logger = LoggerFactory
			.getLogger(DashBoardService.class);

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	BdmTargetTRepository bdmTargetRepository;

	@Autowired
	BeaconConverterService beaconService;

	public PerformaceChartBean getChartValues(String userId,
			String financialYear) throws Exception {

		boolean hasValues = false;

		PerformaceChartBean performanceBean = new PerformaceChartBean();

		financialYear = financialYear.equals("") ? DateUtils
				.getCurrentFinancialYear() : financialYear;
		List<BigDecimal> targetList = bdmTargetRepository
				.findSumOfTargetByBdmTargetIdAndYear(userId, financialYear);
		if (targetList != null && !targetList.isEmpty()) {
			performanceBean.setTarget(targetList.get(0));
			System.out.println("Has Target");
			if (targetList.get(0) != null)
				hasValues = true;
		}
		String year = financialYear.substring(3, 7);
		System.out.println("Year " + year);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(year));
		cal.set(Calendar.MONTH, Calendar.APRIL);
		cal.set(Calendar.DATE, 1);
		Date fromDate = new Date(cal.getTimeInMillis());
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
		// cal.add(Calendar.DATE, -1);
		Date toDate = new Date(cal.getTimeInMillis());

//		System.out.println("Date between : " + fromDate + " - " + toDate);

		List<Object[]> pipelineList = opportunityRepository
				.findDealValueForPipeline(userId,
						new Timestamp(toDate.getTime()));

		System.out.println("Pipeline");

		BigDecimal pipelineSum = new BigDecimal(0);

		for (Object[] pipeline : pipelineList) {
//			System.out.println("Initialised Big Decimal " + pipeline[1]);
//			System.out.println("Initialised Big Decimal " + pipeline[0]);
			if (pipeline[1] != null && pipeline[0] != null) {
				pipelineSum = pipelineSum.add(beaconService.convert(
						pipeline[1].toString(), "USD",
						((Integer) pipeline[0]).doubleValue()));
				hasValues = true;
			}

		}
//		System.out.println("For Ended	with sum " + pipelineSum);
		performanceBean.setPipelineSum(pipelineSum);
//		System.out.println(">>>>>>>>>>>> Pipeline <<<<<<<");

//		System.out.println("Wins");
		List<Object[]> winList = opportunityRepository.findDealValueForWins(
				userId, fromDate, toDate);
		BigDecimal winSum = new BigDecimal(0);
		for (Object[] win : winList) {
			if (win[1] != null && win[0] != null)
				winSum = winSum.add(beaconService.convert(win[1].toString(),
						"USD", ((Integer) win[0]).doubleValue()));
			hasValues = true;
		}
		performanceBean.setWinSum(winSum);

//		System.out.println(">>>>>>>>>>>>>>>> Wins <<<<<<<<<<<<<<< " + winSum);
		if (!hasValues) {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Not Data found for the performance Chart");
		}
		return performanceBean;
	}

}
