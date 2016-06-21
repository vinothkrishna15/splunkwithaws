package com.tcs.destination.controller;


import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.JobName;
import com.tcs.destination.enums.OperationType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.TaskService;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * Controller to handle Task module related requests.
 * 
 */
@RestController
@RequestMapping("/task")
public class TaskController {
	
	private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

	@Autowired
	TaskService taskService;
	
	@Autowired
	JobLauncherController jobLauncherController;

	/**
	 * This method is used to find task details for the given task id.
	 * 
	 * @param id is the task id.
	 * @return task details for the given task id.
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findTaskById(
			@PathVariable("id") String taskId,
			@RequestParam(value="fields", defaultValue="all") String fields,
			@RequestParam(value="view", defaultValue="") String view) throws DestinationException 
	{   
		logger.info("Start of getting the Task Details by Task Id");
		try {
		TaskT task = taskService.findTaskById(taskId);
		logger.info("End of getting the Task Details by Task Id");
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, task), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Task Details for task Id : " + taskId);
	   }
	}

	/**
	 * This method is used to find all the tasks with the given task description.
	 * 
	 * @param nameWith is the task description.
	 * @return tasks with the given task description.
	 */
	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findTasksWithName(
			@RequestParam(value="nameWith") String nameWith,
			@RequestParam(value="fields", defaultValue="all") String fields,
			@RequestParam(value="view", defaultValue="") String view) throws DestinationException 
	{   
		logger.info("Start of Retrieving the Task details by name");
		try {
		List<TaskT> taskList = taskService.findTasksByNameContaining(nameWith);
		logger.info("End of Retrieving the Task details by name");
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Task details for name :" + nameWith);
	   }
	}
	
	/**
	 * This method is used to find all the tasks for the given connect id.
	 * 
	 * @param id is the connect id.
	 * @return tasks for the given connect id.
	 */
	@RequestMapping(value="/findByConnect", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findTasksByConnectId(
			@RequestParam(value="id") String connectId,
			@RequestParam(value="fields", defaultValue="all") String fields,
			@RequestParam(value="view", defaultValue="") String view) throws DestinationException 
	{   
		logger.info("Start of retrieving the task details by connect id");
		try {
		List<TaskT> taskList = taskService.findTasksByConnectId(connectId);
		logger.info("End of retrieving the task details by connect id");
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Task details for connect id :" + connectId);
	   }
	}

	/**
	 * This method is used to find all the tasks for the given opportunity id.
	 * 
	 * @param id is the opportunity id.
	 * @return tasks for the given opportunity id.
	 */
	@RequestMapping(value="/findByOpportunity", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findTasksByOpportunityId(
			@RequestParam(value="id") String opportunityId,
			@RequestParam(value="fields", defaultValue="all") String fields,
			@RequestParam(value="view", defaultValue="") String view) throws DestinationException 
	{   
		logger.info("Start of retrieving the task details by opportunity id");
		try {
		List<TaskT> taskList = taskService.findTasksByOpportunityId(opportunityId);
		logger.info("End of retrieving the task details by opportunity id");
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Task details for opportunity id :" + opportunityId);
	   }
	}

	/**
	 * This method is used to find all the tasks for the given owner id.
	 * 
	 * @param id is the owner id.
	 * @param status is the task status
	 * @return tasks for the given owner id.
	 */
	@RequestMapping(value="/findByOwner", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findTasksByTaskOwner(
			@RequestParam(value="id") String taskOwner,
			@RequestParam(value="status", defaultValue="all") String taskStatus,
			@RequestParam(value="fields", defaultValue="all") String fields,
			@RequestParam(value="view", defaultValue="") String view) throws DestinationException 
	{   
		logger.info("Start of retrieving the task details by task owner");
		try {
		List<TaskT> taskList = taskService.findTasksByTaskOwnerAndStatus(taskOwner, taskStatus);
		logger.info("End of retrieving the task details by task owner");
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Task Details for Task Owner :" + taskOwner);
	   }
	}

	/**
	 * This method is used to find all the tasks assigned to others by the given user id.
	 * 
	 * @param id is the user id.
	 * @return tasks assigned to others by the given user id.
	 */
	@RequestMapping(value="/findAssigned", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findTasksAssignedToOthersByUser(
			@RequestParam(value="fields", defaultValue="all") String fields,
			@RequestParam(value="view", defaultValue="") String view) throws DestinationException 
	{   
		logger.info("Start of retreiving the Tasks assigned to others");
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		try {
		List<TaskT> taskList = taskService.findTasksAssignedtoOthersByUser(userId);
		logger.info("End of retreiving the Tasks assigned to others");
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retreiving the Tasks assigned to others by user :" + userId);
	   }
	}

	/**
	 * This method is used to find all the tasks assigned to a user with a specific target completion date.
	 * 
	 * @param id is the user id
	 * @param fromDate is the target completion start date, toDate is target completion end date
	 * @return tasks assigned to a user with a specific target completion start and end date.
	 */
	@RequestMapping(value="/findByTargetDate", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findTasksByUserAndTargetDate(
			@RequestParam(value="fromDate") @DateTimeFormat(iso = ISO.DATE) Date fromDate,
			@RequestParam(value="toDate") @DateTimeFormat(iso = ISO.DATE) Date toDate,
			@RequestParam(value="fields", defaultValue="all") String fields,
			@RequestParam(value="view", defaultValue="") String view) throws DestinationException 
	{   
		logger.info("Start of retreiving the tasks assigned to a user with a specific target completion date");
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		try {
		List<TaskT> taskList = taskService.findTasksByUserAndTargetDate(userId, fromDate, toDate);
		logger.info("End of retreiving the tasks assigned to a user with a specific target completion date");
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retreiving the Tasks assigned to a user with a specific target completion date");
	   }
	}
	
	/**
	 * This method is used to create a new task for a given Connect or Opportunity.
	 * 
	 * @param TaskT
	 * @return ResponseEntity
	 */
	@RequestMapping(method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> createTask(@RequestBody TaskT task) 
			throws DestinationException {
		logger.info("Start of creating a Task");
		TaskT managedTask = null;
		Status status = null;
		try {
			managedTask = taskService.createTask(task);
			if ((managedTask != null) && (managedTask.getTaskId() != null)) {
				logger.debug("Managed Task and Task Id NOT NULL");
				status = new Status();
				status.setStatus(Status.SUCCESS, managedTask.getTaskId());
				jobLauncherController.asyncJobLaunchForNotification(JobName.notification, EntityType.TASK, managedTask.getTaskId(),OperationType.TASK_CREATE,managedTask.getModifiedBy());
			}
			logger.info("End of creating a Task");
			return new ResponseEntity<String>
				(ResponseConstructors.filterJsonForFieldAndViews("all", "", status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while creating a new Task for a given connect or opportunity");
	   }
		
	}

	/**
	 * This method is used to update a task.
	 * 
	 * @param TaskT
	 * @return ResponseEntity
	 */
	@RequestMapping(method=RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editTask(@RequestBody TaskT task) 
			throws DestinationException {
		logger.info("Start of editing a Task");
		TaskT managedTask = null;
		Status status = null;
		try {
			managedTask = taskService.editTask(task);
			if (managedTask != null)  {
				status = new Status();
				status.setStatus(Status.SUCCESS, "Task Successfully Updated with TaskId "+managedTask.getTaskId());
				jobLauncherController.asyncJobLaunchForNotification(JobName.notification, EntityType.TASK, managedTask.getTaskId(),OperationType.TASK_EDIT,managedTask.getModifiedBy());
			}
			logger.info("End of editing a Task");
			return new ResponseEntity<String>
				(ResponseConstructors.filterJsonForFieldAndViews("all", "", status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while editing a new Task for a given connect or opportunity");
	   }
	}
	
	
	/**
	 * This method is used to find all tasks for the team for the given supervisor id.
	 * 
	 * @param id is the supervisor id.
	 * @return team tasks for the given supervisor id.
	 */
	@RequestMapping(value="/team", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findTeamTasks(
			@RequestParam(value="status", defaultValue="all") String status,
			@RequestParam(value="id") String supervisorId,
			@RequestParam(value="fields", defaultValue="all") String fields,
			@RequestParam(value="view", defaultValue="") String view) throws DestinationException 
	{   
		logger.info("Start of retrieving the Team Tasks");
		try {
		List<TaskT> taskList = taskService.findTeamTasks(supervisorId,status);
		logger.info("End of retrieving the Team Tasks");
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Team Tasks for supervisor id :" + supervisorId);
	   }
	}
	
	/**
	 * This method is used to find all the tasks assigned to others by users under a supervisor.
	 * 
	 * @param supervisorId
	 * @return tasks assigned to others by users under a supervisor.
	 */
	@RequestMapping(value="/team/findAssigned", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findTeamTasksAssignedToOthers(
			@RequestParam(value="supervisorId") String supervisorId,
			@RequestParam(value="fields", defaultValue="all") String fields,
			@RequestParam(value="view", defaultValue="") String view) throws DestinationException 
	{   
		logger.info("Start of retrieving the tasks assigned to others by users under a supervisor");
		try {
		List<TaskT> taskList = taskService.findTeamTasksAssignedtoOthers(supervisorId);
		logger.info("End of retrieving the tasks assigned to others by users under a supervisor");
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in the tasks assigned to others by users under the supervisor" + supervisorId);
	   }
	}
}