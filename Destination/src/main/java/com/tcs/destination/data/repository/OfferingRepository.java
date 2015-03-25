package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.OfferingMappingT;

public interface OfferingRepository extends
		CrudRepository<OfferingMappingT, String> {

}
