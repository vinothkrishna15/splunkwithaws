package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.WorkflowRequestT;

@Repository
public interface WorkflowRequestTRepository extends
		JpaRepository<WorkflowRequestT, Integer> {
	@Query(value = "select entity_type_id, entity_id from workflow_request_t where request_id =?1", nativeQuery = true)
	public List<Integer> findRequestedEntityDdetils(int stepRequestId);

	@Query(value = "select * from workflow_request_t where entity_type_id =?1 and entity_id = ?2", nativeQuery = true)
	public WorkflowRequestT findRequestedRecord(int i,
			Integer workflowCustomerId);

	@Query(value = "select * from workflow_request_t where request_id =?1", nativeQuery = true)
	public WorkflowRequestT findRequest(Integer requestId);
	
	public WorkflowRequestT findByRequestId(Integer id);

	// @Query(value = "select * from workflow_request_t where request_id =?1" ,
	// nativeQuery =true)
	// public WorkflowRequestT findOne(Integer requestId);
}
