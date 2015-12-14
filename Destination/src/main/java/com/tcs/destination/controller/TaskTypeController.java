package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.TaskTypeMappingT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.TaskTypeService;
import com.tcs.destination.utils.ResponseConstructors;

/*
 * This class retrieves the task type mapping
 */
@RestController
@RequestMapping("/tasktype")
public class TaskTypeController {

	private static final Logger logger = LoggerFactory
			.getLogger(TaskTypeController.class);

	@Autowired
	TaskTypeService taskTypeService;

	/**
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAll(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of retrieving the Task Type Mapping");
		try {
		List<TaskTypeMappingT> taskTypeMapping = (List<TaskTypeMappingT>) taskTypeService
				.findAll();
		logger.info("End of retrieving the Task Type Mapping");
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				taskTypeMapping);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Task Type Mapping");
	   }
	}

}
