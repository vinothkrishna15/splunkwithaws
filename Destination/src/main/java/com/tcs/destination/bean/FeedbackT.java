package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the feedback_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="feedbackId")
@Entity
@Table(name="feedback_t")
@NamedQuery(name="FeedbackT.findAll", query="SELECT f FROM FeedbackT f")
public class FeedbackT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="feedback_id")
	private String feedbackId;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	private String description;

	@Column(name="issue_type")
	private String issueType;

	private String module;

	private String priority;

	@Column(name="resolution_comments")
	private String resolutionComments;

	private String status;

	@Column(name="sub_module")
	private String subModule;

	private String title;
	
	@Column(name="updated_datetime")
	private Timestamp updatedDatetime;
	
	@Column(name="updated_user_id")
	private String updatedUserId;
	
	@Column(name = "user_id")
	private String userId;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="user_id",insertable = false, updatable = false)
	private UserT createdUser;
		
	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="updated_user_id",insertable = false, updatable = false)
	private UserT modifiedUser;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUpdatedUserId() {
		return updatedUserId;
	}

	public void setUpdatedUserId(String updatedUserId) {
		this.updatedUserId = updatedUserId;
	}

	public Timestamp getUpdatedDatetime() {
		return updatedDatetime;
	}

	public void setUpdatedDatetime(Timestamp updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
	}

	public UserT getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(UserT createdUser) {
		this.createdUser = createdUser;
	}

	public UserT getModifiedUser() {
		return modifiedUser;
	}

	public void setModifiedUser(UserT modifiedUser) {
		this.modifiedUser = modifiedUser;
	}

	public FeedbackT() {
	}

	public String getFeedbackId() {
		return this.feedbackId;
	}

	public void setFeedbackId(String feedbackId) {
		this.feedbackId = feedbackId;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIssueType() {
		return this.issueType;
	}

	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	public String getModule() {
		return this.module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getPriority() {
		return this.priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getResolutionComments() {
		return this.resolutionComments;
	}

	public void setResolutionComments(String resolutionComments) {
		this.resolutionComments = resolutionComments;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubModule() {
		return this.subModule;
	}

	public void setSubModule(String subModule) {
		this.subModule = subModule;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public UserT getUserT() {
		return this.createdUser;
	}

	public void setUserT(UserT userT) {
		this.createdUser = userT;
	}

}