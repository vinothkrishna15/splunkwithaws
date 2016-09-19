package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.WorkflowBfmT;


@Repository
public interface WorkflowBfmTRepository extends CrudRepository<WorkflowBfmT, String>{

	List<WorkflowBfmT> findWorkflowBfmIdByOpportunityId(String opportunityId);
}
