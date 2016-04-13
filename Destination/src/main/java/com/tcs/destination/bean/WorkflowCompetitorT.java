package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the workflow_competitor_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="workflowCompetitorId")
@Entity
@Table(name="workflow_competitor_t")
@NamedQuery(name="WorkflowCompetitorT.findAll", query="SELECT w FROM WorkflowCompetitorT w")
public class WorkflowCompetitorT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="workflow_competitor_id")
	private Integer workflowCompetitorId;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;

	@Column(name="workflow_competitor_name")
	private String workflowCompetitorName;

	@Column(name="workflow_competitor_notes")
	private String workflowCompetitorNotes;

	@Column(name="workflow_competitor_website")
	private String workflowCompetitorWebsite;

	@Column(name="created_by")
	private String createdBy;

	@Column(name="modified_by")
	private String modifiedBy;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by", updatable = false, insertable = false)
	private UserT createdByUser;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="modified_by", updatable = false, insertable = false)
	private UserT modifiedByUser;

	public WorkflowCompetitorT() {
	}

	public Integer getWorkflowCompetitorId() {
		return this.workflowCompetitorId;
	}

	public void setWorkflowCompetitorId(Integer workflowCompetitorId) {
		this.workflowCompetitorId = workflowCompetitorId;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public String getWorkflowCompetitorName() {
		return this.workflowCompetitorName;
	}

	public void setWorkflowCompetitorName(String workflowCompetitorName) {
		this.workflowCompetitorName = workflowCompetitorName;
	}

	public String getWorkflowCompetitorNotes() {
		return this.workflowCompetitorNotes;
	}

	public void setWorkflowCompetitorNotes(String workflowCompetitorNotes) {
		this.workflowCompetitorNotes = workflowCompetitorNotes;
	}

	public String getWorkflowCompetitorWebsite() {
		return this.workflowCompetitorWebsite;
	}

	public void setWorkflowCompetitorWebsite(String workflowCompetitorWebsite) {
		this.workflowCompetitorWebsite = workflowCompetitorWebsite;
	}

	public UserT getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserT createdByUser) {
		this.createdByUser = createdByUser;
	}

	public UserT getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

}
