package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the workflow_customer_t database table.
 * 
 */
@Entity
@Table(name="workflow_customer_t")
@NamedQuery(name="WorkflowCustomerT.findAll", query="SELECT r FROM WorkflowCustomerT r")
public class WorkflowCustomerT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="workflow_customer_id")
	private Integer workflowCustomerId;

	@Column(name="corporate_hq_address")
	private String corporateHqAddress;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="customer_name")
	private String customerName;

	@Column(name="documents_attached")
	private String documentsAttached;

	private String facebook;

	@Column(name="group_customer_name")
	private String groupCustomerName;

	private byte[] logo;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;

	private String notes;

	private String website;
	
	private String iou;
	
	private String geography;

	//bi-directional many-to-one association to GeographyMappingT
	@ManyToOne
	@JoinColumn(name="geography", insertable = false, updatable = false)
	private GeographyMappingT geographyMappingT;


	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by")
	private UserT userT1;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="modified_by")
	private UserT userT2;
	
	@Transient
	private List<RevenueCustomerMappingT> revenueCustomerMappingTs;
	
	@Transient
	private List<BeaconCustomerMappingT> beaconCustomerMappingTs;
	

	public WorkflowCustomerT() {
	}

	public Integer getWorkflowCustomerId() {
		return this.workflowCustomerId;
	}

	public void setWorkflowCustomerId(Integer workflowCustomerId) {
		this.workflowCustomerId = workflowCustomerId;
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

	public String getCustomerName() {
		return this.customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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

	public String getGroupCustomerName() {
		return this.groupCustomerName;
	}

	public void setGroupCustomerName(String groupCustomerName) {
		this.groupCustomerName = groupCustomerName;
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

	public List<RevenueCustomerMappingT> getRevenueCustomerMappingT() {
		return revenueCustomerMappingTs;
	}

	public void setRevenueCustomerMappingT(
			List<RevenueCustomerMappingT> revenueCustomerMappingTs) {
		this.revenueCustomerMappingTs = revenueCustomerMappingTs;
	}

	public List<BeaconCustomerMappingT> getBeaconCustomerMappingT() {
		return beaconCustomerMappingTs;
	}

	public void setBeaconCustomerMappingT(
			List<BeaconCustomerMappingT> beaconCustomerMappingTs) {
		this.beaconCustomerMappingTs = beaconCustomerMappingTs;
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
	
	

}