package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_delivery_intimated_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "auditDeliveryIntimatedId")
@Entity
@Table(name="audit_delivery_intimated_t")
@NamedQuery(name="AuditDeliveryIntimatedT.findAll", query="SELECT a FROM AuditDeliveryIntimatedT a")
public class AuditDeliveryIntimatedT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_delivery_intimated_id")
	private Integer auditDeliveryIntimatedId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="delivery_intimated_id")
	private String deliveryIntimatedId;

	@Column(name="new_delivery_stage")
	private Integer newDeliveryStage;

	@Column(name="new_reject_comments")
	private String newRejectComments;

	@Column(name="new_reject_reason")
	private String newRejectReason;

	private Boolean notified;

	@Column(name="old_delivery_stage")
	private Integer oldDeliveryStage;

	@Column(name="old_reject_comments")
	private String oldRejectComments;

	@Column(name="old_reject_reason")
	private String oldRejectReason;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="opportunity_id")
	private String opportunityId;

	public AuditDeliveryIntimatedT() {
	}

	public Integer getAuditDeliveryIntimatedId() {
		return this.auditDeliveryIntimatedId;
	}

	public void setAuditDeliveryIntimatedId(Integer auditDeliveryIntimatedId) {
		this.auditDeliveryIntimatedId = auditDeliveryIntimatedId;
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

	public String getDeliveryIntimatedId() {
		return this.deliveryIntimatedId;
	}

	public void setDeliveryIntimatedId(String deliveryIntimatedId) {
		this.deliveryIntimatedId = deliveryIntimatedId;
	}

	public Integer getNewDeliveryStage() {
		return this.newDeliveryStage;
	}

	public void setNewDeliveryStage(Integer newDeliveryStage) {
		this.newDeliveryStage = newDeliveryStage;
	}

	public String getNewRejectComments() {
		return this.newRejectComments;
	}

	public void setNewRejectComments(String newRejectComments) {
		this.newRejectComments = newRejectComments;
	}

	public String getNewRejectReason() {
		return this.newRejectReason;
	}

	public void setNewRejectReason(String newRejectReason) {
		this.newRejectReason = newRejectReason;
	}

	public Boolean getNotified() {
		return this.notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public Integer getOldDeliveryStage() {
		return this.oldDeliveryStage;
	}

	public void setOldDeliveryStage(Integer oldDeliveryStage) {
		this.oldDeliveryStage = oldDeliveryStage;
	}

	public String getOldRejectComments() {
		return this.oldRejectComments;
	}

	public void setOldRejectComments(String oldRejectComments) {
		this.oldRejectComments = oldRejectComments;
	}

	public String getOldRejectReason() {
		return this.oldRejectReason;
	}

	public void setOldRejectReason(String oldRejectReason) {
		this.oldRejectReason = oldRejectReason;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public String getOpportunityId() {
		return this.opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

}