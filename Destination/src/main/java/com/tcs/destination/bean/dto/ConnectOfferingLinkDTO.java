package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The ConnectOfferingLinkDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class ConnectOfferingLinkDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String connectOfferingLinkId;
	private String connectId;
	private OfferingMappingDTO offeringMappingT;

	private Timestamp createdDatetime;
	private Timestamp modifiedDatetime;
	//created_by
	private UserDTO userT2;
	//modified_by
	private UserDTO userT3;
	
	public ConnectOfferingLinkDTO() {
		super();
	}

	public String getConnectOfferingLinkId() {
		return connectOfferingLinkId;
	}

	public void setConnectOfferingLinkId(String connectOfferingLinkId) {
		this.connectOfferingLinkId = connectOfferingLinkId;
	}

	public String getConnectId() {
		return connectId;
	}

	public void setConnectId(String connectId) {
		this.connectId = connectId;
	}

	public OfferingMappingDTO getOfferingMappingT() {
		return offeringMappingT;
	}

	public void setOfferingMappingT(OfferingMappingDTO offeringMappingT) {
		this.offeringMappingT = offeringMappingT;
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