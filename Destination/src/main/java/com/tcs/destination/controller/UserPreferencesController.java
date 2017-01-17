/**
 * 
 */
package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.ContentDTO;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UserPreferencesT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.UserPreferencesService;
import com.tcs.destination.utils.ResponseConstructors;

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

	/**
	 * Method to insert new customer or competitor from the total list of
	 * customers or competitors to add to favorite list
	 * 
	 * @param moduleType
	 * @param name
	 * @return response - status and the description.
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String insertCustomerOrCompetitor(
			@RequestParam(value = "moduleType") String moduleType,
			@RequestParam(value = "name") List<String> name) {
		logger.info("Inside UserPreferencesController for insertCustomerOrCompetitor method: start");
		Status status = new Status();
		String response = null;
		try {
			userPreferencesService
					.insertNewCustomerByuserID(moduleType, name);
				status.setStatus(Status.SUCCESS, "Data Successfully added");
			response = ResponseConstructors.filterJsonForFieldAndViews("all",
					"", status);
			logger.info("Inside UserPreferencesController for insertCustomerOrCompetitor method: exit");
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while adding the details for user preferences");
		}

		return response;
	}

	/**
	 * Method to delete the preferences id for the particular user and the type
	 * of name - either customer or competitor.
	 * 
	 * @param moduleType
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/remove", method = RequestMethod.GET)
	public String deleteFromCustomerOrCompetitor(
			@RequestParam(value = "moduleType") String moduleType,
			@RequestParam(value = "name") String name) {
		logger.info("Inside UserPreferencesController for deleteFromCustomerOrCompetitor method: start");
		String response = null;
		Status status = new Status();
		try {
			userPreferencesService.removePreferencesForUserID(moduleType, name);
			status.setStatus(Status.SUCCESS, "Data Successfully removed");
			response = ResponseConstructors.filterJsonForFieldAndViews("all",
					"", status);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while deleting the preferences from database");
		}
		logger.info("Inside UserPreferencesController for deleteFromCustomerOrCompetitor method: exit");
		return response;
	}

	/**
	 * Method to retrieve the list of customers or competitor available for the
	 * particular user to display either in competitor or customer module.
	 * 
	 * @param moduleType
	 * @return customerOrCompetitorlist - list of customers
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/retrieveFavouriteList", method = RequestMethod.GET)
	public ContentDTO retrieveFavouriteCustomerOrCompetitor(
			@RequestParam(value = "moduleType") String moduleType) {
		logger.info("Inside UserPreferencesController for retrieveFavouriteCustomerOrCompetitor method: start");
		ContentDTO contentDTO = new ContentDTO();
		try {
			contentDTO = userPreferencesService
					.findAvailableFavouriteList(moduleType);

		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while deleting user preferences details");
		}
		logger.info("Inside UserPreferencesController for retrieveFavouriteCustomerOrCompetitor method: exit");
		return contentDTO;
	}
}
