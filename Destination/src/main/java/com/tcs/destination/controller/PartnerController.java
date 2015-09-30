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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.OpportunityService;
import com.tcs.destination.service.PartnerService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.ResponseConstructors;
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


	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@PathVariable("id") String partnerid,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		logger.debug("Inside PartnerController /partner/id="+partnerid+" GET");
		PartnerMasterT partner = partnerService.findById(partnerid);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, partner);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findByNameContaining(
			@RequestParam(value = "nameWith", defaultValue = "") String nameWith,
			@RequestParam(value = "startsWith", defaultValue = "") String startsWith,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception {
		logger.debug("Inside PartnerController /partner?nameWith="+nameWith+" GET");
		List<PartnerMasterT> partners = null;
		
		if (!nameWith.isEmpty()) {
			partners = partnerService.findByNameContaining(nameWith);
		} else if (!startsWith.isEmpty()) {
			partners = partnerService.findByNameStarting(startsWith);
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Either nameWith / startsWith is required");
		}
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view, partners);
	}
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> uploadPartner(
			@RequestParam("userId") String userId,
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
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
}
