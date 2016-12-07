package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * The persistent class for the delivery_centre_t database table.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class DeliveryCentreDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer deliveryCentreId;
	private String active;
	private String deliveryCentre;
	private Timestamp createdDatetime;
	private Timestamp modifiedDatetime;
	
	private String deliveryCentreHead;
	
	private Integer deliveryClusterId;

	private UserDTO createdByUser;
	private UserDTO modifiedByUser;
	
	private UserDTO deliveryCentreHeadUser;

	private DeliveryClusterDTO deliveryClusterT;

	public DeliveryCentreDTO() {
		super();
	}

	public Integer getDeliveryCentreId() {
		return deliveryCentreId;
	}

	public void setDeliveryCentreId(Integer deliveryCentreId) {
		this.deliveryCentreId = deliveryCentreId;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getDeliveryCentre() {
		return deliveryCentre;
	}

	public void setDeliveryCentre(String deliveryCentre) {
		this.deliveryCentre = deliveryCentre;
	}

	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public Timestamp getModifiedDatetime() {
		return modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public String getDeliveryCentreHead() {
		return deliveryCentreHead;
	}

	public void setDeliveryCentreHead(String deliveryCentreHead) {
		this.deliveryCentreHead = deliveryCentreHead;
	}

	public Integer getDeliveryClusterId() {
		return deliveryClusterId;
	}

	public void setDeliveryClusterId(Integer deliveryClusterId) {
		this.deliveryClusterId = deliveryClusterId;
	}

	public UserDTO getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserDTO createdByUser) {
		this.createdByUser = createdByUser;
	}

	public UserDTO getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserDTO modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}

	public UserDTO getDeliveryCentreHeadUser() {
		return deliveryCentreHeadUser;
	}

	public void setDeliveryCentreHeadUser(UserDTO deliveryCentreHeadUser) {
		this.deliveryCentreHeadUser = deliveryCentreHeadUser;
	}

	public DeliveryClusterDTO getDeliveryClusterT() {
		return deliveryClusterT;
	}

	public void setDeliveryClusterT(DeliveryClusterDTO deliveryClusterT) {
		this.deliveryClusterT = deliveryClusterT;
	}
	
}