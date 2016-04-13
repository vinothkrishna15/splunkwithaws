package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the user_module_access_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userModuleAccessId")
@Entity
@Table(name="user_module_access_t")
@NamedQuery(name="UserModuleAccessT.findAll", query="SELECT u FROM UserModuleAccessT u")
public class UserModuleAccessT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="user_module_access_id")
	private Integer userModuleAccessId;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="user_id")
	private UserT userT;

	//bi-directional many-to-one association to ModuleSubModuleT
	@ManyToOne
	@JoinColumn(name="module_sub_module_id")
	private ModuleSubModuleT moduleSubModuleT;

	public UserModuleAccessT() {
	}

	public Integer getUserModuleAccessId() {
		return this.userModuleAccessId;
	}

	public void setUserModuleAccessId(Integer userModuleAccessId) {
		this.userModuleAccessId = userModuleAccessId;
	}

	public ModuleSubModuleT getModuleSubModuleT() {
		return this.moduleSubModuleT;
	}

	public void setModuleSubModuleT(ModuleSubModuleT moduleSubModuleT) {
		this.moduleSubModuleT = moduleSubModuleT;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

}