package com.tcs.destination.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

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
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.TaskService;
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
			@RequestParam(value="view", defaultValue="") String view) throws Exception 
	{
		logger.debug("Inside TaskController /task/id="+taskId+" GET");
		TaskT task = taskService.findTaskById(taskId);
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, task), HttpStatus.OK);
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
			@RequestParam(value="view", defaultValue="") String view) throws Exception 
	{
		logger.debug("Inside TaskController /task?nameWith="+nameWith+" GET");
		List<TaskT> taskList = taskService.findTasksByNameContaining(nameWith);
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);  
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
			@RequestParam(value="view", defaultValue="") String view) throws Exception 
	{
		logger.debug("Inside TaskController /task/findByConnect?id="+connectId+" GET");
		List<TaskT> taskList = taskService.findTasksByConnectId(connectId);
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);  
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
			@RequestParam(value="view", defaultValue="") String view) throws Exception 
	{
		logger.debug("Inside TaskController /task/findByOpportunity?id="+opportunityId+" GET");
		List<TaskT> taskList = taskService.findTasksByOpportunityId(opportunityId);
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);  
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
			@RequestParam(value="view", defaultValue="") String view) throws Exception 
	{
		logger.debug("Inside TaskController /task/findByOwner?id="+taskOwner+" GET");
		List<TaskT> taskList = taskService.findTasksByTaskOwnerAndStatus(taskOwner, taskStatus);
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);  
	}

	/**
	 * This method is used to find all the tasks assigned to others by the given user id.
	 * 
	 * @param id is the user id.
	 * @return tasks assigned to others by the given user id.
	 */
	@RequestMapping(value="/findAssigned", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findTasksAssignedToOthersByUser(
			@RequestParam(value="id") String userId,
			@RequestParam(value="fields", defaultValue="all") String fields,
			@RequestParam(value="view", defaultValue="") String view) throws Exception 
	{
		logger.debug("Inside TaskController /task/findAssigned?id="+userId+" GET");
		List<TaskT> taskList = taskService.findTasksAssignedtoOthersByUser(userId);
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);  
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
			@RequestParam(value="id") String userId,
			@RequestParam(value="fromDate") @DateTimeFormat(iso = ISO.DATE) Date fromDate,
			@RequestParam(value="toDate") @DateTimeFormat(iso = ISO.DATE) Date toDate,
			@RequestParam(value="fields", defaultValue="all") String fields,
			@RequestParam(value="view", defaultValue="") String view) throws Exception 
	{
		logger.debug("Inside TaskController /task/findByTargetDate?id"+userId+" GET");
		List<TaskT> taskList = taskService.findTasksByUserAndTargetDate(userId, fromDate, toDate);
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);  
	}
	
	/**
	 * This method is used to create a new task for a given Connect or Opportunity.
	 * 
	 * @param TaskT
	 * @return ResponseEntity
	 */
	@RequestMapping(method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> createTask(@RequestBody TaskT task) 
			throws Exception {
		logger.debug("Inside TaskController /task POST");
		TaskT managedTask = null;
		Status status = null;
		try {
			managedTask = taskService.createTask(task);
		} catch (Exception ex) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
		if ((managedTask != null) && (managedTask.getTaskId() != null)) {
			logger.debug("Managed Task and Task Id NOT NULL");
			status = new Status();
			status.setStatus(Status.SUCCESS, managedTask.getTaskId());
		}
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews("all", "", status), HttpStatus.OK);
	}

	/**
	 * This method is used to update a task.
	 * 
	 * @param TaskT
	 * @return ResponseEntity
	 */
	@RequestMapping(method=RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editTask(@RequestBody TaskT task) 
			throws Exception {
		logger.debug("Inside TaskController /task PUT");
		TaskT managedTask = null;
		Status status = null;
		try {
			managedTask = taskService.editTask(task);
		} catch (Exception ex) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
		if (managedTask != null)  {
			logger.debug("Managed Task NOT NULL");
			status = new Status();
			status.setStatus(Status.SUCCESS, managedTask.getTaskId());
		}
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews("all", "", status), HttpStatus.OK);
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
			@RequestParam(value="view", defaultValue="") String view) throws Exception 
	{
		logger.debug("Inside TaskController /task/team?id="+supervisorId+" GET");
		List<TaskT> taskList = taskService.findTeamTasks(supervisorId,status);
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);  
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
			@RequestParam(value="view", defaultValue="") String view) throws Exception 
	{
		logger.debug("Inside TaskController /task/team/findAssigned?supervisorId="+supervisorId+" GET");
		List<TaskT> taskList = taskService.findTeamTasksAssignedtoOthers(supervisorId);
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);  
	}
}