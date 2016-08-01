package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the partner_subsp_product_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Entity
@Table(name="partner_subsp_product_mapping_t")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "partnerSubspProductMappingId")
@NamedQuery(name="PartnerSubspProductMappingT.findAll", query="SELECT p FROM PartnerSubspProductMappingT p")
public class PartnerSubspProductMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="partner_subsp_product_mapping_id")
	private String partnerSubspProductMappingId;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;

	@Column(name="product_id")
	private String productId;

	@Column(name="partner_subsp_mapping_id")
	private String partnerSubspMappingId;
	
	@Transient
	private List<ContactT> productContact;
	
	//bi-directional many-to-one association to PartnerSubSpMappingT
	@ManyToOne
	@JoinColumn(name="partner_subsp_mapping_id", insertable = false, updatable = false)
	private PartnerSubSpMappingT partnerSubSpMappingT;
	
	// bi-directional many-to-one association to GeographyMappingT
	@ManyToOne
	@JoinColumn(name = "product_id", insertable = false, updatable = false)
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

	public PartnerSubspProductMappingT() {
	}

	public String getPartnerSubspProductMappingId() {
		return this.partnerSubspProductMappingId;
	}

	public void setPartnerSubspProductMappingId(String partnerSubspProductMappingId) {
		this.partnerSubspProductMappingId = partnerSubspProductMappingId;
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

	public String getProductId() {
		return this.productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public PartnerSubSpMappingT getPartnerSubSpMappingT() {
		return this.partnerSubSpMappingT;
	}

	public void setPartnerSubSpMappingT(PartnerSubSpMappingT partnerSubSpMappingT) {
		this.partnerSubSpMappingT = partnerSubSpMappingT;
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

	public ProductMasterT getProductMasterT() {
		return productMasterT;
	}

	public void setProductMasterT(ProductMasterT productMasterT) {
		this.productMasterT = productMasterT;
	}

	public UserT getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}

	public String getPartnerSubspMappingId() {
		return partnerSubspMappingId;
	}

	public void setPartnerSubspMappingId(String partnerSubspMappingId) {
		this.partnerSubspMappingId = partnerSubspMappingId;
	}

	public List<ContactT> getProductContact() {
		return productContact;
	}

	public void setProductContact(List<ContactT> productContact) {
		this.productContact = productContact;
	}

}