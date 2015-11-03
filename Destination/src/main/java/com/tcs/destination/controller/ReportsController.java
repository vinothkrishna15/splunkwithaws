package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.tcs.destination.service.BDMDetailedReportService;
import com.tcs.destination.service.BDMReportsService;
import com.tcs.destination.service.BuildExcelTargetVsActualDetailedReportService;
import com.tcs.destination.service.ReportsService;
import com.tcs.destination.service.ReportsUploadService;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ResponseConstructors;

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
	
	/**
	 * This Controller retrieves the Target Vs Actual Details based on input parameters
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
			@RequestParam(value = "iou", defaultValue = "") List<String> iouList,
			@RequestParam(value = "currency", defaultValue = "") List<String> currencyList,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {

		List<TargetVsActualDetailed> targetVsActualDetailedList = reportsService
				.getTargetVsActual(geographyList, iouList, fromMonth, toMonth, currencyList,userId);

		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				targetVsActualDetailedList);
	}
	
	/**
	 * This Controller retrieves the Target Vs Actual detailed report in excel format based on input parameters
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
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws Exception {
		InputStreamResource excelFile = reportsService.getTargetVsActualDetailedReport(geography, iou, fromMonth, toMonth, currency,fields,userId);
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		String todaysDate=DateUtils.getCurrentDate();
		logger.debug("Download Header - Attachment : " +"TargetVsActualDetailReport_"+todaysDate+".xlsx");
		respHeaders.setContentDispositionFormData("attachment","TargetVsActualDetailReport_"+todaysDate+".xlsx");
		logger.debug("targetVsActual Detailed Report Downloaded Successfully ");
		return new ResponseEntity<InputStreamResource>(excelFile, respHeaders,HttpStatus.OK);
	}
	
	/**
	 * This Controller retrieves the Target Vs Actual summary report in excel format based on input parameters
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
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "userId") String userId)
			throws Exception {
		InputStreamResource excelFile = reportsService.getTargetVsActualSummaryReport(geography, iou, fromMonth, toMonth, currency,userId);
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		String todaysDate=DateUtils.getCurrentDate();
		logger.debug("Download Header - Attachment : " +"TargetVsActualSummaryReport_"+todaysDate+".xlsx");
		respHeaders.setContentDispositionFormData("attachemnt","TargetVsActualSummaryReport_"+todaysDate+".xlsx");
		logger.debug("targetVsActual Summary Report Downloaded Successfully ");
		return new ResponseEntity<InputStreamResource>(excelFile, respHeaders,HttpStatus.OK);
	}
	
	/**
	 * This Controller retrieves the both Target Vs Actual detailed and summary report in excel format based on input parameters
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
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws Exception {
		InputStreamResource excelFile = reportsService.getTargetVsActualReports(geography, iou, fromMonth, toMonth, currency, fields,userId);
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		String todaysDate=DateUtils.getCurrentDate();
		logger.debug("Download Header - Attachment : " +"TargetVsActualReport_"+todaysDate+".xlsx");
		respHeaders.setContentDispositionFormData("attachment","TargetVsActualReport_"+todaysDate+".xlsx");
		logger.debug("targetVsActual Report Downloaded Successfully ");
		return new ResponseEntity<InputStreamResource>(excelFile, respHeaders,HttpStatus.OK);
	}
	
	/**
	 * This Controller retrieves the Connect detailed report in excel format based on input parameters
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
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "serviceline", defaultValue = "All") List<String> serviceline,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws Exception {
		InputStreamResource connectDetailedReportExcel = reportsService.getConnectDetailedReport(month, quarter, year, iou,geography, country, serviceline,userId,fields);
		HttpHeaders respHeaders = new HttpHeaders();
	    respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
	    String todaysDate=DateUtils.getCurrentDate();
	    logger.debug("Download Header - Attachment : " +"connectDetailedReport_"+todaysDate+".xlsx");
	    respHeaders.setContentDispositionFormData("attachment", "connectDetailReport_"+todaysDate+".xlsx");
		logger.debug("Connect Detailed Report Downloaded Successfully ");
		return new ResponseEntity<InputStreamResource>(connectDetailedReportExcel, respHeaders,HttpStatus.OK);
	}
	
	/**
	 * This Controller retrieves the Connect Summary report in excel format based on input parameters
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
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "serviceline", defaultValue = "All") List<String> serviceline,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws Exception {
		InputStreamResource connectSummaryReportExcel = reportsService.connectSummaryReport(month, quarter, year, iou, geography,
						country, serviceline, userId, fields);
		HttpHeaders respHeaders = new HttpHeaders();
		String todaysDate=DateUtils.getCurrentDate();
		logger.debug("Download Header - Attachment : "+ "connectSummaryReport_"+todaysDate+".xlsx");
		respHeaders.setContentDispositionFormData("attachment","connectSummaryReport_"+todaysDate+".xlsx");
		respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		logger.debug("Connect Summary Report Downloaded Successfully ");
		return new ResponseEntity<InputStreamResource>(
				connectSummaryReportExcel, respHeaders, HttpStatus.OK);
	}
	
	/**
	 * This Controller retrieves the both Connect detailed and summary report in excel format based on input parameters
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
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "serviceline", defaultValue = "All") List<String> serviceline,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws Exception {
		InputStreamResource connectReportExcel = reportsService.getConnectReports(month, quarter, year, iou,geography, country, serviceline,userId,fields);
		HttpHeaders respHeaders = new HttpHeaders();
	    respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
	    String todaysDate=DateUtils.getCurrentDate();
	    logger.debug("Download Header - Attachment : " +" connectReport_"+todaysDate+".xlsx");
	    respHeaders.setContentDispositionFormData("attachment", "connectReport_"+todaysDate+".xlsx");
		logger.debug("Connect Report Downloaded Successfully ");
		return new ResponseEntity<InputStreamResource>(connectReportExcel, respHeaders,HttpStatus.OK);
	}
	
	/**
	 * This Controller retrieves the Bid detailed report in excel format based on input parameters
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
			@RequestParam(value="from",defaultValue="") String fromMonth,
			@RequestParam(value="to",defaultValue="") String toMonth,
			@RequestParam(value = "bidOwner", defaultValue = "") List<String> bidOwner,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "serviceline", defaultValue = "All") List<String> serviceline,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws Exception {
		logger.debug("Inside ReportController /report/bid/detailed GET");
		InputStreamResource bidReportExcel = reportsService.getBidReport(year, fromMonth, toMonth,bidOwner,currency,iou, geography, country,serviceline,userId,fields);
		HttpHeaders respHeaders = new HttpHeaders();
		String todaysDate=DateUtils.getCurrentDate();
		logger.debug("Download Header - Attachment : " + "bidDetailsReport_"+todaysDate+".xlsx");
	    respHeaders.setContentDispositionFormData("attachment", "bidDetailsReport_"+todaysDate+".xlsx");
	    respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		logger.debug("Bid Detailed Report Downloaded Successfully ");
		return new ResponseEntity<InputStreamResource>(bidReportExcel, respHeaders,HttpStatus.OK);
	}
	

	/**
	 * This method gives detailed report of all the opportunities for the given sales stage code.
	 * 
	 * @param userId.
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
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws Exception {

		String toDate=DateUtils.getCurrentDate();
		InputStreamResource opportunityDetailedReportExcel = reportsService.getOpportunitiesWith(month,  quarter, year, geography, country, iou, serviceline,salesStage, currency,userId,fields,toDate);
		HttpHeaders respHeaders = new HttpHeaders();
	    respHeaders.setContentDispositionFormData("attachment", "OpportunityReport_"+toDate+".xlsx");
	    respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
	    logger.debug("Connect Detailed Report Downloaded Successfully ");
		return new ResponseEntity<InputStreamResource>(opportunityDetailedReportExcel, respHeaders,HttpStatus.OK);
	}

	/**
	 * This Controller retrieves the Opportunity summary report in excel format based on input parameters
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
			@RequestParam(value = "salesStage",defaultValue = "0,1,2,3,4,5,6,7,8,9,10") List<Integer> salesStage,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "all") String fields)
			throws Exception {

	InputStreamResource inputStreamResource= reportsService.getOpportunitySummaryReport(month, year, quarter, geography,
			country, iou, currency, serviceline, salesStage,userId);
	HttpHeaders respHeaders = new HttpHeaders();
	String toDate=DateUtils.getCurrentDate();
	  respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    respHeaders.setContentDispositionFormData("attachment", "OpportunityReport_"+toDate+".xlsx");
	return new ResponseEntity<InputStreamResource>(inputStreamResource,respHeaders,HttpStatus.OK);
	}
	
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
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
 		InputStreamResource inputStreamResource=reportsService.getOpportunityBothReport(month, year, quarter, geography,
				country, iou, currency, serviceline, salesStage,userId,fields);
		HttpHeaders respHeaders = new HttpHeaders();
	    respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
	    String toDate=DateUtils.getCurrentDate();
	    respHeaders.setContentDispositionFormData("attachment", "OpportunityReport_"+toDate+".xlsx");
		return new ResponseEntity<InputStreamResource>(inputStreamResource,respHeaders,HttpStatus.OK);
	}
	
	/**
	 * This Controller retrieves the BDM Performance details in excel format
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
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "from", defaultValue = "") String from,
			@RequestParam(value = "to", defaultValue = "") String to,
			@RequestParam(value = "financialYear", defaultValue = "") String financialYear,
			@RequestParam(value = "geography", defaultValue = "") List<String> geography,
			@RequestParam(value = "country", defaultValue = "") List<String> country,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "serviceline", defaultValue = "") List<String> serviceline,
			@RequestParam(value = "salesStage", defaultValue = "0,1,2,3,4,5,6,7,8,9,10,11,12,13") List<Integer> salesStage,
			@RequestParam(value = "opportunityOwners",defaultValue = "") List<String> opportunityOwners,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws Exception {
		InputStreamResource inputStreamResource=bdmDetailedReportService.getBdmDetailedReport(financialYear, from, to,
				 geography,  country,  currency,  serviceline, salesStage, opportunityOwners, userId, fields);
		HttpHeaders respHeaders = new HttpHeaders();
		  respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		  String toDate=DateUtils.getCurrentDate();
	    respHeaders.setContentDispositionFormData("attachment", "BdmPerformanceDetailedReport_"+toDate+".xlsx");
		return new ResponseEntity<InputStreamResource>(inputStreamResource,respHeaders,HttpStatus.OK);
	}
	
	/**
	 * This Controller retrieves BDM Performance Summary details in excel format based on input parameters
	 * @param supervisorId * @param financialYear * @param from
	 * @param to * @param opportunityOwners * @param geography
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
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "financialYear", defaultValue = "") String financialYear,
			@RequestParam(value = "from", defaultValue = "") String from,
			@RequestParam(value = "to", defaultValue = "") String to,
			@RequestParam(value = "opportunityOwners",defaultValue = "All") List<String> opportunityOwners,
			@RequestParam(value = "geography", defaultValue = "") List<String> geography,
			@RequestParam(value = "country", defaultValue = "") List<String> country,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "serviceLines", defaultValue = "") List<String> serviceLines,
			@RequestParam(value = "salesStage", defaultValue = "0,1,2,3,4,5,6,7,8,9,10,11,12,13") List<Integer> salesStage,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws Exception {
		InputStreamResource inputStreamResource=bdmReportsService.getBdmSummaryReport(financialYear, from, to, geography, country,
				currency, serviceLines, salesStage, opportunityOwners, userId, fields);
		HttpHeaders respHeaders = new HttpHeaders();
	    respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
	    String toDate=DateUtils.getCurrentDate();
	    respHeaders.setContentDispositionFormData("attachment", "BdmPerformanceSummaryReport_"+toDate+".xlsx");
		return new ResponseEntity<InputStreamResource>(inputStreamResource,respHeaders,HttpStatus.OK);
	}

	/**
	 * This Controller retrieves the both BDM Performance detailed and summary in excel format based on input parameters
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
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "from", defaultValue = "") String from,
			@RequestParam(value = "to", defaultValue = "") String to,
			@RequestParam(value = "financialYear", defaultValue = "") String financialYear,
			@RequestParam(value = "geography", defaultValue = "") List<String> geography,
			@RequestParam(value = "country", defaultValue = "") List<String> country,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "serviceline", defaultValue = "") List<String> serviceline,
			@RequestParam(value = "salesStage", defaultValue = "0,1,2,3,4,5,6,7,8,9,10,11,12,13") List<Integer> salesStage,
			@RequestParam(value = "opportunityOwners",defaultValue = "") List<String> opportunityOwners,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields)
			throws Exception {
		InputStreamResource inputStreamResource=bdmReportsService.getBdmsReport(financialYear, from, to,
				 geography,  country,  currency,  serviceline, salesStage, opportunityOwners, userId, fields);
		HttpHeaders respHeaders = new HttpHeaders();
		  respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		  String toDate=DateUtils.getCurrentDate();
	    respHeaders.setContentDispositionFormData("attachment", "BdmPerformanceReport_"+toDate+".xlsx");
		return new ResponseEntity<InputStreamResource>(inputStreamResource,respHeaders,HttpStatus.OK);
	}
	
}