package com.tcs.destination.controller;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
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

import com.tcs.destination.bean.PageDTO;
import com.tcs.destination.bean.SearchResultDTO;
import com.tcs.destination.enums.SmartSearchType;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.PartnerDownloadService;
import com.tcs.destination.service.PartnerService;
import com.tcs.destination.service.PartnerUploadService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.PropertyUtil;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * This controller handles the partner module
 * 
 * @author TCS
 *
 */
@RestController
@RequestMapping("/partner")
public class PartnerController {

	private static final Logger logger = LoggerFactory
			.getLogger(PartnerController.class);

	@Autowired
	PartnerService partnerService;

	@Autowired
	PartnerUploadService partnerUploadService;

	@Autowired
	UploadErrorReport uploadErrorReport;

	@Autowired
	PartnerDownloadService partnerDownloadService;

	/**
	 * This method is used to retrieve the partner details by partner id
	 * 
	 * @param partnerid
	 * @param currency
	 * @param fields
	 * @param view
	 * @return partner
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@PathVariable("id") String partnerid,
			@RequestParam(value = "currency", defaultValue = "USD") List<String> currency,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
					throws DestinationException {
		try {
			logger.info("Inside PartnerController: Start of /partner/id="
					+ partnerid + " GET");

			PartnerMasterT partner = partnerService.findById(partnerid,
					currency);
			logger.info("Inside PartnerController: End of /partner/id="
					+ partnerid + " GET");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, partner);
		} catch (DestinationException e) {
			logger.error("Destination Exception" + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving partner details");
		}
	}

	/**
	 * This method is used to retrieve the partner details by the given name
	 * with
	 * 
	 * @param page
	 * @param count
	 * @param nameWith
	 * @param startsWith
	 * @param fields
	 * @param view
	 * @return partners
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findByNameContaining(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "nameWith", defaultValue = "") String nameWith,
			@RequestParam(value = "startsWith", defaultValue = "") String startsWith,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
					throws DestinationException {
		logger.info("Inside PartnerController: Start of /partner?nameWith="
				+ nameWith + " GET");
		PaginatedResponse partners = null;
		try {
			if (!nameWith.isEmpty()) {
				partners = partnerService.findByNameContaining(nameWith, page,
						count);
			} else if (!startsWith.isEmpty()) {
				partners = partnerService.findByNameStarting(startsWith, page,
						count);
			} else {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Either nameWith / startsWith is required");
			}
			logger.info("Inside PartnerController: End of /partner?nameWith="
					+ nameWith + " GET");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, partners);
		} catch (DestinationException e) {
			logger.error("Destination Exception" + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving partner details");
		}
	}

	/**
	 * This method is used to retrieves the group partner name for the given
	 * name with.
	 * 
	 * @param nameWith
	 * @param fields
	 * @param view
	 * @return customer
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/group", method = RequestMethod.GET)
	public @ResponseBody String findByGroupCustomerName(
			@RequestParam("nameWith") String nameWith,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside PartnerController: Start of retrieving the Group partner names");
		String response = null;
		Set<String> groupPartnerNamesSet;
		try {
			groupPartnerNamesSet =  partnerService
				.findByGroupPartnerName(nameWith);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, groupPartnerNamesSet);
			logger.info("Inside PartnerController: Start of retrieving the Group partner names");
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Group partner name for "
							+ nameWith);
		}
		return response;
	}

	/**
	 * This method is used to upload the partner details from the excel to the
	 * database
	 * 
	 * @param file
	 * @param fields
	 * @param view
	 * @return status
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<InputStreamResource> uploadPartner(
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
					throws DestinationException {
		try {
			logger.info("Inside PartnerController: Start of /partner/upload POST");
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			List<UploadServiceErrorDetailsDTO> errorDetailsDTOs = null;

			UploadStatusDTO status = partnerUploadService.upload(file, userId);
			if (status != null) {
				errorDetailsDTOs = status.getListOfErrors();
				for (UploadServiceErrorDetailsDTO up : status.getListOfErrors()) {
					logger.debug(up.getRowNumber() + "   " + up.getMessage());
				}
			}
			InputStreamResource excelFile = uploadErrorReport
					.getErrorSheet(errorDetailsDTOs);
			HttpHeaders respHeaders = new HttpHeaders();
			respHeaders
			.setContentType(MediaType
					.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
			respHeaders.setContentDispositionFormData("attachment",
					"upload_error.xlsx");
			logger.info("Inside PartnerController: End of /partner/upload POST");
			return new ResponseEntity<InputStreamResource>(excelFile,
					respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			logger.error("Destination Exception" + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while uploading partner details");
		}
	}

	/**
	 * This method is used to download the partner details in excel format
	 * 
	 * @param oppFlag
	 * @return excelFile
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> downloadPartner(
			@RequestParam("downloadPartners") boolean oppFlag)
					throws DestinationException {
		try {
			logger.info("Inside PartnerController: Start of /partner/download GET");
			InputStreamResource excelFile = partnerDownloadService
					.getPartners(oppFlag);
			HttpHeaders respHeaders = new HttpHeaders();
			String todaysDate_formatted = DateUtils
					.getCurrentDateInDesiredFormat();
			respHeaders.setContentType(MediaType
					.parseMediaType("application/octet-stream"));
			String environmentName = PropertyUtil
					.getProperty("environment.name");
			String repName = environmentName
					+ "_PartnerMaster&ContactDownload_" + todaysDate_formatted
					+ ".xlsm";
			respHeaders.add("reportName", repName);
			respHeaders.setContentDispositionFormData("attachment", repName);
			logger.info("Inside PartnerController: End of /partner/download GET");
			return new ResponseEntity<InputStreamResource>(excelFile,
					respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			logger.error("Destination Exception" + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while downloading partner details");
		}

	}

	/**
	 * This method is used to download the partner contact details in excel
	 * format
	 * 
	 * @param oppFlag
	 * @return excelFile
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/contactDownload", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> downloadPartnerContacts(
			@RequestParam("downloadPartnerContacts") boolean oppFlag)
					throws DestinationException {
		logger.info("Inside PartnerController: Start of /partner/contactDownload GET");
		try {
			InputStreamResource excelFile = partnerDownloadService
					.getPartnerContacts(oppFlag);
			HttpHeaders respHeaders = new HttpHeaders();
			String todaysDate_formatted = DateUtils
					.getCurrentDateInDesiredFormat();
			respHeaders.setContentType(MediaType
					.parseMediaType("application/octet-stream"));
			String environmentName = PropertyUtil
					.getProperty("environment.name");
			String repName = environmentName + "_PartnerContactDownload_"
					+ todaysDate_formatted + ".xlsm";
			respHeaders.add("reportName", repName);
			respHeaders.setContentDispositionFormData("attachment", repName);
			logger.info("Inside PartnerController: End of /partner/contactDownload GET");
			return new ResponseEntity<InputStreamResource>(excelFile,
					respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			logger.error("Destination Exception" + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while downloading partner contact details");
		}
	}

	/**
	 * This method gives the functionality for partner advanced search
	 * 
	 * @param name
	 * @param geography
	 * @param page
	 * @param count
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody String advancedSearch(
			@RequestParam(value = "nameWith", defaultValue = "") String name,
			@RequestParam(value = "geography", defaultValue = "") List<String> geography,
			@RequestParam(value = "inactive", defaultValue = "false") boolean inactive,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
					throws DestinationException {

		logger.info("Inside PartnerController: Start of /partner/search?name="
				+ name + "&geograph=" + geography + " GET");
		try {
			PaginatedResponse paginatedResponse = partnerService.search(name,
					geography, inactive, page, count);
			logger.info("Inside PartnerController: End of /partner/search?name="
					+ name + "&geograph=" + geography + " GET");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, paginatedResponse);
		} catch (DestinationException e) {
			logger.error("Destination Exception" + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving partner details");
		}
	}

	/**
	 * This method is used to edit the partner master details and it is
	 * accessible only by system admins and strategic group admin
	 * 
	 * @param partnerMaster
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> editPartner(
			@RequestBody PartnerMasterT partnerMaster)
					throws DestinationException {
		logger.info("Inside PartnerController: Start of Edit Partner");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (partnerService.updatePartner(partnerMaster)) {
				status.setStatus(
						Status.SUCCESS,
						"Partner Details updated successfully "
								+ partnerMaster.getPartnerId());
			}
			logger.info("Inside PartnerController: End of Edit Partner");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while editing partner");
		}

	}
	
	/**
	 * Service to fetch the partner related information based on search type and the search keyword 
	 * @param searchType - category type
	 * @param term - keyword
	 * @param getAll - true, to retrieve entire result, false to filter the result to only 3 records.(<b>default:false</b>)
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/search/smart", method = RequestMethod.GET)
	public @ResponseBody String smartSearch(
			@RequestParam("searchType") String searchType,
			@RequestParam("term") String term,
			@RequestParam(value = "getAll", defaultValue = "false") boolean getAll,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "view", defaultValue = "") String view)
					throws DestinationException {
		logger.info("Inside PartnerController: smart search by search term");
		try {
			PageDTO<SearchResultDTO<PartnerMasterT>> res = partnerService.smartSearch(SmartSearchType.get(searchType), term, getAll, page, count);
			logger.info("Inside PartnerController: End - smart search by search term");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, res, !getAll);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Error on Partner smartSearch", e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving Partners list");
		}
		
	}
}
