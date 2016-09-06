package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the delivery_type_mapping_t database table.
 * 
 */
@Entity
@Table(name="delivery_type_mapping_t")
@NamedQuery(name="DeliveryTypeMappingT.findAll", query="SELECT d FROM DeliveryTypeMappingT d")
public class DeliveryTypeMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="delivery_type")
	private String deliveryType;

	//bi-directional many-to-one association to OpportunityT
	@OneToMany(mappedBy="deliveryTypeMappingT")
	private List<OpportunityT> opportunityTs;

	public DeliveryTypeMappingT() {
	}

	public String getDeliveryType() {
		return this.deliveryType;
	}

	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}

	public List<OpportunityT> getOpportunityTs() {
		return this.opportunityTs;
	}

	public void setOpportunityTs(List<OpportunityT> opportunityTs) {
		this.opportunityTs = opportunityTs;
	}

	public OpportunityT addOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().add(opportunityT);
		opportunityT.setDeliveryTypeMappingT(this);

		return opportunityT;
	}

	public OpportunityT removeOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().remove(opportunityT);
		opportunityT.setDeliveryTypeMappingT(null);

		return opportunityT;
	}

}