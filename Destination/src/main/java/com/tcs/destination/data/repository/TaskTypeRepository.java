package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.TaskTypeMappingT;

public interface TaskTypeRepository extends
		CrudRepository<TaskTypeMappingT, String> {

}
