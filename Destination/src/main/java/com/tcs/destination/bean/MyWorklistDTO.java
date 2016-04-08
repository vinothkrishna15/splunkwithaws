package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MyWorklistDTO implements Serializable, Comparable<MyWorklistDTO> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private WorkflowStepT workflowStep;
	private String entityType;
	private String entityName;
	private Timestamp modifiedDatetime;
	@JsonIgnore
	private Integer requestId;
	
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
	
	
	public Integer getRequestId() {
		return requestId;
	}
	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}
	@Override
    public int hashCode() {
        return requestId.hashCode();
    }

	@Override
	public boolean equals(Object object) {
		boolean result = false;
		if (object == null || object.getClass() != getClass()) {
			result = false;
		} else {
			MyWorklistDTO myWorklistDTO = (MyWorklistDTO) object;
			if (this.requestId == myWorklistDTO.getRequestId()) {
				result = true;
			}
		}
		return result;
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
