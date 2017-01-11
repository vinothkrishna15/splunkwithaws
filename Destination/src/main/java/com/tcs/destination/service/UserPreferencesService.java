/**
 * 
 */
package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
			String customerID) throws Exception {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();

		List<String> competitorList = userPreferencesRepository
				.getCompetitorList(userId);
		List<String> customerList = userPreferencesRepository
				.getCustomerList(userId);

		UserPreferencesT response = null;
		UserPreferencesT userPreferencesT = new UserPreferencesT();
		userPreferencesT.setPreferencesId(null);
		userPreferencesT.setModuleType(moduleType);
		userPreferencesT.setUserId(userId);
		if (moduleType.equalsIgnoreCase("COMPETITOR")) {
			response = validateCompetitor(customerID, competitorList, response,
					userPreferencesT);

		} else if (moduleType.equalsIgnoreCase("CUSTOMER")) {
			response = validateCustomer(customerID, customerList, response,
					userPreferencesT);
		} else {
			// throw DestinationException e;
		}

		return response;

	}

	/**
	 * @param customerID
	 * @param customerList
	 * @param response
	 * @param userPreferencesT
	 * @return
	 */
	private UserPreferencesT validateCustomer(String customerID,
			List<String> customerList, UserPreferencesT response,
			UserPreferencesT userPreferencesT) {
		if (customerList.isEmpty()) {
			userPreferencesT.setGroupCustomerName(customerID);
			userPreferencesT.setCompetitorName(null);
			response = userPreferencesRepository.save(userPreferencesT);
		} else if (!customerList.contains(customerID.toString())) {
			userPreferencesT.setGroupCustomerName(customerID);
			userPreferencesT.setCompetitorName(null);
			response = userPreferencesRepository.save(userPreferencesT);
		}
		return response;
	}

	/**
	 * @param customerID
	 * @param competitorList
	 * @param response
	 * @param userPreferencesT
	 * @return
	 */
	private UserPreferencesT validateCompetitor(String customerID,
			List<String> competitorList, UserPreferencesT response,
			UserPreferencesT userPreferencesT) {
		if (competitorList.isEmpty()) {
			userPreferencesT.setCompetitorName(customerID);
			userPreferencesT.setGroupCustomerName(null);
			response = userPreferencesRepository.save(userPreferencesT);
		} else if (!competitorList.contains(customerID.toString())) {
			userPreferencesT.setCompetitorName(customerID);
			userPreferencesT.setGroupCustomerName(null);
			response = userPreferencesRepository.save(userPreferencesT);
		}
		return response;
	}

	}
