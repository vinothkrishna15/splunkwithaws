package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the connect_secondary_owner_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
public class ConnectSecondaryOwnerLinkDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String connectSecondaryOwnerLinkId;
	private Timestamp createdDatetime;
	private Timestamp modifiedDatetime;
	//created_by
	private UserDTO userT2;
	//modified_by
	private UserDTO userT3;

	private String connectId;

	private UserDTO secondaryOwnerUser;

	public ConnectSecondaryOwnerLinkDTO() {
		super();
	}

	public String getConnectSecondaryOwnerLinkId() {
		return connectSecondaryOwnerLinkId;
	}

	public void setConnectSecondaryOwnerLinkId(String connectSecondaryOwnerLinkId) {
		this.connectSecondaryOwnerLinkId = connectSecondaryOwnerLinkId;
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

	public String getConnectId() {
		return connectId;
	}

	public void setConnectId(String connectId) {
		this.connectId = connectId;
	}

	public UserDTO getSecondaryOwnerUser() {
		return secondaryOwnerUser;
	}

	public void setSecondaryOwnerUser(UserDTO secondaryOwnerUser) {
		this.secondaryOwnerUser = secondaryOwnerUser;
	}

}