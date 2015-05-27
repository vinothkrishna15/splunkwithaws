package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the user_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
@Entity
@Table(name = "user_t")
@NamedQuery(name = "UserT.findAll", query = "SELECT u FROM UserT u")
public class UserT implements Serializable {
	private static final long serialVersionUID = 1L;

	public UserT(UserT user) {
		this.bdmTargetTs = user.bdmTargetTs;
		this.bidOfficeGroupOwnerLinkTs = user.bidOfficeGroupOwnerLinkTs;
		this.collaborationCommentTs = user.collaborationCommentTs;
		this.connectSecondaryOwnerLinkTs = user.connectSecondaryOwnerLinkTs;
		this.connectTs = user.connectTs;
		this.documentRepositoryTs = user.documentRepositoryTs;
		this.frequentlySearchedCustomerPartnerTs = user.frequentlySearchedCustomerPartnerTs;
		this.loginHistoryTs = user.loginHistoryTs;
		this.notesTs = user.notesTs;
		this.opportunitySalesSupportLinkTs = user.opportunitySalesSupportLinkTs;
		this.opportunityTimelineHistoryTs = user.opportunityTimelineHistoryTs;
		this.opportunityTs = user.opportunityTs;
		this.supervisorUserId = user.supervisorUserId;
		this.supervisorUserId = user.supervisorUserId;
		this.taskBdmsTaggedLinkTs = user.taskBdmsTaggedLinkTs;
		this.taskTs = user.taskTs;
		this.tempPassword = user.tempPassword;
		this.userEmailId = user.userEmailId;
		this.userFavoritesTs = user.userFavoritesTs;
		this.userGeography = user.userGeography;
		this.userGroupMappingT = user.userGroupMappingT;
		this.userId = user.userId;
		this.userName = user.userName;
		this.userPhoto = user.userPhoto;
		this.userRoleMappingT = user.userRoleMappingT;
		this.userGeneralSettingsT = user.userGeneralSettingsT;
		this.userTelephone = user.userTelephone;

	}

	@Id
	@Column(name = "user_id")
	private String userId;

	@Column(name = "supervisor_user_id")
	private String supervisorUserId;

	@Column(name = "supervisor_user_name")
	private String supervisorUserName;

	@Column(name = "temp_password")
	private String tempPassword;

	@Column(name = "user_email_id")
	private String userEmailId;

	@Column(name = "user_geography")
	private String userGeography;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "user_photo")
	private byte[] userPhoto;

	@Column(name = "user_telephone")
	private String userTelephone;

	@Transient
	private Timestamp lastLogin;

	// bi-directional many-to-one association to BdmTargetT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<BdmTargetT> bdmTargetTs;

	// bi-directional many-to-one association to BidDetailsT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<BidDetailsT> bidDetailsTs;

	@OneToMany(mappedBy = "userT")
	private List<ContactCustomerLinkT> contactCustomerLinkTs;

	@JsonIgnore
	@OneToMany(mappedBy = "createdUser")
	private List<FeedbackT> feedbackTs1;
	
	@JsonIgnore
	@OneToMany(mappedBy = "modifiedUser")
	private List<FeedbackT> feedbackTs2;

	// bi-directional many-to-one association to BidOfficeGroupOwnerLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "bidOfficeGroupOwnerUser")
	private List<BidOfficeGroupOwnerLinkT> bidOfficeGroupOwnerLinkTs;

	// bi-directional many-to-one association to BidOfficeGroupOwnerLinkT
	@OneToMany(mappedBy = "createdModifiedUser")
	private List<BidOfficeGroupOwnerLinkT> bidOfficeGroupOwnerLinkTs2;

	// bi-directional many-to-one association to CollaborationCommentT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<CollaborationCommentT> collaborationCommentTs;

	// bi-directional many-to-one association to CommentsT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<CommentsT> commentsTs;

	// bi-directional many-to-one association to ConnectCustomerContactLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<ConnectCustomerContactLinkT> connectCustomerContactLinkTs;

	// bi-directional many-to-one association to ConnectOfferingLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<ConnectOfferingLinkT> connectOfferingLinkTs;

	// bi-directional many-to-one association to ConnectOpportunityLinkIdT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<ConnectOpportunityLinkIdT> connectOpportunityLinkIdTs;

	// bi-directional many-to-one association to ConnectSecondaryOwnerLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerLinkTs;

	// bi-directional many-to-one association to ConnectSecondaryOwnerLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerLinkTs2;

	// bi-directional many-to-one association to ConnectSubSpLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<ConnectSubSpLinkT> connectSubSpLinkTs;

	// bi-directional many-to-one association to ConnectT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<ConnectT> connectTs;

	// bi-directional many-to-one association to ConnectT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<ConnectT> connectTs2;

	// bi-directional many-to-one association to ConnectTcsAccountContactLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<ConnectTcsAccountContactLinkT> connectTcsAccountContactLinkTs;

	// bi-directional many-to-one association to ContactT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<ContactT> contactTs;

	// bi-directional many-to-one association to CustomerMasterT
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<CustomerMasterT> customerMasterTs;

	// bi-directional many-to-one association to DocumentRepositoryT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<DocumentRepositoryT> documentRepositoryTs;

	//bi-directional many-to-one association to FeedbackT
	

	// bi-directional many-to-one association to
	// FrequentlySearchedCustomerPartnerT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<FrequentlySearchedCustomerPartnerT> frequentlySearchedCustomerPartnerTs;

	// bi-directional many-to-one association to LoginHistoryT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<LoginHistoryT> loginHistoryTs;

	// bi-directional many-to-one association to NotesT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<NotesT> notesTs;

	// bi-directional many-to-one association to OpportunityCompetitorLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<OpportunityCompetitorLinkT> opportunityCompetitorLinkTs;

	// bi-directional many-to-one association to OpportunityCustomerContactLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<OpportunityCustomerContactLinkT> opportunityCustomerContactLinkTs;

	// bi-directional many-to-one association to OpportunityOfferingLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<OpportunityOfferingLinkT> opportunityOfferingLinkTs;

	// bi-directional many-to-one association to OpportunityPartnerLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<OpportunityPartnerLinkT> opportunityPartnerLinkTs;

	// bi-directional many-to-one association to OpportunitySalesSupportLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "salesSupportOwnerUser")
	private List<OpportunitySalesSupportLinkT> opportunitySalesSupportLinkTs;

	// bi-directional many-to-one association to OpportunitySalesSupportLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<OpportunitySalesSupportLinkT> opportunitySalesSupportLinkTs2;

	// bi-directional many-to-one association to OpportunitySubSpLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<OpportunitySubSpLinkT> opportunitySubSpLinkTs;

	// bi-directional many-to-one association to OpportunityT
	@JsonIgnore
	@OneToMany(mappedBy = "primaryOwnerUser")
	private List<OpportunityT> opportunityTs;

	// bi-directional many-to-one association to OpportunityT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<OpportunityT> opportunityTs2;

	// bi-directional many-to-one association to
	// OpportunityTcsAccountContactLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<OpportunityTcsAccountContactLinkT> opportunityTcsAccountContactLinkTs;

	// bi-directional many-to-one association to OpportunityTimelineHistoryT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<OpportunityTimelineHistoryT> opportunityTimelineHistoryTs;

	// bi-directional many-to-one association to OpportunityWinLossFactorsT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<OpportunityWinLossFactorsT> opportunityWinLossFactorsTs;

	// bi-directional many-to-one association to PartnerMasterT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<PartnerMasterT> partnerMasterTs;

	// bi-directional one-to-one association to PushNotificationRegistrationT
	@OneToOne(mappedBy = "userT")
	private PushNotificationRegistrationT pushNotificationRegistrationT;

	// bi-directional many-to-one association to SearchKeywordsT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<SearchKeywordsT> searchKeywordsTs;

	// bi-directional many-to-one association to TaskBdmsTaggedLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<TaskBdmsTaggedLinkT> taskBdmsTaggedLinkTs;

	// bi-directional many-to-one association to TaskBdmsTaggedLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<TaskBdmsTaggedLinkT> taskBdmsTaggedLinkTs2;

	// bi-directional many-to-one association to TaskT
	@JsonIgnore
	@OneToMany(mappedBy = "taskOwnerT")
	private List<TaskT> taskTs;

	// bi-directional many-to-one association to TaskT
	@JsonIgnore
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<TaskT> taskTs2;

	// bi-directional many-to-one association to UserFavoritesT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<UserFavoritesT> userFavoritesTs;

	// bi-directional one-to-one association to UserGeneralSettingsT
	@JsonIgnore
	@OneToOne(mappedBy = "userT")
	private UserGeneralSettingsT userGeneralSettingsT;

	// bi-directional many-to-one association to
	// UserNotificationSettingsConditionsT
	@OneToMany(mappedBy = "userT")
	private List<UserNotificationSettingsConditionsT> userNotificationSettingsConditionsTs;

	// bi-directional many-to-one association to UserNotificationSettingsT
	@OneToMany(mappedBy = "userT")
	private List<UserNotificationSettingsT> userNotificationSettingsTs;

	// bi-directional many-to-one association to UserNotificationsT
	@OneToMany(mappedBy = "userT1")
	private List<UserNotificationsT> userNotificationsTs1;

	// bi-directional many-to-one association to UserNotificationsT
	@OneToMany(mappedBy = "userT2")
	private List<UserNotificationsT> userNotificationsTs2;

	// bi-directional many-to-one association to UserGroupMappingT
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_group")
	private UserGroupMappingT userGroupMappingT;

	// bi-directional many-to-one association to UserRoleMappingT
	// @JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_role")
	private UserRoleMappingT userRoleMappingT;

	// bi-directional many-to-one association to UserTaggedFollowedT
	@OneToMany(mappedBy = "userT1")
	private List<UserTaggedFollowedT> userTaggedFollowedTs1;

	// bi-directional many-to-one association to UserTaggedFollowedT
	@OneToMany(mappedBy = "userT2")
	private List<UserTaggedFollowedT> userTaggedFollowedTs2;

	// bi-directional many-to-one association to GeographyMappingT
	@ManyToOne
	@JoinColumn(name = "user_geography", insertable = false, updatable = false)
	private GeographyMappingT geographyMappingT;

	public UserT() {
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSupervisorUserId() {
		return this.supervisorUserId;
	}

	public void setSupervisorUserId(String supervisorUserId) {
		this.supervisorUserId = supervisorUserId;
	}

	public String getSupervisorUserName() {
		return this.supervisorUserName;
	}

	public void setSupervisorUserName(String supervisorUserName) {
		this.supervisorUserName = supervisorUserName;
	}

	public String getTempPassword() {
		return this.tempPassword;
	}

	public void setTempPassword(String tempPassword) {
		this.tempPassword = tempPassword;
	}

	public String getUserEmailId() {
		return this.userEmailId;
	}

	public void setUserEmailId(String userEmailId) {
		this.userEmailId = userEmailId;
	}

	public String getUserGeography() {
		return this.userGeography;
	}

	public void setUserGeography(String userGeography) {
		this.userGeography = userGeography;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public byte[] getUserPhoto() {
		return this.userPhoto;
	}

	public void setUserPhoto(byte[] userPhoto) {
		this.userPhoto = userPhoto;
	}

	public String getUserTelephone() {
		return this.userTelephone;
	}

	public void setUserTelephone(String userTelephone) {
		this.userTelephone = userTelephone;
	}

	public List<BdmTargetT> getBdmTargetTs() {
		return this.bdmTargetTs;
	}

	public void setBdmTargetTs(List<BdmTargetT> bdmTargetTs) {
		this.bdmTargetTs = bdmTargetTs;
	}

	public BdmTargetT addBdmTargetT(BdmTargetT bdmTargetT) {
		getBdmTargetTs().add(bdmTargetT);
		bdmTargetT.setUserT(this);

		return bdmTargetT;
	}

	public BdmTargetT removeBdmTargetT(BdmTargetT bdmTargetT) {
		getBdmTargetTs().remove(bdmTargetT);
		bdmTargetT.setUserT(null);

		return bdmTargetT;
	}

	public List<ContactCustomerLinkT> getContactCustomerLinkTs() {
		return this.contactCustomerLinkTs;
	}

	public void setContactCustomerLinkTs(
			List<ContactCustomerLinkT> contactCustomerLinkTs) {
		this.contactCustomerLinkTs = contactCustomerLinkTs;
	}

	public ContactCustomerLinkT addContactCustomerLinkT(
			ContactCustomerLinkT contactCustomerLinkT) {
		getContactCustomerLinkTs().add(contactCustomerLinkT);
		contactCustomerLinkT.setUserT(this);

		return contactCustomerLinkT;
	}

	public ContactCustomerLinkT removeContactCustomerLinkT(
			ContactCustomerLinkT contactCustomerLinkT) {
		getContactCustomerLinkTs().remove(contactCustomerLinkT);
		contactCustomerLinkT.setUserT(null);

		return contactCustomerLinkT;
	}

	public List<BidDetailsT> getBidDetailsTs() {
		return this.bidDetailsTs;
	}

	public void setBidDetailsTs(List<BidDetailsT> bidDetailsTs) {
		this.bidDetailsTs = bidDetailsTs;
	}

	public BidDetailsT addBidDetailsT(BidDetailsT bidDetailsT) {
		getBidDetailsTs().add(bidDetailsT);
		bidDetailsT.setUserT(this);

		return bidDetailsT;
	}

	public BidDetailsT removeBidDetailsT(BidDetailsT bidDetailsT) {
		getBidDetailsTs().remove(bidDetailsT);
		bidDetailsT.setUserT(null);

		return bidDetailsT;
	}

	public List<BidOfficeGroupOwnerLinkT> getBidOfficeGroupOwnerLinkTs() {
		return this.bidOfficeGroupOwnerLinkTs;
	}

	public void setBidOfficeGroupOwnerLinkTs(
			List<BidOfficeGroupOwnerLinkT> bidOfficeGroupOwnerLinkTs) {
		this.bidOfficeGroupOwnerLinkTs = bidOfficeGroupOwnerLinkTs;
	}

	public BidOfficeGroupOwnerLinkT addBidOfficeGroupOwnerLinkT(
			BidOfficeGroupOwnerLinkT bidOfficeGroupOwnerLinkT) {
		getBidOfficeGroupOwnerLinkTs().add(bidOfficeGroupOwnerLinkT);
		bidOfficeGroupOwnerLinkT.setBidOfficeGroupOwnerUser(this);

		return bidOfficeGroupOwnerLinkT;
	}

	public BidOfficeGroupOwnerLinkT removeBidOfficeGroupOwnerLinkT(
			BidOfficeGroupOwnerLinkT bidOfficeGroupOwnerLinkT) {
		getBidOfficeGroupOwnerLinkTs().remove(bidOfficeGroupOwnerLinkT);
		bidOfficeGroupOwnerLinkT.setBidOfficeGroupOwnerUser(null);

		return bidOfficeGroupOwnerLinkT;
	}

	public List<BidOfficeGroupOwnerLinkT> getBidOfficeGroupOwnerLinkTs2() {
		return this.bidOfficeGroupOwnerLinkTs2;
	}

	public void setBidOfficeGroupOwnerLinkTs2(
			List<BidOfficeGroupOwnerLinkT> bidOfficeGroupOwnerLinkTs2) {
		this.bidOfficeGroupOwnerLinkTs2 = bidOfficeGroupOwnerLinkTs2;
	}

	public BidOfficeGroupOwnerLinkT addBidOfficeGroupOwnerLinkTs2(
			BidOfficeGroupOwnerLinkT bidOfficeGroupOwnerLinkTs2) {
		getBidOfficeGroupOwnerLinkTs2().add(bidOfficeGroupOwnerLinkTs2);
		bidOfficeGroupOwnerLinkTs2.setCreatedModifiedUser(this);

		return bidOfficeGroupOwnerLinkTs2;
	}

	public BidOfficeGroupOwnerLinkT removeBidOfficeGroupOwnerLinkTs2(
			BidOfficeGroupOwnerLinkT bidOfficeGroupOwnerLinkTs2) {
		getBidOfficeGroupOwnerLinkTs2().remove(bidOfficeGroupOwnerLinkTs2);
		bidOfficeGroupOwnerLinkTs2.setCreatedModifiedUser(null);

		return bidOfficeGroupOwnerLinkTs2;
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
		collaborationCommentT.setUserT(this);

		return collaborationCommentT;
	}

	public CollaborationCommentT removeCollaborationCommentT(
			CollaborationCommentT collaborationCommentT) {
		getCollaborationCommentTs().remove(collaborationCommentT);
		collaborationCommentT.setUserT(null);

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
		commentsT.setUserT(this);

		return commentsT;
	}

	public CommentsT removeCommentsT(CommentsT commentsT) {
		getCommentsTs().remove(commentsT);
		commentsT.setUserT(null);

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
		connectCustomerContactLinkT.setCreatedModifiedByUser(this);

		return connectCustomerContactLinkT;
	}

	public ConnectCustomerContactLinkT removeConnectCustomerContactLinkT(
			ConnectCustomerContactLinkT connectCustomerContactLinkT) {
		getConnectCustomerContactLinkTs().remove(connectCustomerContactLinkT);
		connectCustomerContactLinkT.setCreatedModifiedByUser(null);

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
		connectOfferingLinkT.setCreatedModifiedByUser(this);

		return connectOfferingLinkT;
	}

	public ConnectOfferingLinkT removeConnectOfferingLinkT(
			ConnectOfferingLinkT connectOfferingLinkT) {
		getConnectOfferingLinkTs().remove(connectOfferingLinkT);
		connectOfferingLinkT.setCreatedModifiedByUser(null);

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
		connectOpportunityLinkIdT.setCreatedModifiedByUser(this);

		return connectOpportunityLinkIdT;
	}

	public ConnectOpportunityLinkIdT removeConnectOpportunityLinkIdT(
			ConnectOpportunityLinkIdT connectOpportunityLinkIdT) {
		getConnectOpportunityLinkIdTs().remove(connectOpportunityLinkIdT);
		connectOpportunityLinkIdT.setCreatedModifiedByUser(null);

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
		connectSecondaryOwnerLinkT.setUserT(this);

		return connectSecondaryOwnerLinkT;
	}

	public ConnectSecondaryOwnerLinkT removeConnectSecondaryOwnerLinkT(
			ConnectSecondaryOwnerLinkT connectSecondaryOwnerLinkT) {
		getConnectSecondaryOwnerLinkTs().remove(connectSecondaryOwnerLinkT);
		connectSecondaryOwnerLinkT.setUserT(null);

		return connectSecondaryOwnerLinkT;
	}

	public List<ConnectSecondaryOwnerLinkT> getConnectSecondaryOwnerLinkTs2() {
		return this.connectSecondaryOwnerLinkTs2;
	}

	public void setConnectSecondaryOwnerLinkTs2(
			List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerLinkTs2) {
		this.connectSecondaryOwnerLinkTs2 = connectSecondaryOwnerLinkTs2;
	}

	public ConnectSecondaryOwnerLinkT addConnectSecondaryOwnerLinkTs2(
			ConnectSecondaryOwnerLinkT connectSecondaryOwnerLinkTs2) {
		getConnectSecondaryOwnerLinkTs2().add(connectSecondaryOwnerLinkTs2);
		connectSecondaryOwnerLinkTs2.setCreatedModifiedByUser(this);

		return connectSecondaryOwnerLinkTs2;
	}

	public ConnectSecondaryOwnerLinkT removeConnectSecondaryOwnerLinkTs2(
			ConnectSecondaryOwnerLinkT connectSecondaryOwnerLinkTs2) {
		getConnectSecondaryOwnerLinkTs2().remove(connectSecondaryOwnerLinkTs2);
		connectSecondaryOwnerLinkTs2.setCreatedModifiedByUser(null);

		return connectSecondaryOwnerLinkTs2;
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
		connectSubSpLinkT.setCreatedModifiedByUser(this);

		return connectSubSpLinkT;
	}

	public ConnectSubSpLinkT removeConnectSubSpLinkT(
			ConnectSubSpLinkT connectSubSpLinkT) {
		getConnectSubSpLinkTs().remove(connectSubSpLinkT);
		connectSubSpLinkT.setCreatedModifiedByUser(null);

		return connectSubSpLinkT;
	}

	public List<ConnectT> getConnectTs() {
		return this.connectTs;
	}

	public void setConnectTs(List<ConnectT> connectTs) {
		this.connectTs = connectTs;
	}

	public ConnectT addConnectT(ConnectT connectT) {
		getConnectTs().add(connectT);
		connectT.setUserT(this);

		return connectT;
	}

	public ConnectT removeConnectT(ConnectT connectT) {
		getConnectTs().remove(connectT);
		connectT.setUserT(null);

		return connectT;
	}

	public List<ConnectT> getConnectTs2() {
		return this.connectTs2;
	}

	public void setConnectTs2(List<ConnectT> connectTs2) {
		this.connectTs2 = connectTs2;
	}

	public ConnectT addConnectTs2(ConnectT connectTs2) {
		getConnectTs2().add(connectTs2);
		connectTs2.setCreatedModifiedByUser(this);

		return connectTs2;
	}

	public ConnectT removeConnectTs2(ConnectT connectTs2) {
		getConnectTs2().remove(connectTs2);
		connectTs2.setCreatedModifiedByUser(null);

		return connectTs2;
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
		connectTcsAccountContactLinkT.setCreatedModifiedByUser(this);

		return connectTcsAccountContactLinkT;
	}

	public ConnectTcsAccountContactLinkT removeConnectTcsAccountContactLinkT(
			ConnectTcsAccountContactLinkT connectTcsAccountContactLinkT) {
		getConnectTcsAccountContactLinkTs().remove(
				connectTcsAccountContactLinkT);
		connectTcsAccountContactLinkT.setCreatedModifiedByUser(null);

		return connectTcsAccountContactLinkT;
	}

	public List<ContactT> getContactTs() {
		return this.contactTs;
	}

	public void setContactTs(List<ContactT> contactTs) {
		this.contactTs = contactTs;
	}

	public ContactT addContactT(ContactT contactT) {
		getContactTs().add(contactT);
		contactT.setCreatedModifiedByUser(this);

		return contactT;
	}

	public ContactT removeContactT(ContactT contactT) {
		getContactTs().remove(contactT);
		contactT.setCreatedModifiedByUser(null);

		return contactT;
	}

	public List<CustomerMasterT> getCustomerMasterTs() {
		return this.customerMasterTs;
	}

	public void setCustomerMasterTs(List<CustomerMasterT> customerMasterTs) {
		this.customerMasterTs = customerMasterTs;
	}

	public CustomerMasterT addCustomerMasterT(CustomerMasterT customerMasterT) {
		getCustomerMasterTs().add(customerMasterT);
		customerMasterT.setCreatedModifiedByUser(this);

		return customerMasterT;
	}

	public CustomerMasterT removeCustomerMasterT(CustomerMasterT customerMasterT) {
		getCustomerMasterTs().remove(customerMasterT);
		customerMasterT.setCreatedModifiedByUser(null);

		return customerMasterT;
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
		documentRepositoryT.setUserT(this);

		return documentRepositoryT;
	}

	public DocumentRepositoryT removeDocumentRepositoryT(
			DocumentRepositoryT documentRepositoryT) {
		getDocumentRepositoryTs().remove(documentRepositoryT);
		documentRepositoryT.setUserT(null);

		return documentRepositoryT;
	}

	public List<FrequentlySearchedCustomerPartnerT> getFrequentlySearchedCustomerPartnerTs() {
		return this.frequentlySearchedCustomerPartnerTs;
	}

	public void setFrequentlySearchedCustomerPartnerTs(
			List<FrequentlySearchedCustomerPartnerT> frequentlySearchedCustomerPartnerTs) {
		this.frequentlySearchedCustomerPartnerTs = frequentlySearchedCustomerPartnerTs;
	}

	public FrequentlySearchedCustomerPartnerT addFrequentlySearchedCustomerPartnerT(
			FrequentlySearchedCustomerPartnerT frequentlySearchedCustomerPartnerT) {
		getFrequentlySearchedCustomerPartnerTs().add(
				frequentlySearchedCustomerPartnerT);
		frequentlySearchedCustomerPartnerT.setUserT(this);

		return frequentlySearchedCustomerPartnerT;
	}

	public FrequentlySearchedCustomerPartnerT removeFrequentlySearchedCustomerPartnerT(
			FrequentlySearchedCustomerPartnerT frequentlySearchedCustomerPartnerT) {
		getFrequentlySearchedCustomerPartnerTs().remove(
				frequentlySearchedCustomerPartnerT);
		frequentlySearchedCustomerPartnerT.setUserT(null);

		return frequentlySearchedCustomerPartnerT;
	}

	public List<LoginHistoryT> getLoginHistoryTs() {
		return this.loginHistoryTs;
	}

	public void setLoginHistoryTs(List<LoginHistoryT> loginHistoryTs) {
		this.loginHistoryTs = loginHistoryTs;
	}

	public LoginHistoryT addLoginHistoryT(LoginHistoryT loginHistoryT) {
		getLoginHistoryTs().add(loginHistoryT);
		loginHistoryT.setUserT(this);

		return loginHistoryT;
	}

	public LoginHistoryT removeLoginHistoryT(LoginHistoryT loginHistoryT) {
		getLoginHistoryTs().remove(loginHistoryT);
		loginHistoryT.setUserT(null);

		return loginHistoryT;
	}

	public List<NotesT> getNotesTs() {
		return this.notesTs;
	}

	public void setNotesTs(List<NotesT> notesTs) {
		this.notesTs = notesTs;
	}

	public NotesT addNotesT(NotesT notesT) {
		getNotesTs().add(notesT);
		notesT.setUserT(this);

		return notesT;
	}

	public NotesT removeNotesT(NotesT notesT) {
		getNotesTs().remove(notesT);
		notesT.setUserT(null);

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
		opportunityCompetitorLinkT.setCreatedModifiedByUser(this);

		return opportunityCompetitorLinkT;
	}

	public OpportunityCompetitorLinkT removeOpportunityCompetitorLinkT(
			OpportunityCompetitorLinkT opportunityCompetitorLinkT) {
		getOpportunityCompetitorLinkTs().remove(opportunityCompetitorLinkT);
		opportunityCompetitorLinkT.setCreatedModifiedByUser(null);

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
		opportunityCustomerContactLinkT.setCreatedModifiedByUser(this);

		return opportunityCustomerContactLinkT;
	}

	public OpportunityCustomerContactLinkT removeOpportunityCustomerContactLinkT(
			OpportunityCustomerContactLinkT opportunityCustomerContactLinkT) {
		getOpportunityCustomerContactLinkTs().remove(
				opportunityCustomerContactLinkT);
		opportunityCustomerContactLinkT.setCreatedModifiedByUser(null);

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
		opportunityOfferingLinkT.setCreatedModifiedByUser(this);

		return opportunityOfferingLinkT;
	}

	public OpportunityOfferingLinkT removeOpportunityOfferingLinkT(
			OpportunityOfferingLinkT opportunityOfferingLinkT) {
		getOpportunityOfferingLinkTs().remove(opportunityOfferingLinkT);
		opportunityOfferingLinkT.setCreatedModifiedByUser(null);

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
		opportunityPartnerLinkT.setCreatedModifiedByUser(this);

		return opportunityPartnerLinkT;
	}

	public OpportunityPartnerLinkT removeOpportunityPartnerLinkT(
			OpportunityPartnerLinkT opportunityPartnerLinkT) {
		getOpportunityPartnerLinkTs().remove(opportunityPartnerLinkT);
		opportunityPartnerLinkT.setCreatedModifiedByUser(null);

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
		opportunitySalesSupportLinkT.setSalesSupportOwnerUser(this);

		return opportunitySalesSupportLinkT;
	}

	public OpportunitySalesSupportLinkT removeOpportunitySalesSupportLinkT(
			OpportunitySalesSupportLinkT opportunitySalesSupportLinkT) {
		getOpportunitySalesSupportLinkTs().remove(opportunitySalesSupportLinkT);
		opportunitySalesSupportLinkT.setSalesSupportOwnerUser(null);

		return opportunitySalesSupportLinkT;
	}

	public List<OpportunitySalesSupportLinkT> getOpportunitySalesSupportLinkTs2() {
		return this.opportunitySalesSupportLinkTs2;
	}

	public void setOpportunitySalesSupportLinkTs2(
			List<OpportunitySalesSupportLinkT> opportunitySalesSupportLinkTs2) {
		this.opportunitySalesSupportLinkTs2 = opportunitySalesSupportLinkTs2;
	}

	public OpportunitySalesSupportLinkT addOpportunitySalesSupportLinkTs2(
			OpportunitySalesSupportLinkT opportunitySalesSupportLinkTs2) {
		getOpportunitySalesSupportLinkTs2().add(opportunitySalesSupportLinkTs2);
		opportunitySalesSupportLinkTs2.setCreatedModifiedByUser(this);

		return opportunitySalesSupportLinkTs2;
	}

	public OpportunitySalesSupportLinkT removeOpportunitySalesSupportLinkTs2(
			OpportunitySalesSupportLinkT opportunitySalesSupportLinkTs2) {
		getOpportunitySalesSupportLinkTs2().remove(
				opportunitySalesSupportLinkTs2);
		opportunitySalesSupportLinkTs2.setCreatedModifiedByUser(null);

		return opportunitySalesSupportLinkTs2;
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
		opportunitySubSpLinkT.setCreatedModifiedByUser(this);

		return opportunitySubSpLinkT;
	}

	public OpportunitySubSpLinkT removeOpportunitySubSpLinkT(
			OpportunitySubSpLinkT opportunitySubSpLinkT) {
		getOpportunitySubSpLinkTs().remove(opportunitySubSpLinkT);
		opportunitySubSpLinkT.setCreatedModifiedByUser(null);

		return opportunitySubSpLinkT;
	}

	public List<OpportunityT> getOpportunityTs() {
		return this.opportunityTs;
	}

	public void setOpportunityTs(List<OpportunityT> opportunityTs) {
		this.opportunityTs = opportunityTs;
	}

	public OpportunityT addOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().add(opportunityT);
		opportunityT.setPrimaryOwnerUser(this);

		return opportunityT;
	}

	public OpportunityT removeOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().remove(opportunityT);
		opportunityT.setPrimaryOwnerUser(null);

		return opportunityT;
	}

	public List<OpportunityT> getOpportunityTs2() {
		return this.opportunityTs2;
	}

	public void setOpportunityTs2(List<OpportunityT> opportunityTs2) {
		this.opportunityTs2 = opportunityTs2;
	}

	public OpportunityT addOpportunityTs2(OpportunityT opportunityTs2) {
		getOpportunityTs2().add(opportunityTs2);
		opportunityTs2.setCreatedModifiedByUser(this);

		return opportunityTs2;
	}

	public OpportunityT removeOpportunityTs2(OpportunityT opportunityTs2) {
		getOpportunityTs2().remove(opportunityTs2);
		opportunityTs2.setCreatedModifiedByUser(null);

		return opportunityTs2;
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
		opportunityTcsAccountContactLinkT.setCreatedModifiedByUser(this);

		return opportunityTcsAccountContactLinkT;
	}

	public OpportunityTcsAccountContactLinkT removeOpportunityTcsAccountContactLinkT(
			OpportunityTcsAccountContactLinkT opportunityTcsAccountContactLinkT) {
		getOpportunityTcsAccountContactLinkTs().remove(
				opportunityTcsAccountContactLinkT);
		opportunityTcsAccountContactLinkT.setCreatedModifiedByUser(null);

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
		opportunityTimelineHistoryT.setUserT(this);

		return opportunityTimelineHistoryT;
	}

	public OpportunityTimelineHistoryT removeOpportunityTimelineHistoryT(
			OpportunityTimelineHistoryT opportunityTimelineHistoryT) {
		getOpportunityTimelineHistoryTs().remove(opportunityTimelineHistoryT);
		opportunityTimelineHistoryT.setUserT(null);

		return opportunityTimelineHistoryT;
	}

	public List<OpportunityWinLossFactorsT> getOpportunityWinLossFactorsTs() {
		return this.opportunityWinLossFactorsTs;
	}

	public void setOpportunityWinLossFactorsTs(
			List<OpportunityWinLossFactorsT> opportunityWinLossFactorsTs) {
		this.opportunityWinLossFactorsTs = opportunityWinLossFactorsTs;
	}

	public OpportunityWinLossFactorsT addOpportunityWinLossFactorsT(
			OpportunityWinLossFactorsT opportunityWinLossFactorsT) {
		getOpportunityWinLossFactorsTs().add(opportunityWinLossFactorsT);
		opportunityWinLossFactorsT.setUserT(this);

		return opportunityWinLossFactorsT;
	}

	public OpportunityWinLossFactorsT removeOpportunityWinLossFactorsT(
			OpportunityWinLossFactorsT opportunityWinLossFactorsT) {
		getOpportunityWinLossFactorsTs().remove(opportunityWinLossFactorsT);
		opportunityWinLossFactorsT.setUserT(null);

		return opportunityWinLossFactorsT;
	}

	public List<PartnerMasterT> getPartnerMasterTs() {
		return this.partnerMasterTs;
	}

	public void setPartnerMasterTs(List<PartnerMasterT> partnerMasterTs) {
		this.partnerMasterTs = partnerMasterTs;
	}

	public PartnerMasterT addPartnerMasterT(PartnerMasterT partnerMasterT) {
		getPartnerMasterTs().add(partnerMasterT);
		partnerMasterT.setCreatedModifiedByUser(this);

		return partnerMasterT;
	}

	public PartnerMasterT removePartnerMasterT(PartnerMasterT partnerMasterT) {
		getPartnerMasterTs().remove(partnerMasterT);
		partnerMasterT.setCreatedModifiedByUser(null);

		return partnerMasterT;
	}

	public PushNotificationRegistrationT getPushNotificationRegistrationT() {
		return this.pushNotificationRegistrationT;
	}

	public void setPushNotificationRegistrationT(
			PushNotificationRegistrationT pushNotificationRegistrationT) {
		this.pushNotificationRegistrationT = pushNotificationRegistrationT;
	}

	public List<SearchKeywordsT> getSearchKeywordsTs() {
		return this.searchKeywordsTs;
	}

	public void setSearchKeywordsTs(List<SearchKeywordsT> searchKeywordsTs) {
		this.searchKeywordsTs = searchKeywordsTs;
	}

	public SearchKeywordsT addSearchKeywordsT(SearchKeywordsT searchKeywordsT) {
		getSearchKeywordsTs().add(searchKeywordsT);
		searchKeywordsT.setUserT(this);

		return searchKeywordsT;
	}

	public SearchKeywordsT removeSearchKeywordsT(SearchKeywordsT searchKeywordsT) {
		getSearchKeywordsTs().remove(searchKeywordsT);
		searchKeywordsT.setUserT(null);

		return searchKeywordsT;
	}

	public List<TaskBdmsTaggedLinkT> getTaskBdmsTaggedLinkTs() {
		return this.taskBdmsTaggedLinkTs;
	}

	public void setTaskBdmsTaggedLinkTs(
			List<TaskBdmsTaggedLinkT> taskBdmsTaggedLinkTs) {
		this.taskBdmsTaggedLinkTs = taskBdmsTaggedLinkTs;
	}

	public TaskBdmsTaggedLinkT addTaskBdmsTaggedLinkT(
			TaskBdmsTaggedLinkT taskBdmsTaggedLinkT) {
		getTaskBdmsTaggedLinkTs().add(taskBdmsTaggedLinkT);
		taskBdmsTaggedLinkT.setUserT(this);

		return taskBdmsTaggedLinkT;
	}

	public TaskBdmsTaggedLinkT removeTaskBdmsTaggedLinkT(
			TaskBdmsTaggedLinkT taskBdmsTaggedLinkT) {
		getTaskBdmsTaggedLinkTs().remove(taskBdmsTaggedLinkT);
		taskBdmsTaggedLinkT.setUserT(null);

		return taskBdmsTaggedLinkT;
	}

	public List<TaskBdmsTaggedLinkT> getTaskBdmsTaggedLinkTs2() {
		return this.taskBdmsTaggedLinkTs2;
	}

	public void setTaskBdmsTaggedLinkTs2(
			List<TaskBdmsTaggedLinkT> taskBdmsTaggedLinkTs2) {
		this.taskBdmsTaggedLinkTs2 = taskBdmsTaggedLinkTs2;
	}

	public TaskBdmsTaggedLinkT addTaskBdmsTaggedLinkTs2(
			TaskBdmsTaggedLinkT taskBdmsTaggedLinkTs2) {
		getTaskBdmsTaggedLinkTs2().add(taskBdmsTaggedLinkTs2);
		taskBdmsTaggedLinkTs2.setCreatedModifiedByUser(this);

		return taskBdmsTaggedLinkTs2;
	}

	public TaskBdmsTaggedLinkT removeTaskBdmsTaggedLinkTs2(
			TaskBdmsTaggedLinkT taskBdmsTaggedLinkTs2) {
		getTaskBdmsTaggedLinkTs2().remove(taskBdmsTaggedLinkTs2);
		taskBdmsTaggedLinkTs2.setCreatedModifiedByUser(null);

		return taskBdmsTaggedLinkTs2;
	}

	public List<TaskT> getTaskTs() {
		return this.taskTs;
	}

	public void setTaskTs(List<TaskT> taskTs) {
		this.taskTs = taskTs;
	}

	public TaskT addTaskT(TaskT taskT) {
		getTaskTs().add(taskT);
		taskT.setTaskOwnerT(this);

		return taskT;
	}

	public TaskT removeTaskT(TaskT taskT) {
		getTaskTs().remove(taskT);
		taskT.setTaskOwnerT(null);

		return taskT;
	}

	public List<TaskT> getTaskTs2() {
		return this.taskTs2;
	}

	public void setTaskTs2(List<TaskT> taskTs2) {
		this.taskTs2 = taskTs2;
	}

	public TaskT addTaskTs2(TaskT taskTs2) {
		getTaskTs2().add(taskTs2);
		taskTs2.setCreatedModifiedByUser(this);

		return taskTs2;
	}

	public TaskT removeTaskTs2(TaskT taskTs2) {
		getTaskTs2().remove(taskTs2);
		taskTs2.setCreatedModifiedByUser(null);

		return taskTs2;
	}

	public List<UserFavoritesT> getUserFavoritesTs() {
		return this.userFavoritesTs;
	}

	public void setUserFavoritesTs(List<UserFavoritesT> userFavoritesTs) {
		this.userFavoritesTs = userFavoritesTs;
	}

	public UserFavoritesT addUserFavoritesT(UserFavoritesT userFavoritesT) {
		getUserFavoritesTs().add(userFavoritesT);
		userFavoritesT.setUserT(this);

		return userFavoritesT;
	}

	public UserFavoritesT removeUserFavoritesT(UserFavoritesT userFavoritesT) {
		getUserFavoritesTs().remove(userFavoritesT);
		userFavoritesT.setUserT(null);

		return userFavoritesT;
	}

	public UserGeneralSettingsT getUserGeneralSettingsT() {
		return this.userGeneralSettingsT;
	}

	public void setUserGeneralSettingsT(
			UserGeneralSettingsT userGeneralSettingsT) {
		this.userGeneralSettingsT = userGeneralSettingsT;
	}

	public List<UserNotificationSettingsConditionsT> getUserNotificationSettingsConditionsTs() {
		return this.userNotificationSettingsConditionsTs;
	}

	public void setUserNotificationSettingsConditionsTs(
			List<UserNotificationSettingsConditionsT> userNotificationSettingsConditionsTs) {
		this.userNotificationSettingsConditionsTs = userNotificationSettingsConditionsTs;
	}

	public UserNotificationSettingsConditionsT addUserNotificationSettingsConditionsT(
			UserNotificationSettingsConditionsT userNotificationSettingsConditionsT) {
		getUserNotificationSettingsConditionsTs().add(
				userNotificationSettingsConditionsT);
		userNotificationSettingsConditionsT.setUserT(this);

		return userNotificationSettingsConditionsT;
	}

	public UserNotificationSettingsConditionsT removeUserNotificationSettingsConditionsT(
			UserNotificationSettingsConditionsT userNotificationSettingsConditionsT) {
		getUserNotificationSettingsConditionsTs().remove(
				userNotificationSettingsConditionsT);
		userNotificationSettingsConditionsT.setUserT(null);

		return userNotificationSettingsConditionsT;
	}

	public List<UserNotificationSettingsT> getUserNotificationSettingsTs() {
		return this.userNotificationSettingsTs;
	}

	public void setUserNotificationSettingsTs(
			List<UserNotificationSettingsT> userNotificationSettingsTs) {
		this.userNotificationSettingsTs = userNotificationSettingsTs;
	}

	public UserNotificationSettingsT addUserNotificationSettingsT(
			UserNotificationSettingsT userNotificationSettingsT) {
		getUserNotificationSettingsTs().add(userNotificationSettingsT);
		userNotificationSettingsT.setUserT(this);

		return userNotificationSettingsT;
	}

	public UserNotificationSettingsT removeUserNotificationSettingsT(
			UserNotificationSettingsT userNotificationSettingsT) {
		getUserNotificationSettingsTs().remove(userNotificationSettingsT);
		userNotificationSettingsT.setUserT(null);

		return userNotificationSettingsT;
	}

	public List<UserNotificationsT> getUserNotificationsTs1() {
		return this.userNotificationsTs1;
	}

	public void setUserNotificationsTs1(
			List<UserNotificationsT> userNotificationsTs1) {
		this.userNotificationsTs1 = userNotificationsTs1;
	}

	public UserNotificationsT addUserNotificationsTs1(
			UserNotificationsT userNotificationsTs1) {
		getUserNotificationsTs1().add(userNotificationsTs1);
		userNotificationsTs1.setUserT1(this);

		return userNotificationsTs1;
	}

	public UserNotificationsT removeUserNotificationsTs1(
			UserNotificationsT userNotificationsTs1) {
		getUserNotificationsTs1().remove(userNotificationsTs1);
		userNotificationsTs1.setUserT1(null);

		return userNotificationsTs1;
	}

	public List<UserNotificationsT> getUserNotificationsTs2() {
		return this.userNotificationsTs2;
	}

	public void setUserNotificationsTs2(
			List<UserNotificationsT> userNotificationsTs2) {
		this.userNotificationsTs2 = userNotificationsTs2;
	}

	public UserNotificationsT addUserNotificationsTs2(
			UserNotificationsT userNotificationsTs2) {
		getUserNotificationsTs2().add(userNotificationsTs2);
		userNotificationsTs2.setUserT2(this);

		return userNotificationsTs2;
	}

	public UserNotificationsT removeUserNotificationsTs2(
			UserNotificationsT userNotificationsTs2) {
		getUserNotificationsTs2().remove(userNotificationsTs2);
		userNotificationsTs2.setUserT2(null);

		return userNotificationsTs2;
	}

	public UserGroupMappingT getUserGroupMappingT() {
		return this.userGroupMappingT;
	}

	public void setUserGroupMappingT(UserGroupMappingT userGroupMappingT) {
		this.userGroupMappingT = userGroupMappingT;
	}

	public UserRoleMappingT getUserRoleMappingT() {
		return this.userRoleMappingT;
	}

	public void setUserRoleMappingT(UserRoleMappingT userRoleMappingT) {
		this.userRoleMappingT = userRoleMappingT;
	}

	public List<UserTaggedFollowedT> getUserTaggedFollowedTs1() {
		return this.userTaggedFollowedTs1;
	}

	public void setUserTaggedFollowedTs1(
			List<UserTaggedFollowedT> userTaggedFollowedTs1) {
		this.userTaggedFollowedTs1 = userTaggedFollowedTs1;
	}

	public UserTaggedFollowedT addUserTaggedFollowedTs1(
			UserTaggedFollowedT userTaggedFollowedTs1) {
		getUserTaggedFollowedTs1().add(userTaggedFollowedTs1);
		userTaggedFollowedTs1.setUserT1(this);

		return userTaggedFollowedTs1;
	}

	public UserTaggedFollowedT removeUserTaggedFollowedTs1(
			UserTaggedFollowedT userTaggedFollowedTs1) {
		getUserTaggedFollowedTs1().remove(userTaggedFollowedTs1);
		userTaggedFollowedTs1.setUserT1(null);

		return userTaggedFollowedTs1;
	}

	public List<UserTaggedFollowedT> getUserTaggedFollowedTs2() {
		return this.userTaggedFollowedTs2;
	}

	public void setUserTaggedFollowedTs2(
			List<UserTaggedFollowedT> userTaggedFollowedTs2) {
		this.userTaggedFollowedTs2 = userTaggedFollowedTs2;
	}

	public UserTaggedFollowedT addUserTaggedFollowedTs2(
			UserTaggedFollowedT userTaggedFollowedTs2) {
		getUserTaggedFollowedTs2().add(userTaggedFollowedTs2);
		userTaggedFollowedTs2.setUserT2(this);

		return userTaggedFollowedTs2;
	}

	public UserTaggedFollowedT removeUserTaggedFollowedTs2(
			UserTaggedFollowedT userTaggedFollowedTs2) {
		getUserTaggedFollowedTs2().remove(userTaggedFollowedTs2);
		userTaggedFollowedTs2.setUserT2(null);

		return userTaggedFollowedTs2;
	}

	public Timestamp getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Timestamp lastLogin) {
		this.lastLogin = lastLogin;
	}

	public List<FeedbackT> getFeedbackTs1() {
		return this.feedbackTs1;
	}

	public void setFeedbackTs1(List<FeedbackT> feedbackTs1) {
		this.feedbackTs1 = feedbackTs1;
	}
	
	public List<FeedbackT> getFeedbackTs2() {
		return feedbackTs2;
	}

	public void setFeedbackTs2(List<FeedbackT> feedbackTs2) {
		this.feedbackTs2 = feedbackTs2;
	}
	
	
	public FeedbackT addFeedbackT2(FeedbackT feedbackT) {
		getFeedbackTs2().add(feedbackT);
		feedbackT.setUserT(this);

		return feedbackT;
	}

	public FeedbackT removeFeedbackT2(FeedbackT feedbackT) {
		getFeedbackTs2().remove(feedbackT);
		feedbackT.setUserT(null);

		return feedbackT;
	}

	public FeedbackT addFeedbackT1(FeedbackT feedbackT) {
		getFeedbackTs1().add(feedbackT);
		feedbackT.setUserT(this);

		return feedbackT;
	}

	public FeedbackT removeFeedbackT1(FeedbackT feedbackT) {
		getFeedbackTs1().remove(feedbackT);
		feedbackT.setUserT(null);

		return feedbackT;
	}

	public GeographyMappingT getGeographyMappingT() {
		return this.geographyMappingT;
	}

	public void setGeographyMappingT(GeographyMappingT geographyMappingT) {
		this.geographyMappingT = geographyMappingT;
	}
}