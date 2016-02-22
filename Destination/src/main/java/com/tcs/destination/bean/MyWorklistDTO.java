package com.tcs.destination.bean;

import java.io.Serializable;

public class MyWorklistDTO implements Serializable {

	private WorkflowStepT workflowStep;
	private String entityType;
	private String entityName;
	public WorkflowStepT getWorkflowStep() {
		return workflowStep;
	}
	public void setWorkflowStep(WorkflowStepT workflowStep) {
		this.workflowStep = workflowStep;
	}
	public String getEntityType() {
		return entityType;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	
	
}
