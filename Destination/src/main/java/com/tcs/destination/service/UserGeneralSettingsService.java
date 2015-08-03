package com.tcs.destination.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.UserGeneralSettingsT;
import com.tcs.destination.data.repository.UserGeneralSettingsRepository;
import com.tcs.destination.exception.DestinationException;

@Service
public class UserGeneralSettingsService {

	private static final Logger logger = LoggerFactory
			.getLogger(UserGeneralSettingsService.class);

	@Autowired
	UserGeneralSettingsRepository userGeneralSettingsRepository;

	public boolean addUserGeneralSettings(
			UserGeneralSettingsT userGeneralSettings)
			throws DestinationException {
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

	public boolean updateUserGeneralSettings(
			UserGeneralSettingsT userGeneralSettings)
			throws DestinationException {
		try {
			return userGeneralSettingsRepository.save(userGeneralSettings) != null;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR " + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
	}
}
