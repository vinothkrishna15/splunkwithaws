package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the user_group_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="userGroup")
@Entity
@Table(name="user_group_mapping_t")
@NamedQuery(name="UserGroupMappingT.findAll", query="SELECT u FROM UserGroupMappingT u")
public class UserGroupMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_group")
	private String userGroup;
	
	//bi-directional many-to-one association to UserGroupModuleT
	@OneToMany(mappedBy="userGroupMappingT")
	private List<UserGroupModuleT> userGroupModuleTs;
	
	//bi-directional many-to-one association to GoalGroupMappingT
	@OneToMany(mappedBy="userGroupMappingT")
	private List<GoalGroupMappingT> goalGroupMappingTs;

	//bi-directional many-to-one association to UserT
	@JsonIgnore
	@OneToMany(mappedBy="userGroupMappingT")
	private List<UserT> userTs;

	public UserGroupMappingT() {
	}

	public String getUserGroup() {
		return this.userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}
	
	public List<GoalGroupMappingT> getGoalGroupMappingTs() {
		return this.goalGroupMappingTs;
	}

	public void setGoalGroupMappingTs(List<GoalGroupMappingT> goalGroupMappingTs) {
		this.goalGroupMappingTs = goalGroupMappingTs;
	}

	public GoalGroupMappingT addGoalGroupMappingT(GoalGroupMappingT goalGroupMappingT) {
		getGoalGroupMappingTs().add(goalGroupMappingT);
		goalGroupMappingT.setUserGroupMappingT(this);

		return goalGroupMappingT;
	}

	public GoalGroupMappingT removeGoalGroupMappingT(GoalGroupMappingT goalGroupMappingT) {
		getGoalGroupMappingTs().remove(goalGroupMappingT);
		goalGroupMappingT.setUserGroupMappingT(null);

		return goalGroupMappingT;
	}

	public List<UserT> getUserTs() {
		return this.userTs;
	}

	public void setUserTs(List<UserT> userTs) {
		this.userTs = userTs;
	}

	public UserT addUserT(UserT userT) {
		getUserTs().add(userT);
		userT.setUserGroupMappingT(this);

		return userT;
	}

	public UserT removeUserT(UserT userT) {
		getUserTs().remove(userT);
		userT.setUserGroupMappingT(null);

		return userT;
	}

	public List<UserGroupModuleT> getUserGroupModuleTs() {
		return this.userGroupModuleTs;
	}

	public void setUserGroupModuleTs(List<UserGroupModuleT> userGroupModuleTs) {
		this.userGroupModuleTs = userGroupModuleTs;
	}

	public UserGroupModuleT addUserGroupModuleT(UserGroupModuleT userGroupModuleT) {
		getUserGroupModuleTs().add(userGroupModuleT);
		userGroupModuleT.setUserGroupMappingT(this);

		return userGroupModuleT;
	}

	public UserGroupModuleT removeUserGroupModuleT(UserGroupModuleT userGroupModuleT) {
		getUserGroupModuleTs().remove(userGroupModuleT);
		userGroupModuleT.setUserGroupMappingT(null);

		return userGroupModuleT;
	}
	
}