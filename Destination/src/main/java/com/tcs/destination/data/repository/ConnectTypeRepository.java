package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.ConnectTypeMappingT;

public interface ConnectTypeRepository extends
		CrudRepository<ConnectTypeMappingT, String> {

}
