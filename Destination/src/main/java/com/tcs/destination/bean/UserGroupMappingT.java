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

	//bi-directional many-to-one association to UserT
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

}