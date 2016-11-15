package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditWorkflowPartnerT;

@Repository
public interface AuditWorkflowPartnerTRepository extends CrudRepository<AuditWorkflowPartnerT, Long>{

	List<AuditWorkflowPartnerT> findByWorkflowPartnerIdAndOperationType(
			String entityId, Integer opType);
	List<AuditWorkflowPartnerT> findByWorkflowPartnerId(String entityId);

}
