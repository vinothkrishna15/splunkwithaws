package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the workflow_request_t database table.
 * 
 */
@Entity
@Table(name="workflow_request_t")
@NamedQuery(name="WorkflowRequestT.findAll", query="SELECT w FROM WorkflowRequestT w")
public class WorkflowRequestT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="request_id")
	private Integer requestId;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="entity_id")
	private Integer entityId;

	@Column(name="entity_type_id")
	private Integer entityTypeId;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by")
	private UserT userT1;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="modified_by")
	private UserT userT2;

	//bi-directional many-to-one association to WorkflowStepT
	@OneToMany(mappedBy="workflowRequestT")
	private List<WorkflowStepT> workflowStepTs;

	public WorkflowRequestT() {
	}

	public Integer getRequestId() {
		return this.requestId;
	}

	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public Integer getEntityId() {
		return this.entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public Integer getEntityTypeId() {
		return this.entityTypeId;
	}

	public void setEntityTypeId(Integer entityTypeId) {
		this.entityTypeId = entityTypeId;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
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

	public List<WorkflowStepT> getWorkflowStepTs() {
		return this.workflowStepTs;
	}

	public void setWorkflowStepTs(List<WorkflowStepT> workflowStepTs) {
		this.workflowStepTs = workflowStepTs;
	}

	public WorkflowStepT addWorkflowStepT(WorkflowStepT workflowStepT) {
		getWorkflowStepTs().add(workflowStepT);
		workflowStepT.setWorkflowRequestT(this);

		return workflowStepT;
	}

	public WorkflowStepT removeWorkflowStepT(WorkflowStepT workflowStepT) {
		getWorkflowStepTs().remove(workflowStepT);
		workflowStepT.setWorkflowRequestT(null);

		return workflowStepT;
	}

}