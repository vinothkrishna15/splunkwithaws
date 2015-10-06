package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the goal_group_mapping_t database table.
 * 
 */
@Entity
@JsonFilter(Constants.FILTER)
@Table(name="goal_group_mapping_t")
@NamedQuery(name="GoalGroupMappingT.findAll", query="SELECT g FROM GoalGroupMappingT g")
public class GoalGroupMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	private String financialyear;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="goal_group_mapping_id")
	private String goalGroupMappingId;

	@Column(name="is_active")
	private String isActive;

	//bi-directional many-to-one association to GoalMappingT
	@ManyToOne
	@JoinColumn(name="goal_id")
	private GoalMappingT goalMappingT;

	//bi-directional many-to-one association to UserGroupMappingT
	@ManyToOne
	@JoinColumn(name="user_group")
	private UserGroupMappingT userGroupMappingT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_modified_by")
	private UserT userT;

	public GoalGroupMappingT() {
	}

	public Timestamp getCreatedModifiedDatetime() {
		return this.createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
	}

	public String getFinancialyear() {
		return this.financialyear;
	}

	public void setFinancialyear(String financialyear) {
		this.financialyear = financialyear;
	}

	public String getGoalGroupMappingId() {
		return this.goalGroupMappingId;
	}

	public void setGoalGroupMappingId(String goalGroupMappingId) {
		this.goalGroupMappingId = goalGroupMappingId;
	}

	public String getIsActive() {
		return this.isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public GoalMappingT getGoalMappingT() {
		return this.goalMappingT;
	}

	public void setGoalMappingT(GoalMappingT goalMappingT) {
		this.goalMappingT = goalMappingT;
	}

	public UserGroupMappingT getUserGroupMappingT() {
		return this.userGroupMappingT;
	}

	public void setUserGroupMappingT(UserGroupMappingT userGroupMappingT) {
		this.userGroupMappingT = userGroupMappingT;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

}