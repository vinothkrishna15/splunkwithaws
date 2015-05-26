package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.BeaconConvertorMappingT;
import com.tcs.destination.bean.GeographyReport;
import com.tcs.destination.bean.IOUReport;
import com.tcs.destination.bean.ReportsOpportunity;
import com.tcs.destination.bean.ReportsSalesStage;
import com.tcs.destination.bean.SalesStageMappingT;
import com.tcs.destination.bean.SubSpReport;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.data.repository.ActualRevenuesDataTRepository;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.data.repository.BeaconDataTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.PerformanceReportRepository;
import com.tcs.destination.data.repository.SalesStageMappingRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.exception.NoSuchCurrencyException;
import com.tcs.destination.utils.DateUtils;

import freemarker.template.utility.DateUtil;

@Component
public class PerformanceReportService {

	private static final Logger logger = LoggerFactory
			.getLogger(PerformanceReportService.class);

	@Autowired
	private PerformanceReportRepository perfRepo;

	private static final BigDecimal ZERO_REVENUE = new BigDecimal("0.0");

	@Autowired
	ActualRevenuesDataTRepository actualsRepository;

	@Autowired
	BeaconDataTRepository beaconDataTRepository;

	@Autowired
	BeaconConvertorRepository beaconRepository;

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	SalesStageMappingRepository salesStageMappingRepository;

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

	public ReportsOpportunity getOpportunity(String financialYear,
			String quarter, String geography, String iou, String serviceLine,
			String currency, boolean pipelines) throws DestinationException {
		Date fromDate = getDate(financialYear, quarter, true);
		Date toDate = getDate(financialYear, quarter, false);
		ReportsOpportunity reportsOpportunity = new ReportsOpportunity();
		List<ReportsSalesStage> salesStageList = new ArrayList<ReportsSalesStage>();
		if (pipelines) {
			List<Object[]> pipelineData = opportunityRepository
					.findPipelinePerformance(geography, iou, serviceLine,
							currency, fromDate, toDate);
			if (pipelineData != null) {
				Object[] pipeline = pipelineData.get(0);
				if (pipeline[1] != null) {
					reportsOpportunity.setOverallBidValue(pipeline[1]
							.toString());
				}

				List<Object[]> pipeLinesBySalesStage = opportunityRepository
						.findPipelinePerformanceBySalesStage(geography, iou,
								serviceLine, currency, fromDate, toDate);
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
										.setOverallBidValue(pipeLineBySalesStage[2]
												.toString());
							}

						}
						salesStageList.add(reportsSalesStage);
					}
				}

			}
		} else {
			// Setting values for Pipeline

			List<Object[]> pipelineData = opportunityRepository
					.findPipelinePerformance(geography, iou, serviceLine,
							currency, fromDate, toDate);
			ReportsSalesStage pipeLineReports = new ReportsSalesStage();
			Object[] pipeline = pipelineData.get(0);
			if (pipeline != null) {
				if (pipeline[0] != null)
					pipeLineReports.setBidCount(pipelineData.get(0)[0]
							.toString());
				if (pipeline[1] != null)
					pipeLineReports.setOverallBidValue(pipelineData.get(0)[1]
							.toString());
			}
			pipeLineReports.setSalesStageCode("04 - 08");
			pipeLineReports.setSalesStageCodeDescription("Pipeline");
			salesStageList.add(pipeLineReports);

			// Setting values for Wins
			List<Object[]> winsList = opportunityRepository
					.findWinsPerformance(geography, iou, serviceLine, currency,
							fromDate, toDate);
			Object[] win = winsList.get(0);
			ReportsSalesStage winReports = new ReportsSalesStage();
			winReports.setSalesStageCode("9");
			winReports.setSalesStageCodeDescription(salesStageMappingRepository
					.findBySalesStageCode(9).getSalesStageDescription());
			if (win != null) {
				if (win[0] != null) {
					winReports.setCount(win[0].toString());
				}
				if (win[1] != null) {
					winReports.setOverallBidValue(win[1].toString());
				}
				if (win[2] != null) {
					winReports.setMean(win[2].toString());
				}
				if (win[3] != null) {
					winReports.setMedian(win[3].toString());
				}
			}
			salesStageList.add(winReports);
		}
		reportsOpportunity.setSalesStageList(salesStageList);
		return reportsOpportunity;
	}

	private Date getDate(String financialYear, String quarter,
			boolean isFromDate) throws DestinationException {
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
			String currency, boolean isPipeline) throws DestinationException {
		Date fromDate = getDate(financialYear, quarter, true);
		Date toDate = getDate(financialYear, quarter, false);
		List<IOUReport> iouReports = new ArrayList<IOUReport>();
		List<Object[]> opportunitiesByIOU = null;
		if (isPipeline) {
			opportunitiesByIOU = opportunityRepository
					.findPipelinePerformanceByIOU(geography, serviceLine,
							currency, fromDate, toDate);
		} else {
			opportunitiesByIOU = opportunityRepository
					.findWinsPerformanceByIOU(geography, serviceLine, currency,
							fromDate, toDate);
		}
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

		return iouReports;
	}

	public List<SubSpReport> getOpportunitiesBySubSp(String financialYear,
			String quarter, String geography, String iou, String currency,
			boolean isPipeline) throws DestinationException {
		Date fromDate = getDate(financialYear, quarter, true);
		Date toDate = getDate(financialYear, quarter, false);
		List<SubSpReport> subSpReports = new ArrayList<SubSpReport>();
		List<Object[]> opportunitiesBySubSpReports = null;
		if (isPipeline) {
			opportunitiesBySubSpReports = opportunityRepository
					.findPipelinePerformanceByServiceLine(geography, iou,
							currency, fromDate, toDate);
		} else {
			opportunitiesBySubSpReports = opportunityRepository
					.findWinsPerformanceByServiceLine(geography, iou, currency,
							fromDate, toDate);
		}
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

		return subSpReports;
	}

	public List<GeographyReport> getOpportunitiesByDispGeography(
			String financialYear, String quarter, String subSp, String iou,
			String currency, boolean isPipeline) throws DestinationException {
		Date fromDate = getDate(financialYear, quarter, true);
		Date toDate = getDate(financialYear, quarter, false);
		List<GeographyReport> geographyReports = new ArrayList<GeographyReport>();
		List<Object[]> opportunitiesByGeographyReports = null;
		if (isPipeline) {
			opportunitiesByGeographyReports = opportunityRepository
					.findPipelinePerformanceByGeography(subSp, iou, currency,
							fromDate, toDate);
		} else {
			opportunitiesByGeographyReports = opportunityRepository
					.findWinsPerformanceByGeography(subSp, iou, currency,
							fromDate, toDate);
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

		return geographyReports;
	}

	public List<GeographyReport> getOpportunitiesBySubGeography(
			String financialYear, String quarter, String customerName,
			String serviceLine, String iou, String geography, String currency,
			boolean isPipeline) throws DestinationException {
		Date fromDate = getDate(financialYear, quarter, true);
		Date toDate = getDate(financialYear, quarter, false);
		List<GeographyReport> geographyReports = new ArrayList<GeographyReport>();
		List<Object[]> opportunitiesByGeographyReports = null;
		if (isPipeline) {
			opportunitiesByGeographyReports = opportunityRepository
					.findPipelinePerformanceBySubGeography( customerName, serviceLine, iou, geography,
							currency,fromDate,toDate);
		} else {
			opportunitiesByGeographyReports = opportunityRepository
					.findWinsPerformanceBySubGeography(customerName, serviceLine, iou, geography,
							currency,fromDate,toDate);
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

		return geographyReports;
	}
}
