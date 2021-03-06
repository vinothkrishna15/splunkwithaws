package com.tcs.destination.data.repository;


import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.IouBeaconMappingT;

@Repository
public interface IouBeaconMappingTRepository extends CrudRepository<IouBeaconMappingT, String>{
	
	List<IouBeaconMappingT> findByActiveTrue();

	IouBeaconMappingT findByActiveTrueAndBeaconIou(String beaconIou);
}
