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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "connectId")
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

	@Column(name = "created_datetime", updatable = false)
	private Timestamp createdDatetime;

	@Column(name = "modified_datetime")
	private Timestamp modifiedDatetime;

	@Column(name = "created_by", updatable = false)
	private String createdBy;

	@Column(name = "modified_by")
	private String modifiedBy;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "created_by", updatable = false, insertable = false)
	private UserT createdByUser;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "modified_by", updatable = false, insertable = false)
	private UserT modifiedByUser;

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

	@Column(name = "product_id")
	private String productId;

	@Column(name = "country")
	private String country;

	@Column(name = "time_zone_desc")
	private String timeZone;

	private String location;

	@ManyToOne
	@JoinColumn(name="location", insertable=false, updatable=false)
	private CityMapping cityMapping;

	private String type;

	@ManyToOne
	@JoinColumn(name = "type", insertable = false, updatable = false)
	private ConnectTypeMappingT connectTypeMappingT;

	// bi-directional many-to-one association to TimeZoneMappingT
	@ManyToOne
	@JoinColumn(name = "time_zone_desc", insertable = false, updatable = false)
	private TimeZoneMappingT timeZoneMappingT;

	@Transient
	private List<SearchKeywordsT> searchKeywordsTs;

	// bi-directional many-to-one association to CollaborationCommentT
	@OneToMany(mappedBy = "connectT")
	@OrderBy("updated_datetime DESC")
	private List<CollaborationCommentT> collaborationCommentTs;

	// bi-directional many-to-one association to CommentsT
	@OneToMany(mappedBy = "connectT")
	private List<CommentsT> commentsTs;

	// bi-directional many-to-one association to ConnectCustomerContactLinkT
	@OneToMany(mappedBy = "connectT", cascade = CascadeType.ALL)
	private List<ConnectCustomerContactLinkT> connectCustomerContactLinkTs;

	// bi-directional many-to-one association to ConnectOfferingLinkT
	@OneToMany(mappedBy = "connectT", cascade = CascadeType.ALL)
	private List<ConnectOfferingLinkT> connectOfferingLinkTs;

	// bi-directional many-to-one association to ConnectOpportunityLinkIdT
	@OneToMany(mappedBy = "connectT", cascade = CascadeType.ALL)
	private List<ConnectOpportunityLinkIdT> connectOpportunityLinkIdTs;

	// bi-directional many-to-one association to ConnectSecondaryOwnerLinkT
	@OneToMany(mappedBy = "connectT", cascade = CascadeType.ALL)
	private List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerLinkTs;

	// bi-directional many-to-one association to ConnectSubSpLinkT
	@OneToMany(mappedBy = "connectT", cascade = CascadeType.ALL)
	private List<ConnectSubSpLinkT> connectSubSpLinkTs;

	// bi-directional many-to-one association to CustomerMasterT
	@ManyToOne
	@JoinColumn(name = "customer_id", insertable = false, updatable = false)
	private CustomerMasterT customerMasterT;

	// bi-directional many-to-one association to GeographyCountryMappingT
	@ManyToOne
	@JoinColumn(name = "country", insertable = false, updatable = false)
	private GeographyCountryMappingT geographyCountryMappingT;

	// bi-directional many-to-one association to PartnerMasterT
	@ManyToOne
	@JoinColumn(name = "partner_id", insertable = false, updatable = false)
	private PartnerMasterT partnerMasterT;

	// bi-directional many-to-one association to PartnerMasterT
	@ManyToOne
	@JoinColumn(name = "product_id", insertable = false, updatable = false)
	private ProductMasterT productMasterT;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "primary_owner", insertable = false, updatable = false)
	private UserT primaryOwnerUser;

	// bi-directional many-to-one association to ConnectTcsAccountContactLinkT
	@OneToMany(mappedBy = "connectT", cascade = CascadeType.ALL)
	private List<ConnectTcsAccountContactLinkT> connectTcsAccountContactLinkTs;

	// bi-directional many-to-one association to DocumentRepositoryT
	@OneToMany(mappedBy = "connectT")
	private List<DocumentRepositoryT> documentRepositoryTs;

	// bi-directional many-to-one association to NotesT
	@OneToMany(mappedBy = "connectT", cascade = CascadeType.ALL)
	private List<NotesT> notesTs;

	// bi-directional many-to-one association to TaskT
	@OneToMany(mappedBy = "connectT", cascade = CascadeType.ALL)
	private List<TaskT> taskTs;

	// bi-directional many-to-one association to UserFavoritesT
	@OneToMany(mappedBy = "connectT")
	private List<UserFavoritesT> userFavoritesTs;

	// bi-directional many-to-one association to UserNotificationsT
	@OneToMany(mappedBy = "connectT")
	private List<UserNotificationsT> userNotificationsTs;

	// bi-directional many-to-one association to UserTaggedFollowedT
	@OneToMany(mappedBy = "connectT")
	private List<UserTaggedFollowedT> userTaggedFollowedTs;
	
	@Column(name="cxo_flag")
	private boolean cxoFlag;

	@Transient
	private List<ConnectSubSpLinkT> connectSubLinkDeletionList;

	@Transient
	private List<ConnectOfferingLinkT> connectOfferingLinkDeletionList;

	@Transient
	private List<DocumentRepositoryT> documentsDeletionList;

	@Transient
	private List<ConnectCustomerContactLinkT> deleteConnectCustomerContactLinkTs;

	@Transient
	private List<ConnectTcsAccountContactLinkT> deleteConnectTcsAccountContactLinkTs;

	@Transient
	private List<ConnectSecondaryOwnerLinkT> deleteConnectSecondaryOwnerLinkTs;

	@Transient
	private List<ConnectOpportunityLinkIdT> deleteConnectOpportunityLinkIdTs;

	@Transient
	private List<SearchKeywordsT> deleteSearchKeywordsTs;

	@Transient	
	private boolean enableEditAccess;	

	//temp member for sorting
	@Transient	
	private String custName;

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

	public List<CommentsT> getCommentsTs() {
		return this.commentsTs;
	}

	public void setCommentsTs(List<CommentsT> commentsTs) {
		this.commentsTs = commentsTs;
	}

	public CommentsT addCommentsT(CommentsT commentsT) {
		getCommentsTs().add(commentsT);
		commentsT.setConnectT(this);

		return commentsT;
	}

	public CommentsT removeCommentsT(CommentsT commentsT) {
		getCommentsTs().remove(commentsT);
		commentsT.setConnectT(null);

		return commentsT;
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

	public UserT getPrimaryOwnerUser() {
		return this.primaryOwnerUser;
	}

	public void setPrimaryOwnerUser(UserT primaryOwnerUser) {
		this.primaryOwnerUser = primaryOwnerUser;
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

	public void setUserNotificationsTs(
			List<UserNotificationsT> userNotificationsTs) {
		this.userNotificationsTs = userNotificationsTs;
	}

	public UserNotificationsT addUserNotificationsT(
			UserNotificationsT userNotificationsT) {
		getUserNotificationsTs().add(userNotificationsT);
		userNotificationsT.setConnectT(this);

		return userNotificationsT;
	}

	public UserNotificationsT removeUserNotificationsT(
			UserNotificationsT userNotificationsT) {
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

	public List<SearchKeywordsT> getSearchKeywordsTs() {
		return searchKeywordsTs;
	}

	public void setSearchKeywordsTs(List<SearchKeywordsT> searchKeywordsTs) {
		this.searchKeywordsTs = searchKeywordsTs;
	}

	public List<UserTaggedFollowedT> getUserTaggedFollowedTs() {
		return this.userTaggedFollowedTs;
	}

	public void setUserTaggedFollowedTs(
			List<UserTaggedFollowedT> userTaggedFollowedTs) {
		this.userTaggedFollowedTs = userTaggedFollowedTs;
	}

	public UserTaggedFollowedT addUserTaggedFollowedT(
			UserTaggedFollowedT userTaggedFollowedT) {
		getUserTaggedFollowedTs().add(userTaggedFollowedT);
		userTaggedFollowedT.setConnectT(this);

		return userTaggedFollowedT;
	}

	public UserTaggedFollowedT removeUserTaggedFollowedT(
			UserTaggedFollowedT userTaggedFollowedT) {
		getUserTaggedFollowedTs().remove(userTaggedFollowedT);
		userTaggedFollowedT.setConnectT(null);

		return userTaggedFollowedT;
	}

	public List<ConnectCustomerContactLinkT> getDeleteConnectCustomerContactLinkTs() {
		return deleteConnectCustomerContactLinkTs;
	}

	public void setDeleteConnectCustomerContactLinkTs(
			List<ConnectCustomerContactLinkT> deleteConnectCustomerContactLinkTs) {
		this.deleteConnectCustomerContactLinkTs = deleteConnectCustomerContactLinkTs;
	}

	public List<ConnectTcsAccountContactLinkT> getDeleteConnectTcsAccountContactLinkTs() {
		return deleteConnectTcsAccountContactLinkTs;
	}

	public void setDeleteConnectTcsAccountContactLinkTs(
			List<ConnectTcsAccountContactLinkT> deleteConnectTcsAccountContactLinkTs) {
		this.deleteConnectTcsAccountContactLinkTs = deleteConnectTcsAccountContactLinkTs;
	}

	public List<ConnectSecondaryOwnerLinkT> getDeleteConnectSecondaryOwnerLinkTs() {
		return deleteConnectSecondaryOwnerLinkTs;
	}

	public void setDeleteConnectSecondaryOwnerLinkTs(
			List<ConnectSecondaryOwnerLinkT> deleteConnectSecondaryOwnerLinkTs) {
		this.deleteConnectSecondaryOwnerLinkTs = deleteConnectSecondaryOwnerLinkTs;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public TimeZoneMappingT getTimeZoneMappingT() {
		return timeZoneMappingT;
	}

	public void setTimeZoneMappingT(TimeZoneMappingT timeZoneMappingT) {
		this.timeZoneMappingT = timeZoneMappingT;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public CityMapping getCityMapping() {
		return cityMapping;
	}

	public void setCityMapping(CityMapping cityMapping) {
		this.cityMapping = cityMapping;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ConnectTypeMappingT getConnectTypeMappingT() {
		return connectTypeMappingT;
	}

	public void setConnectTypeMappingT(ConnectTypeMappingT connectTypeMappingT) {
		this.connectTypeMappingT = connectTypeMappingT;
	}

	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public Timestamp getModifiedDatetime() {
		return modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
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

	public List<ConnectOpportunityLinkIdT> getDeleteConnectOpportunityLinkIdTs() {
		return deleteConnectOpportunityLinkIdTs;
	}

	public void setDeleteConnectOpportunityLinkIdTs(
			List<ConnectOpportunityLinkIdT> deleteConnectOpportunityLinkIdTs) {
		this.deleteConnectOpportunityLinkIdTs = deleteConnectOpportunityLinkIdTs;
	}

	public List<SearchKeywordsT> getDeleteSearchKeywordsTs() {
		return deleteSearchKeywordsTs;
	}

	public void setDeleteSearchKeywordsTs(
			List<SearchKeywordsT> deleteSearchKeywordsTs) {
		this.deleteSearchKeywordsTs = deleteSearchKeywordsTs;
	}

	public boolean isEnableEditAccess() {
		return enableEditAccess;
	}

	public void setEnableEditAccess(boolean enableEditAccess) {
		this.enableEditAccess = enableEditAccess;
	}

	public String getProductId() {
		return productId;
	}

	public ProductMasterT getProductMasterT() {
		return productMasterT;
	}

	public void setProductMasterT(ProductMasterT productMasterT) {
		this.productMasterT = productMasterT;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public boolean isCxoFlag() {
		return cxoFlag;
	}

	public void setCxoFlag(boolean cxoFlag) {
		this.cxoFlag = cxoFlag;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}
}