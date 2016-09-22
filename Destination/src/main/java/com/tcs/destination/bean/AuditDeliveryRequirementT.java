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

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="new_competency_area")
	private String newCompetencyArea;

	@Column(name="new_delivery_rgs_id")
	private String newDeliveryRgsId;

	@Column(name="new_employee_id")
	private String newEmployeeId;

	@Column(name="new_employee_name")
	private String newEmployeeName;

	@Column(name="new_experience")
	private String newExperience;

	@Column(name="new_location")
	private String newLocation;

	@Column(name="new_role")
	private String newRole;

	@Column(name="new_site")
	private String newSite;

	@Column(name="new_status")
	private String newStatus;

	@Column(name="new_sub_competency_area")
	private String newSubCompetencyArea;

	@Column(name="old_competency_area")
	private String oldCompetencyArea;

	@Column(name="old_delivery_rgs_id")
	private String oldDeliveryRgsId;

	@Column(name="old_employee_id")
	private String oldEmployeeId;

	@Column(name="old_employee_name")
	private String oldEmployeeName;

	@Column(name="old_experience")
	private String oldExperience;

	@Column(name="old_location")
	private String oldLocation;

	@Column(name="old_role")
	private String oldRole;

	@Column(name="old_site")
	private String oldSite;

	@Column(name="old_status")
	private String oldStatus;

	@Column(name="old_sub_competency_area")
	private String oldSubCompetencyArea;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="requirement_id")
	private String requirementId;

	public AuditDeliveryRequirementT() {
	}

	public Integer getAuditDeliveryRequirementId() {
		return this.auditDeliveryRequirementId;
	}

	public void setAuditDeliveryRequirementId(Integer auditDeliveryRequirementId) {
		this.auditDeliveryRequirementId = auditDeliveryRequirementId;
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

	public String getNewCompetencyArea() {
		return this.newCompetencyArea;
	}

	public void setNewCompetencyArea(String newCompetencyArea) {
		this.newCompetencyArea = newCompetencyArea;
	}

	public String getNewDeliveryRgsId() {
		return this.newDeliveryRgsId;
	}

	public void setNewDeliveryRgsId(String newDeliveryRgsId) {
		this.newDeliveryRgsId = newDeliveryRgsId;
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

	public String getNewExperience() {
		return this.newExperience;
	}

	public void setNewExperience(String newExperience) {
		this.newExperience = newExperience;
	}

	public String getNewLocation() {
		return this.newLocation;
	}

	public void setNewLocation(String newLocation) {
		this.newLocation = newLocation;
	}

	public String getNewRole() {
		return this.newRole;
	}

	public void setNewRole(String newRole) {
		this.newRole = newRole;
	}

	public String getNewSite() {
		return this.newSite;
	}

	public void setNewSite(String newSite) {
		this.newSite = newSite;
	}

	public String getNewStatus() {
		return this.newStatus;
	}

	public void setNewStatus(String newStatus) {
		this.newStatus = newStatus;
	}

	public String getNewSubCompetencyArea() {
		return this.newSubCompetencyArea;
	}

	public void setNewSubCompetencyArea(String newSubCompetencyArea) {
		this.newSubCompetencyArea = newSubCompetencyArea;
	}

	public String getOldCompetencyArea() {
		return this.oldCompetencyArea;
	}

	public void setOldCompetencyArea(String oldCompetencyArea) {
		this.oldCompetencyArea = oldCompetencyArea;
	}

	public String getOldDeliveryRgsId() {
		return this.oldDeliveryRgsId;
	}

	public void setOldDeliveryRgsId(String oldDeliveryRgsId) {
		this.oldDeliveryRgsId = oldDeliveryRgsId;
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

	public String getOldExperience() {
		return this.oldExperience;
	}

	public void setOldExperience(String oldExperience) {
		this.oldExperience = oldExperience;
	}

	public String getOldLocation() {
		return this.oldLocation;
	}

	public void setOldLocation(String oldLocation) {
		this.oldLocation = oldLocation;
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

	public String getOldStatus() {
		return this.oldStatus;
	}

	public void setOldStatus(String oldStatus) {
		this.oldStatus = oldStatus;
	}

	public String getOldSubCompetencyArea() {
		return this.oldSubCompetencyArea;
	}

	public void setOldSubCompetencyArea(String oldSubCompetencyArea) {
		this.oldSubCompetencyArea = oldSubCompetencyArea;
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

}