package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditDeliveryResourcesT;

@Repository
public interface AuditDeliveryResourcesTRepository extends
		CrudRepository<AuditDeliveryResourcesT, Integer> {
		
	List<AuditDeliveryResourcesT> findByDeliveryMasterId(String id);
	
}
