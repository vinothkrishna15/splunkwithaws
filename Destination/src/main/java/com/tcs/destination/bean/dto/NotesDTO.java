package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The persistent class for the notes_t database table.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class NotesDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String noteId;

	private Timestamp createdDatetime;
	private String entityType;

	private String notesUpdated;

	private String connectId;
	private String customerId;
	private String partnerId;
	private String opportunityId;

	private String taskId;

	//user_updated
	private UserDTO userT;

	public NotesDTO() {
		super();
	}

	public String getNoteId() {
		return noteId;
	}

	public void setNoteId(String noteId) {
		this.noteId = noteId;
	}

	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getNotesUpdated() {
		return notesUpdated;
	}

	public void setNotesUpdated(String notesUpdated) {
		this.notesUpdated = notesUpdated;
	}

	public String getConnectId() {
		return connectId;
	}

	public void setConnectId(String connectId) {
		this.connectId = connectId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
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

	public UserDTO getUserT() {
		return userT;
	}

	public void setUserT(UserDTO userT) {
		this.userT = userT;
	}

}