package com.tcs.destination.controller;

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

import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UserGeneralSettingsT;
import com.tcs.destination.service.UserGeneralSettingsService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/userGeneral")
public class UserGeneralSettingsController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserGeneralSettingsController.class);

	@Autowired
	UserGeneralSettingsService userGeneralSettingsService;

		
	@RequestMapping(value = "/setting",method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addUserGeneralSetting(
			@RequestBody UserGeneralSettingsT userGeneralSettings,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside UserGeneralSettingsController /userGeneral/setting POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		if (userGeneralSettingsService.addUserGeneralSettings(userGeneralSettings)) {
			logger.debug("User General Settings inserted Successfully");
			status.setStatus(Status.SUCCESS,"User General Settings Inserted Successfully" );
		}
		return new ResponseEntity<String>(ResponseConstructors.filterJsonForFieldAndViews(
				"all", "", status), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/setting",method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> updateUserGeneralSetting(
			@RequestBody UserGeneralSettingsT userGeneralSettings,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside UserGeneralSettingsController /userGeneral/setting PUT");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		if (userGeneralSettingsService.updateUserGeneralSettings(userGeneralSettings)) {
			logger.debug("User General Settings Updated Successfully");
			status.setStatus(Status.SUCCESS,"User General Settings Updated Successfully" );
		}
		
		return new ResponseEntity<String>(ResponseConstructors.filterJsonForFieldAndViews(
				"all", "", status), HttpStatus.OK);
	}
}