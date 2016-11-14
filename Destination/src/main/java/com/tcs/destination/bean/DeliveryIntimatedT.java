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
 * The persistent class for the delivery_intimated_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "deliveryIntimatedId", scope = DeliveryIntimatedT.class)
@Entity
@Table(name="delivery_intimated_t")
@NamedQuery(name="DeliveryIntimatedT.findAll", query="SELECT d FROM DeliveryIntimatedT d")
public class DeliveryIntimatedT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="delivery_intimated_id")
	private String deliveryIntimatedId;

	private Boolean accepted;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;

	@Column(name="reject_comments")
	private String rejectComments;

	@Column(name="reject_reason")
	private String rejectReason;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="modified_by")
	private String modifiedBy;
	
	@Column(name="delivery_stage")
	private Integer deliveryStage;
	
	@Column(name="opportunity_id")
	private String opportunityId;

	//bi-directional many-to-one association to DeliveryIntimatedCentreLinkT
	@OneToMany(mappedBy="deliveryIntimatedT")
	private List<DeliveryIntimatedCentreLinkT> deliveryIntimatedCentreLinkTs;

	//bi-directional many-to-one association to DeliveryStageMappingT
	@ManyToOne
	@JoinColumn(name="delivery_stage", insertable = false, updatable = false)
	private DeliveryStageMappingT deliveryStageMappingT;

	//bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name="opportunity_id", insertable = false, updatable = false)
	private OpportunityT opportunityT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by", insertable = false, updatable = false)
	private UserT createdByUser;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;

	//bi-directional many-to-one association to DeliveryMasterT
	@OneToMany(mappedBy="deliveryIntimatedT")
	private List<DeliveryMasterT> deliveryMasterTs;

	public DeliveryIntimatedT() {
	}

	public String getDeliveryIntimatedId() {
		return this.deliveryIntimatedId;
	}

	public void setDeliveryIntimatedId(String deliveryIntimatedId) {
		this.deliveryIntimatedId = deliveryIntimatedId;
	}

	public Boolean getAccepted() {
		return this.accepted;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public String getRejectComments() {
		return this.rejectComments;
	}

	public void setRejectComments(String rejectComments) {
		this.rejectComments = rejectComments;
	}

	public String getRejectReason() {
		return this.rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public List<DeliveryIntimatedCentreLinkT> getDeliveryIntimatedCentreLinkTs() {
		return this.deliveryIntimatedCentreLinkTs;
	}

	public void setDeliveryIntimatedCentreLinkTs(List<DeliveryIntimatedCentreLinkT> deliveryIntimatedCentreLinkTs) {
		this.deliveryIntimatedCentreLinkTs = deliveryIntimatedCentreLinkTs;
	}

	public DeliveryIntimatedCentreLinkT addDeliveryIntimatedCentreLinkT(DeliveryIntimatedCentreLinkT deliveryIntimatedCentreLinkT) {
		getDeliveryIntimatedCentreLinkTs().add(deliveryIntimatedCentreLinkT);
		deliveryIntimatedCentreLinkT.setDeliveryIntimatedT(this);

		return deliveryIntimatedCentreLinkT;
	}

	public DeliveryIntimatedCentreLinkT removeDeliveryIntimatedCentreLinkT(DeliveryIntimatedCentreLinkT deliveryIntimatedCentreLinkT) {
		getDeliveryIntimatedCentreLinkTs().remove(deliveryIntimatedCentreLinkT);
		deliveryIntimatedCentreLinkT.setDeliveryIntimatedT(null);

		return deliveryIntimatedCentreLinkT;
	}

	public DeliveryStageMappingT getDeliveryStageMappingT() {
		return this.deliveryStageMappingT;
	}

	public void setDeliveryStageMappingT(DeliveryStageMappingT deliveryStageMappingT) {
		this.deliveryStageMappingT = deliveryStageMappingT;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

	public List<DeliveryMasterT> getDeliveryMasterTs() {
		return this.deliveryMasterTs;
	}

	public void setDeliveryMasterTs(List<DeliveryMasterT> deliveryMasterTs) {
		this.deliveryMasterTs = deliveryMasterTs;
	}

	public DeliveryMasterT addDeliveryMasterT(DeliveryMasterT deliveryMasterT) {
		getDeliveryMasterTs().add(deliveryMasterT);
		deliveryMasterT.setDeliveryIntimatedT(this);

		return deliveryMasterT;
	}

	public DeliveryMasterT removeDeliveryMasterT(DeliveryMasterT deliveryMasterT) {
		getDeliveryMasterTs().remove(deliveryMasterT);
		deliveryMasterT.setDeliveryIntimatedT(null);

		return deliveryMasterT;
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

	public Integer getDeliveryStage() {
		return deliveryStage;
	}

	public void setDeliveryStage(Integer deliveryStage) {
		this.deliveryStage = deliveryStage;
	}

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}
	

}