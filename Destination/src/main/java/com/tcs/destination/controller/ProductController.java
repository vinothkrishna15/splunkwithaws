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
import com.tcs.destination.service.ProductDownloadService;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.PropertyUtil;

/**
 * This controller handles the product module
 * 
 * @author TCS
 *
 */
@RestController
@RequestMapping("/product")
public class ProductController {

	private static final Logger logger = LoggerFactory
			.getLogger(ProductController.class);
	
	@Autowired
	ProductDownloadService productDownloadService;

	/**
	 * This method is used to download the product details in excel format
	 * 
	 * @param oppFlag
	 * @return excelFile
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> downloadPartner(
			@RequestParam("downloadProducts") boolean oppFlag)
					throws DestinationException {
		try {
			logger.info("Inside ProductsController: Start of /product/download GET");
			InputStreamResource excelFile = productDownloadService
					.getProducts(oppFlag);
			HttpHeaders respHeaders = new HttpHeaders();
			String todaysDate_formatted = DateUtils
					.getCurrentDateInDesiredFormat();
			respHeaders.setContentType(MediaType
					.parseMediaType("application/octet-stream"));
			String environmentName = PropertyUtil
					.getProperty("environment.name");
			String repName = environmentName
					+ "_ProductMasterDownload_" + todaysDate_formatted
					+ ".xlsm";
			respHeaders.add("reportName", repName);
			respHeaders.setContentDispositionFormData("attachment", repName);
			logger.info("Inside ProductsController: End of /product/download GET");
			return new ResponseEntity<InputStreamResource>(excelFile,
					respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			logger.error("Destination Exception" + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while downloading products details");
		}

	}
	
	/**
	 * This method is used to download the product contact details in excel
	 * format
	 * 
	 * @param oppFlag
	 * @return excelFile
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/productContactDownload", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<InputStreamResource> downloadProductContacts(
			@RequestParam("downloadProductContacts") boolean oppFlag)
					throws DestinationException {
		logger.info("Inside ProductController: Start of /product /contactDownload GET");
		try {
			InputStreamResource excelFile = productDownloadService.getProductContacts(oppFlag);
			HttpHeaders respHeaders = new HttpHeaders();
			String todaysDate_formatted = DateUtils
					.getCurrentDateInDesiredFormat();
			respHeaders.setContentType(MediaType
					.parseMediaType("application/octet-stream"));
			String environmentName = PropertyUtil
					.getProperty("environment.name");
			String repName = environmentName + "_ProductContactDownload_"
					+ todaysDate_formatted + ".xlsm";
			respHeaders.add("reportName", repName);
			respHeaders.setContentDispositionFormData("attachment", repName);
			logger.info("Inside ProductController: End of /product/contactDownload GET");
			return new ResponseEntity<InputStreamResource>(excelFile,
					respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			logger.error("Destination Exception" + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while downloading product contact details");
		}
	}
}
