package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;


/**
 * The ConnectSubSpLinkDTO.
 * 
 */
@JsonFilter(Constants.FILTER)
public class ConnectSubSpLinkDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String connectSubSpLinkId;
	private SubSpMappingDTO subSpMappingT;
	
	private Timestamp createdDatetime;
	private Timestamp modifiedDatetime;
	//created_by
	private UserDTO userT2;
	//modified_by
	private UserDTO userT3;
	
	public ConnectSubSpLinkDTO() {
		super();
	}

	public String getConnectSubSpLinkId() {
		return connectSubSpLinkId;
	}

	public void setConnectSubSpLinkId(String connectSubSpLinkId) {
		this.connectSubSpLinkId = connectSubSpLinkId;
	}

	public SubSpMappingDTO getSubSpMappingT() {
		return subSpMappingT;
	}

	public void setSubSpMappingT(SubSpMappingDTO subSpMappingT) {
		this.subSpMappingT = subSpMappingT;
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

	public UserDTO getUserT2() {
		return userT2;
	}

	public void setUserT2(UserDTO userT2) {
		this.userT2 = userT2;
	}

	public UserDTO getUserT3() {
		return userT3;
	}

	public void setUserT3(UserDTO userT3) {
		this.userT3 = userT3;
	}
	
}