package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;
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

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the collabration_comment_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="commentId")
@Entity
@Table(name="collaboration_comment_t")
@NamedQuery(name="CollaborationCommentT.findAll", query="SELECT c FROM CollaborationCommentT c")
public class CollaborationCommentT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="comment_id")
	private String commentId;

	@Column(name="comment_type")
	private String commentType;

	private String comments;

	@Column(name="documents_attached")
	private String documentsAttached;

	@Column(name="entity_type")
	private String entityType;

	@Column(name="task_id")
	private String taskId;

	@Column(name="updated_datetime")
	private Timestamp updatedDatetime;

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
	@JoinColumn(name="user_id")
	private UserT userT;

	//bi-directional many-to-one association to DocumentRepositoryT
	@OneToMany(mappedBy="collaborationCommentT")
	private List<DocumentRepositoryT> documentRepositoryTs;

	public CollaborationCommentT() {
	}

	public String getCommentId() {
		return this.commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getCommentType() {
		return this.commentType;
	}

	public void setCommentType(String commentType) {
		this.commentType = commentType;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getDocumentsAttached() {
		return this.documentsAttached;
	}

	public void setDocumentsAttached(String documentsAttached) {
		this.documentsAttached = documentsAttached;
	}

	public String getEntityType() {
		return this.entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getTaskId() {
		return this.taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Timestamp getUpdatedDatetime() {
		return this.updatedDatetime;
	}

	public void setUpdatedDatetime(Timestamp updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
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

	public List<DocumentRepositoryT> getDocumentRepositoryTs() {
		return this.documentRepositoryTs;
	}

	public void setDocumentRepositoryTs(List<DocumentRepositoryT> documentRepositoryTs) {
		this.documentRepositoryTs = documentRepositoryTs;
	}

	public DocumentRepositoryT addDocumentRepositoryT(DocumentRepositoryT documentRepositoryT) {
		getDocumentRepositoryTs().add(documentRepositoryT);
		documentRepositoryT.setCollaborationCommentT(this);

		return documentRepositoryT;
	}

	public DocumentRepositoryT removeDocumentRepositoryT(DocumentRepositoryT documentRepositoryT) {
		getDocumentRepositoryTs().remove(documentRepositoryT);
		documentRepositoryT.setCollaborationCommentT(null);

		return documentRepositoryT;
	}

}