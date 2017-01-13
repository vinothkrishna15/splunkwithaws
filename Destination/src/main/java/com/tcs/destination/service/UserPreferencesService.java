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

	public UserPreferencesT insertNewCustomerByuserID(String moduleType,
			String customerOrCompetitorName) throws Exception {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();

		List<String> competitorList = userPreferencesRepository
				.getCompetitorList(userId);
		List<String> customerList = userPreferencesRepository
				.getCustomerList(userId);
		UserPreferencesT response = null;
		UserPreferencesT userPreferencesT = new UserPreferencesT();
		userPreferencesT.setModuleType(moduleType);
		userPreferencesT.setUserId(userId);
		if (moduleType.equalsIgnoreCase("COMPETITOR")) {
			response = validateCompetitor(customerOrCompetitorName,
					competitorList, response, userPreferencesT);

		} else if (moduleType.equalsIgnoreCase("CUSTOMER")) {
			response = validateCustomer(customerOrCompetitorName, customerList,
					response, userPreferencesT);
		} else {
			logger.error("BAD_REQUEST: URL Needs to be rephrased");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"The Request URL does not meet the required parameters");
		}

		return response;

	}

	/**
	 * @param customerOrCompetitorName
	 * @param customerList
	 * @param response
	 * @param userPreferencesT
	 * @return
	 */
	private UserPreferencesT validateCustomer(String customerOrCompetitorName,
			List<String> customerList, UserPreferencesT response,
			UserPreferencesT userPreferencesT) {
		if (customerList.isEmpty()) {
			userPreferencesT.setGroupCustomerName(customerOrCompetitorName);
			response = userPreferencesRepository.save(userPreferencesT);
		} else if (!customerList.contains(customerOrCompetitorName.toString())) {
			userPreferencesT.setGroupCustomerName(customerOrCompetitorName);
			response = userPreferencesRepository.save(userPreferencesT);
		}
		return response;
	}

	/**
	 * @param customerOrCompetitorName
	 * @param competitorList
	 * @param response
	 * @param userPreferencesT
	 * @return
	 */
	private UserPreferencesT validateCompetitor(
			String customerOrCompetitorName, List<String> competitorList,
			UserPreferencesT response, UserPreferencesT userPreferencesT) {
		if (competitorList.isEmpty()) {
			userPreferencesT.setCompetitorName(customerOrCompetitorName);
			response = userPreferencesRepository.save(userPreferencesT);
		} else if (!competitorList
				.contains(customerOrCompetitorName.toString())) {
			userPreferencesT.setCompetitorName(customerOrCompetitorName);
			response = userPreferencesRepository.save(userPreferencesT);

		}
		return response;
	}

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
