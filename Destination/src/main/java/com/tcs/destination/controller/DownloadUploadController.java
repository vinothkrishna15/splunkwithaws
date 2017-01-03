package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.dto.ImageUploadDTO;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.DownloadUploadService;

/**
 * Controller to handle city based search requests
 * 
 */
@RestController
@RequestMapping("/image")
public class DownloadUploadController {

	private static final Logger logger = LoggerFactory
			.getLogger(DownloadUploadController.class);

	@Autowired
	DownloadUploadService imgService;
	
	@RequestMapping(value = "/logo", method = RequestMethod.GET, produces={"image/jpg", "image/jpeg", "image/png", "image/bmp", "image/gif"})
	public @ResponseBody byte[] getLogo(
			@RequestParam("type") String type,
			@RequestParam("id") String id)	throws DestinationException {
		logger.info("Inside DownloadUploadController: getLogo");
		try {
			byte[] logoStream = imgService.getLogo(type, id);

			logger.info("Inside DownloadUploadController: End of getLogo");
			return logoStream;
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while downloading logo");
		}
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody String putLogo(@RequestBody ImageUploadDTO uploadDTO)	throws DestinationException {
		logger.info("Inside DownloadUploadController: getLogo");
		try {
			imgService.putLogo(uploadDTO);

			logger.info("Inside DownloadUploadController: End of putLogo");
			return "Success";
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while uploading logo");
		}
	}

}
