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
	//Total number of steps in workflow
	 private int numberOfSteps;
	 //Contains stepId and corresponding status of step and approver List.
	 private List<StepDetailsDTO> listOfSteps;
	 
	public WorkflowCustomerT getRequestedCustomer() {
		return requestedCustomer;
	}
	public void setRequestedCustomer(WorkflowCustomerT requestedCustomer) {
		this.requestedCustomer = requestedCustomer;
	}
	public int getNumberOfSteps() {
		return numberOfSteps;
	}
	public void setNumberOfSteps(int numberOfSteps) {
		this.numberOfSteps = numberOfSteps;
	}
	public List<StepDetailsDTO> getListOfSteps() {
		return listOfSteps;
	}
	public void setListOfSteps(List<StepDetailsDTO> listOfSteps) {
		this.listOfSteps = listOfSteps;
	}
	 

}
