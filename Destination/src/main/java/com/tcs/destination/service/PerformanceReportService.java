package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.neo4j.cypher.internal.compiler.v2_1.functions.Round;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.GeographyReport;
import com.tcs.destination.bean.IOUReport;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.ReportsOpportunity;
import com.tcs.destination.bean.ReportsSalesStage;
import com.tcs.destination.bean.SalesStageMappingT;
import com.tcs.destination.bean.SubSpReport;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.data.repository.ActualRevenuesDataTRepository;
import com.tcs.destination.data.repository.BeaconDataTRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.PerformanceReportRepository;
import com.tcs.destination.data.repository.ProjectedRevenuesDataTRepository;
import com.tcs.destination.data.repository.SalesStageMappingRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DateUtils;

@Service
public class PerformanceReportService {

	private static final Logger logger = LoggerFactory
			.getLogger(PerformanceReportService.class);

	private static final BigDecimal ZERO_REVENUE = new BigDecimal("0.0");

	@Autowired
	private PerformanceReportRepository perfRepo;

	@Autowired
	private OpportunityRepository opportunityRepository;

	@Autowired
	BeaconConverterService beaconService;

	@Autowired
	ActualRevenuesDataTRepository actualsRepository;

	@Autowired
	ProjectedRevenuesDataTRepository projectedRepository;

	@Autowired
	BeaconDataTRepository beaconDataTRepository;

	@Autowired
	SalesStageMappingRepository salesStageMappingRepository;

	@Autowired
	CustomerRepository customerRepository;

	public List<TargetVsActualResponse> getTargetVsActualRevenueSummary(
			String financialYear, String quarter, String displayGeography,
			String geography, String serviceLine, String iou,
			String customerName, String currency, String groupCustomer,
			boolean wins) throws Exception {
		logger.info("Inside getRevenueSummary Service");
		List<String> custName = new ArrayList<String>();
		if (customerName.length() == 0 && groupCustomer.length() > 0) {
			custName = customerRepository
					.findByGroupCustomerName(groupCustomer);
			if (custName.isEmpty()) {
				logger.error("NOT_FOUND: Invalid Group Customer");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Invalid Group Customer");
			}
		} else {
			custName.add(customerName);
		}
		
		logger.debug("Financial Year: " + financialYear);
		if (!wins) {
			if (financialYear.equals("")) {
				logger.debug("Financial Year is Empty");
				financialYear = DateUtils.getCurrentFinancialYear();
			}
			// Get get data of actuals
			List<Object[]> actualObjList = null;
			if (quarter.isEmpty()) {
				actualObjList = actualsRepository.findActualRevenue(
						financialYear, quarter, displayGeography, geography,
						iou, custName, serviceLine);
			} else {
				actualObjList = actualsRepository.findActualRevenueByQuarter(
						financialYear, quarter, displayGeography, geography,
						iou, custName, serviceLine);
			}
			logger.info("Actual Revenue has " + actualObjList.size()
					+ " values");

			Map<String, BigDecimal> quarterMap = getMapFromObjList(actualObjList);

			List<Object[]> projectedObjList = null;

			if (quarter.isEmpty()) {
				projectedObjList = projectedRepository.findProjectedRevenue(
						financialYear, quarter, displayGeography, geography,
						iou, custName, serviceLine);
			} else {
				projectedObjList = projectedRepository
						.findProjectedRevenueByQuarter(financialYear, quarter,
								displayGeography, geography, iou, custName,
								serviceLine);
			}
			logger.info("Projected Revenue has " + projectedObjList.size()
					+ " values");

			mergeProjectedRevenue(quarterMap, projectedObjList);

			List<TargetVsActualResponse> actualProjectedList = convertMaptoTargetvsActualResponse(
					quarterMap, currency);

			// service line does not have target revenue
			if (serviceLine.equals("")) {

				List<Object[]> targetRevenueList = null;
				targetRevenueList = beaconDataTRepository.findTargetRevenue(
						financialYear, quarter, displayGeography, geography,
						iou, custName);

				logger.debug("Target Revenue has " + targetRevenueList.size()
						+ " values");

				List<TargetVsActualResponse> targetList = new ArrayList<TargetVsActualResponse>();

				populateResponseList(targetRevenueList, targetList, true,
						currency);

				List<TargetVsActualResponse> tarActResponseList = mergeLists(
						targetList, actualProjectedList);
				if (tarActResponseList.isEmpty()) {
					logger.error("NOT_FOUND: No Relevent Data Found in the database");
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"No Relevent Data Found in the database");
				}
				return tarActResponseList;
			} else {
				if (actualProjectedList.isEmpty()) {
					logger.error("NOT_FOUND: No Relevent Data Found in the database");
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"No Relevent Data Found in the database");
				}
				return actualProjectedList;
			}
		} else {
			if (financialYear.equals("")&&quarter.isEmpty()) {
				logger.debug("Financial Year is Empty");
				financialYear = DateUtils.getCurrentFinancialYear();
			}
			Date fromDate = DateUtils.getDate("", quarter, financialYear, true);
			Date toDate = DateUtils.getDate("", quarter, financialYear, false);
			// TODO:
			List<Object[]> digitalDealValueList = opportunityRepository
					.getDigitalDealValueBySalesStage(fromDate, toDate,
							displayGeography, geography, serviceLine, iou,
							custName, currency);
			List<Object[]> digitalDealValueByTimeLineList = new ArrayList<Object[]>();
			logger.debug("Digital Deal Value has "
					+ digitalDealValueList.size() + " values");
			if (!quarter.isEmpty()) {
				logger.debug("financial year is empty");
				for (Object[] quarterDigitalDealValue : digitalDealValueList) {
					if (quarterDigitalDealValue[0] != null) {
						Object[] quarterArray = new Object[2];
						quarterArray[0] = DateUtils
								.getFormattedMonth((Date) quarterDigitalDealValue[0]);
						quarterArray[1] = quarterDigitalDealValue[1];
						digitalDealValueByTimeLineList.add(quarterArray);
					}
				}
			} else {
				logger.debug("financial year is empty");
				for (Object[] quarterDigitalDealValue : digitalDealValueList) {
					if (quarterDigitalDealValue[0] != null) {
						Object[] quarterArray = new Object[2];
						quarterArray[0] = DateUtils
								.getQuarterForMonth(DateUtils
										.getFormattedMonth((Date) quarterDigitalDealValue[0]));
						quarterArray[1] = quarterDigitalDealValue[1];
						digitalDealValueByTimeLineList.add(quarterArray);
					}
				}
			}
			Map<String, BigDecimal> quarterMap = getSumUpInMap(digitalDealValueByTimeLineList);

			List<TargetVsActualResponse> targetList = new ArrayList<TargetVsActualResponse>();

			populateResponseList(quarterMap, targetList);
			return targetList;
		}
	}

	private Map<String, BigDecimal> getSumUpInMap(
			List<Object[]> digitalDealValueByTimeLineList) {

		Map<String, BigDecimal> map = new TreeMap<String, BigDecimal>();
		for (int i = 0; i < digitalDealValueByTimeLineList.size(); i++) {
			Object[] obj = digitalDealValueByTimeLineList.get(i);
			logger.debug("In item " + i + "  Obj [0] : " + obj[0]
					+ " Obj [1] : " + obj[1]);
			if (obj[0] != null) {
				String dispName = (String) obj[0];
				BigDecimal rev = null;
				if (obj[1] != null) {
					rev = new BigDecimal(obj[1].toString());
					if (dispName != null) {
						if (map.containsKey(dispName)) {
							rev.add(map.get(dispName));
						}
						map.put(dispName, rev);
					}
				}
			}

		}
		return map;

	}

	private void populateResponseList(Map<String, BigDecimal> quarterMap,
			List<TargetVsActualResponse> targetList) {
		for (String quarterKeyset : quarterMap.keySet()) {
			TargetVsActualResponse targetVsActual = new TargetVsActualResponse();
			targetVsActual.setSubTimeLine(quarterKeyset);
			targetVsActual.setDigitalDealValue(quarterMap.get(quarterKeyset));
			targetList.add(targetVsActual);
		}

	}

	private List<TargetVsActualResponse> convertMaptoTargetvsActualResponse(
			Map<String, BigDecimal> quarterMap, String targetCurrency)
			throws Exception {
		List<TargetVsActualResponse> revenueList = new ArrayList<TargetVsActualResponse>();
		for (Map.Entry<String, BigDecimal> entry : quarterMap.entrySet()) {
			String dispName = entry.getKey();
			BigDecimal revenue = entry.getValue();
			BigDecimal revenueCurrency = beaconService.convert(
					BeaconConverterService.ACTUALS_REVENUE_CURRENCY,
					targetCurrency, revenue);
			TargetVsActualResponse resp = new TargetVsActualResponse();
			resp.setSubTimeLine(dispName);
			resp.setActual(revenueCurrency);
			revenueList.add(resp);
		}
		return revenueList;
	}

	private void populateResponseList(List<Object[]> objList,
			List<TargetVsActualResponse> respList, boolean isTarget,
			String targetCurrency) throws Exception {
		if (objList != null && !objList.isEmpty()) {
			for (Object[] objArr : objList) {
				TargetVsActualResponse resp = new TargetVsActualResponse();
				resp.setSubTimeLine((String) objArr[0]);
				if (isTarget) {
					BigDecimal target = (BigDecimal) objArr[1];
					BigDecimal revenueCurrency = beaconService.convert(
							BeaconConverterService.TARGET_REVENUE_CURRENCY,
							targetCurrency, target);
					resp.setTarget(revenueCurrency);
				} else {
					BigDecimal actual = (BigDecimal) objArr[1];
					BigDecimal revenueCurrency = beaconService.convert(
							BeaconConverterService.ACTUALS_REVENUE_CURRENCY,
							targetCurrency, actual);
					resp.setActual(revenueCurrency);
				}
				respList.add(resp);
			}
		}
	}

	private static List<TargetVsActualResponse> mergeLists(
			List<TargetVsActualResponse> targetList,
			List<TargetVsActualResponse> actualList) throws Exception {
		Map<String, BigDecimal[]> map = getMapFromLists(targetList, actualList);
		List<TargetVsActualResponse> respList = getMergedListFromMap(map);
		return respList;
	}

	private static List<TargetVsActualResponse> getMergedListFromMap(
			Map<String, BigDecimal[]> map) throws Exception {
		List<TargetVsActualResponse> respList = new ArrayList<TargetVsActualResponse>();
		for (Map.Entry<String, BigDecimal[]> entry : map.entrySet()) {
			String quarter = entry.getKey();
			BigDecimal[] valuesList = entry.getValue();
			BigDecimal target = valuesList[0];
			BigDecimal actual = valuesList[1];
			TargetVsActualResponse resp = new TargetVsActualResponse();
			resp.setSubTimeLine(quarter);
			resp.setActual(actual);
			resp.setTarget(target);
			respList.add(resp);
		}
		return respList;
	}

	private static Map<String, BigDecimal[]> getMapFromLists(
			List<TargetVsActualResponse> targetList,
			List<TargetVsActualResponse> actualList) throws Exception {
		Map<String, BigDecimal[]> map = new TreeMap<String, BigDecimal[]>();

		// Populate Target Revenue
		for (TargetVsActualResponse obj : targetList) {
			BigDecimal[] values = new BigDecimal[2];
			String quarter = obj.getSubTimeLine();
			BigDecimal target = obj.getTarget();
			values[0] = target;
			values[1] = ZERO_REVENUE;
			map.put(quarter, values);
		}

		// Populate Actual Revenue
		for (TargetVsActualResponse obj : actualList) {
			BigDecimal[] values1 = new BigDecimal[2];
			String quarter = obj.getSubTimeLine();
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

		List<Object[]> iouObjList = perfRepo.getRevenuesByIOU(financialYear,
				quarter, geography, serviceLine);

		// initializing the map with actuals data
		Map<String, BigDecimal> iouMap = getMapFromObjList(iouObjList);

		List<Object[]> iouProjObjList = projectedRepository.getRevenuesByIOU(
				financialYear, quarter, geography, serviceLine);

		// adding projected revenue
		mergeProjectedRevenue(iouMap, iouProjObjList);

		List<IOUReport> iouRevenuesList = convertMaptoIOUList(iouMap, currency);

		if (iouRevenuesList.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Data Found");
		return iouRevenuesList;
	}

	private List<IOUReport> convertMaptoIOUList(Map<String, BigDecimal> iouMap,
			String targetCurrency) throws Exception {
		List<IOUReport> iouList = new ArrayList<IOUReport>();

		for (Map.Entry<String, BigDecimal> entry : iouMap.entrySet()) {
			String iouName = entry.getKey();
			BigDecimal revenue = entry.getValue();
			BigDecimal revenueCurrency = beaconService.convert(
					BeaconConverterService.ACTUALS_REVENUE_CURRENCY,
					targetCurrency, revenue);
			IOUReport iou = new IOUReport();
			iou.setDisplayIOU(iouName);
			iou.setActualRevenue(revenueCurrency);
			iouList.add(iou);
		}
		return iouList;
	}

	private void mergeProjectedRevenue(Map<String, BigDecimal> map,
			List<Object[]> projObjList) throws Exception {
		if (map == null)
			map = new TreeMap<String, BigDecimal>();
		for (Object[] obj : projObjList) {
			String dispName = (String) obj[0];
			if (obj[1] != null && dispName != null) {
				BigDecimal projRev = new BigDecimal(obj[1].toString());
				if (map.containsKey(dispName)) {
					BigDecimal actual = map.get(dispName);
					map.put(dispName, actual.add(projRev));
				} else {
					// if subsp/iou/geography/quarter does not have actuals data
					map.put(dispName, projRev);
				}
			}
		}
	}

	private Map<String, BigDecimal> getMapFromObjList(List<Object[]> objList)
			throws Exception {
		Map<String, BigDecimal> map = new TreeMap<String, BigDecimal>();
		for (Object[] obj : objList) {
			String dispName = (String) obj[0];
			BigDecimal rev = null;
			if (obj[1] != null)
				rev = new BigDecimal(obj[1].toString());
			if (dispName != null)
				map.put(dispName, rev);
		}
		return map;
	}

	public List<SubSpReport> getRevenuesBySubSp(String financialYear,
			String quarter, String displayGeography, String geography,
			String customerName, String iou, String currency,
			String groupCustomer) throws Exception {

		List<String> custName = new ArrayList<String>();
		if (customerName.length() == 0 && groupCustomer.length() > 0) {
			custName = customerRepository
					.findByGroupCustomerName(groupCustomer);
			if (custName.isEmpty()) {
				logger.error("NOT_FOUND: Invalid Group Customer");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Invalid Group Customer");
			}
		} else {
			custName.add(customerName);
		}
		List<Object[]> subObjList = perfRepo.getRevenuesBySubSp(financialYear,
				quarter, displayGeography, geography, custName, iou);

		// initializing the map with actuals data
		Map<String, BigDecimal> subSpMap = getMapFromObjList(subObjList);

		List<Object[]> subProjObjList = projectedRepository.getRevenuesBySubSp(
				financialYear, quarter, displayGeography, geography, custName,
				iou);

		// adding projected revenue
		mergeProjectedRevenue(subSpMap, subProjObjList);

		List<SubSpReport> subSpRevenuesList = convertMaptoSubSpList(subSpMap,
				currency);
		if (subSpRevenuesList.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Data Found");

		return subSpRevenuesList;
	}

	private List<SubSpReport> convertMaptoSubSpList(
			Map<String, BigDecimal> subSpMap, String targetCurrency)
			throws Exception {
		List<SubSpReport> subSpList = new ArrayList<SubSpReport>();

		for (Map.Entry<String, BigDecimal> entry : subSpMap.entrySet()) {
			String dispName = entry.getKey();
			BigDecimal revenue = entry.getValue();
			BigDecimal revenueTargetCurrency = beaconService.convert(
					BeaconConverterService.ACTUALS_REVENUE_CURRENCY,
					targetCurrency, revenue);
			SubSpReport subSp = new SubSpReport();
			subSp.setDisplaySubSp(dispName);
			subSp.setActualRevenue(revenueTargetCurrency);
			subSpList.add(subSp);
		}
		return subSpList;
	}

	public List<GeographyReport> getRevenuesByDispGeography(
			String financialYear, String quarter, String customerName,
			String subSp, String iou, String currency, String groupCustomer)
			throws Exception {

		List<String> custName = new ArrayList<String>();
		if (customerName.length() == 0 && groupCustomer.length() > 0) {
			custName = customerRepository
					.findByGroupCustomerName(groupCustomer);
			if (custName.isEmpty()) {
				logger.error("NOT_FOUND: Invalid Group Customer");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Invalid Group Customer");
			}
		} else {
			custName.add(customerName);
		}
		List<Object[]> geoObjList = perfRepo.getRevenuesByDispGeo(
				financialYear, quarter, custName, subSp, iou);

		// initializing the map with actuals data
		Map<String, BigDecimal> dispGeoMap = getMapFromObjList(geoObjList);

		List<Object[]> geoProjObjList = projectedRepository
				.getRevenuesByDispGeo(financialYear, quarter, custName, subSp,
						iou);

		// adding projected revenue
		mergeProjectedRevenue(dispGeoMap, geoProjObjList);

		List<GeographyReport> geoRevenuesList = convertMaptoGeographyList(
				dispGeoMap, currency);

		if (geoRevenuesList.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Data Found");

		return geoRevenuesList;
	}

	private List<GeographyReport> convertMaptoGeographyList(
			Map<String, BigDecimal> dispGeoMap, String targetCurrency)
			throws Exception {
		List<GeographyReport> geoList = new ArrayList<GeographyReport>();

		for (Map.Entry<String, BigDecimal> entry : dispGeoMap.entrySet()) {
			String dispName = entry.getKey();
			BigDecimal revenue = entry.getValue();
			BigDecimal revenueTargetCurrency = beaconService.convert(
					BeaconConverterService.ACTUALS_REVENUE_CURRENCY,
					targetCurrency, revenue);
			GeographyReport geo = new GeographyReport();
			geo.setGeography(dispName);
			geo.setActualRevenue(revenueTargetCurrency);
			geoList.add(geo);
		}
		return geoList;
	}

	public List<GeographyReport> getRevenuesBySubGeography(
			String financialYear, String quarter, String customerName,
			String subSp, String iou, String displayGeography,
			String geography, String currency, String groupCustomer)
			throws Exception {

		List<Object[]> geoObjList = null;
		List<Object[]> geoProjObjList = null;

		List<String> custName = new ArrayList<String>();
		if (customerName.length() == 0 && groupCustomer.length() > 0) {
			custName = customerRepository
					.findByGroupCustomerName(groupCustomer);
			if (custName.isEmpty()) {
				logger.error("NOT_FOUND: Invalid Group Customer");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Invalid Group Customer");
			}
		} else {
			custName.add(customerName);
		}
		if (geography.isEmpty()) {
			geoObjList = perfRepo.getRevenuesBySubGeo(financialYear, quarter,
					custName, subSp, iou, displayGeography);
			geoProjObjList = projectedRepository.getRevenuesBySubGeo(
					financialYear, quarter, custName, subSp, iou,
					displayGeography);
		} else {
			geoObjList = perfRepo.getRevenuesByCountry(financialYear, quarter,
					custName, subSp, iou, geography);
			geoProjObjList = projectedRepository.getRevenuesByCountry(
					financialYear, quarter, custName, subSp, iou,
					displayGeography);
		}

		// initializing the map with actuals data
		Map<String, BigDecimal> dispGeoMap = getMapFromObjList(geoObjList);

		// adding projected revenue
		mergeProjectedRevenue(dispGeoMap, geoProjObjList);

		List<GeographyReport> geoRevenuesList = convertMaptoGeographyList(
				dispGeoMap, currency);

		if (geoRevenuesList.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Data Found");

		return geoRevenuesList;
	}

	public List<OpportunityT> getTopOpportunities(String currency,
			String geography, int stageFrom, int stageTo, String subSp,
			String iou, Date dateFrom, Date dateTo, int count,
			String customerName, String groupCustomer) throws Exception {
		List<String> custName = new ArrayList<String>();
		if (customerName.length() == 0 && groupCustomer.length() > 0) {
			custName = customerRepository
					.findByGroupCustomerName(groupCustomer);
			if (custName.isEmpty()) {
				logger.error("NOT_FOUND: Invalid Group Customer");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Invalid Group Customer");
			}
		} else {
			custName.add(customerName);
		}
		List<OpportunityT> topOppList = new ArrayList<OpportunityT>();
		topOppList = opportunityRepository.getTopOpportunities(geography,
				subSp, iou, dateFrom, dateTo, stageFrom, stageTo, count,
				custName);
		return topOppList;

	}

	public ReportsOpportunity getOpportunity(String financialYear,
			String quarter, String geography, String iou, String serviceLine,
			String currency, int salesStageFrom, int salesStageTo,
			String customerName, String groupCustomer) throws Exception {
		Date fromDate = getDate(financialYear, quarter, true);
		Date toDate = getDate(financialYear, quarter, false);
		ReportsOpportunity reportsOpportunity = new ReportsOpportunity();
		List<ReportsSalesStage> salesStageList = new ArrayList<ReportsSalesStage>();
		List<String> custName = new ArrayList<String>();
		if (customerName.length() == 0 && groupCustomer.length() > 0) {
			custName = customerRepository
					.findByGroupCustomerName(groupCustomer);
			if (custName.isEmpty()) {
				logger.error("NOT_FOUND: Invalid Group Customer");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Invalid Group Customer");
			}
		} else {
			custName.add(customerName);
		}
//			List<Object[]> pipelineData = opportunityRepository
//					.findPipelinePerformance(geography, iou, serviceLine,
//							currency, custName, fromDate, toDate,
//							salesStageFrom, salesStageTo);
//			if (pipelineData != null) {
//				Object[] pipeline = pipelineData.get(0);
//				if (pipeline[1] != null) {
//					reportsOpportunity.setOverallBidValue(pipeline[1]
//							.toString());
//				}

				List<Object[]> pipeLinesBySalesStage = opportunityRepository
						.findPipelinePerformanceBySalesStage(geography, iou,
								serviceLine, currency, custName, fromDate,
								toDate,salesStageFrom,salesStageTo);
				if (pipeLinesBySalesStage != null) {
					for (Object[] pipeLineBySalesStage : pipeLinesBySalesStage) {
						ReportsSalesStage reportsSalesStage = new ReportsSalesStage();
						if (pipeLineBySalesStage[0] != null) {
							int salesStageCode = Integer
									.parseInt(pipeLineBySalesStage[0]
											.toString());
							reportsSalesStage.setSalesStageCode(salesStageCode
									+ "");
							SalesStageMappingT salesStageMapping = salesStageMappingRepository
									.findBySalesStageCode(salesStageCode);
							if (salesStageMapping != null) {
								reportsSalesStage
										.setSalesStageCodeDescription(salesStageMapping
												.getSalesStageDescription());
							}
							if (pipeLineBySalesStage[1] != null) {
								reportsSalesStage
										.setBidCount(pipeLineBySalesStage[1]
												.toString());
							}
							if (pipeLineBySalesStage[2] != null) {
								reportsSalesStage
										.setDigitalDealValue(pipeLineBySalesStage[2]
												.toString());
							}

						}
						salesStageList.add(reportsSalesStage);
					}
				}

		reportsOpportunity.setSalesStageList(salesStageList);
		return reportsOpportunity;
	}

	private Date getDate(String financialYear, String quarter,
			boolean isFromDate) throws Exception {
		Date date = new Date();
		if (financialYear.equals("")) {
			if (quarter.equals("")) {
				financialYear = DateUtils.getCurrentFinancialYear();
				date = DateUtils.getDateFromFinancialYear(financialYear,
						isFromDate);
			} else {
				date = DateUtils.getDateFromQuarter(quarter, isFromDate);
			}
		} else {
			if (quarter.equals("")) {
				date = DateUtils.getDateFromFinancialYear(financialYear,
						isFromDate);
			} else {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Cannot have both Financial year and Quarter as input parameters.");
			}
		}
		return date;
	}

	public List<IOUReport> getOpportunitiesByIOU(String financialYear,
			String quarter, String geography, String serviceLine,
			String currency, int salesStageFrom, int salesStageTo)
			throws Exception {
		Date fromDate = getDate(financialYear, quarter, true);
		Date toDate = getDate(financialYear, quarter, false);
		List<IOUReport> iouReports = new ArrayList<IOUReport>();
		List<Object[]> opportunitiesByIOU = null;
		opportunitiesByIOU = opportunityRepository
				.findPipelinePerformanceByIOU(geography, serviceLine, currency,
						fromDate, toDate, salesStageFrom, salesStageTo);
		if (opportunitiesByIOU != null) {
			for (Object[] opportunityByIOU : opportunitiesByIOU) {
				IOUReport iouReport = new IOUReport();
				if (opportunityByIOU[0] != null) {
					iouReport.setDisplayIOU(opportunityByIOU[0].toString());
				}
				if (opportunityByIOU[1] != null) {
					iouReport.setDigitalDealValue(new BigDecimal(
							opportunityByIOU[1].toString()));
				}

				iouReports.add(iouReport);
			}

		}
		if (iouReports.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Data Found");

		return iouReports;
	}

	public List<SubSpReport> getOpportunitiesBySubSp(String financialYear,
			String quarter, String displayGeography, String geography,
			String iou, String currency, int salesStageFrom, int salesStageTo)
			throws Exception {
		if (!quarter.isEmpty())
			financialYear = "";
		Date fromDate = getDate(financialYear, quarter, true);
		Date toDate = getDate(financialYear, quarter, false);
		List<SubSpReport> subSpReports = new ArrayList<SubSpReport>();
		List<Object[]> opportunitiesBySubSpReports = null;

		opportunitiesBySubSpReports = opportunityRepository
				.findPipelinePerformanceByServiceLine(displayGeography,
						geography, iou, currency, salesStageFrom, salesStageTo);

		if (opportunitiesBySubSpReports != null) {
			for (Object[] opportunityBySubSp : opportunitiesBySubSpReports) {
				SubSpReport subSpReport = new SubSpReport();
				if (opportunityBySubSp[0] != null) {
					subSpReport.setDisplaySubSp(opportunityBySubSp[0]
							.toString());
				}
				if (opportunityBySubSp[1] != null) {
					subSpReport.setDigitalDealValue(new BigDecimal(
							opportunityBySubSp[1].toString()));
				}

				subSpReports.add(subSpReport);
			}
		}
		if (subSpReports.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Data Found");

		return subSpReports;
	}

	public List<GeographyReport> getOpportunitiesByDispGeography(
			String financialYear, String quarter, String subSp, String iou,
			String currency, int salesStageFrom, int salesStageTo)
			throws Exception {
		Date fromDate = getDate(financialYear, quarter, true);
		Date toDate = getDate(financialYear, quarter, false);
		List<GeographyReport> geographyReports = new ArrayList<GeographyReport>();
		List<Object[]> opportunitiesByGeographyReports = null;
		opportunitiesByGeographyReports = opportunityRepository
				.findPipelinePerformanceByGeography(subSp, iou, currency,
						fromDate, toDate, salesStageFrom, salesStageTo);
		if (opportunitiesByGeographyReports != null) {
			for (Object[] opportunityByGeography : opportunitiesByGeographyReports) {
				GeographyReport geographyReport = new GeographyReport();
				if (opportunityByGeography[0] != null) {
					geographyReport.setGeography(opportunityByGeography[0]
							.toString());
				}
				if (opportunityByGeography[1] != null) {
					geographyReport.setDigitalDealValue(new BigDecimal(
							opportunityByGeography[1].toString()));
				}
				geographyReports.add(geographyReport);
			}
		}

		if (geographyReports.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Data Found");
		return geographyReports;
	}

	public List<GeographyReport> getOpportunitiesBySubGeography(
			String financialYear, String quarter, String customerName,
			String serviceLine, String iou, String displayGeography,
			String geography, String currency, int salesStageFrom,
			int salesStageTo, String groupCustomer) throws Exception {

		List<String> custName = new ArrayList<String>();
		if (customerName.length() == 0 && groupCustomer.length() > 0) {
			custName = customerRepository
					.findByGroupCustomerName(groupCustomer);
			if (custName.isEmpty()) {
				logger.error("NOT_FOUND: Invalid Group Customer");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Invalid Group Customer");
			}
		} else {
			custName.add(customerName);
		}
		Date fromDate = getDate(financialYear, quarter, true);
		Date toDate = getDate(financialYear, quarter, false);
		List<GeographyReport> geographyReports = new ArrayList<GeographyReport>();
		List<Object[]> opportunitiesByGeographyReports = null;
		if (geography.isEmpty()) {
			opportunitiesByGeographyReports = opportunityRepository
					.findPipelinePerformanceBySubGeography(custName,
							serviceLine, iou, displayGeography, currency,
							fromDate, toDate, salesStageFrom, salesStageTo);
		} else {
			opportunitiesByGeographyReports = opportunityRepository
					.findPipelinePerformanceByCountry(custName, serviceLine,
							iou, geography, currency, fromDate, toDate,
							salesStageFrom, salesStageTo);
		}
		if (opportunitiesByGeographyReports != null) {
			for (Object[] opportunityByGeography : opportunitiesByGeographyReports) {
				GeographyReport geographyReport = new GeographyReport();
				if (opportunityByGeography[0] != null) {
					geographyReport.setGeography(opportunityByGeography[0]
							.toString());
				}
				if (opportunityByGeography[1] != null) {
					geographyReport.setDigitalDealValue(new BigDecimal(
							opportunityByGeography[1].toString()));
				}
				geographyReports.add(geographyReport);
			}
		}

		if (geographyReports.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Data Found");

		return geographyReports;
	}
}