package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the workflow_process_template database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="templateId")
@Entity
@Table(name="workflow_process_template")
@NamedQuery(name="WorkflowProcessTemplate.findAll", query="SELECT w FROM WorkflowProcessTemplate w")
public class WorkflowProcessTemplate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="template_id")
	private Integer templateId;

	@Column(name="entity_type_id")
	private Integer entityTypeId;

	private Integer step;

	//bi-directional many-to-one association to UserGroupMappingT
	@ManyToOne
	@JoinColumn(name="user_group")
	private UserGroupMappingT userGroupMappingT;

	public WorkflowProcessTemplate() {
	}

	public Integer getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public Integer getEntityTypeId() {
		return this.entityTypeId;
	}

	public void setEntityTypeId(Integer entityTypeId) {
		this.entityTypeId = entityTypeId;
	}

	

	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	public UserGroupMappingT getUserGroupMappingT() {
		return this.userGroupMappingT;
	}

	public void setUserGroupMappingT(UserGroupMappingT userGroupMappingT) {
		this.userGroupMappingT = userGroupMappingT;
	}

}