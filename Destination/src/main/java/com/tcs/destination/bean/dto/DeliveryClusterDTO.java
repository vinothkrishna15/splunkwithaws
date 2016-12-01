package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;


/**
 * The DeliveryClusterDTO.
 * 
 */
@JsonFilter(Constants.FILTER)
public class DeliveryClusterDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer deliveryClusterId;
	private String deliveryCluster;
	private String active;

	private String deliveryClusterHead;
	private UserDTO deliveryClusterHeadUser;

	private Timestamp createdDatetime;
	private Timestamp modifiedDatetime;
	private UserDTO createdByUser;
	private UserDTO modifiedByUser;
	
	public DeliveryClusterDTO() {
		super();
	}

	public Integer getDeliveryClusterId() {
		return deliveryClusterId;
	}

	public void setDeliveryClusterId(Integer deliveryClusterId) {
		this.deliveryClusterId = deliveryClusterId;
	}

	public String getDeliveryCluster() {
		return deliveryCluster;
	}

	public void setDeliveryCluster(String deliveryCluster) {
		this.deliveryCluster = deliveryCluster;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getDeliveryClusterHead() {
		return deliveryClusterHead;
	}

	public void setDeliveryClusterHead(String deliveryClusterHead) {
		this.deliveryClusterHead = deliveryClusterHead;
	}

	public UserDTO getDeliveryClusterHeadUser() {
		return deliveryClusterHeadUser;
	}

	public void setDeliveryClusterHeadUser(UserDTO deliveryClusterHeadUser) {
		this.deliveryClusterHeadUser = deliveryClusterHeadUser;
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
	
}