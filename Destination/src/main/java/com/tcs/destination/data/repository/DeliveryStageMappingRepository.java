package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryStageMappingT;

@Repository
public interface DeliveryStageMappingRepository extends CrudRepository<DeliveryStageMappingT, Integer> {

}
