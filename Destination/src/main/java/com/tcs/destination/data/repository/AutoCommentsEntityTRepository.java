package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AutoCommentsEntityT;

@Repository
public interface AutoCommentsEntityTRepository extends
		CrudRepository<AutoCommentsEntityT, Integer> {

	AutoCommentsEntityT findByNameIgnoreCaseAndIsactive(String entityName, String isActive);

}
