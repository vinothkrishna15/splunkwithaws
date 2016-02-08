package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.BeaconCustomerMappingT;

@Repository
public interface BeaconCustomerMappingRepository extends
CrudRepository<BeaconCustomerMappingT, String>{

}
