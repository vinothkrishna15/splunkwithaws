package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the delivery_intimated_centre_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "deliveryIntimatedCentreLinkId",scope = DeliveryIntimatedCentreLinkT.class)
@Entity
@Table(name="delivery_intimated_centre_link_t")
@NamedQuery(name="DeliveryIntimatedCentreLinkT.findAll", query="SELECT d FROM DeliveryIntimatedCentreLinkT d")
public class DeliveryIntimatedCentreLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="delivery_intimated_centre_link_id")
	private String deliveryIntimatedCentreLinkId;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="delivery_centre_id")
	private Integer deliveryCentreId;
	
	@Column(name="delivery_intimated_id")
	private String deliveryIntimatedId;

	//bi-directional many-to-one association to DeliveryCentreT
	@ManyToOne
	@JoinColumn(name="delivery_centre_id", insertable = false, updatable = false)
	private DeliveryCentreT deliveryCentreT;

	//bi-directional many-to-one association to DeliveryIntimatedT
	@ManyToOne
	@JoinColumn(name="delivery_intimated_id", insertable = false, updatable = false)
	private DeliveryIntimatedT deliveryIntimatedT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by", insertable = false, updatable = false)
	private UserT createdByUser;

	public DeliveryIntimatedCentreLinkT() {
	}

	public String getDeliveryIntimatedCentreLinkId() {
		return this.deliveryIntimatedCentreLinkId;
	}

	public void setDeliveryIntimatedCentreLinkId(String deliveryIntimatedCentreLinkId) {
		this.deliveryIntimatedCentreLinkId = deliveryIntimatedCentreLinkId;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public DeliveryCentreT getDeliveryCentreT() {
		return this.deliveryCentreT;
	}

	public void setDeliveryCentreT(DeliveryCentreT deliveryCentreT) {
		this.deliveryCentreT = deliveryCentreT;
	}

	public DeliveryIntimatedT getDeliveryIntimatedT() {
		return this.deliveryIntimatedT;
	}

	public void setDeliveryIntimatedT(DeliveryIntimatedT deliveryIntimatedT) {
		this.deliveryIntimatedT = deliveryIntimatedT;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public UserT getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserT createdByUser) {
		this.createdByUser = createdByUser;
	}

	public Integer getDeliveryCentreId() {
		return deliveryCentreId;
	}

	public void setDeliveryCentreId(Integer deliveryCentreId) {
		this.deliveryCentreId = deliveryCentreId;
	}

	public String getDeliveryIntimatedId() {
		return deliveryIntimatedId;
	}

	public void setDeliveryIntimatedId(String deliveryIntimatedId) {
		this.deliveryIntimatedId = deliveryIntimatedId;
	}

}