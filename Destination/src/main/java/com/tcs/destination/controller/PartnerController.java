package com.tcs.destination.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.PartnerDownloadService;
import com.tcs.destination.service.PartnerService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.service.PartnerUploadService;

@RestController
@RequestMapping("/partner")
public class PartnerController {

	private static final Logger logger = LoggerFactory.getLogger(PartnerController.class);
	
	@Autowired
	PartnerService partnerService;
	
	@Autowired
	PartnerUploadService partnerUploadService;
	
	@Autowired
	UploadErrorReport uploadErrorReport;
	
	@Autowired
	PartnerDownloadService partnerDownloadService;
	
	private static final DateFormat actualFormat = new SimpleDateFormat("dd-MMM-yyyy");
	private static final DateFormat desiredFormat = new SimpleDateFormat("MM/dd/yyyy");


	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@PathVariable("id") String partnerid,
			@RequestParam(value = "currency", defaultValue = "USD") List<String> currency,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		logger.debug("Inside PartnerController /partner/id="+partnerid+" GET");
		PartnerMasterT partner = partnerService.findById(partnerid, currency);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, partner);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findByNameContaining(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "nameWith", defaultValue = "") String nameWith,
			@RequestParam(value = "startsWith", defaultValue = "") String startsWith,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception {
		logger.debug("Inside PartnerController /partner?nameWith="+nameWith+" GET");
		PaginatedResponse partners = null;
		
		if (!nameWith.isEmpty()) {
			partners = partnerService.findByNameContaining(nameWith,page,count);
		} else if (!startsWith.isEmpty()) {
			partners = partnerService.findByNameStarting(startsWith,page,count);
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Either nameWith / startsWith is required");
		}
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, partners);
	}
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> uploadPartner(
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
//		UploadStatusDTO status = null;
		List<UploadServiceErrorDetailsDTO> errorDetailsDTOs = null;
		
		 UploadStatusDTO status = partnerUploadService.upload(file, userId);
			if (status != null) {
				errorDetailsDTOs = status.getListOfErrors();
				for (UploadServiceErrorDetailsDTO up : status.getListOfErrors()) {
					System.out.println(up.getRowNumber() + "   " + up.getMessage());
				}
			}
			InputStreamResource excelFile = uploadErrorReport.getErrorSheet(errorDetailsDTOs);
			HttpHeaders respHeaders = new HttpHeaders();
//			respHeaders.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
//			respHeaders.setContentDispositionFormData("attachment","upload_error.xls");
			respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
			respHeaders.setContentDispositionFormData("attachment","upload_error.xlsx");
	        return new ResponseEntity<String>(ResponseConstructors.filterJsonForFieldAndViews(fields, view,status), HttpStatus.OK);

}
	
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> downloadPartner(
			@RequestParam("downloadPartners") boolean oppFlag) throws Exception 
	{
		logger.info("Download request Received : docName ");
		InputStreamResource excelFile = partnerDownloadService.getPartners(oppFlag);
		HttpHeaders respHeaders = new HttpHeaders();
		String todaysDate = DateUtils.getCurrentDate();
		String todaysDate_formatted=desiredFormat.format(actualFormat.parse(todaysDate));
		respHeaders.setContentType(MediaType.parseMediaType("application/octet-stream"));
		
		logger.info("Download Header - Attachment : " + "PartnerMaster&ContactDownload_" + todaysDate_formatted + ".xlsm");
		respHeaders.setContentDispositionFormData("attachment", "PartnerMaster&ContactDownload_" + todaysDate_formatted + ".xlsm");
		logger.info("PartnerMaster & Contact Downloaded Successfully ");
		return new ResponseEntity<InputStreamResource>(excelFile, respHeaders, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/contactDownload", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> downloadPartnerContacts(
			@RequestParam("downloadPartnerContacts") boolean oppFlag) throws Exception 
	{
		logger.info("Download request Received : docName ");
		InputStreamResource excelFile = partnerDownloadService.getPartnerContacts(oppFlag);
		HttpHeaders respHeaders = new HttpHeaders();
		String todaysDate = DateUtils.getCurrentDate();
		String todaysDate_formatted=desiredFormat.format(actualFormat.parse(todaysDate));
		respHeaders.setContentType(MediaType.parseMediaType("application/octet-stream"));
		
		logger.info("Download Header - Attachment : " + "PartnerContactDownload_" + todaysDate_formatted + ".xlsm");
		respHeaders.setContentDispositionFormData("attachment", "PartnerContactDownload_" + todaysDate_formatted + ".xlsm");
		logger.info("Partner Contact Downloaded Successfully ");
		return new ResponseEntity<InputStreamResource>(excelFile, respHeaders, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody String advancedSearch(
			@RequestParam(value = "nameWith", defaultValue = "") String name,
			@RequestParam(value = "geography", defaultValue = "") List<String> geography,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside PartnerController /partner/search?name=" + name
				+ "&geograph=" + geography + " GET");
		PaginatedResponse paginatedResponse = partnerService.search(name,
				geography, page, count);

		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				paginatedResponse);
	}
}
