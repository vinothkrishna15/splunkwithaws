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
 * The persistent class for the module_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "moduleId")
@Entity
@Table(name="module_t")
@NamedQuery(name="ModuleT.findAll", query="SELECT m FROM ModuleT m")
public class ModuleT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="module_id")
	private Integer moduleId;

	@Column(name="module_name")
	private String moduleName;
	

	//bi-directional many-to-one association to ModuleSubModuleT
	@JsonIgnore
	@OneToMany(mappedBy="moduleT", cascade = CascadeType.ALL)
	private List<ModuleSubModuleT> moduleSubModuleTs;

	//bi-directional many-to-one association to UserGroupModuleT
	@JsonIgnore
	@OneToMany(mappedBy="moduleT", cascade = CascadeType.ALL)
	private List<UserGroupModuleT> userGroupModuleTs;

	public ModuleT() {
	}

	public Integer getModuleId() {
		return this.moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleName() {
		return this.moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public List<ModuleSubModuleT> getModuleSubModuleTs() {
		return this.moduleSubModuleTs;
	}

	public void setModuleSubModuleTs(List<ModuleSubModuleT> moduleSubModuleTs) {
		this.moduleSubModuleTs = moduleSubModuleTs;
	}

	public ModuleSubModuleT addModuleSubModuleT(ModuleSubModuleT moduleSubModuleT) {
		getModuleSubModuleTs().add(moduleSubModuleT);
		moduleSubModuleT.setModuleT(this);

		return moduleSubModuleT;
	}

	public ModuleSubModuleT removeModuleSubModuleT(ModuleSubModuleT moduleSubModuleT) {
		getModuleSubModuleTs().remove(moduleSubModuleT);
		moduleSubModuleT.setModuleT(null);

		return moduleSubModuleT;
	}

	public List<UserGroupModuleT> getUserGroupModuleTs() {
		return this.userGroupModuleTs;
	}

	public void setUserGroupModuleTs(List<UserGroupModuleT> userGroupModuleTs) {
		this.userGroupModuleTs = userGroupModuleTs;
	}

	public UserGroupModuleT addUserGroupModuleT(UserGroupModuleT userGroupModuleT) {
		getUserGroupModuleTs().add(userGroupModuleT);
		userGroupModuleT.setModuleT(this);

		return userGroupModuleT;
	}

	public UserGroupModuleT removeUserGroupModuleT(UserGroupModuleT userGroupModuleT) {
		getUserGroupModuleTs().remove(userGroupModuleT);
		userGroupModuleT.setModuleT(null);

		return userGroupModuleT;
	}
}