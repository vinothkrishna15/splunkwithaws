package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the audit_opportunity_delivery_centre_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "auditOpportunityDeliveryCentreId")
@Entity
@Table(name="audit_opportunity_delivery_centre_t")
@NamedQuery(name="AuditOpportunityDeliveryCentreT.findAll", query="SELECT a FROM AuditOpportunityDeliveryCentreT a")
public class AuditOpportunityDeliveryCentreT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_opportunity_delivery_centre_id")
	private Integer auditOpportunityDeliveryCentreId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="delivery_centre_id")
	private Integer deliveryCentreId;

	@Column(name="operation_type")
	private Integer operationType;

	@Column(name="opportunity_delivery_centre_id")
	private Integer opportunityDeliveryCentreId;

	@Column(name="opportunity_id")
	private String opportunityId;

	private String reason;

	public AuditOpportunityDeliveryCentreT() {
	}

	public Integer getAuditOpportunityDeliveryCentreId() {
		return this.auditOpportunityDeliveryCentreId;
	}

	public void setAuditOpportunityDeliveryCentreId(Integer auditOpportunityDeliveryCentreId) {
		this.auditOpportunityDeliveryCentreId = auditOpportunityDeliveryCentreId;
	}

	public String getCreatedModifiedBy() {
		return this.createdModifiedBy;
	}

	public void setCreatedModifiedBy(String createdModifiedBy) {
		this.createdModifiedBy = createdModifiedBy;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return this.createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
	}

	public Integer getDeliveryCentreId() {
		return this.deliveryCentreId;
	}

	public void setDeliveryCentreId(Integer deliveryCentreId) {
		this.deliveryCentreId = deliveryCentreId;
	}

	public Integer getOperationType() {
		return this.operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public Integer getOpportunityDeliveryCentreId() {
		return this.opportunityDeliveryCentreId;
	}

	public void setOpportunityDeliveryCentreId(Integer opportunityDeliveryCentreId) {
		this.opportunityDeliveryCentreId = opportunityDeliveryCentreId;
	}

	public String getOpportunityId() {
		return this.opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public String getReason() {
		return this.reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}