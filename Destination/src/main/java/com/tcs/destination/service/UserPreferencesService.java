/**
 * 
 */
package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.ContentDTO;
import com.tcs.destination.bean.UserPreferencesT;
import com.tcs.destination.data.repository.UserPreferencesRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DestinationUtils;

/**
 * @author tcs2
 *
 */
@Service
public class UserPreferencesService {

	private static final Logger logger = LoggerFactory
			.getLogger(UserPreferencesService.class);

	@Autowired
	UserPreferencesRepository userPreferencesRepository;

	/**
	 * Main method called to add new record into data base based on the module
	 * type and values.
	 * 
	 * @param moduleType
	 * @param customerOrCompetitorName
	 * @return UserPreferencesT - object with all the added values
	 * @throws Exception
	 */
	public UserPreferencesT insertNewCustomerByuserID(String moduleType,
			List<String> customerOrCompetitorName) throws Exception {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();

		List<String> competitorList = userPreferencesRepository
				.getCompetitorList(userId);
		List<String> customerList = userPreferencesRepository
				.getCustomerList(userId);
		UserPreferencesT response = null;

		if (moduleType.equalsIgnoreCase("COMPETITOR")) {
			response = validateCompetitor(customerOrCompetitorName,
					competitorList, response, userId, moduleType);

		} else if (moduleType.equalsIgnoreCase("CUSTOMER")) {
			response = validateCustomer(customerOrCompetitorName, customerList,
					response, userId, moduleType);
		} else {
			logger.error("BAD_REQUEST: URL Needs to be rephrased");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"The Request URL does not meet the required parameters");
		}

		return response;

	}

	/**
	 * Method to validate module type customer
	 * 
	 * @param customerOrCompetitorName
	 * @param customerList
	 * @param response
	 * @param userPreferencesT
	 * @return
	 */
	private UserPreferencesT validateCustomer(
			List<String> customerOrCompetitorName, List<String> customerList,
			UserPreferencesT response, String userId, String moduleType) {
		for (String names : customerOrCompetitorName) {
			if (customerList.isEmpty()) {
				response = setUserPrefsForCustomer(userId, moduleType, names);
			} else if (names != null
					&& !customerList.contains(names.toString())) {
				response = setUserPrefsForCustomer(userId, moduleType, names);
			}
		}
		return response;
	}

	/**
	 * Method to set all the values for customer
	 * 
	 * @param userId
	 * @param moduleType
	 * @param names
	 * @return
	 */
	private UserPreferencesT setUserPrefsForCustomer(String userId,
			String moduleType, String names) {
		UserPreferencesT response;
		UserPreferencesT userPrefDTO = new UserPreferencesT();
		userPrefDTO.setModuleType(moduleType);
		userPrefDTO.setUserId(userId);
		userPrefDTO.setGroupCustomerName(names);
		response = userPreferencesRepository.save(userPrefDTO);
		return response;
	}

	/**
	 * Method to validate module type competitor
	 * 
	 * @param customerOrCompetitorName
	 * @param competitorList
	 * @param response
	 * @param moduleType
	 * @param userId
	 * @return
	 */
	private UserPreferencesT validateCompetitor(
			List<String> customerOrCompetitorName, List<String> competitorList,
			UserPreferencesT response, String userId, String moduleType) {
		for (String names : customerOrCompetitorName) {
			if (competitorList.isEmpty()) {
				response = setUserPrefsForCompetitor(userId, moduleType, names);
			} else if (names != null
					&& !competitorList.contains(names.toString())) {
				response = setUserPrefsForCompetitor(userId, moduleType, names);
			}
		}

		return response;
	}

	/**
	 * Method to set all the required values for competitor.
	 * 
	 * @param userId
	 * @param moduleType
	 * @param names
	 * @return UserPreferencesT
	 */
	private UserPreferencesT setUserPrefsForCompetitor(String userId,
			String moduleType, String names) {
		UserPreferencesT response;
		UserPreferencesT userPrefDTO = new UserPreferencesT();
		userPrefDTO.setModuleType(moduleType);
		userPrefDTO.setUserId(userId);
		userPrefDTO.setCompetitorName(names);
		response = userPreferencesRepository.save(userPrefDTO);
		return response;
	}

	/**
	 * Main method called from controller to fetch all the customers /
	 * competitors available in data base for the accessing user.
	 * 
	 * @param moduleType
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ContentDTO findAvailableFavouriteList(String moduleType) {
		// TODO Auto-generated method stub
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		List<String> availableCustOrCompList = new ArrayList<String>();
		ContentDTO userFavouritesDTO = new ContentDTO();
		if (moduleType.equalsIgnoreCase("COMPETITOR")) {
			availableCustOrCompList = userPreferencesRepository
					.getCompetitorList(userId);

		} else if (moduleType.equalsIgnoreCase("CUSTOMER")) {
			availableCustOrCompList = userPreferencesRepository
					.getCustomerList(userId);
		} else {

			logger.error("BAD_REQUEST: URL Needs to be rephrased");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"The Request URL does not meet the required parameters");
		}
		if (availableCustOrCompList.isEmpty()) {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Available favourite list");

		} else if (availableCustOrCompList.contains(null)) {
			availableCustOrCompList.removeAll(Collections.singleton(null));
			userFavouritesDTO.setContent(availableCustOrCompList);
		} else {
			userFavouritesDTO.setContent(availableCustOrCompList);
		}

		return userFavouritesDTO;
	}

	/**
	 * Main method called from controller to delete the record which user opts
	 * out.
	 * 
	 * @param moduleType
	 * @param customerID
	 */
	public void removePreferencesForUserID(String moduleType, String customerID) {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();

		UserPreferencesT userPreferencesT = new UserPreferencesT();

		if (moduleType.equalsIgnoreCase("COMPETITOR")) {
			userPreferencesT = userPreferencesRepository
					.findByUserIdAndCompetitorName(userId, customerID);
			userPreferencesRepository.delete(userPreferencesT);

		} else if (moduleType.equalsIgnoreCase("CUSTOMER")) {
			userPreferencesT = userPreferencesRepository
					.findByGroupCustomerNameAndUserId(customerID, userId);
			userPreferencesRepository.delete(userPreferencesT);
			;
		} else {
			logger.error("BAD_REQUEST: URL Needs to be rephrased");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"The Request URL does not meet the required parameters");
		}

	}

}
