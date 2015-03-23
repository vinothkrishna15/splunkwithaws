package com.tcs.destination.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import com.tcs.destination.bean.TaskT;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.exception.NoDataFoundException;

/**
 * Service class to handle Task module related requests.
 * 
 */
@Component
public class TaskService {

	@Autowired
	TaskRepository taskRepository;
	
	/**
	 * This method is used to find task details for the given task id.
	 * 
	 * @param taskId
	 * @return task details for the given task id.
	 */
	public TaskT findTaskById(String taskId) {
		TaskT task = taskRepository.findOne(taskId);
		
		if (task == null)
			throw new NoDataFoundException();
		
		//Set the TaskOwner details from User object
		if (task.getUserT() != null) {
			task.setTaskOwnerName(task.getUserT().getUserName());
		}
		return task;
	}
	
	/**
	 * This method is used to find all the tasks for the given connect id.
	 * 
	 * @param connectId
	 * @return tasks for the given connect id.
	 */
	public List<TaskT> findTasksByConnectId(String connectId) {
		List<TaskT> taskList = taskRepository.findByConnectId(connectId);
		
		if ((taskList == null) || taskList.isEmpty())
			throw new NoDataFoundException();

		//Set the TaskOwner details from User object
		for(TaskT task : taskList) {
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
	public List<TaskT> findTasksByOpportunityId(String opportunityId) {
		List<TaskT> taskList = taskRepository.findByOpportunityId(opportunityId);
		
		if ((taskList == null) || taskList.isEmpty())
			throw new NoDataFoundException();

		//Set the TaskOwner details from User object
		for(TaskT task : taskList) {
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
	public TaskT insertTask(TaskT task) {
		return taskRepository.save(task);
	}

}