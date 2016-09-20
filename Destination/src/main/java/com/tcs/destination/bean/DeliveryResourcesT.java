package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the delivery_resources_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "deliveryResourcesId")
@Entity
@Table(name="delivery_resources_t")
@NamedQuery(name="DeliveryResourcesT.findAll", query="SELECT d FROM DeliveryResourcesT d")
public class DeliveryResourcesT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="delivery_resources_id")
	private String deliveryResourcesId;

	@Column(name="created_by")
	private String createdBy;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="modified_by")
	private String modifiedBy;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;

	@Column(name="requirement_fulfillment")
	private String requirementFulfillment;

	private String role;

	private String skill;
	
	@Column(name="delivery_master_id")
	private Integer delievryMasterId;

	//bi-directional many-to-one association to DeliveryMasterT
	@ManyToOne
	@JoinColumn(name="delivery_master_id", insertable = false, updatable = false)
	private DeliveryMasterT deliveryMasterT;
	
	@Column(name="delivery_rgs_id")
	private String rgsId;
	
	// bi-directional many-to-one association to DeliveryRgsT
	@ManyToOne
	@JoinColumn(name = "delivery_rgs_id", insertable = false, updatable = false)
	private DeliveryRgsT deliveryRgsT;
	
	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "created_by", insertable = false, updatable = false)
	private UserT createdByUser;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;

	public DeliveryResourcesT() {
	}

	public String getDeliveryResourcesId() {
		return this.deliveryResourcesId;
	}

	public void setDeliveryResourcesId(String deliveryResourcesId) {
		this.deliveryResourcesId = deliveryResourcesId;
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

	public String getRequirementFulfillment() {
		return this.requirementFulfillment;
	}

	public void setRequirementFulfillment(String requirementFulfillment) {
		this.requirementFulfillment = requirementFulfillment;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getSkill() {
		return this.skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public DeliveryMasterT getDeliveryMasterT() {
		return this.deliveryMasterT;
	}

	public void setDeliveryMasterT(DeliveryMasterT deliveryMasterT) {
		this.deliveryMasterT = deliveryMasterT;
	}

	public Integer getDelievryMasterId() {
		return delievryMasterId;
	}

	public void setDelievryMasterId(Integer delievryMasterId) {
		this.delievryMasterId = delievryMasterId;
	}
	
	public String getRgsId() {
		return this.rgsId;
	}

	public void setRgsId(String rgsId) {
		this.rgsId = rgsId;
	}

	public DeliveryRgsT getDeliveryRgsT() {
		return deliveryRgsT;
	}

	public void setDeliveryRgsT(DeliveryRgsT deliveryRgsT) {
		this.deliveryRgsT = deliveryRgsT;
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