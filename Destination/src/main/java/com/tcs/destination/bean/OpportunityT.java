package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the opportunity_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "opportunityId")
@Entity
@Table(name = "opportunity_t")
@NamedQuery(name = "OpportunityT.findAll", query = "SELECT o FROM OpportunityT o")
public class OpportunityT implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "opportunity_id")
	private String opportunityId;

	@Column(name = "created_by", updatable = false)
	private String createdBy;

	@Column(name = "created_datetime", updatable = false)
	private Timestamp createdDatetime;

	@ManyToOne
	@JoinColumn(name = "created_by", insertable = false, updatable = false)
	private UserT createdByUser;

	@Column(name = "modified_by")
	private String modifiedBy;

	@Column(name = "modified_datetime")
	private Timestamp modifiedDatetime;

	@ManyToOne
	@JoinColumn(name = "modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;

	@Column(name = "crm_id")
	private String crmId;

	@Column(name = "customer_id")
	private String customerId;

	@Temporal(TemporalType.DATE)
	@Column(name = "deal_closure_date")
	private Date dealClosureDate;

	@Column(name = "description_for_win_loss")
	private String descriptionForWinLoss;

	@Column(name = "digital_deal_value")
	private Integer digitalDealValue;

	@Column(name = "documents_attached")
	private String documentsAttached;

	@Column(name = "engagement_duration")
	private String engagementDuration;

	@Temporal(TemporalType.DATE)
	@Column(name = "engagement_start_date")
	private Date engagementStartDate;

	@Column(name = "new_logo")
	private String newLogo;

	@Column(name = "opportunity_description")
	private String opportunityDescription;

	@Column(name = "opportunity_name")
	private String opportunityName;

	@Column(name = "deal_type")
	private String dealType;

	@Column(name = "country")
	private String country;

	@Column(name = "digital_flag")
	private String digitalFlag;

	@Temporal(TemporalType.DATE)
	@Column(name = "opportunity_request_receive_date")
	private Date opportunityRequestReceiveDate;

	@Column(name = "overall_deal_size")
	private Integer overallDealSize;

	@Column(name = "strategic_initiative")
	private String strategicInitiative;

	// bi-directional many-to-one association to OpportunityWinLossFactorsT
	@OneToMany(mappedBy = "opportunityT", cascade = CascadeType.ALL)
	private List<OpportunityWinLossFactorsT> opportunityWinLossFactorsTs;

	@Column(name = "opportunity_owner")
	private String opportunityOwner;

	@Column(name = "deal_currency")
	private String dealCurrency;

	@Column(name = "sales_stage_code")
	private int salesStageCode;

	// bi-directional many-to-one association to BidDetailsT
	@OneToMany(mappedBy = "opportunityT")
	@OrderBy("modified_datetime DESC")
	private List<BidDetailsT> bidDetailsTs;

	// bi-directional many-to-one association to CollaborationCommentT
	@OneToMany(mappedBy = "opportunityT", cascade = CascadeType.ALL)
	@OrderBy("updated_datetime DESC")
	private List<CollaborationCommentT> collaborationCommentTs;

	// bi-directional many-to-one association to CommentsT
	@OneToMany(mappedBy = "opportunityT")
	private List<CommentsT> commentsTs;

	// bi-directional many-to-one association to ConnectOpportunityLinkIdT
	@OneToMany(mappedBy = "opportunityT", cascade = CascadeType.ALL)
	private List<ConnectOpportunityLinkIdT> connectOpportunityLinkIdTs;

	// bi-directional many-to-one association to DocumentRepositoryT
	@OneToMany(mappedBy = "opportunityT", cascade = CascadeType.ALL)
	private List<DocumentRepositoryT> documentRepositoryTs;

	// bi-directional many-to-one association to NotesT
	@OneToMany(mappedBy = "opportunityT", cascade = CascadeType.ALL)
	private List<NotesT> notesTs;

	// bi-directional many-to-one association to OpportunityCompetitorLinkT
	@OneToMany(mappedBy = "opportunityT", cascade = CascadeType.ALL)
	private List<OpportunityCompetitorLinkT> opportunityCompetitorLinkTs;

	// bi-directional many-to-one association to OpportunityCustomerContactLinkT
	@OneToMany(mappedBy = "opportunityT", cascade = CascadeType.ALL)
	private List<OpportunityCustomerContactLinkT> opportunityCustomerContactLinkTs;

	// bi-directional many-to-one association to OpportunityOfferingLinkT
	@OneToMany(mappedBy = "opportunityT", cascade = CascadeType.ALL)
	private List<OpportunityOfferingLinkT> opportunityOfferingLinkTs;

	// bi-directional many-to-one association to OpportunityPartnerLinkT
	@OneToMany(mappedBy = "opportunityT", cascade = CascadeType.ALL)
	private List<OpportunityPartnerLinkT> opportunityPartnerLinkTs;

	// bi-directional many-to-one association to OpportunitySalesSupportLinkT
	@OneToMany(mappedBy = "opportunityT", cascade = CascadeType.ALL)
	private List<OpportunitySalesSupportLinkT> opportunitySalesSupportLinkTs;

	// bi-directional many-to-one association to OpportunitySubSpLinkT
	@OneToMany(mappedBy = "opportunityT", cascade = CascadeType.ALL)
	private List<OpportunitySubSpLinkT> opportunitySubSpLinkTs;

	// bi-directional many-to-one association to BeaconConvertorMappingT
	@ManyToOne
	@JoinColumn(name = "deal_currency", insertable = false, updatable = false)
	private BeaconConvertorMappingT beaconConvertorMappingT;

	// bi-directional many-to-one association to CustomerMasterT
	@ManyToOne
	@JoinColumn(name = "customer_id", insertable = false, updatable = false)
	private CustomerMasterT customerMasterT;

	// bi-directional many-to-one association to DealTypeMappingT
	@ManyToOne
	@JoinColumn(name = "deal_type", insertable = false, updatable = false)
	private DealTypeMappingT dealTypeMappingT;

	// bi-directional many-to-one association to GeographyCountryMappingT
	@ManyToOne
	@JoinColumn(name = "country", insertable = false, updatable = false)
	private GeographyCountryMappingT geographyCountryMappingT;

	// bi-directional many-to-one association to SalesStageMappingT
	@ManyToOne
	@JoinColumn(name = "sales_stage_code", insertable = false, updatable = false)
	private SalesStageMappingT salesStageMappingT;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "opportunity_owner", insertable = false, updatable = false)
	private UserT primaryOwnerUser;

	// bi-directional many-to-one association to
	// OpportunityTcsAccountContactLinkT
	@OneToMany(mappedBy = "opportunityT", cascade = CascadeType.ALL)
	private List<OpportunityTcsAccountContactLinkT> opportunityTcsAccountContactLinkTs;

	// bi-directional many-to-one association to OpportunityTimelineHistoryT
	@OneToMany(mappedBy = "opportunityT", cascade = CascadeType.ALL)
	@OrderBy("updated_datetime DESC")
	private List<OpportunityTimelineHistoryT> opportunityTimelineHistoryTs;

	// bi-directional many-to-one association to TaskT
	@OneToMany(mappedBy = "opportunityT", cascade = CascadeType.ALL)
	private List<TaskT> taskTs;

	// bi-directional many-to-one association to UserFavoritesT
	@OneToMany(mappedBy = "opportunityT", cascade = CascadeType.ALL)
	private List<UserFavoritesT> userFavoritesTs;

	// bi-directional many-to-one association to UserNotificationsT
	@OneToMany(mappedBy = "opportunityT", cascade = CascadeType.ALL)
	private List<UserNotificationsT> userNotificationsTs;

	// bi-directional many-to-one association to UserTaggedFollowedT
	@OneToMany(mappedBy = "opportunityT", cascade = CascadeType.ALL)
	private List<UserTaggedFollowedT> userTaggedFollowedTs;

	@Transient
	private List<SearchKeywordsT> searchKeywordsTs;

	@Transient
	private List<ConnectOpportunityLinkIdT> deleteConnectOpportunityLinkIdTs;

	@Transient
	private List<NotesT> deleteNotesTs;
	
	@Transient
	private List<SearchKeywordsT> deleteSearchKeywordsTs;

	@Transient
	private List<OpportunityCompetitorLinkT> deleteOpportunityCompetitorLinkTs;

	@Transient
	private List<OpportunityCustomerContactLinkT> deleteOpportunityCustomerContactLinkTs;

	@Transient
	private List<OpportunityPartnerLinkT> deleteOpportunityPartnerLinkTs;

	@Transient
	private List<OpportunitySalesSupportLinkT> deleteOpportunitySalesSupportLinkTs;

	@Transient
	private List<OpportunitySubSpLinkT> deleteOpportunitySubSpLinkTs;

	@Transient
	private List<OpportunityTcsAccountContactLinkT> deleteOpportunityOpportunityTcsAccountContactLinkTs;

	@Transient
	private List<OpportunityOfferingLinkT> deleteOpportunityOfferingLinkTs;

	@Transient
	private List<OpportunityTcsAccountContactLinkT> deleteOpportunityTcsAccountContactLinkTs;

	@Transient
	private List<OpportunityWinLossFactorsT> deleteOpportunityWinLossFactorsTs;

	@Transient
	private List<OpportunityDealValue> opportunityDealValues;

	public OpportunityT() {
	}

	public String getOpportunityId() {
		return this.opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public String getCreatedBy() {

		return this.createdBy;

	}

	public void setCreatedBy(String createdBy) {

		this.createdBy = createdBy;

	}

	public Timestamp getCreatedDatetime() {

		return this.createdDatetime;

	}

	public void setCreatedDatetime(Timestamp createdDatetime) {

		this.createdDatetime = createdDatetime;

	}

	public UserT getCreatedByUser() {

		return this.createdByUser;

	}

	public void setCreatedByUser(UserT createdByUser) {

		this.createdByUser = createdByUser;

	}

	public String getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;

	}

	public UserT getModifiedByUser() {
		return this.modifiedByUser;
	}

	public void setModifiedByUser(UserT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;

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

	public Integer getDigitalDealValue() {
		return this.digitalDealValue;
	}

	public void setDigitalDealValue(Integer digitalDealValue) {
		this.digitalDealValue = digitalDealValue;
	}

	public String getDescriptionForWinLoss() {
		return this.descriptionForWinLoss;
	}

	public void setDescriptionForWinLoss(String descriptionForWinLoss) {
		this.descriptionForWinLoss = descriptionForWinLoss;
	}

	public String getDocumentsAttached() {
		return this.documentsAttached;
	}

	public void setDocumentsAttached(String documentsAttached) {
		this.documentsAttached = documentsAttached;
	}

	public String getEngagementDuration() {
		return this.engagementDuration;
	}

	public void setEngagementDuration(String engagementDuration) {
		this.engagementDuration = engagementDuration;
	}

	public Date getEngagementStartDate() {
		return this.engagementStartDate;
	}

	public void setEngagementStartDate(Date engagementStartDate) {
		this.engagementStartDate = engagementStartDate;
	}

	public String getNewLogo() {
		return this.newLogo;
	}

	public void setNewLogo(String newLogo) {
		this.newLogo = newLogo;
	}

	public String getCustomerId() {
		return customerId;
	}

	public String getOpportunityDescription() {
		return this.opportunityDescription;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
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

	public Date getOpportunityRequestReceiveDate() {
		return this.opportunityRequestReceiveDate;
	}

	public void setOpportunityRequestReceiveDate(
			Date opportunityRequestReceiveDate) {
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
		collaborationCommentT.setOpportunityT(this);

		return collaborationCommentT;
	}

	public CollaborationCommentT removeCollaborationCommentT(
			CollaborationCommentT collaborationCommentT) {
		getCollaborationCommentTs().remove(collaborationCommentT);
		collaborationCommentT.setOpportunityT(null);

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
		commentsT.setOpportunityT(this);

		return commentsT;
	}

	public CommentsT removeCommentsT(CommentsT commentsT) {
		getCommentsTs().remove(commentsT);
		commentsT.setOpportunityT(null);

		return commentsT;
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
		connectOpportunityLinkIdT.setOpportunityT(this);

		return connectOpportunityLinkIdT;
	}

	public ConnectOpportunityLinkIdT removeConnectOpportunityLinkIdT(
			ConnectOpportunityLinkIdT connectOpportunityLinkIdT) {
		getConnectOpportunityLinkIdTs().remove(connectOpportunityLinkIdT);
		connectOpportunityLinkIdT.setOpportunityT(null);

		return connectOpportunityLinkIdT;
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
		documentRepositoryT.setOpportunityT(this);

		return documentRepositoryT;
	}

	public DocumentRepositoryT removeDocumentRepositoryT(
			DocumentRepositoryT documentRepositoryT) {
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

	public void setOpportunityCompetitorLinkTs(
			List<OpportunityCompetitorLinkT> opportunityCompetitorLinkTs) {
		this.opportunityCompetitorLinkTs = opportunityCompetitorLinkTs;
	}

	public OpportunityCompetitorLinkT addOpportunityCompetitorLinkT(
			OpportunityCompetitorLinkT opportunityCompetitorLinkT) {
		getOpportunityCompetitorLinkTs().add(opportunityCompetitorLinkT);
		opportunityCompetitorLinkT.setOpportunityT(this);

		return opportunityCompetitorLinkT;
	}

	public OpportunityCompetitorLinkT removeOpportunityCompetitorLinkT(
			OpportunityCompetitorLinkT opportunityCompetitorLinkT) {
		getOpportunityCompetitorLinkTs().remove(opportunityCompetitorLinkT);
		opportunityCompetitorLinkT.setOpportunityT(null);

		return opportunityCompetitorLinkT;
	}

	public List<OpportunityCustomerContactLinkT> getOpportunityCustomerContactLinkTs() {
		return this.opportunityCustomerContactLinkTs;
	}

	public void setOpportunityCustomerContactLinkTs(
			List<OpportunityCustomerContactLinkT> opportunityCustomerContactLinkTs) {
		this.opportunityCustomerContactLinkTs = opportunityCustomerContactLinkTs;
	}

	public OpportunityCustomerContactLinkT addOpportunityCustomerContactLinkT(
			OpportunityCustomerContactLinkT opportunityCustomerContactLinkT) {
		getOpportunityCustomerContactLinkTs().add(
				opportunityCustomerContactLinkT);
		opportunityCustomerContactLinkT.setOpportunityT(this);

		return opportunityCustomerContactLinkT;
	}

	public OpportunityCustomerContactLinkT removeOpportunityCustomerContactLinkT(
			OpportunityCustomerContactLinkT opportunityCustomerContactLinkT) {
		getOpportunityCustomerContactLinkTs().remove(
				opportunityCustomerContactLinkT);
		opportunityCustomerContactLinkT.setOpportunityT(null);

		return opportunityCustomerContactLinkT;
	}

	public List<OpportunityOfferingLinkT> getOpportunityOfferingLinkTs() {
		return this.opportunityOfferingLinkTs;
	}

	public void setOpportunityOfferingLinkTs(
			List<OpportunityOfferingLinkT> opportunityOfferingLinkTs) {
		this.opportunityOfferingLinkTs = opportunityOfferingLinkTs;
	}

	public OpportunityOfferingLinkT addOpportunityOfferingLinkT(
			OpportunityOfferingLinkT opportunityOfferingLinkT) {
		getOpportunityOfferingLinkTs().add(opportunityOfferingLinkT);
		opportunityOfferingLinkT.setOpportunityT(this);

		return opportunityOfferingLinkT;
	}

	public OpportunityOfferingLinkT removeOpportunityOfferingLinkT(
			OpportunityOfferingLinkT opportunityOfferingLinkT) {
		getOpportunityOfferingLinkTs().remove(opportunityOfferingLinkT);
		opportunityOfferingLinkT.setOpportunityT(null);

		return opportunityOfferingLinkT;
	}

	public List<OpportunityPartnerLinkT> getOpportunityPartnerLinkTs() {
		return this.opportunityPartnerLinkTs;
	}

	public void setOpportunityPartnerLinkTs(
			List<OpportunityPartnerLinkT> opportunityPartnerLinkTs) {
		this.opportunityPartnerLinkTs = opportunityPartnerLinkTs;
	}

	public OpportunityPartnerLinkT addOpportunityPartnerLinkT(
			OpportunityPartnerLinkT opportunityPartnerLinkT) {
		getOpportunityPartnerLinkTs().add(opportunityPartnerLinkT);
		opportunityPartnerLinkT.setOpportunityT(this);

		return opportunityPartnerLinkT;
	}

	public OpportunityPartnerLinkT removeOpportunityPartnerLinkT(
			OpportunityPartnerLinkT opportunityPartnerLinkT) {
		getOpportunityPartnerLinkTs().remove(opportunityPartnerLinkT);
		opportunityPartnerLinkT.setOpportunityT(null);

		return opportunityPartnerLinkT;
	}

	public List<OpportunitySalesSupportLinkT> getOpportunitySalesSupportLinkTs() {
		return this.opportunitySalesSupportLinkTs;
	}

	public void setOpportunitySalesSupportLinkTs(
			List<OpportunitySalesSupportLinkT> opportunitySalesSupportLinkTs) {
		this.opportunitySalesSupportLinkTs = opportunitySalesSupportLinkTs;
	}

	public OpportunitySalesSupportLinkT addOpportunitySalesSupportLinkT(
			OpportunitySalesSupportLinkT opportunitySalesSupportLinkT) {
		getOpportunitySalesSupportLinkTs().add(opportunitySalesSupportLinkT);
		opportunitySalesSupportLinkT.setOpportunityT(this);

		return opportunitySalesSupportLinkT;
	}

	public OpportunitySalesSupportLinkT removeOpportunitySalesSupportLinkT(
			OpportunitySalesSupportLinkT opportunitySalesSupportLinkT) {
		getOpportunitySalesSupportLinkTs().remove(opportunitySalesSupportLinkT);
		opportunitySalesSupportLinkT.setOpportunityT(null);

		return opportunitySalesSupportLinkT;
	}

	public List<OpportunitySubSpLinkT> getOpportunitySubSpLinkTs() {
		return this.opportunitySubSpLinkTs;
	}

	public void setOpportunitySubSpLinkTs(
			List<OpportunitySubSpLinkT> opportunitySubSpLinkTs) {
		this.opportunitySubSpLinkTs = opportunitySubSpLinkTs;
	}

	public OpportunitySubSpLinkT addOpportunitySubSpLinkT(
			OpportunitySubSpLinkT opportunitySubSpLinkT) {
		getOpportunitySubSpLinkTs().add(opportunitySubSpLinkT);
		opportunitySubSpLinkT.setOpportunityT(this);

		return opportunitySubSpLinkT;
	}

	public OpportunitySubSpLinkT removeOpportunitySubSpLinkT(
			OpportunitySubSpLinkT opportunitySubSpLinkT) {
		getOpportunitySubSpLinkTs().remove(opportunitySubSpLinkT);
		opportunitySubSpLinkT.setOpportunityT(null);

		return opportunitySubSpLinkT;
	}

	public BeaconConvertorMappingT getBeaconConvertorMappingT() {
		return this.beaconConvertorMappingT;
	}

	public void setBeaconConvertorMappingT(
			BeaconConvertorMappingT beaconConvertorMappingT) {
		this.beaconConvertorMappingT = beaconConvertorMappingT;
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

	public void setGeographyCountryMappingT(
			GeographyCountryMappingT geographyCountryMappingT) {
		this.geographyCountryMappingT = geographyCountryMappingT;
	}

	public SalesStageMappingT getSalesStageMappingT() {
		return this.salesStageMappingT;
	}

	public void setSalesStageMappingT(SalesStageMappingT salesStageMappingT) {
		this.salesStageMappingT = salesStageMappingT;
	}

	public UserT getPrimaryOwnerUser() {
		return this.primaryOwnerUser;
	}

	public void setPrimaryOwnerUser(UserT primaryOwnerUser) {
		this.primaryOwnerUser = primaryOwnerUser;

	}

	public List<OpportunityTcsAccountContactLinkT> getOpportunityTcsAccountContactLinkTs() {
		return this.opportunityTcsAccountContactLinkTs;
	}

	public void setOpportunityTcsAccountContactLinkTs(
			List<OpportunityTcsAccountContactLinkT> opportunityTcsAccountContactLinkTs) {
		this.opportunityTcsAccountContactLinkTs = opportunityTcsAccountContactLinkTs;
	}

	public OpportunityTcsAccountContactLinkT addOpportunityTcsAccountContactLinkT(
			OpportunityTcsAccountContactLinkT opportunityTcsAccountContactLinkT) {
		getOpportunityTcsAccountContactLinkTs().add(
				opportunityTcsAccountContactLinkT);
		opportunityTcsAccountContactLinkT.setOpportunityT(this);

		return opportunityTcsAccountContactLinkT;
	}

	public OpportunityTcsAccountContactLinkT removeOpportunityTcsAccountContactLinkT(
			OpportunityTcsAccountContactLinkT opportunityTcsAccountContactLinkT) {
		getOpportunityTcsAccountContactLinkTs().remove(
				opportunityTcsAccountContactLinkT);
		opportunityTcsAccountContactLinkT.setOpportunityT(null);

		return opportunityTcsAccountContactLinkT;
	}

	public List<OpportunityTimelineHistoryT> getOpportunityTimelineHistoryTs() {
		return this.opportunityTimelineHistoryTs;
	}

	public void setOpportunityTimelineHistoryTs(
			List<OpportunityTimelineHistoryT> opportunityTimelineHistoryTs) {
		this.opportunityTimelineHistoryTs = opportunityTimelineHistoryTs;
	}

	public OpportunityTimelineHistoryT addOpportunityTimelineHistoryT(
			OpportunityTimelineHistoryT opportunityTimelineHistoryT) {
		getOpportunityTimelineHistoryTs().add(opportunityTimelineHistoryT);
		opportunityTimelineHistoryT.setOpportunityT(this);

		return opportunityTimelineHistoryT;
	}

	public OpportunityTimelineHistoryT removeOpportunityTimelineHistoryT(
			OpportunityTimelineHistoryT opportunityTimelineHistoryT) {
		getOpportunityTimelineHistoryTs().remove(opportunityTimelineHistoryT);
		opportunityTimelineHistoryT.setOpportunityT(null);

		return opportunityTimelineHistoryT;
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
		userNotificationsT.setOpportunityT(this);

		return userNotificationsT;
	}

	public UserNotificationsT removeUserNotificationsT(
			UserNotificationsT userNotificationsT) {
		getUserNotificationsTs().remove(userNotificationsT);
		userNotificationsT.setOpportunityT(null);

		return userNotificationsT;
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
		userTaggedFollowedT.setOpportunityT(this);

		return userTaggedFollowedT;
	}

	public UserTaggedFollowedT removeUserTaggedFollowedT(
			UserTaggedFollowedT userTaggedFollowedT) {
		getUserTaggedFollowedTs().remove(userTaggedFollowedT);
		userTaggedFollowedT.setOpportunityT(null);

		return userTaggedFollowedT;
	}

	public String getOpportunityOwner() {
		return opportunityOwner;
	}

	public void setOpportunityOwner(String opportunityOwner) {
		this.opportunityOwner = opportunityOwner;
	}

	public String getDealCurrency() {
		return dealCurrency;
	}

	public void setDealCurrency(String dealCurrency) {
		this.dealCurrency = dealCurrency;
	}

	public int getSalesStageCode() {
		return salesStageCode;
	}

	public void setSalesStageCode(int salesStageCode) {
		this.salesStageCode = salesStageCode;
	}

	public String getDealType() {
		return dealType;
	}

	public void setDealType(String dealType) {
		this.dealType = dealType;
	}

	public List<OpportunityWinLossFactorsT> getOpportunityWinLossFactorsTs() {
		return this.opportunityWinLossFactorsTs;
	}

	public void setOpportunityWinLossFactorsTs(
			List<OpportunityWinLossFactorsT> opportunityWinLossFactorsTs) {
		this.opportunityWinLossFactorsTs = opportunityWinLossFactorsTs;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public List<SearchKeywordsT> getSearchKeywordsTs() {
		return searchKeywordsTs;
	}

	public void setSearchKeywordsTs(List<SearchKeywordsT> searchKeywordsTs) {
		this.searchKeywordsTs = searchKeywordsTs;
	}

	public List<ConnectOpportunityLinkIdT> getDeleteConnectOpportunityLinkIdTs() {
		return deleteConnectOpportunityLinkIdTs;
	}

	public void setDeleteConnectOpportunityLinkIdTs(
			List<ConnectOpportunityLinkIdT> deleteConnectOpportunityLinkIdTs) {
		this.deleteConnectOpportunityLinkIdTs = deleteConnectOpportunityLinkIdTs;
	}

	public List<NotesT> getDeleteNotesTs() {
		return deleteNotesTs;
	}

	public void setDeleteNotesTs(List<NotesT> deleteNotesTs) {
		this.deleteNotesTs = deleteNotesTs;
	}

	public List<OpportunityCompetitorLinkT> getDeleteOpportunityCompetitorLinkTs() {
		return deleteOpportunityCompetitorLinkTs;
	}

	public void setDeleteOpportunityCompetitorLinkTs(
			List<OpportunityCompetitorLinkT> deleteOpportunityCompetitorLinkTs) {
		this.deleteOpportunityCompetitorLinkTs = deleteOpportunityCompetitorLinkTs;
	}

	public List<OpportunityCustomerContactLinkT> getDeleteOpportunityCustomerContactLinkTs() {
		return deleteOpportunityCustomerContactLinkTs;
	}

	public void setDeleteOpportunityCustomerContactLinkTs(
			List<OpportunityCustomerContactLinkT> deleteOpportunityCustomerContactLinkTs) {
		this.deleteOpportunityCustomerContactLinkTs = deleteOpportunityCustomerContactLinkTs;
	}

	public List<OpportunityPartnerLinkT> getDeleteOpportunityPartnerLinkTs() {
		return deleteOpportunityPartnerLinkTs;
	}

	public void setDeleteOpportunityPartnerLinkTs(
			List<OpportunityPartnerLinkT> deleteOpportunityPartnerLinkTs) {
		this.deleteOpportunityPartnerLinkTs = deleteOpportunityPartnerLinkTs;
	}

	public List<OpportunitySalesSupportLinkT> getDeleteOpportunitySalesSupportLinkTs() {
		return deleteOpportunitySalesSupportLinkTs;
	}

	public void setDeleteOpportunitySalesSupportLinkTs(
			List<OpportunitySalesSupportLinkT> deleteOpportunitySalesSupportLinkTs) {
		this.deleteOpportunitySalesSupportLinkTs = deleteOpportunitySalesSupportLinkTs;
	}

	public List<OpportunitySubSpLinkT> getDeleteOpportunitySubSpLinkTs() {
		return deleteOpportunitySubSpLinkTs;
	}

	public void setDeleteOpportunitySubSpLinkTs(
			List<OpportunitySubSpLinkT> deleteOpportunitySubSpLinkTs) {
		this.deleteOpportunitySubSpLinkTs = deleteOpportunitySubSpLinkTs;
	}

	public List<OpportunityTcsAccountContactLinkT> getDeleteOpportunityOpportunityTcsAccountContactLinkTs() {
		return deleteOpportunityOpportunityTcsAccountContactLinkTs;
	}

	public void setDeleteOpportunityOpportunityTcsAccountContactLinkTs(
			List<OpportunityTcsAccountContactLinkT> deleteOpportunityOpportunityTcsAccountContactLinkTs) {
		this.deleteOpportunityOpportunityTcsAccountContactLinkTs = deleteOpportunityOpportunityTcsAccountContactLinkTs;
	}

	public List<OpportunityOfferingLinkT> getDeleteOpportunityOfferingLinkTs() {
		return deleteOpportunityOfferingLinkTs;
	}

	public void setDeleteOpportunityOfferingLinkTs(
			List<OpportunityOfferingLinkT> deleteOpportunityOfferingLinkTs) {
		this.deleteOpportunityOfferingLinkTs = deleteOpportunityOfferingLinkTs;
	}

	public List<OpportunityTcsAccountContactLinkT> getDeleteOpportunityTcsAccountContactLinkTs() {
		return deleteOpportunityTcsAccountContactLinkTs;
	}

	public void setDeleteOpportunityTcsAccountContactLinkTs(
			List<OpportunityTcsAccountContactLinkT> deleteOpportunityTcsAccountContactLinkTs) {
		this.deleteOpportunityTcsAccountContactLinkTs = deleteOpportunityTcsAccountContactLinkTs;
	}

	public String getDigitalFlag() {
		return digitalFlag;
	}

	public void setDigitalFlag(String digitalFlag) {
		this.digitalFlag = digitalFlag;
	}

	public OpportunityT clone() throws CloneNotSupportedException {
		return (OpportunityT) super.clone();
	}

	public OpportunityWinLossFactorsT addOpportunityWinLossFactorsT(
			OpportunityWinLossFactorsT opportunityWinLossFactorsT) {
		getOpportunityWinLossFactorsTs().add(opportunityWinLossFactorsT);
		opportunityWinLossFactorsT.setOpportunityT(this);

		return opportunityWinLossFactorsT;
	}

	public OpportunityWinLossFactorsT removeOpportunityWinLossFactorsT(
			OpportunityWinLossFactorsT opportunityWinLossFactorsT) {
		getOpportunityWinLossFactorsTs().remove(opportunityWinLossFactorsT);
		opportunityWinLossFactorsT.setOpportunityT(null);

		return opportunityWinLossFactorsT;
	}

	public List<OpportunityWinLossFactorsT> getDeleteOpportunityWinLossFactorsTs() {
		return deleteOpportunityWinLossFactorsTs;
	}

	public void setDeleteOpportunityWinLossFactorsTs(
			List<OpportunityWinLossFactorsT> deleteOpportunityWinLossFactorsTs) {
		this.deleteOpportunityWinLossFactorsTs = deleteOpportunityWinLossFactorsTs;
	}
	
	public List<SearchKeywordsT> getDeleteSearchKeywordsTs() {
		return deleteSearchKeywordsTs;
	}
	
	public void setDeleteSearchKeywordsTs(
			List<SearchKeywordsT> deleteSearchKeywordsTs) {
		this.deleteSearchKeywordsTs = deleteSearchKeywordsTs;
	}

	public List<OpportunityDealValue> getOpportunityDealValues() {
		return opportunityDealValues;
	}

	public void setOpportunityDealValues(
			List<OpportunityDealValue> opportunityDealValues) {
		this.opportunityDealValues = opportunityDealValues;
	}

}