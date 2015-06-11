package com.tcs.destination.controller;

import java.util.ArrayList;
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
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.ApplicationSettingsService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/appsettings")
public class ApplicationSettingsController {

	private static final Logger logger = LoggerFactory
			.getLogger(ApplicationSettingsController.class);

	@Autowired
	ApplicationSettingsService applicationSettingsService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAll(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside ApplicationSettingsController /appsettings GET");
		List<ApplicationSettingsT> applicationSettingsTs = (List<ApplicationSettingsT>) applicationSettingsService
				.findAll();
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				applicationSettingsTs);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editSettings(
			@RequestBody List<ApplicationSettingsT> applicationSettingsTs)
			throws Exception {
		Status status = new Status();
		try {
			applicationSettingsService.edit(applicationSettingsTs);
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		status.setStatus(Status.SUCCESS,
				"Application Settings updated successfully");
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews("all", "",
						status), HttpStatus.OK);
	}

}
