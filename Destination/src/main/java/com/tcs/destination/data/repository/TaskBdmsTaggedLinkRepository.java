package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.TaskBdmsTaggedLinkT;

/**
 * 
 * Repository for working with {@link TaskBdmsTaggedLinkT} module objects
 */
@Repository
public interface TaskBdmsTaggedLinkRepository extends CrudRepository<TaskBdmsTaggedLinkT, String> {
}