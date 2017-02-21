package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
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
		this.userId = user.userId;
		this.userName = user.userName;
		this.tempPassword = user.tempPassword;
		this.active = user.active;
		this.userGroup = user.userGroup;
		this.supervisorUserId = user.supervisorUserId;
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

	@Column(name = "base_location")
	private String baseLocation;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "user_photo")
	private byte[] userPhoto;

	@Column(name = "user_telephone")
	private String userTelephone;

	@Column(name = "user_role")
	private String userRole;

	@Column(name = "active")
	private boolean active = true;

	@Column(name = "status")
    private int status; // added to mark whether password is default or notified or changed
	
	@Column(name = "user_mobile")
	private String userMobile;
	
	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	@Transient
	private Integer deliveryClusterId;
	
	@Transient
	private List<Integer> deliveryCentreId;

	@Transient
	private Timestamp lastLogin;

	@Transient
	private List<String> previledgedCustomerNameList;

	@OneToMany(mappedBy="userT")
	private List<UserAccessPrivilegesT> userAccessPrivilegesTs;

	@Transient
	private List<UserAccessPrivilegesT> deleteUserAccessPrivilegesTs;

	//bi-directional many-to-one association to UserModuleAccessT
	@OneToMany(mappedBy="userT", cascade = CascadeType.ALL)
	private List<UserModuleAccessT> userModuleAccessTs;

	@Transient
	private UserModuleAccess userModuleAccess;

	public UserModuleAccess getUserModuleAccess() {
		return userModuleAccess;
	}

	public void setUserModuleAccess(UserModuleAccess userModuleAccess) {
		this.userModuleAccess = userModuleAccess;
	}

	// bi-directional many-to-one association to BdmTargetT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<BdmTargetT> bdmTargetTs;

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

	// bi-directional many-to-one association to CollaborationCommentT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<CollaborationCommentT> collaborationCommentTs;

	// bi-directional many-to-one association to CommentsT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<CommentsT> commentsTs;

	// // bi-directional many-to-one association to ConnectCustomerContactLinkT
	// @JsonIgnore
	// @OneToMany(mappedBy = "createdModifiedByUser")
	// private List<ConnectCustomerContactLinkT> connectCustomerContactLinkTs;
	//
	// // bi-directional many-to-one association to ConnectOfferingLinkT
	// @JsonIgnore
	// @OneToMany(mappedBy = "createdModifiedByUser")
	// private List<ConnectOfferingLinkT> connectOfferingLinkTs;

	// bi-directional many-to-one association to ConnectSecondaryOwnerLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "secondaryOwnerUser")
	private List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerLinkTs;

	// // bi-directional many-to-one association to ConnectSecondaryOwnerLinkT
	// @JsonIgnore
	// @OneToMany(mappedBy = "createdModifiedByUser")
	// private List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerLinkTs2;

	// // bi-directional many-to-one association to ConnectSubSpLinkT
	// @JsonIgnore
	// @OneToMany(mappedBy = "createdModifiedByUser")
	// private List<ConnectSubSpLinkT> connectSubSpLinkTs;

	// bi-directional many-to-one association to ConnectT
	@JsonIgnore
	@OneToMany(mappedBy = "primaryOwnerUser")
	private List<ConnectT> connectTs;

	// bi-directional many-to-one association to ConnectT
	// @JsonIgnore
	// @OneToMany(mappedBy = "createdModifiedByUser")
	// private List<ConnectT> connectTs2;
	//
	// // bi-directional many-to-one association to
	// ConnectTcsAccountContactLinkT
	// @JsonIgnore
	// @OneToMany(mappedBy = "createdModifiedByUser")
	// private List<ConnectTcsAccountContactLinkT>
	// connectTcsAccountContactLinkTs;

	// bi-directional many-to-one association to CustomerMasterT
	@OneToMany(mappedBy = "createdModifiedByUser")
	private List<CustomerMasterT> customerMasterTs;

	// bi-directional many-to-one association to DocumentRepositoryT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<DocumentRepositoryT> documentRepositoryTs;

	// bi-directional many-to-one association to FeedbackT

	// bi-directional many-to-one association to
	// FrequentlySearchedCustomerPartnerT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<FrequentlySearchedCustomerPartnerT> frequentlySearchedCustomerPartnerTs;

	//bi-directional many-to-one association to GoalGroupMappingT
	@OneToMany(mappedBy="userT")
	private List<GoalGroupMappingT> goalGroupMappingTs;

	//bi-directional many-to-one association to GoalMappingT
	@OneToMany(mappedBy="userT")
	private List<GoalMappingT> goalMappingTs;

	// bi-directional many-to-one association to LoginHistoryT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<LoginHistoryT> loginHistoryTs;

	// bi-directional many-to-one association to NotesT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<NotesT> notesTs;

	// bi-directional many-to-one association to OpportunitySalesSupportLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "salesSupportOwnerUser")
	private List<OpportunitySalesSupportLinkT> opportunitySalesSupportLinkTs;

	// bi-directional many-to-one association to OpportunityT
	@JsonIgnore
	@OneToMany(mappedBy = "primaryOwnerUser")
	private List<OpportunityT> opportunityTs;

	// bi-directional many-to-one association to OpportunityTimelineHistoryT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<OpportunityTimelineHistoryT> opportunityTimelineHistoryTs;


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

	// bi-directional many-to-one association to TaskT
	@JsonIgnore
	@OneToMany(mappedBy = "taskOwnerT")
	private List<TaskT> taskTs;

	// bi-directional many-to-one association to TaskT
	@JsonIgnore
	@OneToMany(mappedBy = "createdByUser")
	private List<TaskT> createdTaskTs;

	// bi-directional many-to-one association to TaskT
	@JsonIgnore
	@OneToMany(mappedBy = "modifiedByUser")
	private List<TaskT> modifiedTaskTs;

	// bi-directional many-to-one association to UserFavoritesT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<UserFavoritesT> userFavoritesTs;

	// bi-directional one-to-one association to UserGeneralSettingsT
	@OneToOne(mappedBy = "userT")
	private UserGeneralSettingsT userGeneralSettingsT;

	//bi-directional many-to-one association to UserGoalsT
	@OneToMany(mappedBy="userT1")
	private List<UserGoalsT> userGoalsTs1;

	//bi-directional many-to-one association to UserGoalsT
	@OneToMany(mappedBy="userT2")
	private List<UserGoalsT> userGoalsTs2;

	// bi-directional many-to-one association to
	// UserNotificationSettingsConditionsT
	@OneToMany(mappedBy = "userT")
	private List<UserNotificationSettingsConditionsT> userNotificationSettingsConditionsTs;

	// bi-directional many-to-one association to UserNotificationSettingsT
	@OneToMany(mappedBy = "userT")
	private List<UserNotificationSettingsT> userNotificationSettingsTs;

	// bi-directional many-to-one association to UserNotificationsT
	@OneToMany(mappedBy = "recipientUser")
	private List<UserNotificationsT> userNotificationsTs1;

	// bi-directional many-to-one association to UserNotificationsT
	@OneToMany(mappedBy = "userT")
	private List<UserNotificationsT> userNotificationsTs2;

	// bi-directional many-to-one association to UserGroupMappingT
	//	@JsonIgnore
	@ManyToOne(optional=false)
	@JoinColumn(name = "user_group", insertable = false, updatable = false)
	private UserGroupMappingT userGroupMappingT;

	@Column(name="user_group")
	private String userGroup;

	// bi-directional many-to-one association to UserRoleMappingT
	// @JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_role", insertable = false, updatable = false)
	private UserRoleMappingT userRoleMappingT;

	// bi-directional many-to-one association to UserTaggedFollowedT
	@OneToMany(mappedBy = "userT1")
	private List<UserTaggedFollowedT> userTaggedFollowedTs1;

	// bi-directional many-to-one association to UserTaggedFollowedT
	@OneToMany(mappedBy = "userT2")
	private List<UserTaggedFollowedT> userTaggedFollowedTs2;


	//bi-directional many-to-one association to RequestedCustomerT
	@OneToMany(mappedBy="createdByUser")
	private List<WorkflowCustomerT> workflowCustomerTs1;

	//bi-directional many-to-one association to RequestedCustomerT
	@OneToMany(mappedBy="modifiedByUser")
	private List<WorkflowCustomerT> workflowCustomerTs2;

	//bi-directional many-to-one association to RequestedPartnerT
	@OneToMany(mappedBy="createdByUser")
	private List<WorkflowPartnerT> workflowPartnerTs1;

	//bi-directional many-to-one association to RequestedPartnerT
	@OneToMany(mappedBy="modifiedByUser")
	private List<WorkflowPartnerT> workflowPartnerTs2;


	//bi-directional many-to-one association to WorkflowRequestT
	@OneToMany(mappedBy="createdByUser")
	private List<WorkflowRequestT> workflowRequestTs1;

	//bi-directional many-to-one association to WorkflowRequestT
	@OneToMany(mappedBy="modifiedByUser")
	private List<WorkflowRequestT> workflowRequestTs2;

	//bi-directional many-to-one association to WorkflowStepT
	@OneToMany(mappedBy="user")
	private List<WorkflowStepT> workflowStepTs1;

	//bi-directional many-to-one association to WorkflowProcessTemplate
	@OneToMany(mappedBy="userT")
	private List<WorkflowProcessTemplate> workflowProcessTemplates;

	//bi-directional many-to-one association to WorkflowCompetitorT
	@OneToMany(mappedBy="createdByUser")
	private List<WorkflowCompetitorT> workflowCompetitorTs1;

	//bi-directional many-to-one association to WorkflowCompetitorT
	@OneToMany(mappedBy="modifiedByUser")
	private List<WorkflowCompetitorT> workflowCompetitorTs2;

	//bi-directional many-to-one association to WorkflowStepT
	@OneToMany(mappedBy="modifiedByUser")
	private List<WorkflowStepT> workflowStepTs3;
	
	//bi-directional many-to-one association to UserSubscription
	@OneToMany(mappedBy="userT")
	private List<UserSubscriptions> userSubscriptions;

	@Transient
	private String newPassword;

	//added for partner changes
	//bi-directional many-to-one association to PartnerSubSpMappingT
	@OneToMany(mappedBy="createdByUser")
	private List<PartnerSubSpMappingT> partnerSubSpMappingTs1;

	//bi-directional many-to-one association to PartnerSubSpMappingT
	@OneToMany(mappedBy="modifiedByUser")
	private List<PartnerSubSpMappingT> partnerSubSpMappingTs2;

	//bi-directional many-to-one association to PartnerSubspProductMappingT
	@OneToMany(mappedBy="createdByUser")
	private List<PartnerSubspProductMappingT> partnerSubspProductMappingTs1;

	//bi-directional many-to-one association to PartnerSubspProductMappingT
	@OneToMany(mappedBy="modifiedByUser")
	private List<PartnerSubspProductMappingT> partnerSubspProductMappingTs2;

	//bi-directional many-to-one association to ProductContactLinkT
	@OneToMany(mappedBy="createdByUser")
	private List<ProductContactLinkT> productContactLinkTs1;

	//bi-directional many-to-one association to ProductContactLinkT
	@OneToMany(mappedBy="modifiedByUser")
	private List<ProductContactLinkT> productContactLinkTs2;
	
	//bi-directional many-to-one association to ProductMasterT
	@OneToMany(mappedBy="createdByUser")
	private List<ProductMasterT> productMasterTs1;

	//bi-directional many-to-one association to ProductMasterT
	@OneToMany(mappedBy="modifiedByUser")
	private List<ProductMasterT> productMasterTs2;

	//bi-directional many-to-one association to PartnerContactLinkT
	@OneToMany(mappedBy="createdByUser")
	private List<PartnerContactLinkT> partnerContactLinkTs1;
		
	@OneToMany(mappedBy="modifiedByUser")
	private List<PartnerContactLinkT> partnerContactLinkTs2;
	
	//bi-directional many-to-one association to PartnerContactLinkT
	@OneToMany(mappedBy="createdByUser")
	private List<PartnerMasterT> partnerTs1;
			
	@OneToMany(mappedBy="modifiedByUser")
	private List<PartnerMasterT> partnerTs2;
		
	//bi-directional many-to-one association to PartnerContactLinkT
	@OneToMany(mappedBy="createdByUser")
	private List<ContactT> contactTs1;
			
	@OneToMany(mappedBy="modifiedByUser")
	private List<ContactT> contactTs2;
	
	/* Added for BFM changes */
	//bi-directional many-to-one association to WorkflowBfmT
	@OneToMany(mappedBy="createdByUser")
	private List<WorkflowBfmT> workflowBfmTs1;

	//bi-directional many-to-one association to WorkflowBfmT
	@OneToMany(mappedBy="modifiedByUser")
	private List<WorkflowBfmT> workflowBfmTs2;
	
	// bi-directional many-to-one association to DocumentsT
	@OneToMany(mappedBy = "createdByUser")
	private List<DocumentsT> documentsTs1;

	// bi-directional many-to-one association to DocumentsT
	@OneToMany(mappedBy = "modifiedByUser")
	private List<DocumentsT> documentsTs2;
	
	//bi-directional many-to-one association to PartnerContactLinkT
	@OneToMany(mappedBy="deliveryCentreHeadUser")
	private List<DeliveryCentreT> deliveryCentreTs;
	
	//bi-directional many-to-one association to PartnerContactLinkT
	@OneToMany(mappedBy="deliveryClusterHeadUser")
	private List<DeliveryClusterT> deliveryClusterTs;
	
	@OneToMany(mappedBy="userT")
	private List<DeliveryPmoT> deliveryCentresPmo;

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

	// public List<ConnectCustomerContactLinkT>
	// getConnectCustomerContactLinkTs() {
	// return this.connectCustomerContactLinkTs;
	// }
	//
	// public void setConnectCustomerContactLinkTs(
	// List<ConnectCustomerContactLinkT> connectCustomerContactLinkTs) {
	// this.connectCustomerContactLinkTs = connectCustomerContactLinkTs;
	// }
	//
	// public ConnectCustomerContactLinkT addConnectCustomerContactLinkT(
	// ConnectCustomerContactLinkT connectCustomerContactLinkT) {
	// getConnectCustomerContactLinkTs().add(connectCustomerContactLinkT);
	// connectCustomerContactLinkT.setCreatedModifiedByUser(this);
	//
	// return connectCustomerContactLinkT;
	// }

	// public ConnectCustomerContactLinkT removeConnectCustomerContactLinkT(
	// ConnectCustomerContactLinkT connectCustomerContactLinkT) {
	// getConnectCustomerContactLinkTs().remove(connectCustomerContactLinkT);
	// connectCustomerContactLinkT.setCreatedModifiedByUser(null);
	//
	// return connectCustomerContactLinkT;
	// }

	// public List<ConnectOfferingLinkT> getConnectOfferingLinkTs() {
	// return this.connectOfferingLinkTs;
	// }
	//
	// public void setConnectOfferingLinkTs(
	// List<ConnectOfferingLinkT> connectOfferingLinkTs) {
	// this.connectOfferingLinkTs = connectOfferingLinkTs;
	// }

	// public ConnectOfferingLinkT addConnectOfferingLinkT(
	// ConnectOfferingLinkT connectOfferingLinkT) {
	// getConnectOfferingLinkTs().add(connectOfferingLinkT);
	// connectOfferingLinkT.setCreatedModifiedByUser(this);
	//
	// return connectOfferingLinkT;
	// }

	// public ConnectOfferingLinkT removeConnectOfferingLinkT(
	// ConnectOfferingLinkT connectOfferingLinkT) {
	// getConnectOfferingLinkTs().remove(connectOfferingLinkT);
	// connectOfferingLinkT.setCreatedModifiedByUser(null);
	//
	// return connectOfferingLinkT;
	// }

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
		connectSecondaryOwnerLinkT.setSecondaryOwnerUser(this);

		return connectSecondaryOwnerLinkT;
	}

	public ConnectSecondaryOwnerLinkT removeConnectSecondaryOwnerLinkT(
			ConnectSecondaryOwnerLinkT connectSecondaryOwnerLinkT) {
		getConnectSecondaryOwnerLinkTs().remove(connectSecondaryOwnerLinkT);
		connectSecondaryOwnerLinkT.setSecondaryOwnerUser(null);

		return connectSecondaryOwnerLinkT;
	}

	// public List<ConnectSecondaryOwnerLinkT> getConnectSecondaryOwnerLinkTs2()
	// {
	// return this.connectSecondaryOwnerLinkTs2;
	// }
	//
	// public void setConnectSecondaryOwnerLinkTs2(
	// List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerLinkTs2) {
	// this.connectSecondaryOwnerLinkTs2 = connectSecondaryOwnerLinkTs2;
	// }
	//
	// public ConnectSecondaryOwnerLinkT addConnectSecondaryOwnerLinkTs2(
	// ConnectSecondaryOwnerLinkT connectSecondaryOwnerLinkTs2) {
	// getConnectSecondaryOwnerLinkTs2().add(connectSecondaryOwnerLinkTs2);
	// connectSecondaryOwnerLinkTs2.setCreatedModifiedByUser(this);
	//
	// return connectSecondaryOwnerLinkTs2;
	// }
	//
	// public ConnectSecondaryOwnerLinkT removeConnectSecondaryOwnerLinkTs2(
	// ConnectSecondaryOwnerLinkT connectSecondaryOwnerLinkTs2) {
	// getConnectSecondaryOwnerLinkTs2().remove(connectSecondaryOwnerLinkTs2);
	// connectSecondaryOwnerLinkTs2.setCreatedModifiedByUser(null);
	//
	// return connectSecondaryOwnerLinkTs2;
	// }
	//
	// public List<ConnectSubSpLinkT> getConnectSubSpLinkTs() {
	// return this.connectSubSpLinkTs;
	// }
	//
	// public void setConnectSubSpLinkTs(List<ConnectSubSpLinkT>
	// connectSubSpLinkTs) {
	// this.connectSubSpLinkTs = connectSubSpLinkTs;
	// }
	//
	// public ConnectSubSpLinkT addConnectSubSpLinkT(
	// ConnectSubSpLinkT connectSubSpLinkT) {
	// getConnectSubSpLinkTs().add(connectSubSpLinkT);
	// connectSubSpLinkT.setCreatedModifiedByUser(this);
	//
	// return connectSubSpLinkT;
	// }
	//
	// public ConnectSubSpLinkT removeConnectSubSpLinkT(
	// ConnectSubSpLinkT connectSubSpLinkT) {
	// getConnectSubSpLinkTs().remove(connectSubSpLinkT);
	// connectSubSpLinkT.setCreatedModifiedByUser(null);
	//
	// return connectSubSpLinkT;
	// }
	//
	public List<ConnectT> getConnectTs() {
		return this.connectTs;
	}

	public void setConnectTs(List<ConnectT> connectTs) {
		this.connectTs = connectTs;
	}

	public ConnectT addConnectT(ConnectT connectT) {
		getConnectTs().add(connectT);
		connectT.setPrimaryOwnerUser(this);

		return connectT;
	}

	public ConnectT removeConnectT(ConnectT connectT) {
		getConnectTs().remove(connectT);
		connectT.setPrimaryOwnerUser(null);

		return connectT;
	}

	// public List<ConnectT> getConnectTs2() {
	// return this.connectTs2;
	// }
	//
	// public void setConnectTs2(List<ConnectT> connectTs2) {
	// this.connectTs2 = connectTs2;
	// }
	//
	// public ConnectT addConnectTs2(ConnectT connectTs2) {
	// getConnectTs2().add(connectTs2);
	// connectTs2.setCreatedModifiedByUser(this);
	//
	// return connectTs2;
	// }
	//
	// public ConnectT removeConnectTs2(ConnectT connectTs2) {
	// getConnectTs2().remove(connectTs2);
	// connectTs2.setCreatedModifiedByUser(null);
	//
	// return connectTs2;
	// }
	//
	// public List<ConnectTcsAccountContactLinkT>
	// getConnectTcsAccountContactLinkTs() {
	// return this.connectTcsAccountContactLinkTs;
	// }
	//
	// public void setConnectTcsAccountContactLinkTs(
	// List<ConnectTcsAccountContactLinkT> connectTcsAccountContactLinkTs) {
	// this.connectTcsAccountContactLinkTs = connectTcsAccountContactLinkTs;
	// }
	//
	// public ConnectTcsAccountContactLinkT addConnectTcsAccountContactLinkT(
	// ConnectTcsAccountContactLinkT connectTcsAccountContactLinkT) {
	// getConnectTcsAccountContactLinkTs().add(connectTcsAccountContactLinkT);
	// connectTcsAccountContactLinkT.setCreatedModifiedByUser(this);
	//
	// return connectTcsAccountContactLinkT;
	// }
	//
	// public ConnectTcsAccountContactLinkT removeConnectTcsAccountContactLinkT(
	// ConnectTcsAccountContactLinkT connectTcsAccountContactLinkT) {
	// getConnectTcsAccountContactLinkTs().remove(
	// connectTcsAccountContactLinkT);
	// connectTcsAccountContactLinkT.setCreatedModifiedByUser(null);
	//
	// return connectTcsAccountContactLinkT;
	// }

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

	public List<TaskT> getCreatedTaskTs() {
		return createdTaskTs;
	}

	public void setCreatedTaskTs(List<TaskT> createdTaskTs) {
		this.createdTaskTs = createdTaskTs;
	}

	public List<TaskT> getModifiedTaskTs() {
		return modifiedTaskTs;
	}

	public void setModifiedTaskTs(List<TaskT> modifiedTaskTs) {
		this.modifiedTaskTs = modifiedTaskTs;
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
		userNotificationsTs1.setRecipientUser(this);

		return userNotificationsTs1;
	}

	public UserNotificationsT removeUserNotificationsTs1(
			UserNotificationsT userNotificationsTs1) {
		getUserNotificationsTs1().remove(userNotificationsTs1);
		userNotificationsTs1.setRecipientUser(null);

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
		userNotificationsTs2.setUserT(this);

		return userNotificationsTs2;
	}

	public UserNotificationsT removeUserNotificationsTs2(
			UserNotificationsT userNotificationsTs2) {
		getUserNotificationsTs2().remove(userNotificationsTs2);
		userNotificationsTs2.setUserT(null);

		return userNotificationsTs2;
	}

	public UserGroupMappingT getUserGroupMappingT() {
		return this.userGroupMappingT;
	}

	public void setUserGroupMappingT(UserGroupMappingT userGroupMappingT) {
		this.userGroupMappingT = userGroupMappingT;
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
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

	public String getBaseLocation() {
		return baseLocation;
	}

	public void setBaseLocation(String baseLocation) {
		this.baseLocation = baseLocation;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public List<UserAccessPrivilegesT> getUserAccessPrivilegesTs() {
		return userAccessPrivilegesTs;
	}

	public void setUserAccessPrivilegesTs(
			List<UserAccessPrivilegesT> userAccessPrivilegesTs) {
		this.userAccessPrivilegesTs = userAccessPrivilegesTs;
	}

	public UserAccessPrivilegesT addUserAccessPrivilegesT(UserAccessPrivilegesT userAccessPrivilegesT) {
		getUserAccessPrivilegesTs().add(userAccessPrivilegesT);
		userAccessPrivilegesT.setUserT(this);
		return userAccessPrivilegesT;
	}

	public UserAccessPrivilegesT removeUserAccessPrivilegesT(UserAccessPrivilegesT userAccessPrivilegesT) {
		getUserAccessPrivilegesTs().remove(userAccessPrivilegesT);
		userAccessPrivilegesT.setUserT(null);
		return userAccessPrivilegesT;
	}

	public List<String> getPreviledgedCustomerNameList() {
		return previledgedCustomerNameList;
	}

	public void setPreviledgedCustomerNameList(
			List<String> previledgedCustomerNameList) {
		this.previledgedCustomerNameList = previledgedCustomerNameList;
	}

	public List<GoalGroupMappingT> getGoalGroupMappingTs() {
		return goalGroupMappingTs;
	}

	public void setGoalGroupMappingTs(List<GoalGroupMappingT> goalGroupMappingTs) {
		this.goalGroupMappingTs = goalGroupMappingTs;
	}

	public List<GoalMappingT> getGoalMappingTs() {
		return goalMappingTs;
	}

	public void setGoalMappingTs(List<GoalMappingT> goalMappingTs) {
		this.goalMappingTs = goalMappingTs;
	}

	public List<UserGoalsT> getUserGoalsTs1() {
		return userGoalsTs1;
	}

	public void setUserGoalsTs1(List<UserGoalsT> userGoalsTs1) {
		this.userGoalsTs1 = userGoalsTs1;
	}

	public List<UserGoalsT> getUserGoalsTs2() {
		return userGoalsTs2;
	}

	public void setUserGoalsTs2(List<UserGoalsT> userGoalsTs2) {
		this.userGoalsTs2 = userGoalsTs2;
	}



	public WorkflowCustomerT addWorkflowCustomerTs1(WorkflowCustomerT workflowCustomerTs1) {
		getWorkflowCustomerTs1().add(workflowCustomerTs1);
		workflowCustomerTs1.setCreatedByUser(this);

		return workflowCustomerTs1;
	}

	public WorkflowCustomerT removeRequestedCustomerTs1(WorkflowCustomerT workflowCustomerTs1) {
		getWorkflowCustomerTs1().remove(workflowCustomerTs1);
		workflowCustomerTs1.setCreatedByUser(null);

		return workflowCustomerTs1;
	}



	public WorkflowCustomerT addWorkflowCustomerTs2(WorkflowCustomerT workflowCustomerTs2) {
		getWorkflowCustomerTs2().add(workflowCustomerTs2);
		workflowCustomerTs2.setModifiedByUser(this);

		return workflowCustomerTs2;
	}

	public WorkflowCustomerT removeRequestedCustomerTs2(WorkflowCustomerT workflowCustomerTs2) {
		getWorkflowCustomerTs2().remove(workflowCustomerTs2);
		workflowCustomerTs2.setModifiedByUser(null);

		return workflowCustomerTs2;
	}



	public WorkflowPartnerT addWorkflowPartnerTs1(WorkflowPartnerT workflowPartnerTs1) {
		getWorkflowPartnerTs1().add(workflowPartnerTs1);
		workflowPartnerTs1.setCreatedByUser(this);

		return workflowPartnerTs1;
	}

	public WorkflowPartnerT removeWorkflowPartnerTs1(WorkflowPartnerT workflowPartnerTs1) {
		getWorkflowPartnerTs1().remove(workflowPartnerTs1);
		workflowPartnerTs1.setCreatedByUser(null);

		return workflowPartnerTs1;
	}



	public WorkflowPartnerT addWorkflowPartnerTs2(WorkflowPartnerT workflowPartnerTs2) {
		getWorkflowPartnerTs2().add(workflowPartnerTs2);
		workflowPartnerTs2.setModifiedByUser(this);

		return workflowPartnerTs2;
	}

	public WorkflowPartnerT removeWorkflowPartnerTs2(WorkflowPartnerT workflowPartnerTs2) {
		getWorkflowPartnerTs2().remove(workflowPartnerTs2);
		workflowPartnerTs2.setModifiedByUser(null);

		return workflowPartnerTs2;
	}

	public List<WorkflowRequestT> getWorkflowRequestTs1() {
		return this.workflowRequestTs1;
	}

	public void setWorkflowRequestTs1(List<WorkflowRequestT> workflowRequestTs1) {
		this.workflowRequestTs1 = workflowRequestTs1;
	}

	public WorkflowRequestT addWorkflowRequestTs1(WorkflowRequestT workflowRequestTs1) {
		getWorkflowRequestTs1().add(workflowRequestTs1);
		workflowRequestTs1.setCreatedByUser(this);

		return workflowRequestTs1;
	}

	public WorkflowRequestT removeWorkflowRequestTs1(WorkflowRequestT workflowRequestTs1) {
		getWorkflowRequestTs1().remove(workflowRequestTs1);
		workflowRequestTs1.setCreatedByUser(null);

		return workflowRequestTs1;
	}

	public List<WorkflowRequestT> getWorkflowRequestTs2() {
		return this.workflowRequestTs2;
	}

	public void setWorkflowRequestTs2(List<WorkflowRequestT> workflowRequestTs2) {
		this.workflowRequestTs2 = workflowRequestTs2;
	}

	public WorkflowRequestT addWorkflowRequestTs2(WorkflowRequestT workflowRequestTs2) {
		getWorkflowRequestTs2().add(workflowRequestTs2);
		workflowRequestTs2.setModifiedByUser(this);

		return workflowRequestTs2;
	}

	public WorkflowRequestT removeWorkflowRequestTs2(WorkflowRequestT workflowRequestTs2) {
		getWorkflowRequestTs2().remove(workflowRequestTs2);
		workflowRequestTs2.setModifiedByUser(null);

		return workflowRequestTs2;
	}

	public List<WorkflowStepT> getWorkflowStepTs1() {
		return this.workflowStepTs1;
	}

	public void setWorkflowStepTs1(List<WorkflowStepT> workflowStepTs1) {
		this.workflowStepTs1 = workflowStepTs1;
	}

	public WorkflowStepT addWorkflowStepTs1(WorkflowStepT workflowStepTs1) {
		getWorkflowStepTs1().add(workflowStepTs1);
		workflowStepTs1.setUser(this);

		return workflowStepTs1;
	}

	public WorkflowStepT removeWorkflowStepTs1(WorkflowStepT workflowStepTs1) {
		getWorkflowStepTs1().remove(workflowStepTs1);
		workflowStepTs1.setUser(null);

		return workflowStepTs1;
	}

	public List<WorkflowStepT> getWorkflowStepTs3() {
		return this.workflowStepTs3;
	}

	public void setWorkflowStepTs3(List<WorkflowStepT> workflowStepTs3) {
		this.workflowStepTs3 = workflowStepTs3;
	}

	public WorkflowStepT addWorkflowStepTs3(WorkflowStepT workflowStepTs3) {
		getWorkflowStepTs3().add(workflowStepTs3);
		workflowStepTs3.setModifiedByUser(this);

		return workflowStepTs3;
	}

	public WorkflowStepT removeWorkflowStepTs3(WorkflowStepT workflowStepTs3) {
		getWorkflowStepTs3().remove(workflowStepTs3);
		workflowStepTs3.setModifiedByUser(null);

		return workflowStepTs3;
	}

	public List<WorkflowCustomerT> getWorkflowCustomerTs1() {
		return workflowCustomerTs1;
	}

	public void setWorkflowCustomerTs1(List<WorkflowCustomerT> workflowCustomerTs1) {
		this.workflowCustomerTs1 = workflowCustomerTs1;
	}

	public List<WorkflowCustomerT> getWorkflowCustomerTs2() {
		return workflowCustomerTs2;
	}

	public void setWorkflowCustomerTs2(List<WorkflowCustomerT> workflowCustomerTs2) {
		this.workflowCustomerTs2 = workflowCustomerTs2;
	}

	public List<WorkflowPartnerT> getWorkflowPartnerTs1() {
		return workflowPartnerTs1;
	}

	public void setWorkflowPartnerTs1(List<WorkflowPartnerT> workflowPartnerTs1) {
		this.workflowPartnerTs1 = workflowPartnerTs1;
	}

	public List<WorkflowPartnerT> getWorkflowPartnerTs2() {
		return workflowPartnerTs2;
	}

	public void setWorkflowPartnerTs2(List<WorkflowPartnerT> workflowPartnerTs2) {
		this.workflowPartnerTs2 = workflowPartnerTs2;
	}

	public List<WorkflowProcessTemplate> getWorkflowProcessTemplates() {
		return this.workflowProcessTemplates;
	}

	public void setWorkflowProcessTemplates(List<WorkflowProcessTemplate> workflowProcessTemplates) {
		this.workflowProcessTemplates = workflowProcessTemplates;
	}

	public WorkflowProcessTemplate addWorkflowProcessTemplate(WorkflowProcessTemplate workflowProcessTemplate) {
		getWorkflowProcessTemplates().add(workflowProcessTemplate);
		workflowProcessTemplate.setUserT(this);

		return workflowProcessTemplate;
	}

	public WorkflowProcessTemplate removeWorkflowProcessTemplate(WorkflowProcessTemplate workflowProcessTemplate) {
		getWorkflowProcessTemplates().remove(workflowProcessTemplate);
		workflowProcessTemplate.setUserT(null);

		return workflowProcessTemplate;
	}

	public List<UserModuleAccessT> getUserModuleAccessTs() {
		return this.userModuleAccessTs;
	}

	public void setUserModuleAccessTs(List<UserModuleAccessT> userModuleAccessTs) {
		this.userModuleAccessTs = userModuleAccessTs;
	}

	public UserModuleAccessT addUserModuleAccessT(UserModuleAccessT userModuleAccessT) {
		getUserModuleAccessTs().add(userModuleAccessT);
		userModuleAccessT.setUserT(this);

		return userModuleAccessT;
	}

	public UserModuleAccessT removeUserModuleAccessT(UserModuleAccessT userModuleAccessT) {
		getUserModuleAccessTs().remove(userModuleAccessT);
		userModuleAccessT.setUserT(null);

		return userModuleAccessT;
	}

	public List<UserAccessPrivilegesT> getDeleteUserAccessPrivilegesTs() {
		return deleteUserAccessPrivilegesTs;
	}

	public void setDeleteUserAccessPrivilegesTs(
			List<UserAccessPrivilegesT> deleteUserAccessPrivilegesTs) {
		this.deleteUserAccessPrivilegesTs = deleteUserAccessPrivilegesTs;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<WorkflowCompetitorT> getWorkflowCompetitorTs1() {
		return this.workflowCompetitorTs1;
	}

	public void setWorkflowCompetitorTs1(List<WorkflowCompetitorT> workflowCompetitorTs1) {
		this.workflowCompetitorTs1 = workflowCompetitorTs1;
	}

	public WorkflowCompetitorT addWorkflowCompetitorTs1(WorkflowCompetitorT workflowCompetitorTs1) {
		getWorkflowCompetitorTs1().add(workflowCompetitorTs1);
		workflowCompetitorTs1.setCreatedByUser(this);

		return workflowCompetitorTs1;
	}

	public WorkflowCompetitorT removeWorkflowCompetitorTs1(WorkflowCompetitorT workflowCompetitorTs1) {
		getWorkflowCompetitorTs1().remove(workflowCompetitorTs1);
		workflowCompetitorTs1.setCreatedByUser(null);

		return workflowCompetitorTs1;
	}

	public List<WorkflowCompetitorT> getWorkflowCompetitorTs2() {
		return this.workflowCompetitorTs2;
	}

	public void setWorkflowCompetitorTs2(List<WorkflowCompetitorT> workflowCompetitorTs2) {
		this.workflowCompetitorTs2 = workflowCompetitorTs2;
	}

	public WorkflowCompetitorT addWorkflowCompetitorTs2(WorkflowCompetitorT workflowCompetitorTs2) {
		getWorkflowCompetitorTs2().add(workflowCompetitorTs2);
		workflowCompetitorTs2.setModifiedByUser(this);

		return workflowCompetitorTs2;
	}

	public WorkflowCompetitorT removeWorkflowCompetitorTs2(WorkflowCompetitorT workflowCompetitorTs2) {
		getWorkflowCompetitorTs2().remove(workflowCompetitorTs2);
		workflowCompetitorTs2.setModifiedByUser(null);

		return workflowCompetitorTs2;
	}

	public List<UserSubscriptions> getUserSubscriptions() {
		return userSubscriptions;
	}

	public void setUserSubscriptions(List<UserSubscriptions> userSubscriptions) {
		this.userSubscriptions = userSubscriptions;
	}

	public List<PartnerSubSpMappingT> getPartnerSubSpMappingTs1() {
		return partnerSubSpMappingTs1;
	}

	public void setPartnerSubSpMappingTs1(
			List<PartnerSubSpMappingT> partnerSubSpMappingTs1) {
		this.partnerSubSpMappingTs1 = partnerSubSpMappingTs1;
	}

	public List<PartnerSubSpMappingT> getPartnerSubSpMappingTs2() {
		return partnerSubSpMappingTs2;
	}

	public void setPartnerSubSpMappingTs2(
			List<PartnerSubSpMappingT> partnerSubSpMappingTs2) {
		this.partnerSubSpMappingTs2 = partnerSubSpMappingTs2;
	}

	public List<PartnerSubspProductMappingT> getPartnerSubspProductMappingTs1() {
		return partnerSubspProductMappingTs1;
	}

	public void setPartnerSubspProductMappingTs1(
			List<PartnerSubspProductMappingT> partnerSubspProductMappingTs1) {
		this.partnerSubspProductMappingTs1 = partnerSubspProductMappingTs1;
	}

	public List<PartnerSubspProductMappingT> getPartnerSubspProductMappingTs2() {
		return partnerSubspProductMappingTs2;
	}

	public void setPartnerSubspProductMappingTs2(
			List<PartnerSubspProductMappingT> partnerSubspProductMappingTs2) {
		this.partnerSubspProductMappingTs2 = partnerSubspProductMappingTs2;
	}

	public List<ProductContactLinkT> getProductContactLinkTs1() {
		return productContactLinkTs1;
	}

	public void setProductContactLinkTs1(
			List<ProductContactLinkT> productContactLinkTs1) {
		this.productContactLinkTs1 = productContactLinkTs1;
	}

	public List<ProductContactLinkT> getProductContactLinkTs2() {
		return productContactLinkTs2;
	}

	public void setProductContactLinkTs2(
			List<ProductContactLinkT> productContactLinkTs2) {
		this.productContactLinkTs2 = productContactLinkTs2;
	}

	public List<ProductMasterT> getProductMasterTs1() {
		return productMasterTs1;
	}

	public void setProductMasterTs1(List<ProductMasterT> productMasterTs1) {
		this.productMasterTs1 = productMasterTs1;
	}

	public List<ProductMasterT> getProductMasterTs2() {
		return productMasterTs2;
	}

	public void setProductMasterTs2(List<ProductMasterT> productMasterTs2) {
		this.productMasterTs2 = productMasterTs2;
	}

	public List<PartnerContactLinkT> getPartnerContactLinkTs1() {
		return partnerContactLinkTs1;
	}

	public void setPartnerContactLinkTs1(
			List<PartnerContactLinkT> partnerContactLinkTs1) {
		this.partnerContactLinkTs1 = partnerContactLinkTs1;
	}

	public List<PartnerContactLinkT> getPartnerContactLinkTs2() {
		return partnerContactLinkTs2;
	}

	public void setPartnerContactLinkTs2(
			List<PartnerContactLinkT> partnerContactLinkTs2) {
		this.partnerContactLinkTs2 = partnerContactLinkTs2;
	}

	public List<PartnerMasterT> getPartnerTs1() {
		return partnerTs1;
	}

	public void setPartnerTs1(List<PartnerMasterT> partnerTs1) {
		this.partnerTs1 = partnerTs1;
	}

	public List<PartnerMasterT> getPartnerTs2() {
		return partnerTs2;
	}

	public void setPartnerTs2(List<PartnerMasterT> partnerTs2) {
		this.partnerTs2 = partnerTs2;
	}

	public List<ContactT> getContactTs1() {
		return contactTs1;
	}

	public void setContactTs1(List<ContactT> contactTs1) {
		this.contactTs1 = contactTs1;
	}

	public List<ContactT> getContactTs2() {
		return contactTs2;
	}

	public void setContactTs2(List<ContactT> contactTs2) {
		this.contactTs2 = contactTs2;
	}

	public List<WorkflowBfmT> getWorkflowBfmTs1() {
		return workflowBfmTs1;
	}

	public void setWorkflowBfmTs1(List<WorkflowBfmT> workflowBfmTs1) {
		this.workflowBfmTs1 = workflowBfmTs1;
	}

	public List<WorkflowBfmT> getWorkflowBfmTs2() {
		return workflowBfmTs2;
	}

	public void setWorkflowBfmTs2(List<WorkflowBfmT> workflowBfmTs2) {
		this.workflowBfmTs2 = workflowBfmTs2;
	}

	public List<DocumentsT> getDocumentsTs1() {
		return documentsTs1;
	}

	public void setDocumentsTs1(List<DocumentsT> documentsTs1) {
		this.documentsTs1 = documentsTs1;
	}

	public List<DocumentsT> getDocumentsTs2() {
		return documentsTs2;
	}

	public void setDocumentsTs2(List<DocumentsT> documentsTs2) {
		this.documentsTs2 = documentsTs2;
	}
	
	public Integer getDeliveryClusterId() {
		return deliveryClusterId;
	}

	public void setDeliveryClusterId(Integer deliveryClusterId) {
		this.deliveryClusterId = deliveryClusterId;
	}
	
	public List<Integer> getDeliveryCentreId() {
		return deliveryCentreId;
	}

	public void setDeliveryCentreId(List<Integer> deliveryCentreId) {
		this.deliveryCentreId = deliveryCentreId;
	}
	
	public List<DeliveryCentreT> getDeliveryCentreTs() {
		return deliveryCentreTs;
	}

	public void setDeliveryCentreTs(List<DeliveryCentreT> deliveryCentreTs) {
		this.deliveryCentreTs = deliveryCentreTs;
	}

	public List<DeliveryClusterT> getDeliveryClusterTs() {
		return deliveryClusterTs;
	}

	public void setDeliveryClusterTs(List<DeliveryClusterT> deliveryClusterTs) {
		this.deliveryClusterTs = deliveryClusterTs;
	}

	public List<DeliveryPmoT> getDeliveryCentresPmo() {
		return deliveryCentresPmo;
	}

	public void setDeliveryCentresPmo(List<DeliveryPmoT> deliveryCentresPmo) {
		this.deliveryCentresPmo = deliveryCentresPmo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserT other = (UserT) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	
	//	public List<WorkflowCompetitorT> getWorkflowCompetitorTs1() {
	//		return this.workflowCompetitorTs1;
	//	}
	//
	//	public void setWorkflowCompetitorTs1(List<WorkflowCompetitorT> workflowCompetitorTs1) {
	//		this.workflowCompetitorTs1 = workflowCompetitorTs1;
	//	}
	//
	//	public WorkflowCompetitorT addWorkflowCompetitorTs1(WorkflowCompetitorT workflowCompetitorTs1) {
	//		getWorkflowCompetitorTs1().add(workflowCompetitorTs1);
	//		workflowCompetitorTs1.setCreatedByUser(this);
	//
	//		return workflowCompetitorTs1;
	//	}
	//
	//	public WorkflowCompetitorT removeWorkflowCompetitorTs1(WorkflowCompetitorT workflowCompetitorTs1) {
	//		getWorkflowCompetitorTs1().remove(workflowCompetitorTs1);
	//		workflowCompetitorTs1.setCreatedByUser(null);
	//
	//		return workflowCompetitorTs1;
	//	}
	//
	//	public List<WorkflowCompetitorT> getWorkflowCompetitorTs2() {
	//		return this.workflowCompetitorTs2;
	//	}
	//
	//	public void setWorkflowCompetitorTs2(List<WorkflowCompetitorT> workflowCompetitorTs2) {
	//		this.workflowCompetitorTs2 = workflowCompetitorTs2;
	//	}
	//
	//	public WorkflowCompetitorT addWorkflowCompetitorTs2(WorkflowCompetitorT workflowCompetitorTs2) {
	//		getWorkflowCompetitorTs2().add(workflowCompetitorTs2);
	//		workflowCompetitorTs2.setModifiedByUser(this);
	//
	//		return workflowCompetitorTs2;
	//	}
	//
	//	public WorkflowCompetitorT removeWorkflowCompetitorTs2(WorkflowCompetitorT workflowCompetitorTs2) {
	//		getWorkflowCompetitorTs2().remove(workflowCompetitorTs2);
	//		workflowCompetitorTs2.setModifiedByUser(null);
	//
	//		return workflowCompetitorTs2;
	//	}
	
	
	
	
}