package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * The OpportunityDeliveryCentreMappingDTO
 *  
 */
@JsonInclude(Include.NON_NULL)
public class OpportunityDeliveryCentreMappingDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer opportunityDeliveryCentreId;

	private String opportunityId;
	private Integer deliveryCentreId;
	private DeliveryCentreDTO deliveryCentreT;

	private Timestamp createdDatetime;
	private Timestamp modifiedDatetime;
	private UserDTO createdByUser;
	private UserDTO modifiedByUser;

	public OpportunityDeliveryCentreMappingDTO() {
		super();
	}

	public Integer getOpportunityDeliveryCentreId() {
		return opportunityDeliveryCentreId;
	}

	public void setOpportunityDeliveryCentreId(Integer opportunityDeliveryCentreId) {
		this.opportunityDeliveryCentreId = opportunityDeliveryCentreId;
	}

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public Integer getDeliveryCentreId() {
		return deliveryCentreId;
	}

	public void setDeliveryCentreId(Integer deliveryCentreId) {
		this.deliveryCentreId = deliveryCentreId;
	}

	public DeliveryCentreDTO getDeliveryCentreT() {
		return deliveryCentreT;
	}

	public void setDeliveryCentreT(DeliveryCentreDTO deliveryCentreT) {
		this.deliveryCentreT = deliveryCentreT;
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