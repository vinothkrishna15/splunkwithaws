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
 * The persistent class for the connect_customer_contact_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="connectCustomerContactLinkId")
@Entity
@Table(name="connect_customer_contact_link_t")
@NamedQuery(name="ConnectCustomerContactLinkT.findAll", query="SELECT c FROM ConnectCustomerContactLinkT c")
public class ConnectCustomerContactLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="connect_customer_contact_link_id")
	private String connectCustomerContactLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_modified_by",insertable=false,updatable=false)
	private UserT createdModifiedByUser;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="connect_id")
	private String connectId;
	
	//bi-directional many-to-one association to ConnectT
	@ManyToOne
	@JoinColumn(name="connect_id",insertable=false,updatable=false)
	private ConnectT connectT;

	//bi-directional many-to-one association to ContactT
	@ManyToOne
	@JoinColumn(name="contact_id")
	private ContactT contactT;

	public ConnectCustomerContactLinkT() {
	}

	public String getConnectCustomerContactLinkId() {
		return this.connectCustomerContactLinkId;
	}

	public void setConnectCustomerContactLinkId(String connectCustomerContactLinkId) {
		this.connectCustomerContactLinkId = connectCustomerContactLinkId;
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

	public String getConnectId() {
		return connectId;
	}

	public void setConnectId(String connect_id) {
		this.connectId = connect_id;
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

	public UserT getCreatedModifiedByUser() {
		return this.createdModifiedByUser;
	}

	public void setCreatedModifiedByUser(UserT createdModifiedByUser) {
		this.createdModifiedByUser = createdModifiedByUser;
	}

}