package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

public class StepDetailsDTO implements Serializable {

	private String stepId;
	private String stepApprover;
	private String stepStatus;
	private WorkflowStepT workflowStep;
	
	public String getStepId() {
		return stepId;
	}
	public void setStepId(String stepId) {
		this.stepId = stepId;
	}
	public String getStepApprover() {
		return stepApprover;
	}
	public void setStepApprover(String stepApprover) {
		this.stepApprover = stepApprover;
	}
	public String getStepStatus() {
		return stepStatus;
	}
	public void setStepStatus(String stepStatus) {
		this.stepStatus = stepStatus;
	}
	public WorkflowStepT getWorkflowStep() {
		return workflowStep;
	}
	public void setWorkflowStep(WorkflowStepT workflowStep) {
		this.workflowStep = workflowStep;
	}
	
	
}
