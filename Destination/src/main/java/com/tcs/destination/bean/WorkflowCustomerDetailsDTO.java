package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class WorkflowCustomerDetailsDTO implements Serializable {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Stores requested customer details
	private WorkflowCustomerT requestedCustomer;
	 //Details of workflow steps
	 private List<WorkflowStepT> workflowSteps;
	 //Status of the request
	 private String status;
	 
	public WorkflowCustomerT getRequestedCustomer() {
		return requestedCustomer;
	}
	public void setRequestedCustomer(WorkflowCustomerT requestedCustomer) {
		this.requestedCustomer = requestedCustomer;
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
