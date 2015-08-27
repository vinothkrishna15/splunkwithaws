package com.tcs.destination.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.TargetVsActualDetailed;
import com.tcs.destination.exception.DestinationException;
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
	BuildExcelTargetVsActualDetailedReportService buildExcelReportService;
	
	@Autowired
	ReportsUploadService reportUploadService;
	
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
	
	@RequestMapping(value = "/targetVsActual/detailed/excel", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> getTargetVsActualReport(
			@RequestParam(value = "from") String fromMonth,
			@RequestParam(value = "to", defaultValue = "") String toMonth,
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		InputStreamResource excelFile = reportsService.getTargetVsActualDetailedReport(geography, iou, fromMonth, toMonth, currency,fields,userId);
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		respHeaders.setContentDispositionFormData("Excel","TargetVsActualDetailReport.xlsx");
		return new ResponseEntity<InputStreamResource>(excelFile, respHeaders,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/targetVsActual/summary/excel", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> getTargetVsActualSummaryReport(
			@RequestParam(value = "from") String fromMonth,
			@RequestParam(value = "to", defaultValue = "") String toMonth,
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		InputStreamResource excelFile = reportsService.getTargetVsActualSummaryReport(geography, iou, fromMonth, toMonth, currency,userId);
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		respHeaders.setContentDispositionFormData("Excel","TargetVsActualSummaryReport.xlsx");
		return new ResponseEntity<InputStreamResource>(excelFile, respHeaders,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/targetVsActual/both/excel", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> getTargetVsActualBothReports(
			@RequestParam(value = "from") String fromMonth,
			@RequestParam(value = "to", defaultValue = "") String toMonth,
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		InputStreamResource excelFile = reportsService.getTargetVsActualReports(geography, iou, fromMonth, toMonth, currency, fields,userId);
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		respHeaders.setContentDispositionFormData("Excel","TargetVsActualReport.xlsx");
		return new ResponseEntity<InputStreamResource>(excelFile, respHeaders,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/connect/detailed", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> getDetailedConnectReport(
			@RequestParam(value = "month", defaultValue = "") String month,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "year", defaultValue = "") String year,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "serviceLines", defaultValue = "All") List<String> serviceLines,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		InputStreamResource connectDetailedReportExcel = reportsService.getConnectDetailedReport(month, quarter, year, iou,geography, country, serviceLines,userId,fields);
		HttpHeaders respHeaders = new HttpHeaders();
	    respHeaders.setContentDispositionFormData("Excel", "connectDetailReport.xlsx");
	    logger.debug("Download Header - Attachment : " +"connectDetailedReport.xlsx");
		respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		logger.debug("Connect Detailed Report Downloaded Successfully ");
		return new ResponseEntity<InputStreamResource>(connectDetailedReportExcel, respHeaders,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/connect/summary", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> getSummaryConnectReport(
			@RequestParam(value = "month", defaultValue = "") String month,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "year", defaultValue = "") String year,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "serviceLines", defaultValue = "All") List<String> serviceLines,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		InputStreamResource connectSummaryReportExcel = reportsService
				.connectSummaryReport(month, quarter, year, iou, geography,
						country, serviceLines, userId, fields);
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.setContentDispositionFormData("attachment",
				"connectSummaryReport.xlsx");
		logger.debug("Download Header - Attachment : "
				+ "connectSummaryReport.xlsx");
		respHeaders
				.setContentType(MediaType
						.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		logger.debug("Connect Summary Report Downloaded Successfully ");
		return new ResponseEntity<InputStreamResource>(
				connectSummaryReportExcel, respHeaders, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/connect/both", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> getConnectReport(
			@RequestParam(value = "month", defaultValue = "") String month,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "year", defaultValue = "") String year,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "serviceLines", defaultValue = "All") List<String> serviceLines,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		InputStreamResource connectReportExcel = reportsService.getConnectReports(month, quarter, year, iou,geography, country, serviceLines,userId,fields);
		HttpHeaders respHeaders = new HttpHeaders();
	    respHeaders.setContentDispositionFormData("attachment", "connectReports.xlsx");
	    logger.debug("Download Header - Attachment : " +" connectReports.xlsx");
		respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		logger.debug("Connect Report Downloaded Successfully ");
		return new ResponseEntity<InputStreamResource>(connectReportExcel, respHeaders,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/bid/detailed", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> getDetailedBidReport(
			@RequestParam(value = "year", defaultValue = "") String year,
			@RequestParam(value="from",defaultValue="")@DateTimeFormat(iso = ISO.DATE) Date fromDate,
			@RequestParam(value="to",defaultValue="") @DateTimeFormat(iso = ISO.DATE) Date toDate,
			@RequestParam(value = "bidOwner", defaultValue = "") List<String> bidOwner,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "serviceLines", defaultValue = "All") List<String> serviceLines,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside ReportController /report/bid/detailed GET");
		InputStreamResource bidReportExcel = reportsService.getBidReport(year, fromDate, toDate,bidOwner,currency,iou, geography, country,serviceLines,userId,fields);
		HttpHeaders respHeaders = new HttpHeaders();
	    respHeaders.setContentDispositionFormData("attachment", "bidDetailsReport.xlsx");
		respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		logger.debug("Bid Detailed Report Downloaded Successfully ");
		return new ResponseEntity<InputStreamResource>(bidReportExcel, respHeaders,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/bdmPerformance/detailed", method = RequestMethod.GET)			
	public @ResponseBody ResponseEntity<InputStreamResource> getBdmPerformanceSummary(
			@RequestParam(value = "from", defaultValue = "") String from,
			@RequestParam(value = "to", defaultValue = "") String to,
			@RequestParam(value = "year", defaultValue = "") String year,
			@RequestParam(value = "geography", defaultValue = "") List<String> geography,
			@RequestParam(value = "country", defaultValue = "") List<String> country,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "serviceLines", defaultValue = "") List<String> serviceLines,
			@RequestParam(value = "salesStage") List<Integer> salesStage,
			@RequestParam(value = "opportunityOwnerIds",defaultValue = "") List<String> opportunityOwnerIds,
			@RequestParam(value = "supervisorId") String supervisorId,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		InputStreamResource inputStreamResource=reportsService.getBdmDetailedReport(from,to,geography,country,currency,serviceLines,salesStage,opportunityOwnerIds,supervisorId);
		HttpHeaders respHeaders = new HttpHeaders();
	    respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
	    String toDate=DateUtils.getCurrentDate();
	    respHeaders.setContentDispositionFormData("Excel", "BdmPerformanceReport_"+toDate+".xlsx");
		return new ResponseEntity<InputStreamResource>(inputStreamResource,respHeaders,HttpStatus.OK);
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
			@RequestParam(value = "serviceLines", defaultValue = "All") List<String> serviceLines,
			@RequestParam(value = "salesStage", defaultValue = "0,1,2,3,4,5,6,7,8,9,10") List<Integer> salesStage,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {

		String toDate=DateUtils.getCurrentDate();
		InputStreamResource opportunityDetailedReportExcel = reportsService.getOpportunitiesWith(month,  quarter, year, geography, country, iou, serviceLines,salesStage, currency,userId,fields,toDate);
		HttpHeaders respHeaders = new HttpHeaders();
	    respHeaders.setContentDispositionFormData("attachment", "OpportunityReport_"+toDate+".xlsx");
		respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		logger.debug("Connect Detailed Report Downloaded Successfully ");
		return new ResponseEntity<InputStreamResource>(opportunityDetailedReportExcel, respHeaders,HttpStatus.OK);
	}

	@RequestMapping(value = "/opportunity/summary", method = RequestMethod.GET)			
	public @ResponseBody ResponseEntity<InputStreamResource> getOpportunitySummary(
			@RequestParam(value = "month", defaultValue = "") String month,
			@RequestParam(value = "year", defaultValue = "") String year,
			@RequestParam(value = "quarter", defaultValue = "") String quarter,
			@RequestParam(value = "geography", defaultValue = "All") List<String> geography,
			@RequestParam(value = "country", defaultValue = "All") List<String> country,
			@RequestParam(value = "iou", defaultValue = "All") List<String> iou,
			@RequestParam(value = "currency", defaultValue = "INR") List<String> currency,
			@RequestParam(value = "serviceLines", defaultValue = "All") List<String> serviceLines,
			@RequestParam(value = "salesStage",defaultValue = "0,1,2,3,4,5,6,7,8,9,10") List<Integer> salesStage,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {

	InputStreamResource inputStreamResource= reportsService.getOpportunitySummaryReport(month, year, quarter, geography,
			country, iou, currency, serviceLines, salesStage,userId);
	HttpHeaders respHeaders = new HttpHeaders();
	String toDate=DateUtils.getCurrentDate();
    respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    respHeaders.setContentDispositionFormData("Excel", "OpportunityReport_"+toDate+".xlsx");
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
			@RequestParam(value = "serviceLines", defaultValue = "All") List<String> serviceLines,
			@RequestParam(value = "salesStage", defaultValue = "0,1,2,3,4,5,6,7,8,9,10") List<Integer> salesStage,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "") List<String> fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
 		InputStreamResource inputStreamResource=reportsService.getOpportunityBothReport(month, year, quarter, geography,
				country, iou, currency, serviceLines, salesStage,userId,fields);
		HttpHeaders respHeaders = new HttpHeaders();
	    respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
	    String toDate=DateUtils.getCurrentDate();
	    respHeaders.setContentDispositionFormData("Excel", "OpportunityReport_"+toDate+".xlsx");
		return new ResponseEntity<InputStreamResource>(inputStreamResource,respHeaders,HttpStatus.OK);
	}
	
}