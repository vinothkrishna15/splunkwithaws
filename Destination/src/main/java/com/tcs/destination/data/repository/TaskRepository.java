package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.tcs.destination.bean.TaskT;

/**
 * 
 * Repository for working with {@link TaskT} module objects
 */
@Repository
public interface TaskRepository extends CrudRepository<TaskT, String> {

	/**
	 * Finds all the tasks for the given connect id.
	 * 
	 * @param connectId
	 * @return tasks for the given connect id.
	 */
	List<TaskT> findByConnectId(String connectId);
	
	/**
	 * Finds all the tasks for the given opportunity id.
	 * 
	 * @param opportunityId
	 * @return tasks for the given opportunity id.
	 */
	List<TaskT> findByOpportunityId(String opportunityId);

}