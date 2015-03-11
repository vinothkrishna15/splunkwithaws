package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.sql.Timestamp;


/**
 * The persistent class for the opportunity_sales_support_link_t database table.
 * 
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="opportunitySalesSupportLinkId")
@Entity
@Table(name="opportunity_sales_support_link_t")
@NamedQuery(name="OpportunitySalesSupportLinkT.findAll", query="SELECT o FROM OpportunitySalesSupportLinkT o")
public class OpportunitySalesSupportLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="opportunity_sales_support_link_id")
	private String opportunitySalesSupportLinkId;

	@Column(name="create_modified_datetime")
	private Timestamp createModifiedDatetime;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	//bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name="opportunity_id")
	private OpportunityT opportunityT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="sales_support_owner")
	private UserT userT;

	public OpportunitySalesSupportLinkT() {
	}

	public String getOpportunitySalesSupportLinkId() {
		return this.opportunitySalesSupportLinkId;
	}

	public void setOpportunitySalesSupportLinkId(String opportunitySalesSupportLinkId) {
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

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

}