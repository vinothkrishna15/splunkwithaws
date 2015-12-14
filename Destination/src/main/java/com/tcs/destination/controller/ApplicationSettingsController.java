package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.ApplicationSettingsT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.ApplicationSettingsService;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * Controller to handle application configurations.
 * 
 */
@RestController
@RequestMapping("/appsettings")
public class ApplicationSettingsController {

	private static final Logger logger = LoggerFactory
			.getLogger(ApplicationSettingsController.class);

	@Autowired
	ApplicationSettingsService applicationSettingsService;

	/**
	 * This retrieves the application settings
	 * 
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAll(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside ApplicationSettingsController : Start of retrieving Application settings");
		try {
			List<ApplicationSettingsT> applicationSettingsTs = (List<ApplicationSettingsT>) applicationSettingsService
					.findAll();
			logger.info("Inside ApplicationSettingsController : End of retrieving application settings");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, applicationSettingsTs);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the application settings");
		}
	}
	
	/**
	 * This edits the application settings.
	 * 
	 * @param applicationSettingsTs
	 * @return
	 * @throws DestinationException
	 */

	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editSettings(
			@RequestBody List<ApplicationSettingsT> applicationSettingsTs)
			throws DestinationException {
		logger.info("Inside ApplicationSettingsController : Start of Edit Application settings ");
		Status status = new Status();
		try {
			applicationSettingsService.edit(applicationSettingsTs);
			status.setStatus(Status.SUCCESS,
					"Application Settings updated successfully");
			logger.info("Inside ApplicationSettingsController : End of Edit Application settings");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in editing the application settings");
		}
	}

}
