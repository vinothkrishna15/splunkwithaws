package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_delivery_centre_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "auditDeliveryCentreId")
@Entity
@Table(name="audit_delivery_centre_t")
@NamedQuery(name="AuditDeliveryCentreT.findAll", query="SELECT a FROM AuditDeliveryCentreT a")
public class AuditDeliveryCentreT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_delivery_centre_id")
	private Integer auditDeliveryCentreId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="delivery_centre")
	private String deliveryCentre;

	@Column(name="delivery_centre_id")
	private Integer deliveryCentreId;

	@Column(name="new_active")
	private String newActive;

	@Column(name="new_delivery_centre_head")
	private String newDeliveryCentreHead;

	@Column(name="new_delivery_cluster_id")
	private Integer newDeliveryClusterId;

	@Column(name="old_active")
	private String oldActive;

	@Column(name="old_delivery_centre_head")
	private String oldDeliveryCentreHead;

	@Column(name="old_delivery_cluster_id")
	private Integer oldDeliveryClusterId;

	@Column(name="operation_type")
	private Integer operationType;
	
	private Boolean notified;

	public Boolean getNotified() {
		return this.notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public AuditDeliveryCentreT() {
	}

	public Integer getAuditDeliveryCentreId() {
		return this.auditDeliveryCentreId;
	}

	public void setAuditDeliveryCentreId(Integer auditDeliveryCentreId) {
		this.auditDeliveryCentreId = auditDeliveryCentreId;
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

	public String getDeliveryCentre() {
		return this.deliveryCentre;
	}

	public void setDeliveryCentre(String deliveryCentre) {
		this.deliveryCentre = deliveryCentre;
	}

	public Integer getDeliveryCentreId() {
		return this.deliveryCentreId;
	}

	public void setDeliveryCentreId(Integer deliveryCentreId) {
		this.deliveryCentreId = deliveryCentreId;
	}

	public String getNewActive() {
		return this.newActive;
	}

	public void setNewActive(String newActive) {
		this.newActive = newActive;
	}

	public String getNewDeliveryCentreHead() {
		return this.newDeliveryCentreHead;
	}

	public void setNewDeliveryCentreHead(String newDeliveryCentreHead) {
		this.newDeliveryCentreHead = newDeliveryCentreHead;
	}

	public Integer getNewDeliveryClusterId() {
		return this.newDeliveryClusterId;
	}

	public void setNewDeliveryClusterId(Integer newDeliveryClusterId) {
		this.newDeliveryClusterId = newDeliveryClusterId;
	}

	public String getOldActive() {
		return this.oldActive;
	}

	public void setOldActive(String oldActive) {
		this.oldActive = oldActive;
	}

	public String getOldDeliveryCentreHead() {
		return this.oldDeliveryCentreHead;
	}

	public void setOldDeliveryCentreHead(String oldDeliveryCentreHead) {
		this.oldDeliveryCentreHead = oldDeliveryCentreHead;
	}

	public Integer getOldDeliveryClusterId() {
		return this.oldDeliveryClusterId;
	}

	public void setOldDeliveryClusterId(Integer oldDeliveryClusterId) {
		this.oldDeliveryClusterId = oldDeliveryClusterId;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

}