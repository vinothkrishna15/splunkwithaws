package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the customer_associate_t database table.
 * 
 */
@Entity
@Table(name="customer_associate_t")
@NamedQuery(name="CustomerAssociateT.findAll", query="SELECT c FROM CustomerAssociateT c")
public class CustomerAssociateT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="customer_associate_id")
	private String customerAssociateId;

	@Column(name="allocation_category")
	private String allocationCategory;

	@Temporal(TemporalType.DATE)
	@Column(name="allocation_end_date")
	private Date allocationEndDate;

	@Column(name="allocation_percentage")
	private BigDecimal allocationPercentage;

	@Temporal(TemporalType.DATE)
	@Column(name="allocation_start_date")
	private Date allocationStartDate;

	@Column(name="associate_type")
	private String associateType;

	@Column(name="client_country")
	private String clientCountry;

	@Temporal(TemporalType.DATE)
	@Column(name="created_date")
	private Date createdDate;

	@Column(name="depute_branch")
	private String deputeBranch;

	@Column(name="depute_country")
	private String deputeCountry;

	@Column(name="experience_category")
	private String experienceCategory;

	private String nationality;

	private String stream;

	@Column(name="sub_group")
	private String subGroup;

	@Column(name="working_site")
	private String workingSite;
	
	@Column(name="associate_id")
	private String associateId;
	
	@Column(name="reporting_dc")
	private String reportingDc;
	
	@Column(name="revenue_customer_map_id")
	private Long revenueCustomerMapId;
	
	@Column(name="sp")
	private String sp;
	
	@Column(name = "sub_sp")
	private String subSp;

	//bi-directional many-to-one association to AssociateT
	@ManyToOne
	@JoinColumn(name="associate_id", updatable=false, insertable=false)
	private AssociateT associateT;

	//bi-directional many-to-one association to RevenueCustomerMappingT
	@ManyToOne
	@JoinColumn(name="revenue_customer_map_id", updatable=false, insertable=false)
	private RevenueCustomerMappingT revenueCustomerMappingT;

	public CustomerAssociateT() {
	}

	public String getCustomerAssociateId() {
		return this.customerAssociateId;
	}

	public void setCustomerAssociateId(String customerAssociateId) {
		this.customerAssociateId = customerAssociateId;
	}

	public String getAllocationCategory() {
		return this.allocationCategory;
	}

	public void setAllocationCategory(String allocationCategory) {
		this.allocationCategory = allocationCategory;
	}

	public Date getAllocationEndDate() {
		return this.allocationEndDate;
	}

	public void setAllocationEndDate(Date allocationEndDate) {
		this.allocationEndDate = allocationEndDate;
	}

	public BigDecimal getAllocationPercentage() {
		return this.allocationPercentage;
	}

	public void setAllocationPercentage(BigDecimal allocationPercentage) {
		this.allocationPercentage = allocationPercentage;
	}

	public Date getAllocationStartDate() {
		return this.allocationStartDate;
	}

	public void setAllocationStartDate(Date allocationStartDate) {
		this.allocationStartDate = allocationStartDate;
	}

	public String getAssociateType() {
		return this.associateType;
	}

	public void setAssociateType(String associateType) {
		this.associateType = associateType;
	}

	public String getClientCountry() {
		return this.clientCountry;
	}

	public void setClientCountry(String clientCountry) {
		this.clientCountry = clientCountry;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getDeputeBranch() {
		return this.deputeBranch;
	}

	public void setDeputeBranch(String deputeBranch) {
		this.deputeBranch = deputeBranch;
	}

	public String getDeputeCountry() {
		return this.deputeCountry;
	}

	public void setDeputeCountry(String deputeCountry) {
		this.deputeCountry = deputeCountry;
	}

	public String getExperienceCategory() {
		return this.experienceCategory;
	}

	public void setExperienceCategory(String experienceCategory) {
		this.experienceCategory = experienceCategory;
	}

	public String getNationality() {
		return this.nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getStream() {
		return this.stream;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}

	public String getSubGroup() {
		return this.subGroup;
	}

	public void setSubGroup(String subGroup) {
		this.subGroup = subGroup;
	}

	public String getWorkingSite() {
		return this.workingSite;
	}

	public void setWorkingSite(String workingSite) {
		this.workingSite = workingSite;
	}

	public AssociateT getAssociateT() {
		return this.associateT;
	}

	public void setAssociateT(AssociateT associateT) {
		this.associateT = associateT;
	}

	public RevenueCustomerMappingT getRevenueCustomerMappingT() {
		return this.revenueCustomerMappingT;
	}

	public void setRevenueCustomerMappingT(RevenueCustomerMappingT revenueCustomerMappingT) {
		this.revenueCustomerMappingT = revenueCustomerMappingT;
	}

	public String getAssociateId() {
		return associateId;
	}

	public void setAssociateId(String associateId) {
		this.associateId = associateId;
	}

	public Long getRevenueCustomerMapId() {
		return revenueCustomerMapId;
	}

	public void setRevenueCustomerMapId(Long revenueCustomerMapId) {
		this.revenueCustomerMapId = revenueCustomerMapId;
	}
	
	public String getSp() {
		return sp;
	}

	public void setSp(String sp) {
		this.sp = sp;
	}

	public String getSubSp() {
		return subSp;
	}

	public void setSubSp(String subSp) {
		this.subSp = subSp;
	}

	public String getReportingDc() {
		return reportingDc;
	}

	public void setReportingDc(String reportingDc) {
		this.reportingDc = reportingDc;
	}
	
}