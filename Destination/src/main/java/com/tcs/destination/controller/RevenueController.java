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

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.BeaconCustomerUploadService;
import com.tcs.destination.service.CustomerService;
import com.tcs.destination.service.CustomerUploadService;
import com.tcs.destination.service.RevenueUploadService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.ResponseConstructors;

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
	UploadErrorReport uploadErrorReport;
	/**
	 * This controller uploads the Revenue Deatils to the database
	 * @param userId
	 * @param file
	 * @param fields
	 * @param view
	 * @return ResponseEntity<InputStreamResource>
	 * @throws Exception
	 */
	@RequestMapping(value = "/uploadRevenue", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<InputStreamResource> uploadRevenueMapping(
			@RequestParam("userId") String userId,
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {

		List<UploadServiceErrorDetailsDTO> errorDetailsDTOs = null;
		logger.info("inside revenue controller");
		UploadStatusDTO status = revenueUploadService.upload(file, userId);
		if (status != null) {
			System.out.println(status.isStatusFlag());
			errorDetailsDTOs = status.getListOfErrors();
			for(UploadServiceErrorDetailsDTO err : errorDetailsDTOs){
				System.out.println(err.getRowNumber());
				    System.out.println(err.getMessage());
				}
		}
		
		InputStreamResource excelFile = uploadErrorReport.getErrorSheet(errorDetailsDTOs);
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		respHeaders.setContentDispositionFormData("attachment","revenue_mapping_upload_error.xlsx");
		return new ResponseEntity<InputStreamResource>(excelFile, respHeaders,HttpStatus.OK);
	}
	

}