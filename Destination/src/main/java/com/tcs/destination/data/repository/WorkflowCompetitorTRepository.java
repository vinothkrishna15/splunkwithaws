package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.WorkflowCompetitorT;


@Repository
public interface WorkflowCompetitorTRepository extends CrudRepository<WorkflowCompetitorT, String>{

}
