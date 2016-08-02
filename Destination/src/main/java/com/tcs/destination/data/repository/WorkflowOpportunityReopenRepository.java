package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunityT;

@Repository
public interface WorkflowOpportunityReopenRepository extends
CrudRepository<OpportunityT, Integer> {

}
