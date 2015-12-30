package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.ApplicationSettingsT;
import com.tcs.destination.data.repository.ApplicationSettingsRepository;
import com.tcs.destination.exception.DestinationException;


/*
 * This Service handles the application settings related functionalities
 */
@Service
public class ApplicationSettingsService {

	private static final Logger logger = LoggerFactory
			.getLogger(ApplicationSettingsService.class);

	@Autowired
	ApplicationSettingsRepository applicationSettingsRepository;
    
	/**
	 * This method retrieves the list of all the application settings
	 * @return
	 */
	public List<ApplicationSettingsT> findAll() {
		logger.info("Inside findAll() of ApplicationSettingsService");
		return (List<ApplicationSettingsT>) applicationSettingsRepository
				.findAll();
	}
    
	/**
	 * This method updates the list of application settings passed. 
	 * @param applicationSettingsTs
	 * @throws DestinationException
	 */
	public void edit(List<ApplicationSettingsT> applicationSettingsTs)
			throws DestinationException {
		try {
			logger.info("Begin:Inside edit() of ApplicationSettingsService");
			applicationSettingsRepository.save(applicationSettingsTs);
			logger.info("End:Inside edit() of ApplicationSettingsService");
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}

	}

}
