package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the workflow_step_t database table.
 * 
 */
@Entity
@Table(name="workflow_step_t")
@NamedQuery(name="WorkflowStepT.findAll", query="SELECT w FROM WorkflowStepT w")
public class WorkflowStepT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="step_id")
	private Integer stepId;

	private String comments;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;

	private Integer step;

	@Column(name="step_status")
	private String stepStatus;

	//bi-directional many-to-one association to UserGroupMappingT
	@ManyToOne
	@JoinColumn(name="user_group")
	private UserGroupMappingT userGroupMappingT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="user_id")
	private UserT userT1;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by")
	private UserT userT2;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="modified_by")
	private UserT userT3;

	//bi-directional many-to-one association to WorkflowRequestT
	@ManyToOne
	@JoinColumn(name="request_id")
	private WorkflowRequestT workflowRequestT;

	public WorkflowStepT() {
	}

	public Integer getStepId() {
		return this.stepId;
	}

	public void setStepId(Integer stepId) {
		this.stepId = stepId;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public Integer getStep() {
		return this.step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	public String getStepStatus() {
		return this.stepStatus;
	}

	public void setStepStatus(String stepStatus) {
		this.stepStatus = stepStatus;
	}

	public UserGroupMappingT getUserGroupMappingT() {
		return this.userGroupMappingT;
	}

	public void setUserGroupMappingT(UserGroupMappingT userGroupMappingT) {
		this.userGroupMappingT = userGroupMappingT;
	}

	public UserT getUserT1() {
		return this.userT1;
	}

	public void setUserT1(UserT userT1) {
		this.userT1 = userT1;
	}

	public UserT getUserT2() {
		return this.userT2;
	}

	public void setUserT2(UserT userT2) {
		this.userT2 = userT2;
	}

	public UserT getUserT3() {
		return this.userT3;
	}

	public void setUserT3(UserT userT3) {
		this.userT3 = userT3;
	}

	public WorkflowRequestT getWorkflowRequestT() {
		return this.workflowRequestT;
	}

	public void setWorkflowRequestT(WorkflowRequestT workflowRequestT) {
		this.workflowRequestT = workflowRequestT;
	}

}