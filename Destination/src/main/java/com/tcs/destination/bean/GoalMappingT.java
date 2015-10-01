package com.tcs.destination.bean;


import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.List;


/**
 * The persistent class for the goal_mapping_t database table.
 * 
 */
@Entity
@JsonFilter(Constants.FILTER)
@Table(name="goal_mapping_t")
@NamedQuery(name="GoalMappingT.findAll", query="SELECT g FROM GoalMappingT g")
public class GoalMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="goal_id")
	private String goalId;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="default_target")
	private BigDecimal defaultTarget;

	@Column(name="display_unit")
	private String displayUnit;

	private String financialyear;

	@Column(name="goal_name")
	private String goalName;

	//bi-directional many-to-one association to GoalGroupMappingT
	@OneToMany(mappedBy="goalMappingT")
	private List<GoalGroupMappingT> goalGroupMappingTs;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_modified_by")
	private UserT userT;

	//bi-directional many-to-one association to UserGoalsT
	@OneToMany(mappedBy="goalMappingT")
	private List<UserGoalsT> userGoalsTs;

	public GoalMappingT() {
	}

	public String getGoalId() {
		return this.goalId;
	}

	public void setGoalId(String goalId) {
		this.goalId = goalId;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return this.createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
	}

	public BigDecimal getDefaultTarget() {
		return this.defaultTarget;
	}

	public void setDefaultTarget(BigDecimal defaultTarget) {
		this.defaultTarget = defaultTarget;
	}

	public String getDisplayUnit() {
		return this.displayUnit;
	}

	public void setDisplayUnit(String displayUnit) {
		this.displayUnit = displayUnit;
	}

	public String getFinancialyear() {
		return this.financialyear;
	}

	public void setFinancialyear(String financialyear) {
		this.financialyear = financialyear;
	}

	public String getGoalName() {
		return this.goalName;
	}

	public void setGoalName(String goalName) {
		this.goalName = goalName;
	}

	public List<GoalGroupMappingT> getGoalGroupMappingTs() {
		return this.goalGroupMappingTs;
	}

	public void setGoalGroupMappingTs(List<GoalGroupMappingT> goalGroupMappingTs) {
		this.goalGroupMappingTs = goalGroupMappingTs;
	}

	public GoalGroupMappingT addGoalGroupMappingT(GoalGroupMappingT goalGroupMappingT) {
		getGoalGroupMappingTs().add(goalGroupMappingT);
		goalGroupMappingT.setGoalMappingT(this);

		return goalGroupMappingT;
	}

	public GoalGroupMappingT removeGoalGroupMappingT(GoalGroupMappingT goalGroupMappingT) {
		getGoalGroupMappingTs().remove(goalGroupMappingT);
		goalGroupMappingT.setGoalMappingT(null);

		return goalGroupMappingT;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

	public List<UserGoalsT> getUserGoalsTs() {
		return this.userGoalsTs;
	}

	public void setUserGoalsTs(List<UserGoalsT> userGoalsTs) {
		this.userGoalsTs = userGoalsTs;
	}

	public UserGoalsT addUserGoalsT(UserGoalsT userGoalsT) {
		getUserGoalsTs().add(userGoalsT);
		userGoalsT.setGoalMappingT(this);

		return userGoalsT;
	}

	public UserGoalsT removeUserGoalsT(UserGoalsT userGoalsT) {
		getUserGoalsTs().remove(userGoalsT);
		userGoalsT.setGoalMappingT(null);

		return userGoalsT;
	}

}