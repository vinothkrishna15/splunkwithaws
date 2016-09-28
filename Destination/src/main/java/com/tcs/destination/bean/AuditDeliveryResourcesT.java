package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_delivery_resources_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "auditDeliveryResourceId")
@Entity
@Table(name="audit_delivery_resources_t")
@NamedQuery(name="AuditDeliveryResourcesT.findAll", query="SELECT a FROM AuditDeliveryResourcesT a")
public class AuditDeliveryResourcesT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_delivery_resource_id")
	private Integer auditDeliveryResourceId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="delivery_master_id")
	private String deliveryMasterId;

	@Column(name="delivery_resource_id")
	private Integer deliveryResourceId;

	@Column(name="new_delivery_rgs_id")
	private String newDeliveryRgsId;

	@Column(name="new_requirement_fulfillment")
	private String newRequirementFulfillment;

	@Column(name="new_role")
	private String newRole;

	@Column(name="new_skill")
	private String newSkill;

	@Column(name="old_delivery_rgs_id")
	private String oldDeliveryRgsId;

	@Column(name="old_requirement_fulfillment")
	private String oldRequirementFulfillment;

	@Column(name="old_role")
	private String oldRole;

	@Column(name="old_skill")
	private String oldSkill;

	@Column(name="operation_type")
	private Integer operationType;
	
	private Boolean notified;

	public Boolean getNotified() {
		return this.notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public AuditDeliveryResourcesT() {
	}

	public Integer getAuditDeliveryResourceId() {
		return this.auditDeliveryResourceId;
	}

	public void setAuditDeliveryResourceId(Integer auditDeliveryResourceId) {
		this.auditDeliveryResourceId = auditDeliveryResourceId;
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

	public String getDeliveryMasterId() {
		return this.deliveryMasterId;
	}

	public void setDeliveryMasterId(String deliveryMasterId) {
		this.deliveryMasterId = deliveryMasterId;
	}

	public Integer getDeliveryResourceId() {
		return this.deliveryResourceId;
	}

	public void setDeliveryResourceId(Integer deliveryResourceId) {
		this.deliveryResourceId = deliveryResourceId;
	}

	public String getNewDeliveryRgsId() {
		return this.newDeliveryRgsId;
	}

	public void setNewDeliveryRgsId(String newDeliveryRgsId) {
		this.newDeliveryRgsId = newDeliveryRgsId;
	}

	public String getNewRequirementFulfillment() {
		return this.newRequirementFulfillment;
	}

	public void setNewRequirementFulfillment(String newRequirementFulfillment) {
		this.newRequirementFulfillment = newRequirementFulfillment;
	}

	public String getNewRole() {
		return this.newRole;
	}

	public void setNewRole(String newRole) {
		this.newRole = newRole;
	}

	public String getNewSkill() {
		return this.newSkill;
	}

	public void setNewSkill(String newSkill) {
		this.newSkill = newSkill;
	}

	public String getOldDeliveryRgsId() {
		return this.oldDeliveryRgsId;
	}

	public void setOldDeliveryRgsId(String oldDeliveryRgsId) {
		this.oldDeliveryRgsId = oldDeliveryRgsId;
	}

	public String getOldRequirementFulfillment() {
		return this.oldRequirementFulfillment;
	}

	public void setOldRequirementFulfillment(String oldRequirementFulfillment) {
		this.oldRequirementFulfillment = oldRequirementFulfillment;
	}

	public String getOldRole() {
		return this.oldRole;
	}

	public void setOldRole(String oldRole) {
		this.oldRole = oldRole;
	}

	public String getOldSkill() {
		return this.oldSkill;
	}

	public void setOldSkill(String oldSkill) {
		this.oldSkill = oldSkill;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

}