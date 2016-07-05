package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the product_master_t database table.
 * 
 */
@Entity
@Table(name="product_master_t")
@NamedQuery(name="ProductMasterT.findAll", query="SELECT p FROM ProductMasterT p")
public class ProductMasterT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="product_id")
	private String productId;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;

	@Column(name="product_description")
	private String productDescription;

	@Column(name="product_name")
	private String productName;

	//bi-directional many-to-one association to ProductContactLinkT
	@OneToMany(mappedBy="productMasterT")
	private List<ProductContactLinkT> productContactLinkTs;

	//bi-directional many-to-one association to ProductContactLinkT
	@OneToMany(mappedBy="productMasterT")
	private List<PartnerSubspProductMappingT> partnerSubspProductMappingTs;


	//partner changes added
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
	
	@Column(name = "active")
	private boolean active = true;

	public ProductMasterT() {
	}

	public String getProductId() {
		return this.productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
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

	public String getProductDescription() {
		return this.productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getProductName() {
		return this.productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public List<ProductContactLinkT> getProductContactLinkTs() {
		return this.productContactLinkTs;
	}

	public void setProductContactLinkTs(List<ProductContactLinkT> productContactLinkTs) {
		this.productContactLinkTs = productContactLinkTs;
	}

	public ProductContactLinkT addProductContactLinkT(ProductContactLinkT productContactLinkT) {
		getProductContactLinkTs().add(productContactLinkT);
		productContactLinkT.setProductMasterT(this);

		return productContactLinkT;
	}

	public ProductContactLinkT removeProductContactLinkT(ProductContactLinkT productContactLinkT) {
		getProductContactLinkTs().remove(productContactLinkT);
		productContactLinkT.setProductMasterT(null);

		return productContactLinkT;
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

	public List<PartnerSubspProductMappingT> getPartnerSubspProductMappingTs() {
		return partnerSubspProductMappingTs;
	}

	public void setPartnerSubspProductMappingTs(
			List<PartnerSubspProductMappingT> partnerSubspProductMappingTs) {
		this.partnerSubspProductMappingTs = partnerSubspProductMappingTs;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}