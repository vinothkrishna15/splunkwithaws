package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.ConnectSummaryResponse;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.CurrencyValue;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.OpportunityTcsAccountContactLinkT;
import com.tcs.destination.bean.TargetVsActualDetailed;
import com.tcs.destination.bean.TargetVsActualQuarter;
import com.tcs.destination.bean.TargetVsActualYearToDate;
import com.tcs.destination.data.repository.ActualRevenuesDataTRepository;
import com.tcs.destination.data.repository.BeaconDataTRepository;
import com.tcs.destination.data.repository.BidDetailsTRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.ProjectedRevenuesDataTRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DateUtils;

@Component
public class ReportsService {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportsService.class);

	@Autowired
	ActualRevenuesDataTRepository actualRevenuesDataTRepository;

	@Autowired
	BeaconDataTRepository beaconDataTRepository;

	@Autowired
	BidDetailsTRepository bidDetailsTRepository;

	@Autowired
	BeaconConverterService beaconConverterService;

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	ProjectedRevenuesDataTRepository projectedRevenuesDataTRepository;

	@Autowired
	CustomerRepository customerRepository;

	public List<ConnectT> getConnectDetailedReports(String month,
			String quarter, String year, List<String> iou,
			List<String> geography, List<String> country,
			List<String> serviceLines) throws Exception {
		logger.info("Inside getConnectDetailedReports Service");
		addEmptyValues(iou, geography, country, serviceLines);
		Date fromDate = DateUtils.getDate(month, quarter, year, true);
		Date toDate = DateUtils.getDate(month, quarter, year, false);
		List<ConnectT> connectList = connectRepository.findByConnectReport(
				new Timestamp(fromDate.getTime()),
				new Timestamp(toDate.getTime()), iou, geography, country,
				serviceLines);
		if (connectList.isEmpty()) {
			logger.error("NOT_FOUND: No Relevent Data Found in the database");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Relevent Data Found in the database");
		} else {
			return connectList;
		}
	}

	public List<ConnectSummaryResponse> getSummaryReports(String required,
			String month, String quarter, String year, List<String> iou,
			List<String> geography, List<String> country,
			List<String> serviceLines) throws Exception {
		logger.info("Inside searchForReports Service");
		addEmptyValues(iou, geography, country, serviceLines);
		Date fromDate = DateUtils.getDate(month, quarter, year, true);
		Date toDate = DateUtils.getDate(month, quarter, year, false);
		List<ConnectSummaryResponse> connectSummaryResponses = new ArrayList<ConnectSummaryResponse>();
		switch (required) {
		case "subSp":
			connectSummaryResponses = getConnectSummaryReportsBySubSp(fromDate,
					toDate, iou, geography, country, serviceLines);
			break;
		case "geography":
			connectSummaryResponses = getConnectSummaryReportsByGeography(
					fromDate, toDate, iou, geography, country, serviceLines);
			break;
		case "iou":
			connectSummaryResponses = getConnectSummaryReportsByIou(fromDate,
					toDate, iou, geography, country, serviceLines);
			break;
		default:
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid URL");
		}
		return connectSummaryResponses;
	}

	public List<ConnectSummaryResponse> getConnectSummaryReportsBySubSp(
			Date fromDate, Date toDate, List<String> iou,
			List<String> geography, List<String> country,
			List<String> serviceLines) throws Exception {
		logger.info("Inside getConnectSummaryReportsBySubSp Service");
		List<ConnectSummaryResponse> connectSummaryResponses = new ArrayList<ConnectSummaryResponse>();
		List<Object[]> subSpConnectCountList = connectRepository
				.findBySubSpConnectSummaryReport(
						new Timestamp(fromDate.getTime()),
						new Timestamp(toDate.getTime()), iou, geography,
						country, serviceLines);
		if (subSpConnectCountList.isEmpty()) {
			logger.error("NOT_FOUND: No Relevent Data Found in the database");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Relevent Data Found in the database");
		} else {
			for (Object[] subSpConnectCount : subSpConnectCountList) {
				ConnectSummaryResponse connectSummaryResponse = new ConnectSummaryResponse();
				connectSummaryResponse
						.setConnectCount((BigInteger) subSpConnectCount[0]);
				connectSummaryResponse
						.setRowLabel((String) subSpConnectCount[1]);
				connectSummaryResponses.add(connectSummaryResponse);
			}
			return connectSummaryResponses;
		}
	}

	public List<ConnectSummaryResponse> getConnectSummaryReportsByGeography(
			Date fromDate, Date toDate, List<String> iou,
			List<String> geography, List<String> country,
			List<String> serviceLines) throws Exception {
		logger.info("Inside getConnectSummaryReportsByGeography Service");
		List<ConnectSummaryResponse> connectSummaryResponses = new ArrayList<ConnectSummaryResponse>();
		List<Object[]> geographyConnectCountList = connectRepository
				.findByGeographyConnectSummaryReport(
						new Timestamp(fromDate.getTime()),
						new Timestamp(toDate.getTime()), iou, geography,
						country, serviceLines);
		if (geographyConnectCountList.isEmpty()) {
			logger.error("NOT_FOUND: No Relevent Data Found in the database");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Relevent Data Found in the database");
		} else {
			for (Object[] geographyConnectCount : geographyConnectCountList) {
				ConnectSummaryResponse connectSummaryResponse = new ConnectSummaryResponse();
				connectSummaryResponse
						.setConnectCount((BigInteger) geographyConnectCount[0]);
				connectSummaryResponse
						.setRowLabel((String) geographyConnectCount[1]);
				connectSummaryResponses.add(connectSummaryResponse);
			}
			return connectSummaryResponses;
		}
	}

	public List<ConnectSummaryResponse> getConnectSummaryReportsByIou(
			Date fromDate, Date toDate, List<String> iou,
			List<String> geography, List<String> country,
			List<String> serviceLines) throws Exception {
		logger.info("Inside getConnectSummaryReportsByIou Service");
		List<ConnectSummaryResponse> connectSummaryResponses = new ArrayList<ConnectSummaryResponse>();
		List<Object[]> iouConnectCountList = connectRepository
				.findByIouConnectSummaryReport(
						new Timestamp(fromDate.getTime()),
						new Timestamp(toDate.getTime()), iou, geography,
						country, serviceLines);
		if (iouConnectCountList.isEmpty()) {
			logger.error("NOT_FOUND:No Relevent Data Found in the database");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Relevent Data Found in the database");
		} else {
			for (Object[] iouConnectCount : iouConnectCountList) {
				ConnectSummaryResponse connectSummaryResponse = new ConnectSummaryResponse();
				connectSummaryResponse
						.setConnectCount((BigInteger) iouConnectCount[0]);
				connectSummaryResponse.setRowLabel((String) iouConnectCount[1]);
				connectSummaryResponses.add(connectSummaryResponse);
			}
			return connectSummaryResponses;
		}
	}

	public List<BidDetailsT> getBidDetailedReport(String year, Date fromDate,
			Date toDate, List<String> bidOwner, List<String> currency,
			List<String> iou, List<String> geography, List<String> country,
			List<String> serviceLines) throws DestinationException {
		logger.info("Inside getBidDetailedReport Service");
		Date startDate = null;
		Date endDate = null;
		if (!year.equals("")) {
			logger.debug("year is not Empty");
			startDate = DateUtils.getDateFromFinancialYear(year, true);
			endDate = DateUtils.getDateFromFinancialYear(year, false);
		} else {
			startDate = fromDate;
			endDate = toDate;
		}
		if (bidOwner.size() == 0) {
			logger.debug("bidOwner is Empty");
			bidOwner.add("");
		}
		addEmptyValues(iou, geography, country, serviceLines);
		List<BidDetailsT> bidDetailsList = bidDetailsTRepository
				.findByBidDetailsReport(startDate, endDate, bidOwner, iou,
						geography, country, serviceLines);
		logger.info("Bid details has " + bidDetailsList.size() + " values");
		if (bidDetailsList.isEmpty()) {
			logger.error("NOT_FOUND: No Relevent Data Found in the database");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Relevent Data Found in the database");
		} else {
			for (BidDetailsT bidDetail : bidDetailsList) {
				List<OpportunityTcsAccountContactLinkT> opportunityTcsAccountContactLinkTs = bidDetail
						.getOpportunityT()
						.getOpportunityTcsAccountContactLinkTs();
				for (OpportunityTcsAccountContactLinkT opportunityTcsAccountContactLinkT : opportunityTcsAccountContactLinkTs) {
					opportunityTcsAccountContactLinkT.getContactT()
							.setOpportunityTcsAccountContactLinkTs(null);
				}
				OpportunityT opportunityT = beaconConverterService
						.convertOpportunityCurrency(
								bidDetail.getOpportunityT(), currency);
				bidDetail.setOpportunityT(opportunityT);
			}
			return bidDetailsList;
		}
	}

	public void addEmptyValues(List<String> iou, List<String> geography,
			List<String> country, List<String> serviceLines) {
		if (iou.size() == 0) {
			logger.debug("iou is Empty");
			iou.add("");
		}
		if (geography.size() == 0) {
			logger.debug("geography is Empty");
			geography.add("");
		}
		if (country.size() == 0) {
			logger.debug("country is Empty");
			country.add("");
		}
		if (serviceLines.size() == 0) {
			logger.debug("serviceLines is Empty");
			serviceLines.add("");
		}
	}

	public List<TargetVsActualDetailed> getTargetVsActual(
			List<String> geographyList, List<String> iouList,
			List<String> serviceLines, String fromMonth, String toMonth,
			List<String> currencyList) throws Exception {
		Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap = new TreeMap<String, List<TargetVsActualQuarter>>();
		List<TargetVsActualDetailed> targetVsActualDetails = new ArrayList<TargetVsActualDetailed>();
		if (toMonth.isEmpty()) {
			toMonth = DateUtils.getCurrentMonth();
		}
		List<String> formattedMonths = DateUtils.getAllMonthsBetween(fromMonth,
				toMonth);

		if (formattedMonths != null && !formattedMonths.isEmpty()) {
			for (String formattedMonth : formattedMonths) {
				DateUtils.getQuarterForMonth(formattedMonth);
			}
		}

		addEmptyItemToListIfEmpty(iouList);
		addEmptyItemToListIfEmpty(geographyList);
		addEmptyItemToListIfEmpty(serviceLines);
		if (formattedMonths == null || formattedMonths.isEmpty()) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"No Months available for this perticular range");
		}
		createMapForProjected(iouList, geographyList, formattedMonths,
				customerIdQuarterMap);
		mergeMapForActual(iouList, geographyList, formattedMonths,
				customerIdQuarterMap);
		mergeMapForTarget(iouList, geographyList, fromMonth, toMonth,
				customerIdQuarterMap);
		generateReponseFromMap(customerIdQuarterMap, targetVsActualDetails);
		setCurrency(targetVsActualDetails, currencyList);
		return targetVsActualDetails;
	}

	private void setCurrency(
			List<TargetVsActualDetailed> targetVsActualDetails,
			List<String> currencyList) throws DestinationException {
		try {
			for (TargetVsActualDetailed targetVsActualDetailed : targetVsActualDetails) {
				for (String currency : currencyList) {
					for (TargetVsActualYearToDate targetVsActualYearToDate : targetVsActualDetailed
							.getYearToDate()) {
						// Setting Actual Currency conversion
						if (targetVsActualYearToDate.getActual() != null) {
							CurrencyValue currencyValue = new CurrencyValue();
							currencyValue.setCurrency(currency);
							currencyValue.setValue(beaconConverterService
									.convert("INR", currency,
											targetVsActualYearToDate
													.getActual()));
							targetVsActualYearToDate.getActualValues().add(
									currencyValue);
						}
						// Setting Target Currency conversion
						if (targetVsActualYearToDate.getTarget() != null) {
							CurrencyValue currencyValue = new CurrencyValue();
							currencyValue.setCurrency(currency);
							currencyValue.setValue(beaconConverterService
									.convert("INR", currency,
											targetVsActualYearToDate
													.getTarget()));
							targetVsActualYearToDate.getTargetValues().add(
									currencyValue);
						}
						// Setting Projected Currency conversion
						if (targetVsActualYearToDate.getProjected() != null) {
							CurrencyValue currencyValue = new CurrencyValue();
							currencyValue.setCurrency(currency);
							currencyValue.setValue(beaconConverterService
									.convert("INR", currency,
											targetVsActualYearToDate
													.getProjected()));
							targetVsActualYearToDate.getProjectedValues().add(
									currencyValue);
						}
						// Setting the Target Quarter values
						{
							for (TargetVsActualQuarter targetVsActualQuarter : targetVsActualYearToDate
									.getQuarterList()) {
								// Setting Actual Currency conversion
								if (targetVsActualQuarter.getActual() != null) {
									CurrencyValue currencyValue = new CurrencyValue();
									currencyValue.setCurrency(currency);
									currencyValue
											.setValue(beaconConverterService
													.convert(
															"INR",
															currency,
															targetVsActualQuarter
																	.getActual()));
									targetVsActualQuarter.getActualValues()
											.add(currencyValue);
								}
								// Setting Target Currency conversion
								if (targetVsActualQuarter.getTarget() != null) {
									CurrencyValue currencyValue = new CurrencyValue();
									currencyValue.setCurrency(currency);
									currencyValue
											.setValue(beaconConverterService
													.convert(
															"INR",
															currency,
															targetVsActualQuarter
																	.getTarget()));
									targetVsActualQuarter.getTargetValues()
											.add(currencyValue);
								}
								// Setting Projected Currency conversion
								if (targetVsActualQuarter.getProjected() != null) {
									CurrencyValue currencyValue = new CurrencyValue();
									currencyValue.setCurrency(currency);
									currencyValue
											.setValue(beaconConverterService
													.convert(
															"INR",
															currency,
															targetVsActualQuarter
																	.getProjected()));
									targetVsActualQuarter.getProjectedValues()
											.add(currencyValue);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getLocalizedMessage());
		}

	}

	private void mergeMapForTarget(List<String> iouList,
			List<String> geographyList, String fromMonth, String toMonth,
			Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap)
			throws Exception {
		String fromQuarter = DateUtils.getQuarterForMonth(fromMonth);
		String toQuarter = DateUtils.getQuarterForMonth(toMonth);

		List<String> quarterList = DateUtils.getAllQuartersBetween(fromQuarter,
				toQuarter);

		List<Object[]> targetObjList = beaconDataTRepository
				.getTargetByQuarter(iouList, geographyList, quarterList);
		for (Object[] targetObj : targetObjList) {
			String customerName = targetObj[0].toString();
			String quarter = targetObj[1].toString();
			BigDecimal target = (BigDecimal) targetObj[2];

			// target=target*(4-monthIndex)/3 ; monthIndex={1,2,3}
			if (quarter.equals(fromQuarter)) {
				target = target.multiply(
						new BigDecimal(4 - DateUtils
								.getMonthIndexOnQuarter(fromMonth))).divide(
						new BigDecimal(3), 5, RoundingMode.HALF_UP);
			}
			if (quarter.equals(toQuarter)) {
				target = target.multiply(
						new BigDecimal(4 - DateUtils
								.getMonthIndexOnQuarter(toMonth))).divide(
						new BigDecimal(3), 5, RoundingMode.HALF_UP);
			}
			if (customerIdQuarterMap.containsKey(customerName)) {
				List<TargetVsActualQuarter> targetVsActualQuarterList = customerIdQuarterMap
						.get(customerName);
				boolean hasQuarter = false;
				for (TargetVsActualQuarter targetVsActualQuarter : targetVsActualQuarterList) {
					if (targetVsActualQuarter.getQuarter().equals(quarter)) {
						targetVsActualQuarter.setTarget(target);
						hasQuarter = true;
					}
				}
				if (!hasQuarter) {
					TargetVsActualQuarter targetVsActualQuarter = new TargetVsActualQuarter();
					targetVsActualQuarter.setQuarter(quarter);
					targetVsActualQuarter.setTarget(target);
					targetVsActualQuarterList.add(targetVsActualQuarter);
				}
			} else {
				List<TargetVsActualQuarter> targetVsActualQuarterList = new ArrayList<TargetVsActualQuarter>();
				TargetVsActualQuarter targetVsActualQuarter = new TargetVsActualQuarter();
				targetVsActualQuarter.setQuarter(quarter);
				targetVsActualQuarter.setTarget(target);
				targetVsActualQuarterList.add(targetVsActualQuarter);
				customerIdQuarterMap.put(customerName,
						targetVsActualQuarterList);
			}
		}
	}

	private void mergeMapForActual(List<String> iouList,
			List<String> geographyList, List<String> formattedMonths,
			Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap) {
		List<Object[]> actualRevenueObjList = actualRevenuesDataTRepository
				.getActualRevenuesByQuarter(iouList, geographyList,
						formattedMonths);
		for (Object[] actualRevenueObj : actualRevenueObjList) {
			String customerName = actualRevenueObj[0].toString();
			String quarter = actualRevenueObj[1].toString();
			BigDecimal actual = (BigDecimal) actualRevenueObj[2];
			if (customerIdQuarterMap.containsKey(customerName)) {
				List<TargetVsActualQuarter> targetVsActualQuarterList = customerIdQuarterMap
						.get(customerName);
				boolean hasQuarter = false;
				for (TargetVsActualQuarter targetVsActualQuarter : targetVsActualQuarterList) {
					if (targetVsActualQuarter.getQuarter().equals(quarter)) {
						targetVsActualQuarter.setActual(actual);
						hasQuarter = true;
					}
				}
				if (!hasQuarter) {
					TargetVsActualQuarter targetVsActualQuarter = new TargetVsActualQuarter();
					targetVsActualQuarter.setQuarter(quarter);
					targetVsActualQuarter.setActual(actual);
					targetVsActualQuarterList.add(targetVsActualQuarter);
				}
			} else {
				List<TargetVsActualQuarter> targetVsActualQuarterList = new ArrayList<TargetVsActualQuarter>();
				TargetVsActualQuarter targetVsActualQuarter = new TargetVsActualQuarter();
				targetVsActualQuarter.setQuarter(quarter);
				targetVsActualQuarter.setActual(actual);
				targetVsActualQuarterList.add(targetVsActualQuarter);
				customerIdQuarterMap.put(customerName,
						targetVsActualQuarterList);
			}
		}
	}

	private void generateReponseFromMap(
			Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap,
			List<TargetVsActualDetailed> targetVsActualDetails) {
		for (String customerName : customerIdQuarterMap.keySet()) {
			TargetVsActualDetailed targetVsActualDetailed = new TargetVsActualDetailed();
			List<TargetVsActualYearToDate> targetVsActualYtds = getTargetVsActualYtdList(customerIdQuarterMap
					.get(customerName));
			CustomerMasterT customerMasterT = customerRepository
					.findByCustomerName(customerName);
			targetVsActualDetailed.setCustomerMasterT(customerMasterT);
			targetVsActualDetailed.setYearToDate(targetVsActualYtds);
			targetVsActualDetails.add(targetVsActualDetailed);
		}
	}

	private void createMapForProjected(List<String> iouList,
			List<String> geographyList, List<String> formattedMonths,
			Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap) {
		List<Object[]> projectedQuarterObjList = projectedRevenuesDataTRepository
				.getProjectedRevenuesByQuarter(iouList, geographyList,
						formattedMonths);
		if (projectedQuarterObjList != null) {
			for (Object[] projectedQuarter : projectedQuarterObjList) {
				TargetVsActualQuarter targetVsActualQuarter = new TargetVsActualQuarter();
				String customerName = projectedQuarter[0].toString();
				targetVsActualQuarter
						.setQuarter(projectedQuarter[1].toString());
				targetVsActualQuarter
						.setProjected((BigDecimal) projectedQuarter[2]);
				customerIdQuarterMap = createMapforQuarters(
						customerIdQuarterMap, targetVsActualQuarter,
						customerName);
			}
		}

	}

	private void addEmptyItemToListIfEmpty(List<String> itemList) {
		if (itemList == null || itemList.isEmpty())
			itemList.add("");

	}

	private List<TargetVsActualYearToDate> getTargetVsActualYtdList(
			List<TargetVsActualQuarter> targetVsActualQuarterList) {

		List<TargetVsActualYearToDate> targetVsActualYearToDates = new ArrayList<TargetVsActualYearToDate>();
		Map<String, List<TargetVsActualQuarter>> targetVsActualYtdMap = new HashMap<String, List<TargetVsActualQuarter>>();
		for (TargetVsActualQuarter targetVsActualQuarter : targetVsActualQuarterList) {
			String financialYear = DateUtils
					.getFinancialYearForQuarter(targetVsActualQuarter
							.getQuarter());
			targetVsActualYtdMap = createMapforQuarters(targetVsActualYtdMap,
					targetVsActualQuarter, financialYear);
		}
		for (String financialYear : targetVsActualYtdMap.keySet()) {
			TargetVsActualYearToDate targetVsActualYtd = new TargetVsActualYearToDate();
			targetVsActualYtd.setFinancialYear(financialYear);
			targetVsActualYtd.setQuarterList(targetVsActualYtdMap
					.get(financialYear));
			targetVsActualYearToDates.add(targetVsActualYtd);
		}

		return targetVsActualYearToDates;
	}

	private Map<String, List<TargetVsActualQuarter>> createMapforQuarters(
			Map<String, List<TargetVsActualQuarter>> quarterList,
			TargetVsActualQuarter targetVsActualQuarter, String key) {
		if (quarterList.containsKey(key)) {
			quarterList.get(key).add(targetVsActualQuarter);
		} else {
			List<TargetVsActualQuarter> targetVsActualQuarterMapList = new ArrayList<TargetVsActualQuarter>();
			targetVsActualQuarterMapList.add(targetVsActualQuarter);
			quarterList.put(key, targetVsActualQuarterMapList);
		}
		return quarterList;
	}

}
