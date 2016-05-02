package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.WorkflowStepT;
import com.tcs.destination.enums.WorkflowStatus;

@Repository
public interface WorkflowStepTRepository extends JpaRepository<WorkflowStepT, Integer>{

	WorkflowStepT findByRequestIdAndStepStatus(Integer requestId, String status);

	@Query(value = "select * from workflow_step_t where "
			+ "step = (select max(step) from workflow_step_t where request_id = ?1) and request_id = ?1", nativeQuery = true)
	WorkflowStepT findWorkflowStepForFinalApproval(Integer requestId);

	@Query(value = "select * from workflow_step_t where "
			+ "step < (select max(step) from workflow_step_t where request_id = ?1) and request_id = ?1", nativeQuery = true)
	List<WorkflowStepT> findWorkflowTemplateBelowMaximumStep(
			Integer requestId);

	@Query(value = "select * from workflow_step_t where step_id = ?1 ;", nativeQuery = true)
	public WorkflowStepT findStep(int stepId);

	@Query(value = "select * from workflow_step_t where request_id = ?1 and step = ?2 ;", nativeQuery = true)
	public WorkflowStepT updateNextStep(int requestId, int stepId);
	
	@Query(value = "select * from workflow_step_t  where request_id =(:requestId) order by step asc", nativeQuery = true)
    List<WorkflowStepT> findStepsByRequestId(@Param("requestId") Integer requestId);

	// @Query(value = "select * from workflow_step_t where request_id = ?1 ;",
	// nativeQuery = true )
	// public List<WorkflowStepT> findStepsForRequest(int requestId);

	// for approve and edit
	@Query(value = "select * from workflow_step_t where request_id = (select request_id from workflow_request_t where entity_type_id = ?1 and entity_id = ?2) ORDER BY step ASC;", nativeQuery = true)
	public List<WorkflowStepT> findStepForEditAndApprove(int entityTypeId, String requestId);
	
	public WorkflowStepT findFirstByRequestIdAndStepStatusNotOrderByStepIdDesc(Integer requestId, String stepStatus);
	
	
	
	
}
