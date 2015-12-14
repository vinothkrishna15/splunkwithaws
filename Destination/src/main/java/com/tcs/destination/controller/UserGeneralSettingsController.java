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
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.UserGeneralSettingsService;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * UserGeneralSettingsController 
 * This class deals with user general settings and its associated functions
 */
@RestController
@RequestMapping("/general")
public class UserGeneralSettingsController {

	private static final Logger logger = LoggerFactory
			.getLogger(UserGeneralSettingsController.class);

	@Autowired
	UserGeneralSettingsService userGeneralSettingsService;

	/**
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String ConnectSearchByName(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of retrieving the General Settings by user id");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		try {
			UserGeneralSettingsT userGeneralSettingsT = userGeneralSettingsService
					.findGeneralSettingsByUserId(userId);
			logger.info("End of retrieving the General Settings by user id");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, userGeneralSettingsT);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the General Settings");
		}
	}

	/**
	 * @param userGeneralSettings
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/setting", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addUserGeneralSetting(
			@RequestBody UserGeneralSettingsT userGeneralSettings,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of create user general settings");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (userGeneralSettingsService
					.addUserGeneralSettings(userGeneralSettings)) {
				status.setStatus(Status.SUCCESS,
						"User General Settings Inserted Successfully");
			}
			logger.info("End of create user general settings");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while inserting user general settings");
		}
	}

	/**
	 * @param userGeneralSettings
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/setting", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> updateUserGeneralSetting(
			@RequestBody UserGeneralSettingsT userGeneralSettings,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of Edit user general settings");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (userGeneralSettingsService
					.updateUserGeneralSettings(userGeneralSettings)) {
				status.setStatus(Status.SUCCESS,
						"User General Settings Updated Successfully");
			}
			logger.info("End of Edit user general settings");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while editing the user general settings");
		}
	}
}