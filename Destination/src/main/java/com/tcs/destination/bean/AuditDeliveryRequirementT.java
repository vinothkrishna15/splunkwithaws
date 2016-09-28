package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_delivery_requirement_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "auditDeliveryRequirementId")
@Entity
@Table(name="audit_delivery_requirement_t")
@NamedQuery(name="AuditDeliveryRequirementT.findAll", query="SELECT a FROM AuditDeliveryRequirementT a")
public class AuditDeliveryRequirementT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_delivery_requirement_id")
	private Integer auditDeliveryRequirementId;

	@Column(name="competency_area")
	private String competencyArea;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="delivery_rgs_id")
	private String deliveryRgsId;

	private String experience;

	private String location;

	@Column(name="new_employee_id")
	private String newEmployeeId;

	@Column(name="new_employee_name")
	private String newEmployeeName;

	@Column(name="old_employee_id")
	private String oldEmployeeId;

	@Column(name="old_employee_name")
	private String oldEmployeeName;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="requirement_id")
	private String requirementId;

	private String role;

	private String site;

	private String status;

	@Column(name="sub_competency_area")
	private String subCompetencyArea;

	public AuditDeliveryRequirementT() {
	}

	public Integer getAuditDeliveryRequirementId() {
		return this.auditDeliveryRequirementId;
	}

	public void setAuditDeliveryRequirementId(Integer auditDeliveryRequirementId) {
		this.auditDeliveryRequirementId = auditDeliveryRequirementId;
	}

	public String getCompetencyArea() {
		return this.competencyArea;
	}

	public void setCompetencyArea(String competencyArea) {
		this.competencyArea = competencyArea;
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

	public String getDeliveryRgsId() {
		return this.deliveryRgsId;
	}

	public void setDeliveryRgsId(String deliveryRgsId) {
		this.deliveryRgsId = deliveryRgsId;
	}

	public String getExperience() {
		return this.experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getNewEmployeeId() {
		return this.newEmployeeId;
	}

	public void setNewEmployeeId(String newEmployeeId) {
		this.newEmployeeId = newEmployeeId;
	}

	public String getNewEmployeeName() {
		return this.newEmployeeName;
	}

	public void setNewEmployeeName(String newEmployeeName) {
		this.newEmployeeName = newEmployeeName;
	}

	public String getOldEmployeeId() {
		return this.oldEmployeeId;
	}

	public void setOldEmployeeId(String oldEmployeeId) {
		this.oldEmployeeId = oldEmployeeId;
	}

	public String getOldEmployeeName() {
		return this.oldEmployeeName;
	}

	public void setOldEmployeeName(String oldEmployeeName) {
		this.oldEmployeeName = oldEmployeeName;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public String getRequirementId() {
		return this.requirementId;
	}

	public void setRequirementId(String requirementId) {
		this.requirementId = requirementId;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getSite() {
		return this.site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubCompetencyArea() {
		return this.subCompetencyArea;
	}

	public void setSubCompetencyArea(String subCompetencyArea) {
		this.subCompetencyArea = subCompetencyArea;
	}

}