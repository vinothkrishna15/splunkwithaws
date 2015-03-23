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
 * The persistent class for the connect_tcs_account_contact_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="connectTcsAccountContactLinkId")
@Entity
@Table(name="connect_tcs_account_contact_link_t")
@NamedQuery(name="ConnectTcsAccountContactLinkT.findAll", query="SELECT c FROM ConnectTcsAccountContactLinkT c")
public class ConnectTcsAccountContactLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="connect_tcs_account_contact_link_id")
	private String connectTcsAccountContactLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	//bi-directional many-to-one association to ConnectT
	@ManyToOne
	@JoinColumn(name="connect_id",insertable=false,updatable=false)
	private ConnectT connectT;
	
	@Column(name="connect_id")
	private String connectId;
	
	public String getConnectId() {
		return connectId;
	}

	public void setConnectId(String connectId) {
		this.connectId = connectId;
	}

	//bi-directional many-to-one association to ContactT
	@ManyToOne
	@JoinColumn(name="contact_id")
	private ContactT contactT;

	public ConnectTcsAccountContactLinkT() {
	}

	public String getConnectTcsAccountContactLinkId() {
		return this.connectTcsAccountContactLinkId;
	}

	public void setConnectTcsAccountContactLinkId(String connectTcsAccountContactLinkId) {
		this.connectTcsAccountContactLinkId = connectTcsAccountContactLinkId;
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

	public ContactT getContactT() {
		return this.contactT;
	}

	public void setContactT(ContactT contactT) {
		this.contactT = contactT;
	}

}