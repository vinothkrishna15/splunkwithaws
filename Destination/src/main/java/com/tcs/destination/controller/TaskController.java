package com.tcs.destination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.TaskBdmsTaggedLinkT;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.data.repository.TaskBdmsTaggedLinkRepository;
import com.tcs.destination.service.TaskService;
import com.tcs.destination.utils.Constants;

import java.util.List;

/**
 * Controller to handle Task module related requests.
 * 
 */
@RestController
@RequestMapping("/task")
public class TaskController {

	@Autowired
	TaskService taskService;
	
	@Autowired
	TaskBdmsTaggedLinkRepository taskBdmsTaggedLinkRepository;

	/**
	 * This method is used to find task details for the given task id.
	 * 
	 * @param id is the task id.
	 * @return task details for the given task id.
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public @ResponseBody String findTaskById(
			@PathVariable("id") String taskId,
			@RequestParam(value="fields", defaultValue="all") String fields,
	     	@RequestParam(value="view", defaultValue="") String view) {
	  TaskT task = taskService.findTaskById(taskId);
      return Constants.filterJsonForFieldAndViews(fields, view, task);
	}
	
	/**
	 * This method is used to find all the tasks for the given connect id.
	 * 
	 * @param id is the connect id.
	 * @return tasks for the given connect id.
	 */
	@RequestMapping(value="/findByConnect", method=RequestMethod.GET)
	public @ResponseBody String findTasksByConnectId(
			@RequestParam(value="id", defaultValue="") String connectId,
			@RequestParam(value="fields", defaultValue="all") String fields,
	     	@RequestParam(value="view", defaultValue="") String view) {
	  List<TaskT> taskList = taskService.findTasksByConnectId(connectId);
      return Constants.filterJsonForFieldAndViews(fields, view, taskList);
	}
	
	/**
	 * This method is used to find all the tasks for the given opportunity id.
	 * 
	 * @param id is the opportunity id.
	 * @return tasks for the given opportunity id.
	 */
	@RequestMapping(value="/findByOpportunity", method=RequestMethod.GET)
	public @ResponseBody String findTasksByOpportunityId(
			@RequestParam(value="id", defaultValue="") String opportunityId,
			@RequestParam(value="fields", defaultValue="all") String fields,
	     	@RequestParam(value="view", defaultValue="") String view) {
	  List<TaskT> taskList = taskService.findTasksByOpportunityId(opportunityId);
      return Constants.filterJsonForFieldAndViews(fields, view, taskList);
	}

	/**
	 * This method is used to create a new task for a given Connect or Opportunity.
	 * 
	 * @param 
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	public @ResponseBody String insertToTask(@RequestBody TaskT task) {
		Status status = new Status();
		status.setStatus(Status.FAILED);
		
		List<TaskBdmsTaggedLinkT> taskBdmsTaggedLinkTs = null; 
		try {
			//TaskBdmsTaggedLinkT contains a not null task_id, so save the parent task first
			if (task.getTaskBdmsTaggedLinkTs() != null) {
				taskBdmsTaggedLinkTs = task.getTaskBdmsTaggedLinkTs();
				task.setTaskBdmsTaggedLinkTs(null);
			}
			 
			TaskT managedTask = taskService.insertTask(task);
			if ((null != managedTask) && managedTask.getTaskId() != null) {
				for (TaskBdmsTaggedLinkT taskBdmTaggedLink: taskBdmsTaggedLinkTs) {
					taskBdmTaggedLink.setTaskT(managedTask);
				}
			}

			//Persist TaskBdmsTaggedLinkT
			taskBdmsTaggedLinkRepository.save(taskBdmsTaggedLinkTs);
			status.setStatus(Status.SUCCESS);
		} catch (Exception e) {
			throw e;
		}
		return Constants.filterJsonForFieldAndViews("all", "", status);
	}

}