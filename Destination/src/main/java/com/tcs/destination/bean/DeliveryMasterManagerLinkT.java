package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the delivery_master_manager_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "deliveryMasterManagerLinkId")
@Entity
@Table(name="delivery_master_manager_link_t")
@NamedQuery(name="DeliveryMasterManagerLinkT.findAll", query="SELECT d FROM DeliveryMasterManagerLinkT d")
public class DeliveryMasterManagerLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="delivery_master_manager_link_id")
	private String deliveryMasterManagerLinkId;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;
	
	@Column(name="delivery_master_id")
	private String deliveryMasterId;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="modified_by")
	private String modifiedBy;
	
	@Column(name="delivery_manager_id")
	private String deliveryManagerId;

	//bi-directional many-to-one association to DeliveryMasterT
	@ManyToOne
	@JoinColumn(name="delivery_master_id", insertable = false, updatable = false)
	private DeliveryMasterT deliveryMasterT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by", insertable = false, updatable = false)
	private UserT createdByUser;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="delivery_manager_id", insertable = false, updatable = false)
	private UserT deliveryManager;

	public DeliveryMasterManagerLinkT() {
	}

	public String getDeliveryMasterManagerLinkId() {
		return this.deliveryMasterManagerLinkId;
	}

	public void setDeliveryMasterManagerLinkId(String deliveryMasterManagerLinkId) {
		this.deliveryMasterManagerLinkId = deliveryMasterManagerLinkId;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public DeliveryMasterT getDeliveryMasterT() {
		return this.deliveryMasterT;
	}

	public void setDeliveryMasterT(DeliveryMasterT deliveryMasterT) {
		this.deliveryMasterT = deliveryMasterT;
	}

	public String getDeliveryMasterId() {
		return deliveryMasterId;
	}

	public void setDeliveryMasterId(String deliveryMasterId) {
		this.deliveryMasterId = deliveryMasterId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getDeliveryManagerId() {
		return deliveryManagerId;
	}

	public void setDeliveryManagerId(String deliveryManagerId) {
		this.deliveryManagerId = deliveryManagerId;
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

	public UserT getDeliveryManager() {
		return deliveryManager;
	}

	public void setDeliveryManager(UserT deliveryManager) {
		this.deliveryManager = deliveryManager;
	}
	
	

}