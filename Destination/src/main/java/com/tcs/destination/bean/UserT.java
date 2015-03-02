package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

/**
 * The persistent class for the user_t database table.
 * 
 */
@Entity
@Table(name = "user_t")
@NamedQuery(name = "UserT.findAll", query = "SELECT u FROM UserT u")
public class UserT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	// bi-directional many-to-one association to BidOfficeGroupOwnerLinkT
	@OneToMany(mappedBy = "userT")
	private List<BidOfficeGroupOwnerLinkT> bidOfficeGroupOwnerLinkTs;

	// bi-directional many-to-one association to ConnectSecondaryOwnerLinkT
	@OneToMany(mappedBy = "userT")
	private List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerLinkTs;

	// bi-directional many-to-one association to ConnectT
	@OneToMany(mappedBy = "userT")
	private List<ConnectT> connectTs;

	// bi-directional many-to-one association to LoginHistoryT
	@OneToMany(mappedBy = "userT")
	private List<LoginHistoryT> loginHistoryTs;

	// bi-directional many-to-one association to OpportunitySalesSupportLinkT
	@OneToMany(mappedBy = "userT")
	private List<OpportunitySalesSupportLinkT> opportunitySalesSupportLinkTs;

	// bi-directional many-to-one association to UserFavoritesT
	@OneToMany(mappedBy = "userT")
	private List<UserFavoritesT> userFavoritesTs;

	// bi-directional many-to-one association to UserGroupMappingT
	@ManyToOne
	@JoinColumn(name = "user_group")
	private UserGroupMappingT userGroupMappingT;

	// bi-directional many-to-one association to UserRoleMappingT
	@ManyToOne
	@JoinColumn(name = "user_role")
	private UserRoleMappingT userRoleMappingT;

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
		bidOfficeGroupOwnerLinkT.setUserT(this);

		return bidOfficeGroupOwnerLinkT;
	}

	public BidOfficeGroupOwnerLinkT removeBidOfficeGroupOwnerLinkT(
			BidOfficeGroupOwnerLinkT bidOfficeGroupOwnerLinkT) {
		getBidOfficeGroupOwnerLinkTs().remove(bidOfficeGroupOwnerLinkT);
		bidOfficeGroupOwnerLinkT.setUserT(null);

		return bidOfficeGroupOwnerLinkT;
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
		opportunitySalesSupportLinkT.setUserT(this);

		return opportunitySalesSupportLinkT;
	}

	public OpportunitySalesSupportLinkT removeOpportunitySalesSupportLinkT(
			OpportunitySalesSupportLinkT opportunitySalesSupportLinkT) {
		getOpportunitySalesSupportLinkTs().remove(opportunitySalesSupportLinkT);
		opportunitySalesSupportLinkT.setUserT(null);

		return opportunitySalesSupportLinkT;
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

}