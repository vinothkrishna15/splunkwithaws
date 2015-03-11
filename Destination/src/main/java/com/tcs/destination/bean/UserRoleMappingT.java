package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.List;


/**
 * The persistent class for the user_role_mapping_t database table.
 * 
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="userRole")
@Entity
@Table(name="user_role_mapping_t")
@NamedQuery(name="UserRoleMappingT.findAll", query="SELECT u FROM UserRoleMappingT u")
public class UserRoleMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_role")
	private String userRole;

	//bi-directional many-to-one association to UserT
	@OneToMany(mappedBy="userRoleMappingT")
	private List<UserT> userTs;

	public UserRoleMappingT() {
	}

	public String getUserRole() {
		return this.userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public List<UserT> getUserTs() {
		return this.userTs;
	}

	public void setUserTs(List<UserT> userTs) {
		this.userTs = userTs;
	}

	public UserT addUserT(UserT userT) {
		getUserTs().add(userT);
		userT.setUserRoleMappingT(this);

		return userT;
	}

	public UserT removeUserT(UserT userT) {
		getUserTs().remove(userT);
		userT.setUserRoleMappingT(null);

		return userT;
	}

}