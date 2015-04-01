package com.tcs.destination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.TaskT;
import com.tcs.destination.service.TaskService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.bean.Status;

import java.util.List;
import java.util.Date;

/**
 * Controller to handle Task module related requests.
 * 
 */
@RestController
@RequestMapping("/task")
public class TaskController {

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
		TaskT task = taskService.findTaskById(taskId);
		return new ResponseEntity<String>
			(Constants.filterJsonForFieldAndViews(fields, view, task), HttpStatus.OK);
	}

	/**
	 * This method is used to find all the tasks with the given task description.
	 * 
	 * @param nameWith is the task description.
	 * @return tasks with the given task description.
	 */
	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findTasksWithName(
			@RequestParam(value="nameWith") String chars,
			@RequestParam(value="fields", defaultValue="all") String fields,
			@RequestParam(value="view", defaultValue="") String view) throws Exception 
	{
		List<TaskT> taskList = taskService.findTasksByNameContaining(chars);
		return new ResponseEntity<String>
			(Constants.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);  
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
		List<TaskT> taskList = taskService.findTasksByConnectId(connectId);
		return new ResponseEntity<String>
			(Constants.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);  
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
		List<TaskT> taskList = taskService.findTasksByOpportunityId(opportunityId);
		return new ResponseEntity<String>
			(Constants.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);  
	}

	/**
	 * This method is used to find all the tasks for the given owner id.
	 * 
	 * @param id is the owner id.
	 * @return tasks for the given owner id.
	 */
	@RequestMapping(value="/findByOwner", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findTasksByTaskOwner(
			@RequestParam(value="id") String taskOwner,
			@RequestParam(value="fields", defaultValue="all") String fields,
			@RequestParam(value="view", defaultValue="") String view) throws Exception 
	{
		List<TaskT> taskList = taskService.findTasksByTaskOwner(taskOwner);
		return new ResponseEntity<String>
			(Constants.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);  
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
		List<TaskT> taskList = taskService.findTasksAssignedtoOthersByUser(userId);
		return new ResponseEntity<String>
			(Constants.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);  
	}

	/**
	 * This method is used to find all the tasks assigned to a user with a specific target completion date.
	 * 
	 * @param id is the user id, date is the target completion date
	 * @return tasks assigned to a user with a specific target completion date.
	 */
	@RequestMapping(value="/findByTargetDate", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findTasksByUserAndTargetDate(
			@RequestParam(value="id") String userId,
			@RequestParam(value="date") @DateTimeFormat(iso = ISO.DATE) Date targetDate,
			@RequestParam(value="fields", defaultValue="all") String fields,
			@RequestParam(value="view", defaultValue="") String view) throws Exception 
	{
		List<TaskT> taskList = taskService.findTasksByUserAndTargetDate(userId, targetDate);
		return new ResponseEntity<String>
			(Constants.filterJsonForFieldAndViews(fields, view, taskList), HttpStatus.OK);  
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
		TaskT managedTask = null;
		Status status = null;
		managedTask = taskService.createTask(task);
		if ((managedTask != null) && (managedTask.getTaskId() != null)) {
			status = new Status();
			status.setStatus(Status.SUCCESS, managedTask.getTaskId());
		}
		return new ResponseEntity<String>
			(Constants.filterJsonForFieldAndViews("all", "", status), HttpStatus.OK);
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
		TaskT managedTask = null;
		Status status = null;
		managedTask = taskService.editTask(task);
		if (managedTask != null)  {
			status = new Status();
			status.setStatus(Status.SUCCESS, managedTask.getTaskId());
		}
		return new ResponseEntity<String>
			(Constants.filterJsonForFieldAndViews("all", "", status), HttpStatus.OK);
	}
}