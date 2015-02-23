package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;


/**
 * The persistent class for the iou_mapping_t database table.
 * 
 */
@Entity
@Table(name="iou_mapping_t")
@NamedQuery(name="IouMappingT.findAll", query="SELECT i FROM IouMappingT i")
public class IouMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String iou;

	//bi-directional many-to-one association to CustomerMasterT
	@JsonIgnore
	@OneToMany(mappedBy="iouMappingT")
	private List<CustomerMasterT> customerMasterTs;

	//bi-directional many-to-one association to PartnerMasterT
	@JsonIgnore
	@OneToMany(mappedBy="iouMappingT")
	private List<PartnerMasterT> partnerMasterTs;

	public IouMappingT() {
	}

	public String getIou() {
		return this.iou;
	}

	public void setIou(String iou) {
		this.iou = iou;
	}

	public List<CustomerMasterT> getCustomerMasterTs() {
		return this.customerMasterTs;
	}

	public void setCustomerMasterTs(List<CustomerMasterT> customerMasterTs) {
		this.customerMasterTs = customerMasterTs;
	}

	public CustomerMasterT addCustomerMasterT(CustomerMasterT customerMasterT) {
		getCustomerMasterTs().add(customerMasterT);
		customerMasterT.setIouMappingT(this);

		return customerMasterT;
	}

	public CustomerMasterT removeCustomerMasterT(CustomerMasterT customerMasterT) {
		getCustomerMasterTs().remove(customerMasterT);
		customerMasterT.setIouMappingT(null);

		return customerMasterT;
	}

	public List<PartnerMasterT> getPartnerMasterTs() {
		return this.partnerMasterTs;
	}

	public void setPartnerMasterTs(List<PartnerMasterT> partnerMasterTs) {
		this.partnerMasterTs = partnerMasterTs;
	}

	public PartnerMasterT addPartnerMasterT(PartnerMasterT partnerMasterT) {
		getPartnerMasterTs().add(partnerMasterT);
		partnerMasterT.setIouMappingT(this);

		return partnerMasterT;
	}

	public PartnerMasterT removePartnerMasterT(PartnerMasterT partnerMasterT) {
		getPartnerMasterTs().remove(partnerMasterT);
		partnerMasterT.setIouMappingT(null);

		return partnerMasterT;
	}

}