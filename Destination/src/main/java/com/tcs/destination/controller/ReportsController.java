package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.TargetVsActualDetailed;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.BDMDetailedReportService;
import com.tcs.destination.service.BDMReportsService;
import com.tcs.destination.service.BuildExcelTargetVsActualDetailedReportService;
import com.tcs.destination.service.ReportsService;
import com.tcs.destination.service.ReportsUploadService;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.PropertyUtil;
import com.tcs.destination.utils.ResponseConstructors;

/*
 * This class deals with Reports and its associated functionalities
 */
@RestController
@RequestMapping("/report")
public class ReportsController {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportsController.class);

	@Autowired
	ReportsService reportsService;

	@Autowired
	BDMReportsService bdmReportsService;

	@Autowired
	BuildExcelTargetVsActualDetailedReportService buildExcelReportService;

	@Autowired
	ReportsUploadService reportUploadService;

	@Autowired
	BDMDetailedReportService bdmDetailedReportService;

	private static int targetVsActualConcurrentRequestCounter = 1;
	private static int connectConcurrentRequestCounter = 1;
	private static int bidConcurrentRequestCounter = 1;
	private static int opportunityConcurrentRequestCounter = 1;
	private static int bdmConcurrentRequestCounter = 1;
	private static int deliveryConcurrentRequestCounter = 1;

	@Value("${targetVsActualConcurrentRequestLimit}")
	private int targetVsActualConcurrentRequestLimit;
	@Value("${connectConcurrentRequestLimit}")
	private int connectConcurrentRequestLimit;
	@Value("${bidConcurrentRequestLimit}")
	private int bidConcurrentRequestLimit;
	@Value("${opportunityConcurrentRequestLimit}")
	private int opportunityConcurrentRequestLimit;
	@Value("${bdmConcurrentRequestLimit}")
	private int bdmConcurrentRequestLimit;
	
	@Value("${deliveryConcurrentRequestLimit}")
	private int deliveryConcurrentRequestLimit;

	/**
	 * This Controller retrieves the Target Vs Actual Details based on input
	 * parameters
	 * 
	 * @param fromMonth
	 * @param toMonth
	 * @param geographyList
	 * @param iouList
	 * @param currencyList
	 * @param userId
	 * @param fields
	 * @param view
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/targetVsActual", method = RequestMethod.GET)
	public @ResponseBody String targetVsActual(
			@RequestParam(value = "from") String fromMonth,
			@RequestParam(value = "to", defaultValue = "") String toMonth,
			@RequestParam(value = "geography", defaultValue = "") List<String> geographyList,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "iou", defaultValue = "") List<String> iouList,
			@RequestParam(value = "currency", defaultValue = "") List<String> currencyList,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside ReportsController / Start of retrieving the target vs actual detailed list");
		String response = null;

		String userId = DestinationUtils.getCurrentUserDetails().getUserId();

		List<TargetVsActualDetailed> targetVsActualDetailedList;
		try {
			targetVsActualDetailedList = reportsService.getTargetVsActual(
					geographyList, iouList, fromMonth, toMonth, currencyList,
					userId, country);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, targetVsActualDetailedList);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the target vs actual detailed list");
		}
		logger.info("Inside ReportsController / End of retrieving the target vs actual detailed list");
		return response;
	}

	/**
	 * This Controller retrieves the Target Vs Actual detailed report in excel
	 * format based on input parameters
	 * 
	 * @param fromMonth
	 * @param toMonth
	 * @param geography
	 * @param iou
	 * @param currency
	 * @param userId
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/targetVsActual/detailed", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> getTargetVsActualReport(
			@RequestParam(value = "from") String fromMonth,
			@RequestParam(value = "to", defaultValue = "") String toMonth,
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws DestinationException {
		logger.info("Inside ReportsController / Start of Target vs Actual detailed Report download");
		if (targetVsActualConcurrentRequestCounter <= targetVsActualConcurrentRequestLimit) {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			try {
				++targetVsActualConcurrentRequestCounter;
				InputStreamResource excelFile = reportsService
						.getTargetVsActualDetailedReport(geography, country,
								iou, fromMonth, toMonth, currency, fields,
								userId);
				HttpHeaders respHeaders = new HttpHeaders();
				respHeaders
						.setContentType(MediaType
								.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
				String todaysDate = DateUtils.getCurrentDate();
				String environmentName=PropertyUtil.getProperty("environment.name");
				String repName = environmentName+"_TargetVsActualDetailReport_" + todaysDate
						+ ".xlsx";
				respHeaders.add("reportName", repName);
				respHeaders
						.setContentDispositionFormData("attachment", repName);
				logger.info("Inside ReportsController / End of Target vs Actual detailed Report download");
				return new ResponseEntity<InputStreamResource>(excelFile,
						respHeaders, HttpStatus.OK);
			} catch (DestinationException e) {
				throw e;
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Backend error in downloading the target vs actual detailed report");
			} finally {
				--targetVsActualConcurrentRequestCounter;
			}
		} else {
			throw new DestinationException(HttpStatus.SERVICE_UNAVAILABLE,
					"Target Vs Actual report  is experiencing high loads, please try again after sometime");

		}
	}

	/**
	 * This Controller retrieves the Target Vs Actual summary report in excel
	 * format based on input parameters
	 * 
	 * @param fromMonth
	 * @param toMonth
	 * @param geography
	 * @param iou
	 * @param currency
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/targetVsActual/summary", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> getTargetVsActualSummaryReport(
			@RequestParam(value = "from") String fromMonth,
			@RequestParam(value = "to", defaultValue = "") String toMonth,
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency)
			throws DestinationException {
		logger.info("Inside ReportsController / Start of Target vs Actual summary Report download");
		if (targetVsActualConcurrentRequestCounter <= targetVsActualConcurrentRequestLimit) {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			try {
				++targetVsActualConcurrentRequestCounter;
				InputStreamResource excelFile = reportsService
						.getTargetVsActualSummaryReport(geography, country,
								iou, fromMonth, toMonth, currency, userId);
				HttpHeaders respHeaders = new HttpHeaders();
				respHeaders
						.setContentType(MediaType
								.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
				String todaysDate = DateUtils.getCurrentDate();
				String environmentName=PropertyUtil.getProperty("environment.name");
				String repName = environmentName+"_TargetVsActualSummaryReport_" + todaysDate
						+ ".xlsx";
				respHeaders.add("reportName", repName);
				respHeaders
						.setContentDispositionFormData("attachment", repName);
				logger.info("Inside ReportsController / End of Target vs Actual summary Report download");
				return new ResponseEntity<InputStreamResource>(excelFile,
						respHeaders, HttpStatus.OK);
			} catch (DestinationException e) {
				throw e;
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Backend error in downloading the target vs actual summary report");
			} finally {
				--targetVsActualConcurrentRequestCounter;
			}
		} else {
			throw new DestinationException(HttpStatus.SERVICE_UNAVAILABLE,
					"Target Vs Actual report module is experiencing huge loads, please try again after sometime");

		}
	}

	/**
	 * This Controller retrieves the both Target Vs Actual detailed and summary
	 * report in excel format based on input parameters
	 * 
	 * @param fromMonth
	 * @param toMonth
	 * @param geography
	 * @param iou
	 * @param currency
	 * @param userId
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/targetVsActual/both", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> getTargetVsActualBothReports(
			@RequestParam(value = "from") String fromMonth,
			@RequestParam(value = "to", defaultValue = "") String toMonth,
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws DestinationException {
		logger.info("Inside ReportsController / Start of Target vs Actual both summary and detailed report download");
		if (targetVsActualConcurrentRequestCounter <= targetVsActualConcurrentRequestLimit) {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			try {
				++targetVsActualConcurrentRequestCounter;
				InputStreamResource excelFile = reportsService
						.getTargetVsActualReports(geography, country, iou,
								fromMonth, toMonth, currency, fields, userId);
				HttpHeaders respHeaders = new HttpHeaders();
				respHeaders
						.setContentType(MediaType
								.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
				String todaysDate = DateUtils.getCurrentDate();
				String environmentName=PropertyUtil.getProperty("environment.name");
				String repName =environmentName+"_TargetVsActualReport_" + todaysDate + ".xlsx";
				respHeaders.add("reportName", repName);
				respHeaders
						.setContentDispositionFormData("attachment", repName);
				logger.info("Inside ReportsController / End of Target vs Actual both summary and detailed report download");
				return new ResponseEntity<InputStreamResource>(excelFile,
						respHeaders, HttpStatus.OK);
			} catch (DestinationException e) {
				throw e;
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Backend error in downloading the target vs actual both summary and detailed report");
			} finally {
				--targetVsActualConcurrentRequestCounter;
			}
		} else {
			throw new DestinationException(HttpStatus.SERVICE_UNAVAILABLE,
					"Target Vs Actual report module is experiencing huge loads, please try again after sometime");

		}
	}

	/**
	 * This Controller retrieves the Connect detailed report in excel format based on input parameters
	 * 
	 * @param month
	 * @param quarter
	 * @param year
	 * @param iou
	 * @param geography
	 * @param country
	 * @param serviceline
	 * @param userId
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/connect/detailed", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> getDetailedConnectReport(
			@RequestParam(value = "month", defaultValue = "") String month,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "year", defaultValue = "") String year,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "serviceline", defaultValue = "All") List<String> serviceline,
			@RequestParam(value = "connectCategory", defaultValue = "All") String connectCategory,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws DestinationException {
		logger.info("Inside ReportsController / Start of Connect detailed report download");
		if (connectConcurrentRequestCounter <= connectConcurrentRequestLimit) {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			try {
				++connectConcurrentRequestCounter;
				
				InputStreamResource connectDetailedReportExcel = reportsService.getConnectDetailedReport(month, quarter, year, iou,
								geography, country, serviceline, userId, fields, connectCategory);
				
				HttpHeaders respHeaders = new HttpHeaders();
				
				respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
				
				String todaysDate = DateUtils.getCurrentDate();
				
				String environmentName=PropertyUtil.getProperty("environment.name");
				
				if(!connectCategory.equals("All")){
					environmentName = environmentName+"_"+connectCategory;
				}
				
				String repName =environmentName+"_connectDetailedReport_" + todaysDate+ ".xlsx";
				
				respHeaders.add("reportName", repName);
				
				respHeaders.setContentDispositionFormData("attachment", repName);
				
				logger.info("Inside ReportsController / End of Connect detailed report download");
				return new ResponseEntity<InputStreamResource>(connectDetailedReportExcel, respHeaders, HttpStatus.OK);
			} catch (DestinationException e) {
				throw e;
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,"Backend error in downloading the connect detailed report");
			} finally {
				--connectConcurrentRequestCounter;
			}
		} else {
			throw new DestinationException(HttpStatus.SERVICE_UNAVAILABLE,
					"Connect report  is experiencing high loads, please try again after sometime");

		}
	}

	/**
	 * This Controller retrieves the Connect Summary report in excel format based on input parameters
	 * 
	 * @param month
	 * @param quarter
	 * @param year
	 * @param iou
	 * @param geography
	 * @param country
	 * @param serviceline
	 * @param userId
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/connect/summary", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> getSummaryConnectReport(
			@RequestParam(value = "month", defaultValue = "") String month,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "year", defaultValue = "") String year,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "serviceline", defaultValue = "All") List<String> serviceline,
			@RequestParam(value = "connectCategory", defaultValue = "All") String connectCategory,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws DestinationException {
		logger.info("Inside ReportsController / Start of Connect summary report download");
		if (connectConcurrentRequestCounter <= connectConcurrentRequestLimit) {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			try {
				++connectConcurrentRequestCounter;
				
				InputStreamResource connectSummaryReportExcel = reportsService.connectSummaryReport(month, quarter, year, iou,
								geography, country, serviceline, userId, fields, connectCategory);
				
				HttpHeaders respHeaders = new HttpHeaders();
				
				String todaysDate = DateUtils.getCurrentDate();
				
				String environmentName=PropertyUtil.getProperty("environment.name");
				
				if(!connectCategory.equals("All")){
					environmentName = environmentName+"_"+connectCategory;
				}
				
				String repName = environmentName+"_connectSummaryReport_" + todaysDate + ".xlsx";
				
				respHeaders.add("reportName", repName);
				
				respHeaders.setContentDispositionFormData("attachment", repName);
				
				respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
				
				logger.info("Inside ReportsController / End of Connect summary report download");
				return new ResponseEntity<InputStreamResource>(connectSummaryReportExcel, respHeaders, HttpStatus.OK);

			} catch (DestinationException e) {
				throw e;
			
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,"Backend error in downloading the connect summary report");
			
			} finally {
				--connectConcurrentRequestCounter;
			}
		
		} else {
			throw new DestinationException(HttpStatus.SERVICE_UNAVAILABLE,
					"Connect report  is experiencing high loads, please try again after sometime");
		}
	}

	/**
	 * This Controller retrieves the both Connect detailed and summary report in
	 * excel format based on input parameters
	 * 
	 * @param month
	 * @param quarter
	 * @param year
	 * @param iou
	 * @param geography
	 * @param country
	 * @param serviceline
	 * @param userId
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/connect/both", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> getConnectReport(
			@RequestParam(value = "month", defaultValue = "") String month,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "year", defaultValue = "") String year,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "geography", defaultValue = "") String geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "serviceline", defaultValue = "All") List<String> serviceline,
			@RequestParam(value = "connectCategory", defaultValue = "All") String connectCategory,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields) throws DestinationException {
	
		logger.info("Inside ReportsController / Start of Connect both summary and detailed report download");
		
		if (connectConcurrentRequestCounter <= connectConcurrentRequestLimit) {
			
			String userId = DestinationUtils.getCurrentUserDetails().getUserId();
			
			try {
				++connectConcurrentRequestCounter;
				
				InputStreamResource connectReportExcel = reportsService.getConnectDetailedAndSummaryReports(month, quarter, year, iou,
								geography, country, serviceline, userId, fields,connectCategory);
				
				HttpHeaders respHeaders = new HttpHeaders();
				
				respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
				
				String todaysDate = DateUtils.getCurrentDate();
		
				String environmentName=PropertyUtil.getProperty("environment.name");
				
				if(!connectCategory.equals("All")){
					environmentName = environmentName+"_"+connectCategory;
				}
				
				String repName =environmentName+"_connectReport_" + todaysDate + ".xlsx";
				
				respHeaders.add("reportName", repName);
				
				respHeaders.setContentDispositionFormData("attachment", repName);
				
				logger.info("Inside ReportsController / End of Connect both summary and detailed report download");
				return new ResponseEntity<InputStreamResource>(connectReportExcel, respHeaders, HttpStatus.OK);
			
			} catch (DestinationException e) {
				throw e;
			
			} catch (Exception e) {
			
				logger.error(e.getMessage());
				throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR, "Backend error in downloading the connect both summary and detailed report");
			} finally {
				--connectConcurrentRequestCounter;
			}

		} else {
			throw new DestinationException(HttpStatus.SERVICE_UNAVAILABLE,
					"Connect report  is experiencing high loads, please try again after sometime");

		}
	}

	/**
	 * This Controller retrieves the Bid detailed report in excel format based
	 * on input parameters
	 * 
	 * @param year
	 * @param fromMonth
	 * @param toMonth
	 * @param bidOwner
	 * @param currency
	 * @param iou
	 * @param geography
	 * @param country
	 * @param serviceline
	 * @param userId
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/bid/detailed", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> getDetailedBidReport(
			@RequestParam(value = "year", defaultValue = "") String year,
			@RequestParam(value = "from", defaultValue = "") String fromMonth,
			@RequestParam(value = "to", defaultValue = "") String toMonth,
			@RequestParam(value = "bidOwner", defaultValue = "") List<String> bidOwner,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "serviceline", defaultValue = "All") List<String> serviceline,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws DestinationException {
		logger.info("Inside ReportsController / Start of Bid detailed report download");
		if (bidConcurrentRequestCounter <= bidConcurrentRequestLimit) {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			try {
				++bidConcurrentRequestCounter;
				InputStreamResource bidReportExcel = reportsService
						.getBidReport(year, fromMonth, toMonth, bidOwner,
								currency, iou, geography, country, serviceline,
								userId, fields);
				HttpHeaders respHeaders = new HttpHeaders();
				String todaysDate = DateUtils.getCurrentDate();
				String environmentName=PropertyUtil.getProperty("environment.name");
				String repName = environmentName+"_bidDetailsReport_" + todaysDate + ".xlsx";
				respHeaders.add("reportName", repName);
				respHeaders
						.setContentDispositionFormData("attachment", repName);
				respHeaders
						.setContentType(MediaType
								.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
				logger.info("Inside ReportsController / End of Bid detailed report download");
				return new ResponseEntity<InputStreamResource>(bidReportExcel,
						respHeaders, HttpStatus.OK);
			} catch (DestinationException e) {
				throw e;
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Backend error in downloading the bid detailed report");
			} finally {
				--bidConcurrentRequestCounter;
			}
		} else {
			throw new DestinationException(HttpStatus.SERVICE_UNAVAILABLE,
					"Bid report  is experiencing high loads, please try again after sometime");
		}
	}

	/**
	 * This method gives detailed report of all the opportunities for the given
	 * sales stage code.
	 * 
	 * @param userId
	 *            .
	 * @return opportunity details for the given user id.
	 */
	@RequestMapping(value = "/opportunity/detailed", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> getDetailedOpportunityReport(
			@RequestParam(value = "month", defaultValue = "") String month,
			@RequestParam(value = "year", defaultValue = "") String year,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "serviceline", defaultValue = "All") List<String> serviceline,
			@RequestParam(value = "salesStage", defaultValue = "0,1,2,3,4,5,6,7,8,9,10,11,12,13") List<Integer> salesStage,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws DestinationException {
		logger.info("Inside ReportsController / Start of Opportunity detailed report download");
		if (opportunityConcurrentRequestCounter <= opportunityConcurrentRequestLimit) {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			try {
				++opportunityConcurrentRequestCounter;

				String toDate = DateUtils.getCurrentDate();
				InputStreamResource opportunityDetailedReportExcel = reportsService
						.getOpportunitiesWith(month, quarter, year, geography,
								country, iou, serviceline, salesStage,
								currency, userId, fields, toDate);
				HttpHeaders respHeaders = new HttpHeaders();
				String environmentName=PropertyUtil.getProperty("environment.name");
				String repName = environmentName+"_OpportunityReport_" + toDate + ".xlsx";
				respHeaders.add("reportName", repName);
				respHeaders
						.setContentDispositionFormData("attachment", repName);
				respHeaders
						.setContentType(MediaType
								.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
				logger.info("Inside ReportsController / End of Opportunity detailed report download");
				return new ResponseEntity<InputStreamResource>(
						opportunityDetailedReportExcel, respHeaders,
						HttpStatus.OK);
			} catch (DestinationException e) {
				throw e;
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Backend error in downloading the opportunity detailed report");
			} finally {
				--opportunityConcurrentRequestCounter;
			}
		} else {
			throw new DestinationException(HttpStatus.SERVICE_UNAVAILABLE,
					"Opportunity report  is experiencing high loads, please try again after sometime");
		}

	}

	/**
	 * This Controller retrieves the Opportunity summary report in excel format
	 * based on input parameters
	 * 
	 * @param month
	 * @param year
	 * @param quarter
	 * @param geography
	 * @param country
	 * @param iou
	 * @param currency
	 * @param serviceline
	 * @param salesStage
	 * @param userId
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/opportunity/summary", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> getOpportunitySummary(
			@RequestParam(value = "month", defaultValue = "") String month,
			@RequestParam(value = "year", defaultValue = "") String year,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "serviceline", defaultValue = "All") List<String> serviceline,
			@RequestParam(value = "salesStage", defaultValue = "0,1,2,3,4,5,6,7,8,9,10") List<Integer> salesStage,
			@RequestParam(value = "fields", defaultValue = "all") String fields)
			throws DestinationException {
		logger.info("Inside ReportsController / Start of Opportunity summary report download");
		if (opportunityConcurrentRequestCounter <= opportunityConcurrentRequestLimit) {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			try {
				++opportunityConcurrentRequestCounter;
				InputStreamResource inputStreamResource = reportsService
						.getOpportunitySummaryReport(month, year, quarter,
								geography, country, iou, currency, serviceline,
								salesStage, userId);
				HttpHeaders respHeaders = new HttpHeaders();
				String toDate = DateUtils.getCurrentDate();
				String environmentName=PropertyUtil.getProperty("environment.name");
				String repName =environmentName+"_OpportunityReport_" + toDate + ".xlsx";
				respHeaders.add("reportName", repName);
				respHeaders
						.setContentType(MediaType
								.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
				respHeaders
						.setContentDispositionFormData("attachment", repName);
				logger.info("Inside ReportsController / End of Opportunity summary report download");
				return new ResponseEntity<InputStreamResource>(
						inputStreamResource, respHeaders, HttpStatus.OK);
			} catch (DestinationException e) {
				throw e;
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Backend error in downloading the opportunity summary report");
			} finally {
				--opportunityConcurrentRequestCounter;
			}
		} else {
			throw new DestinationException(HttpStatus.SERVICE_UNAVAILABLE,
					"Opportunity report  is experiencing high loads, please try again after sometime");
		}
	}

	/**
	 * @param month
	 * @param year
	 * @param quarter
	 * @param geography
	 * @param country
	 * @param iou
	 * @param currency
	 * @param serviceline
	 * @param salesStage
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/opportunity/both", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> getOpportunityBoth(
			@RequestParam(value = "month", defaultValue = "") String month,
			@RequestParam(value = "year", defaultValue = "") String year,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "serviceline", defaultValue = "All") List<String> serviceline,
			@RequestParam(value = "salesStage", defaultValue = "0,1,2,3,4,5,6,7,8,9,10,11,12,13") List<Integer> salesStage,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside ReportsController / Start of Opportunity both summary and detailed report download");
		if (opportunityConcurrentRequestCounter <= opportunityConcurrentRequestLimit) {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			try {
				++opportunityConcurrentRequestCounter;
				InputStreamResource inputStreamResource = reportsService
						.getOpportunityBothReport(month, year, quarter,
								geography, country, iou, currency, serviceline,
								salesStage, userId, fields);
				HttpHeaders respHeaders = new HttpHeaders();
				respHeaders
						.setContentType(MediaType
								.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
				String toDate = DateUtils.getCurrentDate();
				String environmentName=PropertyUtil.getProperty("environment.name");
				String repName = environmentName+"_OpportunityReport_" + toDate + ".xlsx";
				respHeaders.add("reportName", repName);
				respHeaders
						.setContentDispositionFormData("attachment", repName);
				logger.info("Inside ReportsController / End of Opportunity both summary and detailed report download");
				return new ResponseEntity<InputStreamResource>(
						inputStreamResource, respHeaders, HttpStatus.OK);
			} catch (DestinationException e) {
				throw e;
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Backend error in downloading the opportunity both summary and detailed report");
			} finally {
				--opportunityConcurrentRequestCounter;
			}
		} else {
			throw new DestinationException(HttpStatus.SERVICE_UNAVAILABLE,
					"Opportunity report  is experiencing high loads, please try again after sometime");

		}
	}

	/**
	 * This Controller retrieves the BDM Performance details in excel format
	 * 
	 * @param from
	 * @param to
	 * @param financialYear
	 * @param geography
	 * @param country
	 * @param currency
	 * @param serviceline
	 * @param salesStage
	 * @param owners
	 * @param supervisorId
	 * @param fields
	 * @param view
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/bdmPerformance/detailed", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> getBdmPerformanceSummary(
			@RequestParam(value = "from", defaultValue = "") String from,
			@RequestParam(value = "to", defaultValue = "") String to,
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "geography", defaultValue = "") List<String> geography,
			@RequestParam(value = "country", defaultValue = "") List<String> country,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "serviceline", defaultValue = "") List<String> serviceline,
			@RequestParam(value = "iou", defaultValue = "") List<String> iou,
			@RequestParam(value = "salesStage", defaultValue = "0,1,2,3,4,5,6,7,8,9,10,11,12,13") List<Integer> salesStage,
			@RequestParam(value = "opportunityOwners", defaultValue = "") List<String> opportunityOwners,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws DestinationException {
		logger.info("Inside ReportsController / Start of BDM Performance detailed report download");
		if (bdmConcurrentRequestCounter <= bdmConcurrentRequestLimit) {

			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			try {
				++bdmConcurrentRequestCounter;
				InputStreamResource inputStreamResource = bdmDetailedReportService
						.getBdmDetailedReport(financialYear, from, to,
								geography, country, currency, serviceline, iou,
								salesStage, opportunityOwners, userId, fields);
				HttpHeaders respHeaders = new HttpHeaders();
				respHeaders
						.setContentType(MediaType
								.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
				String toDate = DateUtils.getCurrentDate();
				String environmentName=PropertyUtil.getProperty("environment.name");
				String repName =environmentName+"_BdmPerformanceDetailedReport_" + toDate
						+ ".xlsx";
				respHeaders.add("reportName", repName);
				respHeaders
						.setContentDispositionFormData("attachment", repName);
				logger.info("Inside ReportsController / End of BDM Performance detailed report download");
				return new ResponseEntity<InputStreamResource>(
						inputStreamResource, respHeaders, HttpStatus.OK);
			} catch (DestinationException e) {
				throw e;
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Backend error in downloading the bdm performance detailed report");
			} finally {
				--bdmConcurrentRequestCounter;
			}
		} else {
			throw new DestinationException(HttpStatus.SERVICE_UNAVAILABLE,
					"Bdm Performance report  is experiencing high loads, please try again after sometime");
		}
	}

	/**
	 * This Controller retrieves BDM Performance Summary details in excel format
	 * based on input parameters
	 * 
	 * @param supervisorId
	 *            * @param financialYear * @param from
	 * @param to
	 *            * @param opportunityOwners * @param geography
	 * @param country
	 * @param currency
	 * @param serviceLines
	 * @param salesStage
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/bdmPerformance/summary", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> getBdmPerformanceSummaryReport(
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "from", defaultValue = "") String from,
			@RequestParam(value = "to", defaultValue = "") String to,
			@RequestParam(value = "opportunityOwners", defaultValue = "") List<String> opportunityOwners,
			@RequestParam(value = "geography", defaultValue = "") List<String> geography,
			@RequestParam(value = "country", defaultValue = "") List<String> country,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "serviceLines", defaultValue = "") List<String> serviceLines,
			@RequestParam(value = "iou", defaultValue = "") List<String> iou,
			@RequestParam(value = "salesStage", defaultValue = "0,1,2,3,4,5,6,7,8,9,10,11,12,13") List<Integer> salesStage,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws DestinationException {
		logger.info("Inside ReportsController / Start of BDM Performance summary report download");
		if (bdmConcurrentRequestCounter <= bdmConcurrentRequestLimit) {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			try {
				++bdmConcurrentRequestCounter;
				InputStreamResource inputStreamResource = bdmReportsService
						.getBdmSummaryReport(financialYear, from, to,
								geography, country, currency, serviceLines,
								iou, salesStage, opportunityOwners, userId,
								fields);
				HttpHeaders respHeaders = new HttpHeaders();
				respHeaders
						.setContentType(MediaType
								.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
				String toDate = DateUtils.getCurrentDate();
				String environmentName=PropertyUtil.getProperty("environment.name");
				String repName = environmentName+"_BdmPerformanceSummaryReport_" + toDate
						+ ".xlsx";
				respHeaders.add("reportName", repName);

				respHeaders.setContentDispositionFormData("attachment",
						"BdmPerformanceSummaryReport_" + toDate + ".xlsx");
				logger.info("Inside ReportsController / End of BDM Performance summary report download");
				return new ResponseEntity<InputStreamResource>(
						inputStreamResource, respHeaders, HttpStatus.OK);
			} catch (DestinationException e) {
				throw e;
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Backend error in downloading the bdm performance summary report");
			} finally {
				--bdmConcurrentRequestCounter;
			}
		} else {
			throw new DestinationException(HttpStatus.SERVICE_UNAVAILABLE,
					"Bdm Performance report  is experiencing high loads, please try again after sometime");
		}
	}

	/**
	 * This Controller retrieves the both BDM Performance detailed and summary
	 * in excel format based on input parameters
	 * 
	 * @param userId
	 * @param from
	 * @param to
	 * @param financialYear
	 * @param geography
	 * @param country
	 * @param currency
	 * @param serviceline
	 * @param salesStage
	 * @param opportunityOwners
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/bdmPerformance/both", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> getBdmsPerformanceReport(
			@RequestParam(value = "from", defaultValue = "") String from,
			@RequestParam(value = "to", defaultValue = "") String to,
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "geography", defaultValue = "") List<String> geography,
			@RequestParam(value = "country", defaultValue = "") List<String> country,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "serviceline", defaultValue = "") List<String> serviceline,
			@RequestParam(value = "iou", defaultValue = "") List<String> iou,
			@RequestParam(value = "salesStage", defaultValue = "0,1,2,3,4,5,6,7,8,9,10,11,12,13") List<Integer> salesStage,
			@RequestParam(value = "opportunityOwners", defaultValue = "") List<String> opportunityOwners,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws DestinationException {
		logger.info("Inside ReportsController / Start of BDM Performance both summary and detailed report download");
		if (bdmConcurrentRequestCounter <= bdmConcurrentRequestLimit) {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			try {
				++bdmConcurrentRequestCounter;
				InputStreamResource inputStreamResource = bdmReportsService
						.getBdmsReport(financialYear, from, to, geography,
								country, currency, serviceline, iou,
								salesStage, opportunityOwners, userId, fields);
				HttpHeaders respHeaders = new HttpHeaders();
				respHeaders
						.setContentType(MediaType
								.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
				String toDate = DateUtils.getCurrentDate();
				String environmentName=PropertyUtil.getProperty("environment.name");
				String repName = environmentName+"_BdmPerformanceReport_" + toDate + ".xlsx";
				respHeaders.add("reportName", repName);

				respHeaders
						.setContentDispositionFormData("attachment", repName);
				logger.info("Inside ReportsController / End of BDM Performance both summary and detailed report download");
				return new ResponseEntity<InputStreamResource>(
						inputStreamResource, respHeaders, HttpStatus.OK);
			} catch (DestinationException e) {
				throw e;
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Backend error in downloading the bdm performance both summary and detailed report");
			} finally {
				--bdmConcurrentRequestCounter;
			}
		} else {
			throw new DestinationException(HttpStatus.SERVICE_UNAVAILABLE,
					"Bdm Performance report  is experiencing high loads, please try again after sometime");

		}
	}
	
	/**
	 * Gets the Engagement report in Excel format and is applicable only for
	 * Delivery Team and Strategic Initiatives
	 * @param country
	 * @param geography
	 * @param iou
	 * @param serviceline
	 * @param deliveryStage
	 * @param deliveryCentre
	 * @param fields
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value= "/deliveryEngagement/", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity <InputStreamResource> getDeliveryEngagementReport(
			@RequestParam(value = "country", defaultValue = "") List<String> country,
			@RequestParam(value = "displayGeography", defaultValue = "") List<String> geography,
			@RequestParam(value = "displayIou", defaultValue = "") List<String> iou,
			@RequestParam(value = "displaySubSp", defaultValue = "") List<String> serviceline,
			@RequestParam(value = "deliveryStage", defaultValue = "-1") List<Integer> deliveryStage,
			@RequestParam(value = "deliveryCentre", defaultValue = "-2") List<Integer> deliveryCentre,
			@RequestParam(value = "optColumns", defaultValue = "") List<String> fields)
			throws DestinationException {
		logger.info("Inside ReportsController / Start of Delivery Engagement detailed report download");
		if (deliveryConcurrentRequestCounter <= deliveryConcurrentRequestLimit) {
			UserT user = DestinationUtils.getCurrentUserDetails();
			try {
				++deliveryConcurrentRequestCounter;
				InputStreamResource inputStreamResource = reportsService
						.getDeliveryEngagementReport(geography,country, serviceline, iou, deliveryStage, deliveryCentre, user, fields);
				HttpHeaders respHeaders = new HttpHeaders();
				respHeaders
						.setContentType(MediaType
								.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
				String toDate = DateUtils.getCurrentDate();
				String environmentName=PropertyUtil.getProperty("environment.name");
				String repName = environmentName+"_DeliveryEngagementReport_" + toDate + ".xlsx";
				respHeaders.add("reportName", repName);

				respHeaders
						.setContentDispositionFormData("attachment", repName);
				logger.info("Inside ReportController / End of Delivery Engagement detailed report download");
				return new ResponseEntity<InputStreamResource>(
						inputStreamResource, respHeaders, HttpStatus.OK);
			} catch (DestinationException e) {
				throw e;
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Backend error in downloading the Delivery Engagement detailed report");
			} finally {
				--deliveryConcurrentRequestCounter;
			}
		} else {
			throw new DestinationException(HttpStatus.SERVICE_UNAVAILABLE,
					"Delivery Engagement report  is experiencing high loads, please try again after sometime");

		}
	}


}