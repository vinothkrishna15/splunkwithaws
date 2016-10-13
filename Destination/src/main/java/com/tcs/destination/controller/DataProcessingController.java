package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.Status;
import com.tcs.destination.enums.RequestType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.DataProcessingService;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * Controller to handle batch requests.
 * 
 */
@RestController
@RequestMapping("/batch")
public class DataProcessingController {

	private static final Logger logger = LoggerFactory
			.getLogger(DataProcessingController.class);

	@Autowired
	DataProcessingService service;

	/**
	 * This method is used to request upload to database
	 * @param file
	 * @param type
	 * @param fields
	 * @param view
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> batchUploadRequest(
			@RequestParam("file") MultipartFile file,
			@RequestParam("type") int type,
			@RequestParam(value = "deleteFrom", defaultValue = "") String deleteFrom,
			@RequestParam(value = "deleteTo", defaultValue = "") String deleteTo,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {

		logger.info("Inside Data Processing controller: Start of upload");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			//updated for product master changes
			if(isUploadRequestType(type))   {
				status = service.saveUploadRequest(file, type,deleteFrom,deleteTo);
				logger.info("UPLOAD SUCCESS - Record Created ");
			} else {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid upload request type.");
			}

		} catch (DestinationException e) {
			logger.error("Destination Exception" + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		logger.info("Inside Data Processing controller: End of upload");
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews(fields, view,
						status), HttpStatus.OK);
	}

	/**
	 * This method is used to request download from database
	 * @param type
	 * @param fields
	 * @param view
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/download", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> batchDownloadRequest(
			@RequestParam("type") int type,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {

		logger.info("Inside Data Processing controller: Start of download");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			//updated for product master
			if (isDownloadRequestType(type)) {
				status = service.saveDownloadRequest(type);
				logger.debug("DOWNLOAD SUCCESS - Record Created ");
			} else {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid download request type.");
			}

		} catch (DestinationException e) {
			logger.error("Destination Exception" + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		logger.info("Inside Data Processing controller: End of download");
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews(fields, view,
						status), HttpStatus.OK);
	}
	
	//added for product master batch
	private boolean isUploadRequestType(int type) {
		boolean isUploadRequest = false;
		if ((type > 0 && type < 10) || type == RequestType.PARTNER_MASTER_UPLOAD.getType()
				|| type == RequestType.PRODUCT_UPLOAD.getType() ||
				type == RequestType.PRODUCT_CONTACT_UPLOAD.getType()
				|| type == RequestType.RGS_UPLOAD.getType()) {			
			isUploadRequest = true;
		}
		return isUploadRequest;
	}
	
	private boolean isDownloadRequestType(int type) {
		boolean isDownloadRequest = false;
		 if ((type > 9 && type < 19) || type == RequestType.PARTNER_MASTER_DOWNLOAD.getType()
					|| type == RequestType.PRODUCT_DOWNLOAD.getType() ||
							type == RequestType.PRODUCT_CONTACT_DOWNLOAD.getType() ||
							 type == RequestType.RGS_DOWNLOAD.getType()) { 
			isDownloadRequest = true;
		}
		return isDownloadRequest;
	}

}
