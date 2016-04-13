package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the user_group_module_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "groupId")
@Entity
@Table(name="user_group_module_t")
@NamedQuery(name="UserGroupModuleT.findAll", query="SELECT u FROM UserGroupModuleT u")
public class UserGroupModuleT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="group_id")
	private Integer groupId;

	//bi-directional many-to-one association to ModuleT
	@ManyToOne
	@JoinColumn(name="module_id")
	private ModuleT moduleT;

	//bi-directional many-to-one association to UserGroupMappingT
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="user_group")
	private UserGroupMappingT userGroupMappingT;

	public UserGroupModuleT() {
	}

	public Integer getGroupId() {
		return this.groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public ModuleT getModuleT() {
		return this.moduleT;
	}

	public void setModuleT(ModuleT moduleT) {
		this.moduleT = moduleT;
	}

	public UserGroupMappingT getUserGroupMappingT() {
		return this.userGroupMappingT;
	}

	public void setUserGroupMappingT(UserGroupMappingT userGroupMappingT) {
		this.userGroupMappingT = userGroupMappingT;
	}

}