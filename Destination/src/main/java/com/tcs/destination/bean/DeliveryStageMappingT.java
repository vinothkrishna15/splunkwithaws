package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.util.List;


/**
 * The persistent class for the delivery_stage_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "stage", scope=DeliveryMasterT.class)
@Entity
@Table(name="delivery_stage_mapping_t")
@NamedQuery(name="DeliveryStageMappingT.findAll", query="SELECT d FROM DeliveryStageMappingT d")
public class DeliveryStageMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer stage;

	private String description;

	//bi-directional many-to-one association to DeliveryMasterT
	@OneToMany(mappedBy="deliveryStageMappingT")
	private List<DeliveryMasterT> deliveryMasterTs;

	public DeliveryStageMappingT() {
	}

	public Integer getStage() {
		return this.stage;
	}

	public void setStage(Integer stage) {
		this.stage = stage;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<DeliveryMasterT> getDeliveryMasterTs() {
		return this.deliveryMasterTs;
	}

	public void setDeliveryMasterTs(List<DeliveryMasterT> deliveryMasterTs) {
		this.deliveryMasterTs = deliveryMasterTs;
	}

	public DeliveryMasterT addDeliveryMasterT(DeliveryMasterT deliveryMasterT) {
		getDeliveryMasterTs().add(deliveryMasterT);
		deliveryMasterT.setDeliveryStageMappingT(this);

		return deliveryMasterT;
	}

	public DeliveryMasterT removeDeliveryMasterT(DeliveryMasterT deliveryMasterT) {
		getDeliveryMasterTs().remove(deliveryMasterT);
		deliveryMasterT.setDeliveryStageMappingT(null);

		return deliveryMasterT;
	}

}