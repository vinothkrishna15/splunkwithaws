package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * The DeliveryOwnershipDTO
 *  
 */
@JsonInclude(Include.NON_NULL)
public class DeliveryOwnershipDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer deliveryOwnershipId;
	private String active;
	private Timestamp createdDatetime;
	private Timestamp modifiedDatetime;
	private String ownership;
	//created_by
	private UserDTO userT1;
	//modified_by
	private UserDTO userT2;

	public DeliveryOwnershipDTO() {
		super();
	}

	public Integer getDeliveryOwnershipId() {
		return deliveryOwnershipId;
	}

	public void setDeliveryOwnershipId(Integer deliveryOwnershipId) {
		this.deliveryOwnershipId = deliveryOwnershipId;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
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

	public String getOwnership() {
		return ownership;
	}

	public void setOwnership(String ownership) {
		this.ownership = ownership;
	}

	public UserDTO getUserT1() {
		return userT1;
	}

	public void setUserT1(UserDTO userT1) {
		this.userT1 = userT1;
	}

	public UserDTO getUserT2() {
		return userT2;
	}

	public void setUserT2(UserDTO userT2) {
		this.userT2 = userT2;
	}
	
	



}