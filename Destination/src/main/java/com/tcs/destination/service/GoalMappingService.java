package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.GoalMappingT;
import com.tcs.destination.data.repository.GoalMappingRepository;

/*
 *This service retrieves all the data from goal_mapping_t
 */
@Service
public class GoalMappingService {
	
	private static final Logger logger = LoggerFactory.getLogger(GoalMappingService.class);

	@Autowired
	GoalMappingRepository goalMappingRepository;

	public List<GoalMappingT> findAll() {
		logger.debug("Inside findAll() GeographyService");
		return (ArrayList<GoalMappingT>) goalMappingRepository.findAll();
	}

}
