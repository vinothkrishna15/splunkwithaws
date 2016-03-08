package com.tcs.destination.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.WorkflowCustomerT;

@Repository
public interface WorkflowCustomerTRepository extends CrudRepository<WorkflowCustomerT, String>{

	@Query (value = "select * from workflow_customer_t where workflow_customer_id = ?1", nativeQuery = true)
	public WorkflowCustomerT findWorkflowCustomer (int workflowCustomerId);
	
	WorkflowCustomerT findByCustomerName(String customerName);
}
