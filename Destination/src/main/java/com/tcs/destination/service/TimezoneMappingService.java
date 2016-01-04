package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.TimeZoneMappingT;
import com.tcs.destination.data.repository.TimezoneMappingRepository;

/**
 *This service retrieves all the data from timezone_mapping_t 
 *
 */
@Service
public class TimezoneMappingService {

	private static final Logger logger = LoggerFactory
			.getLogger(TimezoneMappingService.class);

	@Autowired
	TimezoneMappingRepository timezoneMappingRepository;

	public List<TimeZoneMappingT> findAll() {
		logger.info("Inside findAll() TimezoneMappingService");
		return (List<TimeZoneMappingT>) timezoneMappingRepository
				.findAll();
	}
}
