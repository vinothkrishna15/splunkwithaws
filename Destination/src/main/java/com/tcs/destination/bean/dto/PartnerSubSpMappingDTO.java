package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * The PartnerSubSpMappingDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class PartnerSubSpMappingDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String partnerSubspMappingId;
	private Timestamp createdDatetime;
	private Timestamp modifiedDatetime;
	private Integer subSpId;
	private String subSp;
	private String partnerId;
	private SubSpMappingDTO subSpMappingT;
	
	private UserDTO createdByUser;

	private UserDTO modifiedByUser;

	private List<PartnerSubspProductMappingDTO> partnerSubspProductMappingTs;

	public PartnerSubSpMappingDTO() {
		super();
	}

	public String getPartnerSubspMappingId() {
		return partnerSubspMappingId;
	}

	public void setPartnerSubspMappingId(String partnerSubspMappingId) {
		this.partnerSubspMappingId = partnerSubspMappingId;
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

	public Integer getSubSpId() {
		return subSpId;
	}

	public void setSubSpId(Integer subSpId) {
		this.subSpId = subSpId;
	}

	public String getSubSp() {
		return subSp;
	}

	public void setSubSp(String subSp) {
		this.subSp = subSp;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public SubSpMappingDTO getSubSpMappingT() {
		return subSpMappingT;
	}

	public void setSubSpMappingT(SubSpMappingDTO subSpMappingT) {
		this.subSpMappingT = subSpMappingT;
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

	public List<PartnerSubspProductMappingDTO> getPartnerSubspProductMappingTs() {
		return partnerSubspProductMappingTs;
	}

	public void setPartnerSubspProductMappingTs(
			List<PartnerSubspProductMappingDTO> partnerSubspProductMappingTs) {
		this.partnerSubspProductMappingTs = partnerSubspProductMappingTs;
	}

}