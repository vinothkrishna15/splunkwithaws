package com.tcs.destination.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.UserGeneralSettingsT;
import com.tcs.destination.data.repository.UserGeneralSettingsRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DestinationUtils;

/**
 * 
 * This service is used to handle general settings pertaining to a user
 *
 */
@Service
public class UserGeneralSettingsService {

	private static final Logger logger = LoggerFactory
			.getLogger(UserGeneralSettingsService.class);

	@Autowired
	UserGeneralSettingsRepository userGeneralSettingsRepository;

	/**
	 * This method is used to save details of user settings
	 * @param userGeneralSettings
	 * @return
	 * @throws DestinationException
	 */
	public boolean addUserGeneralSettings(
			UserGeneralSettingsT userGeneralSettings)
			throws DestinationException {
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		userGeneralSettings.setUserId(userId);
		UserGeneralSettingsT userGenSettings = userGeneralSettingsRepository
				.findByUserId(userGeneralSettings.getUserId());
		if (userGenSettings == null) {
			return userGeneralSettingsRepository.save(userGeneralSettings) != null;

		} else {
			logger.error("UserId : "+ userGeneralSettings.getUserId() + " already exist");
			throw new DestinationException(HttpStatus.BAD_REQUEST, "UserId : "
					+ userGeneralSettings.getUserId() + " already exist");
		}

	}

	/**
	 * This method is used to edit user settings 
	 * @param userGeneralSettings
	 * @return
	 * @throws Exception
	 */
	public boolean updateUserGeneralSettings(UserGeneralSettingsT userGeneralSettings)
			throws Exception {
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		userGeneralSettings.setUserId(userId);
		if(userGeneralSettings.getUserId()!=null){
		UserGeneralSettingsT userGenSettings = userGeneralSettingsRepository
				.findByUserId(userGeneralSettings.getUserId());
		if (userGenSettings!= null) {
			return userGeneralSettingsRepository.save(userGeneralSettings) != null;

		} else {
			logger.error("BAD_REQUEST: Invalid Settings ID");
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid Settings ID");
		}
	} else {
		logger.error("BAD_REQUEST: userId is Required");
		throw new DestinationException(HttpStatus.BAD_REQUEST, "userId is Required");
	}
		
	}

	/**
	 * This method is used to search settings for a user based on userId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public UserGeneralSettingsT findGeneralSettingsByUserId(String userId) throws Exception {
		UserGeneralSettingsT userGenSettings = userGeneralSettingsRepository.findByUserId(userId);
		if(userGenSettings!=null){
			return userGenSettings;
		} else {
			logger.error("NOT_FOUND: Settings Not Found");
			throw new DestinationException(HttpStatus.NOT_FOUND, "Settings Not Found");
		}
	}
}
