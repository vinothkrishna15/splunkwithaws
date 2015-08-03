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

@Service
public class ApplicationSettingsService {

	private static final Logger logger = LoggerFactory
			.getLogger(ApplicationSettingsService.class);

	@Autowired
	ApplicationSettingsRepository applicationSettingsRepository;

	public List<ApplicationSettingsT> findAll() {
		logger.debug("Inside findAll Service");
		return (List<ApplicationSettingsT>) applicationSettingsRepository
				.findAll();
	}

	public void edit(List<ApplicationSettingsT> applicationSettingsTs)
			throws DestinationException {
		try {
			applicationSettingsRepository.save(applicationSettingsTs);
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}

	}

}
