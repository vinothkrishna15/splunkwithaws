package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the connect_customer_contact_link_t database table.
 * 
 */
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

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	//bi-directional many-to-one association to ConnectT
	@ManyToOne
	@JoinColumn(name="connect_id")
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