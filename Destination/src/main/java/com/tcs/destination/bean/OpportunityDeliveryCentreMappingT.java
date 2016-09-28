package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;

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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "opportunityDeliveryCentreId")
@Entity
@Table(name="opportunity_delivery_centre_mapping_t")
@NamedQuery(name="OpportunityDeliveryCentreMappingT.findAll", query="SELECT o FROM OpportunityDeliveryCentreMappingT o")
public class OpportunityDeliveryCentreMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="opportunity_delivery_centre_id")
	private Integer opportunityDeliveryCentreId;

	private String reason;
	
	@Column(name = "opportunity_id")
	private String opportunityId;
	
	@Column(name = "delivery_centre_id")
	private Integer deliveryCentreId;
	
	@Column(name="created_datetime")
	private Timestamp createdDatetime;
	
	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;
	
	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "modified_by")
	private String modifiedBy;

	//bi-directional many-to-one association to DeliveryCentreT
	@ManyToOne
	@JoinColumn(name="delivery_centre_id", insertable=false, updatable=false)
	private DeliveryCentreT deliveryCentreT;

	//bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name="opportunity_id", insertable = false, updatable = false)
	private OpportunityT opportunityT;
	
	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "created_by", insertable = false, updatable = false)
	private UserT createdByUser;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;

	public OpportunityDeliveryCentreMappingT() {
	}

	public Integer getOpportunityDeliveryCentreId() {
		return this.opportunityDeliveryCentreId;
	}

	public void setOpportunityDeliveryCentreId(Integer opportunityDeliveryCentreId) {
		this.opportunityDeliveryCentreId = opportunityDeliveryCentreId;
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

	public Integer getDeliveryCentreId() {
		return deliveryCentreId;
	}

	public void setDeliveryCentreId(Integer deliveryCentreId) {
		this.deliveryCentreId = deliveryCentreId;
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

}