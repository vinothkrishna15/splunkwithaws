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

	@Column(name="new_requirement_fulfillment")
	private String newRequirementFulfillment;

	@Column(name="new_rgs_id")
	private String newRgsId;

	@Column(name="old_delivery_master_id")
	private Integer oldDeliveryMasterId;

	@Column(name="old_delivery_resource_id")
	private Integer oldDeliveryResourceId;

	@Column(name="old_experience")
	private String oldExperience;

	@Column(name="old_requirement_fulfillment")
	private String oldRequirementFulfillment;

	@Column(name="old_rgs_id")
	private String oldRgsId;

	@Column(name="old_role")
	private String oldRole;

	@Column(name="old_site")
	private String oldSite;

	@Column(name="old_skill")
	private String oldSkill;

	@Column(name="operation_type")
	private Integer operationType;

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

	public String getNewRequirementFulfillment() {
		return this.newRequirementFulfillment;
	}

	public void setNewRequirementFulfillment(String newRequirementFulfillment) {
		this.newRequirementFulfillment = newRequirementFulfillment;
	}

	public String getNewRgsId() {
		return this.newRgsId;
	}

	public void setNewRgsId(String newRgsId) {
		this.newRgsId = newRgsId;
	}

	public Integer getOldDeliveryMasterId() {
		return this.oldDeliveryMasterId;
	}

	public void setOldDeliveryMasterId(Integer oldDeliveryMasterId) {
		this.oldDeliveryMasterId = oldDeliveryMasterId;
	}

	public Integer getOldDeliveryResourceId() {
		return this.oldDeliveryResourceId;
	}

	public void setOldDeliveryResourceId(Integer oldDeliveryResourceId) {
		this.oldDeliveryResourceId = oldDeliveryResourceId;
	}

	public String getOldExperience() {
		return this.oldExperience;
	}

	public void setOldExperience(String oldExperience) {
		this.oldExperience = oldExperience;
	}

	public String getOldRequirementFulfillment() {
		return this.oldRequirementFulfillment;
	}

	public void setOldRequirementFulfillment(String oldRequirementFulfillment) {
		this.oldRequirementFulfillment = oldRequirementFulfillment;
	}

	public String getOldRgsId() {
		return this.oldRgsId;
	}

	public void setOldRgsId(String oldRgsId) {
		this.oldRgsId = oldRgsId;
	}

	public String getOldRole() {
		return this.oldRole;
	}

	public void setOldRole(String oldRole) {
		this.oldRole = oldRole;
	}

	public String getOldSite() {
		return this.oldSite;
	}

	public void setOldSite(String oldSite) {
		this.oldSite = oldSite;
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