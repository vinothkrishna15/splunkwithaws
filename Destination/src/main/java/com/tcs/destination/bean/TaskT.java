package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
	@GeneratedValue(strategy=GenerationType.IDENTITY)
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

	//bi-directional many-to-one association to CollaborationCommentT
	@OneToMany(mappedBy="taskT")
	private List<CollaborationCommentT> collaborationCommentTs;

	//bi-directional many-to-one association to DocumentRepositoryT
	@OneToMany(mappedBy="taskT")
	private List<DocumentRepositoryT> documentRepositoryTs;

	//bi-directional many-to-one association to NotesT
	@OneToMany(mappedBy="taskT")
	private List<NotesT> notesTs;

	//bi-directional many-to-one association to TaskBdmsTaggedLinkT
	@OneToMany(mappedBy="taskT")
	private List<TaskBdmsTaggedLinkT> taskBdmsTaggedLinkTs;

	//bi-directional many-to-one association to ConnectT
	@ManyToOne
	@JoinColumn(name="connect_id",insertable=false,updatable=false)
	private ConnectT connectT;

	@Column(name="connect_id")
	private String connectId;
	
	public String getConnectId() {
		return connectId;
	}

	public void setConnectId(String connectId) {
		this.connectId = connectId;
	}
	
	//bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name="opportunity_id")
	private OpportunityT opportunityT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="task_owner")
	private UserT userT;

	//bi-directional many-to-one association to UserNotificationsT
	@OneToMany(mappedBy="taskT")
	private List<UserNotificationsT> userNotificationsTs;

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

	public List<CollaborationCommentT> getCollaborationCommentTs() {
		return this.collaborationCommentTs;
	}

	public void setCollaborationCommentTs(List<CollaborationCommentT> collaborationCommentTs) {
		this.collaborationCommentTs = collaborationCommentTs;
	}

	public CollaborationCommentT addCollaborationCommentT(CollaborationCommentT collaborationCommentT) {
		getCollaborationCommentTs().add(collaborationCommentT);
		collaborationCommentT.setTaskT(this);

		return collaborationCommentT;
	}

	public CollaborationCommentT removeCollaborationCommentT(CollaborationCommentT collaborationCommentT) {
		getCollaborationCommentTs().remove(collaborationCommentT);
		collaborationCommentT.setTaskT(null);

		return collaborationCommentT;
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

	public List<UserNotificationsT> getUserNotificationsTs() {
		return this.userNotificationsTs;
	}

	public void setUserNotificationsTs(List<UserNotificationsT> userNotificationsTs) {
		this.userNotificationsTs = userNotificationsTs;
	}

	public UserNotificationsT addUserNotificationsT(UserNotificationsT userNotificationsT) {
		getUserNotificationsTs().add(userNotificationsT);
		userNotificationsT.setTaskT(this);

		return userNotificationsT;
	}

	public UserNotificationsT removeUserNotificationsT(UserNotificationsT userNotificationsT) {
		getUserNotificationsTs().remove(userNotificationsT);
		userNotificationsT.setTaskT(null);

		return userNotificationsT;
	}

}