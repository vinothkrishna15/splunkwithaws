package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the opportunity_sub_sp_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="opportunitySubSpLinkId")
@Entity
@Table(name="opportunity_sub_sp_link_t")
@NamedQuery(name="OpportunitySubSpLinkT.findAll", query="SELECT o FROM OpportunitySubSpLinkT o")
public class OpportunitySubSpLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="opportunity_sub_sp_link_id")
	private String opportunitySubSpLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="sub_sp")
	private String subSp;

	//bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name="opportunity_id")
	private OpportunityT opportunityT;

	public OpportunitySubSpLinkT() {
	}

	public String getOpportunitySubSpLinkId() {
		return this.opportunitySubSpLinkId;
	}

	public void setOpportunitySubSpLinkId(String opportunitySubSpLinkId) {
		this.opportunitySubSpLinkId = opportunitySubSpLinkId;
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

	public String getSubSp() {
		return this.subSp;
	}

	public void setSubSp(String subSp) {
		this.subSp = subSp;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

}