package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditWorkflowStepT;

@Repository
public interface AuditWorkflowStepTRepository extends CrudRepository<AuditWorkflowStepT, Long>{

	@Query(value="select * from audit_workflow_step_t where old_request_id =:workflowId and (new_step_status = 'SUBMITTED' OR (old_step_status <> 'NOT APPLICABLE' OR old_step_status <> NULL))", nativeQuery=true)
	List<AuditWorkflowStepT> findByRequestIdAndStatus(@Param("workflowId") Integer workflowId);

}
