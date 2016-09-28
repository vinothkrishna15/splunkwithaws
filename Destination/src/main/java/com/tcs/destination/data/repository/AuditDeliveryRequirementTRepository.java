package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditDeliveryRequirementT;

@Repository
public interface AuditDeliveryRequirementTRepository extends
		CrudRepository<AuditDeliveryRequirementT, Integer> {
		
	List<AuditDeliveryRequirementT> findByDeliveryRgsId(String rgsId);
}
