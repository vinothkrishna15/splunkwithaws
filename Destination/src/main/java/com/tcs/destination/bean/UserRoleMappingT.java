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
 * The persistent class for the user_role_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="userRole")

@Entity
@Table(name = "user_role_mapping_t")
@NamedQuery(name = "UserRoleMappingT.findAll", query = "SELECT u FROM UserRoleMappingT u")
public class UserRoleMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_role")
	private String userRole;

	// bi-directional many-to-one association to UserT
	@JsonIgnore
	@OneToMany(mappedBy = "userRoleMappingT")
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