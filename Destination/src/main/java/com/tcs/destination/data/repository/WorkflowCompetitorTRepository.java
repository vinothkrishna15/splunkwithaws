package com.tcs.destination.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.WorkflowCompetitorT;
import com.tcs.destination.bean.WorkflowCustomerT;

@Repository
public interface WorkflowCompetitorTRepository extends CrudRepository<WorkflowCompetitorT, Integer>{

}
