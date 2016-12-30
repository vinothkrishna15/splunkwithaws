package com.tcs.destination.controller;

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

import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.RGSDownloadService;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.PropertyUtil;

/**
 * This controller handles the rgs module
 * 
 * @author TCS
 */
@RestController
@RequestMapping("/rgs")
public class RGSDetailsController {

	private static final Logger logger = LoggerFactory
			.getLogger(RGSDetailsController.class);

	@Autowired
	RGSDownloadService rgsDownloadService;

	

	/**
	 * This method is used to download the rgs details in excel format
	 * 
	 * @param oppFlag
	 * @return excelFile
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> downloadRGS
	(@RequestParam(value = "downloadRGS", defaultValue = "false") boolean dataFlag)
					throws DestinationException {
		try {
			logger.info("Inside RGSDetailsController: Start of /rgs/download GET");
			InputStreamResource excelFile = rgsDownloadService.getRGSDetails(dataFlag);
			HttpHeaders respHeaders = new HttpHeaders();
			String todaysDate_formatted = DateUtils
					.getCurrentDateInDesiredFormat();
			respHeaders.setContentType(MediaType
					.parseMediaType("application/octet-stream"));
			String environmentName = PropertyUtil
					.getProperty("environment.name");
			String repName = environmentName
					+ "_RGSDownload_" + todaysDate_formatted
					+ ".xlsx";  
			respHeaders.add("reportName", repName);
			respHeaders.setContentDispositionFormData("attachment", repName);
			logger.info("Inside RGSDetailsController: End of /rgs/download GET");
			return new ResponseEntity<InputStreamResource>(excelFile,
					respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			logger.error("Destination Exception" + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while downloading rgs details");
		}

	}
}
