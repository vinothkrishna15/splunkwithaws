package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the task_t database table.
 * 
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="taskId")
@Entity
@Table(name="task_t")
@NamedQuery(name="TaskT.findAll", query="SELECT t FROM TaskT t")
public class TaskT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="task_id")
	private String taskId;

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

	@Column(name="task_owner")
	private String taskOwner;

	@Column(name="task_status")
	private String taskStatus;

	//bi-directional many-to-one association to CollabrationCommentT
	@OneToMany(mappedBy="taskT")
	private List<CollabrationCommentT> collabrationCommentTs;

	//bi-directional many-to-one association to DocumentRepositoryT
	@OneToMany(mappedBy="taskT")
	private List<DocumentRepositoryT> documentRepositoryTs;

	//bi-directional many-to-one association to NotesT
	@OneToMany(mappedBy="taskT")
	private List<NotesT> notesTs;

	//bi-directional many-to-one association to ConnectT
	@ManyToOne
	@JoinColumn(name="connect_id")
	private ConnectT connectT;

	//bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name="opportunity_id")
	private OpportunityT opportunityT;

	public TaskT() {
	}

	public String getTaskId() {
		return this.taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
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

	public String getTaskOwner() {
		return this.taskOwner;
	}

	public void setTaskOwner(String taskOwner) {
		this.taskOwner = taskOwner;
	}

	public String getTaskStatus() {
		return this.taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public List<CollabrationCommentT> getCollabrationCommentTs() {
		return this.collabrationCommentTs;
	}

	public void setCollabrationCommentTs(List<CollabrationCommentT> collabrationCommentTs) {
		this.collabrationCommentTs = collabrationCommentTs;
	}

	public CollabrationCommentT addCollabrationCommentT(CollabrationCommentT collabrationCommentT) {
		getCollabrationCommentTs().add(collabrationCommentT);
		collabrationCommentT.setTaskT(this);

		return collabrationCommentT;
	}

	public CollabrationCommentT removeCollabrationCommentT(CollabrationCommentT collabrationCommentT) {
		getCollabrationCommentTs().remove(collabrationCommentT);
		collabrationCommentT.setTaskT(null);

		return collabrationCommentT;
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

	public List<NotesT> getNotesTs() {
		return this.notesTs;
	}

	public void setNotesTs(List<NotesT> notesTs) {
		this.notesTs = notesTs;
	}

	public NotesT addNotesT(NotesT notesT) {
		getNotesTs().add(notesT);
		notesT.setTaskT(this);

		return notesT;
	}

	public NotesT removeNotesT(NotesT notesT) {
		getNotesTs().remove(notesT);
		notesT.setTaskT(null);

		return notesT;
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

}