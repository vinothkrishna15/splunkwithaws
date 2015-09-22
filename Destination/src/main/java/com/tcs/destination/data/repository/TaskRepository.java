package com.tcs.destination.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;

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
	List<TaskT> findByTaskDescriptionIgnoreCaseContaining(String taskDescription);

	/**
	 * Finds all the tasks for the given opportunity id.
	 * 
	 * @param opportunityId
	 * @return tasks for the given opportunity id.
	 */
	List<TaskT> findByOpportunityId(String opportunityId);

	/**
	 * Finds all the tasks (OPEN & HOLD) for the given task owner.
	 * 
	 * @param taskOwner
	 *            , taskStatus
	 * @return tasks for the given task owner.
	 */
	List<TaskT> findByTaskOwnerAndTaskStatusNotOrderByTargetDateForCompletionAsc(
			String taskOwner, String taksStatus);

	/**
	 * Finds all the tasks for the given task owner with the given task status.
	 * 
	 * @param taskOwner
	 *            , taskStatus
	 * @return tasks for the given task owner.
	 */

	List<TaskT> findByTaskOwnerAndTaskStatusOrderByTargetDateForCompletionAsc(
			String taskOwner, String taskStatus);

	/**
	 * Finds all the tasks created and assigned to others by the given user id.
	 * 
	 * @param userId
	 *            , userId
	 * @return tasks created and assigned to others by the given user id.
	 */
	List<TaskT> findByCreatedByAndTaskOwnerNotOrderByTargetDateForCompletionAsc(
			String userId, String taskOwner);

	/**
	 * Finds all the tasks for the given user with a specific target start and
	 * end date.
	 * 
	 * @param userId
	 *            , fromDate, toDate
	 * @return tasks for the given user with a specific target start and end
	 *         date.
	 */
	List<TaskT> findByTaskOwnerAndTargetDateForCompletionBetween(String userId,
			Date fromDate, Date toDate);

	/**
	 * Finds all the team tasks for the given supervisor.
	 * 
	 * @param supervisorId
	 *            , taskStatus
	 * @return team tasks for the given supervisor.
	 */
	@Query(value = "select * from task_t where task_owner in (?1) and UPPER(task_status) != ?2 order by target_date_for_completion asc", nativeQuery = true)
	List<TaskT> findTeamTasksBySupervisorId(List<String> userIds,
			String taksStatus);

	@Query(value = "select user_id from ( select task_owner as user_id from task_t where task_id=(:taskId) "
			+ "union select bdms_tagged as user_id from task_bdms_tagged_link_t where task_id=(:taskId)) as users", nativeQuery = true)
	List<String> findOwnersOfTask(@Param("taskId") String taskId);
}