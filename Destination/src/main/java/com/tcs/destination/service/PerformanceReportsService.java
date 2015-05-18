package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.BeaconConvertorMappingT;
import com.tcs.destination.bean.GeographyReport;
import com.tcs.destination.bean.IOUReport;
import com.tcs.destination.bean.SubSpReport;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.data.repository.ActualRevenuesDataTRepository;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.data.repository.BeaconDataTRepository;
import com.tcs.destination.data.repository.PerformanceReportsRepository;
import com.tcs.destination.exception.NoSuchCurrencyException;
import com.tcs.destination.utils.DateUtils;

@Component
public class PerformanceReportsService {

	private static final Logger logger = LoggerFactory
			.getLogger(PerformanceReportsService.class);

	@Autowired
	private PerformanceReportsRepository perfRepo;

	private static final BigDecimal ZERO_REVENUE = new BigDecimal("0.0");

	@Autowired
	ActualRevenuesDataTRepository actualsRepository;

	@Autowired
	BeaconDataTRepository beaconDataTRepository;

	@Autowired
	BeaconConvertorRepository beaconRepository;

	public List<TargetVsActualResponse> getTargetVsActualRevenueSummary(
			String financialYear, String quarter, String geography,
			String serviceLine, String iou, String customerName, String currency) {
		logger.info("Inside getRevenueSummary Service");
		BeaconConvertorMappingT beacon = beaconRepository
				.findByCurrencyName(currency);
		if (beacon == null) {
			logger.error("No Such Currency Exception");
			throw new NoSuchCurrencyException();
		}
		logger.info("Currency Name is " + beacon.getCurrencyName());

		if (financialYear.equals("")) {
			logger.debug("Financial Year is Empty");
			financialYear = DateUtils.getCurrentFinancialYear();
		}
		logger.debug("Financial Year: " + financialYear);

		List<Object[]> actualRevenueList = actualsRepository.findActualRevenue(
				financialYear, quarter, geography, iou, customerName,
				serviceLine);
		logger.info("Actual Revenue has " + actualRevenueList.size()
				+ " values");
		List<Object[]> targetRevenueList = null;
		if (serviceLine.equals("")) {
			targetRevenueList = beaconDataTRepository.findTargetRevenue(
					financialYear, quarter, geography, iou, customerName);
			logger.debug("Target Revenue has " + targetRevenueList.size()
					+ " values");
		}

		List<TargetVsActualResponse> targetList = new ArrayList<TargetVsActualResponse>();
		List<TargetVsActualResponse> actualList = new ArrayList<TargetVsActualResponse>();
		populateResponseList(actualRevenueList, actualList, false,
				beacon.getConversionRate());
		if (serviceLine.equals("")) {
			populateResponseList(targetRevenueList, targetList, true,
					beacon.getConversionRate());
			List<TargetVsActualResponse> tarActResponseList = mergeLists(
					targetList, actualList);
			return tarActResponseList;
		} else {
			return actualList;
		}
	}

	private void populateResponseList(List<Object[]> objList,
			List<TargetVsActualResponse> respList, boolean isTarget,
			BigDecimal conversionRate) {
		if (objList != null && !objList.isEmpty()) {
			for (Object[] objArr : objList) {
				TargetVsActualResponse resp = new TargetVsActualResponse();
				resp.setQuarter((String) objArr[0]);
				if (isTarget) {
					resp.setTarget(((BigDecimal) objArr[1]).divide(
							conversionRate, 2, RoundingMode.HALF_UP));
				} else {
					resp.setActual(((BigDecimal) objArr[1]).divide(
							conversionRate, 2, RoundingMode.HALF_UP));
				}
				respList.add(resp);
			}
		}
	}

	private static List<TargetVsActualResponse> mergeLists(
			List<TargetVsActualResponse> targetList,
			List<TargetVsActualResponse> actualList) {
		Map<String, BigDecimal[]> map = getMapFromLists(targetList, actualList);
		List<TargetVsActualResponse> respList = getMergedListFromMap(map);
		return respList;
	}

	private static List<TargetVsActualResponse> getMergedListFromMap(
			Map<String, BigDecimal[]> map) {
		List<TargetVsActualResponse> respList = new ArrayList<TargetVsActualResponse>();
		for (Map.Entry<String, BigDecimal[]> entry : map.entrySet()) {
			String quarter = entry.getKey();
			BigDecimal[] valuesList = entry.getValue();
			BigDecimal target = valuesList[0];
			BigDecimal actual = valuesList[1];
			TargetVsActualResponse resp = new TargetVsActualResponse();
			resp.setQuarter(quarter);
			resp.setActual(actual);
			resp.setTarget(target);
			respList.add(resp);
		}
		return respList;
	}

	private static Map<String, BigDecimal[]> getMapFromLists(
			List<TargetVsActualResponse> targetList,
			List<TargetVsActualResponse> actualList) {
		Map<String, BigDecimal[]> map = new TreeMap<String, BigDecimal[]>();

		// Populate Target Revenue
		for (TargetVsActualResponse obj : targetList) {
			BigDecimal[] values = new BigDecimal[2];
			String quarter = obj.getQuarter();
			BigDecimal target = obj.getTarget();
			values[0] = target;
			values[1] = ZERO_REVENUE;
			map.put(quarter, values);
		}

		// Populate Actual Revenue
		for (TargetVsActualResponse obj : actualList) {
			BigDecimal[] values1 = new BigDecimal[2];
			String quarter = obj.getQuarter();
			BigDecimal actual = obj.getActual();
			if (map.containsKey(quarter)) {
				BigDecimal[] valList = map.get(quarter);
				valList[1] = actual;
				map.put(quarter, valList);
			} else {
				values1[0] = ZERO_REVENUE;
				values1[1] = actual;
				map.put(quarter, values1);
			}
		}

		return map;
	}

	public List<IOUReport> getRevenuesByIOU(String financialYear,
			String quarter, String geography, String serviceLine,
			String currency) throws Exception {

		BeaconConvertorMappingT beacon = beaconRepository
				.findByCurrencyName(currency);
		if (beacon == null) {
			logger.error("No Such Currency Exception");
			throw new NoSuchCurrencyException();
		}

		List<Object[]> iouObjList = new ArrayList<Object[]>();
		iouObjList = perfRepo.getRevenuesByIOU(financialYear, quarter,
				geography, serviceLine);
		List<IOUReport> iouRevenuesList = new ArrayList<IOUReport>();

		for (Object[] obj : iouObjList) {
			IOUReport item = new IOUReport();
			item.setDisplayIOU((String) obj[0]);
			BigDecimal rev = new BigDecimal(obj[1].toString());
			// BigDecimal revenue = (BigDecimal) obj[1];
			// revenue = revenue.setScale(2, BigDecimal.ROUND_DOWN);
			item.setActualRevenue(rev.divide(beacon.getConversionRate(), 2)
					.setScale(2, BigDecimal.ROUND_DOWN));
			// item.setActualRevenue(rev.setScale(2, BigDecimal.ROUND_DOWN));
			iouRevenuesList.add(item);
		}
		return iouRevenuesList;
	}

	public List<SubSpReport> getRevenuesBySubSp(String financialYear,
			String quarter, String geography, String customerName, String iou,
			String currency) throws Exception {

		BeaconConvertorMappingT beacon = beaconRepository
				.findByCurrencyName(currency);
		if (beacon == null) {
			logger.error("No Such Currency Exception");
			throw new NoSuchCurrencyException();
		}

		List<Object[]> subObjList = new ArrayList<Object[]>();
		subObjList = perfRepo.getRevenuesBySubSp(financialYear, quarter,
				geography, customerName, iou);
		List<SubSpReport> subSpRevenuesList = new ArrayList<SubSpReport>();

		for (Object[] obj : subObjList) {
			SubSpReport item = new SubSpReport();
			item.setDisplaySubSp((String) obj[0]);
			BigDecimal rev = new BigDecimal(obj[1].toString());
			// BigDecimal revenue = (BigDecimal) obj[1];
			// revenue = revenue.setScale(2, BigDecimal.ROUND_DOWN);
			item.setActualRevenue(rev.divide(beacon.getConversionRate(), 2)
					.setScale(2, BigDecimal.ROUND_DOWN));
			// item.setActualRevenue(rev.setScale(2, BigDecimal.ROUND_DOWN));
			subSpRevenuesList.add(item);
		}
		return subSpRevenuesList;
	}

	public List<GeographyReport> getRevenuesByDispGeography(
			String financialYear, String quarter, String customer,
			String subSp, String iou, String currency) throws Exception {

		BeaconConvertorMappingT beacon = beaconRepository
				.findByCurrencyName(currency);
		if (beacon == null) {
			logger.error("No Such Currency Exception");
			throw new NoSuchCurrencyException();
		}

		List<Object[]> geoObjList = new ArrayList<Object[]>();
		geoObjList = perfRepo.getRevenuesByDispGeo(financialYear, quarter,
				customer, subSp, iou);
		List<GeographyReport> geoRevenuesList = new ArrayList<GeographyReport>();

		for (Object[] obj : geoObjList) {
			GeographyReport item = new GeographyReport();
			item.setGeography((String) obj[0]);
			BigDecimal rev = new BigDecimal(obj[1].toString());
			// BigDecimal revenue = (BigDecimal) obj[1];
			// revenue = revenue.setScale(2, BigDecimal.ROUND_DOWN);
			item.setActualRevenue(rev.divide(beacon.getConversionRate(), 2)
					.setScale(2, BigDecimal.ROUND_DOWN));
			// item.setActualRevenue(rev.setScale(2, BigDecimal.ROUND_DOWN));
			geoRevenuesList.add(item);
		}
		return geoRevenuesList;
	}

	public List<GeographyReport> getRevenuesBySubGeography(
			String financialYear, String quarter, String customer,
			String subSp, String iou, String geography, String currency)
			throws Exception {

		BeaconConvertorMappingT beacon = beaconRepository
				.findByCurrencyName(currency);
		if (beacon == null) {
			logger.error("No Such Currency Exception");
			throw new NoSuchCurrencyException();
		}

		List<Object[]> geoObjList = new ArrayList<Object[]>();
		geoObjList = perfRepo.getRevenuesBySubGeo(financialYear, quarter,
				customer, subSp, iou, geography);
		List<GeographyReport> geoRevenuesList = new ArrayList<GeographyReport>();

		for (Object[] obj : geoObjList) {
			GeographyReport item = new GeographyReport();
			item.setGeography((String) obj[0]);
			BigDecimal rev = new BigDecimal(obj[1].toString());
			// BigDecimal revenue = (BigDecimal) obj[1];
			// revenue = revenue.setScale(2, BigDecimal.ROUND_DOWN);
			item.setActualRevenue(rev.divide(beacon.getConversionRate(), 2)
					.setScale(2, BigDecimal.ROUND_DOWN));
			// item.setActualRevenue(rev.setScale(2, BigDecimal.ROUND_DOWN));
			geoRevenuesList.add(item);
		}
		return geoRevenuesList;
	}

}
