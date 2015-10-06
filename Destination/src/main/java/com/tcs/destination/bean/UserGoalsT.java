package com.tcs.destination.bean;


import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;
import java.math.BigDecimal;


/**
 * The persistent class for the user_goals_t database table.
 * 
 */
@Entity
@JsonFilter(Constants.FILTER)
@Table(name="user_goals_t")
@NamedQuery(name="UserGoalsT.findAll", query="SELECT u FROM UserGoalsT u")
public class UserGoalsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_goal_id")
	private String userGoalId;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="financial_year")
	private String financialYear;

	@Column(name="target_value")
	private BigDecimal targetValue;

	//bi-directional many-to-one association to GoalMappingT
	@ManyToOne
	@JoinColumn(name="goal_id")
	private GoalMappingT goalMappingT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="user_id")
	private UserT userT1;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_modified_by")
	private UserT userT2;

	public UserGoalsT() {
	}

	public String getUserGoalId() {
		return this.userGoalId;
	}

	public void setUserGoalId(String userGoalId) {
		this.userGoalId = userGoalId;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return this.createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
	}

	public String getFinancialYear() {
		return this.financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public BigDecimal getTargetValue() {
		return this.targetValue;
	}

	public void setTargetValue(BigDecimal targetValue) {
		this.targetValue = targetValue;
	}

	public GoalMappingT getGoalMappingT() {
		return this.goalMappingT;
	}

	public void setGoalMappingT(GoalMappingT goalMappingT) {
		this.goalMappingT = goalMappingT;
	}

	public UserT getUserT1() {
		return this.userT1;
	}

	public void setUserT1(UserT userT1) {
		this.userT1 = userT1;
	}

	public UserT getUserT2() {
		return this.userT2;
	}

	public void setUserT2(UserT userT2) {
		this.userT2 = userT2;
	}

}