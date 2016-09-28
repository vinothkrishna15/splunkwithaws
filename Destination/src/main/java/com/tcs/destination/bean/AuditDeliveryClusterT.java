package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_delivery_cluster_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "auditDeliveryClusterId")
@Entity
@Table(name="audit_delivery_cluster_t")
@NamedQuery(name="AuditDeliveryClusterT.findAll", query="SELECT a FROM AuditDeliveryClusterT a")
public class AuditDeliveryClusterT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_delivery_cluster_id")
	private Integer auditDeliveryClusterId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="delivery_cluster")
	private String deliveryCluster;

	@Column(name="delivery_cluster_id")
	private Integer deliveryClusterId;

	@Column(name="new_active")
	private String newActive;

	@Column(name="new_delivery_cluster_head")
	private String newDeliveryClusterHead;

	@Column(name="old_active")
	private String oldActive;

	@Column(name="old_delivery_cluster_head")
	private String oldDeliveryClusterHead;

	@Column(name="operation_type")
	private Integer operationType;
	
	private Boolean notified;

	public Boolean getNotified() {
		return this.notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public AuditDeliveryClusterT() {
	}

	public Integer getAuditDeliveryClusterId() {
		return this.auditDeliveryClusterId;
	}

	public void setAuditDeliveryClusterId(Integer auditDeliveryClusterId) {
		this.auditDeliveryClusterId = auditDeliveryClusterId;
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

	public String getDeliveryCluster() {
		return this.deliveryCluster;
	}

	public void setDeliveryCluster(String deliveryCluster) {
		this.deliveryCluster = deliveryCluster;
	}

	public Integer getDeliveryClusterId() {
		return this.deliveryClusterId;
	}

	public void setDeliveryClusterId(Integer deliveryClusterId) {
		this.deliveryClusterId = deliveryClusterId;
	}

	public String getNewActive() {
		return this.newActive;
	}

	public void setNewActive(String newActive) {
		this.newActive = newActive;
	}

	public String getNewDeliveryClusterHead() {
		return this.newDeliveryClusterHead;
	}

	public void setNewDeliveryClusterHead(String newDeliveryClusterHead) {
		this.newDeliveryClusterHead = newDeliveryClusterHead;
	}

	public String getOldActive() {
		return this.oldActive;
	}

	public void setOldActive(String oldActive) {
		this.oldActive = oldActive;
	}

	public String getOldDeliveryClusterHead() {
		return this.oldDeliveryClusterHead;
	}

	public void setOldDeliveryClusterHead(String oldDeliveryClusterHead) {
		this.oldDeliveryClusterHead = oldDeliveryClusterHead;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

}