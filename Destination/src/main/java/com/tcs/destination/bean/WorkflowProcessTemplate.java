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
	
	@Column(name="user_group")
	private String userGroup;
	
	@Column(name="user_role")
	private String userRole;

	private Integer step;
	
	@Column(name="user_id")
	private String user_id;
	
	@ManyToOne
	@JoinColumn(name="user_id", insertable = false, updatable = false)
	private UserT userT;

	public WorkflowProcessTemplate() {
	}

	public Integer getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	

	

	public Integer getEntityTypeId() {
		return entityTypeId;
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

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public UserT getUserT() {
		return userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}
	
    

}