package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the opportunity_delivery_owner_centre_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "opportunityDeliveryOwnerCentreId")
@Entity
@Table(name="opportunity_delivery_owner_centre_mapping_t")
@NamedQuery(name="OpportunityDeliveryOwnerCentreMappingT.findAll", query="SELECT o FROM OpportunityDeliveryOwnerCentreMappingT o")
public class OpportunityDeliveryOwnerCentreMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="opportunity_delivery_owner_centre_id")
	private Integer opportunityDeliveryOwnerCentreId;

	private String reason;
	
	@Column(name = "opportunity_id")
	private String opportunityId;

	//bi-directional many-to-one association to DeliveryCentreT
	@ManyToOne
	@JoinColumn(name="delivery_centre_id")
	private DeliveryCentreT deliveryCentreT;

	//bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name="opportunity_id", insertable = false, updatable = false)
	private OpportunityT opportunityT;

	public OpportunityDeliveryOwnerCentreMappingT() {
	}

	public Integer getOpportunityDeliveryOwnerCentreId() {
		return this.opportunityDeliveryOwnerCentreId;
	}

	public void setOpportunityDeliveryOwnerCentreId(Integer opportunityDeliveryOwnerCentreId) {
		this.opportunityDeliveryOwnerCentreId = opportunityDeliveryOwnerCentreId;
	}

	public String getReason() {
		return this.reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public DeliveryCentreT getDeliveryCentreT() {
		return this.deliveryCentreT;
	}

	public void setDeliveryCentreT(DeliveryCentreT deliveryCentreT) {
		this.deliveryCentreT = deliveryCentreT;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

}