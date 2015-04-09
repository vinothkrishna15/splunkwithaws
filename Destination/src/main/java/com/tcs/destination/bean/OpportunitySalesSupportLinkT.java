package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the opportunity_sales_support_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "opportunitySalesSupportLinkId")
@Entity
@Table(name = "opportunity_sales_support_link_t")
@NamedQuery(name = "OpportunitySalesSupportLinkT.findAll", query = "SELECT o FROM OpportunitySalesSupportLinkT o")
public class OpportunitySalesSupportLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "opportunity_sales_support_link_id")
	private String opportunitySalesSupportLinkId;

	@Column(name = "create_modified_datetime")
	private Timestamp createModifiedDatetime;

	@Column(name = "created_modified_by")
	private String createdModifiedBy;

	@Column(name = "sales_support_owner")
	private String salesSupportOwner;

	@Column(name = "opportunity_id")
	private String opportunityId;

	// bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name = "opportunity_id", insertable = false, updatable = false)
	private OpportunityT opportunityT;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "created_modified_by", insertable = false, updatable = false)
	private UserT createdModifiedByUser;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "sales_support_owner", insertable = false, updatable = false)
	private UserT salesSupportOwnerUser;

	public OpportunitySalesSupportLinkT() {
	}

	public String getOpportunitySalesSupportLinkId() {
		return this.opportunitySalesSupportLinkId;
	}

	public void setOpportunitySalesSupportLinkId(
			String opportunitySalesSupportLinkId) {
		this.opportunitySalesSupportLinkId = opportunitySalesSupportLinkId;
	}

	public Timestamp getCreateModifiedDatetime() {
		return this.createModifiedDatetime;
	}

	public void setCreateModifiedDatetime(Timestamp createModifiedDatetime) {
		this.createModifiedDatetime = createModifiedDatetime;
	}

	public String getCreatedModifiedBy() {
		return this.createdModifiedBy;
	}

	public void setCreatedModifiedBy(String createdModifiedBy) {
		this.createdModifiedBy = createdModifiedBy;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

	public UserT getSalesSupportOwnerUser() {
		return this.salesSupportOwnerUser;
	}

	public void setSalesSupportOwnerUser(UserT salesSupportOwnerUser) {
		this.salesSupportOwnerUser = salesSupportOwnerUser;
	}

	public void setSalesSupportOwner(String salesSupportOwner) {
		this.salesSupportOwner = salesSupportOwner;
	}

	public String getSalesSupportOwner() {
		return salesSupportOwner;
	}

	public UserT getCreatedModifiedByUser() {
		return this.createdModifiedByUser;
	}

	public void setCreatedModifiedByUser(UserT createdModifiedByUser) {
		this.createdModifiedByUser = createdModifiedByUser;
	}

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}
}