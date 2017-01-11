/**
 * 
 */
package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.UserPreferencesService;

/**
 * @author tcs2
 *
 */
@RestController
@RequestMapping("/userPreferences")
public class UserPreferencesController {

	private static final Logger logger = LoggerFactory
			.getLogger(UserPreferencesController.class);

	@Autowired
	UserPreferencesService userPreferencesService;

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String insertCustomerOrCompetitor(
			@RequestParam(value = "moduleType") String moduleType,
			@RequestParam(value = "customerID") String customerID) {
		String response = null;
		try {
			userPreferencesService.insertNewCustomerByuserID(moduleType,
					customerID);
			response = "success";
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while retrieving customer consulting details");
		}

		return response;
	}

	@RequestMapping(value = "/remove", method = RequestMethod.DELETE)
	public String deleteFromCustomerOrCompetitor(
			@RequestParam(value = "moduleType") String moduleType,
			@RequestParam(value = "customerID") String customerID) {
		String response = null;
		try {

			/*userPreferencesService.removeCustomerIDForUserID(moduleType,
					customerID);*/
			response = "Success";
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while retrieving customer consulting details");
		}

		return response;
	}
}
