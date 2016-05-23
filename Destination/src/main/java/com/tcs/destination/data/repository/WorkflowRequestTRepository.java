package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.WorkflowRequestT;

@Repository
public interface WorkflowRequestTRepository extends
		JpaRepository<WorkflowRequestT, Integer> {
	
	@Query(value = "select entity_type_id, entity_id from workflow_request_t where request_id =?1", nativeQuery = true)
	public List<Integer> findRequestedEntityDdetils(int stepRequestId);

	@Query(value = "select * from workflow_request_t where entity_type_id =?1 and entity_id = ?2", nativeQuery = true)
	public WorkflowRequestT findRequestedRecord(int i,
			String workflowCustomerId);

	public WorkflowRequestT findByRequestId(Integer id);
	
	
	public List<WorkflowRequestT> findByCreatedByAndStatus(String createdBy, String status);
	
	public List<WorkflowRequestT> findByCreatedBy(String createdBy);

	/**
	 * @param userId
	 * @param status
	 * @param type
	 * @return WorkflowRequestT
	 */
	@Query("SELECT wf FROM WorkflowRequestT wf JOIN wf.workflowStepTs ws WHERE wf.status = :status  AND ws.userId = :userId ORDER BY ws.stepId")
	public List<WorkflowRequestT> getModifiedByAndStatus(
			@Param("userId") String userId,
			@Param("status") String status);

	/**
	 * @param userId
	 * @param type
	 * @return WorkflowRequestT
	 */
	@Query("SELECT wf FROM WorkflowRequestT wf JOIN wf.workflowStepTs ws WHERE ws.userId = :userId ORDER BY ws.stepId")
	public List<WorkflowRequestT> getModifiedBy(
			@Param("userId") String userId);

	public WorkflowRequestT findByEntityTypeIdAndEntityId(Integer entityTypeId,
			String opportunityId);

	public WorkflowRequestT findByEntityTypeIdAndEntityIdAndStatus(Integer entityTypeId,
			String opportunityId, String status);

}
