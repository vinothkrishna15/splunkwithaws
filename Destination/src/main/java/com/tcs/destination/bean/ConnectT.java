package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the connect_t database table.
 * 
 */
@Entity
@Table(name="connect_t")
@NamedQuery(name="ConnectT.findAll", query="SELECT c FROM ConnectT c")
public class ConnectT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="connect_id")
	private String connectId;

	@Column(name="connect_category")
	private String connectCategory;

	@Column(name="connect_name")
	private String connectName;

	@Column(name="connect_notes")
	private String connectNotes;

	@Column(name="connect_opportunity_link_id")
	private String connectOpportunityLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="customer_contact_name")
	private String customerContactName;

	@Column(name="date_of_connect")
	private Timestamp dateOfConnect;

	@Column(name="documents_attached")
	private String documentsAttached;

	@Column(name="primary_owner")
	private String primaryOwner;

	@Column(name="secondary_owner")
	private String secondaryOwner;

	@Column(name="tcs_account_contact")
	private String tcsAccountContact;

	//bi-directional many-to-one association to ConnectOpportunityLinkIdT
	@OneToMany(mappedBy="connectT")
	private List<ConnectOpportunityLinkIdT> connectOpportunityLinkIdTs;

	//bi-directional many-to-one association to CustomerMasterT
	@ManyToOne
	@JoinColumn(name="customer_id")
	private CustomerMasterT customerMasterT;

	//bi-directional many-to-one association to GeographyCountryMappingT
	@ManyToOne
	@JoinColumn(name="country")
	private GeographyCountryMappingT geographyCountryMappingT;

	//bi-directional many-to-one association to OfferingMappingT
	@ManyToOne
	@JoinColumn(name="offering", referencedColumnName="offering_id")
	private OfferingMappingT offeringMappingT;

	//bi-directional many-to-one association to PartnerMasterT
	@ManyToOne
	@JoinColumn(name="partner_id")
	private PartnerMasterT partnerMasterT;

	//bi-directional many-to-one association to DocumentRepositoryT
	@OneToMany(mappedBy="connectT")
	private List<DocumentRepositoryT> documentRepositoryTs;

	//bi-directional many-to-one association to TaskT
	@OneToMany(mappedBy="connectT")
	private List<TaskT> taskTs;

	//bi-directional many-to-one association to CollabrationCommentT
	@OneToMany(mappedBy="connectT")
	private List<CollabrationCommentT> collabrationCommentTs;

	//bi-directional many-to-one association to SubSpMappingT
	@ManyToOne
	@JoinColumn(name="sub_sp", referencedColumnName="sub_sp_id")
	private SubSpMappingT subSpMappingT;

	//bi-directional many-to-one association to NotesT
	@OneToMany(mappedBy="connectT")
	private List<NotesT> notesTs;

	//bi-directional many-to-one association to OpportunityT
	@OneToMany(mappedBy="connectT")
	private List<OpportunityT> opportunityTs;

	//bi-directional many-to-one association to UserFavoritesT
	@OneToMany(mappedBy="connectT")
	private List<UserFavoritesT> userFavoritesTs;

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

	public String getConnectNotes() {
		return this.connectNotes;
	}

	public void setConnectNotes(String connectNotes) {
		this.connectNotes = connectNotes;
	}

	public String getConnectOpportunityLinkId() {
		return this.connectOpportunityLinkId;
	}

	public void setConnectOpportunityLinkId(String connectOpportunityLinkId) {
		this.connectOpportunityLinkId = connectOpportunityLinkId;
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

	public String getCustomerContactName() {
		return this.customerContactName;
	}

	public void setCustomerContactName(String customerContactName) {
		this.customerContactName = customerContactName;
	}

	public Timestamp getDateOfConnect() {
		return this.dateOfConnect;
	}

	public void setDateOfConnect(Timestamp dateOfConnect) {
		this.dateOfConnect = dateOfConnect;
	}

	public String getDocumentsAttached() {
		return this.documentsAttached;
	}

	public void setDocumentsAttached(String documentsAttached) {
		this.documentsAttached = documentsAttached;
	}

	public String getPrimaryOwner() {
		return this.primaryOwner;
	}

	public void setPrimaryOwner(String primaryOwner) {
		this.primaryOwner = primaryOwner;
	}

	public String getSecondaryOwner() {
		return this.secondaryOwner;
	}

	public void setSecondaryOwner(String secondaryOwner) {
		this.secondaryOwner = secondaryOwner;
	}

	public String getTcsAccountContact() {
		return this.tcsAccountContact;
	}

	public void setTcsAccountContact(String tcsAccountContact) {
		this.tcsAccountContact = tcsAccountContact;
	}

	public List<ConnectOpportunityLinkIdT> getConnectOpportunityLinkIdTs() {
		return this.connectOpportunityLinkIdTs;
	}

	public void setConnectOpportunityLinkIdTs(List<ConnectOpportunityLinkIdT> connectOpportunityLinkIdTs) {
		this.connectOpportunityLinkIdTs = connectOpportunityLinkIdTs;
	}

	public ConnectOpportunityLinkIdT addConnectOpportunityLinkIdT(ConnectOpportunityLinkIdT connectOpportunityLinkIdT) {
		getConnectOpportunityLinkIdTs().add(connectOpportunityLinkIdT);
		connectOpportunityLinkIdT.setConnectT(this);

		return connectOpportunityLinkIdT;
	}

	public ConnectOpportunityLinkIdT removeConnectOpportunityLinkIdT(ConnectOpportunityLinkIdT connectOpportunityLinkIdT) {
		getConnectOpportunityLinkIdTs().remove(connectOpportunityLinkIdT);
		connectOpportunityLinkIdT.setConnectT(null);

		return connectOpportunityLinkIdT;
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

	public void setGeographyCountryMappingT(GeographyCountryMappingT geographyCountryMappingT) {
		this.geographyCountryMappingT = geographyCountryMappingT;
	}

	public OfferingMappingT getOfferingMappingT() {
		return this.offeringMappingT;
	}

	public void setOfferingMappingT(OfferingMappingT offeringMappingT) {
		this.offeringMappingT = offeringMappingT;
	}

	public PartnerMasterT getPartnerMasterT() {
		return this.partnerMasterT;
	}

	public void setPartnerMasterT(PartnerMasterT partnerMasterT) {
		this.partnerMasterT = partnerMasterT;
	}

	public List<DocumentRepositoryT> getDocumentRepositoryTs() {
		return this.documentRepositoryTs;
	}

	public void setDocumentRepositoryTs(List<DocumentRepositoryT> documentRepositoryTs) {
		this.documentRepositoryTs = documentRepositoryTs;
	}

	public DocumentRepositoryT addDocumentRepositoryT(DocumentRepositoryT documentRepositoryT) {
		getDocumentRepositoryTs().add(documentRepositoryT);
		documentRepositoryT.setConnectT(this);

		return documentRepositoryT;
	}

	public DocumentRepositoryT removeDocumentRepositoryT(DocumentRepositoryT documentRepositoryT) {
		getDocumentRepositoryTs().remove(documentRepositoryT);
		documentRepositoryT.setConnectT(null);

		return documentRepositoryT;
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

	public List<CollabrationCommentT> getCollabrationCommentTs() {
		return this.collabrationCommentTs;
	}

	public void setCollabrationCommentTs(List<CollabrationCommentT> collabrationCommentTs) {
		this.collabrationCommentTs = collabrationCommentTs;
	}

	public CollabrationCommentT addCollabrationCommentT(CollabrationCommentT collabrationCommentT) {
		getCollabrationCommentTs().add(collabrationCommentT);
		collabrationCommentT.setConnectT(this);

		return collabrationCommentT;
	}

	public CollabrationCommentT removeCollabrationCommentT(CollabrationCommentT collabrationCommentT) {
		getCollabrationCommentTs().remove(collabrationCommentT);
		collabrationCommentT.setConnectT(null);

		return collabrationCommentT;
	}

	public SubSpMappingT getSubSpMappingT() {
		return this.subSpMappingT;
	}

	public void setSubSpMappingT(SubSpMappingT subSpMappingT) {
		this.subSpMappingT = subSpMappingT;
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

	public List<OpportunityT> getOpportunityTs() {
		return this.opportunityTs;
	}

	public void setOpportunityTs(List<OpportunityT> opportunityTs) {
		this.opportunityTs = opportunityTs;
	}

	public OpportunityT addOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().add(opportunityT);
		opportunityT.setConnectT(this);

		return opportunityT;
	}

	public OpportunityT removeOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().remove(opportunityT);
		opportunityT.setConnectT(null);

		return opportunityT;
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

}