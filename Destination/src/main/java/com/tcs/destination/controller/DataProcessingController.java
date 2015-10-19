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
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.DataProcessingService;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * Controller to handle contact details search requests.
 * 
 */
@RestController
@RequestMapping("/batch")
public class DataProcessingController {

	private static final Logger logger = LoggerFactory
			.getLogger(DataProcessingController.class);
	
	@Autowired
	DataProcessingService service;

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> batchUploadRequest(
	    @RequestParam("file") MultipartFile file,
	    @RequestParam("userId") String userId,
	    @RequestParam("type") int type,
	    @RequestParam(value = "fields", defaultValue = "all") String fields,
	    @RequestParam(value = "view", defaultValue = "") String view)
	    throws Exception {
		
		logger.debug("Upload request Received: type = {}, docName = {} ", type, file.getOriginalFilename());
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (type > 0 && type < 10) {
				 status = service.saveUploadRequest(file, userId, type);
				 logger.debug("UPLOAD SUCCESS - Record Created ");
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
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews(fields, view,
						status), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/download", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> batchDownloadRequest(
	    @RequestParam("userId") String userId,
	    @RequestParam("type") int type,
	    @RequestParam(value = "fields", defaultValue = "all") String fields,
	    @RequestParam(value = "view", defaultValue = "") String view)
	    throws Exception {
		
		logger.debug("Download request Received: type = {}", type);
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (type > 9 && type < 19) {
				 status = service.saveDownloadRequest(userId, type);
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
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews(fields, view,
						status), HttpStatus.OK);
	}
	
}
	
