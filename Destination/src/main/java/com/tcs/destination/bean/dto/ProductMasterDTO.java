package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * The persistent class for the product_master_t database table.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class ProductMasterDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String productId;
	private Timestamp createdDatetime;
	private Timestamp modifiedDatetime;
	private String productDescription;
	private String productName;

	private List<ProductContactLinkDTO> productContactLinkTs;

	private UserDTO createdByUser;
	private UserDTO modifiedByUser;
	
	private boolean active = true;

	public ProductMasterDTO() {
		super();
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
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

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public List<ProductContactLinkDTO> getProductContactLinkTs() {
		return productContactLinkTs;
	}

	public void setProductContactLinkTs(
			List<ProductContactLinkDTO> productContactLinkTs) {
		this.productContactLinkTs = productContactLinkTs;
	}

	public UserDTO getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserDTO createdByUser) {
		this.createdByUser = createdByUser;
	}

	public UserDTO getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserDTO modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}