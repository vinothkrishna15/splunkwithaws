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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="delivery_centre_id")
	private Integer deliveryCentreId;

	private String active;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="delivery_centre")
	private String deliveryCentre;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="modified_by")
	private String modifiedBy;
	
	@Column(name="delivery_centre_head")
	private String deliveryCentreHead;
	
	@Column(name="delivery_cluster_id")
	private Integer deliveryClusterId;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by", insertable = false, updatable = false)
	private UserT createdByUser;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;
	
	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "delivery_centre_head", insertable = false, updatable = false)
	private UserT deliveryCentreHeadUser;

	//bi-directional many-to-one association to OpportunityDeliveryCentreMappingT
	@OneToMany(mappedBy="deliveryCentreT")
	private List<OpportunityDeliveryCentreMappingT> opportunityDeliveryCentreMappingTs;
	
	// bi-directional many-to-one association to DeliveryMasterT
	@OneToMany(mappedBy = "deliveryCentreT")
	private List<DeliveryMasterT> deliveryMasterTs;
	
	// bi-directional many-to-one association to DeliveryClusterT
	@ManyToOne
	@JoinColumn(name = "delivery_cluster_id", insertable = false, updatable = false)
	private DeliveryClusterT deliveryClusterT;

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

	public List<OpportunityDeliveryCentreMappingT> getOpportunityDeliveryCentreMappingTs() {
		return this.opportunityDeliveryCentreMappingTs;
	}

	public void setOpportunityDeliveryCentreMappingTs(List<OpportunityDeliveryCentreMappingT> opportunityDeliveryCentreMappingTs) {
		this.opportunityDeliveryCentreMappingTs = opportunityDeliveryCentreMappingTs;
	}

	public OpportunityDeliveryCentreMappingT addOpportunityDeliveryCentreMappingT(OpportunityDeliveryCentreMappingT opportunityDeliveryCentreMappingT) {
		getOpportunityDeliveryCentreMappingTs().add(opportunityDeliveryCentreMappingT);
		opportunityDeliveryCentreMappingT.setDeliveryCentreT(this);

		return opportunityDeliveryCentreMappingT;
	}

	public OpportunityDeliveryCentreMappingT removeOpportunityDeliveryCentreMappingT(OpportunityDeliveryCentreMappingT opportunityDeliveryCentreMappingT) {
		getOpportunityDeliveryCentreMappingTs().remove(opportunityDeliveryCentreMappingT);
		opportunityDeliveryCentreMappingT.setDeliveryCentreT(null);

		return opportunityDeliveryCentreMappingT;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getDeliveryCentreHead() {
		return deliveryCentreHead;
	}

	public void setDeliveryCentreHead(String deliveryCentreHead) {
		this.deliveryCentreHead = deliveryCentreHead;
	}

	public UserT getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserT createdByUser) {
		this.createdByUser = createdByUser;
	}

	public UserT getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}

	public UserT getDeliveryCentreHeadUser() {
		return deliveryCentreHeadUser;
	}

	public void setDeliveryCentreHeadUser(UserT deliveryCentreHeadUser) {
		this.deliveryCentreHeadUser = deliveryCentreHeadUser;
	}

	public List<DeliveryMasterT> getDeliveryMasterTs() {
		return deliveryMasterTs;
	}

	public void setDeliveryMasterTs(List<DeliveryMasterT> deliveryMasterTs) {
		this.deliveryMasterTs = deliveryMasterTs;
	}

	public Integer getDeliveryClusterId() {
		return deliveryClusterId;
	}

	public void setDeliveryClusterId(Integer deliveryClusterId) {
		this.deliveryClusterId = deliveryClusterId;
	}

	public DeliveryClusterT getDeliveryClusterT() {
		return deliveryClusterT;
	}

	public void setDeliveryClusterT(DeliveryClusterT deliveryClusterT) {
		this.deliveryClusterT = deliveryClusterT;
	}
	
	
	
}