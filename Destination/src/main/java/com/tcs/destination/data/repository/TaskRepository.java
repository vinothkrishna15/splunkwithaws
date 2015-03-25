package com.tcs.destination.data.repository;

import org.springframework.data.jpa.repository.Query;
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
	 * Finds all the tasks with the given task description.
	 * 
	 * @param taskDescription
	 * @return tasks with the given task description.
	 */
	List<TaskT> findByTaskDescriptionIgnoreCaseLike(String taskDescription);

	/**
	 * Finds all the tasks for the given opportunity id.
	 * 
	 * @param opportunityId
	 * @return tasks for the given opportunity id.
	 */
	List<TaskT> findByOpportunityId(String opportunityId);

	/**
	 * Finds all the tasks for the given task owner.
	 * 
	 * @param taskOwner
	 * @return tasks for the given task owner.
	 */
	List<TaskT> findByTaskOwner(String taskOwner);

	/**
	 * Finds all the tasks created and assigned to others by the given user id.
	 * 
	 * @param userId
	 * @return tasks created and assigned to others by the given user id.
	 */
	@Query(value="select * from task_t where created_modified_by = ?1 and task_owner <> ?1 order by target_date_for_completion asc",nativeQuery=true)
	List<TaskT> findTasksAssignedToOthers(String userId);

}