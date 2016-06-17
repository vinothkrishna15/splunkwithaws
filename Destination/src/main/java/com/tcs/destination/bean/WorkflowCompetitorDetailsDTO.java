package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

public class WorkflowCompetitorDetailsDTO  implements Serializable{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Stores requested competitor details
	private WorkflowCompetitorT requestedCompetitor;
	 //Details of workflow steps
	 private List<WorkflowStepT> workflowSteps;
	 //Status of the request
	 private String status;
	 
	 
	public WorkflowCompetitorT getRequestedCompetitor() {
		return requestedCompetitor;
	}
	public void setRequestedCompetitor(WorkflowCompetitorT requestedCompetitor) {
		this.requestedCompetitor = requestedCompetitor;
	}
	public List<WorkflowStepT> getWorkflowSteps() {
		return workflowSteps;
	}
	public void setWorkflowSteps(List<WorkflowStepT> workflowSteps) {
		this.workflowSteps = workflowSteps;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
