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
	
	public List<WorkflowRequestT> findByCreatedByAndStatusAndEntityTypeIdIn(String createdBy, String status,List<Integer> type);
	
	public List<WorkflowRequestT> findByCreatedByAndEntityTypeIdIn(String createdBy,List<Integer> type);
	
	public List<WorkflowRequestT> findByCreatedBy(String createdBy);

	/**
	 * Query fetches submitted or approved or rejected requests by a specific user.
	 * (based on filter status)
	 * Except Pending requests with specific user.
	 * @param userId
	 * @param status
	 * @param type
	 * @return WorkflowRequestT
	 */
	@Query("SELECT wf FROM WorkflowRequestT wf JOIN wf.workflowStepTs ws WHERE wf.status = :status AND ws.userId = :userId AND ws.stepStatus NOT LIKE 'PENDING' ORDER BY ws.stepId")
	public List<WorkflowRequestT> getModifiedByAndStatus(
			@Param("userId") String userId,
			@Param("status") String status);
	
	@Query("SELECT wf FROM WorkflowRequestT wf JOIN wf.workflowStepTs ws WHERE wf.status = :status AND wf.entityTypeId in (:type) AND ws.userId = :userId AND ws.stepStatus NOT LIKE 'PENDING' ORDER BY ws.stepId")
	public List<WorkflowRequestT> getModifiedByAndStatusAndType(
			@Param("userId") String userId,
			@Param("status") String status,
			@Param("type") List<Integer> type);

	/**
	 * Query fetches submitted, approved and rejected requests by a specific user.
	 * Except Pending requests with specific user.
	 * @param userId
	 * @param type
	 * @return WorkflowRequestT
	 */
	@Query("SELECT wf FROM WorkflowRequestT wf JOIN wf.workflowStepTs ws WHERE ws.userId = :userId AND ws.stepStatus NOT LIKE 'PENDING' ORDER BY ws.stepId")
	public List<WorkflowRequestT> getModifiedBy(
			@Param("userId") String userId);
	
	@Query("SELECT wf FROM WorkflowRequestT wf JOIN wf.workflowStepTs ws WHERE ws.userId = :userId AND wf.entityTypeId in (:type) AND ws.stepStatus NOT LIKE 'PENDING' ORDER BY ws.stepId")
	public List<WorkflowRequestT> getModifiedByType(
			@Param("userId") String userId,
			@Param("type") List<Integer> type);


	public WorkflowRequestT findByEntityTypeIdAndEntityId(Integer entityTypeId,
			String opportunityId);

	public List<WorkflowRequestT> findByEntityTypeIdAndEntityIdAndStatus(Integer entityTypeId,
			String opportunityId, String status);

	public WorkflowRequestT findByEntityIdAndEntityTypeIdIn(
			String workflowBfmId, int[] bfmEntityTypeIds);
	
	public WorkflowRequestT findByEntityIdAndStatusAndEntityTypeIdIn(
			String workflowBfmId, String status, int[] bfmEntityTypeIds);

}
