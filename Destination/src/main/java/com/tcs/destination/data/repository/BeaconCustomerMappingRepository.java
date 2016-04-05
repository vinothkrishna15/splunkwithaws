package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.BeaconCustomerMappingT;

@Repository
public interface BeaconCustomerMappingRepository extends
CrudRepository<BeaconCustomerMappingT, Long>{
	@Query(value="select * from beacon_customer_mapping_t where (beacon_customer_name = ?1 and customer_geography = ?2 and beacon_iou = ?3)", nativeQuery = true)
	List<BeaconCustomerMappingT> checkBeaconMappingPK(String financeCustomerName, String customerGeography, String financeIou);
	
	@Query(value="select * from beacon_customer_mapping_t where customer_id in(select customer_id from customer_master_t where customer_name in(select customer_name from workflow_customer_t where workflow_customer_id in (select entity_id from workflow_request_t where request_id= ?1 and status ='APPROVED' and entity_type_id =0 )))", nativeQuery = true)
	List<BeaconCustomerMappingT> getBeaconMappingForWorkflowCustomer(Integer requestId);
}
