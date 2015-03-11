package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the opportunity_t database table.
 * 
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="opportunityId")
@Entity
@Table(name="opportunity_t")
@NamedQuery(name="OpportunityT.findAll", query="SELECT o FROM OpportunityT o")
public class OpportunityT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="opportunity_id")
	private String opportunityId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="crm_id")
	private String crmId;

	@Temporal(TemporalType.DATE)
	@Column(name="deal_closure_date")
	private Date dealClosureDate;

	@Column(name="deal_currency")
	private String dealCurrency;

	@Column(name="digital_deal_value")
	private Integer digitalDealValue;

	@Column(name="documents_attached")
	private String documentsAttached;

	@Temporal(TemporalType.DATE)
	@Column(name="engagement_duration")
	private Date engagementDuration;

	@Temporal(TemporalType.DATE)
	@Column(name="engagement_start_date")
	private Date engagementStartDate;

	@Column(name="factors_for_win_loss")
	private String factorsForWinLoss;

	@Column(name="new_logo")
	private String newLogo;

	@Column(name="opportunity_description")
	private String opportunityDescription;

	@Column(name="opportunity_name")
	private String opportunityName;

	@Column(name="opportunity_owner")
	private String opportunityOwner;

	@Column(name="opportunity_request_receive_date")
	private Timestamp opportunityRequestReceiveDate;

	@Column(name="overall_deal_size")
	private Integer overallDealSize;

	@Column(name="strategic_initiative")
	private String strategicInitiative;

	//bi-directional many-to-one association to BidDetailsT
	@OneToMany(mappedBy="opportunityT")
	private List<BidDetailsT> bidDetailsTs;

	//bi-directional many-to-one association to CollabrationCommentT
	@OneToMany(mappedBy="opportunityT")
	private List<CollabrationCommentT> collabrationCommentTs;

	//bi-directional many-to-one association to ConnectOpportunityLinkIdT
	@OneToMany(mappedBy="opportunityT")
	private List<ConnectOpportunityLinkIdT> connectOpportunityLinkIdTs;

	//bi-directional many-to-one association to DocumentRepositoryT
	@OneToMany(mappedBy="opportunityT")
	private List<DocumentRepositoryT> documentRepositoryTs;

	//bi-directional many-to-one association to NotesT
	@OneToMany(mappedBy="opportunityT")
	private List<NotesT> notesTs;

	//bi-directional many-to-one association to OpportunityCompetitorLinkT
	@OneToMany(mappedBy="opportunityT")
	private List<OpportunityCompetitorLinkT> opportunityCompetitorLinkTs;

	//bi-directional many-to-one association to OpportunityConnectLinkT
	@OneToMany(mappedBy="opportunityT")
	private List<OpportunityConnectLinkT> opportunityConnectLinkTs;

	//bi-directional many-to-one association to OpportunityCustomerContactLinkT
	@OneToMany(mappedBy="opportunityT")
	private List<OpportunityCustomerContactLinkT> opportunityCustomerContactLinkTs;

	//bi-directional many-to-one association to OpportunityPartnerLinkT
	 
	@OneToMany(mappedBy="opportunityT")
	private List<OpportunityPartnerLinkT> opportunityPartnerLinkTs;

	//bi-directional many-to-one association to OpportunitySalesSupportLinkT
	 
	@OneToMany(mappedBy="opportunityT")
	private List<OpportunitySalesSupportLinkT> opportunitySalesSupportLinkTs;

	//bi-directional many-to-one association to CustomerMasterT
	 
	@ManyToOne
	@JoinColumn(name="customer_id")
	private CustomerMasterT customerMasterT;

	//bi-directional many-to-one association to DealTypeMappingT
	 
	@ManyToOne
	@JoinColumn(name="deal_type")
	private DealTypeMappingT dealTypeMappingT;

	//bi-directional many-to-one association to GeographyCountryMappingT
	 
	@ManyToOne
	@JoinColumn(name="country")
	private GeographyCountryMappingT geographyCountryMappingT;

	//bi-directional many-to-one association to OfferingMappingT
	 
	@ManyToOne
	@JoinColumn(name="offering", referencedColumnName="offering")
	private OfferingMappingT offeringMappingT;

	//bi-directional many-to-one association to SalesStageMappingT
	 
	@ManyToOne
	@JoinColumn(name="sales_stage_code")
	private SalesStageMappingT salesStageMappingT;

	//bi-directional many-to-one association to SubSpMappingT
	 
	@ManyToOne
	@JoinColumn(name="sub_sp")
	private SubSpMappingT subSpMappingT;

	//bi-directional many-to-one association to OpportunityTcsAccountContactLinkT
	 
	@OneToMany(mappedBy="opportunityT")
	private List<OpportunityTcsAccountContactLinkT> opportunityTcsAccountContactLinkTs;

	//bi-directional many-to-one association to TaskT
	 
	@OneToMany(mappedBy="opportunityT")
	private List<TaskT> taskTs;

	//bi-directional many-to-one association to UserFavoritesT
	 
	@OneToMany(mappedBy="opportunityT")
	private List<UserFavoritesT> userFavoritesTs;

	public OpportunityT() {
	}

	public String getOpportunityId() {
		return this.opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
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

	public String getCrmId() {
		return this.crmId;
	}

	public void setCrmId(String crmId) {
		this.crmId = crmId;
	}

	public Date getDealClosureDate() {
		return this.dealClosureDate;
	}

	public void setDealClosureDate(Date dealClosureDate) {
		this.dealClosureDate = dealClosureDate;
	}

	public String getDealCurrency() {
		return this.dealCurrency;
	}

	public void setDealCurrency(String dealCurrency) {
		this.dealCurrency = dealCurrency;
	}

	public Integer getDigitalDealValue() {
		return this.digitalDealValue;
	}

	public void setDigitalDealValue(Integer digitalDealValue) {
		this.digitalDealValue = digitalDealValue;
	}

	public String getDocumentsAttached() {
		return this.documentsAttached;
	}

	public void setDocumentsAttached(String documentsAttached) {
		this.documentsAttached = documentsAttached;
	}

	public Date getEngagementDuration() {
		return this.engagementDuration;
	}

	public void setEngagementDuration(Date engagementDuration) {
		this.engagementDuration = engagementDuration;
	}

	public Date getEngagementStartDate() {
		return this.engagementStartDate;
	}

	public void setEngagementStartDate(Date engagementStartDate) {
		this.engagementStartDate = engagementStartDate;
	}

	public String getFactorsForWinLoss() {
		return this.factorsForWinLoss;
	}

	public void setFactorsForWinLoss(String factorsForWinLoss) {
		this.factorsForWinLoss = factorsForWinLoss;
	}

	public String getNewLogo() {
		return this.newLogo;
	}

	public void setNewLogo(String newLogo) {
		this.newLogo = newLogo;
	}

	public String getOpportunityDescription() {
		return this.opportunityDescription;
	}

	public void setOpportunityDescription(String opportunityDescription) {
		this.opportunityDescription = opportunityDescription;
	}

	public String getOpportunityName() {
		return this.opportunityName;
	}

	public void setOpportunityName(String opportunityName) {
		this.opportunityName = opportunityName;
	}

	public String getOpportunityOwner() {
		return this.opportunityOwner;
	}

	public void setOpportunityOwner(String opportunityOwner) {
		this.opportunityOwner = opportunityOwner;
	}

	public Timestamp getOpportunityRequestReceiveDate() {
		return this.opportunityRequestReceiveDate;
	}

	public void setOpportunityRequestReceiveDate(Timestamp opportunityRequestReceiveDate) {
		this.opportunityRequestReceiveDate = opportunityRequestReceiveDate;
	}

	public Integer getOverallDealSize() {
		return this.overallDealSize;
	}

	public void setOverallDealSize(Integer overallDealSize) {
		this.overallDealSize = overallDealSize;
	}

	public String getStrategicInitiative() {
		return this.strategicInitiative;
	}

	public void setStrategicInitiative(String strategicInitiative) {
		this.strategicInitiative = strategicInitiative;
	}

	public List<BidDetailsT> getBidDetailsTs() {
		return this.bidDetailsTs;
	}

	public void setBidDetailsTs(List<BidDetailsT> bidDetailsTs) {
		this.bidDetailsTs = bidDetailsTs;
	}

	public BidDetailsT addBidDetailsT(BidDetailsT bidDetailsT) {
		getBidDetailsTs().add(bidDetailsT);
		bidDetailsT.setOpportunityT(this);

		return bidDetailsT;
	}

	public BidDetailsT removeBidDetailsT(BidDetailsT bidDetailsT) {
		getBidDetailsTs().remove(bidDetailsT);
		bidDetailsT.setOpportunityT(null);

		return bidDetailsT;
	}

	public List<CollabrationCommentT> getCollabrationCommentTs() {
		return this.collabrationCommentTs;
	}

	public void setCollabrationCommentTs(List<CollabrationCommentT> collabrationCommentTs) {
		this.collabrationCommentTs = collabrationCommentTs;
	}

	public CollabrationCommentT addCollabrationCommentT(CollabrationCommentT collabrationCommentT) {
		getCollabrationCommentTs().add(collabrationCommentT);
		collabrationCommentT.setOpportunityT(this);

		return collabrationCommentT;
	}

	public CollabrationCommentT removeCollabrationCommentT(CollabrationCommentT collabrationCommentT) {
		getCollabrationCommentTs().remove(collabrationCommentT);
		collabrationCommentT.setOpportunityT(null);

		return collabrationCommentT;
	}

	public List<ConnectOpportunityLinkIdT> getConnectOpportunityLinkIdTs() {
		return this.connectOpportunityLinkIdTs;
	}

	public void setConnectOpportunityLinkIdTs(List<ConnectOpportunityLinkIdT> connectOpportunityLinkIdTs) {
		this.connectOpportunityLinkIdTs = connectOpportunityLinkIdTs;
	}

	public ConnectOpportunityLinkIdT addConnectOpportunityLinkIdT(ConnectOpportunityLinkIdT connectOpportunityLinkIdT) {
		getConnectOpportunityLinkIdTs().add(connectOpportunityLinkIdT);
		connectOpportunityLinkIdT.setOpportunityT(this);

		return connectOpportunityLinkIdT;
	}

	public ConnectOpportunityLinkIdT removeConnectOpportunityLinkIdT(ConnectOpportunityLinkIdT connectOpportunityLinkIdT) {
		getConnectOpportunityLinkIdTs().remove(connectOpportunityLinkIdT);
		connectOpportunityLinkIdT.setOpportunityT(null);

		return connectOpportunityLinkIdT;
	}

	public List<DocumentRepositoryT> getDocumentRepositoryTs() {
		return this.documentRepositoryTs;
	}

	public void setDocumentRepositoryTs(List<DocumentRepositoryT> documentRepositoryTs) {
		this.documentRepositoryTs = documentRepositoryTs;
	}

	public DocumentRepositoryT addDocumentRepositoryT(DocumentRepositoryT documentRepositoryT) {
		getDocumentRepositoryTs().add(documentRepositoryT);
		documentRepositoryT.setOpportunityT(this);

		return documentRepositoryT;
	}

	public DocumentRepositoryT removeDocumentRepositoryT(DocumentRepositoryT documentRepositoryT) {
		getDocumentRepositoryTs().remove(documentRepositoryT);
		documentRepositoryT.setOpportunityT(null);

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
		notesT.setOpportunityT(this);

		return notesT;
	}

	public NotesT removeNotesT(NotesT notesT) {
		getNotesTs().remove(notesT);
		notesT.setOpportunityT(null);

		return notesT;
	}

	public List<OpportunityCompetitorLinkT> getOpportunityCompetitorLinkTs() {
		return this.opportunityCompetitorLinkTs;
	}

	public void setOpportunityCompetitorLinkTs(List<OpportunityCompetitorLinkT> opportunityCompetitorLinkTs) {
		this.opportunityCompetitorLinkTs = opportunityCompetitorLinkTs;
	}

	public OpportunityCompetitorLinkT addOpportunityCompetitorLinkT(OpportunityCompetitorLinkT opportunityCompetitorLinkT) {
		getOpportunityCompetitorLinkTs().add(opportunityCompetitorLinkT);
		opportunityCompetitorLinkT.setOpportunityT(this);

		return opportunityCompetitorLinkT;
	}

	public OpportunityCompetitorLinkT removeOpportunityCompetitorLinkT(OpportunityCompetitorLinkT opportunityCompetitorLinkT) {
		getOpportunityCompetitorLinkTs().remove(opportunityCompetitorLinkT);
		opportunityCompetitorLinkT.setOpportunityT(null);

		return opportunityCompetitorLinkT;
	}

	public List<OpportunityConnectLinkT> getOpportunityConnectLinkTs() {
		return this.opportunityConnectLinkTs;
	}

	public void setOpportunityConnectLinkTs(List<OpportunityConnectLinkT> opportunityConnectLinkTs) {
		this.opportunityConnectLinkTs = opportunityConnectLinkTs;
	}

	public OpportunityConnectLinkT addOpportunityConnectLinkT(OpportunityConnectLinkT opportunityConnectLinkT) {
		getOpportunityConnectLinkTs().add(opportunityConnectLinkT);
		opportunityConnectLinkT.setOpportunityT(this);

		return opportunityConnectLinkT;
	}

	public OpportunityConnectLinkT removeOpportunityConnectLinkT(OpportunityConnectLinkT opportunityConnectLinkT) {
		getOpportunityConnectLinkTs().remove(opportunityConnectLinkT);
		opportunityConnectLinkT.setOpportunityT(null);

		return opportunityConnectLinkT;
	}

	public List<OpportunityCustomerContactLinkT> getOpportunityCustomerContactLinkTs() {
		return this.opportunityCustomerContactLinkTs;
	}

	public void setOpportunityCustomerContactLinkTs(List<OpportunityCustomerContactLinkT> opportunityCustomerContactLinkTs) {
		this.opportunityCustomerContactLinkTs = opportunityCustomerContactLinkTs;
	}

	public OpportunityCustomerContactLinkT addOpportunityCustomerContactLinkT(OpportunityCustomerContactLinkT opportunityCustomerContactLinkT) {
		getOpportunityCustomerContactLinkTs().add(opportunityCustomerContactLinkT);
		opportunityCustomerContactLinkT.setOpportunityT(this);

		return opportunityCustomerContactLinkT;
	}

	public OpportunityCustomerContactLinkT removeOpportunityCustomerContactLinkT(OpportunityCustomerContactLinkT opportunityCustomerContactLinkT) {
		getOpportunityCustomerContactLinkTs().remove(opportunityCustomerContactLinkT);
		opportunityCustomerContactLinkT.setOpportunityT(null);

		return opportunityCustomerContactLinkT;
	}

	public List<OpportunityPartnerLinkT> getOpportunityPartnerLinkTs() {
		return this.opportunityPartnerLinkTs;
	}

	public void setOpportunityPartnerLinkTs(List<OpportunityPartnerLinkT> opportunityPartnerLinkTs) {
		this.opportunityPartnerLinkTs = opportunityPartnerLinkTs;
	}

	public OpportunityPartnerLinkT addOpportunityPartnerLinkT(OpportunityPartnerLinkT opportunityPartnerLinkT) {
		getOpportunityPartnerLinkTs().add(opportunityPartnerLinkT);
		opportunityPartnerLinkT.setOpportunityT(this);

		return opportunityPartnerLinkT;
	}

	public OpportunityPartnerLinkT removeOpportunityPartnerLinkT(OpportunityPartnerLinkT opportunityPartnerLinkT) {
		getOpportunityPartnerLinkTs().remove(opportunityPartnerLinkT);
		opportunityPartnerLinkT.setOpportunityT(null);

		return opportunityPartnerLinkT;
	}

	public List<OpportunitySalesSupportLinkT> getOpportunitySalesSupportLinkTs() {
		return this.opportunitySalesSupportLinkTs;
	}

	public void setOpportunitySalesSupportLinkTs(List<OpportunitySalesSupportLinkT> opportunitySalesSupportLinkTs) {
		this.opportunitySalesSupportLinkTs = opportunitySalesSupportLinkTs;
	}

	public OpportunitySalesSupportLinkT addOpportunitySalesSupportLinkT(OpportunitySalesSupportLinkT opportunitySalesSupportLinkT) {
		getOpportunitySalesSupportLinkTs().add(opportunitySalesSupportLinkT);
		opportunitySalesSupportLinkT.setOpportunityT(this);

		return opportunitySalesSupportLinkT;
	}

	public OpportunitySalesSupportLinkT removeOpportunitySalesSupportLinkT(OpportunitySalesSupportLinkT opportunitySalesSupportLinkT) {
		getOpportunitySalesSupportLinkTs().remove(opportunitySalesSupportLinkT);
		opportunitySalesSupportLinkT.setOpportunityT(null);

		return opportunitySalesSupportLinkT;
	}

	public CustomerMasterT getCustomerMasterT() {
		return this.customerMasterT;
	}

	public void setCustomerMasterT(CustomerMasterT customerMasterT) {
		this.customerMasterT = customerMasterT;
	}

	public DealTypeMappingT getDealTypeMappingT() {
		return this.dealTypeMappingT;
	}

	public void setDealTypeMappingT(DealTypeMappingT dealTypeMappingT) {
		this.dealTypeMappingT = dealTypeMappingT;
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

	public SalesStageMappingT getSalesStageMappingT() {
		return this.salesStageMappingT;
	}

	public void setSalesStageMappingT(SalesStageMappingT salesStageMappingT) {
		this.salesStageMappingT = salesStageMappingT;
	}

	public SubSpMappingT getSubSpMappingT() {
		return this.subSpMappingT;
	}

	public void setSubSpMappingT(SubSpMappingT subSpMappingT) {
		this.subSpMappingT = subSpMappingT;
	}

	public List<OpportunityTcsAccountContactLinkT> getOpportunityTcsAccountContactLinkTs() {
		return this.opportunityTcsAccountContactLinkTs;
	}

	public void setOpportunityTcsAccountContactLinkTs(List<OpportunityTcsAccountContactLinkT> opportunityTcsAccountContactLinkTs) {
		this.opportunityTcsAccountContactLinkTs = opportunityTcsAccountContactLinkTs;
	}

	public OpportunityTcsAccountContactLinkT addOpportunityTcsAccountContactLinkT(OpportunityTcsAccountContactLinkT opportunityTcsAccountContactLinkT) {
		getOpportunityTcsAccountContactLinkTs().add(opportunityTcsAccountContactLinkT);
		opportunityTcsAccountContactLinkT.setOpportunityT(this);

		return opportunityTcsAccountContactLinkT;
	}

	public OpportunityTcsAccountContactLinkT removeOpportunityTcsAccountContactLinkT(OpportunityTcsAccountContactLinkT opportunityTcsAccountContactLinkT) {
		getOpportunityTcsAccountContactLinkTs().remove(opportunityTcsAccountContactLinkT);
		opportunityTcsAccountContactLinkT.setOpportunityT(null);

		return opportunityTcsAccountContactLinkT;
	}

	public List<TaskT> getTaskTs() {
		return this.taskTs;
	}

	public void setTaskTs(List<TaskT> taskTs) {
		this.taskTs = taskTs;
	}

	public TaskT addTaskT(TaskT taskT) {
		getTaskTs().add(taskT);
		taskT.setOpportunityT(this);

		return taskT;
	}

	public TaskT removeTaskT(TaskT taskT) {
		getTaskTs().remove(taskT);
		taskT.setOpportunityT(null);

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
		userFavoritesT.setOpportunityT(this);

		return userFavoritesT;
	}

	public UserFavoritesT removeUserFavoritesT(UserFavoritesT userFavoritesT) {
		getUserFavoritesTs().remove(userFavoritesT);
		userFavoritesT.setOpportunityT(null);

		return userFavoritesT;
	}

}