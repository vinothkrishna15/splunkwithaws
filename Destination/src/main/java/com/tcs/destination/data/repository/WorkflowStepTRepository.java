package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.WorkflowStepT;

@Repository
public interface WorkflowStepTRepository extends CrudRepository<WorkflowStepT, String>{

	@Query(value = "select * from workflow_step_t where step_id = ?1 ;", nativeQuery = true )
	public WorkflowStepT findStep(int stepId);
	
	@Query(value = "select * from workflow_step_t where request_id = ?1 and step = ?2 ;", nativeQuery = true )
	public WorkflowStepT updateNextStep(int requestId, int stepId);
	
	//@Query(value = "select * from workflow_step_t where request_id = ?1 ;", nativeQuery = true )
//	public List<WorkflowStepT> findStepsForRequest(int requestId);
	
	// for approve and edit
	@Query(value = "select * from workflow_step_t where request_id = (select request_id from workflow_request_t where entity_id = ?1) ;", nativeQuery = true )
	public List<WorkflowStepT> findStepForEditAndApprove(int requestId);
}
