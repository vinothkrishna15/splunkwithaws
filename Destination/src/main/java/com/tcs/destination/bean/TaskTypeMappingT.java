package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the task_type_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Entity
@Table(name = "task_type_mapping_t")
@NamedQuery(name = "TaskTypeMappingT.findAll", query = "SELECT t FROM TaskTypeMappingT t")
public class TaskTypeMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String type;

	private String description;

	// bi-directional many-to-one association to TaskT
	@JsonIgnore
	@OneToMany(mappedBy = "taskTypeMappingT")
	private List<TaskT> taskTs;

	public TaskTypeMappingT() {
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<TaskT> getTaskTs() {
		return this.taskTs;
	}

	public void setTaskTs(List<TaskT> taskTs) {
		this.taskTs = taskTs;
	}

	public TaskT addTaskT(TaskT taskT) {
		getTaskTs().add(taskT);
		taskT.setTaskTypeMappingT(this);

		return taskT;
	}

	public TaskT removeTaskT(TaskT taskT) {
		getTaskTs().remove(taskT);
		taskT.setTaskTypeMappingT(null);

		return taskT;
	}

}