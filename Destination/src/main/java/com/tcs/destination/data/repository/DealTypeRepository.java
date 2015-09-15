package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DealTypeMappingT;

@Repository
public interface DealTypeRepository extends CrudRepository<DealTypeMappingT, String> {

}
