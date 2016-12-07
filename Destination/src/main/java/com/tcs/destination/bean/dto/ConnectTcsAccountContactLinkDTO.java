package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * The ConnectTcsAccountContactLinkDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class ConnectTcsAccountContactLinkDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String connectTcsAccountContactLinkId;

	private String connectId;
	private ContactDTO contactT;
	private Timestamp createdDatetime;
	private Timestamp modifiedDatetime;
	//created_by
	private UserDTO userT2;
	//modified_by
	private UserDTO userT3;

	public String getConnectTcsAccountContactLinkId() {
		return connectTcsAccountContactLinkId;
	}

	public void setConnectTcsAccountContactLinkId(
			String connectTcsAccountContactLinkId) {
		this.connectTcsAccountContactLinkId = connectTcsAccountContactLinkId;
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

	public ContactDTO getContactT() {
		return contactT;
	}

	public void setContactT(ContactDTO contactT) {
		this.contactT = contactT;
	}
}