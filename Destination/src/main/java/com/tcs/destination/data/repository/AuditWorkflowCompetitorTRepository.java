package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditWorkflowCompetitorT;

@Repository
public interface AuditWorkflowCompetitorTRepository extends CrudRepository<AuditWorkflowCompetitorT, Long>{

	List<AuditWorkflowCompetitorT> findByWorkflowCompetitorIdAndOperationType(
			String entityId, Integer opType);
	List<AuditWorkflowCompetitorT> findByWorkflowCompetitorId(
			String entityId);

}
