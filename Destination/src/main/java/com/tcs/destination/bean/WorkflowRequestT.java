package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the workflow_request_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="requestId")
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
	private String entityId;

	@Column(name="entity_type_id")
	private Integer entityTypeId;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="modified_by")
	private String modifiedBy;
	
	private String status;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by", updatable = false, insertable = false)
	private UserT createdByUser;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="modified_by", updatable = false, insertable = false)
	private UserT modifiedByUser;

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

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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