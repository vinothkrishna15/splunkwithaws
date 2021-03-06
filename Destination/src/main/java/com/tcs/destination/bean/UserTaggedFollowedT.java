package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the user_tagged_followed_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="userTaggedFollowedId")
@Entity
@Table(name="user_tagged_followed_t")
@NamedQuery(name="UserTaggedFollowedT.findAll", query="SELECT u FROM UserTaggedFollowedT u")
public class UserTaggedFollowedT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_tagged_followed_id")
	private String userTaggedFollowedId;
	
	@Column(name="connect_id")
	private String connectId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="entity_type")
	private String entityType;
	

	@Column(name="opportunity_id")
	private String opportunityId;

	@Column(name="task_id")
	private String taskId;

	@Column(name="user_id")
	private String userId;

	//bi-directional many-to-one association to ConnectT
	@ManyToOne
	@JoinColumn(name="connect_id",insertable=false,updatable=false)
	private ConnectT connectT;

	//bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name="opportunity_id",insertable=false,updatable=false)
	private OpportunityT opportunityT;

	//bi-directional many-to-one association to TaskT
	@ManyToOne
	@JoinColumn(name="task_id",insertable=false,updatable=false)
	private TaskT taskT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_modified_by",insertable=false,updatable=false)
	private UserT userT1;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="user_id",insertable=false,updatable=false)
	private UserT userT2;

	public UserTaggedFollowedT() {
	}

	public String getUserTaggedFollowedId() {
		return this.userTaggedFollowedId;
	}

	public void setUserTaggedFollowedId(String userTaggedFollowedId) {
		this.userTaggedFollowedId = userTaggedFollowedId;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return this.createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
	}

	public String getEntityType() {
		return this.entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public ConnectT getConnectT() {
		return this.connectT;
	}

	public void setConnectT(ConnectT connectT) {
		this.connectT = connectT;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

	public TaskT getTaskT() {
		return this.taskT;
	}

	public void setTaskT(TaskT taskT) {
		this.taskT = taskT;
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

	public String getConnectId() {
		return connectId;
	}

	public void setConnectId(String connectId) {
		this.connectId = connectId;
	}

	public String getCreatedModifiedBy() {
		return createdModifiedBy;
	}

	public void setCreatedModifiedBy(String createdModifiedBy) {
		this.createdModifiedBy = createdModifiedBy;
	}

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	
}