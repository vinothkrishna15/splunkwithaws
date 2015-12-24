package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.TaskTypeMappingT;
import com.tcs.destination.data.repository.TaskTypeRepository;
/*
 *This service retrieves all the data from taslk_type_mapping_t table 
 */
@Service
public class TaskTypeService {

	private static final Logger logger = LoggerFactory
			.getLogger(TaskTypeService.class);

	@Autowired
	TaskTypeRepository taskTypeRepository;

	public List<TaskTypeMappingT> findAll() {
		logger.info("Inside findAll() TaskTypeService");
		return (List<TaskTypeMappingT>) taskTypeRepository.findAll();
	}

}
