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
 * The persistent class for the document_repository_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="documentId")
@Entity
@Table(name="document_repository_t")
@NamedQuery(name="DocumentRepositoryT.findAll", query="SELECT d FROM DocumentRepositoryT d")
public class DocumentRepositoryT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="document_id")
	private String documentId;

	@Column(name="document_name")
	private String documentName;

	@Column(name="document_search_keywords")
	private String documentSearchKeywords;

	@Column(name="document_type")
	private String documentType;

	@Column(name="entity_type")
	private String entityType;

	@Column(name="file_reference")
	private byte[] fileReference;

	@Column(name="parent_entity")
	private String parentEntity;

	@Column(name="parent_entity_id")
	private String parentEntityId;

	@Column(name="uploaded_datetime")
	private Timestamp uploadedDatetime;

	//bi-directional many-to-one association to CollaborationCommentT
	@ManyToOne
	@JoinColumn(name="comment_id")
	private CollaborationCommentT collaborationCommentT;

	//bi-directional many-to-one association to ConnectT
	@ManyToOne
	@JoinColumn(name="connect_id")
	private ConnectT connectT;

	//bi-directional many-to-one association to CustomerMasterT
	@ManyToOne
	@JoinColumn(name="customer_id")
	private CustomerMasterT customerMasterT;

	//bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name="opportunity_id")
	private OpportunityT opportunityT;

	//bi-directional many-to-one association to PartnerMasterT
	@ManyToOne
	@JoinColumn(name="partner_id")
	private PartnerMasterT partnerMasterT;

	//bi-directional many-to-one association to TaskT
	@ManyToOne
	@JoinColumn(name="task_id")
	private TaskT taskT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="uploaded_by")
	private UserT userT;

	//bi-directional many-to-one association to UserFavoritesT
	@OneToMany(mappedBy="documentRepositoryT")
	private List<UserFavoritesT> userFavoritesTs;

	public DocumentRepositoryT() {
	}

	public String getDocumentId() {
		return this.documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getDocumentName() {
		return this.documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDocumentSearchKeywords() {
		return this.documentSearchKeywords;
	}

	public void setDocumentSearchKeywords(String documentSearchKeywords) {
		this.documentSearchKeywords = documentSearchKeywords;
	}

	public String getDocumentType() {
		return this.documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getEntityType() {
		return this.entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public byte[] getFileReference() {
		return this.fileReference;
	}

	public void setFileReference(byte[] fileReference) {
		this.fileReference = fileReference;
	}

	public String getParentEntity() {
		return this.parentEntity;
	}

	public void setParentEntity(String parentEntity) {
		this.parentEntity = parentEntity;
	}

	public String getParentEntityId() {
		return this.parentEntityId;
	}

	public void setParentEntityId(String parentEntityId) {
		this.parentEntityId = parentEntityId;
	}

	public Timestamp getUploadedDatetime() {
		return this.uploadedDatetime;
	}

	public void setUploadedDatetime(Timestamp uploadedDatetime) {
		this.uploadedDatetime = uploadedDatetime;
	}

	public CollaborationCommentT getCollaborationCommentT() {
		return this.collaborationCommentT;
	}

	public void setCollaborationCommentT(CollaborationCommentT collaborationCommentT) {
		this.collaborationCommentT = collaborationCommentT;
	}

	public ConnectT getConnectT() {
		return this.connectT;
	}

	public void setConnectT(ConnectT connectT) {
		this.connectT = connectT;
	}

	public CustomerMasterT getCustomerMasterT() {
		return this.customerMasterT;
	}

	public void setCustomerMasterT(CustomerMasterT customerMasterT) {
		this.customerMasterT = customerMasterT;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

	public PartnerMasterT getPartnerMasterT() {
		return this.partnerMasterT;
	}

	public void setPartnerMasterT(PartnerMasterT partnerMasterT) {
		this.partnerMasterT = partnerMasterT;
	}

	public TaskT getTaskT() {
		return this.taskT;
	}

	public void setTaskT(TaskT taskT) {
		this.taskT = taskT;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

	public List<UserFavoritesT> getUserFavoritesTs() {
		return this.userFavoritesTs;
	}

	public void setUserFavoritesTs(List<UserFavoritesT> userFavoritesTs) {
		this.userFavoritesTs = userFavoritesTs;
	}

	public UserFavoritesT addUserFavoritesT(UserFavoritesT userFavoritesT) {
		getUserFavoritesTs().add(userFavoritesT);
		userFavoritesT.setDocumentRepositoryT(this);

		return userFavoritesT;
	}

	public UserFavoritesT removeUserFavoritesT(UserFavoritesT userFavoritesT) {
		getUserFavoritesTs().remove(userFavoritesT);
		userFavoritesT.setDocumentRepositoryT(null);

		return userFavoritesT;
	}

}