package com.tcs.destination.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.TaskBdmsTaggedLinkT;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.TaskBdmsTaggedLinkRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.enums.TaskCollaborationPreference;
import com.tcs.destination.enums.TaskEntityReference;
import com.tcs.destination.enums.TaskStatus;
import com.tcs.destination.exception.DestinationException;

/**
 * Service class to handle Task module related requests.
 * 
 */
@Component
public class TaskService {

	private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
	
	private static final String STATUS_CLOSED = "Closed";
	
	@Autowired
	TaskRepository taskRepository;

	@Autowired
	TaskBdmsTaggedLinkRepository taskBdmsTaggedLinkRepository;
	
	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	OpportunityRepository opportunityRepository;

	/**
	 * This method is used to find task details for the given task id.
	 * 
	 * @param taskId
	 * @return task details for the given task id.
	 */
	public TaskT findTaskById(String taskId) throws Exception {
		logger.debug("Inside findTaskById Service");
		TaskT task = taskRepository.findOne(taskId);

		if (task == null) {
			logger.error("NOT_FOUND: No task found for the TaskId");
			throw new DestinationException(HttpStatus.NOT_FOUND, "No Task found");
		}
		return task;
	}

	/**
	 * This method is used to find all the tasks with the given task description.
	 * @param taskDescription
	 * @return tasks with the given task description.
	 */
	public List<TaskT> findTasksByNameContaining(String taskDescription) throws Exception {
		logger.debug("Inside findTasksByNameContaining Service");
		List<TaskT> taskList = taskRepository.
				findByTaskDescriptionIgnoreCaseContaining(taskDescription);

		if ((taskList == null) || taskList.isEmpty()) {
			logger.error("NOT_FOUND: No tasks found with the given task description");
			throw new DestinationException(
					HttpStatus.NOT_FOUND, "No tasks found with the given task description");
		}
		return taskList;
	}
	
	/**
	 * This method is used to find all the tasks for the given connect id.
	 * 
	 * @param connectId
	 * @return tasks for the given connect id.
	 */
	public List<TaskT> findTasksByConnectId(String connectId) throws Exception {
		logger.debug("Inside findTasksByConnectId Service");
		List<TaskT> taskList = taskRepository.findByConnectId(connectId);

		if ((taskList == null) || taskList.isEmpty())
		{
			logger.error("NOT_FOUND: No tasks found for the ConnectId");
			throw new DestinationException(HttpStatus.NOT_FOUND, "No tasks found for the ConnectId");
		}
		return taskList;
	}

	/**
	 * This method is used to find all the tasks for the given opportunity id.
	 * 
	 * @param opportunityId
	 * @return tasks for the given opportunity id.
	 */
	public List<TaskT> findTasksByOpportunityId(String opportunityId) throws Exception {
		logger.debug("Inside findTasksByOpportunityId Service");
		List<TaskT> taskList = taskRepository.findByOpportunityId(opportunityId);

		if ((taskList == null) || taskList.isEmpty())
		{
			logger.error("NOT_FOUND: No tasks found for the OpportunityId");
			throw new DestinationException(HttpStatus.NOT_FOUND, "No tasks found for the OpportunityId");
		}
		return taskList;
	}

	/**
	 * This method is used to find all the tasks (open & hold) for the given task owner.
	 * 
	 * @param taskOwner, taskStatus
	 * @return tasks for the given task owner.
	 */
	public List<TaskT> findTasksByTaskOwner(String taskOwner) throws Exception {
		logger.debug("Inside findTasksByTaskOwner Service");
		List<TaskT> taskList = 
				taskRepository.findByTaskOwnerAndTaskStatusNotOrderByTargetDateForCompletionAsc(taskOwner, STATUS_CLOSED);

		if ((taskList == null) || taskList.isEmpty())
		{
			logger.error("NOT_FOUND: No tasks found for the UserId");
			throw new DestinationException(HttpStatus.NOT_FOUND, "No tasks found for the UserId");
		}
		return taskList;
	}

	/**
	 * This method is used to find all the tasks assigned to others by a given user id.
	 * @param userId
	 * @return tasks assigned to others by a given user id.
	 */
	public List<TaskT> findTasksAssignedtoOthersByUser(String userId) throws Exception {
		logger.debug("Inside findTasksAssignedtoOthersByUser Service");
		List<TaskT> taskList = 
			taskRepository.findByCreatedModifiedByAndTaskOwnerNotOrderByTargetDateForCompletionAsc(userId, userId);

		if ((taskList == null) || taskList.isEmpty()) {
			logger.error("NOT_FOUND: No assigned to others tasks found for the UserId");
			throw new DestinationException
				(HttpStatus.NOT_FOUND, "No assigned to others tasks found for the UserId");
		}
		return taskList;
	}

	/**
	 * This method is used to find all the tasks assigned to a user with a specific target completion start and end date.
	 * @param userId, fromDate, toDate
	 * @return tasks assigned to a user with a specific target completion start and end date.
	 */
	public List<TaskT> findTasksByUserAndTargetDate(String userId, Date fromDate, Date toDate) 
			throws Exception {
		logger.debug("Inside findTasksByUserAndTargetDate Service");
		List<TaskT> taskList = null;
		
		taskList = taskRepository.findByTaskOwnerAndTargetDateForCompletionBetween(userId, fromDate, toDate);
		
		if ((taskList == null) || taskList.isEmpty()) {
			logger.error("NOT_FOUND: No tasks found for the UserId and Target completion date");
			throw new DestinationException(
				HttpStatus.NOT_FOUND, "No tasks found for the UserId and Target completion date");
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
	public TaskT createTask(TaskT task) throws Exception {
		logger.debug("Inside createTask Service");
		List<TaskBdmsTaggedLinkT> taskBdmsTaggedLinkTs = null; 
		TaskT managedTask = null;

		//Validate input parameters
		validateTask(task);

		//TaskBdmsTaggedLinkT contains a not null task_id, so save the parent task first
		if (task.getTaskBdmsTaggedLinkTs() != null) {
			logger.debug("TaskBdmsTaggedLinkTs NOT NULL");
			taskBdmsTaggedLinkTs = task.getTaskBdmsTaggedLinkTs();
			task.setTaskBdmsTaggedLinkTs(null);
		}

		managedTask = taskRepository.save(task);

		if ((null != managedTask) && managedTask.getTaskId() != null) {
			logger.debug("ManagedTask and TaskId NOT NULL");
			if (taskBdmsTaggedLinkTs != null) {
				logger.debug("taskBdmsTaggedLinkTs NOT NULL");
				for (TaskBdmsTaggedLinkT taskBdmTaggedLink: taskBdmsTaggedLinkTs) {
					taskBdmTaggedLink.setTaskT(managedTask);
					taskBdmTaggedLink.setTaskId(managedTask.getTaskId());
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
	public TaskT editTask(TaskT task) throws Exception {
		logger.debug("Inside editTask Service");
		List<TaskBdmsTaggedLinkT> taskBdmsTaggedLinkTs = null; 
		List<TaskBdmsTaggedLinkT> removeBdmsTaggedLinkTs = null; 
		TaskT managedTask = null;

		//Check if task exists
		if (taskRepository.exists(task.getTaskId())) {
			logger.debug("Task Exists");
			//Validate input parameters
			validateTask(task);
			
			if (task.getTaskBdmsTaggedLinkTs() != null) {
				logger.debug("TaskBdmsTaggedLink NOT NULL");
				taskBdmsTaggedLinkTs = task.getTaskBdmsTaggedLinkTs();
				task.setTaskBdmsTaggedLinkTs(null);
			}

			//Remove all the TaskBdmsTaggedLinkT's marked for remove
			if (task.getTaskBdmsTaggedLinkDeletionList() != null) {
				logger.debug("TaskBdmsTaggedLink NOT NULL");
				removeBdmsTaggedLinkTs = task.getTaskBdmsTaggedLinkDeletionList();
				task.setTaskBdmsTaggedLinkDeletionList(null);
			}

			//Persist task
			managedTask = taskRepository.save(task);

			//Persist TaskBdmsTaggedLinkT
			if (taskBdmsTaggedLinkTs != null) {
				logger.debug("taskBdmsTaggedLinkTs NOT NULL");
				for (TaskBdmsTaggedLinkT taskBdmTaggedLink: taskBdmsTaggedLinkTs) {
					taskBdmTaggedLink.setTaskT(managedTask);
					taskBdmTaggedLink.setTaskId(managedTask.getTaskId());
				}
				taskBdmsTaggedLinkRepository.save(taskBdmsTaggedLinkTs);
				logger.debug("TaskBdmsTaggedLinkTs Saved Successfully");
			}

			//Remove all the TaskBdmsTaggedLinkT's marked for remove
			if (removeBdmsTaggedLinkTs != null) {
				logger.debug("TaskBdmsTaggedLink Removed Successfully");
				taskBdmsTaggedLinkRepository.delete(removeBdmsTaggedLinkTs);
			}
		} else {
			logger.error("Task not found: {}", task.getTaskId());
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
			logger.debug("Entity Reference NOT NULL");
			String entityRef = task.getEntityReference();
			if (TaskEntityReference.contains(entityRef)) {
				//If EntityReference is Connect, ConnectId should be passed
				if (TaskEntityReference.CONNECT.equalsName(entityRef)) {
					if (task.getConnectId() == null) {
						logger.error("BAD_REQUEST: ConnectId is required");
						throw new DestinationException(HttpStatus.BAD_REQUEST, "ConnectId is required");
					}
					if (task.getOpportunityId() != null) {
						logger.error("BAD_REQUEST: EntityReference is Connect, OpportunityId should not be passed");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"EntityReference is Connect, OpportunityId should not be passed");
					}
					if (!connectRepository.exists(task.getConnectId())) {
						logger.error("NOT_FOUND: ConnectId not found");
						throw new DestinationException(HttpStatus.NOT_FOUND, "ConnectId not found");
					}
				}
				//If EntityReference is Opportunity, OpportunityId should be passed
				if (TaskEntityReference.OPPORTUNITY.equalsName(entityRef)) {
					if (task.getOpportunityId() == null) {
						logger.error("BAD_REQUEST: OpportunityId is required");
						throw new DestinationException(HttpStatus.BAD_REQUEST, "OpportunityId is required");
					}
					if (task.getConnectId() != null) {
						logger.error("BAD_REQUEST: EntityReference is Opportunity, ConnectId should not be passed");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"EntityReference is Opportunity, ConnectId should not be passed");
					}
					if (!opportunityRepository.exists(task.getOpportunityId())) {
						logger.error("NOT_FOUND: OpportunityId not found");
						throw new DestinationException(HttpStatus.NOT_FOUND, "OpportunityId not found");
					}
				}
			} else {
				logger.error("BAD_REQUEST: Invalid Task Entity Reference");
				throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid Task Entity Reference");
			}
		}
		
		//Validate Task Status
		if (task.getTaskStatus() != null) {
			logger.debug("Task Status NOT NULL");
			if (!TaskStatus.contains(task.getTaskStatus()))
			{
				logger.error("BAD_REQUEST: Invalid Task Status");
				throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid Task Status");
			}				
		}
		
		//Validate Task Collaboration Preference
		if (task.getCollaborationPreference() != null) {
			logger.debug("Task Collaboration Preference NOT NULL");
			String collaborationPreference = task.getCollaborationPreference();
			if (TaskCollaborationPreference.contains(collaborationPreference)) {
				//If BDM collaboration preference is Restricted, one or more BDMs should be tagged
				if (TaskCollaborationPreference.RESTRICTED.equalsName(collaborationPreference)) {
					if (task.getTaskBdmsTaggedLinkTs() == null) {
						logger.error("BAD_REQUEST: BDM Collaboration preference is Restricted, one or more BDMs should be tagged");
						throw new DestinationException(HttpStatus.BAD_REQUEST, 
								"BDM Collaboration preference is Restricted, one or more BDMs should be tagged");
					}
				}
				
			} else {
				logger.error("BAD_REQUEST: Invalid Task BDM Collaboration Preference");
				throw new DestinationException(
						HttpStatus.BAD_REQUEST, "Invalid Task BDM Collaboration Preference");
			}
		}
	}
}