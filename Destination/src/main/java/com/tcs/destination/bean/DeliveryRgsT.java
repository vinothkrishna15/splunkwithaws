package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the delivery_rgs_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "deliveryRgsId")
@Entity
@Table(name="delivery_rgs_t")
@NamedQuery(name="DeliveryRgsT.findAll", query="SELECT d FROM DeliveryRgsT d")
public class DeliveryRgsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="delivery_rgs_id")
	private String deliveryRgsId;

	@Column(name="competency_area")
	private String competencyArea;

	@Column(name="created_by")
	private String createdBy;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="employee_id")
	private String employeeId;

	@Column(name="employee_name")
	private String employeeName;

	private String experience;

	private String location;

	@Column(name="modified_by")
	private String modifiedBy;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;

	@Column(name="requirement_id")
	private String requirementId;

	private String role;

	private String site;

	private String status;

	@Column(name="sub_competency_area")
	private String subCompetencyArea;

	//bi-directional many-to-one association to DeliveryResourcesT
	@OneToMany(mappedBy="deliveryRgsT")
	private List<DeliveryResourcesT> deliveryResourcesTs;
	
	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "created_by", insertable = false, updatable = false)
	private UserT createdByUser;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;

	public DeliveryRgsT() {
	}

	public String getDeliveryRgsId() {
		return this.deliveryRgsId;
	}

	public void setDeliveryRgsId(String deliveryRgsId) {
		this.deliveryRgsId = deliveryRgsId;
	}

	public String getCompetencyArea() {
		return this.competencyArea;
	}

	public void setCompetencyArea(String competencyArea) {
		this.competencyArea = competencyArea;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public String getEmployeeId() {
		return this.employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return this.employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
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

	public String getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
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

	public List<DeliveryResourcesT> getDeliveryResourcesTs() {
		return this.deliveryResourcesTs;
	}

	public void setDeliveryResourcesTs(List<DeliveryResourcesT> deliveryResourcesTs) {
		this.deliveryResourcesTs = deliveryResourcesTs;
	}

	public DeliveryResourcesT addDeliveryResourcesT(DeliveryResourcesT deliveryResourcesT) {
		getDeliveryResourcesTs().add(deliveryResourcesT);
		deliveryResourcesT.setDeliveryRgsT(this);

		return deliveryResourcesT;
	}

	public DeliveryResourcesT removeDeliveryResourcesT(DeliveryResourcesT deliveryResourcesT) {
		getDeliveryResourcesTs().remove(deliveryResourcesT);
		deliveryResourcesT.setDeliveryRgsT(null);

		return deliveryResourcesT;
	}

	public UserT getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserT createdByUser) {
		this.createdByUser = createdByUser;
	}

	public UserT getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}
	

}