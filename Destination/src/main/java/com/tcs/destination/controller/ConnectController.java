package com.tcs.destination.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.ConnectNameKeywordSearch;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.DashBoardConnectsResponse;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.ConnectDownloadService;
import com.tcs.destination.service.ConnectService;
import com.tcs.destination.service.ConnectUploadService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * Controller to handle connection details search requests.
 * 
 */
@RestController
@RequestMapping("/connect")
public class ConnectController {

	private static final Logger logger = LoggerFactory
			.getLogger(ConnectController.class);

	@Autowired
	ConnectService connectService;

	@Autowired
	ConnectUploadService connectUploadService;

	@Autowired
	UploadErrorReport uploadErrorReport;

	@Autowired
	ConnectDownloadService connectDownloadService;
	
	private static final DateFormat actualFormat = new SimpleDateFormat("dd-MMM-yyyy");
	private static final DateFormat desiredFormat = new SimpleDateFormat("MM/dd/yyyy");

	/**
	 * This Method is used to find connection details for the given connection
	 * id.
	 * 
	 * @param Id
	 *            is the connection id.
	 * @return connection details for the particular connection id.
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findConnectById(
			@PathVariable("id") String connectId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.debug("Inside ConnectController /connect/id=" + connectId
				+ " GET");
		ConnectT connect;
		try {
			connect = connectService.findConnectById(connectId);
		
			return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
					connect);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving connect details");
		}
	}

	/**
	 * This Method is used to find connection details for the given connection
	 * name.
	 * 
	 * @param name
	 *            is the connection name.
	 * @return connection details for the particular connection name.
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String ConnectSearchByName(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam("nameWith") String connectName,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "customerId", defaultValue = "") String customerId,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.debug("Inside ConnectController /connect?nameWith="
				+ connectName + " GET");
		try{
		PaginatedResponse paginatedConnect = connectService
				.searchforConnectsByNameContaining(connectName, customerId,
						page, count);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				paginatedConnect);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving connects list");
		}
	}

	/**
	 * This Method is used to find connection details for the given connection
	 * name.
	 * 
	 * @param typed
	 *            is the connection name.
	 * @return connection details for the particular connection name.
	 */
	@RequestMapping(value = "/date", method = RequestMethod.GET)
	public @ResponseBody String search(
			@RequestParam("from") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam("to") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "owner", defaultValue = "ALL") String owner,
			@RequestParam(value = "customerId", defaultValue = "") String customerId,
			@RequestParam(value = "partnerId", defaultValue = "") String partnerId,
			@RequestParam(value = "weekStartDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date weekStartDate,
			@RequestParam(value = "weekEndDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date weekEndDate,
			@RequestParam(value = "monthStartDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date monthStartDate,
			@RequestParam(value = "monthEndDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date monthEndDate)
			throws DestinationException {
		try{
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		logger.debug("Inside ConnectController /connect/date?from=" + fromDate
				+ "&to=" + toDate + "GET");
		if (weekStartDate.getTime() == weekEndDate.getTime()
				&& monthStartDate.getTime() == monthEndDate.getTime()) {
			List<ConnectT> connects = connectService
					.searchforConnectsBetweenForUserOrCustomerOrPartner(
							fromDate, toDate, userId, owner, customerId,
							partnerId, false);
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, connects);
		} else {
			DashBoardConnectsResponse dashBoardConnectsResponse = connectService
					.searchDateRangwWithWeekAndMonthCount(fromDate, toDate,
							userId, owner, customerId, partnerId,
							weekStartDate, weekEndDate, monthStartDate,
							monthEndDate);
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, dashBoardConnectsResponse);
		}
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving connects details");
		}

	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertToConnect(
			@RequestBody ConnectT connect) throws DestinationException {
		logger.debug("Connect Insert Request Received /connect POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (connectService.createConnect(connect, false)) {
				status.setStatus(Status.SUCCESS, connect.getConnectId());
				logger.debug("CONNECT CREATED SUCCESS" + connect.getConnectId());
			}
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while inserting connect");
		}
		
	}

	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editConnect(
			@RequestBody ConnectT connect) throws DestinationException {
		logger.debug("Connect Edit Request Received /connect PUT");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (connectService.updateConnect(connect)) {
				status.setStatus(Status.SUCCESS, connect.getConnectId());
			}
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while updating connect");
		}

		
	}

	/**
	 * This controller retrieves all the connects of users under a supervisor
	 * between dates and also gives a count of connects on a weekly and monthly
	 * basis
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param fields
	 * @param view
	 * @param supervisorId
	 * @param weekStartDate
	 * @param weekEndDate
	 * @param monthStartDate
	 * @param monthEndDate
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/team", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getTeamConnects(
			@RequestParam("from") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam("to") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam("supervisorId") String supervisorId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "role", defaultValue = "all") String role,
			@RequestParam(value = "weekStartDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date weekStartDate,
			@RequestParam(value = "weekEndDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date weekEndDate,
			@RequestParam(value = "monthStartDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date monthStartDate,
			@RequestParam(value = "monthEndDate", defaultValue = "01011970") @DateTimeFormat(pattern = "ddMMyyyy") Date monthEndDate)
			throws DestinationException {

		logger.debug("Inside ConnectController /connect/team?from=" + fromDate
				+ "&to=" + toDate + "&supervisorId " + supervisorId + "GET");
		try{
		DashBoardConnectsResponse dashBoardConnectsResponse = null;
		// Calling the service method
		dashBoardConnectsResponse = connectService.getTeamConnects(
				supervisorId, fromDate, toDate, role, weekStartDate,
				weekEndDate, monthStartDate, monthEndDate);
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews(fields, view,
						dashBoardConnectsResponse), HttpStatus.OK);

		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving Team connects details");
		}
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<InputStreamResource> uploadOpportunity(
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		
		logger.debug("Upload request Received : docName ");
		UploadStatusDTO status = null;
		List<UploadServiceErrorDetailsDTO> errorDetailsDTOs = null;
		try {
			String userId=DestinationUtils.getCurrentUserDetails().getUserId();
			status = connectUploadService.saveConnectDocument(file, userId);
			if (status != null) {
				System.out.println(status.isStatusFlag());
				errorDetailsDTOs = status.getListOfErrors();
				for (UploadServiceErrorDetailsDTO err : errorDetailsDTOs) {
					System.out.println(err.getRowNumber());
					System.out.println(err.getMessage());
				}
			}
			logger.debug("UPLOAD SUCCESS - Record Created ");
		
		InputStreamResource excelFile = uploadErrorReport
				.getErrorSheet(errorDetailsDTOs);
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders
				.setContentType(MediaType
						.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		respHeaders.setContentDispositionFormData("attachment",
				"upload_error.xlsx");
		return new ResponseEntity<InputStreamResource>(excelFile, respHeaders,
				HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while uploading connects");
		}
	}

	/**
	 * This controller retrieves all the connects based on the status and FY
	 * 
	 * @param fields
	 * @param view
	 * @param status
	 * @param financialYear
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getAllConnectsForDashboard(
			@RequestParam(value = "fields", defaultValue = "") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "status", defaultValue = "ALL") String status,
			@RequestParam("fy") String financialYear) throws DestinationException {

		List<ConnectT> listOfConnects = null;
		try {
			listOfConnects = connectService.getAllConnectsForDashbaord(status,
					financialYear);
			if (listOfConnects == null) {
				logger.error("NOT_FOUND : No Connects found for the status {} and FY {}", status, financialYear);
				throw new DestinationException(HttpStatus.NOT_FOUND, "No Connects found for the status " + status + " and FY " + financialYear);
			}

			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews(fields, view,
							listOfConnects), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving connect details");
		}
	}

	/**
	 * This controller performs search of connects and searchKeywords based on
	 * name and keyword
	 * 
	 * @param name
	 * @param keyword
	 * @param fields
	 * @param view
	 * @return String
	 * @throws Exception
	 */
	@RequestMapping(value = "/name", method = RequestMethod.GET)
	public @ResponseBody String findConnectNameOrKeyword(
			@RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.debug("Inside ConnectService /name GET");
		List<ConnectNameKeywordSearch> searchResults = null;

		try{
			searchResults = connectService.findConnectNameOrKeywords(name, keyword);
			if ((searchResults == null) || (searchResults.isEmpty())) {
				logger.error("No Results found for name {} and keyword {}", name,
						keyword);
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Results found for name " + name + " and keyword "
								+ keyword);
			}

			return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
					searchResults);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving connects list");
		}
	}

	/**
	 * This Controller used to download the list of connects in excel format
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> downloadConnect(
			@RequestParam("downloadConnects") boolean oppFlag) throws DestinationException {
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		logger.debug("Download request Received : docName ");
		try{
		InputStreamResource excelFile = connectDownloadService
				.getConnects(userId,oppFlag);
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders
				.setContentType(MediaType
						.parseMediaType("application/vnd.ms-excel.sheet.macroEnabled.12"));
		String todaysDate = DateUtils.getCurrentDate();
		String todaysDate_formatted=desiredFormat.format(actualFormat.parse(todaysDate));
		logger.debug("Download Header - Attachment : "
				+ "ConnectDownload_" + todaysDate_formatted + ".xlsm");
		respHeaders.setContentDispositionFormData("attachment",
				"ConnectDownload_" + todaysDate_formatted + ".xlsm");
		logger.debug("Connect Downloaded Successfully ");
		return new ResponseEntity<InputStreamResource>(excelFile, respHeaders,
				HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while downloading connect details");
		}
	}
	
	/**
	 * This Controller retrieves the List of connects for the given connect ids
	 * @param connectIds
	 * @param fields
	 * @param view
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody String findConnectsByIds(
			@RequestParam("ids") List<String> connectIds,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.debug("Inside ConnectController /connect/id=" + connectIds + " GET");
		try{
			List<ConnectT> connectList = connectService.getConnectsByConnetIds(connectIds);
			return ResponseConstructors.filterJsonForFieldAndViews(fields, view, connectList);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving connects list");
		}
	}

}