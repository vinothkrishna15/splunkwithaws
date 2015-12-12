package com.tcs.destination.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.FrequentlySearchedGroupCustomersT;
import com.tcs.destination.bean.GeographyReport;
import com.tcs.destination.bean.IOUReport;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.ReportsOpportunity;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.SubSpReport;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.PerformanceReportService;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;
/**
 * 
 * This controller handles preformance report related requests
 *
 */
@RestController
@RequestMapping("/perfreport")
public class PerformanceReportController {

	private static final Logger logger = LoggerFactory
			.getLogger(PerformanceReportController.class);

	@Autowired
	PerformanceReportService perfService;

	/**
	 * This method is used to retrieve target vs actual summary
	 * @param financialYear
	 * @param quarter
	 * @param displayGeography
	 * @param geography
	 * @param serviceLine
	 * @param iou
	 * @param wins
	 * @param customerName
	 * @param groupCustomer
	 * @param currency
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/revenue", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getActualRevenue(
			@RequestParam(value = "year", defaultValue = "", required = false) String financialYear,
			@RequestParam(value = "quarter", defaultValue = "", required = false) String quarter,
			@RequestParam(value = "displayGeography", defaultValue = "", required = false) String displayGeography,
			@RequestParam(value = "geography", defaultValue = "", required = false) String geography,
			@RequestParam(value = "serviceline", defaultValue = "", required = false) String serviceLine,
			@RequestParam(value = "iou", defaultValue = "", required = false) String iou,
			@RequestParam(value = "wins", defaultValue = "false") boolean wins,
			@RequestParam(value = "customer", defaultValue = "", required = false) String customerName,
			@RequestParam(value = "groupCustomer", defaultValue = "", required = false) String groupCustomer,
			@RequestParam(value = "currency", defaultValue = "INR", required = false) String currency,
			@RequestParam(value = "fields", defaultValue = "all", required = false) String fields,
			@RequestParam(value = "view", defaultValue = "", required = false) String view)
			throws DestinationException {
		logger.info("Inside PerformanceReportController: Start of /perfreport/revenue GET");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		List<TargetVsActualResponse> response;
		try {
			response = perfService.getTargetVsActualRevenueSummary(
					financialYear, quarter, displayGeography, geography,
					serviceLine, iou, customerName, currency, groupCustomer,
					wins, userId, true);
			logger.info("Inside PerformanceReportController: End of /perfreport/revenue GET");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews(fields,
							view, response), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the target vs actual summary");
		}
	}

	/**
	 * This method is used to revenues and opportunities by IOU
	 * @param financialYear
	 * @param quarter
	 * @param displayGeography
	 * @param geography
	 * @param serviceLine
	 * @param salesStageFrom
	 * @param salesStageTo
	 * @param currency
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/iou")
	public @ResponseBody String getIOU(
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "displayGeography", defaultValue = "") String displayGeography,
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "serviceLine", defaultValue = "") String serviceLine,
			@RequestParam(value = "stagefrom", defaultValue = "-1") int salesStageFrom,
			@RequestParam(value = "stageto", defaultValue = "-1") int salesStageTo,
			@RequestParam(value = "currency", defaultValue = "INR") String currency,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside PerformanceReportController: Start of /perfreport/iou GET");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (financialYear.isEmpty()) {
			financialYear = DateUtils.getCurrentFinancialYear();
		}
		List<IOUReport> iouList = null;
		try {
			if (salesStageFrom != salesStageTo || salesStageFrom != -1) {
				iouList = perfService.getOpportunitiesByIOU(financialYear,
						quarter, displayGeography, geography, serviceLine,
						currency, salesStageFrom, salesStageTo, userId);

			} else {
				iouList = perfService.getRevenuesByIOU(financialYear, quarter,
						displayGeography, geography, serviceLine, currency,
						userId);
			}
			logger.info("Inside PerformanceReportController: End of /perfreport/iou GET");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, iouList);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving revenues and opportunities bu IOU");
		}
	}

	/**
	 * This method is used to retrieve the revenues and opportunities by subsp
	 * @param financialYear
	 * @param quarter
	 * @param displayGeography
	 * @param geography
	 * @param iou
	 * @param customerName
	 * @param groupCustomer
	 * @param currency
	 * @param salesStageFrom
	 * @param salesStageTo
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/subsp")
	public @ResponseBody String getSubSp(
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "displayGeography", defaultValue = "", required = false) String displayGeography,
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "iou", defaultValue = "") String iou,
			@RequestParam(value = "customer", defaultValue = "") String customerName,
			@RequestParam(value = "groupCustomer", defaultValue = "") String groupCustomer,
			@RequestParam(value = "currency", defaultValue = "INR") String currency,
			@RequestParam(value = "stagefrom", defaultValue = "-1") int salesStageFrom,
			@RequestParam(value = "stageto", defaultValue = "-1") int salesStageTo,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside PerformanceReportController: Start of /perfreport/subsp GET");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (financialYear.isEmpty()) {
			financialYear = DateUtils.getCurrentFinancialYear();
		}
		List<SubSpReport> subSpList = null;
		try {
			if (salesStageFrom != salesStageTo || salesStageFrom != -1) {
				subSpList = perfService.getOpportunitiesBySubSp(financialYear,
						quarter, displayGeography, geography, iou, currency,
						salesStageFrom, salesStageTo, userId);

			} else {
				subSpList = perfService.getRevenuesBySubSp(financialYear,
						quarter, displayGeography, geography, customerName,
						iou, currency, groupCustomer, userId);
			}
			logger.info("Inside PerformanceReportController: End of /perfreport/subsp GET");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, subSpList);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the revenues and opportunities by subsp");
		}
	}

	/**
	 * This method is used to retrieve the revenues and opportunities by geography
	 * @param financialYear
	 * @param quarter
	 * @param displayGeography
	 * @param geography
	 * @param iou
	 * @param salesStageFrom
	 * @param salesStageTo
	 * @param serviceLine
	 * @param customerName
	 * @param groupCustomer
	 * @param currency
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/geo")
	public @ResponseBody String getGeo(
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "displayGeography", defaultValue = "") String displayGeography,
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "iou", defaultValue = "") String iou,
			@RequestParam(value = "stagefrom", defaultValue = "-1") int salesStageFrom,
			@RequestParam(value = "stageto", defaultValue = "-1") int salesStageTo,
			@RequestParam(value = "serviceline", defaultValue = "") String serviceLine,
			@RequestParam(value = "customer", defaultValue = "") String customerName,
			@RequestParam(value = "groupCustomer", defaultValue = "") String groupCustomer,
			@RequestParam(value = "currency", defaultValue = "INR") String currency,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside PerformanceReportController: Start of /perfreport/geo GET");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		List<GeographyReport> geoList = null;
		try {
			if (displayGeography.equals("") && geography.isEmpty()) {

				if (salesStageFrom != salesStageTo || salesStageFrom != -1) {
					if (financialYear.isEmpty() && quarter.isEmpty()) {
						financialYear = DateUtils.getCurrentFinancialYear();
					}
					geoList = perfService.getOpportunitiesByDispGeography(
							financialYear, quarter, serviceLine, iou, currency,
							salesStageFrom, salesStageTo, userId);
				} else {
					if (financialYear.isEmpty()) {
						financialYear = DateUtils.getCurrentFinancialYear();
					}
					geoList = perfService.getRevenuesByDispGeography(
							financialYear, quarter, customerName, serviceLine,
							iou, currency, groupCustomer, userId);
				}

			} else {
				if (financialYear.isEmpty() && quarter.isEmpty()) {
					financialYear = DateUtils.getCurrentFinancialYear();
				}
				if (salesStageFrom != salesStageTo || salesStageFrom != -1) {
					geoList = perfService
							.getOpportunitiesBySubGeography(financialYear,
									quarter, customerName, serviceLine, iou,
									displayGeography, geography, currency,
									salesStageFrom, salesStageTo,
									groupCustomer, userId);
				} else {
					geoList = perfService.getRevenuesBySubGeography(
							financialYear, quarter, customerName, serviceLine,
							iou, displayGeography, geography, currency,
							groupCustomer, userId);
				}
			}
			logger.info("Inside PerformanceReportController: End of /perfreport/geo GET");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, geoList);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the revenues and opportunities by geography");
		}
	}

	/**
	 * This method is used to retrieve the opportunity reports.
	 * @param financialYear
	 * @param quarter
	 * @param displayGeography
	 * @param geography
	 * @param iou
	 * @param serviceLine
	 * @param currency
	 * @param salesStageFrom
	 * @param salesStageTo
	 * @param customerName
	 * @param groupCustomer
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/opportunity")
	public @ResponseBody String getOpportunities(
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "displayGeography", defaultValue = "") String displayGeography,
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "iou", defaultValue = "") String iou,
			@RequestParam(value = "serviceline", defaultValue = "") String serviceLine,
			@RequestParam(value = "currency", defaultValue = "INR") String currency,
			@RequestParam(value = "stagefrom") int salesStageFrom,
			@RequestParam(value = "stageto") int salesStageTo,
			@RequestParam(value = "customer", defaultValue = "") String customerName,
			@RequestParam(value = "groupCustomer", defaultValue = "") String groupCustomer,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside PerformanceReportController: Start of /perfreport/opportunity GET");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		ReportsOpportunity reportsOpportunity;
		try {
			reportsOpportunity = perfService.getOpportunity(financialYear,
					quarter, displayGeography, geography, iou, serviceLine,
					currency, salesStageFrom, salesStageTo, customerName,
					groupCustomer, userId);
			logger.info("Inside PerformanceReportController: End of /perfreport/opportunity GET");
			return ResponseConstructors.filterJsonForFieldAndViews("all", "",
					reportsOpportunity);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the reports opportunity");
		}
	}

	/**
	 * This method is used to retrieve the top opportunities
	 * @param financialYear
	 * @param quarter
	 * @param displayGeography
	 * @param geography
	 * @param iou
	 * @param serviceLine
	 * @param salesStageFrom
	 * @param salesStageTo
	 * @param currency
	 * @param count
	 * @param customerName
	 * @param groupCustomer
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/topopps")
	public @ResponseBody String getTopOpportunities(
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "displayGeography", defaultValue = "") String displayGeography,
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "iou", defaultValue = "") String iou,
			@RequestParam(value = "serviceline", defaultValue = "") String serviceLine,
			@RequestParam(value = "stagefrom", defaultValue = "4") int salesStageFrom,
			@RequestParam(value = "stageto", defaultValue = "8") int salesStageTo,
			@RequestParam(value = "currency", defaultValue = "INR") String currency,
			@RequestParam(value = "count", defaultValue = "3") int count,
			@RequestParam(value = "customer", defaultValue = "") String customerName,
			@RequestParam(value = "groupCustomer", defaultValue = "") String groupCustomer,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside PerformanceReportController: Start of /perfreport/topopps GET");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		Date startDate, endDate;
		if (financialYear.isEmpty() && quarter.isEmpty()) {
			financialYear = DateUtils.getCurrentFinancialYear();
			startDate = DateUtils.getDateFromFinancialYear(financialYear, true);
			endDate = DateUtils.getDateFromFinancialYear(financialYear, false);
		} else if (financialYear.isEmpty() && !quarter.isEmpty()) {
			startDate = DateUtils.getDateFromQuarter(quarter, true);
			endDate = DateUtils.getDateFromQuarter(quarter, false);
		} else if (!financialYear.isEmpty() && quarter.isEmpty()) {
			startDate = DateUtils.getDateFromFinancialYear(financialYear, true);
			endDate = DateUtils.getDateFromFinancialYear(financialYear, false);
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Request - year and quarter cannot be set together");
		}
		List<OpportunityT> oppList;
		try {
			oppList = perfService.getTopOpportunities(currency,
					displayGeography, geography, salesStageFrom, salesStageTo,
					serviceLine, iou, startDate, endDate, count, customerName,
					groupCustomer, userId);
			logger.info("Inside PerformanceReportController: End of /perfreport/topopps GET");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, oppList);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the top opportunities");
		}
	}

	/**
	 * This method is used to insert recently searched group customer details
	 * 
	 * @param frequentlySearchedGroupCustomersT
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertToRecentlySearchedGroupCustomer(
			@RequestBody FrequentlySearchedGroupCustomersT frequentlySearchedGroupCustomersT)
			throws DestinationException {
		logger.info("Inside PerformanceReportController: Start of /perfreport POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (perfService
					.insertFrequentlySearchedGroupCustomer(frequentlySearchedGroupCustomersT)) {
				status.setStatus(Status.SUCCESS, "Inserted Successfully");
			}
			logger.info("Inside PerformanceReportController: End of /perfreport POST");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in inserting frequently searched group customer");
		}

	}

	/**
	 * This method used to get give frequently searched group customer
	 * details
	 * 
	 * @param fields
	 * @param view
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/freqSearchGroupCust")
	public @ResponseBody String findFrequentlySearchedGroupCustomer(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside PerformanceReportController: Start of /perfreport/freqSearchGroupCust GET");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		try {
			List<FrequentlySearchedGroupCustomersT> frequentlySearchedGroupCustomersT = perfService
					.findGroupCustomerName(userId);
			logger.info("Inside PerformanceReportController: End of /perfreport/freqSearchGroupCust GET");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, frequentlySearchedGroupCustomersT);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the frequently searched group customer");
		}
	}

}
