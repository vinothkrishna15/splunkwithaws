package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the workflow_step_t database table.
 * 
 */

public class ApproveOrRejectStubResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private String stepStatus;

	private String userId;
	private String comments;
	
	//private String workflowRequestT;

	public ApproveOrRejectStubResponse() {
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getStepStatus() {
		return this.stepStatus;
	}

	public void setStepStatus(String stepStatus) {
		this.stepStatus = stepStatus;
	}

	public String getUserT1() {
		return this.userId;
	}

	public void setUserT1(String userT1) {
		this.userId = userT1;
	}

	/*public String getWorkflowRequestT() {
		return this.workflowRequestT;
	}

	public void setWorkflowRequestT(String workflowRequestT) {
		this.workflowRequestT = workflowRequestT;
	}*/


}