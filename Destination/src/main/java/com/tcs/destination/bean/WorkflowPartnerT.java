package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the workflow_partner_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="workflowPartnerId")
@Entity
@Table(name="workflow_partner_t")
@NamedQuery(name="WorkflowPartnerT.findAll", query="SELECT r FROM WorkflowPartnerT r")
public class WorkflowPartnerT implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="workflow_partner_id")
	private String workflowPartnerId;

	@Column(name="corporate_hq_address")
	private String corporateHqAddress;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="documents_attached")
	private String documentsAttached;

	private String facebook;

	private byte[] logo;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;

	private String notes;

	@Transient
	private String comments;

	@Column(name="partner_name")
	private String partnerName;

	@Column(name="created_by")
	private String createdBy;

	@Column(name="modified_by")
	private String modifiedBy;

	private String website;

	private String geography;

	//bi-directional many-to-one association to GeographyMappingT
	@ManyToOne
	@JoinColumn(name="geography", insertable = false, updatable = false)
	private GeographyMappingT geographyMappingT;

	//added for partner changes -
	//bi-directional many-to-one association to PartnerSubSpMappingT
	@OneToMany(mappedBy="partnerMasterT")
	private List<PartnerSubSpMappingT> partnerSubSpMappingTs;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by", updatable = false, insertable = false)
	private UserT createdByUser;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="modified_by", updatable = false, insertable = false)
	private UserT modifiedByUser;

	//partner related changes

	//added for partner changes - city, country, text1,text2,text3,group partner name,hqpqrtner link id
	private String city;

	private String text1;

	private String text2;

	private String text3;

	@Column(name="group_partner_name")
	private String groupPartnerName;

	private String country;
	
	@Transient
	List<PartnerProductDetailsDTO> partnerProductDetailsDTOs;

	//bi-directional many-to-one association to GeographyCountryMappingT
	@ManyToOne
	@JoinColumn(name="country", insertable = false, updatable = false)
	private GeographyCountryMappingT geographyCountryMappingT;

	@Column(name="hq_partner_link_id")
	private String hqPartnerLinkId;

	//bi-directional many-to-one association to PartnerMasterT
	/*@ManyToOne
		@JoinColumn(name="hq_partner_link_id", insertable = false, updatable = false)
		private PartnerMasterT partnerMasterT;*/


	public WorkflowPartnerT() {
	}

	public String getWorkflowPartnerId() {
		return this.workflowPartnerId;
	}

	public void setWorkflowPartnerId(String workflowPartnerId) {
		this.workflowPartnerId = workflowPartnerId;
	}

	public String getCorporateHqAddress() {
		return this.corporateHqAddress;
	}

	public void setCorporateHqAddress(String corporateHqAddress) {
		this.corporateHqAddress = corporateHqAddress;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public String getDocumentsAttached() {
		return this.documentsAttached;
	}

	public void setDocumentsAttached(String documentsAttached) {
		this.documentsAttached = documentsAttached;
	}

	public String getFacebook() {
		return this.facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public byte[] getLogo() {
		return this.logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getPartnerName() {
		return this.partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public GeographyMappingT getGeographyMappingT() {
		return this.geographyMappingT;
	}

	public void setGeographyMappingT(GeographyMappingT geographyMappingT) {
		this.geographyMappingT = geographyMappingT;
	}

	public UserT getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserT createdByUser) {
		this.createdByUser = createdByUser;
	}

	public UserT getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public GeographyCountryMappingT getGeographyCountryMappingT() {
		return geographyCountryMappingT;
	}

	public void setGeographyCountryMappingT(
			GeographyCountryMappingT geographyCountryMappingT) {
		this.geographyCountryMappingT = geographyCountryMappingT;
	}

	public String getHqPartnerLinkId() {
		return hqPartnerLinkId;
	}

	public void setHqPartnerLinkId(String hqPartnerLinkId) {
		this.hqPartnerLinkId = hqPartnerLinkId;
	}
	
	public List<PartnerSubSpMappingT> getPartnerSubSpMappingTs() {
		return partnerSubSpMappingTs;
	}

	public void setPartnerSubSpMappingTs(
			List<PartnerSubSpMappingT> partnerSubSpMappingTs) {
		this.partnerSubSpMappingTs = partnerSubSpMappingTs;
	}

	public List<PartnerProductDetailsDTO> getPartnerProductDetailsDTOs() {
		return partnerProductDetailsDTOs;
	}
}