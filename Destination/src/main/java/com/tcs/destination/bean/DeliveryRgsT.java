package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the delivery_rgs_t database table.
 * 
 */
@Entity
@Table(name="delivery_rgs_t")
@NamedQuery(name="DeliveryRgsT.findAll", query="SELECT d FROM DeliveryRgsT d")
public class DeliveryRgsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="delivery_rgs_id")
	private String deliveryRgsId;

	//bi-directional many-to-one association to DeliveryRequirementT
	@OneToMany(mappedBy="deliveryRgsT")
	private List<DeliveryRequirementT> deliveryRequirementTs;

	//bi-directional many-to-one association to DeliveryResourcesT
	@OneToMany(mappedBy="deliveryRgsT")
	private List<DeliveryResourcesT> deliveryResourcesTs;

	public DeliveryRgsT() {
	}

	public String getDeliveryRgsId() {
		return this.deliveryRgsId;
	}

	public void setDeliveryRgsId(String deliveryRgsId) {
		this.deliveryRgsId = deliveryRgsId;
	}

	public List<DeliveryRequirementT> getDeliveryRequirementTs() {
		return this.deliveryRequirementTs;
	}

	public void setDeliveryRequirementTs(List<DeliveryRequirementT> deliveryRequirementTs) {
		this.deliveryRequirementTs = deliveryRequirementTs;
	}

	
	public List<DeliveryResourcesT> getDeliveryResourcesTs() {
		return this.deliveryResourcesTs;
	}

	public void setDeliveryResourcesTs(List<DeliveryResourcesT> deliveryResourcesTs) {
		this.deliveryResourcesTs = deliveryResourcesTs;
	}
}