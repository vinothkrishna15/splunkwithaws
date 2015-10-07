package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.BeaconCustomerMappingT;
import com.tcs.destination.bean.IouBeaconMappingT;

@Repository
public interface IouBeaconMappingTRepository extends
CrudRepository<IouBeaconMappingT, String>{
	
}
