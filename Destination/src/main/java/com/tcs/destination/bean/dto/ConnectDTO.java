package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * ConnectDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class ConnectDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String connectId;
	private String connectCategory;
	private String connectName;

	private Timestamp createdDatetime;
	private Timestamp modifiedDatetime;
	private UserDTO createdByUser;
	private UserDTO modifiedByUser;

	private String documentsAttached;
	private Timestamp endDatetimeOfConnect;
	private Timestamp startDatetimeOfConnect;

	private String country;
	private String location;
	private String type;
	private Boolean cxoFlag;

	private CityMappingDTO cityMapping;
	private TimeZoneMappingDTO timeZoneMappingT;

	private List<ConnectCustomerContactLinkDTO> connectCustomerContactLinkTs;
	private List<ConnectOfferingLinkDTO> connectOfferingLinkTs;
	private List<ConnectOpportunityLinkDTO> connectOpportunityLinkIdTs;
	private List<ConnectSecondaryOwnerLinkDTO> connectSecondaryOwnerLinkTs;
	private List<ConnectSubSpLinkDTO> connectSubSpLinkTs;
	private CustomerMasterDTO customerMasterT;
	//country
	private GeographyCountryMappingDTO geographyCountryMappingT;
	private PartnerMasterDTO partnerMasterT;
	private ProductMasterDTO productMasterT;
	private UserDTO primaryOwnerUser;
	private List<ConnectTcsAccountContactLinkDTO> connectTcsAccountContactLinkTs;
	private List<NotesDTO> notesTs;

	public String getConnectId() {
		return connectId;
	}

	public void setConnectId(String connectId) {
		this.connectId = connectId;
	}

	public String getConnectCategory() {
		return connectCategory;
	}

	public void setConnectCategory(String connectCategory) {
		this.connectCategory = connectCategory;
	}

	public String getConnectName() {
		return connectName;
	}

	public void setConnectName(String connectName) {
		this.connectName = connectName;
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

	public String getDocumentsAttached() {
		return documentsAttached;
	}

	public void setDocumentsAttached(String documentsAttached) {
		this.documentsAttached = documentsAttached;
	}

	public Timestamp getEndDatetimeOfConnect() {
		return endDatetimeOfConnect;
	}

	public void setEndDatetimeOfConnect(Timestamp endDatetimeOfConnect) {
		this.endDatetimeOfConnect = endDatetimeOfConnect;
	}

	public Timestamp getStartDatetimeOfConnect() {
		return startDatetimeOfConnect;
	}

	public void setStartDatetimeOfConnect(Timestamp startDatetimeOfConnect) {
		this.startDatetimeOfConnect = startDatetimeOfConnect;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CityMappingDTO getCityMapping() {
		return cityMapping;
	}

	public void setCityMapping(CityMappingDTO cityMapping) {
		this.cityMapping = cityMapping;
	}

	public TimeZoneMappingDTO getTimeZoneMappingT() {
		return timeZoneMappingT;
	}

	public void setTimeZoneMappingT(TimeZoneMappingDTO timeZoneMappingT) {
		this.timeZoneMappingT = timeZoneMappingT;
	}

	public List<ConnectCustomerContactLinkDTO> getConnectCustomerContactLinkTs() {
		return connectCustomerContactLinkTs;
	}

	public void setConnectCustomerContactLinkTs(
			List<ConnectCustomerContactLinkDTO> connectCustomerContactLinkTs) {
		this.connectCustomerContactLinkTs = connectCustomerContactLinkTs;
	}

	public List<ConnectOfferingLinkDTO> getConnectOfferingLinkTs() {
		return connectOfferingLinkTs;
	}

	public void setConnectOfferingLinkTs(
			List<ConnectOfferingLinkDTO> connectOfferingLinkTs) {
		this.connectOfferingLinkTs = connectOfferingLinkTs;
	}

	public List<ConnectOpportunityLinkDTO> getConnectOpportunityLinkIdTs() {
		return connectOpportunityLinkIdTs;
	}

	public void setConnectOpportunityLinkIdTs(
			List<ConnectOpportunityLinkDTO> connectOpportunityLinkIdTs) {
		this.connectOpportunityLinkIdTs = connectOpportunityLinkIdTs;
	}

	public List<ConnectSecondaryOwnerLinkDTO> getConnectSecondaryOwnerLinkTs() {
		return connectSecondaryOwnerLinkTs;
	}

	public void setConnectSecondaryOwnerLinkTs(
			List<ConnectSecondaryOwnerLinkDTO> connectSecondaryOwnerLinkTs) {
		this.connectSecondaryOwnerLinkTs = connectSecondaryOwnerLinkTs;
	}

	public List<ConnectSubSpLinkDTO> getConnectSubSpLinkTs() {
		return connectSubSpLinkTs;
	}

	public void setConnectSubSpLinkTs(List<ConnectSubSpLinkDTO> connectSubSpLinkTs) {
		this.connectSubSpLinkTs = connectSubSpLinkTs;
	}

	public CustomerMasterDTO getCustomerMasterT() {
		return customerMasterT;
	}

	public void setCustomerMasterT(CustomerMasterDTO customerMasterT) {
		this.customerMasterT = customerMasterT;
	}

	public GeographyCountryMappingDTO getGeographyCountryMappingT() {
		return geographyCountryMappingT;
	}

	public void setGeographyCountryMappingT(
			GeographyCountryMappingDTO geographyCountryMappingT) {
		this.geographyCountryMappingT = geographyCountryMappingT;
	}

	public PartnerMasterDTO getPartnerMasterT() {
		return partnerMasterT;
	}

	public void setPartnerMasterT(PartnerMasterDTO partnerMasterT) {
		this.partnerMasterT = partnerMasterT;
	}

	public ProductMasterDTO getProductMasterT() {
		return productMasterT;
	}

	public void setProductMasterT(ProductMasterDTO productMasterT) {
		this.productMasterT = productMasterT;
	}

	public UserDTO getPrimaryOwnerUser() {
		return primaryOwnerUser;
	}

	public void setPrimaryOwnerUser(UserDTO primaryOwnerUser) {
		this.primaryOwnerUser = primaryOwnerUser;
	}

	public List<ConnectTcsAccountContactLinkDTO> getConnectTcsAccountContactLinkTs() {
		return connectTcsAccountContactLinkTs;
	}

	public void setConnectTcsAccountContactLinkTs(
			List<ConnectTcsAccountContactLinkDTO> connectTcsAccountContactLinkTs) {
		this.connectTcsAccountContactLinkTs = connectTcsAccountContactLinkTs;
	}

	public Boolean getCxoFlag() {
		return cxoFlag;
	}

	public void setCxoFlag(Boolean cxoFlag) {
		this.cxoFlag = cxoFlag;
	}

	public List<NotesDTO> getNotesTs() {
		return notesTs;
	}

	public void setNotesTs(List<NotesDTO> notesTs) {
		this.notesTs = notesTs;
	}
	
}