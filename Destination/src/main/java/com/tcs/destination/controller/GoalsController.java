package com.tcs.destination.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.GoalMappingT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.GoalMappingService;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * This controller deals with the goals related services
 * 
 * @author TCS
 *
 */
@RestController
@RequestMapping("/goals")
public class GoalsController {

	private static final Logger logger = LoggerFactory
			.getLogger(GoalsController.class);

	@Autowired
	GoalMappingService goalMappingService;

	/**
	 * This method is used to retrieve all the goal mappings
	 *  
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String goalMappings(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside GoalsController: Start of goalMappings");
		try {
			ArrayList<GoalMappingT> goalMappingTs = new ArrayList<GoalMappingT>();
			goalMappingTs = (ArrayList<GoalMappingT>) goalMappingService.findAll();
			logger.info("Ending GoalsController goalMappings method");
			return ResponseConstructors.filterJsonForFieldAndViews(fields, view, goalMappingTs);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the goal details");
		}
	}
}
