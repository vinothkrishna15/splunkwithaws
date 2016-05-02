package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.util.List;


/**
 * The persistent class for the module_sub_module_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "moduleSubModuleId")
@Entity
@Table(name="module_sub_module_t")
@NamedQuery(name="ModuleSubModuleT.findAll", query="SELECT m FROM ModuleSubModuleT m")
public class ModuleSubModuleT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="module_sub_module_id")
	private Integer moduleSubModuleId;

	//bi-directional many-to-one association to ModuleT
	@ManyToOne
	@JoinColumn(name="module_id")
	private ModuleT moduleT;

	@Column(name="sub_module_name")
	private String subModuleName;
	
	@Column(name="access_type")
	private Integer accessType;

	//bi-directional many-to-one association to UserModuleAccessT
	@Transient
	@JsonIgnore
//	@OneToMany(mappedBy="moduleSubModuleT", cascade = CascadeType.ALL)
	private List<UserModuleAccessT> userModuleAccessTs;

	public ModuleSubModuleT() {
	}

	public Integer getModuleSubModuleId() {
		return this.moduleSubModuleId;
	}

	public void setModuleSubModuleId(Integer moduleSubModuleId) {
		this.moduleSubModuleId = moduleSubModuleId;
	}

	public Integer getAccessType() {
		return this.accessType;
	}

	public void setAccessType(Integer accessType) {
		this.accessType = accessType;
	}

	public String getSubModuleName() {
		return this.subModuleName;
	}

	public void setSubModuleName(String subModuleName) {
		this.subModuleName = subModuleName;
	}

	public ModuleT getModuleT() {
		return this.moduleT;
	}

	public void setModuleT(ModuleT moduleT) {
		this.moduleT = moduleT;
	}

//	public List<UserModuleAccessT> getUserModuleAccessTs() {
//		return this.userModuleAccessTs;
//	}
//
//	public void setUserModuleAccessTs(List<UserModuleAccessT> userModuleAccessTs) {
//		this.userModuleAccessTs = userModuleAccessTs;
//	}

//	public UserModuleAccessT addUserModuleAccessT(UserModuleAccessT userModuleAccessT) {
//		getUserModuleAccessTs().add(userModuleAccessT);
//		userModuleAccessT.setModuleSubModuleT(this);
//
//		return userModuleAccessT;
//	}
//
//	public UserModuleAccessT removeUserModuleAccessT(UserModuleAccessT userModuleAccessT) {
//		getUserModuleAccessTs().remove(userModuleAccessT);
//		userModuleAccessT.setModuleSubModuleT(null);
//
//		return userModuleAccessT;
//	}
}