package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class MyWorklistDTO implements Serializable,Comparable<MyWorklistDTO> {

	private WorkflowStepT workflowStep;
	private String entityType;
	private String entityName;
	private Timestamp modifiedDatetime;
	
	public Timestamp getModifiedDatetime() {
		return modifiedDatetime;
	}
	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}
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
	@Override
	public int compareTo(MyWorklistDTO compareObject) {
		
		long t1= compareObject.getModifiedDatetime().getTime();
		long t2= this.getModifiedDatetime().getTime();
		if(t2 < t1)
            return 1;
		else if(t1 < t2)
            return -1;
		else
            return 0;
	}
	
	
}
