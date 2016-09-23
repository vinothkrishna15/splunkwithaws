package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditDeliveryMasterManagerLinkT;

@Repository
public interface AuditDeliveryMasterManagerLinkRepository extends
		CrudRepository<AuditDeliveryMasterManagerLinkT, Integer> {
		List<AuditDeliveryMasterManagerLinkT> findByDeliveryMasterId(Integer id);
	
}
