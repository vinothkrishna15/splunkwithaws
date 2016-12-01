
package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the customer_master_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
public class CustomerMasterDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String customerId;
	private String corporateHqAddress;
	private Timestamp createdModifiedDatetime;
	private String customerName;
	private String documentsAttached;
	private String facebook;
	private String groupCustomerName;
	private byte[] logo;
	private String notes;
	private String website;
	private String iou;
	private String geography;
	private boolean active;

	private List<BeaconCustomerMappingDTO> beaconCustomerMappingTs;
	private List<ContactCustomerLinkDTO> contactCustomerLinkTs;
	private List<ConnectDTO> connectTs;
	private GeographyMappingDTO geographyMappingT;
	private IouCustomerMappingDTO iouCustomerMappingT;
	//created_modified_by
	private UserDTO createdModifiedByUser;
	private List<NotesDTO> notesTs;
	private List<OpportunityDTO> opportunityTs;
	private List<RevenueCustomerMappingDTO> revenueCustomerMappingTs;

	public CustomerMasterDTO() {
		super();
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCorporateHqAddress() {
		return corporateHqAddress;
	}

	public void setCorporateHqAddress(String corporateHqAddress) {
		this.corporateHqAddress = corporateHqAddress;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getDocumentsAttached() {
		return documentsAttached;
	}

	public void setDocumentsAttached(String documentsAttached) {
		this.documentsAttached = documentsAttached;
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getGroupCustomerName() {
		return groupCustomerName;
	}

	public void setGroupCustomerName(String groupCustomerName) {
		this.groupCustomerName = groupCustomerName;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getIou() {
		return iou;
	}

	public void setIou(String iou) {
		this.iou = iou;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<BeaconCustomerMappingDTO> getBeaconCustomerMappingTs() {
		return beaconCustomerMappingTs;
	}

	public void setBeaconCustomerMappingTs(
			List<BeaconCustomerMappingDTO> beaconCustomerMappingTs) {
		this.beaconCustomerMappingTs = beaconCustomerMappingTs;
	}

	public List<ContactCustomerLinkDTO> getContactCustomerLinkTs() {
		return contactCustomerLinkTs;
	}

	public void setContactCustomerLinkTs(
			List<ContactCustomerLinkDTO> contactCustomerLinkTs) {
		this.contactCustomerLinkTs = contactCustomerLinkTs;
	}

	public List<ConnectDTO> getConnectTs() {
		return connectTs;
	}

	public void setConnectTs(List<ConnectDTO> connectTs) {
		this.connectTs = connectTs;
	}

	public GeographyMappingDTO getGeographyMappingT() {
		return geographyMappingT;
	}

	public void setGeographyMappingT(GeographyMappingDTO geographyMappingT) {
		this.geographyMappingT = geographyMappingT;
	}

	public IouCustomerMappingDTO getIouCustomerMappingT() {
		return iouCustomerMappingT;
	}

	public void setIouCustomerMappingT(IouCustomerMappingDTO iouCustomerMappingT) {
		this.iouCustomerMappingT = iouCustomerMappingT;
	}

	public UserDTO getCreatedModifiedByUser() {
		return createdModifiedByUser;
	}

	public void setCreatedModifiedByUser(UserDTO createdModifiedByUser) {
		this.createdModifiedByUser = createdModifiedByUser;
	}

	public List<NotesDTO> getNotesTs() {
		return notesTs;
	}

	public void setNotesTs(List<NotesDTO> notesTs) {
		this.notesTs = notesTs;
	}

	public List<OpportunityDTO> getOpportunityTs() {
		return opportunityTs;
	}

	public void setOpportunityTs(List<OpportunityDTO> opportunityTs) {
		this.opportunityTs = opportunityTs;
	}

	public List<RevenueCustomerMappingDTO> getRevenueCustomerMappingTs() {
		return revenueCustomerMappingTs;
	}

	public void setRevenueCustomerMappingTs(
			List<RevenueCustomerMappingDTO> revenueCustomerMappingTs) {
		this.revenueCustomerMappingTs = revenueCustomerMappingTs;
	}

}