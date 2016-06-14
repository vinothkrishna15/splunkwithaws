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
 * The persistent class for the delivery_centre_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "deliveryCentreId")
@Entity
@Table(name="delivery_centre_t")
@NamedQuery(name="DeliveryCentreT.findAll", query="SELECT d FROM DeliveryCentreT d")
public class DeliveryCentreT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="delivery_centre_id")
	private Integer deliveryCentreId;

	private String active;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="delivery_centre")
	private String deliveryCentre;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by")
	private UserT userT1;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="modified_by")
	private UserT userT2;

	//bi-directional many-to-one association to OpportunityDeliveryOwnerCentreMappingT
	@OneToMany(mappedBy="deliveryCentreT")
	private List<OpportunityDeliveryOwnerCentreMappingT> opportunityDeliveryOwnerCentreMappingTs;

	public DeliveryCentreT() {
	}

	public Integer getDeliveryCentreId() {
		return this.deliveryCentreId;
	}

	public void setDeliveryCentreId(Integer deliveryCentreId) {
		this.deliveryCentreId = deliveryCentreId;
	}

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public String getDeliveryCentre() {
		return this.deliveryCentre;
	}

	public void setDeliveryCentre(String deliveryCentre) {
		this.deliveryCentre = deliveryCentre;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public UserT getUserT1() {
		return this.userT1;
	}

	public void setUserT1(UserT userT1) {
		this.userT1 = userT1;
	}

	public UserT getUserT2() {
		return this.userT2;
	}

	public void setUserT2(UserT userT2) {
		this.userT2 = userT2;
	}

	public List<OpportunityDeliveryOwnerCentreMappingT> getOpportunityDeliveryOwnerCentreMappingTs() {
		return this.opportunityDeliveryOwnerCentreMappingTs;
	}

	public void setOpportunityDeliveryOwnerCentreMappingTs(List<OpportunityDeliveryOwnerCentreMappingT> opportunityDeliveryOwnerCentreMappingTs) {
		this.opportunityDeliveryOwnerCentreMappingTs = opportunityDeliveryOwnerCentreMappingTs;
	}

	public OpportunityDeliveryOwnerCentreMappingT addOpportunityDeliveryOwnerCentreMappingT(OpportunityDeliveryOwnerCentreMappingT opportunityDeliveryOwnerCentreMappingT) {
		getOpportunityDeliveryOwnerCentreMappingTs().add(opportunityDeliveryOwnerCentreMappingT);
		opportunityDeliveryOwnerCentreMappingT.setDeliveryCentreT(this);

		return opportunityDeliveryOwnerCentreMappingT;
	}

	public OpportunityDeliveryOwnerCentreMappingT removeOpportunityDeliveryOwnerCentreMappingT(OpportunityDeliveryOwnerCentreMappingT opportunityDeliveryOwnerCentreMappingT) {
		getOpportunityDeliveryOwnerCentreMappingTs().remove(opportunityDeliveryOwnerCentreMappingT);
		opportunityDeliveryOwnerCentreMappingT.setDeliveryCentreT(null);

		return opportunityDeliveryOwnerCentreMappingT;
	}

}