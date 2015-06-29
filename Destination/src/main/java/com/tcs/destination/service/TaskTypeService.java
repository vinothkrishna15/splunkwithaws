package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.TaskTypeMappingT;
import com.tcs.destination.data.repository.TaskTypeRepository;

@Component
public class TaskTypeService {

	private static final Logger logger = LoggerFactory
			.getLogger(TaskTypeService.class);

	@Autowired
	TaskTypeRepository taskTypeRepository;

	public List<TaskTypeMappingT> findAll() {
		logger.debug("Inside findAll service");
		return (List<TaskTypeMappingT>) taskTypeRepository.findAll();
	}

}
