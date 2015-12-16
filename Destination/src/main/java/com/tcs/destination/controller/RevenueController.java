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
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.ActualRevenueDataUploadService;
import com.tcs.destination.service.RevenueDownloadService;
import com.tcs.destination.service.RevenueUploadService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;

/**
 * Controller to handle Revenue module related requests.
 * 
 */
@RestController
@RequestMapping("/revenue")
public class RevenueController {

	private static final Logger logger = LoggerFactory
			.getLogger(RevenueController.class);

	@Autowired
	RevenueUploadService revenueUploadService;

	@Autowired
	ActualRevenueDataUploadService actualRevenueDataUplaodService;

	@Autowired
	RevenueDownloadService revenueDownloadService;

	@Autowired
	UploadErrorReport uploadErrorReport;

	/**
	 * This controller uploads the Revenue Details to the database
	 * 
	 * @param userId
	 * @param file
	 * @param fields
	 * @param view
	 * @return ResponseEntity<InputStreamResource>
	 * @throws Exception
	 */
	@RequestMapping(value = "/uploadRevenue", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<InputStreamResource> uploadRevenueMapping(
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside RevenueController / Start of upload revenue mapping");
		String userId= DestinationUtils.getCurrentUserDetails().getUserId();
		List<UploadServiceErrorDetailsDTO> errorDetailsDTOs = null;
		try {
			UploadStatusDTO status = revenueUploadService.upload(file, userId);
			if (status != null) {
				errorDetailsDTOs = status.getListOfErrors();
			}
			InputStreamResource excelFile = uploadErrorReport
					.getErrorSheet(errorDetailsDTOs);
			HttpHeaders respHeaders = new HttpHeaders();
			respHeaders
					.setContentType(MediaType
							.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
			respHeaders.setContentDispositionFormData("attachment",
					"revenue_mapping_upload_error.xlsx");
			logger.info("Inside RevenueController / End of upload revenue mapping");
			return new ResponseEntity<InputStreamResource>(excelFile,
					respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while uploading the revenue mapping");
		}
	}

	/**
	 * This controller uploads the ActualRevenue Details to the database
	 * 
	 * @param userId
	 * @param file
	 * @param fields
	 * @param view
	 * @return ResponseEntity<InputStreamResource>
	 * @throws Exception
	 */
	@RequestMapping(value = "/uploadActualRevenueData", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<InputStreamResource> uploadActualRevenueData(
			@RequestParam("userId") String userId,
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside RevenueController / Start of upload actual revenue data");
		List<UploadServiceErrorDetailsDTO> errorDetailsDTOs = null;
		try {
			UploadStatusDTO status = actualRevenueDataUplaodService.upload(
					file, userId);
			if (status != null) {
				errorDetailsDTOs = status.getListOfErrors();
			}
			InputStreamResource excelFile = uploadErrorReport
					.getErrorSheet(errorDetailsDTOs);
			HttpHeaders respHeaders = new HttpHeaders();
			respHeaders
					.setContentType(MediaType
							.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
			respHeaders.setContentDispositionFormData("attachment",
					"actual_revenue_data_mapping_upload_error.xlsx");
			logger.info("Inside RevenueController / End of upload actual revenue data");
			return new ResponseEntity<InputStreamResource>(excelFile,
					respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while uploading the actual revenue data");
		}
	}

	/**
	 * @param oppFlag
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadActualRevenueTemplate(
			@RequestParam("downloadActualRevenues") boolean oppFlag,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside RevenueController / Start of actual revenue download");
		HttpHeaders respHeaders = null;
		InputStreamResource ActualRevenueDownloadExcel = null;
		try {
			ActualRevenueDownloadExcel = revenueDownloadService
					.getActualRevenueData(oppFlag);
			respHeaders = new HttpHeaders();
			String todaysDate_formatted = DateUtils.getCurrentDateInDesiredFormat();
			String repName = "ActualRevenueDownload_" + todaysDate_formatted + ".xlsm";
			respHeaders.add("reportName", repName);
			respHeaders.setContentDispositionFormData("attachment",repName);
			respHeaders.setContentType(MediaType
					.parseMediaType("application/octet-stream"));
			logger.info("Inside RevenueController / End of actual revenue download ");
			return new ResponseEntity<InputStreamResource>(
					ActualRevenueDownloadExcel, respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while downloading the actual revenue data");
		}
	}
}