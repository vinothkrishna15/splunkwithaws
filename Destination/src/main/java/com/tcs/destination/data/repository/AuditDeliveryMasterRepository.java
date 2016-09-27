package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditDeliveryMasterT;

@Repository
public interface AuditDeliveryMasterRepository extends CrudRepository<AuditDeliveryMasterT, Integer>{ 
	
	List<AuditDeliveryMasterT> findByDeliveryMasterId(String engId);
	
	@Query(value = "select * from audit_delivery_master_t  where (operation_type  =1 or old_delivery_stage <> new_delivery_stage) and delivery_master_id = :engId ORDER BY created_modified_datetime asc", nativeQuery = true)
	List<AuditDeliveryMasterT> getDeliveryCodeChanges(@Param("engId") String engId);
}
