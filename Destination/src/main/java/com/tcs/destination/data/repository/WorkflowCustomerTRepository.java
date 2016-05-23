package com.tcs.destination.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.WorkflowCustomerT;

@Repository
public interface WorkflowCustomerTRepository extends JpaRepository<WorkflowCustomerT, String>{

	@Query(value = "select customer_name from workflow_customer_t where customer_name = (:customerName)", nativeQuery = true)
	String findCustomerName(@Param("customerName") String customerName);

	WorkflowCustomerT findByCustomerName(String customerName);
}
