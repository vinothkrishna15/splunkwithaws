package com.tcs.destination.data.repository;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.BeaconCustomerMappingT;

@Repository
public interface BeaconRepository extends
CrudRepository<BeaconCustomerMappingT, String>{

	@Query(value="select * from beacon_customer_mapping_t where beacon_customer_name = ?1 and customer_geography = ?2 and beacon_iou = ?3", nativeQuery = true)
	List<BeaconCustomerMappingT> findbeaconDuplicates(String beaconCustomerName, String customerGeography, String beaconIou);
	
	@Query(value="select * from beacon_customer_mapping_t where beacon_customer_map_id=?1", nativeQuery = true)
    BeaconCustomerMappingT findByBeaconCustomerMapId(Long beaconCustomerMapId);
	
	@Query(value="select * from beacon_customer_mapping_t where beacon_customer_name = ?1 and customer_geography = ?2 and beacon_iou = ?3 and active =?4", nativeQuery = true)
	List<BeaconCustomerMappingT> findBeaconActive(String beaconCustomerName, String customerGeography, String beaconIou, boolean active);

}

