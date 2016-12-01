package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the bid_office_group_owner_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
public class BidOfficeGroupOwnerLinkDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String bidOfficeGroupOwnerLinkId;
	private Timestamp createdDatetime;
	private UserDTO createdByUser;
	private Timestamp modifiedDatetime;
	private UserDTO modifiedByUser;
	private String bidId;
	private UserDTO bidOfficeGroupOwnerUser;
	
	public BidOfficeGroupOwnerLinkDTO() {
		super();
	}
	
	public String getBidOfficeGroupOwnerLinkId() {
		return bidOfficeGroupOwnerLinkId;
	}
	public void setBidOfficeGroupOwnerLinkId(String bidOfficeGroupOwnerLinkId) {
		this.bidOfficeGroupOwnerLinkId = bidOfficeGroupOwnerLinkId;
	}
	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}
	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}
	public UserDTO getCreatedByUser() {
		return createdByUser;
	}
	public void setCreatedByUser(UserDTO createdByUser) {
		this.createdByUser = createdByUser;
	}
	public Timestamp getModifiedDatetime() {
		return modifiedDatetime;
	}
	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}
	public UserDTO getModifiedByUser() {
		return modifiedByUser;
	}
	public void setModifiedByUser(UserDTO modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}
	public String getBidId() {
		return bidId;
	}
	public void setBidId(String bidId) {
		this.bidId = bidId;
	}
	public UserDTO getBidOfficeGroupOwnerUser() {
		return bidOfficeGroupOwnerUser;
	}
	public void setBidOfficeGroupOwnerUser(UserDTO bidOfficeGroupOwnerUser) {
		this.bidOfficeGroupOwnerUser = bidOfficeGroupOwnerUser;
	}

}