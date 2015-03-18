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
 * The persistent class for the notes_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="noteId")
@Entity
@Table(name="notes_t")
@NamedQuery(name="NotesT.findAll", query="SELECT n FROM NotesT n")
public class NotesT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="note_id")
	private String noteId;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="entity_type")
	private String entityType;

	@Column(name="notes_updated")
	private String notesUpdated;

	//bi-directional many-to-one association to ConnectT
	@ManyToOne
	@JoinColumn(name="connect_id")
	private ConnectT connectT;

	//bi-directional many-to-one association to CustomerMasterT
	@ManyToOne
	@JoinColumn(name="customer_id")
	private CustomerMasterT customerMasterT;

	//bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name="opportunity_id")
	private OpportunityT opportunityT;

	//bi-directional many-to-one association to PartnerMasterT
	@ManyToOne
	@JoinColumn(name="partner_id")
	private PartnerMasterT partnerMasterT;

	//bi-directional many-to-one association to TaskT
	@ManyToOne
	@JoinColumn(name="task_id")
	private TaskT taskT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="user_updated")
	private UserT userT;

	public NotesT() {
	}

	public String getNoteId() {
		return this.noteId;
	}

	public void setNoteId(String noteId) {
		this.noteId = noteId;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public String getEntityType() {
		return this.entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getNotesUpdated() {
		return this.notesUpdated;
	}

	public void setNotesUpdated(String notesUpdated) {
		this.notesUpdated = notesUpdated;
	}

	public ConnectT getConnectT() {
		return this.connectT;
	}

	public void setConnectT(ConnectT connectT) {
		this.connectT = connectT;
	}

	public CustomerMasterT getCustomerMasterT() {
		return this.customerMasterT;
	}

	public void setCustomerMasterT(CustomerMasterT customerMasterT) {
		this.customerMasterT = customerMasterT;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

	public PartnerMasterT getPartnerMasterT() {
		return this.partnerMasterT;
	}

	public void setPartnerMasterT(PartnerMasterT partnerMasterT) {
		this.partnerMasterT = partnerMasterT;
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