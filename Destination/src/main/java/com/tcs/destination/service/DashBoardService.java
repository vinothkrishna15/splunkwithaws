package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.BigInteger;
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

		List<BigInteger> pipelineList = opportunityRepository
				.findDealValueForPipeline(userId);

		if (pipelineList != null && !pipelineList.isEmpty()) {
			performanceBean.setPipelineSum(pipelineList.get(0));
			System.out.println("Has Pipeline");
			if (pipelineList.get(0) != null)
				hasValues = true;
		}

		String year = financialYear.substring(3, 7);
		System.out.println("Year " + year);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(year));
		cal.set(Calendar.MONTH, 4);
		cal.set(Calendar.DATE, 1);
		Date afterDate = new Date(cal.getTimeInMillis());
		List<BigInteger> winList = opportunityRepository.findDealValueForWins(
				userId, afterDate);

		if (winList != null && !winList.isEmpty()) {
			performanceBean.setWinSum(winList.get(0));
			if (winList.get(0) != null)
				hasValues = true;
		}

		if (!hasValues) {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Not Data found for the performance Chart");
		}
		return performanceBean;
	}

}
