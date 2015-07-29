package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.MiscTypeValueT;

public interface MiscTypeValueRepository extends CrudRepository<MiscTypeValueT, Integer> {

	//@Query(value="select * from misc_type_value_t where")
	List<MiscTypeValueT> findByType(String type);
	
}
