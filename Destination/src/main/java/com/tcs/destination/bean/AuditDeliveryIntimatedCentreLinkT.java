package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_delivery_intimated_centre_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "auditDeliveryIntimatedCentreLinkId")
@Entity
@Table(name="audit_delivery_intimated_centre_link_t")
@NamedQuery(name="AuditDeliveryIntimatedCentreLinkT.findAll", query="SELECT a FROM AuditDeliveryIntimatedCentreLinkT a")
public class AuditDeliveryIntimatedCentreLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_delivery_intimated_centre_link_id")
	private Integer auditDeliveryIntimatedCentreLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="delivery_centre_id")
	private Integer deliveryCentreId;

	@Column(name="delivery_intimated_id")
	private String deliveryIntimatedId;

	private Boolean notified;

	@Column(name="operation_type")
	private Integer operationType;

	public AuditDeliveryIntimatedCentreLinkT() {
	}

	public Integer getAuditDeliveryIntimatedCentreLinkId() {
		return this.auditDeliveryIntimatedCentreLinkId;
	}

	public void setAuditDeliveryIntimatedCentreLinkId(Integer auditDeliveryIntimatedCentreLinkId) {
		this.auditDeliveryIntimatedCentreLinkId = auditDeliveryIntimatedCentreLinkId;
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

	public Integer getDeliveryCentreId() {
		return this.deliveryCentreId;
	}

	public void setDeliveryCentreId(Integer deliveryCentreId) {
		this.deliveryCentreId = deliveryCentreId;
	}

	public String getDeliveryIntimatedId() {
		return this.deliveryIntimatedId;
	}

	public void setDeliveryIntimatedId(String deliveryIntimatedId) {
		this.deliveryIntimatedId = deliveryIntimatedId;
	}

	public Boolean getNotified() {
		return this.notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

}