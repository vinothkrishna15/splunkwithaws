package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.util.List;


/**
 * The persistent class for the user_access_privileges_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "privilegeId")
@Entity
@Table(name="user_access_privileges_t")
@NamedQuery(name="UserAccessPrivilegesT.findAll", query="SELECT u FROM UserAccessPrivilegesT u")
public class UserAccessPrivilegesT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="privilege_id")
	private Integer privilegeId;

	@Column(name="privilege_type")
	private String privilegeType;

	@Column(name="privilege_value")
	private String privilegeValue;

	//bi-directional many-to-one association to UserAccessPrivilegesT
	@ManyToOne
	@JoinColumn(name="parent_privilege_id", insertable=false ,updatable=false)
	private UserAccessPrivilegesT userAccessPrivilegesT;

	@Column(name="parent_privilege_id")
	private String parentPrivilegeId;

	//bi-directional many-to-one association to UserAccessPrivilegesT
	@OneToMany(mappedBy="userAccessPrivilegesT",fetch=FetchType.EAGER)
	private List<UserAccessPrivilegesT> userAccessPrivilegesTs;

	//bi-directional many-to-one association to UserT
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="user_id", insertable=false, updatable=false)
	private UserT userT;
	
	@Column(name="user_id")
	private String userId;

	public UserAccessPrivilegesT() {
	}

	public Integer getPrivilegeId() {
		return this.privilegeId;
	}

	public void setPrivilegeId(Integer privilegeId) {
		this.privilegeId = privilegeId;
	}

	public String getPrivilegeType() {
		return this.privilegeType;
	}

	public void setPrivilegeType(String privilegeType) {
		this.privilegeType = privilegeType;
	}

	public String getPrivilegeValue() {
		return this.privilegeValue;
	}

	public void setPrivilegeValue(String privilegeValue) {
		this.privilegeValue = privilegeValue;
	}

	public UserAccessPrivilegesT getUserAccessPrivilegesT() {
		return this.userAccessPrivilegesT;
	}

	public void setUserAccessPrivilegesT(UserAccessPrivilegesT userAccessPrivilegesT) {
		this.userAccessPrivilegesT = userAccessPrivilegesT;
	}

	public List<UserAccessPrivilegesT> getUserAccessPrivilegesTs() {
		return this.userAccessPrivilegesTs;
	}

	public void setUserAccessPrivilegesTs(List<UserAccessPrivilegesT> userAccessPrivilegesTs) {
		this.userAccessPrivilegesTs = userAccessPrivilegesTs;
	}

	public UserAccessPrivilegesT addUserAccessPrivilegesT(UserAccessPrivilegesT userAccessPrivilegesT) {
		getUserAccessPrivilegesTs().add(userAccessPrivilegesT);
		userAccessPrivilegesT.setUserAccessPrivilegesT(this);

		return userAccessPrivilegesT;
	}

	public UserAccessPrivilegesT removeUserAccessPrivilegesT(UserAccessPrivilegesT userAccessPrivilegesT) {
		getUserAccessPrivilegesTs().remove(userAccessPrivilegesT);
		userAccessPrivilegesT.setUserAccessPrivilegesT(null);
		return userAccessPrivilegesT;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getParentPrivilegeId() {
		return parentPrivilegeId;
	}

	public void setParentPrivilegeId(String parentPrivilegeId) {
		this.parentPrivilegeId = parentPrivilegeId;
	}
}