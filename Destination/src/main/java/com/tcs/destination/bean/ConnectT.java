package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the connect_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="connectId")
@Entity
@Table(name = "connect_t")
@NamedQuery(name = "ConnectT.findAll", query = "SELECT c FROM ConnectT c")
public class ConnectT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "connect_id")
	private String connectId;

	@Column(name = "connect_category")
	private String connectCategory;

	@Column(name = "connect_name")
	private String connectName;

	@Column(name = "created_modified_by")
	private String createdModifiedBy;

	@Column(name = "created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name = "documents_attached")
	private String documentsAttached;

	@Column(name = "end_datetime_of_connect")
	private Timestamp endDatetimeOfConnect;

	@Column(name = "start_datetime_of_connect")
	private Timestamp startDatetimeOfConnect;

	@Column(name = "primary_owner")
	private String primaryOwner;

	
	@Column(name = "customer_id")
	private String customerId;
	
	@Column(name = "partner_id")
	private String partnerId;
	
	@Column(name = "country")
	private String country;
	
	public ConnectT(ConnectT con){
		this.collaborationCommentTs=con.collaborationCommentTs;
		this.connectCategory=con.connectCategory;
		this.connectCustomerContactLinkTs=con.connectCustomerContactLinkTs;
		this.connectId=con.connectId;
		this.connectName=con.connectName;
		this.connectOfferingLinkTs=con.connectOfferingLinkTs;
		this.connectOpportunityLinkIdTs=con.connectOpportunityLinkIdTs;
		this.connectSecondaryOwnerLinkTs=con.connectSecondaryOwnerLinkTs;
		this.connectSubSpLinkTs=con.connectSubSpLinkTs;
		this.connectTcsAccountContactLinkTs=con.connectTcsAccountContactLinkTs;
		this.country=con.country;
		this.createdModifiedBy=con.createdModifiedBy;
		this.createdModifiedDatetime=con.createdModifiedDatetime;
		this.customerId=con.customerId;
		this.customerMasterT=con.customerMasterT;
		this.documentRepositoryTs=con.documentRepositoryTs;
		this.documentsAttached=con.documentsAttached;
		this.endDatetimeOfConnect=con.endDatetimeOfConnect;
		this.geographyCountryMappingT=con.geographyCountryMappingT;
		this.notesTs=con.notesTs;
		this.partnerId=con.partnerId;
		this.partnerMasterT=con.partnerMasterT;
		this.primaryOwner=con.primaryOwner;
		this.startDatetimeOfConnect=con.startDatetimeOfConnect;
		this.taskTs=con.taskTs;
		this.userFavoritesTs=con.userFavoritesTs;
		this.userNotificationsTs=con.userNotificationsTs;
		this.userT=con.userT;
		
	}

	
	
	// bi-directional many-to-one association to CollaborationCommentT
	@OneToMany(mappedBy = "connectT")
	@OrderBy("updated_datetime DESC")
	private List<CollaborationCommentT> collaborationCommentTs;

	// bi-directional many-to-one association to ConnectCustomerContactLinkT
	@OneToMany(mappedBy = "connectT",cascade=CascadeType.ALL)
	private List<ConnectCustomerContactLinkT> connectCustomerContactLinkTs;

	// bi-directional many-to-one association to ConnectOfferingLinkT
	@OneToMany(mappedBy = "connectT",cascade=CascadeType.ALL)
	private List<ConnectOfferingLinkT> connectOfferingLinkTs;

	// bi-directional many-to-one association to ConnectOpportunityLinkIdT
	@OneToMany(mappedBy = "connectT",cascade=CascadeType.ALL)
	private List<ConnectOpportunityLinkIdT> connectOpportunityLinkIdTs;

	// bi-directional many-to-one association to ConnectSecondaryOwnerLinkT
	@OneToMany(mappedBy = "connectT",cascade=CascadeType.ALL)
	private List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerLinkTs;

	// bi-directional many-to-one association to ConnectSubSpLinkT
	@OneToMany(mappedBy = "connectT",cascade=CascadeType.ALL)
	private List<ConnectSubSpLinkT> connectSubSpLinkTs;

	// bi-directional many-to-one association to CustomerMasterT
	@ManyToOne
	@JoinColumn(name = "customer_id",insertable=false,updatable=false)
	private CustomerMasterT customerMasterT;

	// bi-directional many-to-one association to GeographyCountryMappingT
	@ManyToOne
	@JoinColumn(name = "country",insertable=false,updatable=false)
	private GeographyCountryMappingT geographyCountryMappingT;

	// bi-directional many-to-one association to PartnerMasterT
	@ManyToOne
	@JoinColumn(name = "partner_id",insertable=false,updatable=false)
	private PartnerMasterT partnerMasterT;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "primary_owner", insertable = false, updatable = false)
	private UserT userT;

@ManyToOne
@JoinColumn(name="created_modified_by",insertable=false,updatable=false)
private UserT createdModifiedByUser;

	// bi-directional many-to-one association to ConnectTcsAccountContactLinkT
	@OneToMany(mappedBy = "connectT",cascade=CascadeType.ALL)
	private List<ConnectTcsAccountContactLinkT> connectTcsAccountContactLinkTs;

	// bi-directional many-to-one association to DocumentRepositoryT
	@OneToMany(mappedBy = "connectT")
	private List<DocumentRepositoryT> documentRepositoryTs;

	// bi-directional many-to-one association to NotesT
	@OneToMany(mappedBy = "connectT",cascade=CascadeType.ALL)
	private List<NotesT> notesTs;

	// bi-directional many-to-one association to TaskT
	@OneToMany(mappedBy = "connectT",cascade=CascadeType.ALL)
	private List<TaskT> taskTs;

	// bi-directional many-to-one association to UserFavoritesT
	@OneToMany(mappedBy = "connectT")
	private List<UserFavoritesT> userFavoritesTs;

	//bi-directional many-to-one association to UserNotificationsT
	@OneToMany(mappedBy="connectT")
	private List<UserNotificationsT> userNotificationsTs;

	@Transient
	private List<ConnectSubSpLinkT> connectSubLinkDeletionList;
	
	@Transient
	private List<ConnectOfferingLinkT> connectOfferingLinkDeletionList;
	
	@Transient
	private List<DocumentRepositoryT> documentsDeletionList;

	public ConnectT() {
	}

	public String getConnectId() {
		return this.connectId;
	}

	public void setConnectId(String connectId) {
		this.connectId = connectId;
	}

	public String getConnectCategory() {
		return this.connectCategory;
	}

	public void setConnectCategory(String connectCategory) {
		this.connectCategory = connectCategory;
	}

	public String getConnectName() {
		return this.connectName;
	}

	public void setConnectName(String connectName) {
		this.connectName = connectName;
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

	public Timestamp getEndDatetimeOfConnect() {
		return this.endDatetimeOfConnect;
	}

	public void setEndDatetimeOfConnect(Timestamp endDatetimeOfConnect) {
		this.endDatetimeOfConnect = endDatetimeOfConnect;
	}

	public Timestamp getStartDatetimeOfConnect() {
		return this.startDatetimeOfConnect;
	}

	public void setStartDatetimeOfConnect(Timestamp startDatetimeOfConnect) {
		this.startDatetimeOfConnect = startDatetimeOfConnect;
	}

	public List<CollaborationCommentT> getCollaborationCommentTs() {
		return this.collaborationCommentTs;
	}

	public void setCollaborationCommentTs(
			List<CollaborationCommentT> collaborationCommentTs) {
		this.collaborationCommentTs = collaborationCommentTs;
	}

	public CollaborationCommentT addCollaborationCommentT(
			CollaborationCommentT collaborationCommentT) {
		getCollaborationCommentTs().add(collaborationCommentT);
		collaborationCommentT.setConnectT(this);

		return collaborationCommentT;
	}

	public CollaborationCommentT removeCollaborationCommentT(
			CollaborationCommentT collaborationCommentT) {
		getCollaborationCommentTs().remove(collaborationCommentT);
		collaborationCommentT.setConnectT(null);

		return collaborationCommentT;
	}

	
	public List<ConnectCustomerContactLinkT> getConnectCustomerContactLinkTs() {
		return this.connectCustomerContactLinkTs;
	}

	public void setConnectCustomerContactLinkTs(
			List<ConnectCustomerContactLinkT> connectCustomerContactLinkTs) {
		this.connectCustomerContactLinkTs = connectCustomerContactLinkTs;
	}

	public ConnectCustomerContactLinkT addConnectCustomerContactLinkT(
			ConnectCustomerContactLinkT connectCustomerContactLinkT) {
		getConnectCustomerContactLinkTs().add(connectCustomerContactLinkT);
		connectCustomerContactLinkT.setConnectT(this);

		return connectCustomerContactLinkT;
	}

	public ConnectCustomerContactLinkT removeConnectCustomerContactLinkT(
			ConnectCustomerContactLinkT connectCustomerContactLinkT) {
		getConnectCustomerContactLinkTs().remove(connectCustomerContactLinkT);
		connectCustomerContactLinkT.setConnectT(null);

		return connectCustomerContactLinkT;
	}

	public List<ConnectOfferingLinkT> getConnectOfferingLinkTs() {
		return this.connectOfferingLinkTs;
	}

	public void setConnectOfferingLinkTs(
			List<ConnectOfferingLinkT> connectOfferingLinkTs) {
		this.connectOfferingLinkTs = connectOfferingLinkTs;
	}

	public ConnectOfferingLinkT addConnectOfferingLinkT(
			ConnectOfferingLinkT connectOfferingLinkT) {
		getConnectOfferingLinkTs().add(connectOfferingLinkT);
		connectOfferingLinkT.setConnectT(this);

		return connectOfferingLinkT;
	}

	public ConnectOfferingLinkT removeConnectOfferingLinkT(
			ConnectOfferingLinkT connectOfferingLinkT) {
		getConnectOfferingLinkTs().remove(connectOfferingLinkT);
		connectOfferingLinkT.setConnectT(null);

		return connectOfferingLinkT;
	}

	public List<ConnectOpportunityLinkIdT> getConnectOpportunityLinkIdTs() {
		return this.connectOpportunityLinkIdTs;
	}

	public void setConnectOpportunityLinkIdTs(
			List<ConnectOpportunityLinkIdT> connectOpportunityLinkIdTs) {
		this.connectOpportunityLinkIdTs = connectOpportunityLinkIdTs;
	}

	public ConnectOpportunityLinkIdT addConnectOpportunityLinkIdT(
			ConnectOpportunityLinkIdT connectOpportunityLinkIdT) {
		getConnectOpportunityLinkIdTs().add(connectOpportunityLinkIdT);
		connectOpportunityLinkIdT.setConnectT(this);

		return connectOpportunityLinkIdT;
	}

	public ConnectOpportunityLinkIdT removeConnectOpportunityLinkIdT(
			ConnectOpportunityLinkIdT connectOpportunityLinkIdT) {
		getConnectOpportunityLinkIdTs().remove(connectOpportunityLinkIdT);
		connectOpportunityLinkIdT.setConnectT(null);

		return connectOpportunityLinkIdT;
	}

	public List<ConnectSecondaryOwnerLinkT> getConnectSecondaryOwnerLinkTs() {
		return this.connectSecondaryOwnerLinkTs;
	}

	public void setConnectSecondaryOwnerLinkTs(
			List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerLinkTs) {
		this.connectSecondaryOwnerLinkTs = connectSecondaryOwnerLinkTs;
	}

	public ConnectSecondaryOwnerLinkT addConnectSecondaryOwnerLinkT(
			ConnectSecondaryOwnerLinkT connectSecondaryOwnerLinkT) {
		getConnectSecondaryOwnerLinkTs().add(connectSecondaryOwnerLinkT);
		connectSecondaryOwnerLinkT.setConnectT(this);

		return connectSecondaryOwnerLinkT;
	}

	public ConnectSecondaryOwnerLinkT removeConnectSecondaryOwnerLinkT(
			ConnectSecondaryOwnerLinkT connectSecondaryOwnerLinkT) {
		getConnectSecondaryOwnerLinkTs().remove(connectSecondaryOwnerLinkT);
		connectSecondaryOwnerLinkT.setConnectT(null);

		return connectSecondaryOwnerLinkT;
	}

	public List<ConnectSubSpLinkT> getConnectSubSpLinkTs() {
		return this.connectSubSpLinkTs;
	}

	public void setConnectSubSpLinkTs(List<ConnectSubSpLinkT> connectSubSpLinkTs) {
		this.connectSubSpLinkTs = connectSubSpLinkTs;
	}

	public ConnectSubSpLinkT addConnectSubSpLinkT(
			ConnectSubSpLinkT connectSubSpLinkT) {
		getConnectSubSpLinkTs().add(connectSubSpLinkT);
		connectSubSpLinkT.setConnectT(this);

		return connectSubSpLinkT;
	}

	public ConnectSubSpLinkT removeConnectSubSpLinkT(
			ConnectSubSpLinkT connectSubSpLinkT) {
		getConnectSubSpLinkTs().remove(connectSubSpLinkT);
		connectSubSpLinkT.setConnectT(null);

		return connectSubSpLinkT;
	}

	public CustomerMasterT getCustomerMasterT() {
		return this.customerMasterT;
	}

	public void setCustomerMasterT(CustomerMasterT customerMasterT) {
		this.customerMasterT = customerMasterT;
	}

	public GeographyCountryMappingT getGeographyCountryMappingT() {
		return this.geographyCountryMappingT;
	}

	public void setGeographyCountryMappingT(
			GeographyCountryMappingT geographyCountryMappingT) {
		this.geographyCountryMappingT = geographyCountryMappingT;
	}

	public PartnerMasterT getPartnerMasterT() {
		return this.partnerMasterT;
	}

	public void setPartnerMasterT(PartnerMasterT partnerMasterT) {
		this.partnerMasterT = partnerMasterT;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

	public List<ConnectTcsAccountContactLinkT> getConnectTcsAccountContactLinkTs() {
		return this.connectTcsAccountContactLinkTs;
	}

	public void setConnectTcsAccountContactLinkTs(
			List<ConnectTcsAccountContactLinkT> connectTcsAccountContactLinkTs) {
		this.connectTcsAccountContactLinkTs = connectTcsAccountContactLinkTs;
	}

	public ConnectTcsAccountContactLinkT addConnectTcsAccountContactLinkT(
			ConnectTcsAccountContactLinkT connectTcsAccountContactLinkT) {
		getConnectTcsAccountContactLinkTs().add(connectTcsAccountContactLinkT);
		connectTcsAccountContactLinkT.setConnectT(this);

		return connectTcsAccountContactLinkT;
	}

	public ConnectTcsAccountContactLinkT removeConnectTcsAccountContactLinkT(
			ConnectTcsAccountContactLinkT connectTcsAccountContactLinkT) {
		getConnectTcsAccountContactLinkTs().remove(
				connectTcsAccountContactLinkT);
		connectTcsAccountContactLinkT.setConnectT(null);

		return connectTcsAccountContactLinkT;
	}

	public List<DocumentRepositoryT> getDocumentRepositoryTs() {
		return this.documentRepositoryTs;
	}

	public void setDocumentRepositoryTs(
			List<DocumentRepositoryT> documentRepositoryTs) {
		this.documentRepositoryTs = documentRepositoryTs;
	}

	public DocumentRepositoryT addDocumentRepositoryT(
			DocumentRepositoryT documentRepositoryT) {
		getDocumentRepositoryTs().add(documentRepositoryT);
		documentRepositoryT.setConnectT(this);

		return documentRepositoryT;
	}

	public DocumentRepositoryT removeDocumentRepositoryT(
			DocumentRepositoryT documentRepositoryT) {
		getDocumentRepositoryTs().remove(documentRepositoryT);
		documentRepositoryT.setConnectT(null);

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
		notesT.setConnectT(this);

		return notesT;
	}

	public NotesT removeNotesT(NotesT notesT) {
		getNotesTs().remove(notesT);
		notesT.setConnectT(null);

		return notesT;
	}

	public List<TaskT> getTaskTs() {
		return this.taskTs;
	}

	public void setTaskTs(List<TaskT> taskTs) {
		this.taskTs = taskTs;
	}

	public TaskT addTaskT(TaskT taskT) {
		getTaskTs().add(taskT);
		taskT.setConnectT(this);

		return taskT;
	}

	public TaskT removeTaskT(TaskT taskT) {
		getTaskTs().remove(taskT);
		taskT.setConnectT(null);

		return taskT;
	}

	public List<UserFavoritesT> getUserFavoritesTs() {
		return this.userFavoritesTs;
	}

	public void setUserFavoritesTs(List<UserFavoritesT> userFavoritesTs) {
		this.userFavoritesTs = userFavoritesTs;
	}

	public UserFavoritesT addUserFavoritesT(UserFavoritesT userFavoritesT) {
		getUserFavoritesTs().add(userFavoritesT);
		userFavoritesT.setConnectT(this);

		return userFavoritesT;
	}

	public UserFavoritesT removeUserFavoritesT(UserFavoritesT userFavoritesT) {
		getUserFavoritesTs().remove(userFavoritesT);
		userFavoritesT.setConnectT(null);

		return userFavoritesT;
	}

	public String getPrimaryOwner() {
		return primaryOwner;
	}

	public void setPrimaryOwner(String primaryOwner) {
		this.primaryOwner = primaryOwner;
	}

	public List<UserNotificationsT> getUserNotificationsTs() {
		return this.userNotificationsTs;
	}

	public void setUserNotificationsTs(List<UserNotificationsT> userNotificationsTs) {
		this.userNotificationsTs = userNotificationsTs;
	}

	public UserNotificationsT addUserNotificationsT(UserNotificationsT userNotificationsT) {
		getUserNotificationsTs().add(userNotificationsT);
		userNotificationsT.setConnectT(this);

		return userNotificationsT;
	}

	public UserNotificationsT removeUserNotificationsT(UserNotificationsT userNotificationsT) {
		getUserNotificationsTs().remove(userNotificationsT);
		userNotificationsT.setConnectT(null);

		return userNotificationsT;
	}
	
	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public List<ConnectSubSpLinkT> getConnectSubLinkDeletionList() {
		return connectSubLinkDeletionList;
	}

	public void setConnectSubLinkDeletionList(
			List<ConnectSubSpLinkT> connectSubLinkDeletionList) {
		this.connectSubLinkDeletionList = connectSubLinkDeletionList;
	}

	public List<ConnectOfferingLinkT> getConnectOfferingLinkDeletionList() {
		return connectOfferingLinkDeletionList;
	}

	public void setConnectOfferingLinkDeletionList(
			List<ConnectOfferingLinkT> connectOfferingLinkDeletionList) {
		this.connectOfferingLinkDeletionList = connectOfferingLinkDeletionList;
	}

	public List<DocumentRepositoryT> getDocumentsDeletionList() {
		return documentsDeletionList;
	}

	public void setDocumentsDeletionList(
			List<DocumentRepositoryT> documentsDeletionList) {
		this.documentsDeletionList = documentsDeletionList;
	}
	
	public UserT getCreatedModifiedByUser() {
return this.createdModifiedByUser;
}

public void setCreatedModifiedByUser(UserT createdModifiedByUser) {
this.createdModifiedByUser = createdModifiedByUser;
}

}