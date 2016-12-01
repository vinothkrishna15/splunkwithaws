package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;


/**
 * The PartnerSubspProductMappingDTO.
 * 
 */
@JsonFilter(Constants.FILTER)
public class PartnerSubspProductMappingDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String partnerSubspProductMappingId;
	private Timestamp createdDatetime;
	private Timestamp modifiedDatetime;
	private String productId;
	private String partnerSubspMappingId;

	private ProductMasterDTO productMasterT;

	private UserDTO createdByUser;
	private UserDTO modifiedByUser;

	public PartnerSubspProductMappingDTO() {
		super();
	}

	public String getPartnerSubspProductMappingId() {
		return partnerSubspProductMappingId;
	}

	public void setPartnerSubspProductMappingId(String partnerSubspProductMappingId) {
		this.partnerSubspProductMappingId = partnerSubspProductMappingId;
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

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getPartnerSubspMappingId() {
		return partnerSubspMappingId;
	}

	public void setPartnerSubspMappingId(String partnerSubspMappingId) {
		this.partnerSubspMappingId = partnerSubspMappingId;
	}

	public ProductMasterDTO getProductMasterT() {
		return productMasterT;
	}

	public void setProductMasterT(ProductMasterDTO productMasterT) {
		this.productMasterT = productMasterT;
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

}