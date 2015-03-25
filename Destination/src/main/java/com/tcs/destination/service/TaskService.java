package com.tcs.destination.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.tcs.destination.bean.TaskBdmsTaggedLinkT;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.data.repository.TaskBdmsTaggedLinkRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants.TaskCollaborationPreference;
import com.tcs.destination.utils.Constants.TaskEntityReference;
import com.tcs.destination.utils.Constants.TaskStatus;

/**
 * Service class to handle Task module related requests.
 * 
 */
@Component
public class TaskService {

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	TaskBdmsTaggedLinkRepository taskBdmsTaggedLinkRepository;

	/**
	 * This method is used to find task details for the given task id.
	 * 
	 * @param taskId
	 * @return task details for the given task id.
	 */
	public TaskT findTaskById(String taskId) throws DestinationException {
		TaskT task = taskRepository.findOne(taskId);

		if (task == null)
			throw new DestinationException(HttpStatus.NOT_FOUND, "No task found for the TaskId");

		//Set the TaskOwner details from User object
		if (task.getUserT() != null) {
			task.setTaskOwnerName(task.getUserT().getUserName());
		}
		return task;
	}

	/**
	 * This method is used to find all the tasks with the given task description.
	 * @param taskDescription
	 * @return tasks with the given task description.
	 */
	public List<TaskT> findTasksByNameContaining(String taskDescription) throws DestinationException {
		List<TaskT> taskList = taskRepository.
				findByTaskDescriptionIgnoreCaseLike("%" + taskDescription + "%");

		if ((taskList == null) || taskList.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND, "No tasks found with the given task description");

		//Set the TaskOwner details from User object
		for (TaskT task : taskList) {
			if (task.getUserT() != null) {
				task.setTaskOwnerName(task.getUserT().getUserName());
			}
		}
		return taskList;
	}
	
	/**
	 * This method is used to find all the tasks for the given connect id.
	 * 
	 * @param connectId
	 * @return tasks for the given connect id.
	 */
	public List<TaskT> findTasksByConnectId(String connectId) throws DestinationException {
		List<TaskT> taskList = taskRepository.findByConnectId(connectId);

		if ((taskList == null) || taskList.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND, "No tasks found for the ConnectId");

		//Set the TaskOwner details from User object
		for (TaskT task : taskList) {
			if (task.getUserT() != null) {
				task.setTaskOwnerName(task.getUserT().getUserName());
			}
		}
		return taskList;
	}

	/**
	 * This method is used to find all the tasks for the given opportunity id.
	 * 
	 * @param opportunityId
	 * @return tasks for the given opportunity id.
	 */
	public List<TaskT> findTasksByOpportunityId(String opportunityId) throws DestinationException {
		List<TaskT> taskList = taskRepository.findByOpportunityId(opportunityId);

		if ((taskList == null) || taskList.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND, "No tasks found for the OpportunityId");

		//Set the TaskOwner details from User object
		for (TaskT task : taskList) {
			if (task.getUserT() != null) {
				task.setTaskOwnerName(task.getUserT().getUserName());
			}
		}
		return taskList;
	}

	/**
	 * This method is used to find all the tasks for the given task owner.
	 * 
	 * @param taskOwner
	 * @return tasks for the given task owner.
	 */
	public List<TaskT> findTasksByTaskOwner(String taskOwner) throws DestinationException {
		List<TaskT> taskList = taskRepository.findByTaskOwnerOrderByTargetDateForCompletionAsc(taskOwner);

		if ((taskList == null) || taskList.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND, "No Tasks Found for the UserId");

		//Set the TaskOwner details from User object
		for (TaskT task : taskList) {
			if (task.getUserT() != null) {
				task.setTaskOwnerName(task.getUserT().getUserName());
			}
		}
		return taskList;
	}

	/**
	 * This method is used to find all the tasks assigned to others by a given user id.
	 * @param userId
	 * @return tasks assigned to others by a given user id.
	 */
	public List<TaskT> findTasksAssignedtoOthersByUser(String userId) throws DestinationException {
		List<TaskT> taskList = 
				taskRepository.findByCreatedModifiedByAndTaskOwnerNotOrderByTargetDateForCompletionAsc(userId, userId);

		if ((taskList == null) || taskList.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND, "No Assigned Tasks Found for the UserId");

		//Set the TaskOwner details from User object
		for (TaskT task : taskList) {
			if (task.getUserT() != null) {
				task.setTaskOwnerName(task.getUserT().getUserName());
			}
		}
		return taskList;
	}

	/**
	 * This method is used to create a new task.
	 * 
	 * @param task
	 * @return task
	 */
	@Transactional
	public TaskT createTask(TaskT task) throws DestinationException, Exception {
		List<TaskBdmsTaggedLinkT> taskBdmsTaggedLinkTs = null; 
		TaskT managedTask = null;

		//Validate input parameters
		validateTask(task);

		//TaskBdmsTaggedLinkT contains a not null task_id, so save the parent task first
		if (task.getTaskBdmsTaggedLinkTs() != null) {
			taskBdmsTaggedLinkTs = task.getTaskBdmsTaggedLinkTs();
			task.setTaskBdmsTaggedLinkTs(null);
		}

		managedTask = taskRepository.save(task);

		if ((null != managedTask) && managedTask.getTaskId() != null) {
			if (taskBdmsTaggedLinkTs != null) {
				for (TaskBdmsTaggedLinkT taskBdmTaggedLink: taskBdmsTaggedLinkTs) {
					taskBdmTaggedLink.setTaskT(managedTask);
				}
				//Persist TaskBdmsTaggedLinkT
				taskBdmsTaggedLinkRepository.save(taskBdmsTaggedLinkTs);
			}
		}
		return managedTask;
	}

	/**
	 * This method is used to update a task.
	 * 
	 * @param task
	 * @return task
	 */
	@Transactional
	public TaskT editTask(TaskT task) throws DestinationException, Exception {
		List<TaskBdmsTaggedLinkT> taskBdmsTaggedLinkTs = null; 
		List<TaskBdmsTaggedLinkT> removeBdmsTaggedLinkTs = null; 
		TaskT managedTask = null;

		//Check if task exists
		if (taskRepository.exists(task.getTaskId())) {

			//Validate input parameters
			validateTask(task);
			
			if (task.getTaskBdmsTaggedLinkTs() != null) {
				taskBdmsTaggedLinkTs = task.getTaskBdmsTaggedLinkTs();
				task.setTaskBdmsTaggedLinkTs(null);
			}

			//Remove all the TaskBdmsTaggedLinkT's marked for remove
			if (task.getTaskBdmsTaggedLinkDeletionList() != null) {
				removeBdmsTaggedLinkTs = task.getTaskBdmsTaggedLinkDeletionList();
				task.setTaskBdmsTaggedLinkDeletionList(null);
			}

			//Persist task
			managedTask = taskRepository.save(task);

			//Persist TaskBdmsTaggedLinkT
			if (taskBdmsTaggedLinkTs != null) {
				taskBdmsTaggedLinkRepository.save(taskBdmsTaggedLinkTs);
			}

			//Remove all the TaskBdmsTaggedLinkT's marked for remove
			if (removeBdmsTaggedLinkTs != null) {
				taskBdmsTaggedLinkRepository.delete(removeBdmsTaggedLinkTs);
			}
		} else {
			System.out.println("Task not found: " + task.getTaskId());
			throw new DestinationException(HttpStatus.NOT_FOUND, "Task Not Found For Update");
		}
		return managedTask;
	}

	/**
	 * This method is used to validate task input parameters.
	 * 
	 * @param task
	 * @return
	 */
	private void validateTask(TaskT task) throws DestinationException {

		//Validate Task Entity Reference 
		if (task.getEntityReference() != null) {
			if (!TaskEntityReference.contains(task.getEntityReference()))
				throw new DestinationException(HttpStatus.NOT_ACCEPTABLE, "Invalid Task Entity Reference");
		}
		
		//Validate Task Status
		if (task.getTaskStatus() != null) {
			if (!TaskStatus.contains(task.getTaskStatus()))
				throw new DestinationException(HttpStatus.NOT_ACCEPTABLE, "Invalid Task Status");
		}
		
		//Validate Task Collaboration Preference
		if (task.getCollaborationPreference() != null) {
			if (!TaskCollaborationPreference.contains(task.getCollaborationPreference()))
				throw new DestinationException(HttpStatus.NOT_ACCEPTABLE, "Invalid Task Collaboration Preference");
		}
	}
}