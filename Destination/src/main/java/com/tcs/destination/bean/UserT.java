package com.tcs.destination.bean;

import java.io.Serializable;
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

	// bi-directional many-to-one association to BdmTargetT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<BdmTargetT> bdmTargetTs;

	// bi-directional many-to-one association to BidOfficeGroupOwnerLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "bidOfficeGroupOwnerUser")
	private List<BidOfficeGroupOwnerLinkT> bidOfficeGroupOwnerLinkTs;

	// bi-directional many-to-one association to CollaborationCommentT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<CollaborationCommentT> collaborationCommentTs;

	// bi-directional many-to-one association to ConnectSecondaryOwnerLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerLinkTs;

	// bi-directional many-to-one association to ConnectT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<ConnectT> connectTs;

	// bi-directional many-to-one association to DocumentRepositoryT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<DocumentRepositoryT> documentRepositoryTs;

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

	// bi-directional many-to-one association to TaskBdmsTaggedLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<TaskBdmsTaggedLinkT> taskBdmsTaggedLinkTs;

	// bi-directional many-to-one association to TaskT
	@JsonIgnore
	@OneToMany(mappedBy = "taskOwnerT")
	private List<TaskT> taskTs;

	// bi-directional many-to-one association to UserFavoritesT
	@JsonIgnore
	@OneToMany(mappedBy = "userT")
	private List<UserFavoritesT> userFavoritesTs;

	// bi-directional one-to-one association to UserSettingsT
	@JsonIgnore
	@OneToOne(mappedBy = "userT")
	private UserGeneralSettingsT userGeneralSettingsT;

	// bi-directional many-to-one association to UserGroupMappingT
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_group")
	private UserGroupMappingT userGroupMappingT;

	// bi-directional many-to-one association to UserRoleMappingT
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_role")
	private UserRoleMappingT userRoleMappingT;

	//bi-directional many-to-one association to UserNotificationsT
	@OneToMany(mappedBy="userT")
	private List<UserNotificationsT> userNotificationsTs;

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

	public UserGeneralSettingsT getUserSettingsT() {
		return this.userGeneralSettingsT;
	}

	public void setUserSettingsT(UserGeneralSettingsT userGeneralSettingsT) {
		this.userGeneralSettingsT = userGeneralSettingsT;
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

	public List<UserNotificationsT> getUserNotificationsTs() {
		return this.userNotificationsTs;
	}

	public void setUserNotificationsTs(List<UserNotificationsT> userNotificationsTs) {
		this.userNotificationsTs = userNotificationsTs;
	}

	public UserNotificationsT addUserNotificationsT(UserNotificationsT userNotificationsT) {
		getUserNotificationsTs().add(userNotificationsT);
		userNotificationsT.setUserT(this);

		return userNotificationsT;
	}

	public UserNotificationsT removeUserNotificationsT(UserNotificationsT userNotificationsT) {
		getUserNotificationsTs().remove(userNotificationsT);
		userNotificationsT.setUserT(null);

		return userNotificationsT;
	}

	public UserGeneralSettingsT getUserGeneralSettingsT() {
		return userGeneralSettingsT;
	}

	public void setUserGeneralSettingsT(UserGeneralSettingsT userGeneralSettingsT) {
		this.userGeneralSettingsT = userGeneralSettingsT;
	}

}