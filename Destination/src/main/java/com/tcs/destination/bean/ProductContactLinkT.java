package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the product_contact_link_t database table.
 * 
 */
@Entity
@Table(name="product_contact_link_t")
@NamedQuery(name="ProductContactLinkT.findAll", query="SELECT p FROM ProductContactLinkT p")
public class ProductContactLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="product_contact_id")
	private String productContactId;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;

	@Column(name="contact_id")
	private String contactId;
	
	//bi-directional many-to-one association to ContactT
	@ManyToOne
	@JoinColumn(name="contact_id", insertable = false, updatable = false)
	private ContactT contactT;

	
	@Column(name="product_id")
	private String productId;
	
	//bi-directional many-to-one association to ProductMasterT
	@ManyToOne
	@JoinColumn(name="product_id", insertable = false, updatable = false)
	private ProductMasterT productMasterT;

	@Column(name="created_by")
	private String createdBy;
	
	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by", insertable = false, updatable = false)
	private UserT createdByUser;

	@Column(name="modified_by")
	private String modifiedBy;
	
	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;

	public ProductContactLinkT() {
	}

	public String getProductContactId() {
		return this.productContactId;
	}

	public void setProductContactId(String productContactId) {
		this.productContactId = productContactId;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public ContactT getContactT() {
		return this.contactT;
	}

	public void setContactT(ContactT contactT) {
		this.contactT = contactT;
	}

	public ProductMasterT getProductMasterT() {
		return this.productMasterT;
	}

	public void setProductMasterT(ProductMasterT productMasterT) {
		this.productMasterT = productMasterT;
	}


	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public UserT getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserT createdByUser) {
		this.createdByUser = createdByUser;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public UserT getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}

}