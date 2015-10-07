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
 * The persistent class for the task_bdms_tagged_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "taskBdmsTaggedLinkId")
@Entity
@Table(name = "task_bdms_tagged_link_t")
@NamedQuery(name = "TaskBdmsTaggedLinkT.findAll", query = "SELECT t FROM TaskBdmsTaggedLinkT t")
public class TaskBdmsTaggedLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "task_bdms_tagged_link_id")
	private String taskBdmsTaggedLinkId;

	@Column(name = "created_by", updatable = false)
	private String createdBy;

	@Column(name = "created_datetime", updatable = false)
	private Timestamp createdDatetime;

	@Column(name = "modified_by")
	private String modifiedBy;

	@Column(name = "modified_datetime")
	private Timestamp modifiedDatetime;

	@Column(name = "task_id")
	private String taskId;

	@Column(name = "bdms_tagged")
	private String bdmsTagged;

	// bi-directional many-to-one association to TaskT
	@ManyToOne
	@JoinColumn(name = "task_id", insertable = false, updatable = false)
	private TaskT taskT;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "created_by", insertable = false, updatable = false)
	private UserT createdByUser;

	@ManyToOne
	@JoinColumn(name = "modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "bdms_tagged", insertable = false, updatable = false)
	private UserT userT;

	public TaskBdmsTaggedLinkT() {
	}

	public String getTaskBdmsTaggedLinkId() {
		return this.taskBdmsTaggedLinkId;
	}

	public void setTaskBdmsTaggedLinkId(String taskBdmsTaggedLinkId) {
		this.taskBdmsTaggedLinkId = taskBdmsTaggedLinkId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public TaskT getTaskT() {
		return this.taskT;
	}

	public void setTaskT(TaskT taskT) {
		this.taskT = taskT;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

	public UserT getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserT createdByUser) {
		this.createdByUser = createdByUser;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public UserT getModifiedByUser() {
		return modifiedByUser;
	}

	public Timestamp getModifiedDatetime() {
		return modifiedDatetime;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setModifiedByUser(UserT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public String getBdmsTagged() {
		return bdmsTagged;
	}

	public void setBdmsTagged(String bdmsTagged) {
		this.bdmsTagged = bdmsTagged;
	}

}