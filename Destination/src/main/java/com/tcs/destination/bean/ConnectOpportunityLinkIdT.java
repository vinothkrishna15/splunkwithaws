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
 * The persistent class for the connect_opportunity_link_id_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "connectOpportunityLinkId")
@Entity
@Table(name = "connect_opportunity_link_id_t")
@NamedQuery(name = "ConnectOpportunityLinkIdT.findAll", query = "SELECT c FROM ConnectOpportunityLinkIdT c")
public class ConnectOpportunityLinkIdT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "connect_opportunity_link_id")
	private String connectOpportunityLinkId;

//	@Column(name = "created_modified_by")
//	private String createdModifiedBy;
//
//	@Column(name = "created_modified_datetime")
//	private Timestamp createdModifiedDatetime;

	@Column(name="created_datetime",updatable = false)
	private Timestamp createdDatetime;
	
	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;
	
	@Column(name = "created_by",updatable = false)
	private String createdBy;
	
	@Column(name = "modified_by")
	private String modifiedBy;
	
	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "created_by", updatable = false, insertable = false)
	private UserT userT2;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "modified_by", updatable = false, insertable = false)
	private UserT userT3;
	
	// bi-directional many-to-one association to ConnectT
	@ManyToOne
	@JoinColumn(name = "connect_id", updatable = false, insertable = false)
	private ConnectT connectT;

	@Column(name = "connect_id")
	private String connectId;

	@Column(name = "opportunity_id")
	private String opportunityId;

	public String getConnectId() {
		return connectId;
	}

	public void setConnectId(String connectId) {
		this.connectId = connectId;
	}

	// bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name = "opportunity_id", insertable = false, updatable = false)
	private OpportunityT opportunityT;

	// bi-directional many-to-one association to UserT
//	@ManyToOne
//	@JoinColumn(name = "created_modified_by", insertable = false, updatable = false)
//	private UserT createdModifiedByUser;

	public ConnectOpportunityLinkIdT() {
	}

	public String getConnectOpportunityLinkId() {
		return this.connectOpportunityLinkId;
	}

	public void setConnectOpportunityLinkId(String connectOpportunityLinkId) {
		this.connectOpportunityLinkId = connectOpportunityLinkId;
	}

//	public String getCreatedModifiedBy() {
//		return this.createdModifiedBy;
//	}
//
//	public void setCreatedModifiedBy(String createdModifiedBy) {
//		this.createdModifiedBy = createdModifiedBy;
//	}
//
//	public Timestamp getCreatedModifiedDatetime() {
//		return this.createdModifiedDatetime;
//	}
//
//	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
//		this.createdModifiedDatetime = createdModifiedDatetime;
//	}

	public ConnectT getConnectT() {
		return this.connectT;
	}

	public void setConnectT(ConnectT connectT) {
		this.connectT = connectT;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

//	public UserT getCreatedModifiedByUser() {
//		return this.createdModifiedByUser;
//	}
//
//	public void setCreatedModifiedByUser(UserT createdModifiedByUser) {
//		this.createdModifiedByUser = createdModifiedByUser;
//	}
	
	
	public String getOpportunityId() {
		return opportunityId;
	}
	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
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

	public UserT getUserT2() {
		return userT2;
	}

	public void setUserT2(UserT userT2) {
		this.userT2 = userT2;
	}

	public UserT getUserT3() {
		return userT3;
	}

	public void setUserT3(UserT userT3) {
		this.userT3 = userT3;
	}
	
}