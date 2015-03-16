package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the task_bdms_tagged_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "taskBdmsTaggedLinkId")
@Entity
@Table(name="task_bdms_tagged_link_t")
@NamedQuery(name="TaskBdmsTaggedLinkT.findAll", query="SELECT t FROM TaskBdmsTaggedLinkT t")
public class TaskBdmsTaggedLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="task_bdms_tagged_link_id")
	private String taskBdmsTaggedLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	//bi-directional many-to-one association to TaskT
	@ManyToOne
	@JoinColumn(name="task_id")
	private TaskT taskT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="bdms_tagged")
	private UserT userT;

	public TaskBdmsTaggedLinkT() {
	}

	public String getTaskBdmsTaggedLinkId() {
		return this.taskBdmsTaggedLinkId;
	}

	public void setTaskBdmsTaggedLinkId(String taskBdmsTaggedLinkId) {
		this.taskBdmsTaggedLinkId = taskBdmsTaggedLinkId;
	}

	public String getCreatedModifiedBy() {
		return this.createdModifiedBy;
	}

	public void setCreatedModifiedBy(String createdModifiedBy) {
		this.createdModifiedBy = createdModifiedBy;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return this.createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
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

}