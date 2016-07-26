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
 * The persistent class for the partner_sub_sp_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Entity
@Table(name="partner_sub_sp_mapping_t")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "partnerSubspMappingId")
@NamedQuery(name="PartnerSubSpMappingT.findAll", query="SELECT p FROM PartnerSubSpMappingT p")
public class PartnerSubSpMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="partner_subsp_mapping_id")
	private String partnerSubspMappingId;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;

	@Column(name="sub_sp_id")
	private Integer subSpId;

	@Column(name="partner_id")
	private String partnerId;
	
	//bi-directional many-to-one association to PartnerMasterT
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sub_sp_id",insertable = false, updatable = false)
	private SubSpMappingT subSpMappingT;
	
	//bi-directional many-to-one association to PartnerMasterT
	@ManyToOne
	@JoinColumn(name="partner_id", insertable = false, updatable = false)
	private PartnerMasterT partnerMasterT;

	@Column(name="created_by")
	private String createdBy;
	
	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by", insertable = false, updatable = false)
	private UserT createdByUser;

	@Column(name="modified_by")
	private String modifiedBy;
	
	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;

	//bi-directional many-to-one association to PartnerSubspProductMappingT
	@OneToMany(mappedBy="partnerSubSpMappingT", cascade = CascadeType.ALL)
	private List<PartnerSubspProductMappingT> partnerSubspProductMappingTs;
	
	@Transient
	private List<PartnerSubspProductMappingT> deletePartnerSubspProductMappingTs;

	

	public PartnerSubSpMappingT() {
	}

	public String getPartnerSubspMappingId() {
		return this.partnerSubspMappingId;
	}

	public void setPartnerSubspMappingId(String partnerSubspMappingId) {
		this.partnerSubspMappingId = partnerSubspMappingId;
	}

	public List<PartnerSubspProductMappingT> getDeletePartnerSubspProductMappingTs() {
		return deletePartnerSubspProductMappingTs;
	}

	public void setDeletePartnerSubspProductMappingTs(
			List<PartnerSubspProductMappingT> deletePartnerSubspProductMappingTs) {
		this.deletePartnerSubspProductMappingTs = deletePartnerSubspProductMappingTs;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public Integer getSubSpId() {
		return this.subSpId;
	}

	public void setSubSpId(Integer subSpId) {
		this.subSpId = subSpId;
	}

	public PartnerMasterT getPartnerMasterT() {
		return this.partnerMasterT;
	}

	public void setPartnerMasterT(PartnerMasterT partnerMasterT) {
		this.partnerMasterT = partnerMasterT;
	}

	public UserT getCreatedByUser() {
		return this.createdByUser;
	}

	public void setCreatedByUser(UserT createdByUser) {
		this.createdByUser = createdByUser;
	}

	public UserT getModifiedByUser() {
		return this.modifiedByUser;
	}

	public void setModifiedByUser(UserT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}

	public List<PartnerSubspProductMappingT> getPartnerSubspProductMappingTs() {
		return this.partnerSubspProductMappingTs;
	}

	public void setPartnerSubspProductMappingTs(List<PartnerSubspProductMappingT> partnerSubspProductMappingTs) {
		this.partnerSubspProductMappingTs = partnerSubspProductMappingTs;
	}

	public PartnerSubspProductMappingT addPartnerSubspProductMappingT(PartnerSubspProductMappingT partnerSubspProductMappingT) {
		getPartnerSubspProductMappingTs().add(partnerSubspProductMappingT);
		partnerSubspProductMappingT.setPartnerSubSpMappingT(this);

		return partnerSubspProductMappingT;
	}

	public PartnerSubspProductMappingT removePartnerSubspProductMappingT(PartnerSubspProductMappingT partnerSubspProductMappingT) {
		getPartnerSubspProductMappingTs().remove(partnerSubspProductMappingT);
		partnerSubspProductMappingT.setPartnerSubSpMappingT(null);

		return partnerSubspProductMappingT;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
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

	public SubSpMappingT getSubSpMappingT() {
		return subSpMappingT;
	}

	public void setSubSpMappingT(SubSpMappingT subSpMappingT) {
		this.subSpMappingT = subSpMappingT;
	}

}