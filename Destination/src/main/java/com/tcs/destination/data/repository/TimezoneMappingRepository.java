package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.TimeZoneMappingT;

public interface TimezoneMappingRepository extends
		CrudRepository<TimeZoneMappingT, String> {

}
