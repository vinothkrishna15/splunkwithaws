package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the task_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="taskId")
@Entity
@Table(name="task_t")
@NamedQuery(name="TaskT.findAll", query="SELECT t FROM TaskT t")
public class TaskT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="task_id")
	private String taskId;

	@Column(name="collaboration_preference")
	private String collaborationPreference;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="documents_attached")
	private String documentsAttached;

	@Column(name="entity_reference")
	private String entityReference;

	@Temporal(TemporalType.DATE)
	@Column(name="target_date_for_completion")
	private Date targetDateForCompletion;

	@Column(name="task_description")
	private String taskDescription;

	@Column(name="task_status")
	private String taskStatus;

	//bi-directional many-to-one association to DocumentRepositoryT
	@OneToMany(mappedBy="taskT")
	private List<DocumentRepositoryT> documentRepositoryTs;

	//bi-directional many-to-one association to TaskBdmsTaggedLinkT
	@OneToMany(mappedBy="taskT")
	private List<TaskBdmsTaggedLinkT> taskBdmsTaggedLinkTs;

	//bi-directional many-to-one association to ConnectT
	@ManyToOne
	@JoinColumn(name="connect_id")
	private ConnectT connectT;

	//bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name="opportunity_id")
	private OpportunityT opportunityT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="task_owner")
	private UserT userT;

	public TaskT() {
	}

	public String getTaskId() {
		return this.taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getCollaborationPreference() {
		return this.collaborationPreference;
	}

	public void setCollaborationPreference(String collaborationPreference) {
		this.collaborationPreference = collaborationPreference;
	}

	public String getCreatedModifiedBy() {
		return this.createdModifiedBy;
	}

	public void setCreatedModifiedBy(String createdModifiedBy) {
		this.createdModifiedBy = createdModifiedBy;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return this.createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
	}

	public String getDocumentsAttached() {
		return this.documentsAttached;
	}

	public void setDocumentsAttached(String documentsAttached) {
		this.documentsAttached = documentsAttached;
	}

	public String getEntityReference() {
		return this.entityReference;
	}

	public void setEntityReference(String entityReference) {
		this.entityReference = entityReference;
	}

	public Date getTargetDateForCompletion() {
		return this.targetDateForCompletion;
	}

	public void setTargetDateForCompletion(Date targetDateForCompletion) {
		this.targetDateForCompletion = targetDateForCompletion;
	}

	public String getTaskDescription() {
		return this.taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public String getTaskStatus() {
		return this.taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public List<DocumentRepositoryT> getDocumentRepositoryTs() {
		return this.documentRepositoryTs;
	}

	public void setDocumentRepositoryTs(List<DocumentRepositoryT> documentRepositoryTs) {
		this.documentRepositoryTs = documentRepositoryTs;
	}

	public DocumentRepositoryT addDocumentRepositoryT(DocumentRepositoryT documentRepositoryT) {
		getDocumentRepositoryTs().add(documentRepositoryT);
		documentRepositoryT.setTaskT(this);

		return documentRepositoryT;
	}

	public DocumentRepositoryT removeDocumentRepositoryT(DocumentRepositoryT documentRepositoryT) {
		getDocumentRepositoryTs().remove(documentRepositoryT);
		documentRepositoryT.setTaskT(null);

		return documentRepositoryT;
	}

	public List<TaskBdmsTaggedLinkT> getTaskBdmsTaggedLinkTs() {
		return this.taskBdmsTaggedLinkTs;
	}

	public void setTaskBdmsTaggedLinkTs(List<TaskBdmsTaggedLinkT> taskBdmsTaggedLinkTs) {
		this.taskBdmsTaggedLinkTs = taskBdmsTaggedLinkTs;
	}

	public TaskBdmsTaggedLinkT addTaskBdmsTaggedLinkT(TaskBdmsTaggedLinkT taskBdmsTaggedLinkT) {
		getTaskBdmsTaggedLinkTs().add(taskBdmsTaggedLinkT);
		taskBdmsTaggedLinkT.setTaskT(this);

		return taskBdmsTaggedLinkT;
	}

	public TaskBdmsTaggedLinkT removeTaskBdmsTaggedLinkT(TaskBdmsTaggedLinkT taskBdmsTaggedLinkT) {
		getTaskBdmsTaggedLinkTs().remove(taskBdmsTaggedLinkT);
		taskBdmsTaggedLinkT.setTaskT(null);

		return taskBdmsTaggedLinkT;
	}

	public ConnectT getConnectT() {
		return this.connectT;
	}

	public void setConnectT(ConnectT connectT) {
		this.connectT = connectT;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

}