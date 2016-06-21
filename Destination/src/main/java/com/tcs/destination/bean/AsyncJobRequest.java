/**
 * 
 * AsyncJobRequest.java 
 *
 * @author TCS
 * @Version 1.0 - 2016
 * 
 * @Copyright 2016 Tata Consultancy 
 */
package com.tcs.destination.bean;

import java.io.Serializable;

import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.JobName;
import com.tcs.destination.enums.Switch;

/**
 * This AsyncJobRequest class <description>
 * 
 */
public class AsyncJobRequest implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JobName jobName;
	private EntityType entityType;
	private String entityId;
	private Switch on;
	
	public AsyncJobRequest() {
		on = Switch.OFF;
	}
	
	public JobName getJobName() {
		return jobName;
	}
	public void setJobName(JobName jobName) {
		this.jobName = jobName;
	}
	public EntityType getEntityType() {
		return entityType;
	}
	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public Switch getOn() {
		return on;
	}
	public void setOn(Switch on) {
		this.on = on;
	}
	
	
}
