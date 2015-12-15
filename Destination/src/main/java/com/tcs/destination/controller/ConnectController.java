package com.tcs.destination.controller;

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

	/**
	 * This Method is used to find connection details for the given connection
	 * id.
	 * 
	 * @param connectIds.            
	 * @param fields
	 * @param view
	 * @return connection details for the particular connection id.
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findConnectById(
			@PathVariable("id") String connectId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside ConnectController:Start of retrieving Connects by Connect id");
		ConnectT connect;
		try {
			connect = connectService.findConnectById(connectId);
			logger.info("Inside ConnectController:End of retrieving Connects by Connect id");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, connect);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving connect details");
		}
	}

	/**
	 * This Method is used to find connection details for the given connection name
	 * 
	 * @param connectName
	 * @param page
	 * @param count
	 * @param fields
	 * @param customerId
	 * @param view
	 * @return connection details for the particular connection name.
	 * @throws DestinationException
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
		logger.info("Inside ConnectController: Start of retrieving Connects by Connect name");
		try {
			PaginatedResponse paginatedConnect = connectService
					.searchforConnectsByNameContaining(connectName, customerId,
							page, count);
			logger.info("Inside ConnectController: End of retrieving Connects by Connect name");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, paginatedConnect);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving connects list");
		}
	}

	/**
	 * This Method is used to find connection details within a date range.
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param fields
	 * @param view
	 * @param owner
	 * @param customerId
	 * @param partnerId
	 * @param weekStartDate
	 * @param weekEndDate
	 * @param monthStartDate
	 * @param monthEndDate
	 * @return connection details for the particular connection name.
	 * @throws DestinationException
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
		try {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			logger.info("Inside ConnectController: Start of retrieving Connects by Date range");
			if (weekStartDate.getTime() == weekEndDate.getTime()
					&& monthStartDate.getTime() == monthEndDate.getTime()) {
				List<ConnectT> connects = connectService
						.searchforConnectsBetweenForUserOrCustomerOrPartner(
								fromDate, toDate, userId, owner, customerId,
								partnerId, false);
				logger.info("Inside ConnectController: End of retrieving Connects");
				return ResponseConstructors.filterJsonForFieldAndViews(fields,
						view, connects);
			} else {
				DashBoardConnectsResponse dashBoardConnectsResponse = connectService
						.searchDateRangwWithWeekAndMonthCount(fromDate, toDate,
								userId, owner, customerId, partnerId,
								weekStartDate, weekEndDate, monthStartDate,
								monthEndDate);
				logger.info("Inside ConnectController: End of retrieving Connects by Date range");
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

	/**
	 * This method is used to add a new Connect
	 * 
	 * @param connect
	 * @return ResponseEntity<String>
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertToConnect(
			@RequestBody ConnectT connect) throws DestinationException {
		logger.info("Inside ConnectController: Start of creating Connect");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (connectService.createConnect(connect, false)) {
				status.setStatus(Status.SUCCESS, connect.getConnectId());
				logger.debug("CONNECT CREATED SUCCESS" + connect.getConnectId());
			}
			logger.info("End of creating Connect");
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

	/**
	 * This Method is used to edit the connect details
	 * 
	 * @param connect
	 * @return ResponseEntity<String>
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editConnect(
			@RequestBody ConnectT connect) throws DestinationException {
		logger.info("Inside ConnectController: Start of Edit Connect");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (connectService.updateConnect(connect)) {
				status.setStatus(Status.SUCCESS, connect.getConnectId());
			}
			logger.info("Inside ConnectController: End of Edit Connect");
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
	 * @return ResponseEntity<String>
	 * @throws DestinationException
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
		logger.info("Inside ConnectController: Start of retrieving Team connects");
		try {
			DashBoardConnectsResponse dashBoardConnectsResponse = null;
			// Calling the service method
			dashBoardConnectsResponse = connectService.getTeamConnects(
					supervisorId, fromDate, toDate, role, weekStartDate,
					weekEndDate, monthStartDate, monthEndDate);
			logger.info("Inside ConnectController: End of retrieving Team connects");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews(fields,
							view, dashBoardConnectsResponse), HttpStatus.OK);

		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving Team connects details");
		}
	}

	/**
	 * This Controller uploads the connect to the database.
	 * 
	 * @param file
	 * @param fields
	 * @param view
	 * @return ResponseEntity<InputStreamResource>
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<InputStreamResource> uploadOpportunity(
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside connect controller : Start of Connect upload");
		UploadStatusDTO status = null;
		List<UploadServiceErrorDetailsDTO> errorDetailsDTOs = null;
		try {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			status = connectUploadService.saveConnectDocument(file, userId);
			if (status != null) {
				errorDetailsDTOs = status.getListOfErrors();
				for (UploadServiceErrorDetailsDTO err : errorDetailsDTOs) {
					logger.debug(err.getRowNumber().toString());
					logger.debug(err.getMessage());
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
			logger.info("Inside ConnectController: End of Connect upload");
			return new ResponseEntity<InputStreamResource>(excelFile,
					respHeaders, HttpStatus.OK);
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
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getAllConnectsForDashboard(
			@RequestParam(value = "fields", defaultValue = "") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "status", defaultValue = "ALL") String status,
			@RequestParam("fy") String financialYear)
			throws DestinationException {
		logger.info("Inside ConnectController: Start of retrieving all Connects for Dashboard");
		List<ConnectT> listOfConnects = null;
		try {
			listOfConnects = connectService.getAllConnectsForDashbaord(status,
					financialYear);
			if (listOfConnects == null) {
				logger.error(
						"NOT_FOUND : No Connects found for the status {} and FY {}",
						status, financialYear);
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Connects found for the status " + status
								+ " and FY " + financialYear);
			}
			logger.info("Inside ConnectController: End of retrieving all Connects for Dashboard");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews(fields,
							view, listOfConnects), HttpStatus.OK);
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
	 * @return 
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/name", method = RequestMethod.GET)
	public @ResponseBody String findConnectNameOrKeyword(
			@RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside ConnectController: Start of Connect Name or keyword search");
		List<ConnectNameKeywordSearch> searchResults = null;

		try {
			searchResults = connectService.findConnectNameOrKeywords(name,
					keyword);
			if ((searchResults == null) || (searchResults.isEmpty())) {
				logger.error("No Results found for name {} and keyword {}",
						name, keyword);
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Results found for name " + name + " and keyword "
								+ keyword);
			}
			logger.info("Inside ConnectController: End of Connect Name or keyword search");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, searchResults);
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
	 * 
	 * @param oppFlag
	 * @return ResponseEntity<InputStreamResource>
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> downloadConnect(
			@RequestParam("downloadConnects") boolean oppFlag)
			throws DestinationException {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		logger.info("Inside ConnectController: Start of Connect Download");
		try {
			InputStreamResource excelFile = connectDownloadService.getConnects(
					userId, oppFlag);
			HttpHeaders respHeaders = new HttpHeaders();
			respHeaders
					.setContentType(MediaType
							.parseMediaType("application/vnd.ms-excel.sheet.macroEnabled.12"));
			String todaysDate_formatted = DateUtils.getCurrentDateInDesiredFormat();
			logger.debug("Download Header - Attachment : " + "ConnectDownload_"
					+ todaysDate_formatted + ".xlsm");
			respHeaders.setContentDispositionFormData("attachment",
					"ConnectDownload_" + todaysDate_formatted + ".xlsm");
			logger.info("Inside ConnectController: End of Connect Download");
			return new ResponseEntity<InputStreamResource>(excelFile,
					respHeaders, HttpStatus.OK);
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
	 * 
	 * @param connectIds
	 * @param fields
	 * @param view
	 * @return 
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody String findConnectsByIds(
			@RequestParam("ids") List<String> connectIds,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside ConnectController: Start of retrieving Connects by list of connect id's");
		try {
			List<ConnectT> connectList = connectService
					.getConnectsByConnetIds(connectIds);
			logger.info("Inside ConnectController: End of retrieving Connects by list of connect id's");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, connectList);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving connects list");
		}
	}

}