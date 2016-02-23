package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the workflow_step_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property= "stepId")
@Entity
@Table(name="workflow_step_t")
@NamedQuery(name="WorkflowStepT.findAll", query="SELECT w FROM WorkflowStepT w")
public class WorkflowStepT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="step_id")
	private Integer stepId;

	private String comments;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;
	
	@Column(name="user_id")
	private String userId;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="modified_by")
	private String modifiedBy;

	private Integer step;

	@Column(name="step_status")
	private String stepStatus;

	//bi-directional many-to-one association to UserGroupMappingT
	@ManyToOne
	@JoinColumn(name="user_group")
	private UserGroupMappingT userGroupMappingT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="user_id", updatable = false, insertable = false)
	private UserT user;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by", updatable = false, insertable = false)
	private UserT createdByUser;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="modified_by", updatable = false, insertable = false)
	private UserT modifiedByUser;

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

	

	public UserT getUser() {
		return user;
	}

	public void setUser(UserT user) {
		this.user = user;
	}

	public UserT getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserT createdByUser) {
		this.createdByUser = createdByUser;
	}

	public UserT getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}

	public WorkflowRequestT getWorkflowRequestT() {
		return this.workflowRequestT;
	}

	public void setWorkflowRequestT(WorkflowRequestT workflowRequestT) {
		this.workflowRequestT = workflowRequestT;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	
	

}