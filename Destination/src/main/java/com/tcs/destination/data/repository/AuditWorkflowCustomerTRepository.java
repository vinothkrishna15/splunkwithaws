package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditWorkflowCustomerT;

@Repository
public interface AuditWorkflowCustomerTRepository extends CrudRepository<AuditWorkflowCustomerT, Long>{

	List<AuditWorkflowCustomerT> findByWorkflowCustomerIdAndOperationType(String entityId, Integer opType);
	List<AuditWorkflowCustomerT> findByWorkflowCustomerId(String entityId);

}
