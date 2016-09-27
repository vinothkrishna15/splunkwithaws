package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_delivery_master_manager_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "auditDeliveryMasterManagerLinkId")
@Entity
@Table(name="audit_delivery_master_manager_link_t")
@NamedQuery(name="AuditDeliveryMasterManagerLinkT.findAll", query="SELECT a FROM AuditDeliveryMasterManagerLinkT a")
public class AuditDeliveryMasterManagerLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_delivery_master_manager_link_id")
	private String auditDeliveryMasterManagerLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="delivery_manager_id")
	private String deliveryManagerId;

	@Column(name="delivery_master_id")
	private String deliveryMasterId;

	@Column(name="operation_type")
	private Integer operationType;

	public AuditDeliveryMasterManagerLinkT() {
	}

	public String getAuditDeliveryMasterManagerLinkId() {
		return this.auditDeliveryMasterManagerLinkId;
	}

	public void setAuditDeliveryMasterManagerLinkId(String auditDeliveryMasterManagerLinkId) {
		this.auditDeliveryMasterManagerLinkId = auditDeliveryMasterManagerLinkId;
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

	public String getDeliveryManagerId() {
		return this.deliveryManagerId;
	}

	public void setDeliveryManagerId(String deliveryManagerId) {
		this.deliveryManagerId = deliveryManagerId;
	}

	public String getDeliveryMasterId() {
		return this.deliveryMasterId;
	}

	public void setDeliveryMasterId(String deliveryMasterId) {
		this.deliveryMasterId = deliveryMasterId;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

}