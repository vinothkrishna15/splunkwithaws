package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the workflow_partner_t database table.
 * 
 */
@Entity
@Table(name="workflow_partner_t")
@NamedQuery(name="WorkflowPartnerT.findAll", query="SELECT r FROM WorkflowPartnerT r")
public class WorkflowPartnerT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="workflow_partner_id")
	private Integer workflowPartnerId;

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

	@Column(name="partner_name")
	private String partnerName;

	private String website;

	//bi-directional many-to-one association to GeographyMappingT
	@ManyToOne
	@JoinColumn(name="geography")
	private GeographyMappingT geographyMappingT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by")
	private UserT userT1;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="modified_by")
	private UserT userT2;

	public WorkflowPartnerT() {
	}

	public Integer getWorkflowPartnerId() {
		return this.workflowPartnerId;
	}

	public void setWorkflowPartnerId(Integer workflowPartnerId) {
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

	public UserT getUserT1() {
		return this.userT1;
	}

	public void setUserT1(UserT userT1) {
		this.userT1 = userT1;
	}

	public UserT getUserT2() {
		return this.userT2;
	}

	public void setUserT2(UserT userT2) {
		this.userT2 = userT2;
	}

}