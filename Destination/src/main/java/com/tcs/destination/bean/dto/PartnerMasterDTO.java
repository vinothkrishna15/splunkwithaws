package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * The PartnerMasterDTO.
 * 
 */
@JsonFilter(Constants.FILTER)
public class PartnerMasterDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String partnerId;
	private String corporateHqAddress;
	private String documentsAttached;
	private String facebook;
	private boolean active = true;
	private byte[] logo;
	private String geography;
	private String partnerName;
	private String website;
	private String notes;

	private List<ConnectDTO> connectTs;
	private List<OpportunityPartnerLinkDTO> opportunityPartnerLinkTs;
	private GeographyMappingDTO geographyMappingT;

	//added for partner changes - city, country, text1,text2,text3,group partner name,hqpqrtner link id
	private String city;
	private String text1;
	private String text2;
	private String text3;
	private String groupPartnerName;
	
	private GeographyCountryMappingDTO geographyCountryMappingT;
	private String hqPartnerLinkId;
	//parent partner
	private PartnerMasterDTO partnerMasterT;
	//child partner
	private List<PartnerMasterDTO> partnerMasterTs;
	private List<PartnerSubSpMappingDTO> partnerSubSpMappingTs;
	private List<PartnerContactLinkDTO> partnerContactLinkTs;

	private UserDTO createdByUser;
	private Timestamp createdDatetime;
	private UserDTO modifiedByUser;
	private Timestamp modifiedDatetime;

	public PartnerMasterDTO() {
		super();
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getCorporateHqAddress() {
		return corporateHqAddress;
	}

	public void setCorporateHqAddress(String corporateHqAddress) {
		this.corporateHqAddress = corporateHqAddress;
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public List<ConnectDTO> getConnectTs() {
		return connectTs;
	}

	public void setConnectTs(List<ConnectDTO> connectTs) {
		this.connectTs = connectTs;
	}

	public List<OpportunityPartnerLinkDTO> getOpportunityPartnerLinkTs() {
		return opportunityPartnerLinkTs;
	}

	public void setOpportunityPartnerLinkTs(
			List<OpportunityPartnerLinkDTO> opportunityPartnerLinkTs) {
		this.opportunityPartnerLinkTs = opportunityPartnerLinkTs;
	}

	public GeographyMappingDTO getGeographyMappingT() {
		return geographyMappingT;
	}

	public void setGeographyMappingT(GeographyMappingDTO geographyMappingT) {
		this.geographyMappingT = geographyMappingT;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
	}

	public String getText2() {
		return text2;
	}

	public void setText2(String text2) {
		this.text2 = text2;
	}

	public String getText3() {
		return text3;
	}

	public void setText3(String text3) {
		this.text3 = text3;
	}

	public String getGroupPartnerName() {
		return groupPartnerName;
	}

	public void setGroupPartnerName(String groupPartnerName) {
		this.groupPartnerName = groupPartnerName;
	}

	public GeographyCountryMappingDTO getGeographyCountryMappingT() {
		return geographyCountryMappingT;
	}

	public void setGeographyCountryMappingT(
			GeographyCountryMappingDTO geographyCountryMappingT) {
		this.geographyCountryMappingT = geographyCountryMappingT;
	}

	public String getHqPartnerLinkId() {
		return hqPartnerLinkId;
	}

	public void setHqPartnerLinkId(String hqPartnerLinkId) {
		this.hqPartnerLinkId = hqPartnerLinkId;
	}

	public PartnerMasterDTO getPartnerMasterT() {
		return partnerMasterT;
	}

	public void setPartnerMasterT(PartnerMasterDTO partnerMasterT) {
		this.partnerMasterT = partnerMasterT;
	}

	public List<PartnerMasterDTO> getPartnerMasterTs() {
		return partnerMasterTs;
	}

	public void setPartnerMasterTs(List<PartnerMasterDTO> partnerMasterTs) {
		this.partnerMasterTs = partnerMasterTs;
	}

	public List<PartnerSubSpMappingDTO> getPartnerSubSpMappingTs() {
		return partnerSubSpMappingTs;
	}

	public void setPartnerSubSpMappingTs(
			List<PartnerSubSpMappingDTO> partnerSubSpMappingTs) {
		this.partnerSubSpMappingTs = partnerSubSpMappingTs;
	}

	public List<PartnerContactLinkDTO> getPartnerContactLinkTs() {
		return partnerContactLinkTs;
	}

	public void setPartnerContactLinkTs(
			List<PartnerContactLinkDTO> partnerContactLinkTs) {
		this.partnerContactLinkTs = partnerContactLinkTs;
	}

	public UserDTO getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserDTO createdByUser) {
		this.createdByUser = createdByUser;
	}

	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public UserDTO getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserDTO modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}

	public Timestamp getModifiedDatetime() {
		return modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

}