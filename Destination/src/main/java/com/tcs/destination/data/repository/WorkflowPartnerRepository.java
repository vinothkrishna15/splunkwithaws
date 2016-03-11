package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.WorkflowPartnerT;

@Repository
public interface WorkflowPartnerRepository extends
CrudRepository<WorkflowPartnerT, Integer> {

}
