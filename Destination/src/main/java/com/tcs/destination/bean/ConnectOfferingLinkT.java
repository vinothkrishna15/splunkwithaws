package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the connect_offering_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="connectOfferingLinkId")
@Entity
@Table(name="connect_offering_link_t")
@NamedQuery(name="ConnectOfferingLinkT.findAll", query="SELECT c FROM ConnectOfferingLinkT c")
public class ConnectOfferingLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="connect_offering_link_id")
	private String connectOfferingLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	//bi-directional many-to-one association to ConnectT
	@ManyToOne
	@JoinColumn(name="connect_id")
	private ConnectT connectT;

	//bi-directional many-to-one association to OfferingMappingT
	@ManyToOne
	@JoinColumn(name="offering")
	private OfferingMappingT offeringMappingT;

	public ConnectOfferingLinkT() {
	}

	public String getConnectOfferingLinkId() {
		return this.connectOfferingLinkId;
	}

	public void setConnectOfferingLinkId(String connectOfferingLinkId) {
		this.connectOfferingLinkId = connectOfferingLinkId;
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

	public ConnectT getConnectT() {
		return this.connectT;
	}

	public void setConnectT(ConnectT connectT) {
		this.connectT = connectT;
	}

	public OfferingMappingT getOfferingMappingT() {
		return this.offeringMappingT;
	}

	public void setOfferingMappingT(OfferingMappingT offeringMappingT) {
		this.offeringMappingT = offeringMappingT;
	}

}